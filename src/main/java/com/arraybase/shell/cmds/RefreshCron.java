package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBUtil;
import com.arraybase.GBV;
import com.arraybase.flare.parse.GBParser;
import com.arraybase.lac.LAC;

public class RefreshCron implements GBPlugin {
	
	public static String nightly = "nightly";
	public static String daily = "daily";
	public static String weekly = "weekly";
	public static String monthly = "montly";
	public static String only_once = "once";
	
	
	

	public String exec(String command, String variable_key) {

		
		String target = LAC.getTarget(command);
		if (!target.startsWith("/"))
			target = GB.pwd() + "/" + target;
		
		

		// this will only handle one or two arguments
		String arguments = GBUtil.parsePArgs(command);
		int ind = arguments.indexOf(',');
		if (ind > 0) {
			String[] two = arguments.split(",");
			two[0] = two[0].trim();
			two[1] = two[1].trim();

		}

		GB.print(" setting the cron job " + arguments
				+ " vairable key : " + variable_key);
		return "got it";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
