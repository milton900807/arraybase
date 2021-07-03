package com.arraybase.tm;

public class GColumn {

	public static final String INTEGER = "INTEGER";
	public static final String SINT = "SINT";
	public static final String SFLOAT = "SFLOAT";
	public static final String DOUBLE = "DOUBLE";
	public static final String FLOAT = "FLOAT";
	public static final String TEXT = "TEXT";
	public static final String DATE = "DATE";
	public static final String STRING = "String";
	public static final String STRING_CI = "string_ci";

	private int width = 29;
	private String type = TEXT;
	private String name = "";
	private boolean defaultVisible = true;
	private String dataIndex = null;
	private boolean sortable = true;

	private String dictionaryURI = null;

	public GColumn() {

	}

	public GColumn(String _value, String _type) {
		name = _value;
		dataIndex = _value;
		type = _type;
	}

	public GColumn(String _value) {
		name = _value;
		dataIndex = _value;
		type = TEXT;
	}

	public GColumn(String _name, int _width) {
		this(_name);

		setWidth(_width);
	}

	public GColumn(String col_id, int _width, boolean _v) {
		this(col_id, _width);
		defaultVisible = _v;
	}

	public boolean getDefaultVisible() {
		return defaultVisible;
	}

	public void setDefaultVisibile(boolean _v) {

		defaultVisible = _v;
	}

	public String getTitle() {
		return name;
	}

	public void setTitle(String title) {
		this.name = title;
	}

	public void setWidth(int _w) {
		width = _w;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	/**
	 * @param string
	 */
	public void setType(String _colType) {
		type = _colType;
	}

	public String getDataindex() {
		return dataIndex;
	}

	public void setDataIndex(String _dataIndex) {
		dataIndex = _dataIndex;
	}

	public boolean getSortable() {
		return sortable;
	}

	public String getDictionaryURI() {
		return dictionaryURI;
	}

	public void setDictionaryURI(String dictionaryURI) {
		this.dictionaryURI = dictionaryURI;
	}

	public void setName( String _name ) {
		name = _name;
	}

}
