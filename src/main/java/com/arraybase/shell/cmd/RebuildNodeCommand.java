package com.arraybase.shell.cmd;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBTableLoader;
import com.arraybase.GBV;
import com.arraybase.db.HBConnect;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.cmds.NodePropertyType;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RebuildNodeCommand implements GBPlugin {

	public String exec(String command, String variable_key) {

		String[] co = command.split("\\s+");
		String node = co[1];

		String node_path = node;
		if (!node.startsWith("/")) {
			node_path = GB.pwd() + "/" + node;
		}
		TNode n = GB.getNodes().getNode(node_path);
		if (n != null) {

			Session session = HBConnect.getSession();
			try {
				session.beginTransaction();
				Criteria c = session.createCriteria(NodeProperty.class);
				c.add(Restrictions.eq("node_id", n.getNode_id())).add(
						Restrictions.eq("type", NodePropertyType.BUILD.name()));
				List l = c.list();

				if (l == null || l.size() <= 0) {
					GB.print("It appears there is no build file associated with this node.");
					return "";
				}

				ArrayList<NodeProperty> list = new ArrayList<NodeProperty>();
				for (int index = 0; index < l.size(); index++) {
					NodeProperty property = (NodeProperty) l.get(index);
					list.add(property);
				}

				for (NodeProperty property : list) {
					byte[] b = property.getFile();
					String file = new String(b);
					try {
						Properties prop = new Properties();
						StringReader istr = new StringReader(file);
						prop.load(istr);
						try {
							GBTableLoader.loadABQ(GB.DEFAULT_UER, prop, node);
						} catch (UsageException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} finally {
				HBConnect.close(session);
			}
		} else {
			GB.print("There is no node with name : " + node_path + "\t[Type="
					+ NodePropertyType.BUILD.name() + "]");
		}

		return null;
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
