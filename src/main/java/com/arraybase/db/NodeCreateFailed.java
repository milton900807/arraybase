package com.arraybase.db;

public class NodeCreateFailed extends Exception {

	private String path = null;

	public NodeCreateFailed(String path, String _msg) {
		super(_msg);
		this.path = path;
	}

}
