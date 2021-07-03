package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.tm.tree.TNode;

public class NodeSettings implements GBPlugin {

	public String exec(String command, String variable_key) {

		String[] args = GBIO.parseParams(command);
		if (args.length != 1) {
			System.err
					.println(" Please provide one argument that is the description of the node object ");
			return "fail";
		}
		String target = GBIO.parsePath(command);
		if (args == null || args.length < 1) {
		} else if (args.length == 1) {
			String field = args[0];

			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(target);
			node.setDescription(args[0]);
			nodes.save(node);

		}
		return "desc set.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
