package com.arraybase.shell.cmds;

public enum NodePropertyType {
	BUILD("BUILD"), MODULE("MODULE"), UPDATE("UPDATE"), PK("PK");

	String name;

	NodePropertyType(String name) {
		this.name = name;
	}

}
