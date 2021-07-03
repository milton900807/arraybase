package com.arraybase.io.parse;

public class DelimNotFoundException extends Exception {

	private String delim = null;

	public DelimNotFoundException(String _delim) {
		delim = _delim;
	}

}
