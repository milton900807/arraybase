package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.NotASearchableTableException;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.TNode;

public class CacheTest implements GBPlugin {

	// mytable.cache(json,index:1)
	public String exec(String command, String variable_key) {

		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);

		TNode node = GB.getNodes().getNode(target);
		String key = args[0];
		String value = args[1];
		try {
			// GB.cache(target, whereClause, "cache", value);
			GB.cache(target, key, value);
		} catch (NotASearchableTableException e) {
			e.printStackTrace();
			GB.print(" Failed to cache.");
			return "Not cached";
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
			GB.print(" Failed to cache.");
			return "Not cached";
		}
		return "Cached.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
