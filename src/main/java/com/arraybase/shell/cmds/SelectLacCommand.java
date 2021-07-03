package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;

public class SelectLacCommand implements GBPlugin {

	public String exec(String command, String variable_key) {
		command = command.trim();
		String[] l = LAC.parse(command);
		GB.print("Selecting.. " + command );
		String target = l[0];
		String action = l[1];
		String data = l[2];
		if ( command.endsWith("]")){
			int lastIndex = command.lastIndexOf(')');
			int llastIndex = command.lastIndexOf(']');
			String output = command.substring(lastIndex+1, llastIndex);
			
			
		}
		
		
		System.out.println ( " target : " + target );
		System.out.println ( " action : " + action );
		System.out.println ( " data : " + data );
		
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
