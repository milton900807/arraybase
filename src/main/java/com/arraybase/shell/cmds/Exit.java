package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;

public class Exit implements GBPlugin {

	public String exec(String command, String variable_key) {
		GB.print("bye.");
		GB.exit ( 0 );
		return "complete";
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
