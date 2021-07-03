package com.arraybase.shell;

import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.util.ABProperties;

/**
 * Created by jmilton on 4/26/2016.
 */
public class SetSolr implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {

        String[] sp = command.split("=");
        if (sp != null && sp.length == 2) {
            ABProperties.setSolrSite(sp[1].trim());
            return "Solr Set to = " + sp[1];
        } else
            throw new UsageException(" Please provide a url in the format solr=myurlstring");
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
