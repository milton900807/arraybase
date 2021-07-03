package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lang.ABRef;
import com.arraybase.modules.UsageException;

public class AsignVarSearch implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		GBV var = null;
		int ind = command.indexOf('=');
		String varst = command.substring(0,ind);
		String value = command.substring(ind+1);
		value = value.trim();
		varst = varst.trim();
		var = new ABRef(value);
		GB.setVariable(varst, var);
		System.out.println( " search ");
		return "Search";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
