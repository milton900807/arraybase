package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class FieldNodeCommandtest implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		GB.print( " This is the FieldNode command test ");
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
