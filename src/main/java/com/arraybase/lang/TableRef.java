package com.arraybase.lang;

public class TableRef {

	private String path = null;
	private String action = null;
	private String searchparam = null;

	public TableRef(String path, String action, String searchParam) {
		this.path = path;
		this.action = action;
		this.searchparam = searchParam;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSearchparam() {
		return searchparam;
	}

	public void setSearchparam(String searchparam) {
		this.searchparam = searchparam;
	}

}
