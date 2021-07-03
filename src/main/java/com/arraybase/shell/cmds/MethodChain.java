package com.arraybase.shell.cmds;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBSearchIterator;
import com.arraybase.GBV;
import com.arraybase.NotASearchableTableException;
import com.arraybase.SearchConfig;
import com.arraybase.lang.ItrVar;
import com.arraybase.qmath.ABOperation;
import com.arraybase.qmath.ABTarget;
import com.arraybase.qmath.EvalOperation;
//import com.arraybase.qmath.AppendColumn;
import com.arraybase.qmath.IQROperation;
import com.arraybase.qmath.LinearRegressionOperation;
import com.arraybase.qmath.MeanOperation;
//import com.arraybase.qmath.TTestOperation;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.util.GBRGX;

public class MethodChain implements GBPlugin {
	public static String function = "search|set|iqr|mean|stdv|eval";
	GBSearch gs = GB.getSearch();
	private int start_count = 0;
	private int end_count = Integer.MAX_VALUE;
	private static final String fun = GBRGX.target + "." + function;
	private static Pattern pattern = Pattern.compile(fun);
	private static Pattern function_pattern = Pattern.compile("\\.(" + function
			+ ")");
	private ArrayList<ABOperation> list = new ArrayList<ABOperation>();
	public static final String target = "[A-Za-z0-9_]*";

	public static void main(String[] args) {
		// String cc = "/data/pub.search(*).iqr([chromStart][chromEnd])";
		String cc = "pub.search(sdd:adsfasd* AND (some:399 AND someting:[9 TO 200])).iqu([adsdf][adfadsf])";
		if (cc.matches(GBRGX.METHOD_CHAIN)) {

			System.out.println(" got it ! ");

		} else
			System.out.println(" NOPE ");

		// MethodChain s = new MethodChain();
		// s.exec(cc, null);
	}

	/**
	 * lthis also handles target.set(field,regex,value)
	 */
	public String exec(String c, String variable____key) {	
		System.out.println(" method chain ");
		start_count = 0;
		end_count = Integer.MAX_VALUE;
		// this is a search and replace.
		String search_ = c;
		ArrayList<ABOperation> pl = build(list, c);
		// once we have the pl list we need to execute it.
		// exec(pl);

		ABOperation b = list.get(list.size() - 1);
		Iterator<ArrayList<LinkedHashMap<String, Object>>> values = b.exec();
		GB.print(values);
		return c;
	}

	private void exec(ArrayList<ABOperation> pl) {
		for (ABOperation op : pl) {
			op.exec();
		}
	}

	// String cc = "/data/pub.search(*).iqr([chromStart][chromEnd])";
	private ArrayList<ABOperation> build(ArrayList<ABOperation> list,
			String method_name) {
		Matcher matcher2 = function_pattern.matcher(method_name);
		if (matcher2.find()) {
			int index = matcher2.start();
			// String s = c.substring(0, index + 1);
			int fun_end = matcher2.end();
			int end_function_index = method_name.indexOf(").", fun_end);
			ArrayList<String> fields = null;
			int[] indexRange = null;
			if (end_function_index > 0) {
				int to_the_next_function_start = method_name.indexOf(".",
						end_function_index + 2);
				if (to_the_next_function_start < 0) {// then we have a situation
														// where we just want to
														// get
														// it all the way to the
														// end
														// of the string
					String field_string_scope = method_name
							.substring(end_function_index);

					// the fields string scope should be representative of the
					// post param close to the end of the string. --in this
					// case.
					indexRange = GBIO.parseRange( field_string_scope);
					fields = GBIO.parseFieldNames(field_string_scope);
				} else {
					
					//	 example string at this point...			).mean([rpkm]{0-1})
					// if there is a method after 
					//this method... it is cut out of this "field_string_scope" string.. 
					//so as not to mess with any specific parsing.
					String field_string_scope = method_name.substring(
							end_function_index, to_the_next_function_start);
					indexRange = GBIO.parseRange( field_string_scope);
					fields = GBIO.parseFieldNames(field_string_scope);
				}
			}

			String function = method_name.substring(index + 1, fun_end);
			System.out.println(method_name + "\n\t function: " + function
					+ "<-- function ");
			ABOperation target = parseTarget(method_name, list);
			String param = parseParam(method_name);
			ABOperation ab = create(function, target, param, fields, indexRange);
			if (list.size() > 0) {
				ABOperation p = list.get(list.size() - 1);
			}
			list.add(ab);
			if (ab != null) {
				if (end_function_index > 0) {
					String sub = method_name.substring(end_function_index + 1);
					return build(list, sub);
				}
			}
		}else
			GB.print ( "No method " + method_name);
		return list;
	}

	private String parseParam(String c) {
		int index = c.indexOf('(');
		int cut = c.indexOf(").");
		if (cut > 0) {
			String param = c.substring(index + 1, cut);
			return param;
		} else {
			int lindex = c.lastIndexOf(')');
			if (lindex < 0)
				return null;
			String param = c.substring(index + 1, lindex);
			return param;
		}
	}

	private ABOperation parseTarget(String c, ArrayList<ABOperation> list) {
		// if this then we need to get the target from the list.
		if (c.startsWith(".")) {// the link
			ABOperation target = list.get(list.size() - 1);
			if (target != null)
				return target;
		}
		int index = c.indexOf('.');
		String sub = c.substring(0, index);
		if (!sub.startsWith("/")) {
			sub = GB.pwd() + "/" + sub;
		}
		ABTarget target = new ABTarget(sub);
		return target;
	}

	private ABOperation create(String _op, ABOperation target, String params,
			ArrayList<String> fields, int[] indexRange) {
		if (_op.equalsIgnoreCase("search")) {
			return new ABOperation(target, params, fields, indexRange);
		} else if (_op.equalsIgnoreCase("iqr")) {
			return new IQROperation(target, params, fields);
		} else if (_op.equalsIgnoreCase("mean")) {
			return new MeanOperation(target, params, fields);
		} else if (_op.equalsIgnoreCase("lr")) {
			return new LinearRegressionOperation(target, params, fields);
		}else if (_op.equalsIgnoreCase("pairedTTest")) {
//			return new TTestOperation(target, params, fields);
		}else if (_op.equalsIgnoreCase("appendColumn")) {
//			return new AppendColumn(target, params, fields);
		}else if (_op.equalsIgnoreCase("eval")) {
			return new EvalOperation(target, params, fields, indexRange);
		}
		return null;
	}

	private ArrayList<String> getAllColumns(String path)
			throws ConnectException {
		ArrayList<GColumn> column = GB.describeTable(path);
		ArrayList<String> cols = new ArrayList<String>();
		for (GColumn cc : column) {
			cols.add(cc.getName());
		}
		return cols;
	}

	private void pullRange(String c) {
		start_count = 0;
		end_count = Integer.MAX_VALUE;

		int bindex = c.lastIndexOf('{');
		if (bindex > 0) {
			String sub = c.substring(bindex);
			if (sub.matches(GBRGX.COUNT_RANGE + "$")) {
				// we have a range.
				int ob = sub.lastIndexOf('{');
				int cb = sub.lastIndexOf('}');
				String rng = sub.substring(ob + 1, cb);
				rng = rng.trim();
				int m = rng.indexOf('-');
				if (m <= 0) {
					GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
				}
				String bg = rng.substring(0, m);
				if (bg == null) {
					GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
				}
				bg = bg.trim();
				String eg = rng.substring(m + 1);
				if (eg == null) {
					GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
				}
				eg = eg.trim();

				try {
					int start = Integer.parseInt(bg);
					int end = Integer.parseInt(eg);

					this.start_count = start;
					this.end_count = end;
				} catch (NumberFormatException nf) {
					nf.printStackTrace();
					GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");

				}
			}
		}
	}

	public String basicSearch(String path, String sortString,
			String search_string, ArrayList<String> columns) {
		{
			int count = 0;
			String[] cols = columns.toArray(new String[columns.size()]);
			try {
				Map<String, String> node_props = GB.getNodeProps(path);
				SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
				config.setConfigProperties(node_props);
				if (sortString == null)
					GB.print("\t-Search : "
							+ search_string
							+ " sortstring : { default sorting... ->insert timestamp }");
				else
					GB.print("\t-Search : " + search_string + " sortstring : "
							+ sortString);
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString, cols,
								start_count, end_count, config);
				ArrayList<LinkedHashMap<String, Object>> first = it.next();
				if (first == null || first.size() <= 0) {
					GB.print("No results");
					return "No results";
				}
				LinkedHashMap fmap = first.get(0);
				if (fmap != null) {
					print(fmap.keySet());
				}
				count = first.size();
				print(first);
				while (it.hasNext()) {
					ArrayList<LinkedHashMap<String, Object>> increment = it
							.next();
					print(increment);
					count += increment.size();
				}
				GB.print("\t\tCount " + count + ". ");
				if (it instanceof GBSearchIterator) {
					GBSearchIterator itg = (GBSearchIterator) it;
					GB.print("\t\tSearch Total " + itg.getTotal());
				}

			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// GB.print("Search complete. ");
		return "";
	}

	/**
	 * Remove the sort string
	 * 
	 * @param search_string
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
	 * @param fields
	 * @param columns
	 * @return
	 */
	public static String getColumns(String fields, ArrayList<String> columns) {
		String f = fields;
		if ((f == null) || !(f.contains("[") && f.contains("]"))) {
			return null;
		}
		if (f.contains(")")) {
			int ind = f.lastIndexOf(')');
			f = f.substring(ind + 1);
			f = f.trim();
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

	public static ArrayList<String> getColumns(String fields) {
		ArrayList<String> columns = new ArrayList<String>();
		getColumns(fields, columns);
		return columns;
	}

	public static String[] getColumnArray(String fields) {
		ArrayList<String> columns = new ArrayList<String>();
		getColumns(fields, columns);
		String[] c = columns.toArray(new String[columns.size()]);
		return c;
	}

	public static String parseSortString(String search_string) {
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

	private void printArgs() {
		GB.print("search=$string, sort=$field, sortd=[desc|asc]");
		GB.print("search=Comment:Jeff*, sort=row_index, direction=desc");
	}

	public GBV execGBVIn(String c, GBV input) {
		if (input != null) {
			GB.print("This object does not handle input... i.e. command1 | searchobject.search(etc...)");
			return null;
		}
		int ti = c.indexOf('.');
		int t2 = c.indexOf('(');
		int t3 = c.lastIndexOf(')');

		pullRange(c);

		String target = "";
		if (ti > 0) {
			target = c.substring(0, ti);
		}
		// String action = c.substring(ti + 1, t2);
		String search_string = c.substring(t2 + 1, t3);
		// String post_string = c.substring(t3 + 1);
		// {{ PARSE ARGUMENTS FROM THE SEARCH STRING }}
		if (search_string.equals("?")) {
			printArgs();
			return null;
		}
		String sortString = null;
		if (search_string.contains(",")) {
			sortString = parseSortString(search_string);
			search_string = removeSortString(search_string);
		}

		if (target != null)
			target = target.trim();
		GBSearch gs = GB.getSearch();
		String path = GB.pwd() + "/" + target;
		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		int count = 0;
		Map<String, String> node_props = GB.getNodeProps(path);
		SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
		config.setConfigProperties(node_props);

		String fields = c.substring(t3 + 1);
		if (fields != null && fields.length() > 0) {
			ArrayList<String> columns = new ArrayList<String>();
			columns = getColumns(fields);
			if (!contains(columns, "TMID"))
				columns.add("TMID");
			String[] cols = null;

			if (columns != null) {
				cols = columns.toArray(new String[columns.size()]);
			}
			if (fields != null)
				fields = fields.trim();

			try {

				if (sortString == null) {
					// GB.print("\t-Search : " + search_string
					// + " sortstring : {}");
				} else {
					// GB.print("\t-Search : " + search_string +
					// " sortstring : "
					// + sortString);
				}
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString, cols,
								start_count, end_count, config);
				GBV<Iterator> itrv = new ItrVar(it);
				return itrv;
			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// System.out.println(" searchstring : " + search_string
			// + " sortstring : " + sortString);
			try {
				ArrayList<GColumn> column = GB.describeTable(path);
				String[] fieldstr = new String[column.size()];
				int index = 0;
				for (GColumn cc : column) {
					fieldstr[index++] = cc.getName();
				}
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString,
								fieldstr, start_count, end_count, config);
				GBV<Iterator> itrv = new ItrVar(it);
				return itrv;

			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotASearchableTableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// SearchConfig.NODE_MANAGED);
		}

		return null;
	}

	private boolean contains(ArrayList<String> columns, String field) {
		for (String c : columns) {
			if (c.equalsIgnoreCase(field))
				return true;
		}
		return false;
	}

}
