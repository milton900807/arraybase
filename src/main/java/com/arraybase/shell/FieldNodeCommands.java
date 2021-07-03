package com.arraybase.shell;

import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.GBVariables;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.cmds.PipeHint;
//import com.arraybase.shell.cmds.IntoCommand;
import com.arraybase.util.GBRGX;

public class FieldNodeCommands extends GBCommand {
	private LinkedHashMap<String, GBPlugin> cmdset = new LinkedHashMap<String, GBPlugin>();
	private LinkedHashMap<String, CommandOption> copsions = new LinkedHashMap<String, CommandOption>();
	
	public FieldNodeCommands ()
	{
		init();
	}

	// order here is important...
	// the first that is added has priority
	
	private void init(){
		cmdset.put("test", new FieldNodeCommandtest());
		copsions.put("\\s*[A-Za-z0-9_]*\\s+", new ListLocalHint());
		copsions.put(GBRGX.SEARCH_FORMAT_OUTPUT + "|", new PipeHint());
	}

	public void exec(String line, String key_v) throws UsageException {
		Set<String> keys = cmdset.keySet();
		boolean found = false;
		if (line != null)
			line = line.trim();
		for (String key : keys) {
			if (line.matches(key)) {
				line = expand(line);
				execMod(cmdset.get(key), line, key_v);
				found = true;
				break;
			}
		}
		if (!found) {
			GB.gogb(parse(line));
		}
	}

	private String expand(String line) {
		GBVariables vars = GB.getVariables();
		Set<String> vs = vars.getSets();
		for (String key : vs) {
			String k = "\\$" + key;
			if (line.contains(k)) {
				GBV v = vars.getVariable(key);
				line.replaceAll(k, v.toString());
				return v.toString();
			}
		}
		return line;
	}

	private static String[] parse(String line) {
		String[] args = line.split("\\s+");
		return args;
	}

	public boolean matches(String line) {
		line = line.trim();
		Set<String> keys = cmdset.keySet();
		for (String key : keys) {
			if (line.matches(key))
				return true;
		}
		return false;
	}

	/**
	 * This is going to execute the dynamically loaded command.
	 * 
	 * @param line
	 * @throws UsageException
	 * @throws ClassNotFoundException
	 */
	private static void execMod(GBPlugin plugin, String line, String key)
			throws UsageException {
		plugin.exec(line, key);
	}

	public  GBPlugin getPlugin(String c1) {
		c1 = c1.trim();
		Set<String> keys = cmdset.keySet();
		for (String key : keys) {
			if (c1.matches(key)) {
				return cmdset.get(key);
			}
		}
		return null;
	}

	public  void printCommands() {
		String line = "";
		Set<String> keys = cmdset.keySet();
		if (line != null)
			line = line.trim();
		for (String key : keys) {
			GB.print(key.toString());
		}
	}

	public  void printHint(String b) {
		Set<String> cs = commandSetHelp();
		for (String s : cs) {
			if (b.matches(s)) {
				CommandOption co = copsions.get(s);
				co.setCurrentBuffer(b);
				GB.print(co.toString());
			}
		}
	}

	private  Set<String> commandSetHelp() {
		return copsions.keySet();
	}

}
