package com.arraybase.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBModule;
import com.arraybase.flare.*;
import com.arraybase.io.ABQFile;
import com.arraybase.lac.VersionManager;
import com.arraybase.shell.cmds.NodePropertyType;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.PathManager;

public class UpdateTableFromABQFile implements GBModule {


	private TNode node = null;


	public UpdateTableFromABQFile ( TNode _currentNode )
	{
		this.node = _currentNode;
	}
	public UpdateTableFromABQFile()
	{

	}


	public void exec(List<String> l) throws UsageException {
		throw new UsageException(
				" This method is currently not implemented... please use exec(Map<String, Object> ");
	}

	/**
	 * This will create a table from a set of parameters defined in the abq
	 * file.
	 */
	public void exec(Map<String, Object> l) throws UsageException {
		// HashMap<String, Object> param_map = new HashMap<String, Object>();
		// param_map.put(GBModule.EXPORT, exported_values);
		// param_map.put(GBModule.PATH, _path);
		// param_map.put(GBModule.QUERY, query);
		// ?
		Map<String, String> config = new HashMap<String, String>();
		for (String keys : l.keySet()) {
			String value = (String) l.get(keys);
			config.put(keys, value);
		}
		String user = (String) l.get(ABQFile.USER);
		String query = (String) l.get(ABQFile.QUERY);
		if (query == null)
			throw new UsageException(
					"Query was not found... Please provide a \"query\" parameter. ");
		final String path = config.get("path");
		if ( this.node == null && path != null ){
			this.node = GB.getNodes().getNode(path);
		}


		final String core = VersionManager.incrementCoreName(TMSolrServer.getCore(path));
		config.put(NodePropertyType.MODULE.name(), this.getModName());
		try {
			new SQLToSolr()
					.run(user,path,"" + query, config, null, query, "" + -1, new GBJobListener() {
						public void jobComplete(String msg) {
							node.setLink(core+".search(*)");
							GB.getNodes().save(node);
						}
					});


		} catch (DBProcessFailedException e) {
			e.printStackTrace();
		}
	}

	public String getModName() {
		return this.getClass().getCanonicalName();
	}

}
