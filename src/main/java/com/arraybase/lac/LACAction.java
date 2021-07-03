package com.arraybase.lac;

public interface LACAction {
	String ANNOTATE = "ANNOTATE";
	String SEARCH = "SEARCH";
	String CREATE_ROW = "CREATE_ROW";
	String CREATE_PROJECT_ROW = "CREATE_PROJECT_ROW";
	Object LIST_FIELDS = "LIST_FIELDS";
	String SEARCH_CORE = "SEARCH_CORE";

	LACActionProcess exec() throws LACExecException;
	String getLAC();
}
