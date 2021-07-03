package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.db.HBConnect;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import org.hibernate.Session;

import java.util.List;

public class mv implements GBPlugin {

	public String exec(String command, String variable_key) {
		// String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		String[] sp = command.split("\\s+");
		if (sp.length != 3) {
			GB.print(" Please provide two arguments... if you really want to move something from one location (1) to another (2) ");
			return "mv command";
		}
		String from = GB.absolute(sp[1].trim());
		String to = GB.absolute(sp[2].trim());
		GB.print(" " + from + " to  " + to);

		String tparent = GBPathUtils.getParent(to).trim();
		String fparent = GBPathUtils.getParent(from).trim();

		NodeManager manager = new NodeManager();
		GBNodes nodes = GB.getNodes();
		TNode toNode = manager.getNode(to);

		if (toNode != null
				&& toNode.getNodeType()
						.equalsIgnoreCase(SourceType.NODE.name())) {
			return moveToDir(manager, nodes, from, to);
		} else {
			// does the parent directory exist
			TNode toParentNode = manager.getNode(tparent);
			if (toParentNode == null)
				GBNodes.mkdir(GB.DEFAULT_UER, tparent);
			// move to the parent dir and then rename this node
			moveToDir(manager, nodes, from, tparent);
			String leaf = GBPathUtils.getLeaf(to);
			rename(manager, from.trim(), leaf);
		}
		return "mv complete";
	}

	private void rename(NodeManager manager, String from, String leaf) {
		TPath fromPath = manager.getPath(from);
		String fromParent = GBPathUtils.getParent(from);
		TPath parentFromPath = manager.getPath(fromParent);

		TNode fromNode = manager.getNode(fromPath.getNode_id());
		fromNode.setName(leaf);
		fromPath.setName(parentFromPath.getName() + "/" + leaf);
		Session sess = HBConnect.getSession();
		try {
            sess.beginTransaction();
			manager.merge(fromNode, sess);
			manager.merge(fromPath, sess);

			if (fromNode.getReference().size() > 0) {
				manager.updatePath(from, parentFromPath.getName() + "/"
						+ leaf);
			}

			sess.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(sess);
		}
	}

	private String moveToDir(NodeManager manager, GBNodes nodes, String from,
			String to) {
		TPath toPath = manager.getPath(to);
		TPath fromPath = manager.getPath(from);

		// if the destination director is the same as the current parent
		// directory
		// do nothing.
		if (toPath.getPath_id() == fromPath.getTMParent()) {
			return "move Complete";
		}

		TNode toNode = manager.getNode(toPath.getNode_id());
		TNode fromNode = manager.getNode(fromPath.getNode_id());
		String fromParentPath = GBPathUtils.getParent(from);
		TPath fromPathParent = manager.getPath(fromParentPath);
		TNode fromNodeParent = manager.getNode(fromPathParent.getNode_id());

		// update the from path
		fromPath.setName(toPath.getName() + "/" + fromNode.getName());
		fromPath.setTMParent(toPath.getPath_id());

		// update the parent references for from node
		List<Integer> refs = fromNodeParent.getReference();
		for (int i = 0; i < refs.size(); i++) {
			int v = refs.get(i);
			if (v == ((Long) fromNode.getNode_id()).intValue()) {
				refs.remove(i);
			}
		}
		fromNodeParent.setReference(refs);

		// update the tonode reference list.
		List<Integer> toNodeRefs = toNode.getReference();
		toNodeRefs.add(((Long) fromNode.getNode_id()).intValue());
		toNode.setReference(toNodeRefs);

		Session sess = HBConnect.getSession();
		try {
			sess.beginTransaction();
			manager.merge(fromNodeParent, sess);
			manager.merge(fromPath, sess);
			manager.merge(toNode, sess);

			// {{ we need to do a find and replace on all path_names}}
			// {{ if this is a directory!}}
			if (fromNode.getReference().size() > 0) {
				manager.updatePath(from,
						toPath.getName() + "/" + fromNode.getName());
			}

			sess.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            HBConnect.close(sess);
			manager.close();
		}
		return "mv Complete";

	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
