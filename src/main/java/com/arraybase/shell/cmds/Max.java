package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearchIterator;
import com.arraybase.GBV;
import com.arraybase.qmath.NumberVar;
import com.arraybase.shell.cmds.MeanColumns.MeanOb;

public class Max extends SingleColumnCommand {

	Pattern p = Pattern.compile("^[a-zA-Z]+([0-9]+).*");
	private double max = -1d;
	boolean first = true;

	public String exec(String cmd, String key) {
		if (cmd.startsWith("max")) {
			int paramst = cmd.indexOf("(");
			int paramend = cmd.lastIndexOf(')');
			if (paramst > 0 && paramend > 0) {
				String searchString = cmd.substring(paramst + 1, paramend);
				return super.exec(searchString.trim(), key);
			}
		}
		GB.print("Usage:  max(search_string)");
		return "";
	}

	public void calculate(Iterator<ArrayList<LinkedHashMap<String, Object>>> it) {
		first = true;
		while (it.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> increment = it.next();
			for (LinkedHashMap<String, Object> data : increment) {
				Set<String> ke = data.keySet();
				for (String k : ke) {
					Object vobject = data.get(k);
					if (vobject != null && vobject instanceof Number) {
						Number n = (Number) vobject;
						double dd = n.doubleValue();
						update(dd);
					} else {
						String v = vobject.toString();
						if (v != null && v.length() > 0) {
							Double d = Double.parseDouble(v);
							update(d);
						}
					}
				}
			}
		}

		GB.print("Max: \t" + max);

	}

	private void update(double dd) {
		if (first) {
			max = dd;
			first = false;
		} else {
			if (dd > max)
				max = dd;
		}
	}

	public GBV execGBVIn(String cmd, GBV input) {
		
		Object object = input.get();
		GBSearchIterator it = (GBSearchIterator) object;
		String[] cols = it.getFields();
		it.setSearchSort(cols[0] + " desc");
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
									
									GB.print("Max:\t" + d.doubleValue());
									NumberVar nv = new NumberVar(d.doubleValue());
									
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
