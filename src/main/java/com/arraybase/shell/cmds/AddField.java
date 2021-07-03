package com.arraybase.shell.cmds;

import java.util.ArrayList;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.TableManager;

public class AddField implements GBPlugin {
	/**
	 *  
	 */
	public String exec(String command, String variable_key) {
		int index = command.indexOf('(');
		int index2 = command.indexOf(')');
		String temp = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		// String tt = GB.pwd() + "/" + temp2.trim();

		int doti = command.indexOf('.');
		String path = command.substring(0, doti);
		path = GB.pwd() + "/" + path.trim();
		temp2 = temp2.trim();
		String[] v = temp.split(",");
		if (v == null || v.length <= 0) {
			v = new String[1];
			v[0] = temp;
		}
		ArrayList<GColumn> gclist = new ArrayList<GColumn>();
		for (String s : v) {
			String t = s.trim();
			String[] tt = t.split("\\s+");
			GColumn column = new GColumn(tt[1], tt[0]);
			gclist.add(column);
		}

		TableManager manager = new TableManager(GB.getConnectionManager());
		String core = GBSolr.getCore(path);
		System.out.println(path);
		for (GColumn c : gclist) {
			String type = c.getType();
			String field = c.getName();
			manager.addColumn(core, field, type);
		}
		GB.print("Column added");
		return "Column added";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
