package com.arraybase.shell.environment;

import java.net.ConnectException;
import java.util.ArrayList;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tree.TNode;

public class FieldMethod implements GBPlugin {

	private TNode table = null;
	private ArrayList<GColumn> cols = new ArrayList<GColumn> ();
	
	
	public FieldMethod(TNode table) {
		this.table = table;
		try {
			cols = GB.getGBTables().describeTable(table);
		} catch (ConnectException e) {
			e.printStackTrace();
		}
	}

	public String exec(String command, String variable_key)
			throws UsageException {
		
		
		// strip the object in the method 
		int st = command.indexOf('(');
		int lt = command.lastIndexOf(')');
		String argument = command.substring ( st+1, lt);
		
		int methodi = command.indexOf('.');
		String method = command.substring(methodi+1, st);
		if ( method != null )
			method = method.trim();
		String field = command.substring(0, methodi);
		if ( field != null )
			field = field.trim();
		
//		FieldFun fieldf  = FieldFunFactory
		
		System.out.println ( " field " + field );
		System.out.println ( " method : " + method );
		System.out.println ( " argument : :"+ argument );
		
		
		
		
		
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
