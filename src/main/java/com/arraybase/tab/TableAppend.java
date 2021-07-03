package com.arraybase.tab;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.arraybase.tm.GColumn;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tree.TNode;

public class TableAppend {
	private TNode dest = null;
	private ArrayList<GColumn> fields = null;
	
	public TableAppend ( TNode append){
		this.dest = append;
		init(dest);
	}
	private void init(TNode dest2) {
		try {
			fields = GBTables.describeTable(dest);
		} catch (ConnectException e) {
			e.printStackTrace();
		}
	}
	public void append(Iterator<ArrayList<LinkedHashMap<String, Object>>> it) {
		
		
	}
}