package com.arraybase.shell;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.util.ABProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
/*

 This will replace the solr core
 */
public class ReplaceSolrCore implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {


        System.out.println (" replace solr core " + command );
        int st = command.indexOf("core");
        if ( st < 0 )
        {
            return "command is not correct -> " + command;
        }
        st += 4;
        String sub = command.substring(st+1);
        String[] vals = sub.split (" ");
        String replace_core = vals[0];
        String with_core = vals[1];


        if ( !replace_core.startsWith("/"))
        {
            replace_core = GB.pwd() + "/" + replace_core;
        }
        if ( !with_core.startsWith("/")){
            with_core = GB.pwd() + "/" + with_core;
        }
        if ( replace_core.startsWith("/")){
            ABTable table = new ABTable(replace_core);
            String replace_link = table.getDataLink();
            ABTable with_table = new ABTable(with_core);
            String new_linek = with_table.getDataLink();
//            table.setDataLink(new_linek);

            int i = replace_link.lastIndexOf('/');
            replace_core = replace_link.substring(i+1).trim();

            int j = new_linek.lastIndexOf('/');
            with_core = new_linek.substring(j+1).trim();
        }


        if ( replace_core != null )
        {
            replace_core = replace_core.trim();
        }
        if ( with_core != null )
        {
            with_core = with_core.trim();
        }
//        solr/admin/cores?action=SWAP&core=core1&other=core0
        String solrSite = ABProperties.get ( ABProperties.SOLRSITE );
        if ( !solrSite.endsWith( ("/"))){
            solrSite = solrSite + "/";
        }
        String swap = solrSite + "admin/cores?action=SWAP&core=" + replace_core + "&other="+ with_core;
        HttpURLConnection con = null;
        try {
            URL url = new URL(swap);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }catch ( MalformedURLException _em ){
            _em.printStackTrace();;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            con.disconnect();
        }

        String solrHome = ABProperties.get("coreRoot");
        System.out.println ( " replacing this : " + solrHome + "/" + replace_core + " with " + with_core );
        return null;
    }

    @Override
    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
