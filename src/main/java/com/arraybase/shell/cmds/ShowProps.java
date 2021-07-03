package com.arraybase.shell.cmds;

import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.TNode;

public class ShowProps implements GBPlugin {

	public String exec(String command, String variable_key) {
		String target = GBIO.parsePath(command);
		TNode node = GB.getNodes().getNode(target);
		GB.print("Node props:");
		if (node == null) {
			GB.print(" Failed to find the node : " + target);
			return "Failed to find the target";
		}
		Map<String, String> properties = NodeManager.getNodePropertyMap(node
				.getNode_id());
		GB.print(properties);
		if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
				|| node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)) {
			String core = TMSolrServer.getCore(target);
			Map<String, String> props = TableManager.getTableProperties(core);
			GB.print("Table props:");
			GB.print(props);
		} else {
			GB.print("You may only check the properties of table nodes.");
		}
		return null;
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
