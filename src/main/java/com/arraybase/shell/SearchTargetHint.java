package com.arraybase.shell;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tables.GBTables;

public class SearchTargetHint extends CommandOption {

	public SearchTargetHint() {
	}

	public String toString() {
		String buf = getCurrentBuffer();
		if (buf != null) {
			String c = buf;

			int ti = c.indexOf('.');
			int t2 = c.indexOf('(');
			int t3 = c.indexOf(')');
			if (t3 > 0) {
				// here is where we print the output options
			}
			String target = "";
			if (ti > 0) {
				target = c.substring(0, ti);
			}
			// -- --
			String path = target;
			if (!path.startsWith("/")) {
				path = GB.pwd() + "/" + path;
			}
			try {
				String dg = "";
				List<GColumn> table_properties = GBTables.describeTable(path);
				if (table_properties != null) {
					for (GColumn col : table_properties) {
						dg += "\t\t" + ABFieldType.simple( col.getType() ) + "\t " + col.getName() + "\t" +  "\n";
					}
				}
				return "\n"+dg;
			} catch (ConnectException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

}
