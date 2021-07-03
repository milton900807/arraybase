package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.TableManager;
import com.arraybase.util.ABProperties;

public class DeleteCore implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		
		int index_core = command.indexOf("core")+4;
		String target = command.substring(index_core);
		if ( target != null )
			target = target.trim();
		else{
			GB.print ( "Command requires a core name you want to remove... ");
			return "Failed to remove";
		}
		
		
		TableManager.delete(target);
		GB.print ( "Core : "+ target + " is removed.");
		
		return "Removed";
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}


	
}
