package com.arraybase.shell.cmds;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.modules.UsageException;

public class DropTable implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		int dropi = command.indexOf("drop")+4;
		String sub = command.substring(dropi);
		sub = sub.trim();
		if ( sub == null || sub.length()<=0)
			throw new UsageException("Cannot drop the table. " + command );
		
		if ( !sub.startsWith("/")){
			sub = GB.pwd() + "/" + sub;
		}
		
		ABTable table = new ABTable ( sub );
		GB.print ( "dropping: " + sub);

		try {
			if ( !table.exists() ){
				throw new UsageException("Cannot drop the table. " + command + "\n TABLE DOES NOT APPEAR TO EXIST.");
			}else{
				table.delete ();
			}
				
		} catch (NodeWrongTypeException e) {
			e.printStackTrace();
			throw new UsageException("Cannot drop the table. " + command + "\n " + e.getMessage());

		}
		GB.print ( "Table  " + sub + " successfully dropped.");
		return "Table droppped.";
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
