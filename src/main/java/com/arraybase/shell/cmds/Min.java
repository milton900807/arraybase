package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.shell.cmds.MeanColumns.MeanOb;

public class Min extends SingleColumnCommand {

	Pattern p = Pattern.compile("^[a-zA-Z]+([0-9]+).*");
	private double min = 0d;
	private boolean first = true;

	public String exec(String cmd, String key) {
		if (cmd.startsWith("min")) {
			int paramst = cmd.indexOf("(");
			int paramend = cmd.lastIndexOf(')');
			if (paramst > 0 && paramend > 0) {
				String searchString = cmd.substring(paramst + 1, paramend);
				return super.exec(searchString.trim(), key);
			}
		}
		return super.exec(cmd, key);
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

		GB.print("Min: \t" + min);

	}

	private void update(double dd) {
		if (first) {
			min = dd;
			first = false;
		} else {
			if (dd < min)
				min = dd;
		}
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
