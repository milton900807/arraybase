package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.HashMap;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class ModeValue implements GBPlugin {

	private static GBLogger log = GBLogger.getLogger(ModeValue.class);

	public String exec(String command, String variable_key) {
		if (command.contains(".mode"))
			return findMode(command, variable_key);
		return "Cannot find mean for this";
	}

	private String findMode(String command, String variable_key) {
		int table = command.indexOf(".mode");
		String ttable = command.substring(0, table);
		int index = command.indexOf('(');
		int index2 = command.lastIndexOf(')');
		String params = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		temp2 = temp2.trim();
		ArrayList<String> fields = new ArrayList<String>();
		if (temp2.contains(",")) {
			String[] v = params.split(",");
			if (v == null || v.length <= 0) {
				v = new String[1];
				v[0] = params;
			}
			for (String s : v) {
				if (s != null && s.length() > 0)
					fields.add(s.trim());
			}
		} else
			fields.add(params.trim());
		String path = GB.pwd() + "/" + ttable.trim();
		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.print("Node with name " + path + " is not available");
			return null;
		}
		final String[] ff = fields.toArray(new String[fields.size()]);
		try {
			ABaseResults res = GBSearch.select(path, ff, "*:*", 0, 1, "" + ff[0]
					+ " desc");
			double maxValue = Double.MIN_VALUE;
			int maxCount = 0;
			int total = res.getTotalHits();
			int count = 0;
			while (count < total) {
				res = GBSearch.select(path, ff, "*:*", count, 1000000, ""
						+ ff[0] + " desc");
				ArrayList<GRow> rows = res.getValues();
				Number previous = null;
				for (GRow r : rows) {
					HashMap data = r.getData();
					Number ob = (Number) data.get(ff[0]);
					if (previous == null
							|| ob.doubleValue() != previous.doubleValue()) {
						ABaseResults vresults = GBSearch.select(path, ff, ff[0]
								+ ":" + ob.toString(), 0, 1, "" + ff[0]
								+ " desc");
						int num_vs = vresults.getTotalHits();
						if (num_vs > maxCount) {
							maxCount = num_vs;
							maxValue = ob.doubleValue();
						}
						previous = ob;
					}
				}
				count += rows.size();
			}
			GB.print("MODE: " + maxValue + "\t Count : " + maxCount);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return "Done.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
