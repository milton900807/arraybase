package com.arraybase;

/**
 * This is the main entry point for the application.
 * author: Milton
 */

import com.arraybase.admin.Manager;
import com.arraybase.db.*;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.LoaderException;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.parse.TypeNotFoundException;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.io.GBFileCrawler;
import com.arraybase.io.GBFileManager;
import com.arraybase.lac.LAC;
import com.arraybase.modules.GBInstaller;
import com.arraybase.modules.UsageException;
import com.arraybase.schedule.GBScheduler;
import com.arraybase.schedule.GBSchedulerNotReady;
import com.arraybase.search.ABaseResults;
import com.arraybase.search.GBResponse;
import com.arraybase.shell.*;
import com.arraybase.shell.cmds.SetSchemaCommand;
import com.arraybase.shell.interactive.GBCommandCenterListener;
import com.arraybase.shell.interactive.StandardRunner;
import com.arraybase.shell.iterminal.c.ConsoleReader;
import com.arraybase.shell.iterminal.c.history.History;
import com.arraybase.tm.*;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class GB {

    private static Stack<GBCommand> commandStack = new Stack<GBCommand>();
    private static ConsoleReader console = null;

    static {
        GBCommand commandCenter = new GBCommand();
        commandStack.push(commandCenter);
    }

    private static GBScheduler scheduler = null;
    public static boolean INTERACTIVE = false;
    static String user_home = System.getProperty("user.home");
    File f = new File(user_home, ".ab.config");
    public static String VERSION = Configuration.OS;
    static GBLogger log = GBLogger.getLogger(GB.class);
    public static String RM_USAGE = "rm $userid $file_path";
    public final static String IMPORT = "import";
    public final static String MAKE_DIRECTORY = "mkdir";
    public final static String RM_DIRECTORY = "rmdir";
    public static final String CREATE = "create";
    public static final String RM = "rm";
    public static final String MK_REF = "mkref";
    public static final String MK_TABLE = "mktable";
    public static final String SET_LINK = "setlink";
    public static final String RENAME = "rename";
    public static final String LINK_NODE = "linknode";
    private static final String PRINT_PROPERTIES = "properties";
    private static final String CRAWL_FILES = "crawl";
    private static final String TAG = "tag";
    private static final String DESC = "desc";
    private static final String MAX = "max";
    private static final String CUT_LINE = "cutline";
    private static final String COUNT = "count";
    private static final String SELECT = "select";
    static final String INSERT = "insert";
    public static final String JOIN = "join";
    public final static String BLOB_UP_USAGE = "$userid $localfile $gridbasepath";
    private static final String MAKE_DIRECTORY_USAGE = "You must provide a user id and a path you want to create:  $userid $path";
    private static final Object MAKE_LINK = "mklink";
    private static final Object LS_LINK = "lslink";
    private static final Object LINK_CORE = "linkcore";
    public static final String RM_DIRECTORY_USAGE = "rmdir $userid $fully_qualififed_path";
    private static final String LINK_CORE_USAGE = "You must provide the following args:  "
            + "$corename $path.  For example if you want to link to a core named 'mycore' you could do the following: "
            + "  linkcore mycore /gne/research/mydirectory/mycore";
    static final String MK_REF_USAGE = "Connect a node to another via a reference.:  mkref $username $fromdir $todir1 ... $todir{n}  ";
    private static final String MK_TABLE_USAGE = "Make a table for the xls file:  mktable $username $local_xls_file $directory_to_store_the_table";
    static final String RENAME_USAGE = "Rename a node or file etc. : rename $userid $fully_qualified_path_to_node $new_name_of_node";
    private static final String LINK_NODE_USAGE = "Link a node object to a path (This is an admin-only feature):  linknode $username $nodeid $pathname";
    public static final String INSTALL = "install";
    private static final String TEST = "test";
    private static final String EXPORT_FILE = "export";
    private static final String EXPORT_ALL = "exportall";
    private static final String EXPORT_PATH = "exportpath";
    static final String SEARCH = "search";
    private static final String SEARCH_TABLE = "searchtable";
    private static final String LIST_FIELDS = "listfields";
    private static final String FIELD_TYPE = "lstypes";
    public static final String BUILD = "build";
    private static final String PRINT_NODE_DETAILS = "details";
    static final String SET = "set";
    private static final String MK_NODE = "mknode";
    private static final String TYPES = "TYPES";
    private static final String ADD = "ADD";
    private static final String START = "start";
    public static final String DEFAULT_UER = "ghirardelli";
    private static final String LOAD_ARRAY = "array";
    private static final String PRINT = "print";
    public static final String IMPORT_APPEND = "append";
    public static final String IMPORT_ATTACH = "attach";

    private static UserPath path = new UserPath();
    private static HostFileSystem lpath = new HostFileSystem();
    private static GBVariables variables = new GBVariables();


    public static GBScheduler start() {
        if (scheduler == null) {
            scheduler = GBScheduler.createScheduler();
        }
        scheduler.start();
        return scheduler;
    }

    public static void addToScheduler(String command, String arraybase, String command1, String time) {
        if (scheduler == null) {
            scheduler = GBScheduler.createScheduler();
        }
        try {
            scheduler.add(command.trim(), arraybase, command1, time);
        } catch (GBSchedulerNotReady gbSchedulerNotReady) {
            gbSchedulerNotReady.printStackTrace();
        }
    }


    public static void test() {
        // gogb ( "blobup", "donaldm", "pom.xml", "/gne/research/test2.3");
        // gogb("mkdir", "test_user", "/test/command_mkdir");
        // gogb("mkdir", "test_user", "/test/another_mkdir");
        // gogb("insert", "db.abq", "/isis/test/loade1");
        // double drandom = Math.random();
        // String random = "test" + ((float) drandom) + "t";
        // gogb("mkdir", "test_user2", "/test/tables/" + random);
        // gogb("ls", "/test/tables");
        // gogb("totable", "test_user2", "src/test/resources/testdata_tsv.tsv",
        // "/test/tables/tvs_test");
        // gogb("ls", "/test/tables");
        // gogb("ls", "/test/tables/tsv_test");
        // gogb("ls", "/test/tables/tsv*");
        // java -jar gb.jar search /test/tables/tvs_test *:*
        // gogb("search", "/test/tables/tvs_test", "*:*");
        // gogb("mktable", "jeff",
        // "src/test/resources/loader_test_osi2298g.xls",
        // "/test/tables");
        // gogb("mktable", "jeff", "src/test/resources/date_check.xls",
        // "/test/tables");
        // gogb("type", "/test/tables/date_check.xls",
        // "STUDY_DATE->float");

        // gogb("search", "/test/tables/date_check.xls", "*:*");
        // gogb("search", "/gne/test/cgp_master_list_1.xls", "*:*");

        // this is the test for the local database.(i.e my lappy)

        //
        // gogb("ls", "/gne/research/test");
        // gogb("mklink", "donaldm", "/gne/research/test/mylink",
        // "mytable.search(*:*)");

        // gogb("build", "searchindex");
        // gogb("install", "gb");

        // gogb("build", "searchindex", "/Users/donaldm/Desktop/ppt");

        // * Make a table from an input file gb mktable donaldm fields=1 types=2
        // * ignore=3 46239h2010.txt
        // gogb("totable", "fields=0", "types=1", "ignore=2,4",
        // "/Users/donaldm/Downloads/46239h2010.txt", "/gne/totable/t4");

        // java -jar gb.jar insert ncbi.abq into /isis/test/rpkm1 where
        // GENE_ID=NCBI_GENE_ID
        // gogb("insert", "src/test/resources/ncbi.abq", "into",
        // "/isis/test/rpkm1", "where",
        // "GENE_ID=NCBI_GENE_ID");

        // insert a text file into a table
        // try {
        // gogb("insert", "types", "src/test/resources/hgnc_downloads.txt");
        // } catch (UsageException e) {
        // e.printStackTrace();
        // }

        // insert an abq file into a table object
        try {
            gogb("import", "-user=test_user",
//                    "src/test/resources/ucsc_gwasCatalog.abq",
                    "./scripts/screening_experiments.abq",
                    "/test/import/totable");
        } catch (UsageException e) {
            e.printStackTrace();
        }
    }

//    public static void startScheduler() {
//      scheduler = GBScheduler.createScheduler();
//    }


    // need to add the xls bug to the list jira..
    public static void main(String[] _margs) {
        String[] args = _margs;
        // {{PREPEND THE ARGS WITH A CONFIG FILE IF NECESSARY }}
        if (_margs != null && _margs.length > 0) {
            String test_for_ab_config = _margs[0];
            if (test_for_ab_config.endsWith(".config")) {
                try {
                    new ABProperties(test_for_ab_config);
                } catch (GBConfigurationException e) {
                    e.printStackTrace();
                    GB.print(" Config file is not valid : "
                            + test_for_ab_config);
                    return;
                }
                args = new String[_margs.length - 1];
                if (_margs.length > 1) {
                    for (int i = 1; i < _margs.length; i++) {
                        args[i] = _margs[i];
                    }
                } else
                    args = null;
            } else
                new ABProperties();

        } else
            new ABProperties();
        // {{ - - - - - - - - - - - - - - - - - - - }}

        if (args == null || args.length <= 0) {
            startInteractivePrompt();
            // return;
        } else {
            // {{ FIRST CHECK TO SEE IF THERE IS A CONFIGURATION FILE }}

            // {{ IS THIS USING A WORKFLOW FILE }}
            if (args.length == 1) {
                String v = args[0];
                if (v.endsWith(".abw")) {
                    WABRun wab;
                    try {
                        wab = new WABRun(v);
                        wab.run();
                    } catch (IOException e) {
                        log.error(e);
                        log.fatal("Failed to load the workflow file: " + v);
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String command = "";
                        for (String arg : args) {
                            command += arg + ' ';
                        }
                        GBCommand gbCommand = new GBCommand();
                        gbCommand.exec(command.trim(), null);
                        exit(0);
                    } catch (UsageException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // {{ OTHERWISE THESE ARE COMMAND-LINE ARGUMENTS }}
                try {
                    String command = "";
                    for (String arg : args) {
                        command += arg + ' ';
                    }
                    GBCommand gbCommand = new GBCommand();
                    gbCommand.exec(command, null);
                } catch (UsageException e) {
                    e.printStackTrace();
                }
            }
        }
        // exit ( 1 );
    }

    static CommandHistory ch = new CommandHistory();

    private static void startInteractivePrompt() {
        new GB();
        // interactive prompt.. we keep track of commands that are called.
        ch.init();
        lcd();
//        GB.CONSOLE = true;
        System.out.println("[+]ArrayBase[+]");
        StandardRunner run = new StandardRunner(new GBCommandCenterListener() {
            public void updateCommandCenter(GBCommand _center) {
                commandStack.push(_center);
            }
        });
        INTERACTIVE = true;
        Thread t = new Thread(run);
        t.start();
    }

    public static String[] parseLine(String line) {
        String[] args = line.split("\\s+");
        return args;
    }

    public static String[] lsSearchableNodes(String _root) {
        DBConnectionManager dbcm = GB.getConnectionManager();
        NodeManager tm = new NodeManager(dbcm);
        TNode node = tm.getNode(_root);

        ArrayList<String> paths = new ArrayList<String>();
        if (!GBSearch.isSearchable(node) && GBNodes.hasChildren(node)) {
            paths = getSearchablePaths(tm, paths, _root);
        }

        dbcm.close();
        String[] ar = paths.toArray(new String[paths.size()]);
        return ar;
    }

    private static ArrayList<String> getSearchablePaths(NodeManager tm,
                                                        ArrayList<String> paths, String root) {
        List<TNode> c = tm.getRefNodes(root);
        for (TNode n : c) {
            if (GBSearch.isSearchable(n)) {
                paths.add(root + "/" + n.getName());
            } else {
                if (GBNodes.hasChildren(n))
                    getSearchablePaths(tm, paths, root + "/" + n.getName());
            }
        }
        return paths;
    }

    /**
     * set the version dynamically
     *
     * @param _db
     */
    public static void setRelease(String _db) {
        GB.VERSION = _db;
    }

    public static HostFileSystem getLocalPath() {
        return lpath;
    }

    /**
     * Return a reference to an object that manages the files in GB. These are
     * the raw files..
     *
     * @return
     */
    public static GBFileManager getGBFileManager() {
        return new GBFileManager();
    }

    /**
     * get a list of values from a specific field in a table with search and
     * sort options. This is equivalent to select fieldname from table where
     * something=something_else order by fieldname desc|asc
     *
     * @param path
     * @param col
     * @param start
     * @return
     */
    public static ArrayList select(String path, String col, String search,
                                   int start, int length, String sortstring) {
        String[] fields = new String[1];
        fields[0] = col;
        try {
            ABaseResults results = GBSearch.select(path, fields, search, start,
                    length, sortstring);
            ArrayList<GRow> res = results.getValues();
            ArrayList ls = new ArrayList();
            for (GRow r : res) {
                HashMap data = r.getData();
                Object value = data.get(col);
                ls.add(value);
            }
            return ls;
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ***********************************************************************
     * <p>
     * This is the main entry point.
     *
     * @throws UsageException ************************************************************************
     */

    public static void gogb(String... _args) throws UsageException {
        _args = preCompute(_args);
        // if we have && values then we need to start the
        boolean method_triggers = false;
        for (String t : _args) {
            if (t.startsWith("&&")) {
                method_triggers = true;
            }
        }
        // method chaining is strictly sequential whereas method triggers are based on varaible conditionals
        if (method_triggers) {
            ArrayList<String> ng = new ArrayList<String>();
            for (String t : _args) {
                ng.add(t);
            }
            MethodTrigEngine mtm = new MethodTrigEngine(ng);
            mtm.start();
        } else {
            String command = _args[0];
            if (command.equals(GB.TEST)) {
                test();
            } else if (command.indexOf("\b[A-Z0-9._%+-]+=") > 0) {
                GBVariables.parse(_args);
            } else if (command.equalsIgnoreCase(GB.CREATE)) {
                GBTables.create(_args, null);
            } else if (command.equalsIgnoreCase(GB.START)) {
                startServices(_args);
            } else if (command.equals(GB.IMPORT)) {
                if (_args == null || _args.length <= 0) {
                    throw new LoadUsageException();
                }
                GBNodes.load(_args);
            } else if (command.equals(GB.IMPORT_APPEND)) {
                // gb append localfile.txt /arraybase/file
                try {
                    GBNodes.append(_args);
                } catch (ConnectException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (command.equals(GB.IMPORT_ATTACH)) {
                // gb attach localfile.txt /arraybase/file where row_num=row_num
                try {
                    GBNodes.attach(_args);
                } catch (ConnectException e) {
                    e.printStackTrace();
                }

            } else if (command.equals(CRAWL_FILES)) {
                GBFileCrawler.go(_args);
            } else if (command.equals(COUNT)) {
                GBCounterManager.count(_args);
            } else if (command.equals(SELECT)) {
                GBSearch.select(_args, System.out);
            } else if (command.equals(GB.MAKE_DIRECTORY)) {
                GBNodes.mkDir(_args);
            } else if (command.equals(GB.RM_DIRECTORY)) {
                GBNodes.removeDir(_args);
            } else if (command.equals(GB.RM)) {
                GBNodes.rm(_args);
            } else if (command.equals(SET_LINK)) {
                GBLinkManager.setLink(_args);
            } else if (command.equals(MK_NODE)) {
                GBNodes.mkNode(_args);
            } else if (command.equals(GB.PRINT_NODE_DETAILS)) {
                GBNodes.printNodeDetails(_args);
            } else if (command.equals(GB.TAG)) { // deprecated
                GBNodes.tag(_args);
            } else if (command.equals(GB.LS_LINK)) {
                String path = _args[1];
                print(getLink(path));
            } else if (command.equals(GB.LINK_CORE)) {
                GBLinkManager.linkCore(_args);
            } else if (command.equals(GB.MK_REF)) {
                GBLinkManager.mkReference(_args);
            } else if (command.equals(GB.RENAME)) {
                GBNodes.rename(_args);
            } else if (command.equals(GB.LINK_NODE)) {
                GBLinkManager.linkNode(_args);
            } else if (command.equals(GB.MK_TABLE)) {
                GBTableLoader.mkTableFromXLS(_args);
            } else if (command.equals(GB.SET)) {
                GBPropertyManager.set(_args);
            } else if (command.equals(GB.JOIN)) {
                GBJoinMnager.join(_args);
            } else if (command.equals(GB.ADD)) {
                GBPropertyManager.add(_args);
            } else if (command.equalsIgnoreCase(GB.CUT_LINE)) {
                GBFileManager.cutLine(_args);
            } else if (command.equals(GB.INSERT)) {
                GBInsertManager.insert(_args);
            } else if (command.equals(GB.PRINT_PROPERTIES)) {
                GB.printProperties();
            } else if (command.equals(GB.INSTALL)) {
                GBInstaller.run(_args);
            } else if (command.equalsIgnoreCase(GB.BUILD)) {
                GBInstaller.build(_args);
            } else if (command.equals(GB.DESC)) {
                GBNodes.describe(_args);
            } else if (command.equals(GB.EXPORT_FILE)) {
                GBFileManager.export(_args);
            } else if (command.equals(GB.EXPORT_ALL)) { // TODO: this is not
                exportAll();
            } else if (command.equals(GB.EXPORT_PATH)) { // TODO: this is not
                String path = _args[1];
                exportAll(path);
            } else if (command.equals(GB.SEARCH)) {
                GBSearch.search(_args, System.out, new SearchConfig(SearchConfig.RAW_SEARCH));
            } else if (command.equals(GB.FIELD_TYPE)) {
                GBTables.changeFieldType(_args);
            } else if (command.equals(GB.LIST_FIELDS)) {
                GBTables.listFields(_args);
            } else if (command.equals(SEARCH_TABLE)) {
                GBSearch.searchTable(_args, new SearchConfig(SearchConfig.RAW_SEARCH));
            } else if (command.equals(LOAD_ARRAY)) {
                GBSearch.searchTable(_args, new SearchConfig(SearchConfig.RAW_SEARCH));
            } else if (command.equals(PRINT)) {
                variables.printVariable(_args);
            } else if (command.equals(MAX)) {
                // TODO: This is not implemented
                String path = "";
                String search = "";
                int increment = -1;
                String field = "";
                try {
                    GB.max(path, field, search, 0, increment, "desc " + field);
                } catch (TypeNotFoundException e) {
                    e.printStackTrace();
                } catch (NodeNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (command.matches(GBRGX.CMD)) {
                // FIRST LETS CHECK THE COMMAND STRUCTURE }}
                {
                    System.err.println(" er ");
                }
            } else if (command.matches(GBRGX.CD)) {
                {
                    if (_args.length != 2) {
                        printUsage("usage:  cd $path_to_directory");
                        return;
                    } else {
                        String _dir = _args[1];
                        GB.cd(_dir);
                    }
                }
            } else if (command.matches(GBRGX.MKDIR)) {
                GB.mkdir(command);
            } else if (command.matches(GBRGX.PWD)) {
                GB.print(GB.pwd());
            } else if (command.matches(GBRGX.LLS)) {
                lls();
            } else if (command.matches(GBRGX.LCD)) {

                if (_args.length == 2) {
                    String localPath = _args[1];
                    localPath = localPath.trim();
                    lcd(localPath);

                }
            }
        }
//        }else
//        {
//            GBCommand command_center = new GBCommand();
//            command_center.exec(command, null);
//        }
    }

    public static double max(String path, String field, String search, int i,
                             int increment, String sort) throws TypeNotFoundException,
            NodeNotFoundException {

        double max = Double.MIN_VALUE;
        String[] fields = new String[1];
        fields[0] = field;
        ABaseResults results = GBSearch.select(path, fields, search, i,
                increment, sort);
        int total = results.getTotalHits();
        ArrayList<GRow> rows = results.getValues();
        GRow first = rows.get(0);
        HashMap data = first.getData();
        Object value = data.get(field);
        if (!(value instanceof Number)) {
            throw new TypeNotFoundException(field);
        } else {
            double v = ((Number) value).doubleValue();
            max = v;
        }

        // GET THE FIRST BATCH
        for (GRow r : rows) {
            data = r.getData();
            Number n = (Number) data.get(field);
            double v = n.doubleValue();
            if (v > max)
                max = v;
        }
        i = increment;
        // GET THE REST
        int index = rows.size();
        while (index < total) {
            results = GBSearch.select(path, fields, search, i, increment, sort);
            for (GRow r : rows) {
                data = r.getData();
                Number n = (Number) data.get(field);
                double v = n.doubleValue();
                if (v > max)
                    max = v;
            }
            i = increment;
        }
        return max;
    }

    /**
     * This will verify and start the default services. 1. Assumes mysql
     * installed on linux 2. Assumes Solr 3.x
     */
    private static void startServices(String[] _args) {
        Connection con = null;
        try {
            DBConnectionManager connection = new DBConnectionManager();
            con = connection.getJDBCConnection();
            if (con != null) {
                print("Database Connection is available and working. ");
            } else {
                GBRelationalDB.startMasterInstance();
            }
        } catch (Exception _e) {
            GBRelationalDB.startMasterInstance();
        } finally {
            JDBC.closeConnection(con);
        }
    }

    /**
     * Precomput the commands... for global settings 1. Record 2. Database
     * pointer.
     *
     * @param _args
     * @return
     */
    // {{ YOU CAN EXPCLICITYLY SET THE VERSION }}
    // gb -db=PRODUCTION import file path --> will load the file forcing the
    // production database.
    private static String[] preCompute(String[] _args) {
        ArrayList<String> commands = new ArrayList<String>();
        int mainIndex = 0;
        for (String _s : _args) {
            if (_s.startsWith("-db=")) {
                setDBVersion(_args);
            } else if (_s.startsWith("-r=")) {
                setRecorder(_s, _args);
            }
            if (!_s.startsWith("-"))
                break;
            mainIndex++;
        }
        for (int i = mainIndex; i < _args.length; i++) {
            commands.add(_args[i]);
        }
        String s = commands.get(0);
        if (s.startsWith("-")) {
            System.out.println(" command parse error " + s);
            exit(0);
        }
        String[] st = new String[commands.size()];
        st = commands.toArray(st);
        return st;
    }

    public static void exit(int i) {
        System.exit(1);
    }

    public static void record(String command) throws RecorderNotFound {

    }

    private static void setRecorder(String _s, String[] _args) {

        String command = "";
        for (String c : _args) {
            if (!c.equals(_s))
                command = command + " " + c;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        // if the next is a switch option then we will use it:
        for (int i = 0; i < _args.length; i++) {
            String ss = _args[i];
            if (ss.startsWith("-")) {
                String[] _split = ss.split("=");
                log.debug("1. " + _split[0] + " 2. " + _split[1]);
                map.put(_split[0], _split[1]);
            }
        }

        String[] s = _s.split("=");
        String fileName = s[1];
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(fileName, true)));
            String comment = map.get("-comment");
            out.println("#" + comment);
            out.println(command);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will return an adjusted param removing the -db option
     *
     * @param _args
     * @return
     */
    private static String[] setDBVersion(String[] _args) {
        String value = _args[0];
        if (value.contains("=")) {
            int vi = value.indexOf('=');
            String vv = value.substring(vi + 1);
            vv = vv.trim();
            setRelease(vv);
        } else {
            log.error(" You specified a version but version syntax is incorrect. "
                    + " Please use the following syntax:\n -db=$dboption");
            exit(1);
        }
        String[] st = new String[_args.length - 1];
        for (int i = 1; i < _args.length; i++) {
            st[i - 1] = _args[i];
        }
        return st;
    }

    static ArrayList<String> parseArgs(String[] _args) {
        ArrayList<String> array = new ArrayList<String>();
        for (String s : _args) {
            array.add(s);
        }
        return array;
    }

    public static void printELog(NodeWrongTypeException e) {
        File f = new File("./lastcommand.trace");
        PrintStream pr;
        try {
            pr = new PrintStream(f);
            e.printStackTrace(pr);
            pr.flush();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    /**
     * Make a table from an input file gb mktable donaldm fields=1 types=2
     * ignore=3 46239h2010.txt
     *
     * @param user_name
     * @param local_file
     * @param path
     * @param vals
     * @return
     */
    private static GBLogger gbl = GBLogger.getLogger(GB.class);

    public static boolean kill_all_iterators = false;

    static {
        gbl.setLevel(GBLogger.DEBUG);
    }

    /**
     * @param _args
     * @return
     */
    static LinkedHashMap<String, String> parseVals(String[] _args) {
        LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
        for (String _a : _args) {
            if (_a.contains("=")) {
                String t = _a;
                String st[] = t.split("=");
                values.put(st[0], st[1]);

            } else
                values.put(_a, _a);
        }
        return values;
    }

    public static String[] getRoots() {
        GBNodes nodes = getNodes();
        return nodes.getRoots();
    }

    /**
     * Remove a file object. This will not remove an object that contains
     * anything. This is explicitly defined to prevent anyone from
     * unintentionally removing a root directory... and losing data.
     *
     * @param _userid
     * @param _path
     * @return
     */
    public static String removeFile(String _userid, String _path) {
        String[] files = GBNodes.listPath(_path);
        if (files == null) {
            return "Path not found.";
        } else {
            NodeManager manager = new NodeManager();
            TNode node = manager.getNode(_path);
            if (node != null) {
                if (node.getReference() != null
                        && node.getReference().size() > 0) {
                    System.err
                            .println(" this contains valid references to sub files.  These must be removed before you may remove this file.  You may use the rmdir.");
                } else {
                    return manager.removePath(_path);
                }
            }
        }
        return "There seems to be a problem here.  Please use rmdir to force remove this file.";

    }

    public static String select(String[] _args) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream pmr = new PrintStream(baos);
        GBSearch.select(_args, pmr);
        String t = baos.toString();
        return t;
    }

    @Command
    public static String roots() {
        String s = "";
        GBNodes nodes = getNodes();
        String[] a = nodes.getRoots();
        for (String r : a) {
            s += r + "\n";
        }
        return s;
    }

    public static ConsoleReader getConsole() {
        return console;
    }

    @Command
    public String search(String path, String sstring) {
        String tpath = getGBPath(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream pmr = new PrintStream(baos);
        String[] args = new String[3];
        args[0] = "search";
        args[1] = tpath;
        args[2] = sstring;
        GBSearch.search(args, pmr, new SearchConfig(SearchConfig.RAW_SEARCH));
        String t = baos.toString();
        return t;
    }

    @Command
    public String select_(String fields, String path, String sstring) {

        String[] f = fields.split(",");
        ArrayList<String> values = new ArrayList<String>();
        for (String s : f) {
            values.add(s.trim());
        }

        String tpath = getGBPath(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream pmr = new PrintStream(baos);
        String[] args = new String[3];
        args[0] = "search";
        args[1] = tpath;
        args[2] = sstring;
        GBSearch.search(args, pmr, new SearchConfig(SearchConfig.RAW_SEARCH));
        String t = baos.toString();
        return t;
    }

    private String getGBPath(String p) {
        if (p.startsWith("/"))
            return p;
        else {
            String temp = path.getPath() + "/" + p;
            return temp;
        }
    }

    public static String lpwd() {
        return lpath.getPath();
    }

    public static String lcd() {
        File f = new File(".");
        String path = f.getAbsolutePath();
        if (path.contains("./")) {
            path = path.replace("./", "/");
        }
        lpath.setPath(path);
        return path;
    }

    public static String lcd(String p) {

        if (p.startsWith("/")) {
            File f = new File(p);
            String abp = f.getAbsolutePath();
            lpath.setPath(abp);
        } else {

            String pp = "";
            if (p.length() > 3) {
                String t = p.substring(1, 3);
                if (t.equalsIgnoreCase(":/")) {
                    pp = p;
                } else {
                    pp = lpath.getPath() + "/" + p;
                }
            } else {
                pp = lpath.getPath() + "/" + p;
            }

            File f = new File(pp);
            System.out.println(" f: " + f.getAbsolutePath());
            if (!f.exists()) {
                return "Path not found on local file system. ";
            } else {


                try {
                    lpath.setPath(f.getCanonicalPath());
                } catch (IOException e) {
                    lpath.setPath(f.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
        String path = lpath.getPath();
        return path;
    }

    // r> upload ( /gne/research/whatever, mydataframe )
    // GB.createLink ( /gne/research/whatever, $corenmae )

    // path is the path in GB
    // link is corename.search(*:*)
    public static void createLink(String _path, String _link) {

    }

    public static ArrayList<String> searchPath_dep(String _path,
                                                   String _searchString) {
        NodeManager tm = new NodeManager();
        LinkedHashMap<String, Integer> value = new LinkedHashMap<String, Integer>();
        value = tm.search(value, _path, _searchString);
        return new ArrayList<String>();

    }

    /**
     * Get a reference to the avaiable tables from the given server.
     */
    public static GBTables getGBTables() {
        return new GBTables();
    }

    public static void printHelp() {
        System.out.println(" TODO COMPLETE THE HELP PAGE ");
        try {
            throw new Exception();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }

    private static void printProperties() {
        Set<Object> keys = ABProperties.getProperties().keySet();
        for (Object key : keys) {
            System.out.println(key);
            String value = ABProperties.get(key.toString());
            System.out.println("-------\t" + key + " = " + value);
        }
    }

    private static Manager getManager() {
        return new Manager();
    }

    public static String getLink(String path) {

        NodeManager nm = new NodeManager();
        TNode node = nm.getNode(path);
        if (node == null) {
            return "Path not valid";
        } else {
            return node.getLink();
        }
    }

    public static void print(String res) {
        // try {
        // printCharacters(res.toCharArray());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        System.out.println("|\t" + res);
    }

    final static Writer out = new OutputStreamWriter(System.out);
    private static final String STATS = "/ab/stats";

    private final static void printCharacters(final char[] c)
            throws IOException {
        int TAB_WIDTH = 4;
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\t') {
                len += TAB_WIDTH;
            } else {
                len++;
            }
        }

        char cbuf[];
        if (len == c.length) {
            cbuf = c;
        } else {
            cbuf = new char[len];
            int pos = 0;
            for (int i = 0; i < c.length; i++) {
                if (c[i] == '\t') {
                    Arrays.fill(cbuf, pos, pos + TAB_WIDTH, ' ');
                    pos += TAB_WIDTH;
                } else {
                    cbuf[pos] = c[i];
                    pos++;
                }
            }
        }
        out.write(cbuf);
    }

    static void prnt(String res) {
        System.out.println("-\t" + res);
    }

    static void print(String[] files) {
        GB.print("");
        if (files == null) {
            System.err.print(" Nothing to list ");
            return;
        }
        for (String f : files) {
            System.out.println("" + f);
        }
        System.out.println("");
    }

    public static void printUsage(String usage) {
        System.err.println(usage);
    }

    /**
     * Buidl the index for the document at the given solr url
     *
     * @param solr_url     : the complete solr url e.g. http://etc....
     * @param documentName
     * @param _input
     */
    public static void buildIndex(String solr_url, String documentName,
                                  InputStream _input) throws IndexBuildFailed {
        throw new IndexBuildFailed("NOT IMPEMENTED");
        // TODO: completet the implementation of this:
        // CommonsHttpSolrClient solr = new CommonsHttpSolrClient ( solr_url );
        //
        //
        // String authors = content.getAuthors();
        // String header = content.getFileHeaderInformation();
        // String content_type = content.getType();
        // String file_content = content.getContentAsString();
        // SolrInputDocument sd = new SolrInputDocument();
        // sd.addField(GBFileStructure.FILE_NAME.name, _name);
        // sd.addField(GBFileStructure.NODE_ID.name, _l);
        // sd.addField(GBFileStructure.HEADER.name, header);
        // sd.addField(GBFileStructure.CONTENT_TYPE.name, content_type);
        // sd.addField(GBFileStructure.ATTRIBUTES.name, header);
        // sd.addField(GBFileStructure.CONTENT.name, file_content);
        // sd.addField(GBFileStructure.AUTHORS.name, authors);
        // sd.addField(GBFileStructure.DATE_LAST_MODIFIED.name, new Date ());
        // sd.addField(GBFileStructure.DATE_CREATED.name, new Date ());
        // sd.addField("TMID",
        // (Math.random() * 100000 / (0.1 * Math.random())));
        // sd.addField("TMID_lastUpdated", new Date());
        // solr.add(sd);
        // solr.commit(true, true);

    }

    /**
     * A solr url that points directly to a solr core using the query.
     *
     * @param solr_url
     * @param _query
     * @return
     * @throws UpdateIndexFailed
     * @deprecated
     */
    public static String removeQuery(String solr_url, String _query)
            throws UpdateIndexFailed {
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(solr_url).build();
            org.apache.solr.client.solrj.response.UpdateResponse response = solr
                    .deleteByQuery(_query);
            solr.commit();
            return "" + response.getStatus();
        } catch (Exception _e) {
            _e.printStackTrace();
            throw new UpdateIndexFailed(_e.getLocalizedMessage());
        } finally {
            IOUTILs.closeResource(solr);
        }
    }

    public static void commit(String _solr_url) throws UpdateIndexFailed {
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(_solr_url).build();
            solr.commit();
        } catch (Exception _e) {
            _e.printStackTrace();
            throw new UpdateIndexFailed(_e.getLocalizedMessage());
        } finally {
            IOUTILs.closeResource(solr);
        }
    }

    public static GBEditor getEditor(String url) {
        GBEditor editor = new GBEditor(url);
        return editor;
    }

    /**
     * This will return -1 if it fails.
     *
     * @param url
     * @param _searchString
     * @return
     */
    public static Integer hitCount(String url, String _searchString) {
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(url).build();
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", "" + _searchString);
            params.set("wt", "xml");
            XMLResponseParser pars = new XMLResponseParser();
            solr.setParser(pars);
            QueryResponse response;
            response = solr.query(params);
            System.out.println(_searchString + "  response = "
                    + response.getResults().size());

            SolrDocumentList list = response.getResults();
            NamedList tresponse = response.getResponse();
            SolrDocumentList response_object = (SolrDocumentList) tresponse
                    .get("response");
            long numfound = response_object.getNumFound();
            return (new Integer((int) numfound));
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException _io) {

            _io.printStackTrace();
        } finally {
            IOUTILs.closeResource(solr);
        }
        return (-1);
    }

    public static String getURL(String _schema) {
        String solr_url = ABProperties.get(ABProperties.SOLRSITE);
        if (!solr_url.endsWith("/")) {
            solr_url += "/";
        }
        return solr_url + _schema;
    }

    /**
     * Search for distinct values
     *
     * @param url
     * @param _searchString
     * @param _fields
     * @param _start
     * @param _rows
     * @return
     */
    public static GBResponse searchDistinct(String url, String _searchString,
                                            ArrayList<String> _fields, int _start, int _rows) {
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(url).build();
            if (_searchString == null || _searchString.length() <= 0)
                _searchString = "*:*";
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", "" + _searchString);
            params.set("start", _start);
            params.set("rows", _rows);
            // params.set("sort", "TMID_lastUpdated desc");
            params.set("facet", true);// &facet=true&facet.field=ca
            for (String field : _fields) {
                params.add("facet.field", field);
            }
            params.set("facet.limit", _rows);
            params.set("facet.mincount", 1);
            params.set("wt", "xml");
            XMLResponseParser pars = new XMLResponseParser();
            solr.setParser(pars);
            log.debug("Loading the XML parser"
                    + params.getParameterNames().toString());
            QueryResponse response;
            try {
                response = solr.query(params);
                return new GBResponse(response, _start, _rows);
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (IOException _io) {
                _io.printStackTrace();
            }
        } finally {
            IOUTILs.closeResource(solr);
        }
        return null;
    }

    public static void saveTableProperties(String _path_to_table,
                                           Map<String, String> _p) {
        String corename = GBSolr.getCore(_path_to_table);
        TableManager.saveTableProperties(corename, _p);
    }

    public static Map<String, String> getTableProperties(String _path_to_table) {
        String corename = GBSolr.getCore(_path_to_table);
        return TableManager.getTableProperties(corename);
    }

    public static void saveNodeProperties(String _path_to_node,
                                          Map<String, String> _p) {
        NodeManager n = new NodeManager();
        n.saveNodeProperties(_p, _path_to_node);
    }

    /**
     * Make a fully qualified URL call to solr
     *
     * @param url
     * @return
     */
    public static boolean callSolr(String url) {
        URL u = null;
        try {
            u = new URL(url);
            URLConnection uc = u.openConnection();
            uc.setUseCaches(true);
            InputStream is = uc.getInputStream();
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String deleteCoreByQuery(String solr_url, String _query)
            throws UpdateIndexFailed {
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(solr_url).build();
            org.apache.solr.client.solrj.response.UpdateResponse response = solr
                    .deleteByQuery(_query);
            solr.commit();
            return "" + response.getStatus();
        } catch (Exception _e) {
            _e.printStackTrace();
            throw new UpdateIndexFailed(_e.getLocalizedMessage());
        } finally {
            IOUTILs.closeResource(solr);
        }
    }

    public static String deleteTableByQuery(String path, String _query)
            throws UpdateIndexFailed {
        try {
            GBNodes nodes = getNodes();
            String core = nodes.getCore(path);
            String url = GB.getDefaultURL();
            String ht = url + core;
            if (GBLinkManager.isFullyQualifiedURL(core)) {
                ht = core;
            }
            return deleteCoreByQuery(ht, _query);
        } catch (Exception _e) {
            _e.printStackTrace();
            throw new UpdateIndexFailed(_e.getLocalizedMessage());
        }
    }

    public static boolean callUrl(String url) throws Exception {
        URL u = null;
        HttpURLConnection uc = null;
        BufferedReader in = null;
        try {
            log.info("\n\nURL : " + url);
            log.info("\n\n : ");
            u = new URL(url);

            uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setUseCaches(false);
            in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                log.info(inputLine);
            log.info("\n\n : ");
        } catch (MalformedURLException e) {
            log.error("\n\n failed... : ");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            IOUTILs.closeResource(in);
            uc.disconnect();
        }
        return true;
    }

    public static int ping(String solr_url) {
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(solr_url).build();
            SolrPingResponse resp = solr.ping();
            return resp.getStatus();
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            IOUTILs.closeResource(solr);
        }
        return -1;
    }

    /**
     * Get the table columns for a table at the absolute path
     *
     * @return
     * @throws ConnectException
     */
    public static ArrayList<GColumn> describeTable(String _path)
            throws ConnectException {
        TNode node = getNodes().getNode(_path);
        if (node == null)
            GB.print(" Failed to find the node : " + _path);
        else {
            String lac = node.getLink();
            if (lac != null) {
                String core = LAC.getTarget(lac);
                try {

                    ArrayList<GColumn> cols = GB.getGBTables().describeTable(
                            node);
                    for (GColumn col : cols) {
                        GB.prnt("+\t\t\t" + col.getType() + "\t"
                                + col.getName());
                    }
                    return cols;
                } catch (Exception _e) {
                    _e.printStackTrace();
                }
                GB.print("\n");

            } else {
                GB.print(" This node is currently not linked : " + _path);
            }
        }
        return null;
    }

    /**
     * Get the table columns for a table at the absolute path
     *
     * @return
     * @throws ConnectException
     */
    public static ArrayList<GColumn> getAllColumns(String _path)
            throws ConnectException {

        TNode node = getNodes().getNode(_path);
        String lac = node.getLink();
        if (lac == null)
            throw new ConnectException(
                    " Faild to findthe link associated with the node : "
                            + _path);
        String core = LAC.getTarget(lac);
        if (core == null)
            throw new ConnectException(
                    "Failed to find the core associated with the node : "
                            + path);

        ArrayList<GColumn> cols = TMSolrServer.describeCore(core);
        if (cols == null || cols.size() <= 0)
            throw new ConnectException("No Columns available for : " + path);
        cols = GBSearch.removeTrackingColumns(cols);
        ArrayList<GColumn> ncols = new ArrayList<GColumn>();
        int index = 0;
        for (int i = index; i < cols.size(); i++) {
            GColumn rcol = cols.get(i);
            ncols.add(rcol);
        }
        return ncols;
    }

    public static GBBuilders getBuilder() {
        return new GBBuilders();
    }

    public static GBSearch getSearch() {
        return new GBSearch();
    }

    /**
     * Search a field for hits.
     *
     * @param url
     * @param _field
     * @param _searchString
     * @return
     */
    public static ArrayList<String> fieldSearch(String url, String _field,
                                                String _searchString) {
        HttpSolrClient solr = null;
        try {
            ModifiableSolrParams params = new ModifiableSolrParams();
            _searchString = _searchString.trim();
            // _searchString = _searchString.replace ("\r", "\r*");
            params.set("q", _field + ":\"" + _searchString
                    + "*\" TMID_lastUpdated desc");
            params.set("start", 0);
            params.set("rows", 150);
            // params.set("facet", true);// &facet=ctrue&facet.field=ca
            // params.add("facet.field", _field.toLowerCase());
            // params.set("facet.limit", 200);
            // params.set("facet.mincount", 1);
            params.set("wt", "xml");
            solr = new HttpSolrClient.Builder(url).build();
            XMLResponseParser pars = new XMLResponseParser();
            solr.setParser(pars);

            QueryResponse response = solr.query(params);
            LinkedHashSet<String> dl = new LinkedHashSet<String>();
            ArrayList<String> l = new ArrayList<String>();
            SolrDocumentList list = response.getResults();
            long num_found = list.getNumFound();
            for (int i = 0; i < num_found && i < 150; i++) {
                SolrDocument ld = list.get(i);
                if (ld != null) {
                    Object value = ld.getFieldValue(_field);
                    if (value != null)
                        dl.add(value.toString());
                }
            }
            for (String s : dl) {
                l.add(s);
            }
            return l;
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException _io) {
            _io.printStackTrace();
        } finally {
            IOUTILs.closeResource(solr);
        }

        return null;
    }

    public static void callURL(String url, String _method) throws GBCallException {
        if (_method == null)
            throw new GBCallException();
        if (!(_method.equals("GET") || (_method.equals("POST"))))
            throw new GBCallException();

        URL u = null;
        HttpURLConnection uc = null;
        BufferedReader in = null;
        try {
            log.info("\n\nURL : " + url);
            log.info("\n\n : ");
            u = new URL(url);
            uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod(_method);
            uc.setUseCaches(false);
            log.info("\n\n : ");
            in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                log.info(inputLine);
        } catch (MalformedURLException e) {
            log.error("\n\n failed... : ");
            e.printStackTrace();
            // throw new SolrCallException();
            throw new GBCallException();
        } catch (IOException e) {
            e.printStackTrace();
            // throw new SolrCallException();
            throw new GBCallException();
        } finally {
            IOUTILs.closeResource(in);
            uc.disconnect();
        }
    }

    /**
     * Get the default gbnode objects that points to the default database
     *
     * @return
     */
    public static GBNodes getNodes() {
        DBConnectionManager db = new DBConnectionManager();
        return new GBNodes(db);
    }

    public static String getColumnProperties(String _path) {
        GBNodes nodes = getNodes();
        TNode nod = nodes.getNode(_path);
        DBConnectionManager db = new DBConnectionManager();
        TableManager tmanager = new TableManager(db);
        String linkl = nod.getLink();

        String t = "";
        List<String> col_order = tmanager.getColumnOrder(linkl);
        if (col_order == null) {
            return null;
        }
        for (String s : col_order) {
            t += s + ",";
        }
        t = t.substring(0, t.length() - 1);
        return t;
    }

    public static DBConnectionManager getConnectionManager() {
        DBConnectionManager dbcm = new DBConnectionManager();
        return dbcm;
    }

    public static void exportAll() {
        exportAll(null);
    }

    public static void exportAll(String _path) {
        String str;
        if (_path == null) {
            str = "select path_name from ab_path";
        } else {
            str = "select path_name from ab_path where path_name like '"
                    + _path + "%'";
        }
        DBConnectionManager db = new DBConnectionManager();
        Connection con = db.createConnection();
        File f = null;
        FileOutputStream outStream = null;
        FileInputStream inStream = null;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(str);

            while (rs.next()) {
                String path_name = rs.getString(1);
                print(" Path : " + path_name);
                f = GBFileManager.getFile(path_name);
                if (f != null) {
                    File t = new File(path_name);
                    String wpath = t.getParentFile().getAbsolutePath();
                    wpath = wpath.substring(1);
                    File localDir = new File(wpath);
                    log.debug("Local Director: " + localDir.getAbsolutePath());
                    localDir.mkdirs();
                    byte[] buffer = new byte[1024];
                    int length;
                    try {
                        File localFile = new File(localDir, f.getName());
                        log.debug(" writing to " + localFile.getAbsolutePath());
                        outStream = new FileOutputStream(localFile);
                        inStream = new FileInputStream(f);
                        while ((length = inStream.read(buffer)) > 0) {
                            outStream.write(buffer, 0, length);
                        }
                        System.out.println("Saved "
                                + localFile.getAbsolutePath());
                    } catch (IOException _io) {
                        _io.printStackTrace();
                        log.error("Failed to save the path : " + path_name);
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBC.closeConnection(con);
            IOUTILs.closeResource(inStream);
            IOUTILs.closeResource(outStream);
            if (f != null && f.isFile())
                f.delete();
        }
    }

    public static void executeExternalProcess(String goSolr)
            throws IOException, InterruptedException {

        Runtime r = Runtime.getRuntime();
        Process p = r.exec(goSolr);
        // BufferedReader reader = new BufferedReader(new InputStreamReader(
        // p.getInputStream()));
        // String line = reader.readLine();
        // while (line != null) {
        // line = reader.readLine();
        // }

    }

    /**
     * List the nodes in the current path
     *
     * @return
     */
    public static String ls() {
        return ls(path.getPath());
    }

    public UserPath gePath() {
        return path;
    }

    public static String cd(String _dir) {
        if (_dir.equals("..") || _dir.equals("../")) {
            // String parent = PathManager.getPopPath(_dir);
            // this is incomplete
            String parent = PathManager.getParent(path.getPath());
            path.setPath(parent);
            return path.getPath();
        } else if (_dir.startsWith("/")) {
            path.setPath(_dir);
        } else {
            path.setPath(path.getPath() + "/" + _dir);
        }
        return _dir;
    }

    public static String pwd() {
        return path.getPath();
    }

    public static String mkdir(String _dir) {
        String p = _dir;
        if (!_dir.startsWith("/")) {
            p = path.getPath() + "/" + _dir;
        }
        // System.out.println (" path " + p);
        return "" + GBNodes.mkdir(DEFAULT_UER, p);
    }

    public String lls(String path) {
        File f = new File(path);
        if (f.exists()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream pmr = new PrintStream(baos);
            String[] ff = f.list();
            for (String s : ff) {
                pmr.append(s + "\n");
            }
            String t = baos.toString();
            return t;
        } else {
            return "Does not appear to be a path on the local file system. ";
        }
    }

    public static String lls() {
        String p = lpath.getPath();
        File f = new File(p);
        if (f.exists()) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream baos = System.out;
            PrintStream pmr = new PrintStream(baos);
            String[] ff = f.list();
            for (String s : ff) {
                pmr.append(s + "\n");
            }
            String t = baos.toString();
            return t;
        } else {
            return "Does not appear to be a path on the local file system. ";
        }
    }

    @Command(name = "import")
    public String loadtable(String local_file, String dir) {
        if (!dir.startsWith("/")) {
            String abdir = path.getPath();
            return loadtable(local_file, abdir + "/" + dir);
        }
        if (local_file.startsWith("/")) {
            File f = new File(local_file);
            if (!f.exists()) {
                return "Local file : " + local_file + " not found.";
            } else {
                String[] args = new String[6];
                args[0] = "import";
                args[1] = "-type=table";
                args[2] = "-fields=0";
                args[3] = "-delim=\"\\t\"";
                args[4] = "" + local_file;
                args[5] = "" + dir;
                try {
                    GBNodes.load(args);
                } catch (UsageException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String s = lpath.getPath();
            return loadtable(s + "/" + local_file, dir);
        }
        return "Load complete";
    }


    @Command
    public static String ls(@Param(name = "args") String... _args) {
        if (_args.length <= 0) {
            return roots();
        }

        String _p = _args[0];
        if (!_p.startsWith("/"))
            _p = "/" + _p;
//        GBNodes nodes = getNodes();
        String[] re = GBNodes.listPath(_p);
        String res = "";
        if (re == null) {
            return null;
        }
        for (String p : re) {
            res += p + "\n";
        }
        return res;
    }

    /**
     * fix the path for the gb
     *
     * @param p
     * @return
     */
    private String fix(String p) {
        if (p.startsWith("/")) {
            return p;
        }
        return path.getPath() + "/" + p;
    }

    @Command
    public void desc(String path) {
        path = fix(path);
        GBNodes.describe(path);
    }

    @Command
    public void create(@Param(name = "args") String... args) {
        // GBTables.create(args);
    }

    @Command(name = "print")
    public void printVariables(String key) {
        variables.printVariable(key);
    }

    public static String getDefaultUser() {
        return "giradelli";
    }

    public static void setVariable(String key, GBV gbv) {
        variables.setVar(key, gbv);
    }

    public static void setVariable(String _vcommand) throws UsageException {
        String[] sp = _vcommand.split("=");
        try {
            variables.setVar(sp[0], sp[1]);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
            throw new UsageException(GBVariables.USAGE);
        }

    }

    public static GBVariables getVariables() {
        return variables;
    }

    public static GBV getVariable(String _cmd) {
        return variables.getVariable(_cmd);
    }

    public static String getDefaultURL() {
        String solr_url = ABProperties.get(ABProperties.SOLRSITE);
        if (!solr_url.endsWith("/")) {
            solr_url += "/";
        }
        return solr_url;
    }

    public static void print(Map<String, String> props) {
        if (props == null) {
            print("-\t" + " no values");
            return;
        }
        Set<String> p = props.keySet();
        for (String s : p) {
            print("-\t" + s + "\t" + props.get(s));
        }
        print("\n");
    }

    public static String absolute(String item) {
        if (item.startsWith("/"))
            return item;
        else
            return GB.pwd() + "/" + item.trim();
    }

    public static void setNodeProperty(long node_id, String key, String value) {
        Session session = HBConnect.getSession();
        try {
            session.beginTransaction();
            Criteria c = session.createCriteria(NodeProperty.class);
            c.add(Restrictions.and(Restrictions.eq("node_id", node_id),
                    Restrictions.eq("name", key)));
            List list = c.list();
            if (list == null || list.size() <= 0) {
                NodeProperty nodep = new NodeProperty();
                nodep.setNode_id(node_id);
                nodep.setName(key);
                nodep.setProperty(value);
                session.save(nodep);
            } else {
                NodeProperty nodep = (NodeProperty) list.get(0);
                nodep.setProperty(value);
                session.update(nodep);
            }
            session.flush();
            session.getTransaction().commit();
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(session);
        }
    }

    public static String setNodeProperty(String node, String key, String value) {
        TNode tnode = getNodes().getNode(node);
        if (tnode == null)
            return "Could not find a node at path: " + node;
        else {
            setNodeProperty(tnode.getNode_id(), key, value);
            return "Property has been saved.";
        }

    }

    public static String getNodeProperty(String node, String key) {
        TNode tnode = getNodes().getNode(node);
        if (tnode != null) {
            return getNodeProperty(tnode.getNode_id(), key);
        } else
            return null;
    }

    public static String getNodeProperty(long node_id, String key) {
        Session session = HBConnect.getSession();
        try {
            session.beginTransaction();
            Criteria c = session.createCriteria(NodeProperty.class);
            c.add(Restrictions.and(Restrictions.eq("node_id", node_id),
                    Restrictions.eq("name", key)));
            List list = c.list();
            if (list == null || list.size() <= 0) {
                return null;
            } else {
                NodeProperty nodep = (NodeProperty) list.get(0);
                String property = nodep.getProperty();
                session.close();
                return property;
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(session);
        }
        return null;
    }

    public static Map<String, String> getNodeProps(String _path) {
        NodeManager manager = new NodeManager();
        TNode node = manager.getNode(_path);
        if (node != null)
            return NodeManager.getNodePropertyMap(node.getNode_id());
        else
            return null;
    }

    public static Map<String, String> getNodeProps(long _id) {
        return NodeManager.getNodePropertyMap(_id);
    }

    public static void removeNodeProperty(long node_id, String key) {
        Session session = HBConnect.getSession();
        try {
            session.beginTransaction();
            Criteria c = session.createCriteria(NodeProperty.class);
            c.add(Restrictions.and(Restrictions.eq("node_id", node_id),
                    Restrictions.eq("name", key)));
            List list = c.list();
            if (list == null || list.size() <= 0) {
                return;
            } else {
                NodeProperty nodep = (NodeProperty) list.get(0);
                session.delete(nodep);
                session.flush();
                session.getTransaction().commit();
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(session);
        }
    }

    public static void print(Properties prop) {
        Set obj = prop.keySet();
        for (Object objs : obj) {
            GB.print(objs.toString() + " == "
                    + prop.getProperty(objs.toString()));
        }
    }

    public static ArrayList<GRow> fetch(String path, String search,
                                        String column) {
        String[] s = new String[1];
        s[0] = column;
        String site = ABProperties.getSolrURL();
        String core = TMSolrServer.getCore(path);
        ABaseResults results = TMSolrServer.search(site, core, search, 0, 10,
                null, s, new SearchConfig(SearchConfig.RAW_SEARCH));
        if (results == null)
            return null;
        else
            return results.getValues();

    }

    public static ArrayList<GRow> cacheGet(String path, String key) {
        String[] s = new String[1];
        String site = ABProperties.getSolrURL();
        String core = TMSolrServer.getCore(path);
        ABaseResults results = TMSolrServer.search(site, core, "index:" + key,
                0, 10, null, s, new SearchConfig(SearchConfig.RAW_SEARCH));
        if (results == null)
            return null;
        else
            return results.getValues();

    }

    public static boolean isCache(String path) {
        TNode node = GB.getNodes().getNode(path);
        if (node == null)
            return false;
        GB.print("is node : " + node.getNode_id() + " a cache table?");
        String schema = TMSolrServer.getCore(path);
        if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
                || node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)) {
            GB.print("Yes it is!");

            return true;
        }
        return false;
    }

    /**
     * Put an item in the object.
     *
     * @param path
     * @param key
     * @return
     * @throws NotASearchableTableException
     */
    public static String cache(String path, String key, String value)
            throws NotASearchableTableException, NodeNotFoundException {
        TNode node = GB.getNodes().getNode(path);
        if (node == null)
            throw new NodeNotFoundException(path);
        String schema = TMSolrServer.getCore(path);
        if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
                || node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)) {
            String solr = ABProperties.get(ABProperties.SOLRSITE);
            TMSolrServer so = new TMSolrServer(solr);
            String column = key.trim();
            value = value.trim();
            try {
                GResults res = so.search(schema, "index:" + key,
                        new SearchConfig(SearchConfig.RAW_SEARCH));
                if (res != null && res.getTotalHits() > 0)
                    so.findAndReplace(schema, column, null, "index:" + key,
                            value);
                else {
                    ArrayList<String> values = new ArrayList<String>();
                    // order is important.
                    values.add(key.trim());
                    values.add(value.trim());
                    String link = node.getLink();
                    String _schema = LAC.getTarget(link);
                    String url = GB.getDefaultURL();
                    TableManager.add(values, url, _schema);
                }
            } catch (LoaderException e) {
                e.printStackTrace();
            }
        } else
            throw new NotASearchableTableException();

        return "Item added";
    }

    public static void createCache(String _path) throws NodeCreateFailed {
        TNode node = GB.getNodes().getNode(_path);
        if (node != null)
            throw new NodeCreateFailed(_path,
                    "There is a node already defined here.");
        GBTables.createDefaultCacheTable(_path);
        GB.print(" Cache has been created ");
    }

    /**
     * perform a global search on a table.
     *
     * @param _path
     * @return
     */
    public static GBSearchIterator select(String _path) {
        GBSearchIterator gb = new GBSearchIterator(_path, "*:*", null, null,
                new SearchConfig(SearchConfig.RAW_SEARCH), 100000);
        return gb;
    }

    /**
     * perform a global search on a table.
     *
     * @param _path
     * @return
     */
    public static GBSearchIterator select(String _path, String search) {
        GBSearchIterator gb = new GBSearchIterator(_path, search, null, null,
                new SearchConfig(SearchConfig.RAW_SEARCH), 100000);
        return gb;
    }

    /**
     * perform a global search on a table.
     *
     * @param _path
     * @return
     */
    public static GBSearchIterator select(String _path, String search,
                                          String _sort) {
        GBSearchIterator gb = new GBSearchIterator(_path, search, _sort, null,
                new SearchConfig(SearchConfig.RAW_SEARCH), 100000);
        return gb;
    }

    public static String copy(String from, String to) {
        GB.print("\tCopy \t\t " + from + " to  " + to);
        String tparent = GBPathUtils.getParent(to).trim();

        NodeManager manager = new NodeManager();
        GBNodes nodes = GB.getNodes();
        TNode toNode = manager.getNode(to);
        // check if there is a node at this point
        TNode toParentNode = manager.getNode(tparent);
        if (toParentNode == null)
            GBNodes.mkdir(GB.DEFAULT_UER, tparent);
        // TPath toPath = manager.getPath(to);


        TPath fromPath = manager.getPath(from);
        if (fromPath == null) {
            GB.print("Path " + from + " not found.");
            return "Path " + from + " not found.";
        }


        TNode fromNode = manager.getNode(fromPath.getNode_id());
        // if there is a link then we need to copy the core
        if (fromNode.getLink() != null) {
            String corename = LAC.getTarget(fromNode.getLink());
            String to_core_name = NameUtiles.convertToValidCharName(to);
            TNode tnode = nodes.createNode(GB.DEFAULT_UER, to, to_core_name
                    + ".search(*)", fromNode.getNodeType());
            if (tnode != null) {
                String url = ABProperties.getSolrURL();
                LinkedHashMap<String, String> lm = new LinkedHashMap<String, String>();
                // we need to create the values
                lm.put("from", corename);
                lm.put("to", to_core_name);
                try {
                    TMSolrServer.callSolrAction(url, "cp_core", lm);
                } catch (SolrCallException e) {
                    e.printStackTrace();
                }
            }
        } else {
            TNode tnode = nodes.createNode(GB.DEFAULT_UER, to, null, fromNode.getNodeType());
        }
        return "Copy complete.";
    }

    /**
     * perform a global search on a table.
     *
     * @param _path
     * @return
     */
    public static GBSearchIterator select(String _path, String search,
                                          String _sort, String[] _cols) {
        GBSearchIterator gb = new GBSearchIterator(_path, search, _sort, _cols,
                new SearchConfig(SearchConfig.RAW_SEARCH), 100000);
        return gb;
    }

    public static void print(
            ArrayList<LinkedHashMap<String, Object>> increment,
            String post_string) {
        for (LinkedHashMap<String, Object> ls : increment) {
            Set<String> keys = ls.keySet();
            String ps = post_string;
            for (String key : keys) {
                ps = ps.replace("[" + key + "]", ls.get(key) + "\t\t");
            }
            System.out.println(" " + ps);
        }
    }

    public static void print(Set<String> keys) {
        String ps = "";
        for (String key : keys) {
            ps += "[" + key + "]" + "\t\t";
        }
        GB.print(ps);
    }

    public static void print(ArrayList<LinkedHashMap<String, Object>> increment) {
        for (LinkedHashMap<String, Object> ls : increment) {
            Set<String> keys = ls.keySet();
            String ps = "";
            for (String key : keys) {
                ps += "[" + ls.get(key) + "]" + "\t\t";
            }
            GB.print(ps);
        }
    }


    public static TableManager getTableManager() {
        DBConnectionManager dbcm = GB.getConnectionManager();
        return new TableManager(dbcm);
    }

    public static TNode createTable(String _user, String _path,
                                    Map<String, String> fields) {
        ArrayList<GColumn> gclist = new ArrayList<GColumn>();
        Set<String> cfields = fields.keySet();
        for (String s : cfields) {
            String name = s.trim();
            String type = fields.get(s);
            GColumn column = new GColumn(name, type);
            gclist.add(column);
        }
        TNode node = GB.getNodes().getNode(_path);
        if (node == null) {
            node = GB.getNodes().mkNode(_user, _path, "", SourceType.TABLE);
        }

        String schema = NameUtiles.convertToValidCharName(_path);
        // NameUtiles.prepend(user, path);
        DBConnectionManager dbcm = GB.getConnectionManager();
        NodeManager tmnode = new NodeManager(dbcm);
        LinkedHashMap<String, Map<String, String>> _params = SetSchemaCommand
                .createParameters(gclist);
        Set<String> list2 = _params.keySet();
        for (String l : list2) {
            System.out.println("\tf:" + l);
        }
        TableManager tmd = new TableManager(dbcm);
        tmd.build(_user, TableManager.TMSOLR, schema, "__", "1", _params, null);
        // TableManager table_manager = tables.get
        // GB.print("\n\n\n created schema = \t " + table_name);
        String link = "" + schema + ".search(*:*)";
        node.setLink(link);
        node.setLastEditedDate(new Date());
        Session hibernateSession = dbcm.getSession();
        hibernateSession.beginTransaction();
        tmnode.save(node, hibernateSession);

        // String _lib_name = NameUtiles.strip(_userName, _name);
        TTable litem = new TTable();
        try {
            // lock it
            synchronized (hibernateSession) {

                Criteria c = hibernateSession.createCriteria(TTable.class);
                c.add(Restrictions.eq("title", schema));
                List values = c.list();
                if (values != null && values.size() > 0) {
                    litem = (TTable) values.get(0);
                }
                litem.setDescription("");
                litem.setLastEdited(new Date());
                // litem.setSecurityStatus(_security + ".png");
                litem.setUser(_user);
                litem.setSourceType(node.getNodeType());

                TMTableSettings tmset = litem.getSettings();
                if (tmset == null)
                    tmset = new TMTableSettings();
                Set<String> keys = _params.keySet();
                LinkedHashMap<String, Integer> order = new LinkedHashMap<String, Integer>();
                int index1 = 0;
                for (String key : keys) {
                    order.put(key, index1++);
                }
                tmset.setCol_order(order);
                litem.setSettings(tmset);
                litem.setTitle(schema);
                if (litem.getItemID() < 0)
                    hibernateSession.save(litem);
                else {
                    hibernateSession.update(litem);
                    hibernateSession.flush();
                }
                hibernateSession.getTransaction().commit();
            }

            return node;
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(hibernateSession);
        }
        return null;
    }

    /**
     * Create a node with the given type and path... If the node exists... throw
     * exception
     *
     * @param type
     * @param path
     * @return
     * @throws NodeExistsException
     */
    public static TNode create(SourceType type, String path)
            throws NodeExistsException {
        TNode node = GB.getNodes().getNode(path);
        if (node == null) {
            node = GB.getNodes().mkNode(GB.DEFAULT_UER, path, "",
                    SourceType.TABLE);
        } else {
            throw new NodeExistsException(path, node);
        }
        return node;
    }

    public static TNode createTempTable(String _user, Map<String, String> fields) {

        Date current = new Date();
        SecureRandom random = new SecureRandom();
        String value = new BigInteger(130, random).toString(32);
        if (value.length() > 4) {
            value = value.substring(0, 4);
        }

        String temp = "/tmp/" + value + "" + current.getTime() + "/temp";
        String _path = temp;

        ArrayList<GColumn> gclist = new ArrayList<GColumn>();
        Set<String> cfields = fields.keySet();
        for (String s : cfields) {
            String name = s.trim();
            String type = fields.get(s);
            GColumn column = new GColumn(name, type);
            gclist.add(column);
        }
        TNode node = GB.getNodes().getNode(_path);
        if (node == null) {
            node = GB.getNodes().mkNode(_user, _path, "", SourceType.TABLE);
        }
        String schema = NameUtiles.convertToValidCharName(_path);
        // NameUtiles.prepend(user, path);
        DBConnectionManager dbcm = GB.getConnectionManager();
        NodeManager tmnode = new NodeManager(dbcm);
        LinkedHashMap<String, Map<String, String>> _params = SetSchemaCommand
                .createParameters(gclist);
        for (GColumn g : gclist) {
            System.out.println(" 1.column " + g.getName());
        }
        Set<String> list2 = _params.keySet();
        for (String l : list2) {
            System.out.println(" 2.column " + l);
        }
        TableManager tmd = new TableManager(dbcm);
        tmd.build(_user, TableManager.TMSOLR, schema, "__", "1", _params, null);
        // TableManager table_manager = tables.get
        // GB.print("\n\n\n created schema = \t " + table_name);
        String link = "" + schema + ".search(*:*)";
        node.setLink(link);
        node.setLastEditedDate(new Date());
        tmnode.save(node);

        Session hibernateSession = null;
        // String _lib_name = NameUtiles.strip(_userName, _name);
        TTable litem = new TTable();
        try {
            hibernateSession = dbcm.getSession();
            // lock it
            synchronized (hibernateSession) {
                hibernateSession.beginTransaction();
                Criteria c = hibernateSession.createCriteria(TTable.class);
                c.add(Restrictions.eq("title", schema));
                List values = c.list();
                if (values != null && values.size() > 0) {
                    litem = (TTable) values.get(0);
                }
                litem.setDescription("");
                litem.setLastEdited(new Date());
                // litem.setSecurityStatus(_security + ".png");
                litem.setUser(_user);
                litem.setSourceType(node.getNodeType());

                TMTableSettings tmset = litem.getSettings();
                if (tmset == null)
                    tmset = new TMTableSettings();
                Set<String> keys = _params.keySet();
                LinkedHashMap<String, Integer> order = new LinkedHashMap<String, Integer>();
                int index1 = 0;
                for (String key : keys) {
                    order.put(key, index1++);
                }
                tmset.setCol_order(order);
                litem.setSettings(tmset);
                litem.setTitle(schema);
                if (litem.getItemID() < 0)
                    hibernateSession.save(litem);
                else {
                    hibernateSession.update(litem);
                    hibernateSession.flush();
                }
                hibernateSession.getTransaction().commit();
            }
            return node;
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(hibernateSession);
        }
        return null;
    }

    public static void execute(String command) throws UsageException {
        System.out.println(" -- ");
        GBCommand standard = new GBCommand();
        standard.exec(command, null);
    }

    public static void printSub(String string) {
        GB.print("\t\t" + string);
    }


    public static void print(
            Iterator<ArrayList<LinkedHashMap<String, Object>>> values) {
        if (values != null)
            while (values.hasNext()) {

                ArrayList<LinkedHashMap<String, Object>> sf = values.next();
                for (Map<String, Object> m : sf) {

                    Set<String> keys = m.keySet();
                    String row = "";
                    for (String k : keys) {
                        row += "[" + m.get(k) + "]";
                    }
                    GB.print(row);

                }

            }

    }

    public static GBCommand getCommands() {
        GBCommand c = commandStack.peek();
        return c;
    }

    public static void exec(String v, String param) {
        try {
            GBCommand c = commandStack.peek();
            c.exec(v, param);
        } catch (UsageException e) {
            e.printStackTrace();
        }
    }

//    static ABTable stats = new ABTable(STATS);

    public static ABTable getStats() {
        return null;
    }


    public static ABTable createStats() {
        HashMap<String, String> stats_schema = new HashMap<String, String>();
        stats_schema.put("path", "string_ci");
        stats_schema.put("action_date", "date");
        stats_schema.put("action", "string_ci");
        ABTable stats = new ABTable(STATS);
        try {
            stats.create(stats_schema);
        } catch (NodeExistsException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private static HashMap<String, String> pathcache = new HashMap<String, String>();

    static void updateStats(final String path, final String action) {
//        final String hash = path;
//        String previous = pathcache.get(hash);
//
//        if (previous != null) {
//            return;
//        } else {
//            System.out.println(path + "___ put update stat path : " + hash);
//            pathcache.put(hash, path);
//            final ABTable stats = getStats();
//            final Thread t = new Thread(new Runnable() {
//                public void run() {
//                    try {
//
//
//                        // {{ THIS IS ADDED IN ORDER TO KEEP FROM OVERLOADING THE SOLR SERVER }}
//
//                        Thread.sleep(5000l);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    ArrayList<LinkedHashMap<String, Object>> update = new ArrayList<LinkedHashMap<String, Object>>();
//                    LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
//                    m.put("path", path);
//                    m.put("action_date", new Date());
//                    m.put("action", action);
//                    update.add(m);
//
//                    System.out.println(" ____  ");
//                    stats.append(update);
//                    try {
//                        Thread.sleep(5000l);
//                        pathcache.remove(hash);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//
//
//                }
//            });
//            t.start();
//        }
    }

    public static String pwd_currentDirectory() {
        String pwd = pwd();
        int i = pwd.lastIndexOf('/');
        if (i > 0) {
            String t = pwd.substring(i + 1);
            return t.trim();
        }
        return pwd;
    }

    public static void registerConsoleReader(ConsoleReader reader) {
        console = reader;
    }

    public static History getConsoleHistory() {
        if (console == null)
            return null;
        return console.getHistory();
    }
}
