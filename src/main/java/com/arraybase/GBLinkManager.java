package com.arraybase;

import java.util.Date;

import com.arraybase.db.util.SourceType;
import com.arraybase.tm.tree.TNode;

public class GBLinkManager {

	/**
	 * Save a link onto a node.
	 * 
	 * @param _args
	 */
	public static void setLink(String[] _args) {
		String userid = _args[1];
		String path = _args[2];
		String link = _args[3];
		GB.printUsage("Setting the link : " + link + " \n to path : " + path
				+ "\n");

		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.printUsage("Path does not exist.  If you want to create a node first please use the mknode command");
		}
		node.setLink(link);
		node.setUser(userid);
		node.setLastEditedDate(new Date());
		GBNodes mn = GB.getNodes();
		mn.save(node);
		GB.print("Node is saved. ");
	}

	/**
	 * gb $core_name $path
	 * 
	 * @param _args
	 */
	public static void linkCore(String[] _args) {
		String core_name = _args[1];
		String path = _args[2];
		GB.print("" + createSolrLink(path, core_name));
	}

	public static TNode createSolrLink(String path, String _corename) {
		return GBNodes.mkNode(GB.getDefaultUser(), path, _corename
				+ ".search(*:*)", SourceType.DB);
	}

	/**
	 * Link the node with the a particular path -
	 * 
	 * @param user_name
	 * @param node_idl
	 * @param path
	 * @return
	 */
	private static String linkNode(String user_name, long node_idl, String path) {
		GBNodes nodes = GB.getNodes();

		nodes.linkNode(user_name, node_idl, path);
		return null;
	}

	public static void mkReference(String[] _args) {
		if (_args.length < 3) {
			GB.printUsage(GB.MK_REF_USAGE);
			return;
		}
		String user_name = _args[1];
		String parent_path = _args[2];
		String[] linkpaths = buildList(_args, 3);
		System.out.println("  " + mkRef(user_name, parent_path, linkpaths));
		System.out.println(" Links are complete ");
	}

	private static String mkRef(String user_name, String parent_path,
			String[] linkpaths) {
		GBNodes n = GB.getNodes();

		// first check that we have the parent node that we're going
		// to add the references.
		TNode pnode = n.getNode(parent_path);
		if (pnode == null) {
			System.err.print(" Failed to find the path " + parent_path);
			return "Failed to create links.";
		}
		System.out.println("Parent path: -[" + pnode.getNode_id() + "]-  "
				+ pnode.getName());
		// get the tnode object via hibernate and save.
		n.saveReferences(pnode, linkpaths);
		return "Links created in " + parent_path;
	}

	/*
	 * Helper method for building the args list for remaining arguments/
	 * 
	 * @param _args
	 * 
	 * @param startIndex
	 * 
	 * @return
	 */
	private static String[] buildList(String[] _args, int startIndex) {
		String[] tt = new String[_args.length - startIndex];
		for (int i = startIndex; i < _args.length; i++) {
			tt[i - startIndex] = _args[i];
		}
		return tt;
	}

	public static void linkNode(String[] _args) {
		String user_name = _args[1];
		String node_id = _args[2];
		String path = _args[3];
		try {
			long node_idl = Long.parseLong(node_id);
			System.out.println(" " + linkNode(user_name, node_idl, path));
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	public static boolean isFullyQualifiedURL(String core) {
		if ( core==null || core.length() <= 0)
			return false;
		return core.startsWith("http://") || core.startsWith("https://");
	}
	public static boolean isValidURL(String core) {
		return core.startsWith("http://") || core.startsWith("https://");
	}

	public static String getSolrRoot(String core) {
		// http://192.168.1.105:8983/solr/#/screening_data
		int t = core.indexOf("(");
		if (t > 0) {
			String sub = core.substring(0, t);
			int la = sub.lastIndexOf('/');
			String root = core.substring(0, la) + "/";

			
			return root;
		} else {
			int la = core.lastIndexOf("/");
			String root = core.substring(0, la) + "/";
			return root;
		}
	}

	public static String getCoreLK(String core) {
		int la = core.lastIndexOf("/");
		String root = core.substring(la + 1);
		return root;
	}

	/**
	 *  Simple utility function toi 
	 * @param url
	 * @param core
	 * @return
	 */
	public static String concat(String url, String core) {
		
		if ( core.startsWith("/"))
			core= core.substring(1).trim();
		if ( url.endsWith ( "/"))
			return url + core;
		
		
		return url + "/" + core;
	}

}
