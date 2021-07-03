package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.tree.TNode;

public class SetFieldProp implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		String[] lac = LAC.parse(command);
		String target = lac[0];
		String action = lac[1];
		String data = lac[2];
		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(GB.pwd() + "/" + target);
		if (node != null){
			if ( action.equalsIgnoreCase("setfieldprop")){
//				node.(data);
				GB.print("Link has changed " + data);
				nodes.save(node);
				return "Link changed.";
			}
		}
		return "Property set.";
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
