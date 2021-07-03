package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.NotASearchableTableException;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.google.gson.Gson;

public class DiffField implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		
		if ( args != null && args.length ==1  ){
			String field = args[0];
			if ( field != null )
				field = field.trim();
			printDiff(target, field);
			
		}
		
		
		return null;
	}

	private void printDiff(String target, String field) {
		// iterate over a field and print the difference.
		TNode node = GB.getNodes().getNode(target);
		NodeProperty nps = NodeManager.getNodeProperty(node.getNode_id(), NodeProperty.NODE_GENERATOR);
		String json = nps.getProperty();
		Gson g = new Gson();
		Map installer = g.fromJson(json, Map.class);
//		String driver =  installer.get(ABQFile.DRIVER);

		
		GBSearch search = GB.getSearch();
		String[] fc = {field};
		try {
			Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch.searchAndDeploy(target, "*:*", field + " DESC", fc, null);
			while ( it.hasNext() ){
				ArrayList<LinkedHashMap<String, Object>> vals = it.next();
				for ( LinkedHashMap<String, Object> val : vals )
				{
					Object value = val.get ( field );
					System.out.println ( value );
				}
			}
		} catch (NotASearchableTableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
