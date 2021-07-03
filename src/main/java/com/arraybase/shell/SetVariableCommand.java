package com.arraybase.shell;

import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.GBVariables;
import com.arraybase.modules.UsageException;

public class SetVariableCommand implements GBPlugin {

	public String exec(String command, String variable_key) {
		try {
			GBVariables.setVariable(command);
		} catch (UsageException e) {
			e.printStackTrace();
		}
		return "complete";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
