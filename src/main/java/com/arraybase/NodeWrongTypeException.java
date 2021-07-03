package com.arraybase;

public class NodeWrongTypeException extends Exception {
	
	private String path = null;

	public NodeWrongTypeException(String _path) {
		path = _path;
	}
	

}
