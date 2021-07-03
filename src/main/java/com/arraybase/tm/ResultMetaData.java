package com.arraybase.tm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ResultMetaData {

	private HashMap<String, String> fieldMap = new HashMap<String, String>();
	private HashMap<String, String> descriptionMap = new HashMap<String, String>();
	private HashMap<String, String> typeMap = new HashMap<String, String>();
	private HashMap<String, Integer> widthMap = new HashMap<String, Integer>();
	private HashMap<String, Boolean> visMap = new HashMap<String, Boolean>();

	private String title = "NA";
	private ArrayList<String> columnOrder = new ArrayList<String>();
	private String linkTemplate = null;
	private String defaultGroupByField = null;
	private boolean selectionColumnVisible = true;
	private boolean linkColumnVisible = true;
	private String description = "";
	private Boolean toolbarVisible = true;

	public ResultMetaData() {
	}

	public String getColumn(int _index) {
		return columnOrder.get(_index);
	}

	public String getType(String _colName) {
		return typeMap.get(_colName);
	}

	public HashMap<String, String> getTypeMap() {
		return typeMap;
	}

	public void setTypeMap(HashMap<String, String> typeMap) {
		this.typeMap = typeMap;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String _title) {
		title = _title;
	}

	public ArrayList<String> getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(ArrayList<String> columnOrder) {
		this.columnOrder = columnOrder;
	}

	public int getColumnCount() {
		return columnOrder.size();
	}

	public String getResultTitle() {
		return title;
	}

	public void addColumn(String _field, String display, String description,
			int _width, boolean isVis) {
		columnOrder.add(display);
		fieldMap.put(display, _field);
		typeMap.put(display, description);
		widthMap.put(display, new Integer(_width));
		visMap.put(display, isVis);
	}

	public void changeColumn(String _field, String _newdisplay) {
		Set<String> vkeys = fieldMap.keySet();
		String original = null;
		for (Iterator iterator = vkeys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = fieldMap.get(key);
			if (value.equalsIgnoreCase(_field)) {
				original = key;
			}
		}
		if (original == null) {
			System.err
					.println(" failed to change this column beause it is not a valid column ");
			return;
		}
		int index = columnOrder.indexOf(original);
		if (index < 0) {
			System.err
					.println(" failed to change this column beause it is not a valid column ");
			return;
		}
		// {{ UPDATE THE TYPE MAP }}
		String otype = typeMap.get(original);
		typeMap.remove(original);
		typeMap.put(_newdisplay, otype);

		// {{ UPDATE THE WIDTH MAP }}
		Integer wmap = widthMap.get(original);
		widthMap.remove(original);
		widthMap.put(_newdisplay, wmap);

		// {{ UPDATE THE COLUMN ORDER }}
		columnOrder.remove(index);
		columnOrder.set(index, _newdisplay);

		// {{ UPDATE THE FIELD MAP }}
		String fieldType = fieldMap.get(original);
		fieldMap.remove(original);
		fieldMap.put(_newdisplay, fieldType);
	}

	public void clearColumns() {
		columnOrder.clear();
		fieldMap.clear();
		typeMap.clear();
		widthMap.clear();
	}

	public String getDBField(String _display) {
		return fieldMap.get(_display);
	}

	public int getWidth(String _key) {
		Integer i = widthMap.get(_key);
		if (i != null)
			return i.intValue();
		else
			return 90;
	}

	/**
	 * @param linkTemplate2
	 */
	public void setLinkTemplate(String _linkTemplate) {
		linkTemplate = _linkTemplate;
	}

	public String getLinkTemplate() {
		return linkTemplate;
	}

	public void setDefaultVisibilityMap(HashMap<String, Boolean> _visMap) {
		visMap = _visMap;
	}

	/**
	 * @param widthMap2
	 */
	public void setWidthMap(HashMap<String, Integer> _widthMap) {
		widthMap = _widthMap;
	}

	/**
	 * @param descMap
	 */
	public void setDescriptionMap(HashMap<String, String> _descMap) {
		descriptionMap = _descMap;
	}

	/**
	 * @param fieldMap2
	 */
	public void setFieldMap(HashMap<String, String> _fieldMap) {
		fieldMap = _fieldMap;
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getFieldMap() {
		return fieldMap;
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getDescriptionMap() {
		return descriptionMap;
	}

	/**
	 * @return
	 */
	public HashMap<String, Integer> getWidthMap() {
		return widthMap;
	}

	public void setDefaultGroupBy(String _field) {
		defaultGroupByField = _field;
	}

	/**
	 * @return
	 */
	public String getDefaultGroupBy() {
		return defaultGroupByField;
	}

	/**
	 * @param string
	 * @return
	 */
	public String getUserField(String _dbField) {
		Set set = fieldMap.keySet();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String _key_ = (String) iterator.next();
			String value = fieldMap.get(_key_);
			if (value != null && value.equalsIgnoreCase(_dbField))
				return _key_;
		}
		return null;
	}

	public Boolean getDefaultVisible(String key) {

		Object ob = visMap.get(key);
		if (ob == null)
			return true;

		return visMap.get(key);
	}

	public void setSelectionColumnVisible(boolean b) {
		selectionColumnVisible = b;
	}

	public boolean getSelectionColumnVisible() {
		return selectionColumnVisible;
	}

	public void setLinkColumnVisible(boolean hlink) {
		linkColumnVisible = hlink;
	}

	public boolean getLinkColumnVisible() {
		return linkColumnVisible;
	}

	/**
	 * @return
	 */
	public HashMap<String, Boolean> getDefaultVisibilityMap() {
		return visMap;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String _description) {
		description = _description;
	}

	/**
	 * @param toolbar
	 */
	public void setToolbarVisible(Boolean _toolbar) {
		toolbarVisible = _toolbar;
	}

	public boolean getToolbarVisible() {
		return toolbarVisible;
	}
}
