package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.HBConnect;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

public class rm implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] sp = command.split("\\s+");
		if (sp.length != 2) {
			GB.print(" Usage: rm {something]");
			return "rm command";
		}
		String rmn = GB.absolute(sp[1].trim());
		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(rmn);
		TPath node_path = nodes.getPath(node);
		if (node_path == null)
			node_path = nodes.getPath(rmn);

		if (node == null)
			GB.print("Failed to find the Node object: " + rmn);
		if (node_path == null)
			GB.print(" Failed to find the Path object for: " + rmn);

		// remove the references
		String parent = GBPathUtils.getParent(rmn);
		GB.print("Removing references from parent " + parent);

		TNode parent_node = nodes.getNode(parent);
		if (parent_node != null) {
			ArrayList<Integer> remove = new ArrayList<Integer>();
			List<Integer> refs = parent_node.getReference();
			for (int i = 0; i < refs.size(); i++) {
				int l = refs.get(i);
				TNode n = nodes.getNode(l);
				if (n == null)
					remove.add(i);
			}
			for (Integer index : remove) {
				refs.remove(index);
			}
			parent_node.setReference(refs);
			Session sess = HBConnect.getSession();
			try {
				sess.beginTransaction();
				Criteria c = sess.createCriteria(TNode.class).add(
						Restrictions.eq("node_id", parent_node.getNode_id()));
				List l = c.list();
				if (l != null || l.size() > 0) {
					TNode pnode = (TNode) l.get(0);
					pnode.setReference(refs);
					Criteria cc = sess.createCriteria(TNode.class).add(
							Restrictions.eq("node_id", node.getNode_id()));
					List ll = cc.list();
					if (ll != null) {
						TNode remove_me = (TNode) ll.get(0);
						sess.delete(remove_me);
					}
					sess.update(pnode);
					sess.flush();
				}
				sess.getTransaction().commit();
			} finally {
				HBConnect.close();
			}
			GBPathUtils.remove(node_path);
		}
		return "rm complete";

	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
