package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.search.ABaseResults;

public class Head implements GBPlugin {

	/**
	 * head the data.
	 */
	public String exec(String command, String variable_key) {
		String[] params = GBIO.parseParams(command);
		String path = GBIO.parsePath(command);
		GBSearch search = GB.getSearch();
		GB.print ( "searching top lines from " + path);
		try {
			ABaseResults results = search.searchTable(path, "*:*", System.out,
					params, 0, 3, null, new SearchConfig(SearchConfig.RAW_SEARCH));
			if (results == null) {
				GB.print("No reults");
			} else
				GBIO.printResults(results, System.out, params);
		} catch (NotASearchableTableException e) {
			e.printStackTrace();
		}
		return "complete";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
