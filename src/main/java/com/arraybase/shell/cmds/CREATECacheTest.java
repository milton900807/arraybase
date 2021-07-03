package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.NodeCreateFailed;

public class CREATECacheTest implements GBPlugin {

	public String exec(String command, String variable_key) {
		
		String[] sp = command.split("\\s+");
		String table = sp[2];
		String pwd = GB.pwd();
		String target = pwd + "/" + table;
		
		try {
			GB.createCache(target);
		} catch (NodeCreateFailed e) {
			e.printStackTrace();
			return "Cache failed.";
		}
		return "Cache created";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
