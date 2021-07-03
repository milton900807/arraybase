package com.arraybase.flare;

import com.arraybase.*;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.lac.LAC;
import com.arraybase.search.ABaseResults;
import com.arraybase.tab.field.FieldNotFoundException;
import com.arraybase.tm.*;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.schema.TrieDateField;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.*;
import java.util.*;

import static com.arraybase.tm.ResultsFactory.buildResults;

public class TMSolrServer {
    private String server = null;
    private static GBLogger log = GBLogger.getLogger(TMSolrServer.class);

    public TMSolrServer(String _server) {
        server = _server;
    }


    private static String getDictionaryFieldLine(String dict, String field) {
        String sd = "<dictionaryField name=\"" + field + "\" uri=\"" + dict
                + "\"> </dictionaryField>\n";
        return sd;
    }

    public static boolean callSolr(String url) throws SolrCallException {
        URL u = null;
        HttpURLConnection uc = null;
        BufferedReader in = null;
        try {
            System.out.println("\n\nURL : " + url);
            log.debug("\n\n : ");
            u = new URL(url);

            uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("GET");
            uc.setUseCaches(false);
            in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                log.debug(inputLine);
            in.close();
            log.debug("\n\n : ");
        } catch (MalformedURLException e) {
            log.error("\n\n failed... : ");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {

            IOUTILs.closeResource(in);
            uc.disconnect();
        }
        return true;
    }

    public static boolean callSolrAction(String server, String action,
                                         Map<String, String> params) throws SolrCallException {
        HttpURLConnection uc = null;
        BufferedReader in = null;
        try {
            URL u = null;
            if (action == null) {
                throw new SolrCallException(
                        "No action defined in the solr call " + action);
            }
            action = action.trim();
            String url = server;
            String paramlist = buildParamList(params);
            url = url.trim();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += "admin/cores?action=" + action + "&" + paramlist;
            log.debug("\n\nURL : " + url);
            log.debug("\n\n : ");
            u = new URL(url);
            uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("GET");
            uc.setUseCaches(false);
            in = new BufferedReader(new InputStreamReader(
                    uc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                log.debug(inputLine);
            log.debug("\n\n : ");
        } catch (MalformedURLException e) {
            log.error("\n\n failed... : ");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUTILs.closeResource(in);
            uc.disconnect();
        }
        return true;
    }

    /**
     * Build a string that represents the url parameter list
     *
     * @param params
     * @returnm
     */
    private static String buildParamList(Map<String, String> params) {
        String p = "";
        Set<String> pset = params.keySet();
        for (String v : pset) {
            p += "" + v + "=" + params.get(v) + "&";
        }
        // clip the end
        if (p.endsWith("&")) {
            p = p.substring(0, p.lastIndexOf("&"));
        }
        return p;
    }

    public static boolean post(String url, String urlParameters)
            throws SolrCallException {
        URL u = null;
        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        BufferedReader in = null;
        try {
            log.debug("\n\nURL : " + url);
            u = new URL(url);

            connection = (HttpURLConnection) u.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);

            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                log.debug(inputLine);

            log.debug("\n\n : ");
        } catch (MalformedURLException e) {
            log.error("\n\n failed... : ");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            IOUTILs.closeResource(wr);
            IOUTILs.closeResource(in);
            connection.disconnect();
        }
        return true;
    }

    /**
     * @deprecated
     */
    protected static Boolean createConfFolder(String indexPath) {
        String solrRoot = ABProperties.get("solrRoot");
        log.debug("solrRoot: " + solrRoot);
        if (!solrRoot.endsWith(File.separator))
            solrRoot += File.separator;
        /*
         * if (solrRoot==null) return false; String fldr = solrRoot + indexName
		 * + File.separator;
		 */
        String fldr = solrRoot + indexPath;
        if (fldr.indexOf("\\") > 0) {
            fldr = fldr.replaceAll("\\\\\\\\", "\\\\"); // first revert any
            // doubles into singles
            // to ensure we get all
            // doubles in the end
            fldr = fldr.replaceAll("\\\\", "\\\\\\\\"); // make singles doubles
        }
        try {
            File dir = new File(fldr);
            if (!dir.exists())
                dir.mkdirs();
            else {
                dir.delete();
                dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!fldr.endsWith(File.separator))
            fldr += File.separator;

        File fff = new File(solrRoot + "conf.zip");
        if (!fff.exists()) {
            FileUnzipper.unzip("conf.zip", fldr);
        } else
            FileUnzipper.unzip(solrRoot + "conf.zip", fldr);

        return true;

    }

    public String getServer() {
        return server;
    }

    public static GResults search(String _server, String _schema,
                                  String _searchString, int _start, int _rows, SearchConfig mode) {
        return search(_server, _schema, _searchString, _start, _rows,
                "TMID_lastUpdated desc", null, mode);
    }

    public static String getCore(String _path) {
        // {{ GET THE NODE OBJECT AT THIS PATH }}
        GBNodes nodes = GB.getNodes();
        TNode node = nodes.getNode(_path);
        // TODO: make this a more useful feedback mechanism
        if (node == null) {
            System.err
                    .println("Core Search failed: the path (" + _path + ") does not seem to be correct.");
            return null;
        }
        String lac = node.getLink();
        if ( lac == null )
            return null;
        return GBSolr.getCoreFromLAC(lac, nodes.getDBConnectionManager());
    }

    public static GResults distinct(String _server, String _schema,
                                    String _searchString, ArrayList<String> _fields, int _start,
                                    int _rows) throws FieldNotFoundException, ConnectException {
        HttpSolrClient solr = null;
        try {

            ArrayList<GColumn> desc = describeCore(_server, _schema);
            // {{ MAKE SURE THE FIELDS ARE PART OF THE CORE }}

            for (String field : _fields) {
                boolean field_found = false;
                for (GColumn col : desc) {
                    if (field.equalsIgnoreCase(col.getName())) {
                        field_found = true;
                    }
                }
                if (!field_found)
                    throw new FieldNotFoundException(_schema, field);
            }


            // first is to make sure the 900807 fields are available for
            // all the _fields above.
            // String solr_url = ABProperties.get(ABProperties.SOLRSITE);
            // if (!solr_url.endsWith("/")) {
            // solr_url += "/";
            // }
            log.debug("_searchString: " + _searchString);
            _searchString = _searchString.replaceAll(" and ", " AND ");
            _searchString = _searchString.replaceAll(" not ", " NOT ");
            _searchString = _searchString.replaceAll(" or ", " OR ");
            String solrurl = ABProperties.get(ABProperties.SOLRSITE);
            if (!solrurl.endsWith("/"))
                solrurl += "/";

            String url = solrurl + _schema;
            if (GBLinkManager.isFullyQualifiedURL(_schema)) {
                url = _schema;
            }

            // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
            solr = new HttpSolrClient.Builder(url).build();

            if (_searchString == null || _searchString.length() <= 0)
                _searchString = "*:*";

            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", "" + _searchString);
            params.set("start", _start);
            params.set("rows", _rows);
            params.set("fl", "*");
//			params.set("sort", "TMID_lastUpdated desc");
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


            QueryResponse response = solr.query(params);
            // int numfound = response.getFacetFields().size();
            int numfound = 0;
            // log.debug("Launching: "
            // + solr.getHttpClient().getParams().toString());
            List<FacetField> fields = response.getFacetFields();
            int increment = _rows - _start;
            int index = 0;
            // crazy facet hashmap
            LinkedHashMap<String, LinkedHashMap<String, Integer>> f_results = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
            for (FacetField f : fields) {
                index = 0;
                String field_name = f.getName();
                // int field_count = f.getValueCount();
                // log.debug(" name : " + field_name + " count : " +
                // field_count);
                List<Count> counts = f.getValues();
                if (counts != null) {
                    LinkedHashMap<String, Integer> facet_query = new LinkedHashMap<String, Integer>();
                    for (Count c : counts) {
                        if (index < increment) {
                            String name = c.getName();
                            Long count = c.getCount();
                            facet_query.put(name, count.intValue());
                        }
                        index++;
                        f_results.put(field_name, facet_query);
                    }
                }
            }
            numfound = index;

            // BUID THE RESULT THROUGH THE RESULTS FACTORY
            log.debug("Building the results...");

            // Results results = ResultsFactory.buildResults(_schema, response);
            GResults results = new GResults();
            results.setSuccessfulSearch(true);
            results.setMessage("faceted restuls");
            results.setFacet(f_results);
            results.setTotalHits(numfound);
            if (desc == null || desc.size() <= 0) {
                GResults re = new GResults();
                re.setSuccessfulSearch(false);
                re.setMessage("Schema : "
                        + _schema
                        + " not found.  Failed to connect to the Bioinformatics database");
                return re;
            }
            results.setColumns(desc);
            log.debug("Results are built.  Returning the results.");
            return results;
        } catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(solr);
        }
        return null;

    }

    public static LinkedHashMap<String, Long> facets(String _server,
                                                     String _schema, String _searchString, int _start, int _rows,
                                                     String _sort, String _facet_field, SearchConfig mode) {
        if (_sort == null)
            _sort = "TMID_lastUpdated desc";

        HttpSolrClient solr = null;
        try {
            String solr_url = _server;
            if (!solr_url.endsWith("/")) {
                solr_url += "/";
            }
            // {{ DO CONFIG WORKUP }}
            if (mode != null && mode.getMode() != SearchConfig.RAW_SEARCH)
                _searchString = mode.updateSearchString(_searchString);
            if (!_searchString.contains(":")) {
                _searchString = _searchString.toLowerCase();
            }

            // log.debug("connecting... " + solr_url + _schema);
            // log.debug("\n\n\n");
            // log.debug("_searchString: " + _searchString);
            // log.debug("\n\n\n");
            // if (_searchString.indexOf(":") <= 0)
            // _searchString = _searchString.toLowerCase();
            // adjust the and or not
            _searchString = _searchString.replaceAll(" and ", " AND ");
            _searchString = _searchString.replaceAll(" not ", " NOT ");
            _searchString = _searchString.replaceAll(" or ", " OR ");
            String url = _server;
            if (!url.endsWith("/"))
                url += "/";

            if (GBLinkManager.isFullyQualifiedURL(_schema)) {
                url = GBLinkManager.getSolrRoot(_schema);
                _schema = GBLinkManager.getCoreLK(_schema);
            }

            // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
//            HttpClient client = new DefaultHttpClient();
            solr = new HttpSolrClient.Builder(url + _schema).build();
            solr.setUseMultiPartPost(true);
            if (_searchString == null || _searchString.length() <= 0)
                _searchString = "*:*";

            // System.out.println("\tSearch : " + _searchString);

            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", "" + _searchString);
            params.set("start", _start);
            params.set("rows", _rows);
            params.set("sort", _sort);
            params.set("fl", "*");

            params.set("wt", "xml");
            params.set("facet", true);
            params.set("facet.field", _facet_field);
            XMLResponseParser pars = new XMLResponseParser();
            QueryResponse response = solr.query(params);

            LinkedHashMap<String, Long> facet_map = new LinkedHashMap<String, Long>();
            List<FacetField> fac = response.getFacetFields();
            for (FacetField ff : fac) {
                String name = ff.getName();
                if (name.equalsIgnoreCase(_facet_field)) {
                    int count = ff.getValueCount();
//                    String gap = ff.getGap();

                    List<Count> l = ff.getValues();
                    for (Count cc : l) {
                        String s = cc.getName();
                        facet_map.put(s, cc.getCount());
                    }
                }
            }

            return facet_map;

        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            IOUTILs.closeResource(solr);
        }
        return new LinkedHashMap<String, Long>();
    }

    public static GResults search(HttpSolrClient solr, String _searchString, int _start, int _rows, String _sort,
                                  String[] cols, SearchConfig mode) {
        if (_sort == null)
            _sort = "TMID_lastUpdated desc";
        try {
            String fl = null;
            if (cols != null && cols.length > 0) {
                fl = "";
                for (String col : cols) {
                    fl += col + ",";
                }
                fl = fl.substring(0, fl.length() - 1);// trim the comma
            }

            if (mode == null)
                mode = new SearchConfig(SearchConfig.RAW_SEARCH);

            // {{ DO CONFIG WORKUP }}
            if (mode != null && mode.getMode() != SearchConfig.RAW_SEARCH)
                _searchString = mode.updateSearchString(_searchString);
            if (!_searchString.contains(":")) {
                _searchString = _searchString.toLowerCase();
            }

            // log.debug("connecting... " + solr_url + _schema);
            // log.debug("\n\n\n");
            // log.debug("_searchString: " + _searchString);
            // log.debug("\n\n\n");
            // if (_searchString.indexOf(":") <= 0)
            // _searchString = _searchString.toLowerCase();
            // adjust the and or not
            _searchString = _searchString.replaceAll(" and ", " AND ");
            _searchString = _searchString.replaceAll(" not ", " NOT ");
            _searchString = _searchString.replaceAll(" or ", " OR ");
            // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
            HttpClient client = new DefaultHttpClient();
            solr.setUseMultiPartPost(true);

            if (_searchString == null || _searchString.length() <= 0)
                _searchString = "*:*";

            // System.out.println("\tSearch : " + _searchString);

            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", "" + _searchString);
            params.set("start", _start);
            params.set("rows", _rows);
            params.set("sort", _sort);
            if (fl != null) {
                params.set("fl", fl.trim());
            } else
                params.set("fl", "*");
            params.set("wt", "xml");
            QueryResponse response = solr.query(params);
            GResults results = buildResults(_start, _rows, response);
            return results;
        } catch (org.apache.solr.client.solrj.SolrServerException _solrException) {

            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;

        } catch (Exception _e) {
            _e.printStackTrace();
            log.debug("Search failed... null return");
            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;
        }
    }


    public static GResults search(SolrClient solr, ArrayList<GColumn> desc, String _schema,
                                  String _searchString, int _start, int _rows, String _sort,
                                  String[] cols, SearchConfig mode) {
        if (_sort == null)
            _sort = "TMID_lastUpdated desc";
        try {
            String fl = null;
            if (cols != null && cols.length > 0) {
                fl = "";
                for (String col : cols) {
                    fl += col + ",";
                }
                fl = fl.substring(0, fl.length() - 1);// trim the comma
            }

            if (mode == null)
                mode = new SearchConfig(SearchConfig.RAW_SEARCH);


            // {{ DO CONFIG WORKUP }}
            if (mode != null && mode.getMode() != SearchConfig.RAW_SEARCH)
                _searchString = mode.updateSearchString(_searchString);

            if (!_searchString.contains(":")) {
                _searchString = _searchString.toLowerCase();
            }

            // log.debug("connecting... " + solr_url + _schema);
            // log.debug("\n\n\n");
            // log.debug("_searchString: " + _searchString);
            // log.debug("\n\n\n");
            // if (_searchString.indexOf(":") <= 0)
            // _searchString = _searchString.toLowerCase();
            // adjust the and or not
            // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
            HttpClient client = new DefaultHttpClient();


            //solr.setUseMultiPartPost(true);


            if (_searchString == null || _searchString.length() <= 0)
                _searchString = "*:*";
            // System.out.println("\tSearch : " + _searchString);
            ModifiableSolrParams params = new ModifiableSolrParams();

            _searchString = GBUtil.prepareSearchString(_searchString);


            params.set("q", "" + _searchString);
            params.set("start", _start);
            params.set("rows", _rows);
            params.set("sort", _sort);
            if (fl != null) {
                params.set("fl", fl.trim());
            } else
                params.set("fl", "*");

            params.set("wt", "xml");
            // params.set("facet", true);
            QueryResponse response = solr.query(params);

            // BUID THE RESULT THROUGH THE RESULTS FACTORY
            // TODO: Test search and pay attention to all columns.
            GResults results = buildResults(_schema, _start, _rows, response);
            // TODO: need to make sure this works as advertised.
            if (mode.getMode() != SearchConfig.RAW_SEARCH) {
                desc = GBSearch.removeTrackingColumns(desc);
            }
            if (desc == null || desc.size() <= 0) {
                GResults re = new GResults();
                re.setSuccessfulSearch(false);
                re.setMessage("Schema : "
                        + _schema
                        + " not found.  Failed to connect to the Bioinformatics database");
                return re;
            }
            results.setColumns(desc);
            return results;
        } catch (org.apache.solr.client.solrj.SolrServerException _solrException) {

            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;

        } catch (Exception _e) {
            _e.printStackTrace();
            log.debug("Search failed... null return");
            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;
        }
    }


    public static GResults search(String _server, String _schema,
                                  String _searchString, int _start, int _rows, String _sort,
                                  String[] cols, SearchConfig mode) {
        if (_sort == null)
            _sort = "TMID_lastUpdated desc";

        HttpSolrClient solr = null;
        try {
            String fl = null;
            if (cols != null && cols.length > 0) {
                fl = "";
                for (String col : cols) {
                    fl += col + ",";
                }
                fl = fl.substring(0, fl.length() - 1);// trim the comma
            }

            String solr_url = _server;
            if (!solr_url.endsWith("/")) {
                solr_url += "/";
            }
            // {{ DO CONFIG WORKUP }}
            if (mode != null && mode.getMode() != SearchConfig.RAW_SEARCH)
                _searchString = mode.updateSearchString(_searchString);
            if (!_searchString.contains(":")) {
                _searchString = _searchString.toLowerCase();
            }
            _searchString = _searchString.replaceAll(" and ", " AND ");
            _searchString = _searchString.replaceAll(" not ", " NOT ");
            _searchString = _searchString.replaceAll(" or ", " OR ");
            String url = _server;
            if (!url.endsWith("/"))
                url += "/";
            // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
            if (GBLinkManager.isFullyQualifiedURL(_schema)) {
                url = GBLinkManager.getSolrRoot(_schema);
                _schema = GBLinkManager.getCoreLK(_schema);
            }

            solr = new HttpSolrClient.Builder(url + _schema).build();
            QueryResponse response = null;
            try {
                solr.setUseMultiPartPost(true);
                if (_searchString == null || _searchString.length() <= 0)
                    _searchString = "*:*";

                // System.out.println("\tSearch : " + _searchString);

                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set("q", "" + _searchString);
                params.set("start", _start);
                params.set("rows", _rows);
                params.set("sort", _sort);
                if (fl != null) {
                    params.set("fl", fl.trim());
                } else
                    params.set("fl", "*");

                params.set("wt", "xml");
                // params.set("facet", true);
                response = solr.query(params);
            } finally {
                IOUTILs.closeResource(solr);
            }

            // BUID THE RESULT THROUGH THE RESULTS FACTORY
            GResults results = buildResults(_schema, _start, _rows, response);
            ArrayList<GColumn> desc = describeCore(_server, _schema);
            // TODO: Test search and pay attention to all columns.
            // TODO: need to make sure this works as advertised.
            desc = GBSearch.removeTrackingColumns(desc);
            if (desc == null || desc.size() <= 0) {
                GResults re = new GResults();
                re.setSuccessfulSearch(false);
                re.setMessage("Schema : "
                        + _schema
                        + " not found.  Failed to connect to the Bioinformatics database");
                return re;
            }
            results.setColumns(desc);
            return results;
        } catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;
        } catch (Exception _e) {
            _e.printStackTrace();
            log.debug("Search failed... null return");
            GResults re = new GResults();
            re.setSuccessfulSearch(false);
            re.setMessage("Failed to connect to the Bioinformatics database");
            return re;
        } finally {
            IOUTILs.closeResource(solr);
        }
    }


    static class CoreCache {
        private final long HALF_DAY = 1000 * 60 * 60 * 12;
        private final int MAX_CACHE = 1000;
        private LinkedHashMap<String, ArrayList<GColumn>> columns = new LinkedHashMap<String, ArrayList<GColumn>>();
        private Date lastRefresh;

        CoreCache() {
            lastRefresh = new Date();
        }

        private ArrayList<GColumn> descCore(String hash) {
            if (lastRefresh.before(new Date(System.currentTimeMillis() - HALF_DAY))) {
                log.debug("Column description cache expired. Clearing stale records.");
                lastRefresh = new Date();
                columns.clear();
            }
            return columns.get(hash);
        }

        public void put(String hash, ArrayList<GColumn> col) {
            if (columns.size() > MAX_CACHE) {
                removeLast();
            }
            columns.put(hash, col);
        }

        //should unit test this.
        private void removeLast() {
            int index = 0;
            Set<Map.Entry<String, ArrayList<GColumn>>> t = columns.entrySet();
            for (Map.Entry<String, ArrayList<GColumn>> v : t) {
                if (index == (t.size() - 1)) {
                    String last_hash_key = v.getKey();
                    columns.remove(last_hash_key);
                }
                index++;
            }
        }
        }

        private static CoreCache corecache = new CoreCache();

        public static synchronized ArrayList<GColumn> describeCore(String _server, String _schema)
                throws ConnectException {
            String hash = _server + _schema;
            ArrayList<GColumn> columns = corecache.descCore(hash);
            if (columns != null) {
                return columns;
            }
            URLConnection conn = null;
            if (_schema == null) {
                GB.print("Desc: No schema ");
                return null;
            } else if (_server == null) {
                GB.print("Desc: No server ");
                return null;
            }
            InputStream istream = null;
            InputStream dictionaries_istream = null;
            try {
                String solr_url = _server;
                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                String urls = solr_url + _schema;
                if (GBLinkManager.isFullyQualifiedURL(_schema)) {
                    urls = _schema;
                }

                URL url = new URL(urls + "/admin/file/?file=managed-schema");
                conn = url.openConnection();

                istream = conn.getInputStream();

                org.xml.sax.XMLReader parser = new org.apache.xerces.parsers.SAXParser();
                PatternHandler handler = new PatternHandler();
                parser.setContentHandler(handler);
                parser.setErrorHandler(handler);
                org.xml.sax.InputSource input = new InputSource(istream);
                parser.parse(input);


                // {{ get the dictionary xml }}
//                URL dictionary_url = new URL(urls
//                        + "/admin/file/?file=dictionary.xml");
//                URLConnection dictionary_conn = dictionary_url.openConnection();
//                dictionaries_istream = dictionary_conn.getInputStream();
//                org.xml.sax.XMLReader dictionary_parser = new org.apache.xerces.parsers.SAXParser();
//                DictionaryXMLHandler dictionary_handler = new DictionaryXMLHandler();
//                dictionary_parser.setContentHandler(dictionary_handler);
//                dictionary_parser.setErrorHandler(dictionary_handler);
//                org.xml.sax.InputSource dictionary_input = new InputSource(
//                        dictionaries_istream);
//                dictionary_parser.parse(dictionary_input);

                ArrayList<GColumn> cp = new ArrayList<GColumn>();
                ArrayList<LinkedHashMap<String, String>> data = handler.getData();
//                HashMap<String, String> dictionary_data = dictionary_handler
//                        .getData();

                for (HashMap<String, String> val : data) {
                    String _key = val.get("name");
                    String value = val.get("type");
                    GColumn cprop = new GColumn();
                    cprop.setTitle(_key);
                    cprop.setType(value);
//                    String duri = dictionary_data.get(_key);
//                    cprop.setDictionaryURI(duri);
                    cp.add(cprop);
                }

//			for ( GColumn colss : cp ){
//				System.out.println ( " column name : "  + colss.getName () );
//			}


                // {{ THIS IS A WAY TO MANAGE THE ORDER OF FIELDS BY A STATE JSON OBJECT }}
                ArrayList<GColumn> norder = new ArrayList<GColumn>();
                List<GColumn> order = TableManager.getFieldOrder(_schema,
                        GB.getConnectionManager(), cp);

                if (order != null) {
                    for (GColumn i : order) {
                        for (GColumn c : cp) {
                            if (c.getName().equalsIgnoreCase(i.getName()))
                                norder.add(c);
                        }
                    }
                    corecache.put ( _server+_schema, norder );
                    return norder;
                }// we don't have an order defined...
                else {
                    // System.err
                    // .println(" Warning:  The field order object is not available. ");
                    for (GColumn c : cp) {
                        norder.add(c);
                    }
                    corecache.put ( _server+_schema, norder );
                    return norder;
                }
            } catch (ConnectException _ce) {
                _ce.printStackTrace();
                throw new ConnectException(_ce.getLocalizedMessage());
            } catch (Exception _e) {
                _e.printStackTrace();
                throw new ConnectException(_e.getLocalizedMessage());
            } finally {
                IOUTILs.closeResource(istream);
                IOUTILs.closeResource(dictionaries_istream);
            }
        }

        public GResults search(String _table_name, String _w, SearchConfig mode) {
            return search(server, _table_name, _w, 0, 10000, mode);
        }

        public void load(GResults _r) throws LoaderException {
            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                log.debug("starting loader...\n\n\n");
                String solr_url = server;
                String stat_msg = "";
                String table_name = _r.getTarget();
                table_name = table_name.replace(' ', '_');
                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(
                            stat_msg,
                            "Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
                }

                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }

                solr = new HttpSolrClient.Builder(solr_url + table_name).build();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                ArrayList<GRow> rows = _r.getValues();
                for (GRow row : rows) {

                    SolrInputDocument solr_doc = new SolrInputDocument();
                    HashMap row_d = row.getData();
                    Set<String> s = row_d.keySet();
                    for (String key : s) {
                        Object value = row_d.get(key);
                        solr_doc.addField(key, value);
                    }
                    solr_doc.addField("TMID", TMID.create());
                    solr_doc.addField("TMID_lastUpdated", new Date());

                    try {
                        docs.add(solr_doc);
                    } catch (Exception _e) {
                        _e.printStackTrace();
                        System.err.println("\n\n\t\t" + _e.getMessage() + "\n\n");
                    }
                }
                try {
                    solr.add(docs);
                    solr.commit();
                } catch (SolrServerException e) {
                    e.printStackTrace();
                    System.err.println("\n\n\t\t" + e.getMessage() + "\n\n");
                    throw new LoaderException(
                            "Failed to add the solr docs to the solr server"
                                    + e.getLocalizedMessage());
                }
                docs.clear();
                if (el.count() > 0)
                    throw new LoaderException(el);
            } catch (IOException _e) {
                _e.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }
        }

        /**
         * this is used for attaching an annotation to an entire table.. This is a
         * dynamic append meaning that the field names must end in _int, _st, etc...
         * according to the solr schema config documentation. Please review the
         * schem_template for more information on this.
         *
         * @param _table
         * @param _data
         */
        public void dynamicAppendSchema(String _table, String _param,
                                        Map<String, String> _data) throws LoaderException {
            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                log.setLevel(Level.DEBUG);
                log.debug("\n\n\n\t Append the schema with the new fields\n\n");
                String solr_url = server;
                String stat_msg = "";
                String table_name = _table;
                table_name = table_name.replace(' ', '_');
                NodeManager st = new NodeManager();
                // {{ VALIDATE THE PATH }}
                TNode node = st.getNode(table_name);
                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(
                            stat_msg,
                            "Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
                }
                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                // StreamingUpdateSolrServer solr = new StreamingUpdateSolrServer
                // (solr_url+table_name, 100, 3);
                solr = new HttpSolrClient.Builder(solr_url + table_name).build();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                // http://localhost:8983/solr/admin/cores?action=CREATE&name=coreX&instanceDir=path_to_instance_directory&config=config_file_name.xml&schema=schem_file_name.xml&dataDir=data
                // copySchema ( table_name, table_name + "_temp");
                int increment = 100000;
                int t_index = 0;
                boolean complete = false;
                while (!complete) {
                    GResults r = search(server, table_name, _param, t_index,
                            increment, new SearchConfig(SearchConfig.RAW_SEARCH));
                    // log.debug("\n\n results are null  for " + table_name
                    // + " Param : " + _param + " t_index: " + t_index);
                    if (r == null) {
                        log.debug("\n\n results are null  for " + table_name
                                + " Param : " + _param + " t_index: " + t_index);
                        break;
                    }
                    t_index += increment;
                    ArrayList<GRow> rows = r.getValues();
                    if (rows.size() < increment) {
                        complete = true;
                    }
                    for (GRow row : rows) {
                        SolrInputDocument solr_doc = new SolrInputDocument();
                        HashMap row_d = row.getData();
                        Set<String> s = row_d.keySet();
                        for (String key : s) {
                            Object value = row_d.get(key);
                            solr_doc.setField(key, value);
                        }
                        // append the annotations
                        Set<String> new_fields = _data.keySet();
                        for (String n_field : new_fields) {
                            Object ob = _data.get(n_field);
                            solr_doc.setField(n_field, ob);
                        }
                        // solr_doc.addField("TMID", TMID.create());
                        // solr_doc.addField("TMID_lastUpdated", new Date());
                        try {
                            docs.add(solr_doc);
                        } catch (Exception _e) {
                            _e.printStackTrace();
                            log.error("\n\n\t\t" + _e.getMessage() + "\n\n");
                        }
                    }
                    try {

                        log.debug(" Appending schema : " + t_index);
                        solr.add(docs);

                        // {{ REMOVE THE PREVIOUS HITS FOR THIS QUERY THAT ARE NOT
                        // ANNOTATED }}
                        String field_query = "";
                        Set<String> new_fields = _data.keySet();
                        for (String n_field : new_fields) {
                            Object ob = _data.get(n_field);
                            field_query += " NOT " + n_field + ":" + ob.toString()
                                    + "";
                        }
                        solr.deleteByQuery(_param + " " + field_query);
                        solr.commit();
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                        log.error("\n\n\t\t" + e.getMessage() + "\n\n");
                        throw new LoaderException(
                                "Failed to add the solr docs to the solr server"
                                        + e.getLocalizedMessage());
                    }
                    docs.clear();
                }

                try {
                    solr.commit();
                } catch (SolrServerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (el.count() > 0)
                    throw new LoaderException(el);
            } catch (IOException _e) {
                _e.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }
        }


        public void dynamicRemoveAnnotation(String _table, List<String> fields)
                throws LoaderException {
            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                log.setLevel(Level.DEBUG);
                log.debug("\n\n\n\t Append the schema with the new fields\n\n");
                String solr_url = server;
                String stat_msg = "";
                String table_name = _table;
                table_name = table_name.replace(' ', '_');
                NodeManager st = new NodeManager();
                // {{ VALIDATE THE PATH }}
                TNode node = st.getNode(table_name);

                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(
                            stat_msg,
                            "Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
                }

                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                solr = new HttpSolrClient.Builder(solr_url + table_name).build();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

                int increment = 100;
                int t_index = 0;
                boolean complete = false;
                while (!complete) {
                    GResults r = search(server, table_name, "*:*", t_index,
                            increment, new SearchConfig(SearchConfig.RAW_SEARCH));
                    if (r == null)
                        break;
                    t_index += increment;

                    ArrayList<GRow> rows = r.getValues();
                    if (rows.size() < increment) {
                        complete = true;
                    }

                    for (GRow row : rows) {
                        SolrInputDocument solr_doc = new SolrInputDocument();
                        HashMap row_d = row.getData();
                        Set<String> s = row_d.keySet();
                        for (String key : s) {
                            if (!removeField(fields, key)) {
                                Object value = row_d.get(key);
                                solr_doc.setField(key, value);
                            }
                        }

                        try {
                            docs.add(solr_doc);
                        } catch (Exception _e) {
                            _e.printStackTrace();
                            log.error("\n\n\t\t" + _e.getMessage() + "\n\n");
                        }
                    }
                    t_index++;
                    try {
                        solr.add(docs);
                        solr.commit();
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                        log.error("\n\n\t\t" + e.getMessage() + "\n\n");
                        throw new LoaderException(
                                "Failed to add the solr docs to the solr server"
                                        + e.getLocalizedMessage());
                    }
                    docs.clear();
                }
                if (el.count() > 0)
                    throw new LoaderException(el);
            } catch (IOException _e) {
                _e.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }

        }

        private boolean removeField(List<String> _removeFields, String _field) {
            for (String s : _removeFields) {
                if (s.equalsIgnoreCase(_field))
                    return true;
            }
            return false;
        }

        public static void main(String[] _args) {
            String schema = "milton_Repository_v_c_studies";
            String solr = ABProperties.get(ABProperties.SOLRSITE);
            TMSolrServer so = new TMSolrServer(solr);
            try {

                // 0.36253884579440776_1323052889570
                so.findAndReplace(schema, "project", "equals", "V-0519",
                        "hello world");
            } catch (LoaderException e) {
                e.printStackTrace();
            }

        }

        public void findAndReplace(String schema, String column, String deprecated,
                                   String find_text, String replace_text) throws LoaderException {
            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                String _param = find_text;
                if (replace_text == null)
                    replace_text = "";
                String solr_url = server;
                String stat_msg = "";
                String table_name = schema;
                table_name = table_name.replace(' ', '_');
                // NodeManager st = new NodeManager();
                // TNode node = st.getNode(table_name);
                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(stat_msg,
                            "Table name was not defined.  "
                                    + "Try putting a $table= variable in the"
                                    + " \"new table script\" field.");
                }
                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                solr = new HttpSolrClient.Builder(solr_url + table_name).build();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                // http://localhost:8983/solr/admin/cores?action=CREATE&name=coreX&instanceDir=path_to_instance_directory&config=config_file_name.xml&schema=schem_file_name.xml&dataDir=data
                int increment = 10000;
                int t_index = 0;
                boolean complete = false;
                // _param = column + ":\"" + find_text + "\"";
                // _param = column + ":" + find_text + "";

                // System.out.println(" search : " + _param);

                while (!complete) {
                    GResults r = search(server, table_name, _param, t_index,
                            increment, new SearchConfig(SearchConfig.RAW_SEARCH));
                    if (r == null) {
                        // log.debug("\n\n results are null  for " + table_name
                        // + " Param : " + _param + " t_index: " + t_index);
                        break;
                    }
                    t_index += increment;
                    ArrayList<GRow> rows = r.getValues();
                    if (rows.size() < increment) {
                        complete = true;
                    }
                    int index = 0;
                    for (GRow row : rows) {
                        SolrInputDocument solr_doc = new SolrInputDocument();
                        HashMap row_d = row.getData();
                        Set<String> s = row_d.keySet();
                        boolean addit = false;
                        for (String key : s) {
                            Object value = row_d.get(key);
                            String value_st = value.toString();
                            if (key.equalsIgnoreCase(column)) {
                                // if (value_st.contains(find_text)) {
                                // value_st = value_st.replace(find_text,
                                // replace_text);
                                // value = value_st;
                                // addit = true;
                                // }
                                System.out.println(" add it : " + key + " value : "
                                        + value);
                                addit = true;
                                solr_doc.setField(key, replace_text);
                            } else
                                solr_doc.setField(key, value);
                        }
                        try {
                            if (addit) {
                                System.out.println(" add it index:" + index++);
                                docs.add(solr_doc);

                            }
                        } catch (Exception _e) {
                            _e.printStackTrace();
                            log.error("\n\n\t\t" + _e.getMessage() + "\n\n");
                        }
                    }
                    try {
                        if (docs.size() > 0) {
                            UpdateResponse reps = solr.add(docs);
                            int status = reps.getStatus();
                        }
                        solr.commit();
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                        log.error("\n\n\t\t" + e.getMessage() + "\n\n");
                        throw new LoaderException(
                                "Failed to add the solr docs to the solr server"
                                        + e.getLocalizedMessage());
                    }
                    docs.clear();
                }
                try {
                    solr.commit();
                } catch (SolrServerException e) {
                    e.printStackTrace();
                }
                if (el.count() > 0)
                    throw new LoaderException(el);
            } catch (IOException _e) {
                _e.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }
        }

        public void fillDownWillConditional(String schema, String set_column,
                                            String enter_text, String operation, String where_col,
                                            String conditional_text) throws LoaderException {

            // will eventually make these member variables.. but for now.. (simply)
            // do it.
            int mode = 0;
            int CONTAINS = 1;
            int STARTS_WITH = 2;
            int ENDS_WITH = 3;
            int EQUALS = 4;
            int NOTHING = 0;

            if (operation == null)
                mode = NOTHING;
            else if (operation.equalsIgnoreCase("equals"))
                mode = EQUALS;
            else if (operation.equalsIgnoreCase("startsWith"))
                mode = STARTS_WITH;
            else if (operation.equalsIgnoreCase("endsWith"))
                mode = ENDS_WITH;
            else if (operation.equalsIgnoreCase("contains"))
                mode = CONTAINS;
            else
                mode = NOTHING;

            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                String _param = "*:*";
                log.debug("\n\n\n\t Append the schema with the new fields\n\n");
                String solr_url = server;
                String stat_msg = "";
                String table_name = schema;
                table_name = table_name.replace(' ', '_');
                NodeManager st = new NodeManager();
                TNode node = st.getNode(table_name);
                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(
                            stat_msg,
                            "Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
                }

                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                solr = new HttpSolrClient.Builder(solr_url + table_name).build();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                // http://localhost:8983/solr/admin/cores?action=CREATE&name=coreX&instanceDir=path_to_instance_directory&config=config_file_name.xml&schema=schem_file_name.xml&dataDir=data
                int increment = 100000;
                int t_index = 0;
                boolean complete = false;
                while (!complete) {
                    GResults r = search(server, table_name, _param, t_index,
                            increment, new SearchConfig(SearchConfig.RAW_SEARCH));
                    if (r == null) {
                        log.debug("\n\n results are null  for " + table_name
                                + " Param : " + _param + " t_index: " + t_index);
                        break;
                    }
                    t_index += increment;
                    ArrayList<GRow> rows = r.getValues();
                    if (rows.size() < increment) {
                        complete = true;
                    }
                    // loop over the rows.
                    for (GRow row : rows) {
                        HashMap row_d = row.getData();
                        Set<String> s = row_d.keySet();

                        if (mode == NOTHING) {
                            docs.add(getSolrDoc(row_d, set_column, enter_text));
                        } else {
                            for (String key : s) {
                                Object value = row_d.get(key);
                                String value_st = value.toString();
                                if (key.equalsIgnoreCase(where_col)) {
                                    if (mode == CONTAINS) {
                                        if (value_st.contains(conditional_text)) {
                                            docs.add(getSolrDoc(row_d, set_column,
                                                    enter_text));
                                        }
                                    } else if (mode == STARTS_WITH) {
                                        if (value_st.startsWith(conditional_text)) {
                                            docs.add(getSolrDoc(row_d, set_column,
                                                    enter_text));
                                        }
                                    } else if (mode == ENDS_WITH) {
                                        if (value_st.endsWith(conditional_text)) {
                                            docs.add(getSolrDoc(row_d, set_column,
                                                    enter_text));
                                        }
                                    }
                                }
                            }
                        }

                    }
                    try {
                        UpdateResponse reps = solr.add(docs);
                        int status = reps.getStatus();
                        solr.commit();
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                        log.error("\n\n\t\t" + e.getMessage() + "\n\n");
                        throw new LoaderException(
                                "Failed to add the solr docs to the solr server"
                                        + e.getLocalizedMessage());
                    }
                    docs.clear();
                    if (el.count() > 0)
                        throw new LoaderException(el);
                }
            } catch (IOException _e) {
                _e.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }
        }

        private SolrInputDocument getSolrDoc(HashMap row_d, String set_column,
                                             String enter_text) {
            SolrInputDocument doc = new SolrInputDocument();
            Set<String> cols = row_d.keySet();
            for (String col : cols) {
                Object value = row_d.get(col);
                String vst = value.toString();
                doc.setField(col, value);
            }
            doc.setField(set_column, enter_text);
            return doc;
        }

        public static boolean callSolr(String url, String _method)
                throws SolrCallException {
            if (_method == null)
                throw new SolrCallException(" Solr call failed :  " + url
                        + " for method : " + _method);
            if (!(_method.equals("GET") || (_method.equals("POST"))))
                throw new SolrCallException(" Solr POST call failed :  " + url
                        + " for method : " + _method);

            HttpURLConnection uc = null;
            BufferedReader in = null;
            URL u = null;
            try {
                log.debug("\n\nURL : " + url);
                log.debug("\n\n : ");
                u = new URL(url);
                uc = (HttpURLConnection) u.openConnection();
                uc.setRequestMethod(_method);
                uc.setUseCaches(false);
                log.debug("\n\n : ");
                in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    log.debug(inputLine);
                in.close();
            } catch (MalformedURLException e) {
                log.error("\n\n failed... : ");
                e.printStackTrace();
                throw new SolrCallException(" Solr call failed :  " + url
                        + " for method : " + _method);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SolrCallException(" Solr call failed :  " + url
                        + " for method : " + _method);
            } finally {
                IOUTILs.closeResource(in);
                uc.disconnect();
            }
            return true;
        }

        public static HashMap<String, Map<String, String>> appendTMFields(
                HashMap<String, Map<String, String>> _params) {
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
            Date dd = new Date();
            HashMap<String, String> last_updated = new HashMap<String, String>();
            last_updated.put("fieldName", "TMID_lastUpdated");
            last_updated.put("sortable", "true");
            last_updated.put("indexed", "true");
            last_updated.put("defaultString",CurrentTimeForSolr.timeStr());
            last_updated.put("dataType", "date");
            last_updated.put("requiredField", "true");
            _params.put("TMID_lastUpdated", last_updated);
            return _params;
        }


        public static String clearCore(String _url) {
            String delParams = "commit=true&stream.body=<delete><query>*:*</query></delete>";
            // System.out.println("" + _url);
            // first try to delete if the core is there.
            if (!_url.endsWith("/"))
                _url += "/";
            try {
                String deleteTableSite = _url + "update";
                // + URLEncoder.encode(delParams, "UTF-8");
                TMSolrServer.post(deleteTableSite, delParams);
            } catch (Exception _e) {
                _e.printStackTrace();
            }
            return "Index clear";
        }

        /**
         * Client method for creating a table object
         *
         * @param inputParms
         * @param defaultOperatorAND
         * @return
         */
        public static String createSchema(String user, String _solrSite,
                                          String _table_name, Map<String, Map<String, String>> inputParms,
                                          Boolean defaultOperatorAND) {
            String solrSite = _solrSite;
            if (solrSite == null)
                solrSite = ABProperties.get("solrSite");
            if (solrSite == null)
                return "ERROR: solrSite not defined";
            if (!solrSite.endsWith("/"))
                solrSite += "/";
            // we are calling the admin
            String createTableSite = solrSite + "admin/cores";
            boolean bb = false;
            try {
                String table = GBUtil.toGSON(inputParms);
                // http://localhost:8983/solr/search_index/update?stream.body=%3Cdelete%3E%3Cquery%3E*:*%3C/query%3E%3C/delete%3E&commit=true
                String params = "action=create_table&table_name="
                        + URLEncoder.encode(_table_name, "UTF-8") + "&user="
                        + URLEncoder.encode(user, "UTF-8") + "&table="
                        + URLEncoder.encode(table, "UTF-8");
                log.debug(" Table Name " + _table_name);
                log.debug("\t\t " + createTableSite);
                bb = TMSolrServer.post(createTableSite, params);
            } catch (SolrCallException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (bb)
                return "created";

            else
                return null;
        }


        public static ABaseResults search(String site, String core, String[] _cols,
                                          String _searchString, int start, int rows, String sort,
                                          SearchConfig mode) {

            String solr_url = ABProperties.get(ABProperties.SOLRSITE);
            String url = core;
            if (!GBLinkManager.isFullyQualifiedURL(core)) {
                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                url = solr_url + core;
            }

            HttpSolrClient solr = null;
            try {
                // {{ DO CONFIG WORKUP }}
                if (mode.getMode() != SearchConfig.RAW_SEARCH)
                    _searchString = mode.updateSearchString(_searchString);
                if (!_searchString.contains(":")) {
                    _searchString = _searchString.toLowerCase();
                }
                _searchString = _searchString.replaceAll(" and ", " AND ");
                _searchString = _searchString.replaceAll(" not ", " NOT ");
                _searchString = _searchString.replaceAll(" or ", " OR ");
                // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
                solr = new HttpSolrClient.Builder(url).build();

                if (_searchString == null || _searchString.length() <= 0)
                    _searchString = "*:*";

                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set("q", "" + _searchString);
                params.set("start", start);
                params.set("rows", rows);
                params.set("sort", sort);
                params.set("wt", "xml");
                if (_cols != null && _cols.length > 0) {
                    String fields = "";
                    for (int i = 0; i < _cols.length; i++) {
                        if (i > 0)
                            fields += ",";
                        fields += _cols[i];
                    }
                    params.set("fl", fields);
                } else
                    params.set("fl", "*");

                // params.set("facet", true);
                QueryResponse response = solr.query(params);
                // log.debug("Launching: "
                // + solr.getHttpClient().getParams().toString());

                // BUID THE RESULT THROUGH THE RESULTS FACTORY
                GResults results = buildResults(core, start, rows, response);
                ArrayList<GColumn> desc = describeCore(solr_url, core);
                // TODO: Test search and pay attention to all columns.
                // TODO: need to make sure this works as advertised.
                desc = GBSearch.removeTrackingColumns(desc);
                if (desc == null || desc.size() <= 0) {
                    GResults re = new GResults();
                    re.setSuccessfulSearch(false);
                    re.setMessage("Schema : "
                            + core
                            + " not found.  Failed to connect to the Bioinformatics database");
                    return re;
                }
                results.setColumns(desc);
                return results;
            } catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
                GResults re = new GResults();
                re.setSuccessfulSearch(false);
                re.setMessage("Failed to connect to the Bioinformatics database");
                return re;
            } catch (Exception _e) {
                _e.printStackTrace();
                log.debug("Search failed... null return");
                GResults re = new GResults();
                re.setSuccessfulSearch(false);
                re.setMessage("Failed to connect to the Bioinformatics database");
                return re;
            } finally {
                IOUTILs.closeResource(solr);
            }
        }

        public static int countCore(String _server, String _schema) {
            if (GBLinkManager.isFullyQualifiedURL(_schema)) {
                _server = GBLinkManager.getSolrRoot(_schema);
                _schema = GBLinkManager.getCoreLK(_schema);
            }

            GResults res = search(_server, _schema, "*:*", 0, 1,
                    new SearchConfig(SearchConfig.RAW_SEARCH));
            if (res == null) {
                GB.print("\n search was not found . ");
                return -1;
            }
            return res.getTotalHits();
        }

        public static void append(String core,
                                  ArrayList<LinkedHashMap<String, String>> dlist)
                throws LoaderException {
            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                log.debug("starting loader...\n\n\n");
                String solr_url = ABProperties.getSolrURL();
                String stat_msg = "";
                String table_name = core;
                table_name = table_name.replace(' ', '_');
                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(
                            stat_msg,
                            "Table name was not defined.  "
                                    + "Try putting a $table= variable in the \"new table script\" field.");
                }

                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }

                String h = solr_url + table_name;
                solr = new HttpSolrClient.Builder(h).build();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                for (LinkedHashMap<String, String> row_d : dlist) {
                    SolrInputDocument solr_doc = new SolrInputDocument();
                    Set<String> s = row_d.keySet();
                    for (String key : s) {
                        Object value = row_d.get(key);
                        solr_doc.addField(key, value);
                    }
                    solr_doc.addField("TMID", TMID.create());
                    solr_doc.addField("TMID_lastUpdated", new Date());

                    try {
                        docs.add(solr_doc);
                    } catch (Exception _e) {
                        _e.printStackTrace();
                        System.err.println("\n\n\t\t" + _e.getMessage() + "\n\n");
                    }
                }
                try {
                    solr.add(docs);
                    solr.commit();
                } catch (SolrServerException e) {
                    e.printStackTrace();
                    System.err.println("\n\n\t\t" + e.getMessage() + "\n\n");
                    throw new LoaderException(
                            "Failed to add the solr docs to the solr server"
                                    + e.getLocalizedMessage());
                }
                docs.clear();
                if (el.count() > 0)
                    throw new LoaderException(el);
            } catch (IOException _e) {
                _e.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }
        }

        /**
         * This has not been completely tested.
         *
         * @param core
         * @param dlist
         * @param where_clauses
         * @throws LoaderException
         */
        public static void insert(String core, ArrayList<GColumn> desc,
                                  ArrayList<LinkedHashMap<String, String>> dlist,
                                  ArrayList<WhereClause> where_clauses) throws LoaderException {
            ErrorLog el = new ErrorLog();
            HttpSolrClient solr = null;
            try {
                String solr_url = ABProperties.getSolrURL();
                String stat_msg = "";
                String table_name = core;
                table_name = table_name.replace(' ', '_');
                if (solr_url == null) {
                    throw new LoaderException(stat_msg,
                            "Please provide a solr url in the properties: e.g. solr.url");
                }
                if (table_name == null || table_name.length() <= 0) {
                    throw new LoaderException(
                            stat_msg,
                            "Table name was not defined.  "
                                    + "Try putting a $table= variable in the \"new table script\" field.");
                }
                if (!solr_url.endsWith("/")) {
                    solr_url += "/";
                }
                String h = solr_url + table_name;

                solr = new HttpSolrClient.Builder(h).build();
                String searchstring = buildSearchString(dlist, where_clauses);
                String fieldList = buildFieldList(desc, where_clauses);

                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set("q", "" + searchstring);
                params.set("start", 0);
                params.set("rows", dlist.size());
                // params.set("sort", "f1 asc");
                params.set("wt", "xml");
                params.set("fl", fieldList);

                QueryResponse response = solr.query(params);

                // DOCUMENTS FOR UPDATE
                Iterator<SolrDocument> iter = response.getResults().iterator();
                ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                while (iter.hasNext()) {
                    SolrDocument resultDoc = iter.next();
                    for (LinkedHashMap<String, String> new_values : dlist) {
                        if (isSatisfied(where_clauses, new_values, resultDoc)) {
                            String id = (String) resultDoc.getFieldValue("TMID");
                            SolrInputDocument doc = new SolrInputDocument();

                            doc.addField("TMID", "" + id);
                            Set<String> keys = new_values.keySet();
                            for (String key : keys) {
                                Map<String, String> partialUpdate = new HashMap<String, String>();
                                Object new_v = new_values.get(key);
                                if (new_v == null || new_v.toString().length() <= 0) {

                                } else {
                                    partialUpdate.put("set", new_v.toString());
                                    doc.addField(key, partialUpdate);
                                }
                            }
                            doc.setField("TMID_lastUpdated", new Date());
                            docs.add(doc);
                        } else {
                            // System.out.println("Failed" + where_clauses);
                        }
                    }
                }
                try {
                    if (docs != null && docs.size() > 0) {
                        solr.add(docs);
                        solr.commit();
                    }
                } catch (SolrServerException e) {
                    e.printStackTrace();
                    System.err.println("\n\n\t\t" + e.getMessage() + "\n\n");
                    throw new LoaderException(
                            "Failed to add the solr docs to the solr server"
                                    + e.getLocalizedMessage());
                }
                docs.clear();
                if (el.count() > 0)
                    throw new LoaderException(el);
            } catch (IOException _e) {
                _e.printStackTrace();
            } catch (SolrServerException e1) {
                e1.printStackTrace();
            } finally {
                IOUTILs.closeResource(solr);
            }
        }

        public static String getCore(TNode node) {
            String link = node.getLink();
            if (link == null)
                return null;
            String target = LAC.getTarget(link);
            return target;
        }

        private static String buildFieldList(ArrayList<GColumn> desc,
                                             ArrayList<WhereClause> where_clauses) {
            String t = "TMID";
            for (WhereClause w : where_clauses) {
                String r = w.getRight();
                if (r != null) {
                    t += "," + r;
                }
            }
            return t;
        }

        // at the moment we are only going to compare strings.. but
        // this is where we can do much much more... etc..
        private static boolean isSatisfied(ArrayList<WhereClause> where_clauses,
                                           LinkedHashMap<String, String> left_doc, SolrDocument right_doc) {

            // debug
            // String k = left_doc.get("gene_symbol");
            // if ( k.equalsIgnoreCase("KRAS"))
            // System.out.println ( "got it ");

            for (WhereClause w : where_clauses) {
                String left = w.getLeft();
                String right = w.getRight();
                String left_string = left_doc.get(left);
                if (left_string == null)
                    return false;
                Object right_object = right_doc.getFieldValue(right);
                if (right_object != null) {
                    String right_string = right_object.toString();
                    if (right_string == null)
                        return false;
                    return left_string.equalsIgnoreCase(right_string.trim());
                }
            }

            return false;
        }

        private static String buildSearchString(
                ArrayList<LinkedHashMap<String, String>> dlist,
                ArrayList<WhereClause> where_clauses) {
            String t = "";
            for (LinkedHashMap<String, String> doc : dlist) {
                for (WhereClause w : where_clauses) {
                    t += w.getRight() + ":" + doc.get(w.getLeft()) + " OR ";
                }
            }
            if (t.endsWith(" OR "))
                t = t.substring(0, t.length() - 4);
            return t.trim();
        }

        /**
         * @param _schema
         * @return
         */
        public static ArrayList<GColumn> describeCore(String _schema) {


            ArrayList<GColumn> column_set = corecache.descCore(_schema);
            if ( column_set != null )
            {
                return column_set;
            }

            URLConnection conn = null;
            if (_schema == null) {
                GB.print("Desc: No schema ");
                return null;
            }

            try {
                String urls = _schema;
                if (GBLinkManager.isFullyQualifiedURL(_schema)) {
                    urls = _schema;
                } else {
                    String server = GB.getDefaultURL();
                    if (_schema.startsWith("/") && server.endsWith("/")) {
                        _schema = _schema.trim();
                        _schema = _schema.substring(1);
                    }
                    urls = server + _schema;
                }

//                URL url = new URL(urls + "/admin/file/?file=schema.xml");
                URL url = new URL(urls + "/admin/file/?file=managed-schema");
                conn = url.openConnection();
                if (conn == null)
                    return null;
//			_isis_production_express_master_index_fpkm_tracking
                InputStream istream = conn.getInputStream();

                org.xml.sax.XMLReader parser = new org.apache.xerces.parsers.SAXParser();
                PatternHandler handler = new PatternHandler();
                parser.setContentHandler(handler);
                parser.setErrorHandler(handler);
                org.xml.sax.InputSource input = new InputSource(istream);
                parser.parse(input);

                // {{ get the dictionary xml }}
                URL dictionary_url = new URL(urls
                        + "/admin/file/?file=dictionary.xml");
                URLConnection dictionary_conn = dictionary_url.openConnection();
                InputStream dictionaries_istream = dictionary_conn.getInputStream();
                org.xml.sax.XMLReader dictionary_parser = new org.apache.xerces.parsers.SAXParser();
                DictionaryXMLHandler dictionary_handler = new DictionaryXMLHandler();
                dictionary_parser.setContentHandler(dictionary_handler);
                dictionary_parser.setErrorHandler(dictionary_handler);
                org.xml.sax.InputSource dictionary_input = new InputSource(
                        dictionaries_istream);
                dictionary_parser.parse(dictionary_input);

                ArrayList<GColumn> cp = new ArrayList<GColumn>();
                ArrayList<LinkedHashMap<String, String>> data = handler.getData();
                HashMap<String, String> dictionary_data = dictionary_handler
                        .getData();

                for (HashMap<String, String> val : data) {
                    String _key = val.get("name");
                    String value = val.get("type");
                    GColumn cprop = new GColumn();
                    cprop.setTitle(_key);
                    cprop.setType(value);
                    String duri = dictionary_data.get(_key);
                    cprop.setDictionaryURI(duri);
                    cp.add(cprop);
                }
                ArrayList<GColumn> norder = new ArrayList<GColumn>();
                List<GColumn> order = TableManager.getFieldOrder(_schema,
                        GB.getConnectionManager(), cp);

                if (order != null) {
                    for (GColumn i : order) {
                        for (GColumn c : cp) {
                            if (c.getName().equalsIgnoreCase(i.getName()))
                                norder.add(c);
                        }
                    }


                    corecache.put ( _schema, norder );
                    return norder;
                }// we don't have an order defined...
                else {
                    for (GColumn c : cp) {
                        norder.add(c);
                    }
                    corecache.put ( _schema, norder );
                    return norder;
                }
            } catch (ConnectException _ce) {
                _ce.printStackTrace();
            } catch (Exception _e) {
                _e.printStackTrace();
            } finally {
            }
            return null;
        }
    }

