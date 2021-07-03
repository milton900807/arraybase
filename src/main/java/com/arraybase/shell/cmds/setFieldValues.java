package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.HashMap;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.modules.UsageException;
import com.arraybase.tab.field.FieldAction;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tree.TNode;

public class setFieldValues implements FieldAction {

	
	private String path = null;
	private ArrayList<GColumn> cols = null;
	private String action = null;
	private TNode node = null;
	private String target_field = null;
	
	public setFieldValues(TNode node, ArrayList<GColumn> cols,
			String target_table, String target_field, String action) {
		this.path = target_table;
		this.cols = cols;
		this.node = node;
		this.action = action;
		this.target_field = target_field;
	}

	public void start() throws UsageException {
		GB.print( "update action starting : "+ action);
		String[] params = GBIO.parseParams(action);
		if ( params == null )
			throw new UsageException( "You need to provide paramerters ");
		
		if ( params.length != 2)
			throw new UsageException( "The function requires two parameters:  (search_string, value)");
		
		String field_object = params[1];
		String q = params[0];
		if ( field_object == null )
			throw new UsageException ( "Failed to determine the *value* you would like to insert.  usage: (query, value)");
		if ( q == null )
			throw new UsageException ( "Failed to determine the *query* you would like to insert.  usage: (query, value)");
		HashMap<String, Object> values = new HashMap<String, Object> ();
		
		
		
		values.put ( target_field, field_object );
		ABTable table = new ABTable ( path );
		table.update(q, values);
		
	}

}
