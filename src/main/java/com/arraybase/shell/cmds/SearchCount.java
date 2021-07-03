package com.arraybase.shell.cmds;

import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.SearchConfig;

public class SearchCount implements GBPlugin {

	public String exec(String command, String variable_key) {

		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);

		String path = target;
		String search_string = args[0];
		String field = args[1];

		GBSearch search = GB.getSearch();
		LinkedHashMap<String, Long> counts = GBSearch.facet(path, search_string, field);

		Set<String> keys = counts.keySet();
		for (String k : keys) {
			Long i = counts.get(k);
			GB.print(k + " " + i);
		}
		return "complete";
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
