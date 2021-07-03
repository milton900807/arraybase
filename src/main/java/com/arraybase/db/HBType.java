package com.arraybase.db;

import com.arraybase.io.GBBlobFile;
import com.arraybase.tm.builder.jobs.Job;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TableTemplate;
import com.arraybase.tm.tree.*;
import com.arraybase.util.ABProperties;

public enum HBType {

	All("all", "all", ABProperties.get("hibernate"), Job.class, TPath.class,
			TMNodeLink.class, GBBlobFile.class, TTable.class,
			TMTableSettings.class, TNode.class, TableTemplate.class, NodeProperty.class);
	private String name = "unknown";
	private String db = "";
	private Class[] classes = null;
	private String configFile = null;

	HBType(String _name, String _db, String _hibernate_config_file, Class... c) {
		name = _name;
		configFile = _hibernate_config_file;
		db = _db;
		classes = c;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDb() {
		return db;
	}

	public String getConfigFile() {
		return configFile;
	}

	public Class[] getClasses() {
		return classes;
	}

}
