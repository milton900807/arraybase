package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBLinkManager;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.UpdateIndexFailed;

public class RMWhere implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		GBNodes nodes = GB.getNodes();
		String core = nodes.getCore(target);
		String url = GB.getDefaultURL();
		
		String ht = url + core;
		if ( GBLinkManager.isFullyQualifiedURL( core )){
			ht = core;
		}
		
		if (args == null || args.length <= 0) {
			GB.print(" You must provide a search query as a parameter in order to delete:  \n\te.g. mytarget.rm(field1:something)"
					+ " where field1:something is the query that represents the records you want to remove.");
			return "Error";
		}
		try {
			GB.deleteCoreByQuery(ht, args[0]);
		} catch (UpdateIndexFailed e) {
			e.printStackTrace();
		}

		GB.print("Delete complete ");

		return "Delete complete";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
