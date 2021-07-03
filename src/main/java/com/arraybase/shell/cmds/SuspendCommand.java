package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class SuspendCommand implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {

		GB.print("Suspending... only kills the interactive mode.  NOTE: If you have separate processes running (e.g. a node.refresh) they will live on.");
		GB.INTERACTIVE = false;
		return "Kill interactive done.";
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		GB.INTERACTIVE = false;
		return null;
	}

}
