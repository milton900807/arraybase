package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class LSSearchableNodes implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		int space = command.indexOf ( " ");
		if ( space > 0 ){
			String path = command.substring (space+1);
			if ( !path.startsWith("/"))
				path = GB.pwd() + "/" + path;
			path = path.trim();
			GB.print( "Finding query nodes for " + path );
			String[] paths = GB.lsSearchableNodes(path);
			for ( String path__ : paths ){
				GB.print ( path__ );
			}
		}
		// TODO Auto-generated method stub
		return "complete";
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		// TODO Auto-generated method stub
		return null;
	}

}
