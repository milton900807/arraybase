package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.tm.tree.TNode;

public class SetLacCommand2 implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] lac = LAC.parse(command);
		String target = lac[0];
		String action = lac[1];
		String data = lac[2];
//		System.out.println(target);
//		System.out.println(action);
//		System.out.println(data);
		if (action.equalsIgnoreCase("setLink")) {
			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(GB.pwd() + "/" + target);
			if (node != null)
				node.setLink(data);
			GB.print("Link has changed " + data);
			nodes.save(node);
			return "Link changed.";
		} else if (action.equalsIgnoreCase("settype")) {
			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(GB.pwd() + "/" + target);
			if (node != null)
				node.setNodeType(data);
			GB.print("Type has changed " + data);
			nodes.save(node);
			return "Type changed.";
		} else if (action.equalsIgnoreCase("setuser")) {
			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(GB.pwd() + "/" + target);
			if (node != null) {
				node.setUser(data);
				node.setOwner(data);
			}
			GB.print("Owner changed to " + data);
			nodes.save(node);
			return "Owner changed.";
		} else if (action.equalsIgnoreCase("setdescription")) {
			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(GB.pwd() + "/" + target);
			if (node != null)
				node.setDescription(data);
			GB.print("Description has changed: " + data);
			nodes.save(node);
			return "Type changed.";
		} else if (action.equalsIgnoreCase("setname")) {
			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(GB.pwd() + "/" + target);
			if (node != null)
				node.setName(data);
			GB.print("Name has changed: " + data);
			nodes.save(node);
			return "Name changed.";
		}
		return "Link is set ";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
