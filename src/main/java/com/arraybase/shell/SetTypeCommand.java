package com.arraybase.shell;

import java.util.ArrayList;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tree.TNode;

public class SetTypeCommand implements GBPlugin {

	public String exec(String command, String variable_key) {
		int index = command.indexOf('(');
		int index2 = command.indexOf(')');
		String temp = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		temp2 = temp2.trim();
		String type = temp;

		int t2 = command.indexOf("settype");
		if (t2 > 0) {

			String tteypt = LAC.getData(command);
			String target = LAC.getTarget(command);
			GBNodes nodes = GB.getNodes();
			if (target.startsWith("/")) {
				TNode node = nodes.getNode(target);
				if (node != null)
					node.setNodeType(tteypt);
				GB.print("Type has changed " + tteypt);
				nodes.save(node);

			} else {
				TNode node = nodes.getNode(GB.pwd() + "/" + target);
				if (node != null)
					node.setNodeType(tteypt);
				GB.print("Type has changed " + tteypt);
				nodes.save(node);
			}

		} else {
			int type_index = command.indexOf("type") + 4;
			String table = command.substring(type_index, index);
			table = table.trim();

			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(GB.pwd() + "/" + table);
			if (node != null)
				node.setNodeType(type);
			GB.print("Type has changed " + type);
			nodes.save(node);
		}
		return "Type changed.";

	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
