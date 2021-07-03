package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.db.util.SourceType;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.SolrTargetNotDefinedException;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.TNode;

import java.io.PrintStream;
import java.net.ConnectException;
import java.util.*;

public class searchflow implements GBPlugin {

	public String exec(String c, String variable_key) throws UsageException {
		int ti = c.indexOf('.');
		int t2 = c.indexOf('(');
		int t3 = c.indexOf(')');
		String target = "";
		if (ti > 0) {
			target = c.substring(0, ti);
		}

		int intoco = c.indexOf('>');
		String va = c.substring(intoco + 1);
		if (va != null)
			va = va.trim();
		String search_string = c.substring(t2 + 1, t3);
		// {{ PARSE ARGUMENTS FROM THE SEARCH STRING }}
		if (search_string.equals("?")) {
			return "help";
		}
		String sortString = null;
		if (search_string.contains(",")) {
			sortString = parseSortString(search_string);
			search_string = removeSortString(search_string);

		}
		String fields = c.substring(t3 + 1);
		if (fields != null && fields.length() > 0) {
			ArrayList<String> columns = new ArrayList<String>();
			columns = getColumns(fields);
			String[] cols = null; 
			if ( cols != null )
				cols = columns.toArray(new String[columns.size()]);
			if (fields != null)
				fields = fields.trim();
			if (target != null)
				target = target.trim();
			GBSearch gs = GB.getSearch();
			String path = GB.pwd() + "/" + target;
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			int count = 0;
			try {
				Map<String, String> node_props = GB.getNodeProps(path);
				SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
				config.setConfigProperties(node_props);
				if (sortString == null)
					GB.print("\t-Search : " + search_string
							+ " sortstring : {}");
				else
					GB.print("\t-Search : " + search_string + " sortstring : "
							+ sortString);
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString, cols,
								config);
				ArrayList<GColumn> desc = GB.describeTable(path);
				ArrayList<LinkedHashMap<String, Object>> first = it.next();
				if (first == null || first.size() <= 0) {
					GB.print("No results");
					return "No results";
				}
				LinkedHashMap fmap = first.get(0);
				if (fmap != null) {
					print(fmap.keySet());
				}
				

				if ( !va.startsWith("/")){
					va = GB.pwd() + "/" + va;
				}
				
				
//				if ( va.matches ( GBRGX.field_target ) )
//				{
//					// put the search output into the fields
//					System.out.println ( " Cannot perform this field set function this way.. ");
//					return null;
//				}else if ( va.matches ( GBRGX.target )){
//					
//					
//					
//				}
				
				TNode node = GB.getNodes().getNode(va);
				if (node != null && (!SourceType.isTable(node.getNodeType()))) {
					GB.print("It appears you are trying to pass a table iterator into a node that is not a table object.");
					return "";
				} else if (node == null) {
					// {{ create the table }}
					GB.print ( "Creating table.");
					node = createTable(va, fmap, desc);
				}

				// with the node object lets create a tablemanager object to do
				// the row additions
				TableManager tableManager = new TableManager(node);
				count += tableManager.addList(first);
				while (it.hasNext()) {
					ArrayList<LinkedHashMap<String, Object>> increment = it
							.next();
					count += tableManager.addList(increment);
				}
				GB.print(count + " hits. ");
			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectException e) {
				e.printStackTrace();
			} catch (SolrTargetNotDefinedException e) {
				e.printStackTrace();
			}
		} else {
			GBSearch gs = GB.getSearch();
			PrintStream out = System.out;
			String path = GB.pwd() + "/" + target;
			gs.searchTable(path, search_string, sortString, out, 0, 100000,
					new SearchConfig(SearchConfig.NODE_CONFIG));
		}
		return "";
	}

	/**
	 * Remove the sort string
	 * 
	 * @param _search
	 * @return
	 */
	private String removeSortString(String _search) {
		int com = _search.indexOf(',');
		if (com > 0) {
			String st = _search.substring(0, com);
			return st.trim();
		}
		return _search;
	}

	/**
	 * Create a table with the correct types.w
	 * 
	 * @param va
	 * @param first
	 * @param desc
	 */
	private TNode createTable(String va, LinkedHashMap<String, Object> first,
			ArrayList<GColumn> desc) {
		String path = GB.pwd() + "/" + va;
		System.out.println(" creat table " + path);

		LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
		Set<String> keys = first.keySet();
		for (String key : keys) {
			GColumn column = getColumn(key, desc);
			if (column != null) {
				fields.put(key, column.getType());
			}
		}
		GB.print("Creating the table : " + path);
		TNode node = GB.createTable(GB.DEFAULT_UER, path, fields);
		// we should add some exception throwing here... if things don't work..
		// later.
		return node;
	}

	private GColumn getColumn(String key, ArrayList<GColumn> desc) {
		for (GColumn c : desc) {
			if (c.getName().equalsIgnoreCase(key))
				return c;
		}

		return null;
	}

	/**
	 * @param fields
	 * @param columns
	 * @return
	 */
	private static String getColumns(String fields, ArrayList<String> columns) {
		String f = fields;
		if ((f == null) || !(f.contains("[") && f.contains("]"))) {
			return null;
		}
		int index = f.indexOf('[');
		int lindex = f.indexOf(']');
		String field = f.substring(index + 1, lindex);
		columns.add(field);
		String t = f.substring(lindex + 1);
		int i2 = t.indexOf('[');
		if (i2 < 0)
			return t;
		return getColumns(t, columns);
	}

	public static void main(String[] args) {

		String test = "http://thisisatest.com?f[i][anothersomething]eld[mylink][lfield]andth[field1]enanother&[field2]";
		search2 s = new search2();
//		ArrayList<String> cols = s.getColumns(test);
//		for (String c : cols) {
//			System.out.println(c);
//		}

	}

	ArrayList<String> getColumns(String fields) {
		ArrayList<String> columns = new ArrayList<String>();
		getColumns(fields, columns);
		return columns;
	}

	private String parseSortString(String search_string) {
		String[] sp = search_string.split(",");
		search_string = sp[0];
		String sort_field = null;
		String direction = null;
		if (sp.length >= 2)
			sort_field = sp[1];
		if (sp.length >= 3)
			direction = sp[2];
		if (direction == null)
			direction = "desc";
		for (String s : sp) {
			if (s.contains("=")) {
				String[] var = s.split("=");
				String v = var[0].trim();
				if (v.equalsIgnoreCase("search"))
					search_string = var[1];
				else if (v.equalsIgnoreCase("sort")) {
					sort_field = var[1];
				} else if (v.equalsIgnoreCase("direction")) {
					direction = var[1];
				}
			}
		}
		sort_field = sort_field.trim();

		if (sort_field.endsWith(" desc") || sort_field.endsWith(" asc"))
			return sort_field;

		String sortString = sort_field + " " + direction;
		return sortString;
	}

	private void print(ArrayList<LinkedHashMap<String, Object>> increment,
			String post_string) {
		for (LinkedHashMap<String, Object> ls : increment) {
			Set<String> keys = ls.keySet();
			String ps = post_string;
			for (String key : keys) {
				ps = ps.replace("[" + key + "]", ls.get(key) + "\t\t");
			}
			System.out.println(" " + ps);
		}
	}

	private void print(Set<String> keys) {
		String ps = "";
		for (String key : keys) {
			ps += "[" + key + "]" + "\t\t";
		}
		GB.print(ps);
	}

	private int print(ArrayList<LinkedHashMap<String, Object>> increment) {
		int count = 0;
		for (LinkedHashMap<String, Object> ls : increment) {
			Set<String> keys = ls.keySet();
			String ps = "";
			for (String key : keys) {
				ps += "[" + ls.get(key) + "]" + "\t\t";
			}
			GB.print(ps);
			count++;
		}
		return count;
	}

	private ArrayList<String> process(String post_string) {
		return null;
	}

	private void printArgs() {
		GB.print("search=$string, sort=$field, sortd=[desc|asc]");
		GB.print("search=Comment:Jeff*, sort=row_index, direction=desc");

	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
