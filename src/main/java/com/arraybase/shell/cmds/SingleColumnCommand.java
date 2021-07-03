package com.arraybase.shell.cmds;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBSearchIterator;
import com.arraybase.GBV;
import com.arraybase.NotASearchableTableException;
import com.arraybase.SearchConfig;
import com.arraybase.tm.NodeNotFoundException;


// example:  mean(primer_probes.search(CELL_LINE:$isis_cell_line AND TARGET:$mtid)[CT]{0-100})
public class SingleColumnCommand implements GBPlugin {

	public String exec(String c, String variable_key) {
		search2 ss = new search2();
		GBV<Iterator> it = ss.execGBVIn(c, null);
		calculate(it.get());
		return "Single field search complete.";
	}

	public void calculate(Iterator<ArrayList<LinkedHashMap<String, Object>>> it) {

	}

	/**
	 * @param fields
	 * @param columns
	 * @return
	 */
	private String getColumns(String fields, ArrayList<String> columns) {
		String f = fields;
		if ((f == null) || !(f.contains("[") && f.contains("]"))) {
			return null;
		}
		int index = f.indexOf('[');
		int lindex = f.indexOf(']');
		String field = f.substring(index + 1, lindex);
		columns.add(field);
		String t = f.substring(lindex + 1);
		int i2 = t.indexOf('[');
		if (i2 < 0)
			return t;
		return getColumns(t, columns);
	}

	private ArrayList<String> getColumns(String fields) {
		ArrayList<String> columns = new ArrayList<String>();
		getColumns(fields, columns);
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

	private void print(ArrayList<LinkedHashMap<String, Object>> increment,
			String post_string) {
		for (LinkedHashMap<String, Object> ls : increment) {
			Set<String> keys = ls.keySet();
			String ps = post_string;
			for (String key : keys) {
				ps = ps.replace("[" + key + "]", ls.get(key) + "\t\t");
			}
			System.out.println(" " + ps);
		}
	}

	private void print(Set<String> keys) {
		String ps = "";
		for (String key : keys) {
			ps += "[" + key + "]" + "\t\t";
		}
		GB.print(ps);
	}

	private void print(ArrayList<LinkedHashMap<String, Object>> increment) {
		for (LinkedHashMap<String, Object> ls : increment) {
			Set<String> keys = ls.keySet();
			String ps = "";
			for (String key : keys) {
				ps += "[" + ls.get(key) + "]" + "\t\t";
			}
			GB.print(ps);
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
