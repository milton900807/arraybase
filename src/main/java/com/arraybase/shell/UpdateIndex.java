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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class UpdateIndex implements com.arraybase.GBPlugin {

    public String exec(String command, String variable_key) throws UsageException {
        String[] it = command.split("\\s+");
        String index_path = it[2];
        String evfile = it[1];
        ABTable table = new ABTable(index_path);
        try {
            if (!table.exists()) {
                GB.print(" Failed to find the path " + index_path);
                return null;
            }
        } catch (NodeWrongTypeException e) {
            e.printStackTrace();
        }
        System.out.println(" file " + evfile);
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
            JSONArray field_sets = root.getJSONArray("fields");
            LinkedHashMap<String, String> pr = new LinkedHashMap<String, String>();
            for (int i = 0; i < field_sets.length(); i++) {
                JSONObject obj = field_sets.getJSONObject(i);
                String name = obj.getString("name");
                String type = obj.getString("type");
                String function = obj.getString("function");
                pr.put(name, function);
            }
//            table.commit();
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
