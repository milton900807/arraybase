package com.arraybase.shell.cmds;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBModule;
import com.arraybase.GBModuleBuildFactory;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.google.gson.Gson;

public class RefreshNode implements GBPlugin {

    public String exec(String command, String variable_key) {
        // {{ 1. GET THE TARGET OBJECT }}
        String[] args = GBIO.parseParams(command);
        String target = GBIO.parsePath(command);
        if (target == null) {
            GB.print("Failed to find the target : " + target);
            return null;
        }

        System.out.println(target);


        TNode node = GB.getNodes().getNode(target);
        long node_id = node.getNode_id();
        // {{ 2. PULL THE PROPERTIES FROM THIS NODE }}
        Map<String, String> nps = NodeManager.getNodePropertyMap(node_id);
        System.out.println ( " node id " + node_id );
        String json = nps.get(NodeProperty.NODE_GENERATOR);
        Gson g = new Gson();
        Map installer = g.fromJson(json, Map.class);
        if (installer == null) {
            GB.print("Configuration error.... it looks like there isn't a prop configuration available to permit you to reload this node.  ");
            return "";
        }
        Set keys = installer.keySet();

        LinkedHashMap<String, Object> reb = new LinkedHashMap<String, Object>();
        for (Object k : keys) {
            Object o = installer.get(k);
            String strk = k.toString();
            reb.put(strk, o);
        }
        String module = "ABQ_UPDATE";////(String) reb.get(NodePropertyType.MODULE.name());
        GB.print("Refreshing with module : " + module);


        GBModule m = GBModuleBuildFactory.create("ABQ_UPDATE", node);
        if (m == null) {
            GB.print("Configuration error.. the refresh was invalid since I could not find module : "
                    + module
                    + "... which is the software required to load this object.");
            return null;
        } else {
            try {
                m.exec(reb);
            } catch (UsageException e) {
                e.printStackTrace();
                GB.print("Configuration error.. the refresh failed to successfully execute module : "
                        + module
                        + "... which is the software required to load this object.");
            }
        }
        return "Node reloaded.";
    }

    public GBV execGBVIn(String cmd, GBV input) {
        return null;
    }

}
