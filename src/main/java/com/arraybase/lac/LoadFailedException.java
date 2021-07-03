package com.arraybase.lac;

public class LoadFailedException extends Exception {

	public LoadFailedException(Exception _e) {
		super ( _e );
	}
	public LoadFailedException ( String _msg ){
		super ( _msg );
	}
}
