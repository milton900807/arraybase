package com.arraybase.db;

import com.arraybase.tm.tree.TNode;

public class NodeExistsException extends Exception {

	private TNode node = null;
	private String path = null;
	
	
	
	public NodeExistsException ()
	{
		
	}
	
	public NodeExistsException(String path, TNode node) {
		this.node = node;
		this.path = path;
	}


	public TNode getNode() {
		return node;
	}


	public void setNode(TNode node) {
		this.node = node;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	
}
