package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.db.util.SourceType;
import com.arraybase.lac.LAC;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBRGX;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class InsertLacCommand implements GBPlugin {

	public String exec(String command, String variable_key) {
		String[] lac = LAC.parse(command);
		String target = lac[0];
		String action = lac[1];
		String data = lac[2];

		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(GB.pwd() + "/" + target);
		if (node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)
				|| node.getNodeType().equalsIgnoreCase(SourceType.DB.name)) {
			String[] list = data.split(",");
			ArrayList<String> values = new ArrayList<String>();
			for (String s : list) {
				values.add(s.trim());
			}
			// first figure out the 
			
			// if the param is a query param.
			if ( values.size()==1){
				String va = values.get ( 0 );
				if ( va.matches(GBRGX.SEARCH_FORMAT_OUTPUT)){
					
					String _path = LAC.getTarget(va);
					String searchString = LAC.getData(va);
					String sortString = null;
					
					if ( searchString.contains(",")){
						sortString = search2.parseSortString(searchString);
					}
					int li= va.lastIndexOf(')');
					String fields = va.substring(li+1);
					String[] _cols = search2.getColumnArray(fields);
					
					
					GBSearch search = GB.getSearch();
					try {
						if ( !_path.startsWith("/")){
							_path = GB.pwd () + "/" + _path;
						}
						Iterator<ArrayList<LinkedHashMap<String, Object>>> itt = GBSearch.searchAndDeploy(_path,
								searchString, sortString, _cols, new SearchConfig(SearchConfig.RAW_SEARCH));
						String path = target;
						if ( !path.startsWith("/")){
							path = GB.pwd () + "/" + path;
						}
						ABTable table = new ABTable ( path );
						table.append(itt);
						
						
					} catch (NotASearchableTableException e) {
						e.printStackTrace();
					} catch (NodeNotFoundException e) {
						e.printStackTrace();
					}
					return "Insert complete.";
					
				}
			}
			
			String link = node.getLink();
			GB.print("link: " + link);
			String _schema = LAC.getTarget(link);
			String url = GB.getDefaultURL();
			TableManager.add(values, url, _schema);
		}
		GB.print("Insert  " + data);
		nodes.save(node);

		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
