package com.arraybase.shell;

import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;

public class RMProp implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		if (args == null || args.length < 1) {
		} else if (args.length == 1) {
			TNode node = GB.getNodes().getNode(target);
			String key = "" + args[0];
			GB.removeNodeProperty(node.getNode_id(), key);
			GB.print("Node has been removed for node: " + node.getName()
					+ " propety : " + key);
			Map<String, String> props = NodeManager.getNodePropertyMap(node
					.getNode_id());
			GB.print(" Remaining properties for this node: ");
			GB.print(props);
		} else
			System.err
					.println(" Please provide only one property to remove at a time. (for now) ");

		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
