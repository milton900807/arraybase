package com.arraybase.evolve;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.modules.DocumentStoreToSolr;
import com.arraybase.tm.GRow;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class FetchFunctionEvolve implements EvolveFunction {
    private final ABTable abTable;
    String paramtemplate = null;
    String keytype = "string"; // by default
    String field_name = null;

    public FetchFunctionEvolve(String field_name, String function_key, ABTable t) {
        this.paramtemplate = function_key;
        this.field_name = field_name;
        String key = parseKey(paramtemplate);
        if (key != null) {
            key = key.trim();
            if (key.startsWith("(int)")) {
                this.paramtemplate = paramtemplate.replace("(int)", "");
                this.keytype = "int";
            }
        }
        this.abTable = t;
    }

    public void eval(GRow gRow) {
        Map data = gRow.getData();
        String key = parseKey(paramtemplate);
        if (key != null) {
            key = key.trim();
        }
        Object value = data.get(key);
        if (value != null) {
            if (!keytype.equalsIgnoreCase("string"))
                value = castToType(this.keytype, value);
            String url = paramtemplate.replace("{{" + key + "}}", value.toString());
            String json_path = parseJSONPath(url);
            url = parseJSONURL(url);
            try {
                Object current_json = DocumentStoreToSolr.readJsonFromUrl(url);
//                System.out.println(" current json " + current_json);
                Object jsonvalue = null;
                try {
                    jsonvalue = DocumentStoreToSolr.extractValue(current_json, json_path);
                    if (jsonvalue == null) {
                        GB.print("Field: " + this.field_name + " \t failed to find json object for  \t " + json_path + "\t In " + value);
                        GB.print(" \t Results: Json doc " + jsonvalue);
                    } else {
                        String tmid = (String) data.get("TMID");
                        abTable.set(tmid, this.field_name, jsonvalue, false);
                        gRow.set(this.field_name, jsonvalue);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonvalue != null && jsonvalue.toString().length() > 0)
                    data.put(key, jsonvalue.toString());
//                cache.add(data);
//                if (cache.size() > 1000) {
//                    cache = flushCache(cache, abTable);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            System.out.println(" url " + url);
        } else {
            GB.print("\t Parameter is not in row  " + key);
        }
    }

    public static Object castToType(String keytype, Object value) {
        if (keytype.equalsIgnoreCase("int")) {
            Float fl = Float.parseFloat(value.toString());
            return new Integer(fl.intValue());
        }
        return value;
    }

    public static String parseKey(String paramtemplate) {
        int iv = paramtemplate.indexOf("{{");
        int ev = paramtemplate.lastIndexOf("}}");
        if ( iv < 0 || ev < 0 )
        {
            return paramtemplate;
        }
        String variable_name = paramtemplate.substring(iv + 2, ev);
        return variable_name;
    }

    public static String parseJSONPath(String url) {
        String temp = url;
        int ind = url.indexOf("->");
        if (ind < 0) {
            return url;
        }
        temp = url.substring(ind + 2);
        return temp;
    }

    public static String parseJSONURL(String url) {

        String temp = url;
        int ind = url.indexOf("->");
        if (ind < 0) {
            return url;
        }
        temp = url.substring(0, ind);
        return temp;
    }

    private ArrayList<Map<String, Object>> flushCache(ArrayList<Map<String, Object>> cache, ABTable table) {
//        table.append(cache);
        return new ArrayList<Map<String, Object>>();
    }


}
