package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.GBCommand;

public class InsertInto implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		// {{ parse the composite command }}
		String[] commands = command.split("\\>");
		GBV current = null;
		// System.out.println(command + " is the command ");
		for (int i = 0; i < commands.length - 1; i++) {
			String c1 = commands[i];
			c1 = c1.trim();
			
			GBCommand _cc_ = GB.getCommands();
			if (_cc_.matches(c1)) {
				GBPlugin plugin = _cc_.getPlugin(c1);
				GBPlugin next = _cc_.getPlugin(commands[i + 1]);
				if (i == 0) {
					current = plugin.execGBVIn(c1, null);
				}
				if (next == null) {
					GB.print(commands[i + 1] + " not found. ");
					return "composite failed.";
				}
				current = next.execGBVIn(commands[i + 1], current);
			}
		}
		return "composite complete";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
