package com.arraybase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.arraybase.tm.DefaultWhereClause;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.WhereClause;

/**
 * Used to attach a set of columns to an existing table.
 * 
 * @author milton
 * 
 */
class GBTableAttach {

	private String left = null;
	private String right = null;
	private int data_start_row = 0;
	private String[] types = null;
	private String[] titles = null;

	public ArrayList<GColumn> getColumns() {
		ArrayList<GColumn> columns = new ArrayList<GColumn>();
		int i = 0;
		for (String title : titles) {
			String type = types[i];
			GColumn column = new GColumn();
			column.setTitle(title);
			column.setType(type);
			columns.add(column);
			i++;
		}
		return columns;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public void setStartLoadingFromRow(int data_start_index) {
		this.data_start_row = data_start_index;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public int getDataStartLine() {
		return data_start_row;
	}
	
	public String getRight ()
	{
		return right;
	}

	public String getLeft() {
		return left;
	}

	/**
	 *  
	 * @return
	 */
	public ArrayList<WhereClause> getWhereClauses() {
		// this is not well thought out... still need to work on this.
		// but for now we're going with what we got!
		ArrayList<WhereClause> wc = new ArrayList<WhereClause> ();
		WhereClause wc1 = new DefaultWhereClause(left, right);
		wc.add ( wc1 );
		return wc;
	}


}
