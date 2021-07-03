package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBRGX;

public class KillNode implements GBPlugin {

    public String exec(String command, String variable_key) {
        // {{ 1. GET THE TARGET OBJECT }}
        // System.out.println(command + " and " + variable_key);
        String[] args = command.split("\\s+");

        if (args != null && args.length == 3) {

            if (args[1].equalsIgnoreCase("core")) {
                DeleteCore decl = new DeleteCore();
                try {
                    return decl.exec(command, variable_key);
                } catch (UsageException e) {
                    e.printStackTrace();
                    GB.print(" Failed to remove the core: " + args[2]);
                    return "rm failed.";
                }
            }
        }


        if (args.length != 2) {
            GB.print("Command structure does not look right... only one argument should be provided.");
            return "not right";
        }
        // String target = GBIO.parsePath(command);
        String target = args[1];

        if (target == null) {
            GB.print("Failed to find the target : " + target);
        }
        if (!target.startsWith("/"))
            target = GB.pwd() + "/" + target;

        GB.print("\t Removing... " + target);
        TNode node = GB.getNodes().getNode(target);
        if (node == null) {
            GB.print("\t Node object not found... ");
            return "Nope";
        }
        String link = node.getLink();
        if (link != null && link.matches(GBRGX.LAC)) {
            String target_name = LAC.getTarget(link);
            NodeManager.deleteNode(target);
            delete(target_name);
        } else {
            NodeManager.deleteNode(target);
        }
        return "deed is done.";
    }

    public GBV execGBVIn(String cmd, GBV input) {
        return null;
    }

    private void delete(String core) {
        String solr_url = ABProperties.getSolrURL();
        String call_solr = solr_url + "admin/cores?action=UNLOAD&core=" + core
                + "&deleteIndex=true&deleteDataDir=true";
        try {
            TMSolrServer.callSolr(call_solr);
        } catch (SolrCallException e) {
            e.printStackTrace();
        }
    }

}
