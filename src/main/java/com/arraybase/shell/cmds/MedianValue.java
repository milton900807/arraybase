package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBSearchIterator;
import com.arraybase.GBV;
import com.arraybase.qmath.NumberVar;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class MedianValue implements GBPlugin {

	private static GBLogger log = GBLogger.getLogger(MedianValue.class);

	public String exec(String command, String variable_key) {
		if (command.contains(".median"))
			return findMedian(command, variable_key);
		return "Cannot find mean for this";
	}

	private String findMedian(String command, String variable_key) {
		int table = command.indexOf(".median");
		String ttable = command.substring(0, table);
		int index = command.indexOf('(');
		int index2 = command.lastIndexOf(')');
		String params = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		temp2 = temp2.trim();
		ArrayList<String> fields = new ArrayList<String>();
		if (params.contains(",")) {
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

		String field = fields.get(0);
		String search_string = "*:*";
		if (fields.size() > 1) {
			search_string = fields.get(1);
		}
		final String[] ff = { field };
		try {
			ABaseResults res = GBSearch.select(path, ff, search_string, 0,
					1000000, "" + ff[0] + " desc");
			int total = res.getTotalHits();

			double median = 0.f;
			int half = total / 2;
			if (half % 2 == 1) {
				res = GBSearch.select(path, ff, search_string, half, 1, ""
						+ ff[0] + " desc");
				ArrayList<GRow> rows = res.getValues();
				GRow r = rows.get(0);
				HashMap data = r.getData();
				Number ob = (Number) data.get(ff[0]);
				double v = ob.doubleValue();
				median = v;
			} else {
				res = GBSearch.select(path, ff, search_string, half - 1, 2, ""
						+ ff[0] + " desc");
				ArrayList<GRow> rows = res.getValues();
				GRow r = rows.get(0);
				GRow r1 = rows.get(1);
				HashMap data = r.getData();
				HashMap data1 = r1.getData();
				Number ob = (Number) data.get(ff[0]);
				double v = ob.doubleValue();
				Number ob1 = (Number) data1.get(ff[0]);
				double v1 = ob1.doubleValue();
				median = (v + v1) / 2;

			}
			GB.print("Median: " + median);
			return "Median: " + median;

		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return "Max value not found.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// apob.search(percent_control:[30 TO 35])[percent_control]|mean
		// passes in an iterator of arraylist<linkedhashmaps> etc...
		Object object = input.get();
		GBSearchIterator it = (GBSearchIterator) object;
		String[] cols = it.getFields();
		// now sure what to do here.....
		
		// the median of a table is a bit strange... so I need to
		// think about this a bit.
		it.setSearchSort(cols[0] + " desc");
		int index = 0;

		int n = it.getTotal();
		int target_i = n;
		boolean even = false;
		if (n % 2 == 0) {
			target_i = n / 2;
			even = true;
		} else {
			target_i = (n + 1) / 2;
		}
		Double f1 = null;
		Double f2 = null;

		GB.print(" target_index : " + target_i + " n " + n);

		while (it.hasNext()) {
			Object ob = it.next();
			if (ob instanceof ArrayList) {
				ArrayList list = (ArrayList) ob;
				for (Object obb : list) {
					if (obb instanceof LinkedHashMap) {
						LinkedHashMap<String, Object> obbb = (LinkedHashMap<String, Object>) obb;
						Set<String> stss = obbb.keySet();
						for (String key : stss) {
							Object value = obbb.get(key);
							if (value instanceof Number) {
								try {
									Number d = (Number) value;
									index++;
									if (index >= target_i) {
										if (even) {
											double val = d.doubleValue();
											if (f1 == null)
												f1 = val;
											else {
												
												double median = (f1 + val) / 2.0d;
												GB.print(" Median : " + median);
												return new NumberVar(median);
											}
										} else {
											double median = d.doubleValue();
											GB.print(" Median : " + median);
											return new NumberVar(median);
										}
									}
								} catch (ClassCastException _ex) {
									_ex.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

}
