package com.arraybase.flare.parse;

import java.util.ArrayList;

import com.arraybase.GB;
import com.arraybase.tm.GColumn;

public class TypeNotFoundException extends Exception {

	private ArrayList<GColumn> cols = null;
	private String field = null;

	public TypeNotFoundException(String _field, ArrayList<GColumn> cols) {
		super("Type not found for field " + _field);
		field = _field;
		this.cols = cols;
	}

	public TypeNotFoundException(String _field) {
		field = _field;
	}

	public void printMessage() {
		GB.print("Field : " + field);
		if (cols != null) {
			for (GColumn col : cols) {
				GB.print("" + col.getName());
			}
		}
	}

}
