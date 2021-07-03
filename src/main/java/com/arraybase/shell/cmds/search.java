package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.tm.NodeNotFoundException;

import java.io.PrintStream;
import java.util.*;

public class search implements GBPlugin {

	public String exec(String c, String variable_key) {

		int ti = c.indexOf('.');
		int t2 = c.indexOf('(');
		int t3 = c.indexOf(')');

		String target = c.substring(0, ti);
		String action = c.substring(ti + 1, t2);
		String search_string = c.substring(t2 + 1, t3);

		String post_string = c.substring(t3 + 1);

		ArrayList<String> proc = process(post_string);

		// {{ PARSE ARGUMENTS FROM THE SEARCH STRING }}
		if (search_string.equals("?")) {
			printArgs();
			return "help";
		}

		String sortString = null;
		if (search_string.contains(",")) {
			sortString = parseSortString(search_string);
		}

		String fields = c.substring(t3 + 1);
		if (fields != null && fields.length() > 0) {
			ArrayList<String> columns = new ArrayList<String>();
			columns = getColumns(fields);
			if (columns == null || columns.size() <= 0) {
				GB.print(" Doesn't look like there are any columns for the items you selected. ");
				return "fail";
			}
			String[] cols = columns.toArray(new String[columns.size()]);
			if (fields != null)
				fields = fields.trim();
			if (target != null)
				target = target.trim();
			GBSearch gs = GB.getSearch();
			String path = GB.pwd() + "/" + target;
			Map<String, String> node_props = GB.getNodeProps(path);
			SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
			config.setConfigProperties(node_props);

			try {
				// if (sortString == null)
				// System.out.println("\t---Search : " + search_string
				// + " sortstring : {default}");
				// else
				// System.out.println("\t---Search : " + search_string
				// + " sortstring : " + sortString);

				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString, cols, config);
				// while (it.hasNext()) {
				// ArrayList<LinkedHashMap<String, String>> increment = it
				// .next();
				// print(increment);
				// }

			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// System.out.println(" searchstring : " + search_string
			// + " sortstring : " + sortString);
			GBSearch gs = GB.getSearch();
			PrintStream out = System.out;
			String path = GB.pwd() + "/" + target;
			gs.searchTable(path, search_string, sortString, out, 0, 1000000, new SearchConfig(SearchConfig.NODE_CONFIG));
		}
		return "";
	}

	private ArrayList<String> getColumns(String fields) {

		ArrayList<String> columns = new ArrayList<String>();
		String[] temp = fields.split("\\[");
		for (String t : temp) {
			t = t.trim();
			if (t == null || t.length() == 0) {
			} else {
				int t4 = t.indexOf(']');
				if (t != null && t.length() > 0 && t4 > 0 && t4 <= t.length()) {
					String tt = t.substring(0, t4);
					if (tt != null) {
						tt = tt.trim();
						columns.add(tt);
					}
				}
			}
		}
		return columns;
	}

	private String parseSortString(String search_string) {
		String[] sp = search_string.split(",");
		search_string = sp[0];
		String sort_field = null;
		String direction = null;
		if (sp.length >= 2)
			sort_field = sp[1];
		if (sp.length >= 3)
			direction = sp[2];
		if (direction == null)
			direction = "desc";
		for (String s : sp) {
			if (s.contains("=")) {
				String[] var = s.split("=");
				String v = var[0].trim();
				if (v.equalsIgnoreCase("search"))
					search_string = var[1];
				else if (v.equalsIgnoreCase("sort")) {
					sort_field = var[1];
				} else if (v.equalsIgnoreCase("direction")) {
					direction = var[1];
				}
			}
		}
		String sortString = sort_field + " " + direction;
		return sortString;
	}

	private void print(ArrayList<LinkedHashMap<String, String>> increment) {
		for (LinkedHashMap<String, String> ls : increment) {
			Set<String> keys = ls.keySet();
			for (String key : keys) {
				GB.print(" " + ls.get(key) + "\t");
			}
			System.out.println(" ");
		}
	}

	private ArrayList<String> process(String post_string) {
		// TODO Auto-generated method stub
		return null;
	}

	private void printArgs() {
		GB.print("search=$string, sort=$field, sortd=[desc|asc]");
		GB.print("search=Comment:Jeff*, sort=row_index, direction=desc");

	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
