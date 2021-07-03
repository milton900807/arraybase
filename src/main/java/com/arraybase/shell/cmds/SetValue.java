package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.LoaderException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;

public class SetValue implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		if (args == null || args.length < 1) {
			GB.print("No arguments available...");
			return "Failed";
		}
		if (args.length >= 2) {
			TNode node = GB.getNodes().getNode(target);
			String schema = TMSolrServer.getCore(target);
			String key = "" + args[0];
			String value = "" + args[1];
			if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
					|| node.getNodeType().equalsIgnoreCase(
							SourceType.TABLE.name)) {
				String solr = ABProperties.get(ABProperties.SOLRSITE);
				TMSolrServer so = new TMSolrServer(solr);
				int w = args.length - 1;
				String whereClause = args[w];
				if (whereClause != null)
					whereClause = whereClause.trim();
				for (int i = 0; i < w; i++) {
					String arg3 = args[i];
					int findex = arg3.indexOf('=');
					String column = arg3.substring(0, findex);
					String field_value = arg3.substring(findex + 1);
					column = column.trim();
					field_value = field_value.trim();
					try {
						so.findAndReplace(schema, column, null, whereClause,
								field_value);
					} catch (LoaderException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			GB.print("Incorrect arguments.  You need to have at least 3 args.. where the last argument is the \"where\" clause.");
		}
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
