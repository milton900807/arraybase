package com.arraybase.modules;

public class NodeTypeChangeManager {

	private String status = "unknown";

	/**
	 *  
	 */
	public NodeTypeChangeManager() {

	}

	public NodeTypeChangeManager(String _status) {
		status = _status;
	}

	public void setStatus(String _status) {
		status = _status;
	}

	public String getStatus() {
		return status;
	}

}
