package com.arraybase.tab.field;

import com.arraybase.*;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.TNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ToLowerCase implements FieldAction, Runnable {

	private TNode table = null;
	private String field = null;
	private String path = null;
	private ArrayList<GColumn> cols = null;

	public ToLowerCase(TNode target_table, ArrayList<GColumn> cols,
			String path, String target_field) throws TypeNotCorrect {
		this.path = path;
		this.cols = cols;
		table = target_table;
		field = target_field;
		// we need to make sure the column is available as a string 
		boolean isString = false;
		for (GColumn c : cols) {
			if (c.getName().equalsIgnoreCase(field)) {
				if ((c.getType().equalsIgnoreCase(GColumn.STRING))
						|| (c.getType().equalsIgnoreCase(GColumn.STRING))) {
					isString = true;
				}
			}
		}
		
		if ( !isString )
			throw new TypeNotCorrect ( "You may only perform this action on a String or Text type");
		
	}

	public void start() {
		GB.print("Starting the field operation: ToLowerCase ");
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {

		GB.print("Converting all values in field : "+ field + " to lower case.");
		String[] col = { "TMID", field };
		try {
			ABTable table = new ABTable(path);
			Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
					.searchAndDeploy(path, "*:*", null, col, new SearchConfig(SearchConfig.RAW_SEARCH));
			while (it.hasNext()) {

				ArrayList<LinkedHashMap<String, Object>> rows = it.next();
				for (LinkedHashMap<String, Object> row : rows) {
					Object pk = row.get("TMID");
					Object editVal = row.get(field);
					table.insert("TMID:" + pk, field, editVal.toString());
				}

			}

		} catch (NotASearchableTableException e) {
			e.printStackTrace();
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}

	}

}
