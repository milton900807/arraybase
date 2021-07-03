package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class FieldLoader implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		GB.print ( " we have the field ");
		
		
		return null;
	}
	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
