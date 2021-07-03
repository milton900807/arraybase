package com.arraybase.flare;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.JDBC;
import com.arraybase.io.ABQFile;
import com.arraybase.plugin.ABIndexer;
import com.arraybase.plugin.ABQFunction;
import com.arraybase.plugin.ABQParams;
import com.arraybase.tm.builder.jobs.Job;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import sun.misc.Unsafe;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

// for reference these are the types and their values. 
//public static final int	ARRAY	2003
//public static final int	BIGINT	-5
//public static final int	BINARY	-2
//public static final int	BIT	-7
//public static final int	BLOB	2004
//public static final int	BOOLEAN	17
//public static final int	CHAR	1
//public static final int	CLOB	2005
//public static final int	DATALINK	70
//public static final int	DATE	94
//public static final int	DECIMAL	3
//public static final int	DISTINCT	2001
//public static final int	DOUBLE	9
//public static final int	FLOAT	6
//public static final int	INTEGER	4
//public static final int	JAVA_OBJECT	2000
//public static final int	LONGVARBINARY	-4
//public static final int	LONGVARCHAR	-1
//public static final int	NULL	0
//public static final int	NUMERIC	2
//public static final int	OTHER	1111
//public static final int	REAL	7
//public static final int	REF	2006
//public static final int	SMALLINT	5
//public static final int	STRUCT	2002
//public static final int	TIME	92
//public static final int	TIMESTAMP	93
//public static final int	TINYINT	-6
//public static final int	VARBINARY	-3
//public static final int	VARCHAR	12

/**
 * This takes in a java object (i.e. the data producer) and indexes it.
 * NOTE: This requires the ArraybaseIndexer api
 */
public class JavaObjectToSolr {

    private GBLogger log = GBLogger.getLogger(JavaObjectToSolr.class);
    private DBConnectionManager dbcm = new DBConnectionManager();
    private GBJobListener gbJobListener = null;
    private String path = null;

    public String run(String _user, String _path, String class_object) {
        // first thing is to instantiate the object
        try {
            Object ob = Class.forName(class_object).newInstance();
            if (ob instanceof ABIndexer) {
                ABIndexer abi = (ABIndexer) ob;
                this.path = _path;
                ABTable abc = new ABTable(this.path.trim());
                try {
                    if (abc.exists()) {
                    }
                } catch (NodeWrongTypeException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return "Complete";
    }

    private String createJobId() {
        return this.hashCode() + new Date().toString();
    }

    class ObjectIndexer extends Thread {
        private String user = null;
        private String table_url = null;
        private String source_type = null;
        private Map<String, String> query_config = null;
        private Map<String, Map<String, String>> schema_config = null;
        private String query = null;
        int index = 0;
        private String job_id = null;
        ABIndexer abIndexer = null;


        public ObjectIndexer(ABIndexer abindexer) {
            this.abIndexer = abindexer;
        }

        public void run() {
            try {

                GB.print("\n Job:" + job_id + " indexer started for " + table_url);
                runIndex();
                GB.print(job_id + " Indexer complete.");
                //GB.exit(1);
            } catch (Exception _e) {
                _e.printStackTrace();
            } finally {
                completeJob();
            }
        }


        public String runIndex() throws DBProcessFailedException {
            try {
                Calendar calendar = Calendar.getInstance();
                Date start_date = calendar.getTime();
                System.out.println(new Timestamp(start_date.getTime()));
                InMemoryJobManager.log(job_id, "Index start time : "
                        + new Timestamp(start_date.getTime()));
                String stat_msg = "Error";
                String solr_url = getDefaultSolrURL();
                // String solr_dir = getDefaultSolrDir();
                if (solr_url == null) {
                    InMemoryJobManager
                            .log(job_id,
                                    "Failed... the  app is incorrectly configured (.properties)");
                    throw new DBProcessFailedException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (path == null || path.length() <= 0) {
                    InMemoryJobManager
                            .log(job_id,
                                    "Failed... the web app is incorrectly configured (.properties)");
                    throw new DBProcessFailedException(
                            stat_msg,
                            "path as not defined.  Try putting a $table= variable in the \"new table script\" field.");
                }

                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                // get the connection to the solr site.
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                ABTable table = new ABTable(path);
                LinkedHashMap<String, Map<String, String>> solr_config = abIndexer.getSchema();
                try {
                    table.create(solr_config);
                } catch (PathNotFoundException e1) {
                    e1.printStackTrace();
                }
                int count = 0;
                int increment = 0;

                while (abIndexer.hasNext()) {
                    SolrInputDocument sid = new SolrInputDocument();
                    Map<String, Object> object = abIndexer.next();
                    Set<String> cn = object.keySet();
                    for (String field : cn) {
                        Object value = object.get(field);
                        sid.addField(field, value);
                    }

                    sid.addField("TMID", TMID.create());
                    sid.addField("TMID_lastUpdated", new Date());
                    docs.add(sid);
                    if (count % 100 == 0) {
                        GB.print(" \t count -> " + count);
                    }

                    if (increment >= abIndexer.getCommitIncrement()) {
                        if (docs != null && docs.size() > 0) {
                            HttpSolrClient solr = table.getHttpClient();
                            System.out.println(" calling : " + solr.toString());
                            solr.add(docs);
                            InMemoryJobManager
                                    .log(job_id, "Committing to index...");
                            solr.commit();
                            InMemoryJobManager.log(job_id, "Commit complete for "
                                    + docs.size() + " documents.");
                            Runtime.getRuntime().gc();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }

                        }
                        docs.clear();
                        increment = 0;
                    }
                    count++;
                }
                if (docs != null && docs.size() > 0) {
                    HttpSolrClient solr = table.getHttpClient();
                    solr.add(docs);
                    InMemoryJobManager
                            .log(job_id, "Committing to index...");
                    solr.commit();
                    InMemoryJobManager.log(job_id, "Commit complete for "
                            + docs.size() + " documents.");
                }
                GB.print("Increment committing:\t: " + docs.size());
                docs.clear();
                HttpSolrClient solr = table.getHttpClient();
                try {
                    solr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (SolrServerException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            Date current = c.getTime();
            InMemoryJobManager.log(job_id,
                    "Job Complete. Cleaning resources... ");
            return "Complete";
        }

        private Map<String, Object> getSolrValues(SolrInputDocument sid) {
            LinkedHashMap<String, Object> v = new LinkedHashMap<String, Object>();
            Collection<String> fields = sid.getFieldNames();
            Iterator<String> fi = fields.iterator();
            while (fi.hasNext()) {
                String field = fi.next();
                Object value = sid.get(field);
                v.put(field, value);

            }
            return v;
        }

        private ABQParams buildABQParams(SolrInputDocument sid) {
            ABQParams p = new ABQParams();
            Iterator<String> names = sid.getFieldNames().iterator();
            while (names.hasNext()) {
                String na = names.next();
                Object value = sid.get(na).getValue();
                if (na != null && value != null)
                    p.append(na, value);
            }
            return p;
        }

        private LinkedHashMap<String, ABQFunction> parseAndLoadExportFieldFunctions(String exportlist) {

            return null;
        }

        private boolean inFunctionlist(LinkedHashMap<String, String> exportFunctions, String field_name) {
            return exportFunctions.containsValue(field_name);
        }

        private LinkedHashMap<String, String> parseExportFieldFunctions(String exportlist) {
            LinkedHashMap<String, String> functions = new LinkedHashMap<String, String>();
            String[] l = exportlist.split(",");
            for (String i : l) {
                String item = i.trim();
                if (item.contains("=")) {
                    // DON'T USE REGEX HERE.  JUST GET THE FIRST INDEX OF THE = SINCE WE MIGHT HNAVE A COMPLETE FUNCTION INLINE
                    int eqindex = item.indexOf('=');
                    if (eqindex > 0) {
                        String p = item.substring(0, eqindex);
                        String v = item.substring(eqindex + 1);
                        functions.put(p.trim(), v.trim());
                    }
                }
            }
            return functions;
        }

        private void completeJob() {
            gbJobListener.jobComplete("Complete.");
            Session session = HBConnect.getSession();
            try {
                session.beginTransaction();
                Criteria ct = session.createCriteria(Job.class).add(Restrictions.eq("job_id", job_id));
                Job job = (Job) ct.uniqueResult();
                if (job != null) {
                    job.setStatus(Job.COMPLETE_STATUS);
                    job.setCompleted(new Date());
                    session.update(job);
                    session.flush();
                    session.getTransaction().commit();
                }
            } finally {
                HBConnect.close(session);
            }
        }

        private void closeJob() {
            // new JobCloser().start();
        }


        public String getDefaultSolrURL() {
            String solr_url = ABProperties.get(ABProperties.SOLRSITE);
            if (!solr_url.endsWith("/")) {
                solr_url += "/";
            }
            return solr_url;
        }
//        private String configureSolr(String _name,
//                                     HashMap<String, Map<String, String>> _params, String _url) {
//            log.config("Solr schema: ");
//
//            Set<String> schema = _params.keySet();
//            for (String sc : schema) {
//                log.config("\t\t" + sc);
//            }
//
//            // {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
//            UUID idOne = UUID.randomUUID();
//            HashMap<String, String> uuidp = new HashMap<String, String>();
//            uuidp.put("fieldName", "TMID");
//            uuidp.put("sortable", "true");
//            uuidp.put("indexed", "true");
//            uuidp.put("defaultString", idOne.toString());
//            uuidp.put("dataType", "string");
//            uuidp.put("requiredField", "true");
//            _params.put("TMID", uuidp);
//
//            Calendar c = Calendar.getInstance();
//            HashMap<String, String> last_updated = new HashMap<String, String>();
//            last_updated.put("fieldName", "TMID_lastUpdated");
//            last_updated.put("sortable", "true");
//            last_updated.put("indexed", "true");
//            last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
//            last_updated.put("dataType", "date");
//            last_updated.put("requiredField", "true");
//            _params.put("TMID_lastUpdated", last_updated);
//            log.info("Generating the solr schema... ");
//
//            String solrSite = ABProperties.get("solrSite");
//            return TMSolrServer.createSchema(_url, solrSite, _name, _params,
//                    false);
//        }

        /**
         * Build the field properties for the solr instance given the sql type
         * int and type name
         *
         * @param type
         * @param type_name
         * @param field_name
         * @return
         */
        public HashMap<String, String> buildFieldProperties(int type,
                                                            String type_name, String field_name) {
            HashMap<String, String> params = new HashMap<String, String>();
            System.out.println(" type : " + type + " type_name " + type_name
                    + " field name : " + field_name);
            HashMap<String, String> props = new HashMap<String, String>();
            props.put("caseControl", "true");
            props.put("indexed", "true");
            props.put("stored", "true");
            props.put("multiValued", "false");
            props.put("dataType", getDataType(type_name));
            return props;
        }

        private String getDataType(String type_name) {
            if (type_name.equalsIgnoreCase("varchar"))
                return "string";
            else if (type_name.equalsIgnoreCase("text"))
                return "text";
            else if (type_name.equalsIgnoreCase("integer"))
                return "integer";
            else if (type_name.equalsIgnoreCase("double"))
                return "sfloat";
            else if (type_name.equalsIgnoreCase("number"))
                return "sfloat";
            else if (type_name.equalsIgnoreCase("sint"))
                return type_name;
            else
                return type_name;
        }

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

        private int updateStatement(String _field_name, int type,
                                    ResultSet res, SolrInputDocument sid) throws SQLException {
// debug shit
//            if (_field_name.equals("orderid")) {
//                double val = res.getDouble("orderid");
//                if (val == 149469)
//                    System.out.println(" pause ");
//            }
            if (type == Types.VARCHAR) {
                String gene = res.getString(_field_name);
                sid.addField(_field_name, gene);
            } else if (type == Types.INTEGER) {
                int int_value = res.getInt(_field_name);
                sid.addField(_field_name, int_value);
            } else if (type == Types.DATE) {
                Date d = res.getDate(_field_name);
                sid.addField(_field_name, d);
            } else if (type == Types.TIMESTAMP) {
                try {
                    Object date = res.getObject(_field_name);
                    if (date != null) {
                        String str = date.toString();
                        // System.out.println("__ ++" + str);
                        sid.addField(_field_name, date);
                    } else {
                        String ds = res.getString(_field_name);
                        if (ds != null) {
                            Date datev = try_to_parse_date_field(ds);
                            if (datev != null)
                                sid.addField(_field_name, datev);
                        }
                    }
                } catch (Exception _e) {
                    GB.print(" Failed to load the field : " + _field_name);
                    _e.printStackTrace();
                    return 0;
                }
            } else if (type == Types.TIME) {
                Date d = res.getTime(_field_name);
                sid.addField(_field_name, d);
            } else if (type == Types.NUMERIC) {
                // WE NEED TO PULL THIS OUT AS A DOUBLE BECAUSE
                // THERE COULD BE A NUMERIC OVERFLOW IF WE USE ANYTHING ELSE..
                // ESPECIALLY WITH ORACLE.
                Double l = res.getDouble(_field_name);
                if (l == null)
                    l = res.getDouble(_field_name.toUpperCase());
                if (l == null)
                    l = res.getDouble(_field_name.toLowerCase());


                if (Math.floor(l) == l) {
                    Integer inval = l.intValue();
                    sid.addField(_field_name, inval);
                } else {
                    sid.addField(_field_name, l);
                }
            } else if (type == Types.CHAR) {
                String gene = res.getString(_field_name);
                sid.addField(_field_name, gene);
            } else if (type == Types.INTEGER) {
            } else {
                try {
                    Object object = res.getObject(_field_name);
                    if (object != null) {
                        sid.addField(_field_name, object);
                    } else {
                        GB.print("Failed to add " + _field_name
                                + "\t with TYPE ID: " + type);

                        return 0;
                    }
                } catch (Exception _e) {
                    GB.print(_field_name + " type = " + type
                            + " error getting index : " + index + 1);
                    _e.printStackTrace();
                }
            }
            return 1;
        }

    }

    private ABQFunction loadFunctionObject(String function, LinkedHashMap<String, Integer> fields) {
        //org.monomer.abplugs.HELMConjugateExtractor.eval([0].helm)
        if (function.startsWith("fetch(")) {
            return null;
        } else if (function.startsWith("(float)")) {
            return createJavaFunctionFloatObject(function, fields);
        } else if (function.startsWith("{")) {
            return createJavaFunctionObject(function, fields);
        } else if (function.startsWith("->")) {
            return null;
        }
        String clst = function.trim();
        int e = function.indexOf('(');
        if (e > 0) {
            clst = function.substring(0, e);
            e = clst.lastIndexOf('.');
            clst = function.substring(0, e);
        }


        try {
            Object ob = Class.forName(clst).newInstance();
            if (ob instanceof ABQFunction) {
                return (ABQFunction) ob;
            }
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }


        return null;
    }

    private ABQFunction createJavaFunctionFloatObject(String function, LinkedHashMap<String, Integer> fields) {

        if (function.trim().startsWith("(float)")) {
            int fb = function.indexOf(')');
            function = function.substring(fb + 1).trim();
        }

        final String className = "Oarca" + TMID.create();
        final String path = "com.arraybase.dynamoclass";
        final String fullClassName = path.replace('.', '/') + "/" + className;
        final StringBuilder source = new StringBuilder();
        source.append("package " + path + ";");
        source.append("import com.arraybase.plugin.ABQFunction;");
        source.append("import com.arraybase.plugin.ABQParams;");
        source.append("public class " + className + " implements ABQFunction{\n");
        source.append("public float evalFloat ( ABQParams params ) throws NumberFormatException \n" + extractTypes(function, fields) + "\n");
        source.append("\npublic String toString() {\n");
        source.append("     return \"HelloWorld - Java Dynamic Class Creation...\";");
        source.append(" }\n");

        // set the type of the function as a floating piont calculation
        source.append("\n public String getType() {\n");
        source.append("     return FLOAT;");
        source.append(" }\n");
        source.append("\n public String eval(ABQParams params) { return null; \n");
        source.append(" }\n");


        source.append("}\n");
        System.out.println(" we have the function :\n\n\n " + source);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }

            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };
        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().
                        getStandardFileManager(null, null, null)) {
            public JavaFileObject getJavaFileForOutput(
                    Location location, String className,
                    JavaFileObject.Kind kind,
                    FileObject sibling) {
                return simpleJavaFileObject;
            }
        };
        ToolProvider.getSystemJavaCompiler().getTask(null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();
        final byte[] bytes = byteArrayOutputStream.toByteArray();
// use the unsafe class to load in the class bytes
        final Field f;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Unsafe unsafe = (Unsafe) f.get(null);
            final Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length, ClassLoader.getSystemClassLoader(), this.getClass().getProtectionDomain());
//            final Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes,
            final Object o = aClass.newInstance();
            ABQFunction abv = (ABQFunction) o;
            return abv;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


        return null;

    }

    private ABQFunction createJavaFunctionObject(String function, LinkedHashMap<String, Integer> fields) {


        final String className = "Oarca" + TMID.create();
        final String path = "com.arraybase.dynamoclass";
        final String fullClassName = path.replace('.', '/') + "/" + className;
        final StringBuilder source = new StringBuilder();
        source.append("package " + path + ";");
        source.append("import com.arraybase.plugin.ABQFunction;");
        source.append("import com.arraybase.plugin.ABQParams;");
        source.append("public class " + className + " implements ABQFunction{\n");
        source.append("    public String eval ( ABQParams params )" + extractTypes(function, fields));
        source.append("public float evalFloat ( ABQParams params ) throws NumberFormatException { throw new NumberFormatException (); }\n");
        source.append("public String getType () {return STRING;}\n");
        source.append("}\n");
        System.out.println(" we have the function :\n\n\n " + source);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }

            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };
        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().
                        getStandardFileManager(null, null, null)) {
            public JavaFileObject getJavaFileForOutput(
                    Location location, String className,
                    JavaFileObject.Kind kind,
                    FileObject sibling) {
                return simpleJavaFileObject;
            }
        };
        ToolProvider.getSystemJavaCompiler().getTask(null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();
        final byte[] bytes = byteArrayOutputStream.toByteArray();
// use the unsafe class to load in the class bytes
        final Field f;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Unsafe unsafe = (Unsafe) f.get(null);
            final Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes.length, ClassLoader.getSystemClassLoader(), this.getClass().getProtectionDomain());
//            final Class aClass = unsafe.defineClass(fullClassName, bytes, 0, bytes,
            final Object o = aClass.newInstance();
            ABQFunction abv = (ABQFunction) o;
            return abv;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


        return null;
    }

    private String extractTypes(String function, LinkedHashMap<String, Integer> fields) {
        String f = function.trim();
        f = f.substring(1, f.length() - 1);
        Set<String> keys = fields.keySet();
        for (String key : keys) {
            Integer value = fields.get(key);
            if (value == Types.FLOAT || value == Types.DOUBLE || value == Types.NUMERIC) {
                f = "float " + key + "=params.getFloat(\"" + key + "\");\n" + f;
            } else {
                f = "String " + key + "=params.getString(\"" + key + "\");\n" + f;
            }
        }
        return "{" + f + "}";
    }

    public static Connection createConnection(Map<String, String> scope_config,
                                              String job_id) throws SQLException {
        String url = scope_config.get(ABQFile.URL);
        String pass = scope_config.get(ABQFile.PASSWORD);
        String user = scope_config.get(ABQFile.USER);
        String driver = scope_config.get(ABQFile.DRIVER_CLASS);
        InMemoryJobManager.log(job_id, "DB Connection: connecting... " + url);
        return createConnection(url, pass, user, driver, job_id);
    }

    public static Date try_to_parse_date_field(String ds) {
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

    public static boolean inlist(String[] exportFields, String field_name) {
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

    public static String[] parseExportFields(String exportlist) {
        ArrayList<String> fields = new ArrayList<String>();
        String[] sp = exportlist.split(",");
        if (sp != null) {
            int i = 0;
            for (String s : sp) {

                // remove the type if there is one specified
                String t = s.trim();
                if (t.contains("=")) {
                    String[] xsp = t.split("=");
                    fields.add(xsp[0].trim());
                } else {
                    fields.add(s.trim());
                }
            }

            String[] fsp = fields.toArray(new String[fields.size()]);
            return fsp;
        }
        return null;
    }

    /**
     * Method to encapsulate the various ways of making a jdbc connection given
     * a url.
     *
     * @param url
     * @param pass
     * @param user
     * @param _driver
     * @return
     * @throws SQLException
     */
    private static Connection createConnection(String url, String pass,
                                               String user, String _driver, String _job_id) throws SQLException {
        return JDBC.createConnection(url, pass, user, _driver, _job_id);
    }

    /**
     * @param q
     * @return
     * @deprecated use buildOracleQuery2
     */

    @Deprecated
    public static String buildOracleQuery(String q, int start, int length) {
        q = q.trim();
        if (q.startsWith("select")) {
            q = q.replaceFirst("select", "select ROWNUM,");
        }
        String query = "select * from " + "(select a.*, ROWNUM r__ from " + ""
                + "(" + q + ") a where ROWNUM <" + (start + length)
                + ") where r__ >=" + start;
        return query;
    }

    // --select * from
// --(select a.*, ROWNUM r__ from
// --(select b.*, ROWNUM from
// --(select distinct p.set_id,
// --ll.official_symbol as gene, p.set_name, p.forward_isisno,
// p.forward_len, p.reverse_isisno, p.reverse_len, p.probe_isisno,
// p.probe_len, tar.mol_targetid
// --from antisense.pp_set_tss_pos_snapshot p join it_targetseq tar on
// p.tss_id= tar.target_id join it_moltarg_to_ll_snapshot ll
// --on tar.MOL_TARGETID=ll.MOL_TARGETID where ll.mol_targetid=114) b)
// --a where ROWNUM <100000) where r__ >=0
    public static String buildOracleQuery2(String q, int start, int length) {
        q = q.trim();
//		if (q.startsWith("select")) {
//			q = q.replaceFirst("select", "select ROWNUM,");
//		}
        String query = "select * from " + "(select a.*, ROWNUM r__ from "
                + "(select b.*, ROWNUM from" + "(" + q + ")" + " b)"
                + "a where ROWNUM <" + (start + length) + ") where r__ >="
                + start;
        return query;
    }

    public static <T, E> List<T> getKeysByValue(Map<T, E> map, E value) {
        List<T> keys = new ArrayList<T>();
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    /**
     * This will query the database and convert the results to a map object
     */
    private LinkedHashMap<String, Object> query(Map<String, String> _db_config,
                                                LinkedHashMap<String, Integer> types, String sql_string) {
        Connection _connection = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            _connection = JDBC.createConnection(_db_config, 1);

            LinkedHashMap<String, Object> row_d = new LinkedHashMap<String, Object>();
            st = _connection.createStatement();
            if (sql_string.contains(";")) {
                log.info("\t " + sql_string);
                String[] pre_q = sql_string.split(";");
                for (int i = 0; i < pre_q.length - 1; i++) {
                    st.execute(pre_q[i]);
                }
                sql_string = pre_q[pre_q.length - 1];
            }

            log.info("\t " + sql_string);
            rs = st.executeQuery(sql_string);
            // get the results
            while (rs.next()) {
                Set<String> fields = types.keySet();
                for (String field : fields) {
                    int type = types.get(field);
                    if (type == Types.VARCHAR) {
                        String value = rs.getString(field);
                        row_d.put(field, value);
                    } else if (type == Types.INTEGER) {
                        int int_value = rs.getInt(field);
                        row_d.put(field, int_value);
                    } else {
                        String value = rs.getString(field);
                        row_d.put(field, value);
                    }
                }
            }
            return row_d;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
            JDBC.closeConnection(_connection);
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

    private String updateStatement(String _field_name, int type, ResultSet res,
                                   SolrInputDocument sid, int index) throws SQLException {

        if (type == Types.VARCHAR) {
            String gene = res.getString(index + 1);
            sid.setField(_field_name, gene);
            return gene;
        } else if (type == Types.INTEGER) {
            int int_value = res.getInt(index + 1);
            sid.setField(_field_name, int_value);
            return int_value + "";
        } else {
            String value = res.getString(index + 1);
            sid.setField(_field_name, value);
            return value;
        }
    }

    public static String setRange(String url, String q, int start, int increment) {
        if (url.startsWith("jdbc:mysql")) {
            if (q.contains("$start")) {
                q = q.replace("$start", "" + start);
                q = q.replace("$increment", "" + increment);
            } else {
                q = q + " limit " + start + ", " + increment;
            }
        } else {
            q = buildOracleQuery2(q, start, increment);
        }
        return q;
    }

}
