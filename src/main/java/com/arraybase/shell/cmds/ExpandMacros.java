package com.arraybase.shell.cmds;

import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.GBVariables;
import com.arraybase.modules.UsageException;

public class ExpandMacros implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		GBVariables vars = GB.getVariables();
		Set<String> vs = vars.getSets();
		for (String key : vs) {
			String k = "\\$" + key;
			if (command.contains(k)) {
				GBV v = vars.getVariable(key);
				command.replaceAll(k, v.toString());
				return v.toString();
			}
		}
		return command;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
