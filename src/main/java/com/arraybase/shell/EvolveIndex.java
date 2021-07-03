package com.arraybase.shell;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.evolve.DataSourceFunction;
import com.arraybase.evolve.EvolveDataStoreFunction;
import com.arraybase.evolve.EvolveFunction;
import com.arraybase.evolve.EvolveFunctionFactory;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.GRow;
import org.apache.solr.request.json.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class EvolveIndex implements com.arraybase.GBPlugin {

    private LinkedHashMap<String, EvolveDataStoreFunction> datasource = new LinkedHashMap<String, EvolveDataStoreFunction>();


    public String exec(String command, String variable_key) throws UsageException {
        String[] it = command.split("\\s+");
        String index_path = it[2];
        String evfile = it[1];
        ABTable t = new ABTable(index_path);
        try {
            if (!t.exists()) {
                GB.print(" Failed to find the path " + index_path);
                return null;
            }
        } catch (NodeWrongTypeException e) {
            e.printStackTrace();
        }
        System.out.println(" ev file " + evfile);
        System.out.println(" index path " + index_path);
        FileReader pinputstram = null;
        try {
            pinputstram = new FileReader(evfile);
            JSONTokener tokener = new JSONTokener(pinputstram);
            JSONObject root = new JSONObject(tokener);
            Iterator itset = root.keys();
            while (itset.hasNext()) {
                String key = (String) itset.next();
                System.out.println(" key " + key);
            }
            this.datasource = loadDataSource(root);

            JSONArray field_sets = root.getJSONArray("fields");
            LinkedHashMap<String, String> pr = new LinkedHashMap<String, String>();
            for (int i = 0; i < field_sets.length(); i++) {
                JSONObject obj = field_sets.getJSONObject(i);
                String name = obj.getString("name");
                String type = obj.getString("type");
                String function = obj.getString("function");
                t.addField(ABFieldType.getType(type), name);
                pr.put(name, function);
            }
            int total = t.getDocSize();
            int increment = 10000;
            if (root.has("increment"))
                increment = root.getInt("increment");
            Set<String> keys = pr.keySet();
            List<EvolveFunction> efunction = new ArrayList<>();
            for (String key : keys) {
                String function_value = pr.get(key);
                if (function_value.contains("(")) {
                    int ind = function_value.indexOf("(");
                    int end = function_value.lastIndexOf(')');
                    String parms = function_value.substring(ind + 1, end);
                    String function_key = function_value.substring(0, ind);
                    EvolveFunction ev = EvolveFunctionFactory.create(key, function_key, parms, t);
                    // update the datasource object
                    efunction.add(ev);
                } else if (function_value.startsWith("datasource.")) {
                    EvolveFunction ev = EvolveFunctionFactory.createDataSourcEV(key, function_value, t);
                    if (ev instanceof DataSourceFunction) {
                        ((DataSourceFunction) ev).setDataSource(this.datasource);
                    }
                    efunction.add(ev);
                }
            }
            for (int i = 0; i < total; i += increment) {
                ABaseResults res = t.search("*:*", null, i, increment);
                List<GRow> list = res.getValues();
                for (int l = 0; l < list.size(); l++) {
                    GRow row = list.get(l);
                    // update the data source objects first
                    if (this.datasource != null) {
                        Set<String> ds = this.datasource.keySet();
                        for (String key : ds) {
                            EvolveDataStoreFunction eds = this.datasource.get(key);
                            eds.update(row);
                        }
                    }//
                    for (EvolveFunction ev : efunction) {
                        ev.eval(row);
                    }
                }
                GB.print(" \n\n Committing \n " + i);
                t.commit();
            }
            t.commit();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private LinkedHashMap<String, EvolveDataStoreFunction> loadDataSource(JSONObject root) {

        try {
            if (!root.has("datasource")) {
                return null;
            }
            JSONArray field_sets = root.getJSONArray("datasource");
            LinkedHashMap<String, EvolveDataStoreFunction> pr = new LinkedHashMap<String, EvolveDataStoreFunction>();
            for (int i = 0; i < field_sets.length(); i++) {
                JSONObject obj = field_sets.getJSONObject(i);
                String name = obj.getString("name");
                String function = obj.getString("function");

                if (function.contains("(")) {
                    int ind = function.indexOf("(");
                    int end = function.lastIndexOf(')');
                    String parms = function.substring(ind + 1, end);
                    String function_key = function.substring(0, ind);
                    EvolveDataStoreFunction ev = EvolveFunctionFactory.createDataSource(function_key, parms);
                    pr.put(name, ev);
                }
            }
            return pr;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
