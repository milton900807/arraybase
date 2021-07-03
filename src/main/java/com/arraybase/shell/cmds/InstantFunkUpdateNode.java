package com.arraybase.shell.cmds;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBModule;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UpdateTableFromABQFile;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.google.gson.Gson;

/**
 * @author milton
 * 
 */
public class InstantFunkUpdateNode implements GBPlugin {
	public String exec(String command, String variable_key) throws UsageException {
		// {{ 1. GET THE TARGET OBJECT }}
		String[] args = GBIO.parseParams(command);
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

		NodeProperty update_object = NodeManager.getNodeProperty(node_id,
				NodePropertyType.UPDATE.name);
		if (update_object != null)
			return update(node, update_object);
		else {
			Map<String, String> nps = NodeManager.getNodePropertyMap(node_id);
			if (nps != null)
				return update(node, nps, args);
		}
		return "Update failed... update property was not available and an update object was not found in the target : "+ target;
	}

	private String update(TNode node, NodeProperty update_object) throws UsageException {

		RunUpdateObject runUpdate = new RunUpdateObject();
		runUpdate.update ( node, update_object);
		return "UPDATE RUNNING";
	}

	private String update(TNode node, Map<String, String> nps, String[] args) {
		String json = nps.get(NodeProperty.NODE_GENERATOR);

		Gson g = new Gson();
		Map installer = g.fromJson(json, Map.class);
		if (installer == null) {
			GB.print("Configuration error.... it looks like there isn't a configuration available to permit you to reload this node.  ");
			return "";
		}
		Set keys = installer.keySet();

		LinkedHashMap<String, Object> reb = new LinkedHashMap<String, Object>();
		for (Object k : keys) {
			Object o = installer.get(k);
			String strk = k.toString();
			reb.put(strk, o);
		}

		if (args != null) {
			String params = args[0];

		} else {
			
			// we haven abq file so load it from the abq.
			String module = (String) reb.get(NodePropertyType.MODULE.name());
			
			
			
			GBModule m = new UpdateTableFromABQFile();
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
		}
		return "Node reloaded.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
