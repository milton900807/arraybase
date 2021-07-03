package com.arraybase.qmath;

import com.arraybase.GBSearch;
import com.arraybase.NotASearchableTableException;
import com.arraybase.SearchConfig;
import com.arraybase.tm.NodeNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Default operation is a search
 * 
 * @author milton
 * 
 */
public class ABOperation {
	// wire in the input

	private ABOperation itarget = null;
	private String otarget = null;
	private String param = null;
	private List<String> fields = null;
	private String method = "search";
	private List<String> outFields = new ArrayList<String>();
	private int[] indexRange = null;

	public ABOperation() {

	}

	public ABOperation(ABOperation in, String param,
			ArrayList<String> out_fields, int[] indexRange) {
		this.itarget = in;
		this.param = param;
		this.indexRange = indexRange;
		outFields = out_fields;
		targetOutput();
		parseFields();
	}
	
	
	

	public int[] getIndexRange() {
		return indexRange;
	}

	public void setIndexRange(int[] indexRange) {
		this.indexRange = indexRange;
	}

	public List<String> getOutFields() {
		return outFields;
	}

	public void setOutFields(List<String> outFields) {
		this.outFields = outFields;
	}

	private void targetOutput() {
		if (itarget != null)
			otarget = itarget.getOTarget();
	}

	private String getOTarget() {
		return otarget;
	}

	private String getFields(List<String> fields2) {
		String f = "";
		for (String field : fields2) {
			f += "_" + field;
		}
		return f;
	}

	private void parseFields() {
		if (param != null) {
			fields = new ArrayList<String>();
			fields = parseFields(param, fields);
		}
	}

	/**
	 * Parse the fields contained within the paramstring: e.g.
	 * [field1][field2]...[fieldn]
	 * 
	 * @param p
	 * @param _fields
	 * @return
	 */
	private List<String> parseFields(String p, List<String> _fields) {
		int f = p.indexOf('[');
		int l = p.indexOf(']');
		if (f >= 0 && l > f) {
			String field = p.substring(f + 1, l);
			if (field != null) {
				_fields.add(field);
			}
			String np = p.substring(l + 1);
			return parseFields(np, _fields);
		} else
			return _fields;
	}

	public List<String> getFields() {
		return fields;
	}

	public ABOperation getItarget() {
		return itarget;
	}

	public void setItarget(ABOperation itarget) {
		this.itarget = itarget;
	}

	public String getOtarget() {
		return otarget;
	}

	public void setOtarget(String otarget) {
		this.otarget = otarget;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		// default operation does not take in input
		// default operation does not push to output
		// search2 search = new search2 ();
		// search.exec(target.toString()+".search("+ param + ")", null);
		ABOperation in = getItarget();
		String out = in.getOtarget();
		String param = getParam();
		String searchString = param;

		String[] cols = null;
		List<String> outfields = getOutFields();
		if (outfields != null) {
			cols = outfields.toArray(new String[outfields.size()]);
		}
		try {

			if (indexRange != null && indexRange.length == 2) {
				return GBSearch
						.searchAndDeploy(out, searchString, null, cols,
								indexRange[0], indexRange[1],
								new SearchConfig(SearchConfig.NODE_CONFIG));
			} else {
				return GBSearch.searchAndDeploy(out, searchString, null, cols,
						new SearchConfig(SearchConfig.NODE_CONFIG));
			}
		} catch (NotASearchableTableException e) {
			e.printStackTrace();
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
