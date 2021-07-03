package com.arraybase.io;

import com.arraybase.GB;
import com.arraybase.modules.UsageException;
import com.arraybase.util.IOUTILs;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class ABQFile {

    // String url = scope_config.get("url");
    // String pass = scope_config.get("password");
    // String user = scope_config.get("user");
    // String driver = scope_config.get("driver_class");

    public static String PLUGINS = "plugins";
    public static String PASSWORD = "password";
    public static String USER = "user";
    public static String DRIVER_CLASS = "driver_class";
    public static String DRIVER = "driver";
    public static String EXPORT_FIELDS = "export";
    public static String URL = "url";
    public static String QUERY = "query";
    public static String LAC = "lac";
    public static String NODE_PATH = "path";
    public static String MODULE = "module";
    public static String PK = "pk";
    public static String TYPES = "types";


    public static final String EXPORT_FIELDS_DELIM = ",\\s*";
    public static final String END = "end";
    public static final String START = "start";
    public static final String INCREMENT = "increment";
    public static final String EXPORT = "export";
    public static final Object UPDATE_WHERE_CLAUSE = "update_where";

    // {{ THESE ARE CONDITIONAL VARIABLES }}
    public static final String CURRENT_TIME = "current_time";
    public static final String CURRENT_DATE = "current_date";
    public static final String PK_LAST = "lastpk";


    private HashMap<String, Object> param = new HashMap<String, Object>();
    private String insert_mod = null;
    private Properties p = new Properties();

    public ABQFile(Properties p, HashMap<String, Object> param_map,
                   String insert_module_type) {
        p = p;
        insert_mod = insert_module_type;
        param = param_map;
    }

    public HashMap<String, Object> getParam() {
        return param;
    }

    public void setParam(HashMap<String, Object> param) {
        this.param = param;
    }

    public String getInsert_mod() {
        return insert_mod;
    }

    public void setInsert_mod(String insert_mod) {
        this.insert_mod = insert_mod;
    }

    public Properties getP() {
        return p;
    }

    public void setP(Properties p) {
        this.p = p;
    }

    public static Properties load(File file_name) throws IOException,
            UsageException {
        FileReader r = null;
        try {
            GB.print(" Loading " + file_name.getAbsolutePath());
            // {{ 11.28.2017 }}
            // We are going to load a properties file... but we may need to reformat the file if it is not in "properties" format:
            r = new FileReader(file_name);
            BufferedReader br = new BufferedReader(r);
            String line = br.readLine();
            StringBuffer strb = new StringBuffer();
            boolean next = true;
            while (line != null) {
                String tline = line.trim();
                if (tline.matches("^[A-Za-z0-9_]*\\=")) {
                    String nline = br.readLine();
                    // read the lines for the exported param until we hit the next exported parameter
                    while (nline != null && (!nline.matches("^[A-Za-z0-9_]*\\=.*"))) {
                        tline += nline.trim();
                        nline = br.readLine();
                        // skip comments
                        while (nline != null && nline.trim().matches("\\/\\/.*")) {
                            nline = br.readLine();
                        }
                    }
                    line = nline;
                    next = false;// this just keeps me from having to use mark-supported readers.
                }
                if (tline != null && tline.length() > 0)
                    strb.append(tline + "\n");

                if (next) {
                    line = br.readLine();
                } else {
                    next = true;
                }
                // skip comments
                while (line != null && line.trim().matches("\\/\\/.*")) {
                    line = br.readLine();
                }
            }
            StringReader strreader = new StringReader(strb.toString());
            Properties p = new Properties();
            p.load(strreader);
            String export = p.getProperty(EXPORT_FIELDS);
//			if (export == null) {
//				throw new UsageException(
//						"export parameter is not defined in the abq file. \n\n");
//			}
            if (export != null) {
                String[] exported_values = export.split(",\\s*");

                // if this is just a direct sql select then we are going to have
                if (exported_values == null || exported_values.length <= 0) {
                    throw new UsageException(
                            "export parameter is not defined correctly in the abq file; please provide comma "
                                    + "seperated fields that exist in the query. \n\n");
                }
            }
            // {{ DETERMINE THE TYPE OF INSERT }}
//			String url = p.getProperty(URL);
//			String query = p.getProperty(QUERY);
//			if (url == null) {
//				throw new UsageException(
//						"url parameter is not defined in the abq file. \n\n");
//			}
            return p;
        } finally {
            IOUTILs.closeResource(r);
        }
    }

}
