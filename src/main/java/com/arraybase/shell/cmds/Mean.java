package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.io.GBFileType;
import com.arraybase.shell.cmds.MeanColumns.MeanOb;
import com.arraybase.tab.ABFieldType;

//example: mean(primer_probes.search(CELL_LINE:$isis_cell_line AND TARGET:$mtid)[CT]{0-100})
public class Mean extends SingleColumnCommand {

	Pattern p = Pattern.compile("^[a-zA-Z]+([0-9]+).*");
	private double sum = 0.0;
	private double n = 1;

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
		n = 0.0;
		sum = 0;
		while (it.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> increment = it.next();
			for (LinkedHashMap<String, Object> data : increment) {
				Set<String> ke = data.keySet();
				for (String k : ke) {
					if (!ABFieldType.isReserved(k)) {
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
		}
		double v = sum / (n);
		GB.print("");
		GB.print("Mean: \t" + v);
		GB.print("\tn: \t" + n);
		GB.print("\tsum: \t" + sum);

	}

	private void update(double dd) {
		n++;
		sum += dd;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
