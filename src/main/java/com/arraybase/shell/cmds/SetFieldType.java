package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.tm.tables.GBTables;

public class SetFieldType implements GBPlugin {

	/**
	 *  
	 */
	public String exec(String command, String variable_key) {
		System.out.println(command + " command ");
		String[] params = GBIO.parseParams(command, "=");
		String target = GBIO.parsePath(command);
		GBTables tables = GB.getGBTables();
		String field = params[0];
		String type = params[1];
		if (type.equalsIgnoreCase("int"))
			type = "sint";
		else if (type.equalsIgnoreCase("double"))
			type = "sdouble";
		else if (type.equalsIgnoreCase("float"))
			type = "sfloat";

		tables.setTypeField(target, field, type);

		return "" + target + " to type " + type;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// System.out.println(cmd + " command ");
		return null;
	}
}
