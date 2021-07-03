package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class ExecuteVariable implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		String[] t = command.split(" ");
		String var= t[1].trim();
		GBV variable = GB.getVariable(var);
//		String strvalue = variable.get().toString();
		String v = variable.toString();
		GB.print ( v );
		
		GB.exec(v, null);
		
		return "executing.";
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		// TODO Auto-generated method stub
		return null;
	}

}
