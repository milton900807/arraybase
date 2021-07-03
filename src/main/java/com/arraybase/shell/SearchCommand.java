package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;

public class SearchCommand implements GBPlugin {

	public String exec(String s, String _variable) {

		int index = s.indexOf('[');
		int left_br = s.indexOf('[');
		int right_br = s.indexOf(']');
		String path = s.substring(0, index);
		String fields = s.substring(left_br + 1, right_br);
		String[] f = fields.split(",");
		int left_parm = s.indexOf("(");
		int right_parm = s.indexOf(")");

		String searchString = s.substring(left_parm + 1, right_parm);
		int lst_open = s.lastIndexOf("[");
		int lst_close = s.lastIndexOf(']');
		String range = s.substring(lst_open + 1, lst_close);
		int start = 0;
		int end = 1000;
		if (range.contains(",")) {
			String[] sp = range.split(",");
			String sstrt = sp[0].trim();
			start = Integer.parseInt(sstrt);
			String increment = sp[1].trim();
			end = Integer.parseInt(increment);
		} else {
			String strt = range.trim();
			start = Integer.parseInt(strt);
		}
		String[] t = path.split("/");
		String table = t[t.length - 1];

		System.out.println(" path:\t " + path);
		System.out.println(" table:\t " + table);
		for (int i = 0; i < f.length; i++) {
			System.out.println(" field:\t" + f[i]);
		}
		System.out.println(" start : " + start + " increment : " + end);
		System.out.println(" search string:\t" + searchString);
		return "Complete";
	}

	/**
	 * Update the This will store the executed value in a temporary variable
	 */
	public GBV execGBVIn(String cmd, GBV input) {
		exec(cmd, getClass().getCanonicalName());
		return GB.getVariable(getClass().getCanonicalName());
	}

}
