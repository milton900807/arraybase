package com.arraybase.shell.cmds;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.modules.UsageException;

public class ReplaceCommand implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		String path = GBIO.parsePath(command);
		if ( !path.startsWith("/"))
			path = GB.pwd() + "/" + path;

		ABTable b = new ABTable(path);
		
		try {
			if ( b.exists() )
			{
				System.out.println ( " we have a handle on the table .");
			}
		} catch (NodeWrongTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		// TODO Auto-generated method stub
		return null;
	}

}
