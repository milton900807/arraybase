package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class CompositeCommand implements GBPlugin {

	public String exec(String command, String variable_key) throws UsageException {
		// {{ parse the composite command }}
		String[] commands = command.split("\\|");
		GBV current = null;
		// System.out.println(command + " is the command ");
		for (int i = 0; i < commands.length - 1; i++) {
			String c1 = commands[i];
			c1 = c1.trim();
			GBCommand command_center= GB.getCommands ();
			if (command_center.matches(c1)) {
				GBPlugin plugin = command_center.getPlugin(c1);
				GBPlugin next = command_center.getPlugin(commands[i + 1]);
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
