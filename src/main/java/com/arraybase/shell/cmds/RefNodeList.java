package com.arraybase.shell.cmds;

import java.util.List;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.tm.tree.TNode;

public class RefNodeList implements GBPlugin {

	public String exec(String command, String variable_key) {
		String target = GBIO.parsePath(command);
		String ftar = GB.pwd() + "/" + target;
		List<TNode> nodes = GBNodes.getRefNodes(ftar);
		for (TNode nd : nodes) {
			System.out.println("-\t" + nd.getName());
		}
		return "done";
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
