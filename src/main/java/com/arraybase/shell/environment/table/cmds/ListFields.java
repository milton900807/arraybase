package com.arraybase.shell.environment.table.cmds;

import java.net.ConnectException;
import java.util.ArrayList;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tree.TNode;

public class ListFields implements GBPlugin {
	
	
	private TNode table = null;
	private ArrayList<GColumn> cols = null;
	
	public ListFields(TNode table) {
		this.table = table;
		try {
			cols = GB.getGBTables().describeTable(table);
		} catch (ConnectException e) {
			e.printStackTrace();
		}
	}

	public String exec(String command, String variable_key)
			throws UsageException {
		GB.print ( "\n");
		for ( GColumn col : cols ){
			GB.print( "\t" + col.getName() );
		}
		return "list";
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		GB.print ( "\n");
		for ( GColumn col : cols ){
			GB.print( "\t" + col.getName() );
		}
		return input;
	}

}
