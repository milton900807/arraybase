package com.arraybase.shell.cmds;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.db.NodeExistsException;
import com.arraybase.modules.UsageException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

//create table temp for(1133016,1133025) helm=http://oligodb:8080/oligos/1133016->[0].helm five_prime=[0].helm three_prime=[0].helm isisno=[0].isisno sequence=[0].equivSequence
public class JSONFieldsImporter implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {

//        create table helm2 for(300000,300002) helm=http://oligodb:8080/oligos/{{index}}->[0].helm isisno=[0].isisno
//        create table  chemistry for(0,300000) => helm=http://oligodb:8080/oligos/{{i}}/[0].helm
//        create table  chemistry for(0,300000) => helm=http://oligodb:8080/oligos/{{i}}/[0].helm field=[0].isino

        int index = command.indexOf('(');
        int lndex = command.indexOf(')', index + 1);
        if (index < 0 || lndex < 0) {
            GB.print(" JSONFieldsImporter is incorrectly formatted.. looking for the 'for' loop.. but could not find it. ");
            return null;
        }


        String create_table_str = command.substring(0, index);
        int forin = create_table_str.indexOf("for");
        int create_table_index = create_table_str.indexOf("table") + 5;
        String table_name = create_table_str.substring(create_table_index, forin);
        table_name = table_name.trim();
        System.out.println(" table name " + table_name);
        int startIndex = 0;
        int endIndex = -1;

        String temp_incr = command.substring(index + 1, lndex);
        String[] start_stop_str = temp_incr.split(",");
        try {
            startIndex = Integer.parseInt(start_stop_str[0]);
            endIndex = Integer.parseInt(start_stop_str[1]);

            System.out.println(" start index " + startIndex);
            System.out.println(" end index " + endIndex);
        } catch (NumberFormatException nu) {
            GB.print(" Please provide integer values for the increments ");
            return null;
        }

        String fields = command.substring(lndex + 1);
        if (fields == null) {
            GB.print(" Please provide field values  ");
            return null;
        } else {
            fields = fields.trim();
        }

        LinkedHashMap<String, String> urlMap = new LinkedHashMap<String, String>();


        HashMap<String, String> map = new HashMap<String, String>();
        String[] field_list = fields.split("\\s");
        for (String fl : field_list) {
            System.out.println(" fl " + fl);
            String[] furl = fl.split("=");
            if (furl.length != 2) {
                GB.print(" Field values were not formatted correctly.. please fix this.  fielname=http://etc fieldname2=http//etc/");
                return null;
            }
            map.put(furl[0], "string_ci");
            urlMap.put(furl[0], furl[1]);
        }

        //
        GB.print(" Creating the table " + table_name);
        table_name = table_name.trim();

        String current_location = GB.pwd() + "/" + table_name;

        ABTable table = createTable(current_location, map);
        // now loop over the fields and import the values


        try {
            Thread.sleep(5000l);
        } catch (Exception _e) {
            _e.printStackTrace();
        }

        System.out.println("\n\nLoading data....\n\n");

        ArrayList<LinkedHashMap<String,Object>> cache = new ArrayList<LinkedHashMap<String,Object>>();
        for (int i = startIndex; i < endIndex; i++) {
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            Set<String> fieldset = urlMap.keySet();
            Object current_json = null;
            for (String f : fieldset) {
                String json_path = null;
                String url = urlMap.get(f);
                if (url.startsWith("http") && url.contains("->")) {
                    json_path = parseJSONPath(url);
                    url = parseJSONURL(url);
                    url = url.replaceAll("\\{\\{index\\}\\}", "" + i);
                    try {
                        current_json = readJsonFromUrl(url);
                        System.out.println(" url " + url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    json_path = url;
                }
                Object value = null;
                try {
                    value = extractValue(current_json, json_path);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (value != null && value.toString().length() > 0)
                    if ( f.equalsIgnoreCase("helm")){
                        String processedhelm = processHELMForBetterLuceneSearching ( value.toString() );
                        data.put(f, processedhelm);
                    } else if ( f.equalsIgnoreCase("five_prime")){
                        String processedhelm = processfor_five_prime_chemistry( value.toString() );
                        if ( processedhelm != null && processedhelm.length() > 0 )
                            data.put(f, processedhelm);
                    } else if ( f.equalsIgnoreCase("three_prime")){
                        String processedhelm = processfor_three_prime_chemistry( value.toString() );
                        if ( processedhelm != null && processedhelm.length() > 0 )
                            data.put(f, processedhelm);
                    }


                    else {
                        data.put(f, value.toString());
                    }
            }
            cache.add(data);
            if ( cache.size() > 1000 ){
                cache = flushCache ( cache, table );
            }
        }
        flushCache(cache, table);
        return "Complete";
    }

    public static String processfor_three_prime_chemistry(String s) {
        int st = s.indexOf("$");
        String t= null;
        if ( st <= 0 )
            return s;
        String subhelm = s.substring(0, st);
        String[] chains = subhelm.split ("\\|");
        // make sure we  test all chems
        for ( String chain : chains )
        {
            if ( chain.toLowerCase().startsWith("chem")){
                String chainid = parseChainId ( chain );
                String temp = chain.substring(chainid.length()+1);

                int threeprimeindex = determineThreePrimeIndex(chainid, s);
                if ( threeprimeindex > 2 ) {
                    temp = temp.replaceAll("\\{", " ");
                    temp = temp.replaceAll("\\}", " ");
                    temp= temp.replaceAll("\\[", " ");
                    temp= temp.replaceAll("\\]", " ");
                    temp = temp.trim();
                    temp = temp.replaceAll("(\\s)+", " ");
                    System.out.println( " three prime " + temp );
                    return temp;
                }
                else
                {
                }
//                RNA1{[cet](G)p.[cet](T)p.[cet](T)p.[cet]([m5C])p.[cet](A)p.[cet]([m5C])p.[cet]([m5C])p.[cet](T)p.[cet](T)p.[cet](G)p.[cet]([m5C])p.[cet](A)p.[cet](T)p.[cet](G)p.[cet](G)p.[cet](A)p.[cet](A)p.[cet](G)p.[cet]([m5C])p.[cet](A)p}|CHEM1{[THAGN3]}$RNA1,CHEM1,60:R2-1:R1$$$
                //RNA1,CHEM1,60:R2-1:R1
            }
        }
        return t;
    }


    private static int determineThreePrimeIndex(String unit, String helm) {
        //RNA1,CHEM1,30:R2-1:R1|CHEM1,CHEM2,1:R2-1:R1
        String connection_string = HELMUtil.getConnectionString(helm);
//        if (connection_string.contains("|")) {
            String[] con = connection_string.split("\\|");
            // we need to get the index given the chem-label above
            for (String c : con) {
                String[] units = c.split(",");
                for (int t = 0; t < units.length; t++) {
                    if (units[t].toUpperCase().equalsIgnoreCase(unit)) {
                        for (int u = 0; u < units.length; u++) {
                            // {{ FIRST CHECK TO SEE IF THIS IS ATTACHED TO THE RNA STRAND }}
                            if (units[u].toUpperCase().startsWith("RNA")) {
                                int monomer_index = getIndex(u, units[2]);
                                return monomer_index+1;
                            } else if (units[u].toUpperCase().startsWith("CHEM") && (!units[u].equals(unit))) {
                                int threeprimeindex = determineThreePrimeIndex(units[u], helm);
                                if (threeprimeindex > 0) {
                                    return threeprimeindex + 1;
                                }
                            }
                        }
                    }
                }
            }
//        }
        return -1;
    }





    public static String processHELMForBetterLuceneSearching(String s) {
        int st = s.indexOf("$");

        if ( st <= 0 )
            return s;

        String chains = s.substring(0, st);



        String t = chains.replaceAll("\\.", " ");

        t = t.replaceAll("RNA[0-1]+", " ");
        t = t.replaceAll("CHEM[0-1]+", " ");
        t = t.replaceAll("PEPTIDE[0-1]+", " ");



        t = t.replaceAll("\\{", " ");
        t = t.replaceAll("\\}", " ");
        t = t.replaceAll("\\[", " ");
        t = t.replaceAll("\\]", " ");
        t = t.replaceAll("\\(", " ");
        t = t.replaceAll("\\)", " ");
        t = t.replaceAll("\\$", "");
        t = t.replaceAll("(\\s)+", " ");
        return t;
    }

    public static String processfor_five_prime_chemistry(String s) {
        int st = s.indexOf("$");
//        int cot = s.indexOf("$", st+1);
        String t= null;
        if ( st <= 0 )
            return s;

        String subhelm = s.substring(0, st);
        String[] chains = subhelm.split ("\\|");
        for ( String chain : chains )
        {
            if ( chain.toLowerCase().startsWith("chem")){
                String chainid = parseChainId ( chain );
                int fiveprimeindex = determineFivePrimeIndex(chainid, s);
//                System.out.println ( " fivec prime " + fiveprimeindex );
                if ( fiveprimeindex == 1) {
                    String temp = chain.substring(chainid.length()+1);
                    temp = temp.replaceAll("\\{", " ");
                    temp = temp.replaceAll("\\}", " ");
                    temp= temp.replaceAll("\\[", " ");
                    temp= temp.replaceAll("\\]", " ");
                    temp = temp.trim();
                    temp = temp.replaceAll("(\\s)+", " ");

                    System.out.println( " five prime " + temp );
                    return temp;
                }
                else
                {
//                    return null;
                }
//                RNA1{[cet](G)p.[cet](T)p.[cet](T)p.[cet]([m5C])p.[cet](A)p.[cet]([m5C])p.[cet]([m5C])p.[cet](T)p.[cet](T)p.[cet](G)p.[cet]([m5C])p.[cet](A)p.[cet](T)p.[cet](G)p.[cet](G)p.[cet](A)p.[cet](A)p.[cet](G)p.[cet]([m5C])p.[cet](A)p}|CHEM1{[THAGN3]}$RNA1,CHEM1,60:R2-1:R1$$$
                //RNA1,CHEM1,60:R2-1:R1
            }
        }
       return t;
    }

    private static String parseChainId(String chain) {
        int parm = chain.indexOf('{');
        String idv = chain.substring(0, parm);
        return idv;
    }


    private ArrayList<LinkedHashMap<String, Object>> flushCache(ArrayList<LinkedHashMap<String, Object>> cache, ABTable table) {
        table.append(cache);
        return new ArrayList<LinkedHashMap<String, Object>> ();
    }


    //    where json_path is something like [0].helm
    private Object extractValue(Object json, String json_path) throws JSONException {
        if (json instanceof JSONArray) {
            JSONArray ja = (JSONArray) json;
            int vi = json_path.indexOf("[");
            if (vi != 0) {
                GB.print(" the format of the json doesn't match the returned object " + json_path);
            }
            int vf = json_path.indexOf("]");
            String js = json_path.substring(vi + 1, vf);
            js = js.trim();
            int index = Integer.parseInt(js);
            Object next_object = ja.get(index);
            String next_value = json_path.substring(vf + 1);
            if (next_value == null || next_value.length() == 0) {
                return next_object;
            } else {
                return extractValue(next_object, next_value);
            }
        } else if (json instanceof JSONObject) {
            JSONObject ja = (JSONObject) json;
            int vi = json_path.indexOf('.');
            int vfc1 = json_path.indexOf('.');
            int vfc2 = json_path.indexOf('[');

            if (vfc1 > 0 || vfc2 > 0) {
                if (vfc1 < vfc2) {
                    String next = json_path.substring(vi + 1, vfc1);
                    JSONObject job = ja.getJSONObject(next);
                    json_path = json_path.substring(vi + 1, vfc1) + 1;
                    return extractValue(job, json_path);
                } else {
                    String next = json_path.substring(vi + 1, vfc2);
                    json_path = json_path.substring(vi + 1, vfc2) + 1;
                    JSONArray job = ja.getJSONArray(next);
                    return extractValue(job, json_path);
                }

            } else {
                String final_value = json_path.substring(vi + 1).trim();
                return ja.get(final_value);
            }


        }

        return null;

    }

    private JSONArray readJsonArrayFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            if (jsonText != null && jsonText.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonText);
                return jsonArray;
            } else if (jsonText != null && jsonText.startsWith("{")) {
                return null;
            } else {
                return null;
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            is.close();
        }
        return null;
    }

    private String parseJSONPath(String url) {

        String temp = url;
        int ind = url.indexOf("->");
        if (ind < 0) {
            return null;
        }
        temp = url.substring(ind + 2);
        return temp;
    }

    private String parseJSONURL(String url) {

        String temp = url;
        int ind = url.indexOf("->");
        if (ind < 0) {
            return null;
        }
        temp = url.substring(0, ind);
        return temp;
    }

    public static ABTable createTable(String tablename, Map<String, String> map) {
        tablename = tablename.trim();
        ABTable new_table_obj = new ABTable(tablename);
        try {
            new_table_obj.create(map);
        } catch (NodeExistsException e) {
            e.printStackTrace();
        }
        return new_table_obj;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static Object readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            if (jsonText != null && jsonText.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonText);
                return jsonArray;
            } else if (jsonText != null && jsonText.startsWith("{")) {
                JSONObject json = new JSONObject(jsonText);
                return json;
            } else {
                return null;
            }
        } finally {
            is.close();
        }
    }

    private static int determineFivePrimeIndex(String unit, String helm) {
        //RNA1,CHEM1,30:R2-1:R1|CHEM1,CHEM2,1:R2-1:R1
        String connection_string = HELMUtil.getConnectionString(helm);
        if (connection_string.contains("|")) {
            String[] con = connection_string.split("\\|");
            // we need to get the index given the chem-label above
            for (String c : con) {
                String[] units = c.split(",");
                for (int t = 0; t < units.length; t++) {
                    if (units[t].toUpperCase().equalsIgnoreCase(unit)) {
                        for (int u = 0; u < units.length; u++) {
                            // {{ FIRST CHECK TO SEE IF THIS IS ATTACHED TO THE RNA STRAND }}
                            if (units[u].toUpperCase().startsWith("RNA")) {
                                int monomer_index = getIndex(u, units[2]);
                                if (monomer_index == 1) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            } else if (units[u].toUpperCase().startsWith("CHEM") && (!units[u].equals(unit))) {
                                int fiveprimeindex = determineFivePrimeIndex(units[u], helm);
                                if (fiveprimeindex > 0) {
                                    return fiveprimeindex + 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    private static int getIndex(int i, String unit) {
        String[] un = unit.split("-");
        String[] t = un[i].split ( ":");
        Integer gt = Integer.parseInt(t[0]);
        return gt;
    }

}


class HELMUtil {


    public static boolean isHELM ( String _string )
    {
        if ( _string.contains("{") && _string.contains("}") && _string.contains("$")){
            return true;
        }
        else
        {
            return false;
        }
    }


    public static String[] parseChains(String chemical_notation) {
        int chainindex = chemical_notation.indexOf('$');
        String sub = chemical_notation.substring(0, chainindex);
        if (sub.contains("|")) {
            return sub.split("\\|");
        } else {
            String[] s = new String[1];
            s[0] = sub.trim();
            return s;
        }
    }

    public static String getConnectionString(String helm) {
        int st = helm.indexOf('$');
        int en = helm.indexOf('$', st + 1);
        String connection_string = helm.substring(st + 1, en);
        return connection_string;
    }

    public static String parsePolymerIDFromChain(String c) {
        int i = c.indexOf('{');
        String t = c.trim();
        return t.substring(0, i);
    }

    public static String parseChainFromPolymer(String c) {
        int i = c.indexOf('{');
        int f = c.indexOf('}');
        String t = c.substring(i + 1, f);
        return t.trim();
    }

    public static String parseMonomer(String unit, int monomer_index, String helm) {
        String[] chains = parseChains(helm);
        for (String c : chains) {
            if (c.startsWith(unit)) {
                String monomerlist = parseChainFromPolymer(c);
                String monomerindex = getMonomerInChain(monomerlist, monomer_index);
                return monomerindex;

            }
        }
        return null;

    }

    private static String getMonomerInChain(String monomerlist, int monomer_index) {

        monomer_index = monomer_index-1;

        monomerlist = monomerlist.replaceAll("\\(", ".");
        monomerlist = monomerlist.replaceAll("\\)", ".");
        String[] sp = monomerlist.split("\\.");
        if (monomer_index < sp.length)
            return sp[monomer_index];
        else
            return null;

    }



}