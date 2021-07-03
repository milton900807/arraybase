package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBUtil;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;

public class Deletefacet implements GBPlugin {
	public final static String correctUsage = "target.delete(facet_name)";

	public String exec(String command, String variable_key)
			throws UsageException {
		// something.setFacet(tissue=something,someting2)
		String arguments = GBUtil.parsePArgs(command);
		int ind = arguments.indexOf(',');
		if (ind > 0) {
			String[] two = arguments.split(",");
			two[0] = two[0].trim();
			two[1] = two[1].trim();

		}
		String name = arguments;
		if (name == null)
			throw new UsageException(correctUsage);
		String target = GBUtil.parsePath(command);
		if (target == null)
			throw new UsageException(
					" Target object not found in the command : " + command);
		GB.print("Removing Node facet: " + name + " from node " + target);

		TNode node = GB.getNodes().getNode(target);
		NodeManager.deleteFacet(node.getNode_id(), name);
		GB.print("Facet has been saved");
		return "Facet saved";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
