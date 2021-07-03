package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.HBConnect;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.FileUtilities;
import org.hibernate.Session;

import java.io.File;

public class SetNodePropertyCommand implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] cmd = command.split("\\s+");
		if (cmd.length == 4) {

			String type = cmd[1];
			String filename = cmd[2];
			String table_name = cmd[3];

			String absolute_table = GB.pwd() + "/" + table_name;
			if (table_name.startsWith("/")) {
				absolute_table = table_name;
			}
			TNode node = GB.getNodes().getNode(absolute_table);
			if (node == null) {
				GB.print("The table name : " + absolute_table
						+ " was not found. ");
				return "error";
			} else {
				// attempt to load the file
				File f = new File(filename);
				if (f.exists()) {
					NodeProperty p = new NodeProperty();
					p.setNode_id(node.getNode_id());
					String s = FileUtilities.readFile(f);
					p.setProperty(filename);
					p.setFile(s.getBytes());
					p.setType(NodePropertyType.BUILD.name);
					Session sess = HBConnect.getSession();
					try {
						sess.beginTransaction();
						sess.save(p);
						sess.getTransaction().commit();
						GB.print("" + NodePropertyType.BUILD
								+ " has been set on node " + absolute_table);
					} finally {
						HBConnect.close(sess);
					}
					return "Complete";

				} else {
					GB.print(" Could not find the file : "
							+ f.getAbsolutePath());
					return "error";
				}
			}

		}
		return "";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
