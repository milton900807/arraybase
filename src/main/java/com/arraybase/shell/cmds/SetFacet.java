package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.List;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBUtil;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;

public class SetFacet implements GBPlugin {

	public final static String correctUsage = "target.setFacet(facet_name=facetvalue1,facetvalue2...etc.)";

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

		GB.print("Node facet: " + arguments);
		String name = parseName(arguments);
		if (name == null)
			throw new UsageException(correctUsage);
		List<String> values = parseValues(arguments);
		if (values == null)
			throw new UsageException(
					"Failed to get the values from the facet arguments usage is: "
							+ correctUsage);

		String target = GBUtil.parsePath(command);
		if (target == null)
			throw new UsageException(
					" Target object not found in the command : " + command);

		TNode node = GB.getNodes().getNode(target);

		NodeManager.setFacet(node.getNode_id(), name, values);
		GB.print("Facet has been saved");
		return "Facet saved";
	}

	/**
	 * Parse the list of values
	 * 
	 * @param arguments
	 * @return
	 */
	private List<String> parseValues(String arguments) {
		int ind = arguments.indexOf('=');
		String sub = arguments.substring(ind + 1);
		if (sub == null)
			return null;
		if (sub != null)
			sub = sub.trim();
		String[] sp = sub.split(",");
		if (sp == null || sp.length <= 0)
			return null;

		ArrayList<String> list = new ArrayList<String>();
		for (String s : sp) {
			if (s != null)
				list.add(s.trim());
		}
		return list;
	}

	private String parseName(String arguments) {

		int ind = arguments.indexOf('=');
		String n = arguments.substring(0, ind);
		if (n != null)
			return n.trim();
		return null;
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
