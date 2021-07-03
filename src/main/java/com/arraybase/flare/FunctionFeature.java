package com.arraybase.flare;

import com.arraybase.modules.DocumentStoreToSolr;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FunctionFeature {
    Object current_json = null;

    public static String getType(String function) {

        return "string_ci";


    }

    public static Map<String, String> getFieldProperties(String function) {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("caseControl", "true");
        props.put("indexed", "true");
        props.put("stored", "true");
        props.put("multiValued", "false");
        props.put("dataType", getDataType(function));
        return props;
    }

    private static String getDataType(String function) {

        if (function.startsWith("fetch")) {
            return "string_ci";
        } else if (function.startsWith("(float)")) {
            return "float";
        } else if (function.startsWith("(int)")) {
            return "int";
        } else {
            return "string_ci";
        }
    }

    public Object evaluateFunction(String field, String functioncode, FunctionFeatureValues sid) {
        functioncode = functioncode.replaceAll("\\s", "");

        if (functioncode.startsWith("fetch(")) {
            int st = functioncode.indexOf('(');
            int sf = functioncode.lastIndexOf(')');
            String url = functioncode.substring(st + 1, sf);
            String[] params = parseParams(url);
            url = applyParams(url, params, sid);
            String json_path = null;
            String endpoint = url;
            if (url.startsWith("http") && url.contains("->")) {
                json_path = parseJSONPath(url);
                endpoint = parseURLFromJSONPathAppendedURL(url);
            }
            try {
                current_json = DocumentStoreToSolr.readJsonFromUrl(endpoint);
                Object value = DocumentStoreToSolr.extractValue(current_json, json_path);
                return value;
//                System.out.println ( " value  " + value );
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (functioncode.contains("->")) {
            // for example:
//            targets=->[0].design.intendedTargetSites.mus-musculus-88[*].symbol -> [0].intendedTargetSites.homo-sapiens-88.[*].symbol,
            String[] functions = splitFunctions(functioncode);
            if (functions.length > 1) {
                String values = "";
                for (String fun : functions) {

                    // pull annotations
                    String annotation = pullAnnotations(fun);

                    String json_path = parseJSONPath(fun);
                    try {
                        Object value = DocumentStoreToSolr.extractValue(current_json, json_path);
                        values += annotation + value + " ";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return values;
            } else {

                String annotation = pullAnnotations(functioncode);
                String json_path = parseJSONPath(functioncode);
                try {
                    Object value = DocumentStoreToSolr.extractValue(current_json, json_path);
                    return annotation + value;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
        }
        return null;
    }

    private String pullAnnotations(String _t) {
        if (_t.startsWith("\"")) {
            int index = _t.indexOf("\"");
            int lastindex = _t.indexOf('"', index + 1);
            String ano = _t.substring(index + 1, lastindex);
            return ano;

        }
        return "";
    }

    private String[] splitFunctions(String functioncode) {
        ArrayList<String> f = new ArrayList<String>();
        String[] sp = functioncode.split("-\\>");
        for (String s : sp) {
            if (s != null && s.trim().length() > 0) {
                f.add(s);
            }
        }
        return f.toArray(new String[f.size()]);
    }

    private static String parseURLFromJSONPathAppendedURL(String url) {
        int ind = url.indexOf("->");
        String val = url.substring(0, ind);
        return val.trim();
    }

    private static String parseJSONPath(String url) {

        int st = url.indexOf('"');
        int lt = url.lastIndexOf('"');
        if (lt > 0) {
            return url.substring(lt + 1);
        }

        String temp = url;
        int ind = url.indexOf("->");
        if (ind < 0) {
            return url.trim();
        }
        temp = url.substring(ind + 2);
        return temp;
    }

    private String parseJSONURL(String url) {

        String temp = url;
        int ind = url.indexOf("->");
        if (ind < 0) {
            return url.trim();
        }
        temp = url.substring(0, ind);
        return temp;
    }


    private static String applyParams(String url, String[] params, FunctionFeatureValues sid) {
        String nurl = url;
        for (String p : params) {
            Object value = sid.get(p);
            if (value == null) {
                System.err.println(" failed to find the key " + p + " in the url " + nurl);
//                for (String s : keys) {
//                    System.out.println(" key " + s);
//                }
            }else
            nurl = nurl.replaceAll("\\{\\{" + p + "\\}\\}", value.toString());
        }
        return nurl;

    }

    private static String[] parseParams(String url) {
        String[] yxyurl = url.split("\\{\\{");
        ArrayList<String> ls = new ArrayList<String>();
        for (String y : yxyurl) {
            if (y.indexOf("}}") > 0) {
                int yi = y.indexOf("}}");
                String sub = y.substring(0, yi);
                ls.add(sub);
            }
        }
        String[] l = ls.toArray(new String[ls.size()]);
        return l;
    }
}
