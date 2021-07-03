package com.arraybase.flare.file;

public enum GBFileStructure {
	TYPE("TYPE"), CONTENT_TYPE("CONTENT_TYPE"), HEADER("HEADER"), CONTENT(
			"CONTENT"), DATE_CREATED("DATE_CREATED"), DATE_LAST_MODIFIED(
			"LAST_MODIFIED"), AUTHORS("AUTHORS"), ATTRIBUTES("ATTRIBUTES"), FILE_NAME(
			"FILE_NAME"), NODE_ID("NODE_ID"), DESCRIPTION("DESCRIPTION"), MIME(
			"MIME"), TITLE("TITLE"), UUID("UUID"), IDS("ID"), PATH("PATH");

	public String name = null;

	GBFileStructure(String _name) {
		name = _name;
	}

}
