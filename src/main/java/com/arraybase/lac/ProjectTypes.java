package com.arraybase.lac;


public enum ProjectTypes {
	VENDOR("Vendor (V-)", "\"V-*\""), COLLABORATOR("Collaboration (C-)", "\"C-*\""), INTERNAL(
			"Internal (X-)", "\"X-*\"");
	private String searchString = "*";
	private String name = "";

	ProjectTypes(String _name, String _search) {
		name = _name;
		searchString = _search;
	}
	public String getSearchString ()
	{
		return searchString;
	}
	public String getName() {
		return name;
	}

	
}