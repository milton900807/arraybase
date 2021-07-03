package com.arraybase.tm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.arraybase.search.ABaseResults;

public class GResults implements ABaseResults  {
	private ResultMetaData resultDesc = null;
	private String type = "Unknown";
	private String target = "";
	private String action = "";
	private String title = "Hits";
	private ArrayList<GRow> values = new ArrayList<GRow>();
	private int totalHits = 0;
	private int currentStart = 0;
	private int currentEnd = 0;
	private int increment = 100;
	private HashMap properties = new HashMap();
	private String icon = null;
	private String sortInfo = "title";
	private String groupField = "NA";
	private ArrayList<GColumn> columns = new ArrayList<GColumn>();
	private String dataType = null;
	private LinkedHashMap<String, LinkedHashMap<String, Integer>> facet = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
	private String url = null;
	private boolean successfulSearch = false;
	private String message = "";

	public void setIcon(String _icon) {
		icon = _icon;
	}

	public void setURL(String _url) {
		url = _url;
	}

	public String getURL() {
		return url;
	}

	public void setResultDescriptor(ResultMetaData _resultDesc) {
		resultDesc = _resultDesc;
	}

	public ResultMetaData getResultDescriptor() {
		return resultDesc;
	}

	public String getIcon() {
		return icon;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String _dataType) {
		dataType = _dataType;
	}

	public String getGroupField() {
		return groupField;
	}

	public void setSort(String _name) {
		sortInfo = _name;
	}

	public String getSort() {
		return sortInfo;
	}

	public void addProperty(String _key, String _prop) {
		properties.put(_key, _prop);
	}

	public HashMap getProperties() {
		return properties;
	}

	public void setTarget(String _target) {
		target = _target;
	}

	public void setAction(String _action) {
		action = _action;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int _incr) {
		increment = _incr;
	}

	public int getTotalHits() {
		return totalHits;
	}

	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}

	public int getCurrentStart() {
		return currentStart;
	}

	public void setCurrentStart(int currentStart) {
		this.currentStart = currentStart;
	}

	public int getCurrentEnd() {
		return currentEnd;
	}

	public void setCurrentEnd(int currentEnd) {
		this.currentEnd = currentEnd;
	}

	private void setResults(ArrayList results) {
		// this.results = results;
	}

	public GResults() {

	}

	public GResults(String _title) {
		title = _title;
	}

	/**
	 * @param geneList
	 */
	public GResults(ArrayList list) {
		setValues(list);
	}

	public void setTitle(String _title) {
		title = _title;
	}

	public ArrayList<GRow> getValues() {
		return values;
	}

	public void setValues(ArrayList values) {
		this.values = values;
	}

	public void sort(String _sortType) {

	}

	public String getTitle() {
		return title;
	}

	public String getTarget() {
		return target;
	}

	public String getAction() {
		return action;
	}

	/**
	 * @return
	 */
	public ArrayList<GColumn> getColumns() {
		return columns;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String _type) {
		type = _type;
	}

	public void setColumns(ArrayList<GColumn> _columns) {
		columns = _columns;
	}

	/**
	 * @param defaultGroupBy
	 */
	public void setDefaultGroupBy(String defaultGroupBy) {
		groupField = defaultGroupBy;
	}

	public void setFacet(
			LinkedHashMap<String, LinkedHashMap<String, Integer>> _facetResult) {
		facet = _facetResult;
	}

	public LinkedHashMap<String, LinkedHashMap<String, Integer>> getFacet() {
		return facet;
	}


	public void setSuccessfulSearch(boolean successfulSearch) {
		this.successfulSearch = successfulSearch;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LinkedHashMap<String, Integer> getFacet(String field) {
		return facet.get(field);
	}

	/**
	 *  TODO: not implemented at the moment
	 */
	public void addField(String type, String fieldName) {
	}

	public void setSuccess(boolean b) {
		successfulSearch=b;
	}

	public String getDefaultGroupBy() {
		return groupField;
	}

	public boolean getSuccess() {
		return successfulSearch;
	}

	public String hits() {
		return "" + totalHits;
	}

}