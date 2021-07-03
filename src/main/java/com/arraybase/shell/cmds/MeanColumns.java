package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;

public class MeanColumns extends SingleColumnCommand {

	Pattern p = Pattern.compile("^[a-zA-Z]+([0-9]+).*");
	private double sum = 0;

	public String exec(String cmd, String key) {
		if (cmd.startsWith("mean")) {
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
		while (it.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> increment = it.next();
			for (LinkedHashMap<String, Object> data : increment) {
				Set<String> ke = data.keySet();
				for (String k : ke) {
					MeanOb mo = getMeanOb(k);
					Object vobject = data.get(k);
					if (vobject != null && vobject instanceof Number) {
						Number n = (Number) vobject;
						mo.add(n.doubleValue());
					} else {
						String v = vobject.toString();
						if (v != null && v.length() > 0) {
							Double d = Double.parseDouble(v);
							mo.add(d);
						}
					}
					columns.put(k, mo);
				}
			}
		}
		Set<String> val = columns.keySet();
		for (String key : val) {
			MeanOb m = columns.get(key);
			GB.print(key + ":\t" + m.mean());
		}
	}

	LinkedHashMap<String, MeanOb> columns = new LinkedHashMap<String, MeanOb>();

	private MeanOb getMeanOb(String k) {
		MeanOb mo = columns.get(k);
		if (mo == null) {
			mo = new MeanOb();
			columns.put(k, mo);
		}
		return mo;
	}

	class MeanOb {
		double sum = 0;
		double count = 0;

		public MeanOb(String v) {
			try {
				sum = Double.parseDouble(v);
				count = 1;
			} catch (NumberFormatException ne) {
				ne.printStackTrace();
			}
		}

		public double mean() {
			return sum / count;
		}

		public MeanOb() {
		}

		public void add(double d) {
			sum += d;
			count++;
		}

	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
