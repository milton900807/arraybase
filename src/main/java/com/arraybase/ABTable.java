package com.arraybase;

import com.arraybase.db.NodeExistsException;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.flare.ABFileInputDocument;
import com.arraybase.flare.TMID;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.modules.BuildTableFromABQFile;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.*;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.google.gson.Gson;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ABTable {

    private String url = null;
    private String path = null;
    private HttpSolrClient solr = null;


    public ABTable(String path) {
        this.path = path;
        String _url = TMSolrServer.getCore(path);
        if (_url == null) {
            GB.print(" Failed to find the link for path : " + path);
            return;
        }
        url = buildURL(_url);
        solr = new HttpSolrClient.Builder(url).build();
    }

    public HttpSolrClient getHttpClient() {
        return this.solr;
    }

    public void setDataLink(String _data) {
        url = _data;
        GBNodes nodes = GB.getNodes();
        GBNodes n = GB.getNodes();
        TNode node = n.getNode(path);
        if (node != null)
            node.setLink(url);
        GB.print(path + " points to " + url);
        nodes.save(node);
    }

    public String getDataLink() {
        return url;
    }


    /**
     * Refresh the node using the available Gen objects (see NodeProperty.NODE_GENERATOR)
     */
    public void refresh() {
        TNode node = GB.getNodes().getNode(path);
        long node_id = node.getNode_id();

        Map<String, String> nps = NodeManager.getNodePropertyMap(node_id);
        String json = nps.get(NodeProperty.NODE_GENERATOR);
        Gson g = new Gson();


        Map installer = g.fromJson(json, Map.class);
        if (installer == null) {
            GB.print("Configuration error.... it looks like there isn't a prop configuration available to permit you to reload this node.  ");
        }
//        Set keys = installer.keySet();
//        LinkedHashMap<String, Object> reb = new LinkedHashMap<String, Object>();
        String type = null;

        Object module_object = installer.get("MODULE");
        if (module_object != null && module_object.toString().equalsIgnoreCase("com.arraybase.modules.BuildTableFromABQFile")) {
            BuildTableFromABQFile abq = new BuildTableFromABQFile();
            try {
                abq.exec(installer);
            } catch (UsageException e) {
                e.printStackTrace();
            }
        }
    }


    public Map<String, String> getSchema() throws NodeNotFoundException {
        HashMap<String, String> schema = new HashMap<String, String>();
        try {
            if (!exists())
                throw new NodeNotFoundException(path);
            ArrayList<GColumn> fields = getFields();
            for (GColumn g : fields) {
                schema.put(g.getName(), g.getType());
            }
        } catch (NodeWrongTypeException e) {
            e.printStackTrace();
            throw new NodeNotFoundException(path);
        }
        return schema;
    }


    public ABTable copy(String _path) throws NodeExistsException, NodeNotFoundException {
        ABTable nt = new ABTable(_path);
        nt.create(getSchema());
        return nt;
    }


    public void create(Map<String, String> schema) throws NodeExistsException {
        try {
            if (exists())
                throw new NodeExistsException();
        } catch (NodeWrongTypeException e) {
            e.printStackTrace();
            throw new NodeExistsException();
        }
        AB.createTable(path, schema);
        String _url = TMSolrServer.getCore(path);
        url = buildURL(_url);
        GB.print("\n\n\n\n\n table created with core : " + url + "\n\n\n\n\n");
        solr = new HttpSolrClient.Builder(url).build();
    }

    public void recreate(Map<String, String> schema) {
        delete();
        try {
            create(schema);
        } catch (NodeExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String buildURL(String _url) {

        if (_url.startsWith("http://")) {
            url = _url;
            return url;
        } else {
            String solr_url = GB.getDefaultURL() + _url;
            return solr_url;
        }
    }

    // addField(ABTableTypes.SORTED_INT, "mtid");
    public void addField(ABFieldType type, String _name) {
        TableManager tm = GB.getTableManager();
        tm.addColumn(url, _name, type.getType());
    }

    public ABaseResults search(String q, String[] cols, int start, int total) {
        return ABaseNode.get(path, q, cols, start, total, solr);
    }

    public void insert(String search_string, String field, final Object value) {
        GBSearch gs = GB.getSearch();
        String sortString = null;
        try {

            if (solr == null) {
                reconnect();
            }


            solr.setUseMultiPartPost(true);
            String[] cols = {"TMID", field};
            Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
                    .searchAndDeploy(path, search_string, sortString, cols,
                            new SearchConfig(SearchConfig.RAW_SEARCH));
            int index = 0;
            while (it.hasNext()) {
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                ArrayList<LinkedHashMap<String, Object>> ali = it.next();
                for (LinkedHashMap<String, Object> row : ali) {
                    SolrInputDocument doc = new SolrInputDocument();
                    String id = (String) row.get("TMID");
                    Object object = row.get(field);

                    // System.out.println ( " object : "+ object.toString() +
                    // " and " + value.toString() );
                    if (object == null
                            || (!value.toString().equalsIgnoreCase(
                            object.toString()))) {// only
                        // add
                        // if
                        // different
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("set", value + "");
                        doc.addField("TMID", id);
                        doc.addField(field, map);
                        doc.addField("TMID_lastUpdated", new Date());
                        GB.print(search_string + " set " + field + ":" + value
                                + " ");
                        docs.add(doc);
                    }
                }
                try {
                    if (docs.size() > 0) {
                        solr.add(docs);
                        index += docs.size();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // if we get a lot of adds without commit.. go ahead and commit.
                if (index > 500000) {
                    index = 0;
                    solr.commit();
                }

            }

            solr.commit();

        } catch (NotASearchableTableException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void reconnect() {
        String _url = TMSolrServer.getCore(path);
        if (_url == null) {
            GB.print(" Failed to find the link for path : " + path);
            return;
        }
        url = buildURL(_url);
        GB.print("Connecting to solr client : " + url);
        this.solr = new HttpSolrClient.Builder(url).build();
    }


    /**
     * Search for values and set the value for the field of the results
     *
     * @param search
     * @param field
     * @param value
     */
    public void qSet(String search, String field, String value) {
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put(field, value);
        update(search, values);
    }


    public void set(String tmid, String field, final Object value, boolean b) {
        if (solr == null) {
            reconnect();
        }

        try {

            SolrInputDocument doc = new SolrInputDocument();
            Map<String, Object> map = new HashMap<String, Object>();
            GB.print(" Set: " + field + " -> " + value);
            map.put("set", value + "");
            doc.addField("TMID", tmid);
            doc.addField(field, map);
            doc.addField("TMID_lastUpdated", new Date());
            GB.print(doc.toString());
//            GB.print(" Set: " + field + " -> " + value);
            solr.add(doc);

            if (b)
                solr.commit();

        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void update(String search_string, final Map<String, Object> values) {

        if (solr == null) {
            reconnect();
        }
        String sortString = null;
        try {
            Set<String> fields = values.keySet();
            Iterator<String> itt = fields.iterator();
            String[] cols = new String[fields.size() + 1];
            int index = 0;
            while (itt.hasNext()) {
                String field = itt.next();
                cols[index++] = field;
            }
            cols[index] = "TMID";
            Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
                    .searchAndDeploy(path, search_string, sortString, cols,
                            new SearchConfig(SearchConfig.RAW_SEARCH));
            if (it == null || (!it.hasNext())) {
                {
                    append(values, true);
                }
            } else {
                int not_updated = 0;
                int updated_count = 0;
                int count = 0;
                while (it.hasNext()) {
                    ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                    ArrayList<LinkedHashMap<String, Object>> ali = it.next();

                    for (LinkedHashMap<String, Object> row : ali) {
                        count++;
                        Set<String> keys = values.keySet();
                        for (String key : keys) {
                            Object value = values.get(key);
                            String id = (String) row.get("TMID");
                            Object object = row.get(key);

                            if (object == null
                                    || (!value.toString().equalsIgnoreCase(
                                    object.toString()))) {// only
                                // add
                                // if
                                // different
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("set", value + "");
                                SolrInputDocument document = new SolrInputDocument();

                                document.addField("TMID", id.trim());
                                document.addField("TMID_lastUpdated", new Date());
                                document.addField(key, map);
                                // GB.print("\tUpdating table \t TMID: " + id
                                // + " Field:" + field + ":" + value +
                                // " document count:"
                                // + docs.size());
                                docs.add(document);
                            } else {
                                // GB.print("\tNOT Updating table with : "
                                // + search_string + " set " + field + ":"
                                // + value + " " + docs.size()
                                // + " as the value is the same.");
                                not_updated++;
                            }
                        }
                    }
                    try {
                        if (docs.size() > 0) {
                            solr.add(docs);
                            solr.commit();
                            updated_count += docs.size();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    GB.print("Count : " + count);
                    GB.print("\tTotal updated : " + updated_count);
                    GB.print("\tTotal skipped : " + not_updated);

                }
            }
        } catch (NotASearchableTableException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean hasField(String node) {
        try {
            ArrayList<GColumn> cols = GBTables.describeTable(path);

            for (GColumn gc : cols) {
                if (node.equalsIgnoreCase(gc.getName()))
                    return true;
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Append the table with the documention as a map
     *
     * @param distinct
     */
    public void append(Map<String, Object> constants, Map<String, Object> distinct, boolean _commit) {

        if (solr == null) {
            reconnect();
        }


        SolrInputDocument doc = new SolrInputDocument();
        Set<String> keys = distinct.keySet();
        for (String k : keys) {
            Object value = distinct.get(k);
            if (k.equalsIgnoreCase("_version_")) {
                // do not add the version number if there is one.

            } else {

                if (value != null && value.toString().length() > 0) {
                    doc.addField(k, value);
                    Set<String> ckeys = constants.keySet();
                    for (String c : ckeys) {
                        Object cvalue = constants.get(k);
                        doc.addField(c, cvalue);
                    }
                }

            }
            try {
                solr.add(doc);
                if (_commit)
                    solr.commit();
            } catch (SolrServerException se) {
                se.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getUniqueFieldObject(String search, String field) {
        ABaseResults results = search(search, new String[]{"" + field}, 0, 1);
        ArrayList<GRow> row = results.getValues();
        if (row == null || row.size() <= 0) {
            return null;
        }
        GRow r = row.get(0);
        HashMap map = r.getData();
        return map.get(field);
    }


    public GRow searchForFirstRow(String search) {
        ABaseResults row = search(search, null, 0, 1);
        List<GRow> r = row.getValues();

        if (r.size() > 0)
            return r.get(0);
        else
            return null;
    }

    public GRow searchForFirstRow(String field, String str) {
        ABaseResults row = search(field + ":" + str, null, 0, 1);
        List<GRow> r = row.getValues();

        if (r.size() > 0)
            return r.get(0);
        else
            return null;
    }

    public String searchForFirstRowValue(String str, String field_to_return) {
        ABaseResults row = search(str, null, 0, 1);
        List<GRow> r = row.getValues();

        if (r.size() > 0) {

            GRow grow = r.get(0);
            Map data = grow.getData();
            if (data != null && data.size() > 0) {
                Object ob = data.get(field_to_return);
                if (ob != null)
                    return ob.toString();
            }

        }
        return null;
    }


    /**
     * Append the table with the as a map
     *
     * @param map
     */

    public void append(Map<String, Object> map, boolean _commit) {

        if (solr == null) {
            reconnect();
        }


        SolrInputDocument doc = new SolrInputDocument();
        Set<String> keys = map.keySet();
        for (String k : keys) {
            Object value = map.get(k);
            if (k.equalsIgnoreCase("_version_")) {
                // do not add the version number if there is one.

            } else {

                if (value != null && value.toString().length() > 0)
                    doc.addField(k, value);

            }

        }
        try {

            doc.setField("TMID", TMID.create());
            doc.setField("TMID_lastUpdated", new Date());
            solr.setUseMultiPartPost(true);


            solr.add(doc);
            if (_commit)
                solr.commit();
        } catch (SolrServerException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Append the table with the documention as a map
     */
    public void commit() {
        try {
            if (solr == null) {
                reconnect();
            }

            solr.commit();
        } catch (SolrServerException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exists() throws NodeWrongTypeException {
        GBNodes n = GB.getNodes();
        TNode node = n.getNode(path);
        if (node == null)
            return false;

        if (!existInSolr()) {
            return false;
        }


        if (GBTables.isTable(node))
            return true;
        throw new NodeWrongTypeException("Now was found but is not a table. ");
    }

    private boolean existInSolr() {
        String _url = TMSolrServer.getCore(path);
        if (_url == null) {
            GB.print(" Failed to find the link for path : " + path);
            return false;
        }
        url = buildURL(_url);

        HttpURLConnection connection = null;
        try {
            //Create connection
            String test_url = this.url;
            if (!test_url.endsWith("/select?q=*:*")) {
                test_url = test_url + "/select?q=*:*"; // new version of solr requires this.
            }

            URL url = new URL(test_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            GB.print("GET rsp" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
//                GB.print(response.toString());
                return true;
            } else {
                GB.print("Failed to find the solr core " + url);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    public ArrayList<GColumn> getFields() {
        ArrayList<GColumn> cols = new ArrayList<GColumn>();
        try {
            cols = GBTables.describeTable(path);
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        return cols;
    }


    public void append(ArrayList<LinkedHashMap<String, Object>> maplist) {

        if (solr == null) {
            reconnect();
        }


        ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        for (Map<String, Object> map : maplist) {
            SolrInputDocument doc = new SolrInputDocument();
            Set<String> keys = map.keySet();
            for (String k : keys) {
                if (k != null)
                    k = k.trim();
                if (k.equalsIgnoreCase("_version_")) {
                    // do not add the version number if there is one.

                } else {
                    Object value = map.get(k);
                    if (value != null && value.toString().length() > 0)

                        doc.addField(k.trim(), value);
                }
            }
            doc.setField("TMID", TMID.create());
            doc.setField("TMID_lastUpdated", new Date());
            docs.add(doc);
        }
        try {

            if (solr == null) {
                reconnect();
            }


            solr.add(docs);
            solr.commit();
            GB.printSub(docs.size() + " appended to " + this.path);
            GB.print("");
        } catch (SolrServerException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void append(Iterator<ArrayList<LinkedHashMap<String, Object>>> itt) {
        while (itt.hasNext()) {
            ArrayList<LinkedHashMap<String, Object>> rows = itt.next();
            append(rows);
        }
    }

    public String getUrl() {
        return url;
    }

    public void append(LinkedHashMap<String, Object> list) {
        ArrayList<LinkedHashMap<String, Object>> alist = new ArrayList<LinkedHashMap<String, Object>>();
        alist.add(list);
        append(alist);
    }

    public Iterator<ArrayList<LinkedHashMap<String, Object>>> iterate(
            String field) throws NotASearchableTableException,
            NodeNotFoundException {
        String[] cols = {field};
        GBSearch gs = GB.getSearch();
        Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
                .searchAndDeploy(path, "*:*", null, cols,
                        new SearchConfig(SearchConfig.RAW_SEARCH));
        return new ABFieldIterator(it, field);
    }

    public static void main(String[] args) {

//		String t = "[hello][world]this is a test[is][a][test]";
//		ArrayList<String> list = new ArrayList<String>();
//		String name = GBIO.parseFieldNames(t, list);
//		for (String f : list) {
//			System.out.println(" f : " + f);
//		}


        ABTable table = new ABTable("/temp/sc");
        table.refresh();


    }

    public void delete(String q) {
        try {
            GB.deleteTableByQuery(path, q);
        } catch (UpdateIndexFailed e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the table and unload all data.
     */
    public void delete() {
        String core = TMSolrServer.getCore(path);
        if (core != null) {
            TableManager.delete(core);
        }
        NodeManager manager = new NodeManager();
        GBNodes n = GB.getNodes();
        TNode node = n.getNode(path);
        if (node != null) {
            manager.removePath(path);
        }
    }

    public void replace(String search_string, String sortString, String field,
                        String from, String to) {
        if (solr == null) {
            reconnect();
        }

        try {
            String[] cols = {field, "TMID"};
            Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
                    .searchAndDeploy(path, search_string, sortString, cols,
                            new SearchConfig(SearchConfig.RAW_SEARCH));
            int not_updated = 0;
            int updated_count = 0;
            int count = 0;
            while (it.hasNext()) {
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                ArrayList<LinkedHashMap<String, Object>> ali = it.next();
                for (LinkedHashMap<String, Object> row : ali) {
                    count++;
                    String id = (String) row.get("TMID");
                    Object object = row.get(field);
                    if (object != null) {
                        if (object instanceof String) {
                            String objects = (String) object;
                            String new_value = objects.replace(from, to);
                            if (!objects.equals(new_value)) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("set", new_value);
                                SolrInputDocument document = new SolrInputDocument();
                                document.addField("TMID", id.trim());
                                document.addField("TMID_lastUpdated",
                                        new Date());
                                document.addField(field, map);
                                docs.add(document);
                            } else
                                not_updated++;
                        } else
                            not_updated++;
                    } else {
                        not_updated++;
                    }
                }
                try {
                    if (docs.size() > 0) {
                        solr.add(docs);
                        solr.commit();
                        updated_count += docs.size();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            GB.print("\tTotal updated : " + updated_count);
            GB.print("\tTotal skipped : " + not_updated);

        } catch (NotASearchableTableException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }

    }



    public void create(LinkedHashMap<String, Map<String, String>> solr_config) throws PathNotFoundException {
        if (path == null || path.length() == 0)
            throw new PathNotFoundException(" Path was null ");
        String url = AB.createTable(path, solr_config);

        url = buildURL(url);
        GB.print("\n\n\n\n\n table created with core : " + url + "\n\n\n\n\n");
        solr = new HttpSolrClient.Builder(url).build();
    }

    public void add(ArrayList<ABFileInputDocument> docs) {


    }

    public int getDocSize() {
        ABaseResults res = ABaseNode.get(path, "*:*", null, 0, 10, solr);
        return res.getTotalHits();
//        {
//            "responseHeader":{
//            "status":0,
//                    "QTime":2,
//                    "params":{
//                "q":"*:*",
//                        "_":"1541436742224"}},
//            "response":{"numFound":1933,"start":0,"docs":[
//            {

    }
}
