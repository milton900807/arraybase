package com.arraybase.shell;

import java.util.ArrayList;
import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.var.gbv.GBVPointer;
import com.arraybase.search.ABaseResults;
import com.arraybase.search.SearchPointer;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeNotFoundException;

public class ArrayCommand implements GBPlugin {

	public String exec(String _cmd, String _variable) {
		int dotindex = _cmd.indexOf('.');
		int varindex = _cmd.indexOf('=');
		String variable = _cmd.substring(0, varindex);
		variable = variable.trim();

		String table = _cmd.substring(varindex + 1, dotindex);
		String field = _cmd.substring(dotindex + 1);

		table = table.trim();
		field = field.trim();

		GB.print("Printing the field " + field + " from table " + table);

		String path = GB.pwd() + "/" + table;
		GBSearch search = GB.getSearch();

		String[] st = new String[1];
		st[0] = field;
		try {
			ABaseResults rb = GBSearch.select(path, st, "*:*", 0, 10000, null);
			ArrayList<GRow> list = rb.getValues();
			ArrayList<GColumn> clist = rb.getColumns();
			for (int i = 0; i < st.length; i++) {
				for (GColumn gc : clist) {
					if (st[i].equalsIgnoreCase(gc.getName())) {
						st[i] = gc.getName();
					}
				}
			}
			ArrayList<String> vlist = new ArrayList<String>();
			for (GRow r : list) {
				Map data = r.getData();
				String sequence = (String) data.get(st[0]);
				System.out.println(" sequence : " + sequence);
			}
			SearchPointer searchptr = new SearchPointer(path, st, "*:*", 0,
					10000);
			GBVPointer ggbv = new GBVPointer(searchptr);
			GB.setVariable(variable, ggbv);
			GBIO.printResults(rb, System.out, st);
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return "Complete.";
	}

	/**
	 * Update the This will store the executed value in a temporary variable
	 */
	public GBV execGBVIn(String cmd, GBV input) {
		exec(cmd, getClass().getCanonicalName());
		return GB.getVariable(getClass().getCanonicalName());
	}

}
