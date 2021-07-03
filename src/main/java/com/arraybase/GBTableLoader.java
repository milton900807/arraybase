package com.arraybase;

import com.arraybase.bio.FASTALoader;
import com.arraybase.bio.FASTATableAppend;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.JDBC;
import com.arraybase.db.jdbc.TypeMappingException;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.*;
import com.arraybase.flare.parse.GBParseException;
import com.arraybase.io.ABQFile;
import com.arraybase.io.parse.FailedToParseTypeException;
import com.arraybase.lac.LAC;
import com.arraybase.lac.LACAction;
import com.arraybase.lac.LACActionFactory;
import com.arraybase.modules.GBTypes;
import com.arraybase.modules.UsageException;
import com.arraybase.net.FTPImporter;
import com.arraybase.plugin.ABIndexer;
import com.arraybase.shell.ImportJarArrayBaseIndexer;
import com.arraybase.shell.cmds.SetSchemaCommand;
import com.arraybase.tm.*;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.Date;

public final class GBTableLoader {

    private XLSObject tstruct = null;
    private String local_file = null;
    private String username = null;
    private DBConnectionManager dbcm = new DBConnectionManager();
    private static GBLogger log = GBLogger.getLogger(GBTableLoader.class);

    /**
     * Load the table with the GB Loader
     */
    public GBTableLoader(String _username, XLSObject _tstruct,
                         String _local_file) {
        tstruct = _tstruct;
        username = _username;
        local_file = _local_file;
    }

    public static HashMap<String, Map<String, String>> buildSolrFields(
            XLSObject _ob) {
        String[] fields = _ob.getFields();
        String[] type = _ob.getTypes();
        if (type == null) {
            type = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                type[i] = "String";
            }
        }
        LinkedHashMap<String, Map<String, String>> params = new LinkedHashMap<String, Map<String, String>>();
        for (int i = 0; i < fields.length; i++) {
            HashMap<String, String> field = new HashMap<String, String>();
            field.put("fieldName", fields[i]);
            field.put("sortable", "true");
            field.put("indexed", "true");
            field.put("defaultString", "");
            field.put("dataType", type[i]);
            field.put("requiredField", "false");
            params.put("" + fields[i], field);
        }
        return params;
    }

    public static XLSObject rename_fields_for_solr(XLSObject xls_ob) {
        String[] fields = xls_ob.getFields();
        int index = 0;
        for (int l = 0; l < fields.length; l++) {
            index = 0;
            String t = fields[l].trim();
            t = t.replace(':', '_');
            t = t.replace(' ', '_');
            t = t.replace("#", "_Num_");
            t = t.replace('/', '_');
            t = t.replace('\\', '_');
            t = t.replace('$', '_');
            t = t.replace(',', '_');
            t = t.replace("(", "__");
            t = t.replace(")", "__");
            t = t.replace(',', '_');
            t = t.replace("%", "_percent_");
            fields[l] = t;
        }
        fields = mkdistinct(fields, index);
        xls_ob.setFields(fields);
        return xls_ob;
    }

    /**
     * Create a table given the table structure: Map<String, String>
     *
     * @param ignore
     */
    static String createAndLoadTable(String user_name, String local_file,
                                     String _delim, String path, Map<String, String> table_structure,
                                     ArrayList<Integer> ignore, SourceType _node_type) {
        String table_name = NameUtiles.convertToValidCharName(path);
        XLSObject xls_object = createTableStructureObject(local_file,
                table_name, _delim, table_structure);
        GBTableLoader t = new GBTableLoader(user_name, xls_object, local_file);
        if (_delim == null || _delim.length() <= 0)
            _delim = "\\t";
        t.load(_delim, ignore, true);
        String target = xls_object.getName();
        LACAction lac = LACActionFactory.create(target, LACAction.SEARCH_CORE,
                "*:*");
        String lacs = lac.getLAC();
        GB.print("Creating path " + path);
        TNode node = GBNodes.mkNode(user_name, path, lacs, _node_type);
        if (node != null)
            GB.print("[" + node.getNode_id() + "] Node created. ");
        else
            GB.print(" Node not created ");

        return lacs;
    }

    /**
     * We're goign to make a table from any text file with basic delimiter. Tab
     * delimiting. printUsage(
     * "EXAMPLE: gb totable donaldm fields=1 types=2 ignore=3 46239h2010.txt /gne/research/my_new_table"
     * );
     *
     * @param user_name
     * @param local_file
     * @param path
     * @param vals
     * @return
     */
    static String mkTable(String user_name, String local_file, String _delim,
                          String path, Map<String, String> vals) {
        log.debug(" local file into a table  : " + user_name);
        String fields = vals.get("-fields");
        GBLogger.status(" fields : " + fields);
        String types = vals.get("-types");
        log.debug(" types  : " + types);
        String ignore = vals.get("-ignore");
        int title_rowi = 0;
        int types_rowi = 1;

        log.debug(vals);

        ArrayList<Integer> ig_rowi = new ArrayList<Integer>();
        if (fields != null) {
            Integer field_row = Integer.parseInt(fields);
            title_rowi = field_row;
        }
        if (types != null) {
            Integer types_row = Integer.parseInt(types);
            types_rowi = types_row;
        }
        if (ignore != null) {
            String[] ignore_rows = ignore.split(",");
            if (ignore_rows != null) {
                for (String ir : ignore_rows) {
                    Integer it = Integer.parseInt(ir);
                    ig_rowi.add(it.intValue());
                }
            }
        }
        ig_rowi.add(title_rowi);
        ig_rowi.add(types_rowi);
        Integer[] ttt = ig_rowi.toArray(new Integer[ig_rowi.size()]);
        int[] ignore_rowi = new int[ttt.length];
        int index = 0;
        for (Integer i : ttt) {
            ignore_rowi[index++] = i;
        }

        String table_name = NameUtiles.convertToValidCharName(path);
        XLSObject xls_object = createTableStructureObject(title_rowi,
                types_rowi, ignore_rowi, local_file, table_name, _delim);
        GBTableLoader t = new GBTableLoader(user_name, xls_object, local_file);
        if (_delim == null || _delim.length() <= 0)
            _delim = "\\t";
        t.load(_delim, false);
        String target = xls_object.getName();
        LACAction lac = LACActionFactory.create(target, LACAction.SEARCH_CORE,
                "*:*");
        String lacs = lac.getLAC();
        return lacs;
    }

    private static XLSObject createTableStructureObject(int title_rowi,
                                                        int types_rowi, int[] ignore_rowi, String _file_name,
                                                        String _table_name, String delim) {
        String file_name = GBPathUtils.getLeaf(_file_name);
        if (_table_name == null)
            _table_name = file_name;
        XLSObject tstruct = new XLSObject(_table_name, delim);
        FileReader reader = null;
        BufferedReader br = null;
        try {
            reader = new FileReader(new File(_file_name));
            br = new BufferedReader(reader);
            String line = br.readLine();
            int index = 0;
            boolean skip = false;// skip reading another line if the types are
            // not found.
            tstruct.setIgnoreRows(ignore_rowi);
            while (line != null) {
                if (index == title_rowi) {
                    parseTitles(tstruct, line);
                } else if (index == types_rowi) {
                    try {
                        parseTypes(tstruct, line);
                    } catch (FailedToParseTypeException e) {
                        GB.print("No types line defined.. will load this as string types");
                        tstruct.setTypes("string");

                        // this pretty much means we have a regular csv file
                        // with no types defined.
                        // so we need to remove this row as an ignored row in
                        // the csv parsing routine.
                        tstruct.removeIgnoreRow(types_rowi);

                        skip = true;
                    }
                }
                if (!skip)
                    line = br.readLine();
                skip = false;
                index++;
            }

            String[] types = tstruct.getTypes();
            if (types == null || types.length <= 0) {
                tstruct.setTypes(XLSObject.DEFAULT_TYPE);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(br);
            IOUTILs.closeResource(reader);
        }
        return tstruct;
    }

    private static XLSObject createTableStructureObject(String _file_name,
                                                        String _table_name, String delim,
                                                        Map<String, String> table_structure) {
        String file_name = GBPathUtils.getLeaf(_file_name);
        if (_table_name == null)
            _table_name = file_name;
        XLSObject tstruct = new XLSObject(_table_name, delim);

        String[] fields = getfields(table_structure);
        String[] types = getTypes(table_structure);
        tstruct.setFields(fields);
        tstruct.setTypes(types);
        if (types == null || types.length <= 0) {
            tstruct.setTypes(XLSObject.DEFAULT_TYPE);
        }
        return tstruct;
    }

    private static String[] getTypes(Map<String, String> table_structure) {
        Set<String> fields = table_structure.keySet();
        String[] f = new String[fields.size()];
        int index = 0;
        for (String field : fields) {
            String type = table_structure.get(field);
            f[index++] = type;
        }
        return f;
    }

    private static String[] getfields(Map<String, String> table_structure) {
        Set<String> fields = table_structure.keySet();
        String[] f = new String[fields.size()];
        int indx = 0;
        for (String field : fields) {
            f[indx++] = field;
        }
        return f;
    }

    private static void parseTypes(XLSObject tstruct, String line)
            throws FailedToParseTypeException {
        String[] types = line.split(tstruct.getDelim());
        if (types == null || types.length <= 0) {
            GB.print(" Using the delim: unable to get types line from " + line);
            throw new FailedToParseTypeException();
        }
        if (!validType(types)) {
            GB.print(" Valid types were not found in:\n\t\t " + line);
            throw new FailedToParseTypeException();
        }

        String[] adjusted_types = FieldTypes.map(types);
        tstruct.setTypes(adjusted_types);
    }

    private static final String[] valid_types = {"i", "s", "f", "d", "t",
            "sint", "int", "text", "string", "integer", "sortedInteger",
            "double", "sdouble", "sfloat", "float", "Date", "number"};

    private static boolean validType(String[] types) {

        for (String t : types) {
            t = t.trim();

            boolean found_one = false;
            for (String v : valid_types) {
                if (t.equalsIgnoreCase(v)) {
                    found_one = true;
                }
            }
            if (!found_one) {
                return false;
            }
        }
        return true;
    }

    private static void parseTitles(XLSObject tstruct, String line) {
        String[] fields = line.split(tstruct.getDelim());
        tstruct.setFields(fields);
    }

    public static String[] mkdistinct(String[] fields, int index) {
        for (int l = 0; l < fields.length; l++) {
            index = 0;
            String t = fields[l].trim();
            if (!isDistinct(fields, t))
                t = increment(fields, t, index++);
            fields[l] = t.trim();
        }
        return fields;
    }

    public static String increment(String[] fields, String t, int i) {
        String tt = t + i;
        for (int j = 0; j < fields.length; j++) {
            // String d = fields[j].trim();
            if (tt.equalsIgnoreCase(fields[j].trim())) {
                tt = increment(fields, tt + i, i++);
            }
        }
        return tt;
    }

    public static boolean isDistinct(String[] fields, String t) {
        int index = 0;
        for (int j = 0; j < fields.length; j++) {
            if (t.equalsIgnoreCase(fields[j].trim())) {
                if (index > 0)
                    return false;
                index++;
            }
        }
        return true;
    }

    /**
     * Make a table froma local xls file.
     * <p/>
     * TODO: Really need to implement the parameters feature.
     *
     * @return
     */
    private static String mkTableFromXLS(String userName, String local_file,
                                         String path, Map<String, String> _params) {
        String local_file_name = GBPathUtils.getLeaf(local_file);
        String[] ls = GBNodes.listPath(path);
        if (ls == null) {
            return "The directory : " + path
                    + " was not found.  Please create it first";
        }
        local_file_name = local_file_name.trim();
        String gb_file_name = GBPathUtils.createNewSequenceFileName(ls,
                local_file_name, 0);
        if (gb_file_name != null) {
            File fi = new File(local_file);
            if (!fi.exists()) {
                return "Failed to find the local file.";
            } else {

                DBConnectionManager db = new DBConnectionManager();
                XLSIntegration xls = new XLSIntegration(db);
                ProcessReport report = xls.buildTableFromXLSFile(gb_file_name,
                        userName, "", fi, _params);
                String core_name = report.getCore();

                // create an action node.
                String lac = LAC.construct(core_name, "load", "*");
                NodeManager manager = new NodeManager();
                TNode node = manager.createPath(path + "/" + local_file_name,
                        userName, SourceType.DB, lac);

                if (node != null)
                    return "Table created.";
                else
                    // TODO: put in an exception frameowerk here.
                    return "Failed to create the node... but it looks like the core was created.  debug in gb..";
            }
        }
        return null;
    }

    /**
     * Load the table using the table structure object
     *
     * @param _delim
     */
    public void load(String _delim, boolean _include_row_number) {
        try {
            tstruct = rename_fields_for_solr(tstruct);
            HashMap<String, Map<String, String>> params = buildSolrFields(tstruct);
            TableManager tmd = new TableManager(dbcm);
            tmd.build(username, TableManager.TMSOLR, tstruct.getName(), "",
                    "1", params, null);
            LoadTableToSolr.loadTxt(local_file, _delim, _include_row_number,
                    tstruct);
        } catch (LoaderException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the table using the table structure object
     *
     * @param _delim
     * @param b
     * @param ignore
     */
    public void load(String _delim, ArrayList<Integer> ignore, boolean b) {
        try {
            tstruct = rename_fields_for_solr(tstruct);
            HashMap<String, Map<String, String>> params = buildSolrFields(tstruct);
            TableManager tmd = new TableManager(dbcm);
            tmd.build(username, TableManager.TMSOLR, tstruct.getName(), "", "1", params, null);
            tstruct.addIgnoreRows(ignore);

            LoadTableToSolr.loadTxt(local_file, _delim, false, tstruct);
        } catch (LoaderException e) {
            e.printStackTrace();
        }
    }

    public static void mkTableFromXLS(String[] _args) {
        String user_name = _args[1];
        String local_file = _args[2];
        String path = _args[3];
        System.out.println(mkTableFromXLS(user_name, local_file, path, null));
    }

    /**
     * Load the text file into a table...
     */
    public static void load(String user_name, String localfile, String path,
                            Map<String, String> vals) {
        // Map<String, String> vals = GB.parseVals(_args);
        String delim = vals.get("-delim");
        if (delim == null)
            delim = "\\t";
        String lac = GBTableLoader.mkTable(user_name, localfile, delim, path,
                vals);
        GB.print("Creating path " + path);
        TNode node = GBNodes.mkNode(user_name, path, lac, SourceType.DB);
        if (node != null)
            GB.print("[" + node.getNode_id() + "] Node created. ");
        else
            GB.print(" Node not created ");
    }

    /**
     * Append a table with a map of values
     *
     * @throws NodeNotFoundException
     */
    public static String append(String path, ArrayList<GRow> rows)
            throws NodeNotFoundException {

        GBNodes nodes = GB.getNodes();
        TNode node = nodes.getNode(path);
        if (node == null) {
            throw new NodeNotFoundException(path);
        }
        String core = nodes.getCore(path);
        TableManager.update(core, rows);
        return "Update complete.";
    }

    public static void appendABQ(String u, String abq_file, String gb_file)
            throws TypeMappingException, UsageException {
        File f = new File(abq_file);

        Properties abq_properties = new Properties();
        try {
            abq_properties = ABQFile.load(f);
        } catch (IOException e) {
            GB.print("IO Error: Failed to load the abq file.  Appears I cannot read it.");
            e.printStackTrace();
        } catch (UsageException e) {
            e.printStackTrace();
            GB.print("IO Error: Failed to load the abq file.  Appears I cannot read it.");
        }


        if (gb_file == null || gb_file.length() <= 0) {

            String path = abq_properties.getProperty(ABQFile.NODE_PATH);
            if (path == null || path.length() <= 0) {
                throw new UsageException("Arraybase node path was not found in the command line or the abq file.  Please provide this before loading.");
            }
            gb_file = path;
        }

        TNode node = GB.getNodes().getNode(gb_file);
        String lac = node.getLink();
        // if no link is available that means we are not pointing to a table
        // so let's do that; i.e. make a table.
        if (lac == null || lac.equals("?")) {
            String user = GB.getDefaultUser();
            String schema = NameUtiles.convertToValidCharName(gb_file);
            DBConnectionManager dbcm = GB.getConnectionManager();
            NodeManager tmnode = new NodeManager(dbcm);
            ArrayList<GColumn> gclist = new ArrayList<GColumn>();
            GColumn column = new GColumn("string", "name");
            gclist.add(column);
            LinkedHashMap<String, Map<String, String>> _params = SetSchemaCommand
                    .createParameters(gclist);
            Set<String> list2 = _params.keySet();
            for (String l : list2) {
                System.out.println(" \t\tColumn " + l);
            }
            TableManager tmd = new TableManager(dbcm);
            tmd.build(user, TableManager.TMSOLR, schema, "__", "1", _params,
                    null);
            log.debug(" Table " + schema + "  has been built.");
            // TableManager table_manager = tables.get
            GB.print("\n\n\n created schema = \t " + gb_file);
            String link = "" + schema + ".search(*:*)";
            node.setLink(link);
            node.setLastEditedDate(new Date());
            tmnode.save(node);
        }

        Map<String, String> af = convert_(abq_properties);
        // we have to make sure the schema match
        try {
            ArrayList<GColumn> list = GB.describeTable(gb_file);
            list = GBSearch.removeTrackingColumns(list);
            Connection conn = SQLToSolr.createConnection(af, new Date().toString());
            Statement st = null;
            ResultSet rs = null;
            try {
                st = conn.createStatement();
                String query = af.get(ABQFile.QUERY);
                String url = abq_properties.getProperty(ABQFile.URL);
                query = SQLToSolr.setRange(url, query, 0, 1);
                rs = st.executeQuery(query);
                ResultSetMetaData meta = rs.getMetaData();
                int col_count = meta.getColumnCount();
                for (int index = 1; index < col_count; index++) {
                    String type = meta.getColumnTypeName(index);
                    String column_name = meta.getColumnName(index);
                    if (index < list.size()) {
                        GColumn column = list.get(index);
                        GB.print(type + ":" + column_name + " --> "
                                + column.getType() + ":" + column.getName());
                    } else {
                        GB.print(type
                                + ":"
                                + column_name
                                + " --> does not map to existing schema and will not be loaded");

                    }

                }
                for (int index = 1; index < col_count; index++) {
                    int type = meta.getColumnType(index);
                    String typename = meta.getColumnTypeName(index);
                    if (index < list.size()) {
                        GColumn column = list.get(index);
                        String ctype = column.getType();
                        if (!canMapFromRDBToSolr(ctype, type)) {
                            throw new TypeMappingException("Cannot map the type : "
                                    + column.getName() + " to type: " + typename);
                        }
                    }
                }
            } finally {
                JDBC.closeResultSet(rs);
                JDBC.closeStatement(st);
                JDBC.closeConnection(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            e.printStackTrace();
        }

    }

    public static boolean canMapFromRDBToSolr(String ctype, int type) {
        if (ctype.equalsIgnoreCase(GBTypes.STRING)) {
            if (type == Types.CHAR || type == Types.LONGNVARCHAR
                    || type == Types.LONGVARCHAR || type == Types.NCHAR
                    || type == Types.SQLXML || type == Types.VARCHAR
                    || type == Types.VARBINARY)
                return true;
        } else if (ctype.equalsIgnoreCase(GBTypes.BOOLEAN)) {
            if (type == Types.BOOLEAN)
                return true;
        } else if (ctype.equalsIgnoreCase(GBTypes.DOUBLE))
            if (type == Types.DOUBLE || type == Types.FLOAT
                    || type == Types.BIGINT || type == Types.BIT
                    || type == Types.DECIMAL || type == Types.SMALLINT
                    || type == Types.INTEGER)
                return true;
            else if (ctype.equalsIgnoreCase(GBTypes.FLOAT)) {
                if (type == Types.DOUBLE || type == Types.FLOAT
                        || type == Types.BIGINT || type == Types.BIT
                        || type == Types.DECIMAL || type == Types.SMALLINT
                        || type == Types.INTEGER)
                    return true;
            } else if (ctype.equalsIgnoreCase(GBTypes.INT)) {
                if (type == Types.BIGINT || type == Types.BIT
                        || type == Types.SMALLINT || type == Types.INTEGER)
                    return true;
            } else if (ctype.equalsIgnoreCase(GBTypes.BOOLEAN)) {
                if (type == Types.BOOLEAN || type == Types.BIT
                        || type == Types.SMALLINT || type == Types.INTEGER)
                    return true;
            } else if (ctype.equalsIgnoreCase(GBTypes.TEXT)) {
                return true;
            }
        return false;
    }

    /**
     * Load a sql descriptor file.
     */
    public static void loadABQ(String u, String abq_file, String gb_file, String jobid, String final_operation)
            throws IOException, UsageException {
        log.debug(" Loading the abq   " + abq_file);
        File f = new File(abq_file);
        GB.print("Loading " + f.getAbsolutePath());
        Properties paf = ABQFile.load(f);
        Map<String, Object> af = convert(paf);
        if (gb_file == null || gb_file.length() <= 0) {
            String path = (String) af.get(ABQFile.NODE_PATH);
            if (path == null || path.length() <= 0)
                throw new UsageException("Arraybase node path was not found in the command line or the abq file.  Please provide this before loading.");
            gb_file = path;
        }
        String url = (String) af.get(ABQFile.URL);
        af.put("path", gb_file);
        af.put("gbuser", u);
        af.put("job_id", jobid);
        af.put("url", url);
        af.put ("final-operation", final_operation);


        String query = paf.getProperty("query");
        if (query == null || query.length() <= 0) {
            GB.print(" Please provide a query syntax... ");
            return;
        }
        if (query.toLowerCase().startsWith("select ")) {
            // {{ IF WE HAVE A SITUATION WHERE WE ARE LOADING FROM A DATABASE USING SQL THEN WE DO THIS }}
            GBModule mod = GBModuleBuildFactory.create(GBModule.ABQ, null);
            if (mod == null) {
                throw new UsageException("Please provide a select statement for the search ");
            } else {
                GB.print(" Mod loaded " + mod.getModName());
            }
            mod.exec(af);
        } else {
            GBModule mod = GBModuleBuildFactory.create(GBModule.ABQ_FOR_DOCUMENT_STORE, null);
            mod.exec(af);
        }
    }

    /**
     * Load a sql descriptor file.
     */
    public static void loadABQ(String user, Properties paf, String gb_file)
            throws IOException, UsageException {
        log.debug(" Loading the query file... ");
        Map<String, Object> af = convert(paf);
        log.debug("printing the configuration properties of the abq file ");
        String url = (String) af.get(ABQFile.URL);
        af.put("path", gb_file);
        af.put("gbuser", user);
        GBModule mod = GBModuleBuildFactory.create(GBModule.ABQ, null);
        if (mod == null) {
            throw new UsageException(
                    "Could not find module for this object type : " + url);
        }
        mod.exec(af);
    }

    /**
     * Conver the properties object to a Map. utility function
     *
     * @param paf
     * @return
     */
    private static Map<String, Object> convert(Properties paf) {
        Map<String, Object> v = new HashMap<String, Object>();
        Set k = paf.keySet();
        for (Object s : k) {
            String ss = s.toString();
            String value = paf.getProperty(ss);
            v.put(ss, value);
        }
        return v;
    }

    /**
     * Conver the properties object to a Map. utility function
     *
     * @param paf
     * @return
     */
    private static LinkedHashMap<String, String> convert_(Properties paf) {
        LinkedHashMap<String, String> v = new LinkedHashMap<String, String>();
        Set k = paf.keySet();
        for (Object s : k) {
            String ss = s.toString();
            String value = paf.getProperty(ss);
            v.put(ss, value);
        }
        return v;
    }
//import --user=jeff --overlap=25 ./fasta/human-grch38-chr1.fa /human/chr1
    public static void loadFASTA(String annotation, String local, String gb_file, Integer sequence_overlap) {
        System.out.println(" loading a fasta file:  " + local);
        File local_file = new File(local);
        if (local.endsWith(".gz")) {
            File local_file_compressed = new File(local);
            FTPImporter.gunzipIt(local_file_compressed);
            String nfilename = local.substring(0, local.length() - 3);
            local_file = new File(nfilename);
        }
        Map<String, String> schema = new LinkedHashMap<String, String>();
        schema.put("seq_index", "string_ci");
        schema.put("annotation", "string_ci");
        schema.put("header", "string_ci");
        schema.put("start", "sint");
        schema.put("end", "sint");
        schema.put("sequence", "string_ci");
        schema.put("gene", "string_ci");
        schema.put("chromosome", "string_ci");
        try {
            // first thing we need to do is determine the size of the file by characters

            FileInputStream inputStream = new FileInputStream(local_file);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            ABTable table = new ABTable(gb_file);
            if (!table.exists()) {
                table.create(schema);
            }
            String currentid = "";
            String current_sequence = new String("");
            int currentLine = 0;
            //>ENST00000415118 havana_ig_:qgene:known chromosome:GRCh38:14:22438547:22438554:1 gene:ENSG00000223997 gene_biotype:TR_D_gene transcript_biotype:TR_D_gene
            String _currentid = null;
            Integer start = 0;
            Integer end = 0;
            String header = "";
            String[] _header = null;

            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith(">")) {
                    _header = line.split("\\s");
                    header = line.trim();
                }
            }
            current_sequence = FASTALoader.loadSequence(local_file);
            end = current_sequence.length();
            if (current_sequence != null && current_sequence.length() > 0) {
                FASTATableAppend fa = new FASTATableAppend();
                _currentid = currentid.replace(':', '_');
                System.out.println("" + currentLine + "\t " + currentid);
                fa.append(table, _currentid, header, annotation, current_sequence, start, end, sequence_overlap);
            }


        } catch (Exception _e) {
            _e.printStackTrace();
        }


    }

    public static void main(String[] args) throws IOException {
        // replace this with a known encoding if possible
        Charset encoding = Charset.defaultCharset();
        String filename = "C:\\Users\\jmilton\\dev\\ab\\arraybase\\hs_ref_GRCh38.p2_chr1.fa";
        File file = new File(filename);
        go2(file);
    }


    private static void go2(File local_file) throws IOException {

        FileInputStream inputStream = new FileInputStream(local_file);
        Scanner sc = new Scanner(inputStream, "UTF-8");
        if (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith(">")) {
            }
        }
        int character_count = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            character_count += line.length();
        }

        Charset encoding = Charset.defaultCharset();
        Reader reader = new InputStreamReader(new FileInputStream(local_file), encoding);
        char[] cs = new char[character_count];
        int r = -1;
        int i = 0;
        boolean start_count = false;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            if (start_count && ch != '\n')
                cs[i++] = ch;
            else if (ch == '\n') {
                start_count = true;
            }
        }
        String sequence = new String(cs);
        System.out.println("string length : " + sequence.length());
        String firstset = sequence.substring(0, 10);
        String lastset = sequence.substring(sequence.length() - 10);
        System.out.println(" first : " + firstset);
        System.out.println(" last : " + lastset);
        System.out.println(" ------ ");


    }

    private static void handleFile(File file, Charset encoding)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             Reader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer);
        }
    }

    private static void handleCharacters(Reader reader)
            throws IOException {
        int r;

        char[] cth = new char[239324742];

        int i = 0;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            cth[i++] = ch;
        }
        System.out.println(" done reading all the characters. ");
        String chrom = new String(cth);
        System.out.println(" we have a string now that is length : " + chrom);


    }


    private static void writeFastaForDebugPurposese(String chrom, String s, String current_sequence) {

        try {
            PrintStream pr = new PrintStream(new File("output.fa"));

            pr.println("> " + chrom + " " + s);
            pr.println(current_sequence);
            pr.flush();
            pr.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static Integer[] getStartAndStop(String chrom) {
//        chromosome:GRCh38:14:22449113:22449125:1
        String[] st = chrom.split(":");
        try {
            Integer start_site = Integer.parseInt(st[st.length - 3]);
            Integer end_site = Integer.parseInt(st[st.length - 2]);
            return new Integer[]{start_site, end_site};
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
        return null;

    }


    public static void createAndLoadTable(String u, String local_file2,
                                          String _delim, String gb_file, Map<String, String> table_structure,
                                          SourceType source_type) {
        ArrayList<Integer> ignore = new ArrayList<Integer>();
        createAndLoadTable(u, local_file2, _delim, gb_file, table_structure,
                ignore, source_type);
    }

    /**
     * Append the file to the table
     */
    public static void append(String gb_file, String local_file,
                              Map<GColumn, Integer> cols, String delim) {

        BufferedReader bread = null;
        FileReader fileReader = null;
        try {
            int post_increment = 10000; // post every 1000 documents
            ArrayList<LinkedHashMap<String, String>> dlist = new ArrayList<LinkedHashMap<String, String>>();
            int line_index = 0;
            fileReader = new FileReader(local_file);
            bread = new BufferedReader(fileReader);
            String line = bread.readLine();
            int increment_line = 0;
            int total_loaded = 0;
            int failed_lines = 0;
            while (line != null) {
                if (line.endsWith(delim)) {
                    line += "_";
                }
                String[] c = line.split(delim);
                if (c.length == cols.size()) {
                    Set<GColumn> gcol = cols.keySet();
                    LinkedHashMap<String, String> inputdata = new LinkedHashMap<String, String>();
                    String tout = "";
                    for (GColumn col : gcol) {
                        int i = cols.get(col);
                        try {
                            String value = Parse
                                    .tryToParse(c[i], col.getType());
                            tout += "\t\t[" + col.getType() + "]" + value;
                            inputdata.put(col.getName(), value);
                        } catch (LoaderException e) {
                            e.printStackTrace();
                        } catch (GBParseException e) {
                            e.printStackTrace();
                        }
                    }
                    GB.print("+\t" + tout);
                    dlist.add(inputdata);
                    // {{ post the increment if we're there }}
                    if (increment_line >= post_increment) {
                        appendTable(gb_file, dlist);
                        dlist = new ArrayList<LinkedHashMap<String, String>>();
                        total_loaded += increment_line;
                        increment_line = 0;
                    }
                } else {
                    GB.print("-\t [" + line_index + "] not loaded.\n");
                    GB.print("" + line);
                    for (String s : c)
                        GB.print("\t" + s);
                    failed_lines++;
                }
                line = bread.readLine();
                line_index++;
                increment_line++;
            }
            appendTable(gb_file, dlist);
            total_loaded += increment_line;

            GB.print("Lines failed [" + failed_lines + "]");
            GB.print("Loaded [" + total_loaded + "]");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(bread);
            IOUTILs.closeResource(fileReader);
        }
    }

    /**
     * Append the file to the table
     *
     * @param gb_file
     * @param left_schema
     */
    public static void insert(String local_file, String delim, String gb_file,
                              ArrayList<GColumn> left_schema, GBTableAttach gb) {

        BufferedReader bread = null;
        FileReader fileReader = null;
        try {
            // insert the data from local_file into these columns
            ArrayList<GColumn> cols = gb.getColumns();
            ArrayList<WhereClause> where_clauses = gb.getWhereClauses();
            // using the above where clause.
            int line_index = 0;
            fileReader = new FileReader(local_file);
            bread = new BufferedReader(fileReader);
            String line = bread.readLine();
            int increment_line = 0;
            int total_loaded = 0;
            int failed_lines = 0;
            int currentLine = 0;
            int start = gb.getDataStartLine();
            // get to the starting point
            while (line != null && start > currentLine) {
                line = bread.readLine();
                currentLine++;
            }

            int post_increment = 1000; // post every 1000 documents
            ArrayList<LinkedHashMap<String, String>> dlist = new ArrayList<LinkedHashMap<String, String>>();

            while (line != null) {
                if (line.endsWith(delim)) {
                    line += "_";
                }
                String[] c = line.split(delim);
                if (c.length == cols.size()) {
                    LinkedHashMap<String, String> inputdata = new LinkedHashMap<String, String>();
                    String tout = "";
                    int col_index = 0;
                    for (GColumn col : cols) {
                        try {
                            String value = Parse.tryToParse(c[col_index++],
                                    col.getType());
                            tout += "\t\t[" + col.getType() + "]" + value;
                            inputdata.put(col.getName(), value);
                        } catch (LoaderException e) {
                            e.printStackTrace();
                        } catch (GBParseException e) {
                            e.printStackTrace();
                        }
                    }
                    GB.print("+\t" + tout);
                    dlist.add(inputdata);
                    // {{ post the increment if we're there }}
                    if (increment_line >= post_increment) {
                        insertTable(gb_file, cols, dlist, where_clauses);
                        dlist = new ArrayList<LinkedHashMap<String, String>>();
                        total_loaded += increment_line;
                        increment_line = 0;
                    }
                } else {
                    GB.print("-\t [" + line_index + "] not loaded.\n");
                    GB.print("" + line);
                    for (String s : c)
                        GB.print("\t" + s);
                    failed_lines++;
                }
                line = bread.readLine();
                line_index++;
                increment_line++;
            }
            insertTable(gb_file, cols, dlist, where_clauses);
            total_loaded += increment_line;

            GB.print("Lines failed [" + failed_lines + "]");
            GB.print("Loaded [" + total_loaded + "]");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(bread);
            IOUTILs.closeResource(fileReader);
        }
    }

    /**
     * @param path
     * @param dlist         --left side documents ( i.e. flatfile documents )
     * @param where_clauses
     */
    private static void insertTable(String path, ArrayList<GColumn> desc,
                                    ArrayList<LinkedHashMap<String, String>> dlist,
                                    ArrayList<WhereClause> where_clauses) {
        String core = TMSolrServer.getCore(path);
        try {
            TMSolrServer.insert(core, desc, dlist, where_clauses);
        } catch (LoaderException e) {
            e.printStackTrace();
        }
    }

    private static void appendTable(String path,
                                    ArrayList<LinkedHashMap<String, String>> dlist) {
        String core = TMSolrServer.getCore(path);
        try {
            TMSolrServer.append(core, dlist);
        } catch (LoaderException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load an xls file into GB with the given path
     * NOTE: If the XLSX workbook has multiple sheets then the ab_file will be a directory with each sheet
     * as tables in that directory
     *
     * @param u
     * @throws IOException
     */
    public static void loadXLSX(String u, String local_file, String ab_file) throws IOException {

        File f = new File(local_file);
        GB.print("Reading file : " + f.getAbsolutePath());
        if (!f.exists()) {
            new FileNotFoundException("Failed to find the file : " + f.getAbsolutePath());
        }
        FileInputStream fi = new FileInputStream(f);
        XSSFWorkbook wb = new XSSFWorkbook(fi);
        int count_sheets = wb.getNumberOfSheets();
        GB.print(" Loading  " + count_sheets + " sheets.");
        if (count_sheets > 1) {
            try {
                XLS.loadMultipleSheets(u, wb, ab_file);
            } catch (FieldTitleNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            XLS.loadSingleSheet(u, wb, ab_file);
        }
    }
    public static synchronized void loadLibrary(java.io.File jar) throws Exception {
        try {
            /*We are using reflection here to circumvent encapsulation; addURL is not public*/
            java.net.URLClassLoader loader = (java.net.URLClassLoader)ClassLoader.getSystemClassLoader();
            java.net.URL url = jar.toURI().toURL();
            /*Disallow if already loaded*/
            for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())){
                if (it.equals(url)){
                    return;
                }
            }
            java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{java.net.URL.class});
            method.setAccessible(true); /*promote the method to public access*/
            method.invoke(loader, new Object[]{url});
        } catch (final java.lang.NoSuchMethodException |
                java.lang.IllegalAccessException |
                java.net.MalformedURLException |
                java.lang.reflect.InvocationTargetException e){
            throw new Exception(e);
        }
    }
    public static void loadJARIndexer(String u, String local_file, String final_param) {
        System.out.println (" jar file " + local_file );
        File myJar = new File ( local_file );
        try {
            loadLibrary(myJar);
            Class classToLoad = Class.forName(final_param);
            Object instance = classToLoad.newInstance();
            if ( instance instanceof ABIndexer ) {
                ABIndexer abi = (ABIndexer) instance;
                System.out.println( " abi " + abi.getCommitIncrement() );
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
