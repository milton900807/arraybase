package com.arraybase.shell.cmds;


import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;

public class NodeSetProperty implements GBPlugin {

	// node.setproperty(propertyname, value)
	public String exec(String command, String variable_key) {

		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		if (args == null || args.length < 1) {
			// count the rows of the table
		} else if (args.length == 2) {
			// count the number of particular field types
			TNode node = GB.getNodes().getNode(target);
			String field = "" + args[0];
			String value = "" + args[1];
			
			GB.setNodeProperty(node.getNode_id(), NodeProperty.getFieldLink(field), value);
			GB.print("Saved: " + node.getName() + " field link -->" + field + "="
					+ value);
			if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
					|| node.getNodeType().equalsIgnoreCase(
							SourceType.TABLE.name)) {
				// String core = TMSolrServer.getCore(target);
				// TableManager.addTableProperty(core, key.trim(),
				// value.trim());
			} else {
				System.err
						.println(" Not a table node object.  This command only workds on table objects ");
			}
		} else
			System.err.println(" wrong args. ");

		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
