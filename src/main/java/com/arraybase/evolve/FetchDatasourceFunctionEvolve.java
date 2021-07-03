package com.arraybase.evolve;

import com.arraybase.GB;
import com.arraybase.modules.DocumentStoreToSolr;
import com.arraybase.tm.GRow;
import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public class FetchDatasourceFunctionEvolve implements EvolveDataStoreFunction {
    private String keytype = "string";
    String url = null;
    Object content = null;
    String paramtemplate = null;

    public FetchDatasourceFunctionEvolve(String param) {
        this.url = param;
        this.paramtemplate = param;
        String key = FetchFunctionEvolve.parseKey(paramtemplate);
        if (key != null) {
            key = key.trim();
            if (key.startsWith("(int)")) {
                this.paramtemplate = paramtemplate.replace("(int)", "");
                this.keytype = "int";
            }
        }
    }
    public void update(GRow gRow) {
        Map data = gRow.getData();
        String key = FetchFunctionEvolve.parseKey(paramtemplate);
        if (key != null) {
            key = key.trim();
        }
        Object value = data.get(key);
        if (value != null) {
            if (!keytype.equalsIgnoreCase("string"))
                value = FetchFunctionEvolve.castToType(this.keytype, value);
            String url = paramtemplate.replace("{{" + key + "}}", value.toString());
            String json_path = FetchFunctionEvolve.parseJSONPath(url);
            url = FetchFunctionEvolve.parseJSONURL(url);
            try {
                Object current_json = DocumentStoreToSolr.readJsonFromUrl(url);
                Object jsonvalue = null;
                try {
                    jsonvalue = DocumentStoreToSolr.extractValue(current_json, json_path);
                    if (jsonvalue == null) {
                        GB.print(" \t Results: Json doc " + jsonvalue);
                    }
                    else
                    {
                        this.content = jsonvalue;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonvalue != null && jsonvalue.toString().length() > 0)
                    data.put(key, jsonvalue.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            GB.print("\t Parameter is not in row  " + key);
        }
    }
}
