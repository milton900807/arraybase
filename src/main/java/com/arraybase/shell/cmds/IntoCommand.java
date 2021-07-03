package com.arraybase.shell.cmds;

import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class IntoCommand implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		String[] cs = command.split(">");
		for ( String comma : cs){
			System.out.println ( " command : " + comma );
		}
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
