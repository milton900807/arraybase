package com.arraybase.tab.field;

public class FieldNotFoundException extends Exception {

	
	public FieldNotFoundException ( String _table, String _field ){
		super ( "Table : "+ _table + " does not appear to have the field : " + _field );
	}
	
	
}
