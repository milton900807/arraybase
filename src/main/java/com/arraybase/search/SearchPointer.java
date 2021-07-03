package com.arraybase.search;

public class SearchPointer {

	private String path = null;
	private String[] fields = null;
	private String searchString = null;
	private int start = 0;
	private int max = 100;

	public SearchPointer(String path, String[] st, String string, int _start,
			int _max) {
		this.path = path;
		fields = st;
		searchString = string;
		start = _start;
		this.max = _max;
	}
	

	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String[] getFields() {
		return fields;
	}


	public void setFields(String[] fields) {
		this.fields = fields;
	}


	public String getSearchString() {
		return searchString;
	}


	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getMax() {
		return max;
	}


	public void setMax(int max) {
		this.max = max;
	}


	public String getFieldsAsArray() {
		if (fields == null || fields.length <= 0)
			return "[all_fields]";
		String s = "[";
		for (String f : fields) {
			s += f + ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		return s;
	}

}
