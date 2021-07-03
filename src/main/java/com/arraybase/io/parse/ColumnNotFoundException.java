package com.arraybase.io.parse;

public class ColumnNotFoundException extends Exception {

	int col = -1;
	String st = null;

	public ColumnNotFoundException(int _col, String _st) {
		col = _col;
		st = _st;
	}

}
