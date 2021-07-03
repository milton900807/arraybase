package com.arraybase.shell.cmds;

public class SQLSearch {

	private String sql = "";
	
	public SQLSearch(String _s) {
		sql = _s;
	}

	
	/*
	 * 
	 */
	public void setWhere(String _where) {
		sql += " where " + _where;
	}

}
