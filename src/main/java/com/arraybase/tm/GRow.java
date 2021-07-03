package com.arraybase.tm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.arraybase.tm.tables.RowData;

public class GRow implements RowData {

	private String title = null;
	private String uri = "";
	private boolean selected = false;
	private boolean visible = true;
	private HashMap data = new HashMap();
	private HashMap<String, String> dataType = new HashMap<String, String>();
	private String reportType = null;
	private ArrayList<String> order = new ArrayList<String>();
	private String className = "com.tissuematch.indexers.client.ResultRow";

	/**
	 * @param string
	 */
	public GRow(String _title) {
		title = _title;
	}

	public GRow() {
	}

	public void setType(String _type) {
		reportType = _type;
	}

	public String getType() {
		return reportType;
	}

	public void add(String _key, Object _value) {
		data.put(_key, _value);
	}

	public void setData(HashMap _data) {
		data = _data;
	}

	public HashMap getData() {
		return data;
	}

	public void set(String _key, Object _data) {
		data.put(_key, _data);
		order.add(_key);
	}

	public String getHTML() {
		return title;
	}

	/**
	 * @param string
	 * @param seriesID
	 */
	public void appendData(String _key, String _seriesID) {
		data.put(_key, _seriesID);
		order.add(_key);
	}

	public ArrayList getSubReports() {
		return null;
	}

	public ArrayList getSubReports(String _property) {
		return null;
	}

	public String getTitle() {
		return title;
	}

	public String getURI() {
		return uri;
	}

	public void highlight(String _text, String _color) {

	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setSelected(boolean _value) {
		selected = _value;
	}

	public void setVisible(boolean _value) {
		visible = _value;
	}

	public void setTitle(String _title) {
		title = _title;
	}

	public void setURI(String _uri) {
		uri = _uri;
	}

	/**
		 * 
		 */
	public void set(String _key, String _value) {
		order.add(_key);
		data.put(_key, _value);
	}

	public void setType(String _key, String _type) {
		dataType.put(_key, _type);
	}

	public String getType(String _key) {
		return dataType.get(_key);
	}

	public ArrayList<String> getOrder() {
		return order;
	}

	/**
	 * This is in order to support reflection on the gwt client This a
	 * polymorphic method for creating unrealized object. This means allows me
	 * to work with and create an instanceof ResultRow without knowing what the
	 * actual object is.
	 * 
	 * @param _param
	 * @return
	 */
	public GRow newInstance(String _param) {
		return new GRow();
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<String, String> getDataTypes() {
		return dataType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String _name) {
		className = _name;
	}

	public String toString() {
		String line = "";
		Set keys = data.keySet();
		for (Object field : keys) {
			Object d = data.get(field);
			line += field + "=" + d.toString();
			line += "        ";
		}
		return line;
	}

	public void setDataTypes(HashMap<String, String> _dataTypes) {
		dataType = _dataTypes;
	}

	public boolean getSelected() {
		return selected;
	}

	public boolean getVisible() {
		return visible;
	}

}
