package com.arraybase.shell;

import java.util.ArrayList;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBTableLoader;
import com.arraybase.GBV;
import com.arraybase.var.gbv.GBVPointer;
import com.arraybase.var.gbv.GBVTable;
import com.arraybase.search.ABaseResults;
import com.arraybase.search.SearchPointer;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeNotFoundException;

public class PrintFieldCommand implements GBPlugin {

	public String exec(String s, String _variable_) {
		int index = s.indexOf('[');
		int left_br = s.indexOf('[');
		int right_br = s.indexOf(']');
		String path = s.substring(0, index);
		String fields = s.substring(left_br + 1, right_br);
		String[] f = fields.split(",");
		String[] t = path.split("/");
		String table = t[t.length - 1];

		if (f != null && f.length > 1) {
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].trim();
			}
		}

		System.out.println(" path:\t " + path);
		System.out.println(" table:\t " + table);
		for (int i = 0; i < f.length; i++) {
			GB.print(" field:\t" + f[i]);
		}

		try {
			ABaseResults results = GB.getSearch().select(path, f, "*:*", 0, 10000,
					null);
			int total_hits = results.getTotalHits();
			GBIO.printResults(results, System.out, f);
			while (index < total_hits) {
				results = GB.getSearch().select(path, f, "*:*", index, 10000,
						null);
				GBIO.printResults(results, System.out, f);
				index += 10000;
			}

		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}

		return "Complete";
	}

	/**
	 * Update the This will store the executed value in a temporary variable
	 */
	public GBV execGBVIn(String s, GBV __input) {

		int index = s.indexOf('[');
		int left_br = s.indexOf('[');
		int right_br = s.indexOf(']');
		String path = s.substring(0, index);
		String fields = s.substring(left_br + 1, right_br);
		String[] f = fields.split(",");
		String[] t = path.split("/");
		String table = t[t.length - 1];

		if (f != null && f.length > 1) {
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].trim();
			}
		}
		if (__input != null) {
			Object object = __input.get();
			if (object instanceof GBVTable) {
				GBVTable v = (GBVTable) object;
				ArrayList<GRow> rr = v.getRows();
				try {
					GBTableLoader.append(path, rr);
				} catch (NodeNotFoundException e) {
					e.printStackTrace();
				}
			} else if (object instanceof GBVPointer) {
				GBVPointer ob = (GBVPointer) object;
				SearchPointer search = (SearchPointer) ob.get();

			}
		}
		SearchPointer searchptr = new SearchPointer(path, f, "*:*", 0, 10000);
		GBVPointer ggbv = new GBVPointer(searchptr);
		return ggbv;
	}

}
