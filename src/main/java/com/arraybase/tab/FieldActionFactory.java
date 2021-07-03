package com.arraybase.tab;

import java.net.ConnectException;
import java.util.ArrayList;




import com.arraybase.GB;
import com.arraybase.shell.cmds.GBPathNotFoundException;
import com.arraybase.shell.cmds.TrimField;
import com.arraybase.shell.cmds.setFieldValues;
import com.arraybase.tab.field.FieldAction;
import com.arraybase.tab.field.ToLowerCase;
import com.arraybase.tab.field.FieldNotFoundException;
import com.arraybase.tab.field.NoFieldActionFound;
import com.arraybase.tab.field.TypeNotCorrect;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBRGX;

public class FieldActionFactory {

	public final static String TO_LOWERCASE = "tolowercase";
	public final static String TRIM = "trim\\(\\s*\\)";
	public final static String TRIGGER = "trigger";
	public final static String SET = "set\\s";
	public final static String SET_FIELD = "set\\s*\\(.*\\)";

	public static FieldAction create(String target_table, String target_field,
			String action) throws GBPathNotFoundException, NoFieldActionFound,
			FieldNotFoundException, TypeNotCorrect {
		final String path = GB.pwd();
		if (!target_table.startsWith("/"))
			target_table = path + "/" + target_table;
		TNode node = GB.getNodes().getNode(target_table);
		if (node == null)
			throw new GBPathNotFoundException("Path : " + target_table
					+ " was not found.");
		// check the node to make sure we have the field we want to opperate on.
		try {
			ArrayList<GColumn> cols = GB.describeTable(target_table);
			boolean found = false;
			if (cols == null || cols.size() <= 0) {
				GB.print(" No fields on this table. ");
				GB.print("\t Cannot perform field-action");
				return null;
			}
			for (GColumn col : cols) {
				String n = col.getName();
				if (n.equalsIgnoreCase(target_field))
					found = true;
			}
			if (!found) {
				throw new FieldNotFoundException(target_table, target_field);
			}
			System.out.println ( " Action : "+ action);

			if (action.equalsIgnoreCase(TO_LOWERCASE)) {
				ToLowerCase t = new ToLowerCase(node, cols, target_table,
						target_field);
				return t;
			} else if (action.matches(SET_FIELD)) {
				setFieldValues t = new setFieldValues(node, cols, target_table,
						target_field, action);
				return t;
			} else if ( action.matches ( TRIM ) )
			{
				TrimField tf = new TrimField (node, cols, target_table, target_field);
				return tf;
			}

			throw new NoFieldActionFound(action);
		} catch (ConnectException e) {
			e.printStackTrace();
			throw new FieldNotFoundException(target_table, target_field);
		}

	}

}
