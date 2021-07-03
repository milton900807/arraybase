package com.arraybase.shell.loaders;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.db.NodeExistsException;
import com.arraybase.modules.UsageException;

import java.io.*;
import java.util.*;

//+load --delimeter=tab --schema:1:DB,2:DB_ID,3:DB_SYMBOL,9:ASPECT,10:DB_OBJECT_NAME;11:SYNONYM C:/Users/jmilton/Downloads/goa_human.gaf /annotations/human/goa
//load --delimeter=tab C:/Users/jmilton/Downloads/goa_human.gaf /annotations/human/goa
public class LoadTextFile implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {

        String[] sp = command.split(" ");
        String delim = parse("delimeter", sp);
        if (delim != null) {
            if (delim.equalsIgnoreCase("tab")) {
                delim = "\t";
            } else if (delim.equalsIgnoreCase("space")) {
                delim = "\r";
            }
        }

        Map<String, String> ab_schema = new HashMap<String, String>();
        String schema = parse("schema", sp);
        if (schema != null) {
            ab_schema = createSchema(schema);
        }
        Map<Integer, String> field_map = createFieldMap(schema);
        String in = sp[sp.length - 2];
        in = in.trim();
        File infile = new File(in);
        String path = sp[sp.length - 1].trim();
        ABTable b = new ABTable(path.trim());
        try {
            if (!b.exists()) {
                b.create(ab_schema);
            }
            FileReader reader = new FileReader(infile);
            BufferedReader breader = new BufferedReader(reader);
            ArrayList<LinkedHashMap<String, Object>> docs = new ArrayList<>();
            String line = breader.readLine();
            while (line != null) {
                if (line.startsWith("#") || line.startsWith("#")) {
                } else {
                    LinkedHashMap<String, Object> doc = new LinkedHashMap<String, Object>();
                    StringTokenizer st = new StringTokenizer(line, delim);
                    int col = 0;
                    while (st.hasMoreTokens()) {

                        String token = st.nextToken();
                        String field_key = field_map.get(col);
                        if (field_key != null && token != null && token.length() > 0 && field_key.length() > 0) {
                            doc.put(field_key, token);
                        }
                        col++;
                    }
                    if (doc.size() > 0)
                        docs.add(doc);

                    line = breader.readLine();
                }
                if (docs.size() >= 100000) {
                    b.append(docs);
                    docs = new ArrayList<LinkedHashMap<String, Object>>();
                }

            }
            if (docs.size() > 0) {
                b.append(docs);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NodeExistsException e) {
            e.printStackTrace();
        } catch (NodeWrongTypeException e) {
            e.printStackTrace();
        }


        GB.print(" loading the text file " + command);
        return null;
    }

    private Map<Integer, String> createFieldMap(String schema) {
        LinkedHashMap<Integer, String> fm = new LinkedHashMap<>();
        StringTokenizer ts = new StringTokenizer(schema, ",");
        while (ts.hasMoreTokens()) {
            String temp = ts.nextToken();
            String index = temp.substring(0, temp.indexOf(':'));
            String field_name = temp.substring(temp.indexOf(':') + 1);
            Integer iindex = Integer.parseInt(index);
            fm.put(iindex, field_name.trim());
        }
        return fm;

    }

    private Map<String, String> createSchema(String schema) {

        String[] fields = schema.split(",");
        HashMap<String, String> s = new HashMap<String, String>();
        for (String f : fields) {
            s.put(f.trim(), "string_ci");
        }
        return s;
    }

    @Override
    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }

    public static String parse(String key, String[] co) {
        for (String st : co) {
            st = st.trim();
            if (st.startsWith("--" + key)) {
                String[] sp = st.split("=");
                return sp[1];
            }
        }
        return null;
    }
}
