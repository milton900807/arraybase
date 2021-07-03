package com.arraybase.shell.cmds;

import java.util.Map;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.google.gson.Gson;

public class SetABQValue implements GBPlugin {

	public String exec(String command, String variable_key___) {
		
		String[] args = GBIO.parseParams(command);
		
		if ( args==null || args.length != 2 )
			try {
				throw new UsageException("Please provide a name value pair");
			} catch (UsageException e) {
				e.printStackTrace();
				return "failed";
			}
		String a1 = args[0];
		String a2 = args[1];
		
		String target = GBIO.parsePath(command);
		// this will update an index. n
		// node.refresh(where MTID > node.max(MTID))
		if (target == null) {
			GB.print("Failed to find the target : " + target);
			return null;
		}
		TNode node = GB.getNodes().getNode(target);
		long node_id = node.getNode_id();
		// {{ 2. PULL THE PROPERTIES FROM THIS NODE }}
		NodeProperty nps = NodeManager.getNodeProperty(node_id, NodeProperty.NODE_GENERATOR);
		String json = nps.getProperty();
		Gson g = new Gson();
		Map installer = g.fromJson(json, Map.class);
		if (installer == null) {
			GB.print("Configuration error.... it looks like there isn't a configuration available to permit you to reload this node.  ");
			return "";
		}
		installer.put ( a1, a2 );
		String jsonString = g.toJson(installer);
		nps.setProperty (jsonString );
		NodeManager.saveNodeProperty(nps);
		Set keys = installer.keySet();
		return "Node Saved";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
