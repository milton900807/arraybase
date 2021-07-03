package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.qmath.NumberVar;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class SumValue implements GBPlugin {

	private static GBLogger log = GBLogger.getLogger(MEANValue.class);

	public String exec(String command, String variable_key) {
		double max = Double.MIN_VALUE;

		if (command.contains(".sum"))
			return findsum(command, variable_key);
		return "Cannot find mean for this";
	}

	private String findsum(String command, String variable_key) {
		int table = command.indexOf(".sum");
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
			GB.print("Value : " + value);
			return "Mean: ";
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return "Max value not found.";
	}

	public GBV execGBVIn(String ____not_used_for_this, GBV input) {
		// apob.search(percent_control:[30 TO 35])[percent_control]|mean
		// passes in an iterator of arraylist<linkedhashmaps> etc...
		// i.e. a se
		input.reset();
		Object object = input.get();
		if (!(object instanceof Iterator)) {
			GB.print("MeanValue Cannot handle this type of input object.");
		}
		Iterator it = (Iterator) object;
		Double sum = 0d;
		double index = 0;
		while (it.hasNext()) {
			Object ob = it.next();
			if (ob instanceof ArrayList) {
				ArrayList list = (ArrayList) ob;
				for (Object obb : list) {
					if (obb instanceof LinkedHashMap) {
						LinkedHashMap<String, Object> obbb = (LinkedHashMap<String, Object>) obb;
						Set<String> stss = obbb.keySet();
						for (String key : stss) {
							if (!ABFieldType.isReserved(key)) {
								Object value = obbb.get(key);
								// System.out.println(" k: " + key + " v : "
								// + value.toString());
								if (value instanceof Number) {
									try {
										Number d = (Number) value;
										Double db = d.doubleValue();
										if (db.isNaN() || db.isInfinite()) {
											GB.print("Warning: "
													+ db.toString()
													+ " found at n="
													+ index
													+ "  ...skipping this (n) as part of the mean.");

										} else {
											sum += d.doubleValue();
											index++;
										}
									} catch (ClassCastException _ex) {
										_ex.printStackTrace();
									}
								} else {
									if (value != null) {
										String va = value.toString();
										if (va != null && va.length() > 0) {
											try {
												Double dab = Double
														.parseDouble(va.trim());
												sum += dab.doubleValue();
												index++;
											} catch (NumberFormatException _nn) {
												GB.print("Warning... Not a number : "
														+ va);

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (sum <= 0 || index <= 0)
			return null;
		GB.printSub("Sum : " + sum);
		GB.printSub("N : " + index);
		return new NumberVar(sum);
	}

}
