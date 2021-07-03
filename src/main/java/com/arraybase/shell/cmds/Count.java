package com.arraybase.shell.cmds;

import java.net.ConnectException;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.io.parse.ABResultsVar;
import com.arraybase.db.util.SourceType;
import com.arraybase.search.ABaseResults;
import com.arraybase.tab.field.FieldNotFoundException;
import com.arraybase.tm.tree.TNode;

public class Count implements GBPlugin {

	/**
	 * count the number of distinct groups [column][count] {desc|asc}
	 * 
	 * @see com.arraybase.GBPlugin#exec(java.lang.String, java.lang.String)
	 */
	public String exec(String command, String variable_key) {

		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		String field = "";
		if (args == null || args.length < 1) {
			// count the rows of the table
		} else if (args.length == 1) {
			// count the number of particular field types
			field = args[0];
			TNode node = GB.getNodes().getNode(target);

			if (node == null) {
				GB.print(" Node node found : " + target);
				return "No  node. ";
			}
			if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
					|| node.getNodeType().equalsIgnoreCase(
							SourceType.TABLE.name)) {
				GBSearch sb = GB.getSearch();
				try {
					sb.printDistinct(target, "*:*", field);
				} catch (ConnectException e) {
					GB.print("Not sure why but failed to connect to the server "
							+ e.getLocalizedMessage());
				} catch (FieldNotFoundException e) {
					GB.print("Ther field  " + field
							+ " is not a valid field in this " + target
							+ " \t\tERR(" + e.getLocalizedMessage() + ")");
				}
			}

		} else if (args.length == 2) {
			// count the number of particular field types
			field = args[0];
			String search = args[1];
			TNode node = GB.getNodes().getNode(target);

			if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
					|| node.getNodeType().equalsIgnoreCase(
							SourceType.TABLE.name)) {
				GBSearch sb = GB.getSearch();

				try {
					sb.printDistinct(target, search, field);
				} catch (ConnectException e) {
					GB.print("Not sure why but failed to connect to the server "
							+ e.getLocalizedMessage());
				} catch (FieldNotFoundException e) {
					GB.print("Ther field  " + field
							+ " is not a valid field in this " + target
							+ " \t\tERR(" + e.getLocalizedMessage() + ")");
				}
			}
		}

		return "count complete";
	}

	public GBV execGBVIn(String command, GBV input) {

		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		if (args == null || args.length < 1) {
			// count the rows of the table
		} else if (args.length == 1) {
			// count the number of particular field types
			String field = args[0];
			TNode node = GB.getNodes().getNode(target);

			if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
					|| node.getNodeType().equalsIgnoreCase(
							SourceType.TABLE.name)) {
				GBSearch sb = GB.getSearch();
				ABaseResults abr;
				try {
					abr = sb.getDistinct(target, "*:*", field);
					ABResultsVar var = new ABResultsVar(abr);
					return var;
				} catch (ConnectException e) {
					GB.print("Not sure why but failed to connect to the server "
							+ e.getLocalizedMessage());
				} catch (FieldNotFoundException e) {
					GB.print("Ther field  " + field
							+ " is not a valid field in this " + target
							+ " \t\tERR(" + e.getLocalizedMessage() + ")");
				}
				return null;
			}

		} else if (args.length == 2) {
			// count the number of particular field types
			String field = args[0];
			String search = args[1];
			TNode node = GB.getNodes().getNode(target);

			if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
					|| node.getNodeType().equalsIgnoreCase(
							SourceType.TABLE.name)) {
				GBSearch sb = GB.getSearch();
				ABaseResults ab = null;
				try {
					ab = sb.getDistinct(target, search, field);
				} catch (ConnectException e) {
					GB.print("Not sure why but failed to connect to the server "
							+ e.getLocalizedMessage());
				} catch (FieldNotFoundException e) {
					GB.print("Ther field  " + field
							+ " is not a valid field in this " + target
							+ " \t\tERR(" + e.getLocalizedMessage() + ")");
				}
				if (ab == null)
					return null;
				ABResultsVar var = new ABResultsVar(ab);
				return var;
			}
		}
		return null;
	}
}
