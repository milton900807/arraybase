package com.arraybase.shell;

import java.util.LinkedHashMap;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;

public class MakeTableCommand implements GBPlugin {

	public String exec(String command, String _variable) {

		// mktable tablename[something][int someinthing_index][String value]
		String tablename = null;
		LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();

		int lindex = command.indexOf('[');
		int rindex = command.lastIndexOf(']');
		tablename = command.substring(0, lindex);
		tablename = tablename.trim();
		String temp = command.substring(lindex);
		String[] params = temp.split("\\[");
		for (String p : params) {
			System.out.println(p);
			String v = p.trim();
			v = v.substring(1, v.length() - 1);
			System.out.println(" v : " + v);
		}

		return "complete";
	}

	/**
	 * Update the This will store the executed value in a temporary variable
	 */
	public GBV execGBVIn(String cmd, GBV input) {
		exec(cmd, getClass().getCanonicalName());
		return GB.getVariable(getClass().getCanonicalName());
	}

}
