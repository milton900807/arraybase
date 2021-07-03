package com.arraybase.shell.cmds;

import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.GBVariables;

public class PrintVariables implements GBPlugin {

	public String exec(String command, String variable_key) {
		GBVariables.printAll ();
		return "done";
	}
	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
