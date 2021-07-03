package com.arraybase.shell;

import com.arraybase.ABaseNode;
import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GColumn;
import com.arraybase.*;
import com.arraybase.tm.GResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.ResultsFactory;
import com.arraybase.util.ABProperties;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by jmilton on 3/7/2016.
 */
public class CreateTableFromURL implements com.arraybase.GBPlugin {

    public final static int MAX_DOCS=90000000;
    public final static int INCREMENT=100000;

    private boolean alive = true;

    //    create table fpkm from http://biosolr1:8983/solr/_temp_isis_express_fileindexr_gtf
    public String exec(String command, String variable_key) throws UsageException {
        int from_index = command.indexOf("from") + 5;
        int create_index = command.indexOf("table") + 6;
        String url = command.substring(from_index);
        String path = command.substring(create_index, from_index - 5);
        if (path == null || path.length() < 0) {
            GB.print(" Path was not determined from the command : " + command);
            return null;
        }

        path = path.trim();

        GB.print("Creating table : " + path);
        url = url.trim();
        if (url.endsWith("/"))
            url = url.substring(0, url.length() - 1);
        try {
            if (url.indexOf('#') > 0)
                url = url.replace("#/", "");
            URL u = new URL(url);
            String webapps = u.getPath();
            int ind = webapps.indexOf('#');
            if (ind > 0)
                webapps = webapps.substring(0, ind);
            else {
                int lasti = webapps.lastIndexOf('/');
                webapps = webapps.substring(0, lasti);
            }
            if (webapps.startsWith("/"))
                webapps = webapps.substring(1).trim();


            String server = u.getProtocol() + "://" + u.getHost() + ":" + u.getPort() + "/" + webapps;

            String core = parserCore(url);
            if (core.startsWith("#"))
                core = core.substring(1).trim();

            List<GColumn> desc = ABaseNode.describeCore(server, core);


            // build the schema object:
            Map<String, String> schema = new LinkedHashMap<String, String>();
            for (GColumn gc : desc) {
                String type = gc.getType();
                String name = gc.getName();
                if (name.contains("__900807") || name.equalsIgnoreCase("_version_")) {

                } else {
                    schema.put(name, type);
                }
            }
            schema.remove("TMID_lastUpdated");
            schema.remove("TMID");

            Set<String> fields = schema.keySet();
            String[] descl = new String[fields.size()];
            int i = 0;
            for (String dd : fields) {
                descl[i++] = dd;
            }


            String absolutepath = GB.pwd() + "/" + path;
            GB.createTable("abuser", absolutepath, schema);

            ABTable new_table = new ABTable(absolutepath);
//
            HttpSolrClient client = new HttpSolrClient.Builder(url).build();
            for ( int docs=0; docs<MAX_DOCS; docs+=INCREMENT) {
                buildtablefromquery("*:*", descl, docs, INCREMENT, client, new_table);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            GB.print("Does not appear this a valid URL string : " + url);
        } catch (IOException e) {
            e.printStackTrace();
            GB.print("Could not connect to : " + url);
        }


        System.out.println(" create the table from a url string : " + command);

        return null;
    }

    private String parserCore(String url) {

        if (url.endsWith("/"))
            url = url.substring(0, url.length() - 1);
        int last = url.lastIndexOf('/');
        return url.substring(last + 1).trim();
    }

    private String parserServer(String url) {
        int index = url.indexOf(":");
        if (index < 0) {
            index = url.indexOf('/', 4);
        }
        String ser = url.substring(0, index);
        return ser;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        System.out.println(" create the table from a url string : " + cmd);
        return null;
    }


    /**
     */
    private void buildtablefromquery(String searchString, String[] cols, int start_document, int document_count,
                                     HttpSolrClient solr, ABTable in) {
        String host = ABProperties.getSolrURL();
        String sort = "TMID_lastUpdated desc";
        try {
            String fl = null;
            if (cols != null && cols.length > 0) {
                fl = "";
                for (String col : cols) {
                    fl += col + ",";
                }
                fl = fl.substring(0, fl.length() - 1);// trim the comma
            }
            String solr_url = host;
            if (!solr_url.endsWith("/")) {
                solr_url += "/";
            }

            searchString = searchString.replaceAll(" and ", " AND ");
            searchString = searchString.replaceAll(" not ", " NOT ");
            searchString = searchString.replaceAll(" or ", " OR ");
            // {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
            solr.setUseMultiPartPost(true);
            try {
                SolrPingResponse pingResp = solr.ping();
                if (pingResp.getStatus() < 0) {
                }
            } catch (Exception _se) {
                _se.printStackTrace();
            }
            if (searchString == null || searchString.length() <= 0)
                searchString = "*:*";
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("q", "" + searchString);
            params.set("start", start_document);
            params.set("rows", document_count);
            params.set("sort", sort);
            if (fl != null) {
                params.set("fl", fl.trim());
            } else
                params.set("fl", "*");

            params.set("wt", "json");
            // params.set("facet", true);
            QueryResponse response = solr.query(params);
            SolrDocumentList list = response.getResults();
            List<GColumn> fs = in.getFields();
            int index = 0;
            while ((index < list.size()) && alive) {
                HashMap data = new HashMap();
                SolrDocument document = list.get(index++);
                for (GColumn column : fs) {
                    Object col = document.get(column.getName());
                    data.put(column.getName(), col);
                }
                if (index % 1000 == 0)
                    in.append(data, true);
                else
                    in.append(data, false);
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }
}