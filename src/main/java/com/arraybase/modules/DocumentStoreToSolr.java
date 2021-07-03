package com.arraybase.modules;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.JDBC;
import com.arraybase.flare.*;
import com.arraybase.io.ABQFile;
import com.arraybase.shell.cmds.JSONFieldsImporter;
import com.arraybase.tm.builder.jobs.Job;
import com.arraybase.tm.tables.TTable;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class DocumentStoreToSolr {

    private GBLogger log = GBLogger.getLogger(DocumentStoreToSolr.class);
    private DBConnectionManager dbcm = new DBConnectionManager();
    private GBJobListener gbJobListener = null;

    public String run(String _user, String _path, String _core, String _description,
                      Map<String, String> _query_config,
                      Map<String, Map<String, String>> _schema_config, String _export,
                      String job_id, GBJobListener gbJobListener) throws DBProcessFailedException {
        this.gbJobListener = gbJobListener;
        String core = _core;
        String end_core_name = _core;
        String stat_msg = "Error: ";
        TTable litem = new TTable();
        litem.setDescription("---" + _description + "---");
        litem.setLastEdited(new Date());
        litem.setSecurityStatus("1");
        litem.setSourceType("unknown");
        litem.setUser(_user);
        // litem.setSourceType(_source_type);
        litem.setTitle(end_core_name);
        Session hibernateSession = null;
        try {
            hibernateSession = dbcm.getSession();
            hibernateSession.beginTransaction();
            Criteria c = hibernateSession.createCriteria(TTable.class);
            c.add(Restrictions.eq("title", litem.getTitle()));
            List ls = c.list();
            if (ls.size() > 0) {
                TTable li1 = (TTable) ls.get(0);
                // {{ WE SHOULD BLOW AWAY THE CORE }}
                deprecate(li1.getTitle());
                li1.setTitle(core);
                li1.setLastEdited(new Date());
                hibernateSession.update(li1);
                hibernateSession.getTransaction().commit();
            } else {
                hibernateSession.save(litem);
                hibernateSession.getTransaction().commit();
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(hibernateSession);
        }
        // we want to spin off a series of threads that will do the inserts:
        int thread_count = 1;
        for (int i = 0; i < thread_count; i++) {
            new JeffsDocumentIndexer(_user, _path, "", _query_config, _schema_config,
                    _export, i + 1, job_id).start();
        }
        return stat_msg;
    }

    private String createJobId() {
        return this.hashCode() + new Date().toString();
    }

    private void deprecate(String title) {
        String solr_url = ABProperties.getSolrURL();
        // http://localhost:8983/solr/admin/cores?action=UNLOAD&core=core0&deleteIndex=true
        // http://localhost:8983/solr/admin/cores?action=RENAME&core=core0&other=core5
        String call_solr = solr_url + "admin/cores?action=UNLOAD&core=" + title
                + "&deleteIndex=true&deleteDataDir=true";

        try {
            TMSolrServer.callSolr(call_solr);
        } catch (SolrCallException e) {
        }
    }

    public static String getDefaultSolrURL() {
        String solr_url = ABProperties.get(ABProperties.SOLRSITE);
        if (!solr_url.endsWith("/")) {
            solr_url += "/";
        }
        return solr_url;
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

    public static String getFieldList(String query) {

        int index = query.indexOf("=");
        if (index > 0) {
            return query.substring(index + 1);
        } else {
            return query;
        }
    }

    public static Object extractValue(Object json, String json_path) throws JSONException {
        if ( json_path == null || json_path.trim().length() == 0 )
            return json;

        if (json instanceof JSONArray) {
            JSONArray ja = (JSONArray) json;
            int vi = json_path.indexOf("[");
            if (vi != 0) {
                GB.print(" the format of the json doesn't match the returned object " + json_path);
            }
            int vf = json_path.indexOf("]");
            String js = json_path.substring(vi + 1, vf);
            js = js.trim();

            if (js.equals("*")) {
                ArrayList o = new ArrayList();
                String next_value = json_path.substring(vf + 1);
                for (int i = 0; i < ja.length(); i++) {
                    o.add(extractValue(ja.get(i), next_value));
                }
                String list_of_stuff = "";
                for ( Object b : o )
                {
                    if ( b != null && b.toString().length() > 0 )
                    {
                        list_of_stuff += b.toString() + " ";
                    }
                }
                return list_of_stuff.trim();
            } else {

                int index = Integer.parseInt(js);
                Object next_object = ja.get(index);
                String next_value = json_path.substring(vf + 1);
                if (next_value == null || next_value.length() == 0) {
                    return next_object;
                } else {
                    return extractValue(next_object, next_value);
                }
            }
        } else if (json instanceof JSONObject) {
            JSONObject ja = (JSONObject) json;
            int vi = json_path.indexOf('.');
            int vfc1 = json_path.indexOf('.', vi + 1);
            int vfc2 = json_path.indexOf('[', vi + 1);
            int delindex = 0;
            if (vfc1>0 && vfc1 < vfc2)// mark the first one.
                delindex = vfc1;
            else
                delindex = vfc2;

            if (vi == 0 && vfc1 < 0 && vfc2 < 0) {
                String final_value = json_path.substring(vi + 1).trim();
                try {
                    if ( ja.has ( final_value ) ) {
                        Object val = ja.get(final_value);
                        return ja.get(final_value);
                    }else
                    {
//                        GB.print ( "Key " + final_value + " was not found in " + ja.toString() );
                        GB.print ( "Key " + final_value + " was not found in " + json_path);
                    }
                } catch (Exception _e) {
                    _e.printStackTrace();
                    GB.print(" FIELD WAS NOT FOUND " + _e.getMessage());
                    return null;
                }
            } else if (delindex > 0) {
                String next = json_path.substring(vi + 1, delindex);
                if ( ja.has ( next ) ) {
                    Object job = ja.get(next);
                    json_path = trimToNextSet(json_path, next);
                    return extractValue(job, json_path);
                }else {
//                    GB.print (" Key " + next + " not found in \t" + json_path );
                }
            } else {
                String final_value = json_path.substring(vi + 1).trim();
                try {
                    Object val = ja.get(final_value);
                } catch (Exception _e) {
                    _e.printStackTrace();
                    GB.print(" FIELD WAS NOT FOUND " + _e.getMessage());
                    return null;
                }
                return ja.get(final_value);
            }
        }
        return null;
    }

    private static String trimToNextSet(String json_path, String next) {

        int offset = 0;
        if (json_path.startsWith(".") && (!next.startsWith("."))) {
            offset = 1;
        }

        String t = json_path.substring(next.length() + offset);
        return t;

    }

    class JeffsDocumentIndexer extends Thread {

        private final String fully_qualified_table_path;
        private String user = null;
        private String source_type = null;
        private Map<String, String> query_config = null;
        private Map<String, Map<String, String>> schema_config = null;
        private String query = null;
        int index = 0;
        private String job_id = null;
        private GBLogger log = GBLogger.getLogger(DocumentStoreToSolr.class);

        public JeffsDocumentIndexer(String _user, String fully_qualified_table_path,
                                    String _source_type, Map<String, String> _query_config,
                                    Map<String, Map<String, String>> _schema_config, String _export,
                                    int i, String _job_id) {
            user = _user;
            this.fully_qualified_table_path = fully_qualified_table_path;
            query_config = _query_config;
            query = _export;
            schema_config = _schema_config;
            index = i;
            job_id = _job_id;
        }

        public void run() {
            try {
                GB.print("\n Job:" + job_id + " indexer started for " + fully_qualified_table_path);
                runIndex();
                GB.print(job_id + " Indexer complete.");
                //GB.exit(1);
            } catch (Exception _e) {
                _e.printStackTrace();
            } finally {
                //completeJob();
            }
        }


        public String runIndex() throws DBProcessFailedException {
            String solr_url = DocumentStoreToSolr.getDefaultSolrURL();
            try {
                Calendar calendar = Calendar.getInstance();
                java.util.Date start_date = calendar.getTime();
                System.out.println(new Timestamp(start_date.getTime()));
                InMemoryJobManager.log(job_id, "Index start time : "
                        + new Timestamp(start_date.getTime()));
                String stat_msg = "Error";
                int increment = 200;
                int start = 0;
                int end = 0;
                boolean hasmore = true;
                int total_count = 0;
                int MAX_COUNT = Integer.MAX_VALUE;
                if (solr_url == null) {
                    InMemoryJobManager
                            .log(job_id,
                                    "Failed... the  app is incorrectly configured (.properties)");
                    throw new DBProcessFailedException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (fully_qualified_table_path == null || fully_qualified_table_path.length() <= 0) {
                    InMemoryJobManager
                            .log(job_id,
                                    "Failed... the web app is incorrectly configured (.properties)");
                    throw new DBProcessFailedException(
                            stat_msg,
                            "Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
                }
                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                // get the connection to the solr site.
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                end = start + 200;
                HttpSolrClient solr = null;
                try {
                    String end_value = query_config.get(ABQFile.END);
                    if (end_value == null) {
                        end_value = "1000000000";
                        InMemoryJobManager.log(job_id,
                                "Max values was not found; MAX COUNT IS SET TO  "
                                        + end_value);
                    }
                    end = Integer.parseInt(end_value);
                    String start_index = query_config.get(ABQFile.START);
                    if (start_index == null)
                        start_index = 0 + "";
                    String increment_ = query_config.get(ABQFile.INCREMENT);
                    if (increment_ == null)
                        increment_ = 1000 + "";
                    start = Integer.parseInt(start_index);
                    increment = Integer.parseInt(increment_);
                } catch (Exception _e) {
                    log.info("We are assuming this particular database sql call doese not require the end, start and increment parameters");
                    _e.printStackTrace();
                    InMemoryJobManager.log(job_id,
                            "Error" + _e.getLocalizedMessage());
                } finally {
                    IOUTILs.closeResource(solr);
                }
                MAX_COUNT = end - start;
                int run_index = 0;
                // VALIDATE

                if ((start + increment) > end) {
                    increment = end - start;
                    InMemoryJobManager
                            .log(job_id,
                                    "The (start+increment) value is greater than the end value; increment is reset");
                }
                LinkedHashMap<String, Map<String, String>> solr_config = new LinkedHashMap<String, Map<String, String>>();
                LinkedHashMap<String, String> urlMap = new LinkedHashMap<String, String>();
                Map<String, String> type_map = new HashMap<String, String>();
                String types = query_config.get(ABQFile.TYPES);
                if (types != null) {
                    String[] t = types.split(",");
                    if (t == null || t.length > 0) {
                        for (String s : t) {
                            String[] r = s.split("=");
                            if (r != null && r.length == 2)
                                type_map.put(r[0].trim(), r[1].trim());
                        }
                    }
                }
                HashMap<String, String> map = new HashMap<String, String>();
                String field_list_str = query;
                String[] field_list = field_list_str.split(",");
                for (String fl : field_list) {
//                System.out.println(" fl " + fl);
                    String[] furl = fl.split("=");
                    if (furl.length != 2) {
                        GB.print(" Field values were not formatted correctly.. please fix this.  fielname=http://etc fieldname2=http//etc/");
                        return null;
                    }

                    String field_type = "string_ci";
                    if (furl[0].equalsIgnoreCase("isisno")) {
                        field_type = "sint";
                    }
                    map.put(furl[0].trim(), field_type);
                    urlMap.put(furl[0].trim(), furl[1].trim());
                }
                // as this point we have the schema defined and the url map ready
                GB.print(" Creating the table " + this.fully_qualified_table_path);
                // now loop over the fields and import the values
                ABTable table = new ABTable(this.fully_qualified_table_path);
                try {
                    table.create(map);
                } catch (Exception _e) {
                    GB.print(" Node already created ");
                }
                try {
                    Thread.sleep(5000l);
                } catch (Exception _e) {
                    _e.printStackTrace();
                }
                return loadData(start, end, urlMap, table);
            } catch (Exception _e) {
                _e.printStackTrace();
                InMemoryJobManager.log(job_id,
                        "Failed... the web app is incorrectly configured ("
                                + _e.getMessage() + ")");
                System.err
                        .println(" failed to parse the increment parameters from "
                                + "the config map"
                                + ".  This is required if "
                                + "you are going to declare start and increment variables "
                                + " in the query ");
                throw new DBProcessFailedException("",
                        "Failed to query. You need to define query init values.");
            }
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


        private String loadData(int startIndex, int endIndex, HashMap<String, String> urlMap, ABTable table) {
            System.out.println("\n\nLoading data....\n\n");

            ArrayList<LinkedHashMap<String, Object>> cache = new ArrayList<LinkedHashMap<String, Object>>();
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
                            current_json = DocumentStoreToSolr.readJsonFromUrl(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        json_path = url;
                    }
                    Object value = null;
                    try {
                        value = extractValue(current_json, json_path);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (value != null && value.toString().length() > 0)
                        if (f.equalsIgnoreCase("helm_search")) {
                            String processedhelm = processHELMForBetterLuceneSearching(value.toString());
                            data.put(f, processedhelm);
                        } else if (f.equalsIgnoreCase("five_prime")) {
                            String processedhelm = processfor_five_prime_chemistry(value.toString());
                            if (processedhelm != null && processedhelm.length() > 0)
                                data.put(f, processedhelm);
                        } else if (f.equalsIgnoreCase("three_prime")) {
                            String processedhelm = processfor_three_prime_chemistry(value.toString());
                            if (processedhelm != null && processedhelm.length() > 0)
                                data.put(f, processedhelm);
                        } else {
                            data.put(f, value.toString());
                        }
                }
                cache.add(data);
                if (cache.size() > 1000) {
                    cache = flushCache(cache, table);
                }
            }
            flushCache(cache, table);
            return "Complete";

//            return null;
        }

        private String processfor_three_prime_chemistry(String s) {
            return JSONFieldsImporter.processfor_three_prime_chemistry(s);
        }

        private String processfor_five_prime_chemistry(String s) {
            return JSONFieldsImporter.processfor_five_prime_chemistry(s);
        }

        private String processHELMForBetterLuceneSearching(String s) {
            return JSONFieldsImporter.processHELMForBetterLuceneSearching(s);
        }

        private ArrayList<LinkedHashMap<String, Object>> flushCache(ArrayList<LinkedHashMap<String, Object>> cache, ABTable table) {
            table.append(cache);
            return new ArrayList<LinkedHashMap<String, Object>>();
        }


        /**
         * Configure the solr with the name and the fields (_params) using the
         * URL and
         *
         * @param _name
         * @param _params
         * @param _url
         * @return
         */
        private String configureSolr(String _name,
                                     HashMap<String, Map<String, String>> _params, String _url) {
            log.config("Solr schema: ");

            Set<String> schema = _params.keySet();
            for (String sc : schema) {
                log.config("\t\t" + sc);
            }

            // {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
            UUID idOne = UUID.randomUUID();
            HashMap<String, String> uuidp = new HashMap<String, String>();
            uuidp.put("fieldName", "TMID");
            uuidp.put("sortable", "true");
            uuidp.put("indexed", "true");
            uuidp.put("defaultString", idOne.toString());
            uuidp.put("dataType", "string");
            uuidp.put("requiredField", "true");
            _params.put("TMID", uuidp);

            Calendar c = Calendar.getInstance();
            HashMap<String, String> last_updated = new HashMap<String, String>();
            last_updated.put("fieldName", "TMID_lastUpdated");
            last_updated.put("sortable", "true");
            last_updated.put("indexed", "true");
            last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
            last_updated.put("dataType", "date");
            last_updated.put("requiredField", "true");
            _params.put("TMID_lastUpdated", last_updated);
            log.info("Generating the solr schema... ");

            String solrSite = ABProperties.get("solrSite");
            return TMSolrServer.createSchema(_url, solrSite, _name, _params,
                    false);
        }

        /**
         * Build the field properties for the solr instance given the sql type
         * int and type name
         *
         * @param type_name
         * @return
         */
        public HashMap<String, String> buildFieldProperties(String type_name) {
            HashMap<String, String> props = new HashMap<String, String>();
            props.put("caseControl", "true");
            props.put("indexed", "true");
            props.put("stored", "true");
            props.put("multiValued", "false");
            props.put("dataType", type_name);
            return props;

            // temp = kvp.get("caseControl");
            // caseSensitive = true;
            // temp = kvp.get("sortable");
            // sortable = false;
            // temp = kvp.get("indexed");
            // indexed = false;
            // temp = kvp.get("stored");
            // stored = false;
            // temp = kvp.get("multiValued");
            // multi = true;
            // temp = kvp.get("defaultSearchField");
            // temp = kvp.get("requiredField");
            // if (temp != null && temp.equalsIgnoreCase("true"))
            // requiredField = true;
            // temp = kvp.get("dataType");
            // if (temp != null) {
            // if (temp.equalsIgnoreCase("string")) {
            // if (caseSensitive)
            // fieldLine = fieldLine.replaceAll("`TYPE`", "text");
            // else
            // fieldLine = fieldLine.replaceAll("`TYPE`", "string");
            // } else if (temp.equalsIgnoreCase("integer")) {
            // if (sortable)
            // fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
            // else
            // fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
            // } else if (temp.equalsIgnoreCase("float")) {
            // if (sortable)
            // fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
            // else
            // fieldLine = fieldLine.replaceAll("`TYPE`", "float");
            // } else
            // fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
            //
        }


        public Date try_to_parse_date_field(String ds) {
            SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd k:mm:ss");
            try {

                Date d = s1.parse(ds);
                return d;

            } catch (Exception _e) {
                _e.printStackTrace();
                System.out.println(" failed to parse date : " + ds);
            }

            return null;
        }

        public boolean inlist(String[] exportFields, String field_name) {
            if (exportFields == null || exportFields.length <= 0)
                return true;
            else {
                field_name = field_name.trim();
                for (String s : exportFields) {
                    if (s.equalsIgnoreCase(field_name))
                        return true;
                }
            }
            return false;
        }

        public String[] parseExportFields(String exportlist) {
            String[] sp = exportlist.split(",");
            if (sp != null) {
                int i = 0;
                for (String s : sp) {
                    sp[i++] = s.trim();
                }
                return sp;
            }
            return null;
        }


        private LinkedHashMap<String, String> parse(String where_clause) {
            String[] sp = where_clause.split("\\s+");
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            for (String s : sp) {

                log.debug(" s : " + s);
                if (s.contains("=")) {
                    String[] v = s.split("=");
                    map.put(v[0], v[1]);
                }
            }
            return map;
        }
    }
}


