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

public class MINMAXValue implements GBPlugin {

	private static GBLogger log = GBLogger.getLogger(MINMAXValue.class);

	public String exec(String command, String variable_key) {
		double max = Double.MIN_VALUE;

		if (command.contains(".min"))
			return findMin(command, variable_key);
		else
			return findMax(command, variable_key);

	}

	private String findMax(String command, String variable_key) {
		int table = command.indexOf(".max");
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

		if (fields == null || fields.size() <= 0) {
			GB.print("Provide a field you want to measure. ");
			return null;
		}

		String field = fields.get(0);
		String search = "*:*";
		if (fields.size() > 1) {
			search = fields.get(1);
		}

		String path = GB.pwd() + "/" + ttable.trim();
		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.print("Node with name " + path + " is not available");
			return null;
		}
		// final String[] ff = fields.toArray(new String[fields.size()]);
		String[] ff = { field };
		try {
			ABaseResults res = GBSearch.select(path, ff, search, 0, 1, ""
					+ field + " desc");
			if (res == null) {
				GB.print("Failed to find results for : " + search);
				return null;
			}

			int total = res.getTotalHits();
			ArrayList<GRow> rows = res.getValues();
			int current = 0;
			GRow r = rows.get(0);
			HashMap data = r.getData();
			Number ob = (Number) data.get(field);
			GB.print("\t\tMax: " + ob.toString());
			double max = ob.doubleValue();
			return "Max value" + max;
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

		String field = fields.get(0);
		String search = "*:*";
		if (fields.size() > 1) {
			search = fields.get(1);
		}

		String table_name = temp2;
		String path = GB.pwd() + "/" + ttable.trim();
		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.print("Node with name " + path + " is not available");
			return null;
		}
		// final String[] ff = fields.toArray(new String[fields.size()]);
		String[] ff = { field };
		try {
			ABaseResults res = GBSearch.select(path, ff, search, 0, 1, ""
					+ field + " asc");
			int total = res.getTotalHits();
			ArrayList<GRow> rows = res.getValues();
			int current = 0;
			GRow r = rows.get(0);
			HashMap data = r.getData();
			Number ob = (Number) data.get(field);
			GB.print("\t\tMin: " + ob.toString());
			double min = ob.doubleValue();
			return "Min value: " + min;
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return "Minimum could not be determined from the current dataset.";
	}

	public GBV execGBVIn(String cmd, GBV input) {

		Object object = input.get();
		GBSearchIterator it = (GBSearchIterator) object;
		String[] cols = it.getFields();
		if (cmd.equalsIgnoreCase("max"))
			it.setSearchSort(cols[0] + " desc");
		else
			it.setSearchSort(cols[0] + " asc");

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

									GB.print(cmd + ":\t" + d.doubleValue());
									NumberVar nv = new NumberVar(
											d.doubleValue());

									return nv;
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
