package com.arraybase;

import com.arraybase.aws.S3;
import com.arraybase.aws.S3Importer;
import com.arraybase.db.*;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.BatchLoader;
import com.arraybase.flare.BatchLoaderFailedException;
import com.arraybase.flare.Parse;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.io.ABQFile;
import com.arraybase.io.GBFileManager;
import com.arraybase.lac.LAC;
import com.arraybase.modules.NodeTypeChangeManager;
import com.arraybase.modules.UsageException;
import com.arraybase.net.FTPImporter;
import com.arraybase.search.ABaseResults;
import com.arraybase.search.SearchPathFailedExeption;
import com.arraybase.shell.cmds.CreateTableNodeCommand;
import com.arraybase.tm.*;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class GBNodes {
    private NodeManager node = new NodeManager();
    private DBConnectionManager dbcm = null;
    private static GBLogger log = GBLogger.getLogger(GBNodes.class);

    public GBNodes(DBConnectionManager _dbcm) {
        dbcm = _dbcm;
        node = new NodeManager(dbcm);
    }

    public String getPath(long node_id) {
        return node.getPath(node_id);
    }

    public Map<Integer, TNode> load(List<Integer> _node_ids) {
        return node.load(_node_ids);
    }

    public TPath getPath(String _path) {
        return node.getPath(_path);
    }

    public TNode getNode(String _path) {
        return node.getNode(_path);
    }

    public TNode getNode(long _nodeid) {
        return node.getNode(_nodeid);
    }

    /**
     * @param path    : the fully qualifed name of the object that will undergo a
     *                name change.
     * @param newName -- is just the new name of the leaf node from the previous
     *                argument:
     *                <p>
     *                For example to change the name of a node /gne/research/mynode
     *                to /gne/research/yourNode call this method with rename (
     *                /gne/research/mynode, yourNode );
     */
    public String rename(String path, String newName) {
        return node.renameLeaf(path, newName);
    }

    public String linkNode(String user_name, long node_idl, String path) {
        return node.linkNode(user_name, node_idl, path);
    }

    public List<TNode> getNodes(List<Integer> reference) {
        return node.getNodes(reference);
    }

    public String[] getNodes(String _path) {

        // System.out.println(_path);
        if (_path.equalsIgnoreCase("/")) {
            GBNodes nodes = GB.getNodes();
            String[] a = nodes.getRoots();
            return a;
        }
        NodeManager tm = new NodeManager();
        if (_path.equals("*")) {
            List<String> nodes = tm.getAllPaths();
            String[] p = nodes.toArray(new String[nodes.size()]);
            return p;
        } else if (_path.endsWith("*")) { // wild card ls
            List<TNode> nodes = tm.getRefNodes(_path);
            if (nodes == null) {
                return null;
            }
            for (TNode n : nodes) {
                if (n != null) {
                    String[] files = tm.list(n);
                    return files;
                }
            }
        } else {
            _path = _path.trim();
            TNode n = tm.getNode(_path);
            if (n == null) {
                return null;
            }
            String[] files = tm.listChildNames(n);
            return files;
        }
        return null;
    }

    /**
     * Get the path for a node object.
     *
     * @return
     */
    public TPath getPath(TNode n) {
        TPath path = null;
        Session session = null;
        try {
            session = dbcm.getSession();
            session.beginTransaction();
            path = node.getPathForNode(n.getNode_id(), session);
            return path;
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(session);
        }
        return path;
    }

    public TPath getTPath(TNode _n) {
        return node.getTPath(_n.getNode_id());
    }

    /**
     * Given a path this will return a core string
     *
     * @param path
     * @return
     */
    public String getCore(String path) {
        TNode thisnode = getNode(path);
        if (thisnode == null)
            return "Node not found.";
        String lac = thisnode.getLink();
        if (lac == null)
            return "Node not found."; // need to add exception framework here.
        return GBSolr.getCoreFromLAC(lac, dbcm);
    }

    public String saveReferences(TNode pnode, String[] linkpaths) {
        Session s = null;
        try {
            s = dbcm.getSession();
            s.beginTransaction();
            Criteria c = s.createCriteria(TNode.class);
            c.add(Restrictions.eq("node_id", pnode.getNode_id()));
            TNode pitem = (TNode) c.uniqueResult();
            for (String lpath : linkpaths) {
                TNode ct = getNode(lpath);
                // System.out.println("-[" + pnode.getNode_id() + "]- --> -["
                // + ct.getNode_id() + "]-");
                pitem.addCRef(ct);
            }
            s.saveOrUpdate(pitem);
            s.getTransaction().commit();
        } catch (Exception _e) {
            _e.printStackTrace();
            return "Failed to create links to : [" + pnode.getNode_id() + "]";
        } finally {
            HBConnect.close(s);
        }
        return "References saved";

    }

    public void save(TNode _node) {
        node.save(_node);
    }

    public void save(TNode _node, Session session) {
        node.save(_node, session);
    }

    public void save(TPath _path, Session _session) {
        node.save(_path, _session);
    }

    public DBConnectionManager getDBConnectionManager() {
        return dbcm;
    }

    /**
     * Get all the root files
     *
     * @return
     */
    public String[] getRoots() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            String sql = "select distinct group_name from ab_path";
            con = dbcm.getJDBCConnection();
            st = con.createStatement();
            rs = st.executeQuery(sql);
            ArrayList<String> l = new ArrayList<String>();
            while (rs.next()) {
                String dl = rs.getString(1);
                if (!dl.startsWith("/"))
                    dl = "/" + dl;
                // log.debug(dl);
                l.add(dl);

            }

            String[] s = new String[l.size()];
            s = l.toArray(s);
            return s;
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
            JDBC.closeConnection(con);
        }
        return null;
    }

    public void save(TPath t) {
        Session s = null;
        try {
            s = dbcm.getSession();
            s.beginTransaction();

            if (t.getPath_id() > 0) {
                Criteria c = s.createCriteria(TPath.class);
                c.add(Restrictions.eq("path_id", t.getPath_id()));
                List l = c.list();
                if (l != null && l.size() > 0) {
                    TPath p = (TPath) l.get(0);
                    p.setDescription(t.getDescription());
                    p.setGroup_name(t.getGroup_name());
                    p.setName(t.getName());
                    p.setPath_id(t.getPath_id());
                    p.setTMParent(t.getTMParent());
                    s.update(p);
                    s.flush();
                }
            } else {
                s.save(t);
                s.getTransaction().commit();
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(s);
        }
    }

    /**
     * @return
     */
    public NodeTypeChangeManager changeType(TNode _node, String _type) {
        // if the node is an instance of a field type node then
        // we are going to change the field node to the new
        // node type.
        if (_node instanceof FieldNode) {
            FieldNode fn = (FieldNode) _node;
            String f_type = fn.getNodeType();
            if (f_type != null && f_type.equalsIgnoreCase(_type)) {
                return new NodeTypeChangeManager(
                        "Node type is the same.  Nothing changed.");
            } else {
                String lac = _node.getLink();
                String core = GBSolr.getCoreFromLAC(lac, dbcm);
                String url = ABProperties.getSolrURL();
                GBTables.changeField(url, core, _node.getName(), _type);
                return new NodeTypeChangeManager("Field : " + _node.getName()
                        + " in table : " + core + " has changed to  " + _type);
            }
        }
        return new NodeTypeChangeManager("Nothing changed... ");
    }

    public static void list(String[] _args) {
        if (_args.length == 2) {
            // {{ if we have exactly one argument }}
            String path = _args[1];
            if (path == null || path.length() <= 0) {
                System.err.println(" Please provide a path argument.");
                return;
            } else {
                listPath(path);
            }
        } else {
            NodeManager tm = new NodeManager();
        }
    }

    public static String[] getNodeNames(String _path) {
        ArrayList<String> str = new ArrayList<String>();
        NodeManager tm = new NodeManager();
        List<TNode> nodes = tm.getRefNodes(_path);
        for (TNode n : nodes) {
            str.add(n.getName());
        }
        if (str.size() <= 0)
            return new String[0];
        else
            return str.toArray(new String[str.size()]);
    }

    public static String[] listPath(String _path) {
        // System.out.println(_path);
        if (_path.equalsIgnoreCase("/")) {
            GBNodes nodes = GB.getNodes();
            String[] a = nodes.getRoots();
            return a;
        }

        NodeManager tm = new NodeManager();
        if (_path.equals("*")) {
            List<String> nodes = tm.getAllPaths();
            for (String d : nodes) {
                GB.print(d);
            }
        } else if (_path.endsWith("*")) { // wild card ls
            List<TNode> nodes = tm.getRefNodes(_path);
            if (nodes == null) {
                System.err.println("No path : " + _path);
            }
            for (TNode n : nodes) {
                if (n != null) {
                    String[] files = tm.list(n);
                    if (n.getName() != null) {
                        System.out.printf("-[%1$s]}: %2$s/ ", n.getNode_id(),
                                n.getName() + "\n");
                        GB.print(files);
                    }
                }
            }
        } else {
            _path = _path.trim();
            TNode n = tm.getNode(_path);
            if (n == null) {
                System.err.println("No path : " + _path);
                return null;
            }
            String[] files = tm.list(n);
            if (files.length > 0) {
                System.out.printf("-[%1$s]: %2$s/ ", n.getNode_id(),
                        n.getName() + "\n");
                GB.print(files);
            } else {
                String nodeid = "" + n.getNode_id();
                if (n.getNode_id() < 0)
                    nodeid = "NA";
                System.out.printf("-[%1$s]: %2$s ", nodeid, n.getName() + "\n");
                if (n instanceof FieldNode) {
                    try {
                        String[] columns = {n.getName()};
                        String parent_path = GBPathUtils.getParent(_path);
                        searchPath(parent_path, "*:*", columns, System.out,
                                new SearchConfig(SearchConfig.RAW_SEARCH));
                    } catch (NotASearchableTableException e) {
                        e.printStackTrace();
                    }
                }
            }
            return files;
        }
        return null;
    }

    public List<TNode> listPathNodes(String _path) {
        NodeManager tm = new NodeManager();
        return tm.getNodes(_path);
    }

    public static ArrayList<String> listPathName(String _path) {
        NodeManager tm = new NodeManager();
        ArrayList<String> names = new ArrayList<String>();
        if (_path.equals("*")) {
            log.info("Listing all available paths: ");
            List<String> nodes = tm.getAllPaths();
            for (String d : nodes) {
                names.add(d);
            }
            return names;
        } else if (_path.endsWith("*")) { // wild card ls
            List<TNode> nodes = tm.getNodes(_path);
            if (nodes == null) {
                return names;
            }
            for (TNode n : nodes) {
                if (n != null) {
                    String[] files = tm.list(n);
                    if (n.getName() != null) {
                        names.add(n.getName());
                    }
                }
            }
        } else {
            List<TNode> nodes = tm.getNodes(_path);
            if (nodes == null || nodes.size() <= 0) {
                System.err.println("No path : " + _path);
                return null;
            }
            for (TNode _nn_ : nodes) {
                names.add(_nn_.getName());
            }
        }
        return names;
    }

    public static void loadBinary(String... _args) throws UsageException {
        String userid = _args[0];
        String localfile = _args[1];
        String path = _args[2];

        GB.print("\tInserting binary file into the following path : " + path);
        String res = save(userid, localfile, path);
        GB.print(res);
    }

    /**
     * This takes a properties object with the following required values: url
     * pass user driver cfg cfgClassess
     */
    public static String save(String _userId, String _localfile, String _path) {
        File f = new File(_localfile);
        if (!f.exists())
            return "File " + f.getAbsolutePath() + " not found";
        else {
            GBFileManager fileManager = new GBFileManager();
            fileManager.save(_userId, f, _path);
            return "Saved to : " + _path;
        }
    }

    public static void mkDir(String[] _args) {
        if (_args.length == 3) {
            String userid = _args[1];
            String path = _args[2];
            GB.print(mkdir(userid, path));
        } else if (_args.length == 2) {
            String userid = GB.DEFAULT_UER;
            String path = _args[1];
            if (!path.startsWith("/"))
                path = GB.pwd() + "/" + path;
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            GB.print(mkdir(userid, path));
        }

    }

    public static String mkdir(String _userid, String _path) {
        NodeManager manager = new NodeManager();
        TNode node = manager.createPath(_path, _userid, SourceType.NODE);
        if (node != null) {
            return "\t " + _path;
        } else
            return "Failed to create directory";

    }

    /**
     * @param _args
     * @deprecated
     */
    public static void tag(String[] _args) {
        String userid = _args[1];
        String path = _args[2];
        String fac = _args[3];
        GB.print(mkdir(userid, path));
        TNode newReference = NodeManager.createNodeFromReferenceScript(fac);
    }

    public static void removeDir(String[] _args) {
        String userid = _args[1];
        String path = _args[2];
        GB.print(rmdir(userid, path));
        return;
    }

    /**
     * Remoe the directory in the path
     *
     * @param _userid
     * @param _path   : fully qualified path to be removed
     * @return
     */
    public static String rmdir(String _userid, String _path) {
        String[] files = GBNodes.listPath(_path);
        if (files == null) {
            return "Path not found.";
        } else {
            NodeManager manager = new NodeManager();
            return manager.removePath(_path);
        }
    }

    public static void rm(String[] _args) {
        String userid = _args[1];
        String path = _args[2];
        GB.print(rmdir(userid, path));
        return;
    }

    public static void mkNode(String[] _args) {
        if (_args.length < 4) {
            GB.printUsage("example:  gb mknode $userid $path $link $type");
        }
        String userid = _args[1];
        String path = _args[2];
        String link = _args[3];
        String v = _args[4];
        if (v == null)
            v = SourceType.DB.name;
        TNode node = GB.getNodes().getNode(path);
        if (node != null) {
            GB.printUsage("Node already exists.");
        }
        mkNode(userid, path, link, SourceType.getType(v));
        GB.print("Node created. ");
    }

    public static TNode mkNode(String userid, String path, String link,
                               SourceType db) {
        NodeManager manager = new NodeManager();
        TNode node = manager.createPath(path, userid, db, link);
        if (node != null) {
            return node;
        } else
            return null;
    }

    public static TNode createNode(String userid, String path, String link,
                                   SourceType db) {
        NodeManager manager = new NodeManager();
        TNode node = manager.createPath(path, userid, db, link);
        return node;
    }

    public static void printNodeDetails(String[] _args) {
        if (_args.length != 2) {
            GB.printUsage("Please provide only a path to a node.");
            return;
        }
        String path = _args[1];
        GBNodes nodes = GB.getNodes();
        TNode node = nodes.getNode(path);
        if (node == null) {
            System.err
                    .println("Search failed: the path does not seem to be correct.");
            return;
        }
        GB.print(node.getName());
        GB.print(node.getCreated_by());
        GB.print(node.getNodeType());
        GB.print(node.getCreatedDate().toGMTString());
        GB.print("");
        GB.print(">>Node-specific information below: ");
        GB.print("");
        GB.print("");
        if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)) {
            GB.print("\tTable");
            String core = GBSolr.getCore(path);
            List<String> cols = GBSolr.getColumnPropertiesAsList(path);
            GB.print("\t\t **Table id " + core);
            GB.print("\t\t   Columns: ");
            for (String s : cols) {
                GB.print("\t\t\t" + s);
            }
        }
        GB.print("");
        GB.print("------------\n\n");
        return;
    }

    public static void rename(String[] _args) {
        if (_args.length < 4) {
            GB.printUsage(GB.RENAME_USAGE);
            return;
        }
        String user_name = _args[1];
        String path = _args[2];
        String toName = _args[3];
        System.out.println(" " + renameNode(path, toName));
    }

    private static String renameNode(String path, String toPath) {

        try {
            String newName = "Unknown";
            path = path.trim();
            toPath = toPath.trim();
            // we need to parse this path object a bit
            if (toPath.contains("/")) {
                if (path.endsWith("/'")) {
                    path = path.substring(0, path.length() - 1);
                }
                if (toPath.endsWith("/")) {
                    toPath = toPath.substring(0, toPath.length() - 1);
                }

                int r1 = path.lastIndexOf('/');
                int r2 = toPath.lastIndexOf('/');

                String ra = path.substring(0, r1);
                String rb = toPath.substring(0, r2);

                if (!ra.equalsIgnoreCase(rb)) {
                    return "The paths are not the same.";
                } else
                    newName = ra.substring(r2 + 1);
            } else
                newName = toPath;

            GBNodes nodes = GB.getNodes();
            System.out.println(nodes.rename(path, newName));
        } catch (Exception _e) {
            _e.printStackTrace();
            return "Rename Faied.";
        }
        return "Rename complete " + path + " -> " + toPath;

    }

    public static String describe(String path) {
        GBNodes node_manager = GB.getNodes();
        if (!path.startsWith("/")) {
            path = GB.pwd() + "/" + path;
        }
        TNode n = node_manager.getNode(path);
        if (n == null) {
            GB.print("Path not found.");
            return "";
        }

        String type = n.getNodeType();
        GBFileManager manager = GB.getGBFileManager();
        GB.print("Name:\t" + n.getName());
        GB.print("User:\t" + n.getCreated_by());
        GB.print("Link:\t" + n.getLink());
        GB.print("Description:\t" + n.getDescription());
        GB.print("Type:\t" + n.getNodeType());
        String link = n.getLink();
        if (link != null) {
            String target = LAC.getTarget(n.getLink());
            GB.print("\tTable: " + target);
            try {
                ArrayList<GColumn> cols = GB.getGBTables().describeCore(target);
                // we need to sort this based on the table properties.
                if (cols == null || cols.size() <= 0) {
                    GB.print("  ");
                    return "No schema defined. ";
                }
                for (GColumn col : cols) {
                    GB.prnt("+\t\t" + col.getType() + " " + col.getName());
                }
                int rows_count = GB.getGBTables().count(target);
                GB.print("Rows: " + rows_count);
                GB.print("\n");
            } catch (ConnectException e) {
                e.printStackTrace();
            }
        } else if (type.equals(SourceType.RAW_FILE.name)) {
            long raw_file_id = GBFileManager.getFileId(n);
            // get the file id
            String desc = manager.getQuickFileDescription(raw_file_id);
            GB.print(desc);
        }
        return "";
    }

    /**
     * This will export the AB object into the database currently there is a
     * limit of 10 000 000 rows.
     *
     * @param path
     * @param database_descriptor
     * @throws NodeWrongTypeException
     * @throws IOException
     */
    public static void exportPath(String path, String database_descriptor)
            throws NodeWrongTypeException, IOException {
        Properties pr = new Properties();
        FileReader reader = new FileReader(database_descriptor);
        try {
            pr.load(reader);

            TNode node = GB.getNodes().getNode(path);
            if (node != null && node.getNodeType() != null) {
                if (node.getNodeType() != null) {
                    if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)) {
                        String lac = node.getLink();
                        GBSearch g = GB.getSearch();
                        g.searchCore(lac, "*:*", 0, 100000000, pr, null,
                                new SearchConfig(SearchConfig.RAW_SEARCH));
                    }
                } else
                    throw new NodeWrongTypeException(path);
            }
        } finally {
            IOUTILs.closeResource(reader);
        }
    }

    /**
     * @throws NotASearchableTableException
     */
    public static ABaseResults searchPath(String _path, String _searchString,
                                          String[] _cols, PrintStream _printStream, SearchConfig mode)
            throws NotASearchableTableException {
        GBSearch se = GB.getSearch();
        if (mode == null)
            mode = new SearchConfig(SearchConfig.RAW_SEARCH);
        if (!_path.endsWith("*")) {
            TNode no = GB.getNodes().getNode(_path);
            if (no != null && (GBSearch.isSearchable(no))) {
                return se.searchTable(_path, _searchString, null, _printStream,
                        _cols, mode);
            } else
                return se.searchPath(_path, _searchString, _printStream);
        } else
            return se.searchPath(_path, _searchString, _printStream);
    }

    /**
     * @param _p
     * @deprecated please use NodeManager.saveNodeProperty(NodeProperty)
     */
    public static void saveNodeProperty(NodeProperty _p) {
        Session session = HBConnect.getSession();
        try {
            long node_id = _p.getNode_id();
            String key = _p.getName();

            session.beginTransaction();
            Criteria c = session.createCriteria(NodeProperty.class);
            c.add(Restrictions.and(Restrictions.eq("node_id", node_id),
                    Restrictions.eq("name", key)));
            List list = c.list();
            if (list == null || list.size() <= 0) {
                session.save(_p);
            } else {
                NodeProperty nodep = (NodeProperty) list.get(0);
                nodep.setProperty(_p.getProperty());
                nodep.setFile(_p.getFile());
                nodep.setType(_p.getType());
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

    /**
     * Load an object into the node tree. e.g. import -type SQL -user=g file.abq
     *
     * @param _args
     * @throws UsageException
     */
    public static void load(String[] _args) throws UsageException {
        // by default we try to load it as a table.
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        _args = GBUtil.parseOptions(options, _args);
        String local_file = _args[1];
        File f = new File(local_file);
        if (f.exists() && (_args.length == 2) && f.isDirectory()) {
            GB.print("\n\t Loading: " + f.getAbsolutePath());
            if (_args[0].equalsIgnoreCase(GB.IMPORT)) {
                String file = _args[1];
                File root = new File(file);
                try {
                    BatchLoader loader = new BatchLoader(root);
                    loader.start();
                } catch (BatchLoaderFailedException e) {
                    e.printStackTrace();
                }


            }

        } else {
            String gb_file = null;
            if (_args.length > 2)
                gb_file = _args[2];
            if (_args[0].equalsIgnoreCase(GB.IMPORT)) {
                importData(options, local_file, gb_file);
            }
            log.debug(_args);
            log.debug("Options that were collected for this. ");
            log.debug(options);
        }
    }


    public static void importData(LinkedHashMap<String, String> options, String local_file, String gb_file) throws UsageException {
        String t = options.get("--type");
        String overlapstr = options.get("--overlap");
        String final_param = options.get("--final");

        System.out.println(" final param " + final_param);


        Integer overlap = 0;
        if (overlapstr != null) {
            try {
                overlap = Integer.parseInt(overlapstr.trim());
            } catch (NumberFormatException ne) {
                GB.print("overlap flag is not a integer value");
                return;
            }
        }
        String u = options.get("--user");
        String annotation = options.get("--annotation");
        if (t == null)
            t = deriveLoadTypeFromFile(local_file);
        if (t == null)
            t = GBFileManager.BINARY;


        GB.print("Loading file as type = " + t);


        if (t.equalsIgnoreCase(GBFileManager.XLSX)) {

            try {
                GBTableLoader.loadXLSX(u, local_file, gb_file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (t.equalsIgnoreCase(GBFileManager.FTP)) {
            FTPImporter.load(local_file);
        } else if (t.equalsIgnoreCase(GBFileManager.S3)) {
            S3Importer.load(local_file, gb_file);
        } else if (t.equalsIgnoreCase(GBFileManager.JSON)) {
            String schema_param = options.get("--schema");
            loadJSON(schema_param, local_file, gb_file);
        } else if (t.equalsIgnoreCase(GBFileManager.BINARY)) {
            loadBinary(u, local_file, gb_file);
        } else if (t.equalsIgnoreCase(GBFileManager.TABLE)) {
            GBTableLoader.load(u, local_file, gb_file, options);
        } else if (t.equalsIgnoreCase(GBFileManager.ABQ)) {
            try {
                GBTableLoader.loadABQ(u, local_file, gb_file, local_file, final_param);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (t.equalsIgnoreCase(GBFileManager.JAR)) {

            //
            GBTableLoader.loadJARIndexer(u, local_file, gb_file);


        } else if (t.equalsIgnoreCase(GBFileManager.FASTA)) {
            try {
                if (annotation == null)
                    annotation = "";
//                import --user=jeff --overlap=25 ./fasta/human-grch38-chr1.fa /human/chr1
                GBTableLoader.loadFASTA(annotation, local_file, gb_file, overlap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (t.equalsIgnoreCase("VALUES")) {
            if (local_file.endsWith(".csv") || local_file.endsWith(".CSV")) {
                options.put("-delim", ",");
            }
            // this will include the row number index.
            // and the column number index
            // no title are added for the columns because this is assumed to
            // be added with the
            // use of meta data tables...
                    /*
                     * gb import -type=floatdata ./localfile.csv
					 * /db/something/mygbfile 1) parse the file structure into a map
					 * 2) determine the delim 3) load the data with types and
					 * sequenced indexes on rows and columns
					 */
            String _delim = options.get("-delim");
            if (_delim == null) {
                _delim = "\t";
                options.put("-delim", "\t");
            }
            try {

                Map<String, String> table_structure = null;
                String header = options.get("-header");
                // if we have a header flag then we need to take care of it.
                if (header != null) {
                    // TODO: IMPLIMENT THE HEADER loading where the file
                    // does not contain a header
                    // String name_value_pair =
                    // GBUtil.parseNameValuePair(header);
                } else {
                    table_structure = createFloatTableStructure(_delim,
                            local_file);
                }
                if (table_structure != null) {
                    GBTableLoader.createAndLoadTable(u, local_file, _delim,
                            gb_file, table_structure,
                            SourceType.VALUE_TABLE);
                } else {
                    GBTableLoader.load(u, local_file, gb_file, options);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (t.equalsIgnoreCase("COLUMNS")) {
            if (local_file.endsWith(".csv") || local_file.endsWith(".CSV")) {
                options.put("-delim", ",");
            }
                    /*
                     * This loads a meta data...and implies that the table is an
					 * annotation support table to other tables.
					 */
            String _delim = options.get("-delim");
            if (_delim == null) {
                options.put("-delim", "\t");
                _delim = "\t";
            }
            try {
                Map<String, String> table_structure = createMetaDataTableStructure(
                        _delim, local_file);

                // {{ ADD THE IGNORE ROWS }}
                ArrayList<Integer> ignore = new ArrayList<Integer>();
                ignore.add(0); // don't load the title row for this file.
                if (table_structure != null) {
                    GBTableLoader.createAndLoadTable(u, local_file, _delim,
                            gb_file, table_structure, ignore,
                            SourceType.COLUMN_METATABLE);
                } else {
                    GBTableLoader.load(u, local_file, gb_file, options);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            GBTableLoader.load(u, local_file, gb_file, options);
        }


    }

    //    import --schema=helm:string_ci,isisno:sint file.json
    private static void loadJSON(String schema_line, String local_file, String gb_file) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("--schema", schema_line);
        LinkedHashMap<String, String> abschema = CreateTableNodeCommand.getSchema(options);
        ABTable abtable = new ABTable(gb_file);
        try {
            abtable.create(abschema);
        } catch (NodeExistsException e) {
            e.printStackTrace();
        }
        File f = new File(local_file);
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(f));
            JSONArray jsonArray = new JSONArray( tokener );
            int length = jsonArray.length();



            ArrayList<LinkedHashMap<String, Object>> setlist = new ArrayList<>();

            for ( int i = 0; i < length; i++)
            {
                JSONObject doc = jsonArray.getJSONObject(i);
                LinkedHashMap<String, Object> row = jsonObjectToMap ( doc );
                setlist.add ( row );
//                abtable.append(row, false);
                if ( i % 5000 == 0) {
                    abtable.append(setlist);
                    abtable.commit();
                    setlist = new ArrayList<>();
                }
            }
            abtable.commit();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private static LinkedHashMap<String,Object> jsonObjectToMap(JSONObject doc) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            Iterator<String> keys = doc.keys();
            while( keys.hasNext() )
            {
                String key = keys.next();
                try {
                    if ( key.equalsIgnoreCase( "helm")){
                        // please remove this conditional.. this is just a temp feature
                        String helmstring = doc.getString(key);
                        helmstring = helmstring.replace('{', ' ');
                        helmstring = helmstring.replace('}', ' ');
                        helmstring = helmstring.replace('[', ' ');
                        helmstring = helmstring.replace(']', ' ');
                        helmstring = helmstring.replace('.', ' ');
                        helmstring = helmstring.replace(',', ' ');
                        helmstring = helmstring.replace('(', ' ');
                        helmstring = helmstring.replace(')', ' ');
                        map.put ( "monomers", helmstring);
                    }
                    map.put ( key, doc.get(key ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return map;

    }


    /**
     * create a table structure mapping with the delim
     *
     * @param delim
     * @param local_file
     * @return
     * @throws IOException
     */
    private static Map<String, String> createFloatTableStructure(String delim,
                                                                 String local_file) throws IOException {
        BufferedReader reader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(local_file);
            reader = new BufferedReader(fileReader);
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IOException("Failed to read the file : " + local_file);
            } else {
                String[] fields = firstLine.split(delim);
                String[] types = new String[fields.length];
                types[0] = "int";
                fields[0] = 1 + "";
                for (int i = 1; i < fields.length; i++) {
                    types[i] = "double";
                    fields[i] = (i + 1) + "";
                }
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                for (int i = 0; i < fields.length; i++) {
                    String field = fields[i];
                    String type = types[i];
                    map.put("f" + field, type);
                }
                return map;
            }
        } finally {
            IOUTILs.closeResource(reader);
            IOUTILs.closeResource(fileReader);
        }
    }

    private static Map<String, String> createMetaDataTableStructure(
            String delim, String local_file) throws IOException {
        BufferedReader reader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(local_file);
            reader = new BufferedReader(fileReader);
            String firstLine = reader.readLine();
            reader.close();
            if (firstLine == null) {
                throw new IOException("Failed to read the file : " + local_file);
            } else {
                String[] fields = firstLine.split(delim);
                String[] types = new String[fields.length];
                types[0] = "int";
                for (int i = 1; i < fields.length; i++) {
                    types[i] = "string";
                }
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                for (int i = 0; i < fields.length; i++) {
                    String field = fields[i];
                    String type = types[i];
                    map.put(field, type);
                }
                return map;
            }
        } finally {
            IOUTILs.closeResource(reader);
            IOUTILs.closeResource(fileReader);
        }
    }

    private static String deriveLoadTypeFromFile(String local_file) {
        if (local_file.startsWith("ftp://")) {
            return GBFileManager.FTP;
        } else if (local_file.startsWith("https://s3.amazon")) {
            return GBFileManager.S3;
        }
        if (local_file.toLowerCase().endsWith(".xlsx"))
            return GBFileManager.XLSX;
        else if (local_file.endsWith("abq"))
            return GBFileManager.ABQ;
        else if (local_file.endsWith("txt") || local_file.endsWith("csv"))
            return GBFileManager.TABLE;
        else if (local_file.endsWith("json") || local_file.endsWith("JSON"))
            return GBFileManager.JSON;
        else if (local_file.endsWith("jar") || local_file.endsWith("JAR"))
            return GBFileManager.JAR;
        else if (local_file.endsWith("fasta") || local_file.endsWith("fa"))
            return GBFileManager.FASTA;
        else
            return GBFileManager.BINARY;
    }


    public static String describe(String[] _args) {
        if (_args == null) {
            GB.print("Please provide a path");
            return "";
        }
        String path = _args[1];
        return describe(path);
    }

    /**
     * Search a path with the given table, field and search term. within the
     * index result range.
     *
     * @param _path
     * @param search_string
     * @param fields
     * @param _printStream
     * @param start
     * @param increment
     * @throws SearchPathFailedExeption
     */
    public static ABaseResults searchPath(String _path, String search_string,
                                          String[] fields, PrintStream _printStream, int start,
                                          int increment, String sort, SearchConfig config)
            throws SearchPathFailedExeption {
        GBSearch se = GB.getSearch();
        if (!_path.endsWith("*")) {
            try {
                return se.searchTable(_path, search_string, _printStream,
                        fields, start, increment, sort, config);
            } catch (NotASearchableTableException e) {
                e.printStackTrace();
            }
        } else
            throw new SearchPathFailedExeption();
        return null;
    }

    /**
     * Search a path with the given table, field and search term. within the
     * index result range.
     *
     * @param _path
     * @param search_string
     * @param fields
     * @param _printStream
     * @param start
     * @param increment
     * @throws SearchPathFailedExeption
     * @throws NotASearchableTableException
     */
    public static ABaseResults searchPath(TNode _path, String search_string,
                                          String[] fields, PrintStream _printStream, int start,
                                          int increment, String sort, SearchConfig config)
            throws SearchPathFailedExeption, NotASearchableTableException {
        GBSearch se = GB.getSearch();
        return se.searchTable(_path, search_string, _printStream, fields,
                start, increment, sort, config);
    }

    public static List<TNode> getRefNodes(String path) {
        GBNodes nod = GB.getNodes();
        return nod.getReferenceNodes(path);
    }

    public List<TNode> getReferenceNodes(String path) {
        List<TNode> n = node.getRefNodes(path);
//        for (TNode nn : n) {
//            System.out.println(" nn " + nn.getName());
//        }
        return n;
    }

    public TNode createNode(String _userid, String to, String link,
                            String nodeType) {
        return createNode(_userid, to, link, SourceType.getType(nodeType));
    }

    /**
     * Command to appeend a tab delimited file to an existing index. This is
     * particulary tricky.
     *
     * @param _args
     * @throws UsageException
     * @throws IOException
     */
    public static void append(String[] _args) throws UsageException,
            IOException {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        _args = GBUtil.parseOptions(options, _args);
        String local_file = _args[1];
        String gb_file = _args[2];
        if (_args[0].equalsIgnoreCase(GB.IMPORT_APPEND)) {
            String t = options.get("-type");
            if (t == null)
                t = deriveLoadTypeFromFile(local_file);
            if (t == null) {
                t = "table";
                options.put("-type", "table");
            }

            GB.print(" Appending file as type = " + t);

            // {{ first check the node }}
            NodeManager manager = new NodeManager();
            TNode node = manager.getNode(gb_file);
            if (node == null) {
                throw new UsageException(
                        "[=]Need to provide a path to a node that exists and has the same schema as the file");
            }
            // {{ MAKE SURE THE CORE IS ACTIVE }}
            String core_name = TMSolrServer.getCore(gb_file);
            if (core_name == null) {
                throw new UsageException(
                        "[+]Need to provide a path to a node that exists and has the same schema as the file: "
                                + core_name);
            }

            String delim = "\t";
            String delim_option = options.get("-delim");
            if (delim_option != null)
                delim = delim_option;

            if (local_file.endsWith(".csv") || local_file.endsWith(".CSV")) {
                delim = ",";
            }

            // {{ NOW WE NEED TO COMPARE THE FILE WITH THE SCHEMA }}
            ArrayList<GColumn> cols = GB.getGBTables().describeTable(node);
            for (GColumn col : cols) {

            }
            // {{ CHECK THE FIRST LINE OF THE IMPORT FILE }}
            Map<GColumn, Integer> cols_map = checkFile(local_file, cols, delim);
            GBTableLoader.append(gb_file, local_file, cols_map, delim);

        }
        log.debug(_args);
        log.debug("Options that were collected for this. ");
        log.debug(options);

    }

    private static LinkedHashMap<GColumn, Integer> checkFile(String local_file,
                                                             ArrayList<GColumn> cols, String delim) throws IOException,
            UsageException {

        BufferedReader reader = null;
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(local_file);
            reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            if (line == null)
                throw new UsageException("You need "
                        + "to provide a title line (tab delimited) "
                        + "that labels the columns and that match"
                        + " the schema titles you wish to import into." + ";");
            String[] apsch = line.split(delim);

            for (int i = 0; i < apsch.length; i++) {
                apsch[i] = apsch[i].trim();
            }

            if (apsch.length != cols.size()) {
                throw new UsageException("The format of the file : " + local_file
                        + " is incorrect.\n\n  " + apsch.length
                        + " columns in the file" + " and " + cols.size()
                        + " columns in the schema.");
            }
            LinkedHashMap<GColumn, Integer> map = new LinkedHashMap<GColumn, Integer>();
            int col_index = 0;
            for (String col : apsch) {
                boolean found = false;
                for (GColumn c : cols) {
                    if (c.getName().equalsIgnoreCase(col.trim())) {
                        found = true;
                        map.put(c, new Integer(col_index));
                    }
                }
                if (!found) {
                    throw new UsageException(
                            "At least one column title needs to match a field in the schema. "
                                    + "Col " + col
                                    + " was not found in the schema.");
                }
                col_index++;
            }
            Set<GColumn> keys = map.keySet();
            GB.print("\nAppending:\n");
            for (GColumn s : keys) {
                int index = map.get(s);
                GB.print("\t\t" + s.getName() + " --> " + index);
            }
            return map;
        } finally {
            IOUTILs.closeResource(reader);
            IOUTILs.closeResource(fileReader);
        }
    }

    public static void attach(String[] _args) throws UsageException,
            ConnectException {
        // gb append localfile.txt /arraybase/file where row_num=row_num
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        _args = GBUtil.parseOptions(options, _args);
        String local_file = _args[1];
        String gb_file = _args[2];

        boolean f = false;
        String where_clause = null;

        for (String arg : _args) {
            if (f) {
                where_clause = arg;
            }
            if (arg.equalsIgnoreCase("where"))
                f = true;
        }

        // if we are going to attach a file or a abq script
        if (_args[0].equalsIgnoreCase(GB.IMPORT_ATTACH)) {
            if (local_file.toLowerCase().endsWith(".abq")) {
                attachABQFile(options, local_file, gb_file, where_clause);
            } else {
                attachFlatFile(options, local_file, gb_file, where_clause);
            }

        }
    }

    // gb attach local_file gb_file where local_file.field=gb_file.field
    // gb attach local_file gb_file where local_file.field=gb_file.field
    // gb attach local_file gb_file where local_file.field=gb_file.field
    // gb attach local_file gb_file where local_file.field=gb_file.field
    private static void attachABQFile(LinkedHashMap<String, String> options,
                                      String local_file, String gb_file, String where_clause)
            throws ConnectException, UsageException {
        // {{ NOW WE NEED TO COMPARE THE FILE WITH THE SCHEMA }}
        // ArrayList<GColumn> cols = GB.getGBTables().describe(core_name);
        GBAttachABQ gb = verifyABQFileCanBeAttached(local_file, gb_file,
                where_clause);
        // now we need to attach the file
        // this could be a long running process...
        // we should probbably do somelthing smart about this.
        gb.executeAttachProcess();

    }

    private static GBAttachABQ verifyABQFileCanBeAttached(String local_file,
                                                          String gb_file, String where_clause) throws UsageException {

        String[] sp = where_clause.split("=");
        if (sp == null || sp.length != 2) {
            throw new UsageException(
                    " Please provide a where clause of the form local_file_row"
                            + "=gb_file_row.  Note that there are no spaces around the equals sign.");
        } else {
            GBAttachABQ ref = new GBAttachABQ(gb_file, where_clause);
            String left = sp[0];
            String right = sp[1];
            ref.setLeft(left);
            ref.setRight(right);
            try {
                File local_filef = new File(local_file);
                if (!local_filef.exists()) {
                    throw new FileNotFoundException(
                            local_filef.getAbsolutePath());
                }
                Properties p = new Properties();
                FileReader reader = new FileReader(local_filef);
                try {
                    p.load(reader);
                } finally {
                    reader.close();
                }

                // dumb... but we need to convert this to map
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                Set<Object> keys = p.keySet();
                for (Object k : keys) {
                    map.put(k.toString(), p.getProperty(k.toString()));
                }
                String core_name = TMSolrServer.getCore(gb_file);

                // 1. Set the column identies
                String exp = map.get(ABQFile.EXPORT_FIELDS);
                String[] exported_values = exp
                        .split(ABQFile.EXPORT_FIELDS_DELIM);

                ArrayList<GColumn> abq_cols = SQLManager.desc(map,
                        exported_values);
                ref.setLeftColumns(abq_cols);

                ArrayList<GColumn> cols = GB.getGBTables().describeCore(
                        core_name, null);
                ref.setRightColumns(cols);

                // 2. set the properties file for the abq execute process
                ref.setAbq(map);

                return ref;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new UsageException(e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                throw new UsageException(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Attach the flat file to the gb object.
     *
     * @param options
     * @param local_file
     * @param gb_file
     * @param where_clause
     * @throws ConnectException
     * @throws UsageException
     */
    private static void attachFlatFile(LinkedHashMap<String, String> options,
                                       String local_file, String gb_file, String where_clause)
            throws ConnectException, UsageException {

        String delim_option = options.get("-delim");
        GB.print(" Attaching fields from file : " + local_file);
        String delim = "\t";
        if (delim_option != null)
            delim = delim_option;
        else if (local_file.endsWith(".csv") || local_file.endsWith(".CSV")) {
            delim = ",";
        }

        String core_name = TMSolrServer.getCore(gb_file);
        // {{ NOW WE NEED TO COMPARE THE FILE WITH THE SCHEMA }}
        ArrayList<GColumn> cols = GB.getGBTables()
                .describeCore(core_name, null);
        GBTableAttach gb = verifyFileCanBeAttached(local_file, delim, cols,
                where_clause);
        // {{ APPEND THE SCHEMA }}
        ArrayList<GColumn> gclist = gb.getColumns();

        TableManager manager = new TableManager(GB.getConnectionManager());
        String core = GBSolr.getCore(gb_file);

        for (GColumn c : gclist) {
            String type = c.getType();
            String field = c.getName();
            manager.addColumn(core, field, type);
        }

        // {{ LOAD THE FILE }}
        GBTableLoader.insert(local_file, delim, gb_file, cols, gb);
    }

    private static GBTableAttach verifyFileCanBeAttached(String local_file,
                                                         String delim, ArrayList<GColumn> cols, String where_clause)
            throws UsageException {

        String[] sp = where_clause.split("=");
        if (sp == null || sp.length != 2) {
            throw new UsageException(
                    " Please provide a where clause of the form local_file_row"
                            + "=gb_file_row.  Note that there are no spaces around the equals sign.");
        } else {

            GBTableAttach ref = new GBTableAttach();
            String left = sp[0];
            String right = sp[1];
            ref.setLeft(left);
            ref.setRight(right);

            FileReader fileReader = null;
            BufferedReader reader = null;
            try {
                fileReader = new FileReader(local_file);
                reader = new BufferedReader(fileReader);
                String title_line = reader.readLine();
                String type_line = reader.readLine();
                String[] titles = title_line.split(delim);
                String[] types = type_line.split(delim);
                int data_start_index = 1;

                boolean aretypes = true;
                for (String t : types) {
                    if (!Parse.isAType(t))
                        aretypes = false;
                }
                if (!aretypes) {
                    GB.print("Looks like the second row is not a type column.  We will load these as data.");
                    types = new String[titles.length];
                    for (int i = 0; i < types.length; i++) {
                        types[i] = "string";
                    }
                    GB.print("All v8alues loading as string types.");
                } else {
                    data_start_index = 2;
                }
                ref.setTitles(titles);
                ref.setTypes(types);
                ref.setStartLoadingFromRow(data_start_index);

                return ref;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new UsageException(e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                throw new UsageException(e.getLocalizedMessage());
            } finally {
                IOUTILs.closeResource(reader);
                IOUTILs.closeResource(fileReader);
            }
        }
    }

    public static boolean hasChildren(TNode _node) {
        return _node.getReference() != null && _node.getReference().size() > 0;
    }

}
