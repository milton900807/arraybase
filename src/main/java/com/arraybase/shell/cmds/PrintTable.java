package com.arraybase.shell.cmds;

import com.arraybase.*;

import java.util.ArrayList;

public class PrintTable implements GBPlugin {
	public String exec(String command, String variable_key) {

		int st1 = command.indexOf('(');
		int st2 = command.indexOf(')');
		String target = command.substring(0, st1);
		String path = GB.pwd() + "/" + target;
		String sub = command.substring(st2 + 1);
		String search = command.substring(st1 + 1, st2);
		ArrayList<String> pth = parse(sub.trim());
		try {
			GBSearch.select(path, search, System.out, pth, new SearchConfig(SearchConfig.RAW_SEARCH));
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return "Out";
	}

	/**
	 * @param sub
	 * @return
	 */
	private ArrayList<String> parse(String sub) {
		ArrayList<String> va = new ArrayList<String>();
		String[] sp = sub.split("\\[");
		for (String s : sp) {
			int endIndex = s.indexOf("]");
			if (endIndex >= 0) {
				String v = s.substring(0, endIndex);
				v = v.trim();
				va.add(v);
			}
		}
		return va;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
