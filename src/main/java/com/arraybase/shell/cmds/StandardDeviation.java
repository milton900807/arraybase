package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.qmath.NumberVar;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

import java.util.*;

public class StandardDeviation implements GBPlugin {

	private static GBLogger log = GBLogger.getLogger(StandardDeviation.class);

	public String exec(String command, String variable_key) {
		double max = Double.MIN_VALUE;
		if (command.contains(".mean"))
			return findMean(command, variable_key);
		return "Cannot find mean for this";
	}
	private String findMean(String command, String variable_key) {
		int table = command.indexOf(".stdv");
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
		try {
			// pass in the search term if we have one.
			String field = fields.get(0);
			String search = "*:*";
			if (fields.size() > 1) {
				search = fields.get(1);
			}
			final String[] ff = { field };

			double value = 0.0f;
			int total = 1;
			try {
				Iterator<ArrayList<LinkedHashMap<String, Object>>> values = GBSearch
						.searchAndDeploy(path, search, ff[0] + " desc", ff,
								new SearchConfig(SearchConfig.RAW_SEARCH));
				while (values.hasNext()) {

					ArrayList<LinkedHashMap<String, Object>> ob = values.next();
					for (LinkedHashMap<String, Object> row : ob) {
						Object obvalue = row.get(ff[0]);
						if (obvalue != null) {
							Double db = Double.parseDouble(obvalue.toString());
							// GB.print ( db + "\t  / \t" + total);
							value += db;
							total++;
						}

					}

				}

			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NumberFormatException _n) {
				_n.printStackTrace();
				GB.print("An object used for the calculation was not a number... calculation will not be correct.");
			}
			double mean = (value / total);
			GB.print("Value : " + value + "/" + total);
			GB.print("Mean: " + mean);
			return "Mean: ";

		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return "Max value not found.";
	}

	private String findMin(String command, String variable_key) {
		int table = command.indexOf(".min");
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
		String table_name = temp2;
		String path = GB.pwd() + "/" + ttable.trim();
		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.print("Node with name " + path + " is not available");
			return null;
		}
		final String[] ff = fields.toArray(new String[fields.size()]);
		try {
			ABaseResults res = GBSearch.select(path, ff, "*:*", 0, 1, ""
					+ ff[0] + " asc");
			int total = res.getTotalHits();
			ArrayList<GRow> rows = res.getValues();
			int current = 0;
			GRow r = rows.get(0);
			HashMap data = r.getData();
			Number ob = (Number) data.get(ff[0]);
			GB.print("\t\tMin: " + ob.toString());
			double min = ob.doubleValue();
			return "Min value: " + min;
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return "Minimum could not be determined from the current dataset.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// apob.search(percent_control:[30 TO 35])[percent_control]|mean
		// passes in an iterator of arraylist<linkedhashmaps> etc...
		// i.e. a se

		MEANValue meanv = new MEANValue();
		NumberVar mvar = (NumberVar) meanv.execGBVIn(cmd, input);
		double mean = mvar.get();

		// make sure we are iterating from the start.
		input.reset();

		Object object = input.get();
		Iterator it = (Iterator) object;

		Double sum = 0d;
		double n = 0;
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
									Double db = d.doubleValue();
									if (db.isNaN() || db.isInfinite()) {
										GB.print("Warning: "
												+ db.toString()
												+ " found at n="
												+ n
												+ "  ...skipping this (n) as part of the mean.");

									} else {
										sum += ((d.doubleValue() - mean) * (d
												.doubleValue() - mean));
										n++;
									}
								} catch (ClassCastException _ex) {
									_ex.printStackTrace();
								}
							}else
							{
								GB.print ( "Not a number " + value);
							}
						}
					}
				}
			}
		}

		double s = Math.sqrt(sum / (n - 1));
		GB.print(" Standard Deviation : " + s);
		GB.printSub("n : " + n);
		NumberVar stdvN = new NumberVar(s);
		return stdvN;
	}

}
