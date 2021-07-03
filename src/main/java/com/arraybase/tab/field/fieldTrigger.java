package com.arraybase.tab.field;

import java.util.ArrayList;

import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tree.TNode;

public class fieldTrigger implements FieldAction {
	
	private TNode node = null;
	private ArrayList<GColumn> cols = null;
	private String target_table = null;
	private String action = null;
	private String target_field = null;

	public fieldTrigger(TNode node, ArrayList<GColumn> cols,
			String target_table, String target_field, String action) {
		this.node = node;
		this.cols = cols;
		this.action = action;
		this.target_table = target_table;
		this.target_field = target_field;
		
	}

	/**
	 *  trigger action from a field 
	 */
	public void start() throws UsageException {
		
		
		
	
		
		
		
	}
}
