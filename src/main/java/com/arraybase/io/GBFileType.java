package com.arraybase.io;

public enum GBFileType {
	BINARY("binary"), TABLE("table");

	String name = "unknown";

	GBFileType(String _name) {
		name = _name;
	}
}
