package com.arraybase.db.jdbc;

import java.util.HashMap;
import java.util.Map;

public class DBConfiguration {

	private String name = "";
	private Map<String, String> config = new HashMap<String, String> ();

	public DBConfiguration (  String _name ){
		setName ( _name );
	}
	
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setConfig ( Map<String, String> _config ){
		config = _config;
	}
	
	
	public Map<String, String> getProperties() {
		return config;
	}

}
