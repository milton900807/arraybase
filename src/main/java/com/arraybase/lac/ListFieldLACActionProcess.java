package com.arraybase.lac;

import java.util.ArrayList;

import com.arraybase.tm.GColumn;

public class ListFieldLACActionProcess<T> extends
		LACActionProcess<ArrayList<GColumn>> {
	private ArrayList<GColumn> fields = null;
	

	public ListFieldLACActionProcess(ArrayList<GColumn> _fields) {
		super ( "Fields : "+ _fields.size());
		fields = _fields;
	}
	
	public ListFieldLACActionProcess(String string) {
		super ( string );
	}

	public ArrayList<GColumn> getValues ()
	{
		return fields;
	}
	

}
