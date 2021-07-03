package com.arraybase.shell.cmds.plot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.arraybase.GBV;
import com.arraybase.shell.cmds.search2;

/**
 * This is an object that determines the xmin for a given search
 * target.action(search)[x][y]
 * 
 * @author milton
 * 
 */
public class FieldSort {

	private String target = null;
	private String action = null;
	private String data = null;
	private String field = null;
	private String direction = null;

	public FieldSort(String target, String action, String data, String field, String _min_or_max) {
		this.target = target;
		this.action = action;
		this.data = data;
		this.field = field;
		direction = _min_or_max;
	}

	public float calculate() {
		String d = data;
		String search = target + ".search" + "(" + data + " NOT " + field+":NaN," + field
				+ " " + direction +")[" + field + "]{0-1}";
		if (d.contains(","))
			search = target + ".search" + "(" + data + " NOT " + field+":NaN,"  + direction + ")[" + field
					+ "]{0-1}";
		System.out.println ( " FieldSort search : "+ search );
		search2 s = new search2();
		GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> iv = s
				.execGBVIn(search, null);
		Iterator<ArrayList<LinkedHashMap<String, Object>>> ob = iv.get();
		ArrayList<LinkedHashMap<String, Object>> incr = ob.next();
		LinkedHashMap<String, Object> first = incr.get(0);
		Object v = first.get(field);
		if (v instanceof String)
			return 0f;
		else if (v instanceof Number)
			return ((Number) v).floatValue();
		else
			return 0f;
	}
	public String toString ()
	{
		return calculate () + "";
	}

}
