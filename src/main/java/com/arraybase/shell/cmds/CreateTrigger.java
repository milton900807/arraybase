package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.db.util.SourceType;

public class CreateTrigger implements GBPlugin {
	public String exec(String command, String variable_key)
			throws UsageException {
		String[] sp = command.split("\\s+");
		String table = sp[2];
		String pwd = GB.pwd();
		String table_name = pwd + "/" + table;
		GB.getNodes().mkNode(GB.getDefaultUser(), table_name, "?", SourceType.TABLE_TRIGGER);
		GB.print("Table : "+ table_name + " has been created");
		return "Complete";
	}
	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}
}
