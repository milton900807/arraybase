package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.lang.ItrVar;
import com.arraybase.modules.UsageException;
import com.arraybase.qmath.FloatVar;
import com.arraybase.shell.GBCommand;
import com.arraybase.shell.cmds.operator.FieldOperator;
import com.arraybase.tab.FieldFunctionException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.util.GBRGX;

import java.net.ConnectException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class search2 implements GBPlugin {

	public static final Pattern field_function_pattern = Pattern
			.compile(GBRGX.FIELD_FUNCTION_REPLACE);
	private GBSearch gs = GB.getSearch();
	private int start_count = 0;
	private int end_count = Integer.MAX_VALUE;

	/**
	 * lthis also handles target.set(field,regex,value)
	 */
	public String exec(String c, String variable____key) {

		start_count = 0;
		end_count = Integer.MAX_VALUE;

		// this is a search and replace.
		String search_ = c;
		if (c.contains(").set")) {
			int ssi = c.indexOf(").set");
			search_ = c.substring(0, ssi + 1);
		}

		int ti = search_.indexOf('.');
		int t2 = search_.indexOf('(');
		int t3 = search_.lastIndexOf(')');
		String target = "";
		if (ti > 0) {
			target = search_.substring(0, ti);
		}

		String path = target;
		// target is defined at this point
		if (!target.startsWith("/"))
			path = GB.pwd() + "/" + target;

		pullRange(search_);

		// String action = c.substring(ti + 1, t2);
		String search_string = search_.substring(t2 + 1, t3);
		// String post_string = c.substring(t3 + 1);
		// {{ PARSE ARGUMENTS FROM THE SEARCH STRING }}
		if (search_string.equals("?")) {
			printArgs();
			return "help";
		}
		if (search_string.contains(":notnull"))
			search_string = search_string.replace(":notnull", ":[* TO *]");

		String sortString = null;
		if (search_string.contains(",")) {
			sortString = parseSortString(search_string);
			search_string = removeSortString(search_string);
		}

		String fields = search_.substring(t3 + 1);

		ArrayList<String> columns = new ArrayList<String>();

		if (fields != null && fields.length() > 0)
			columns = getColumns(fields);
		else {
			try {
				columns = getAllColumns(path);
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				columns.add("TMID");
				GB.print(" Failed to access the table schema for : " + path);
			}

		}

		if (fields != null)
			fields = fields.trim();
		if (target != null)
			target = target.trim();

		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		// fpkm_tracking.search(directory:heru).set(name,[directory])
		// check if this is a search and function feature.
		int search_set_function = c.indexOf(").set");
		if (search_set_function > 0) {
			int openparen = c.indexOf("(", search_set_function + 5);
			int closeparen = c.lastIndexOf(")");
			String setfun_params = c.substring(openparen + 1, closeparen);

			return searchAndSet(path, search_string, sortString, columns,
					setfun_params);
		} else {
			return basicSearch(path, sortString, search_string, columns);
		}
	}

	private ArrayList<String> getAllColumns(String path)
			throws ConnectException {
		ArrayList<GColumn> column = GB.getAllColumns(path);
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

	private String parseSetField(String c) {

		return null;
	}

	/**
	 * This is a search function that edits the table as it searches. //
	 * fpkm_tracking.search(directory:heru).set(name,Human [directory])
	 * 
	 * @param path
	 * @param search_string
	 * @param sortString
	 * @param columns
	 * @return
	 */
	private String searchAndSet(String path, String search_string,
			String sortString, ArrayList<String> columns, String setfun_params) {
		Matcher m = field_function_pattern.matcher(setfun_params);
		if (m.find()) {
			try {
				ABTable abTable = new ABTable(path);
				if (abTable.exists()) {
					// {{ FIRST NEED TO RECOGNIZE THAT THE SETFUN PARAMS COULD
					// ACTUALLY
					// BE A FUNCTION }}
					// e.g. Name,[Name].replace(HO,HE)
					// in this case we want to process the function for each
					// set.
					GB.print("The set function syntax contains a field-level function.  This will be evaluated for each row.");
					// Name,[Name].replace(HO,HE)) ----e.,g
					int replace_function_index = setfun_params.indexOf(".replace");

					if (replace_function_index > 0) {
						// {{ GET THE COLUMN FIELD }}
						int field_name_end_index = setfun_params.indexOf(',');
						if (field_name_end_index < 0)
							throw new FieldFunctionException(
									"You must provide a field value as the first parameter to this function .");
						String field_value = setfun_params.substring(0,
								field_name_end_index);
						// {{ THEN THIS IS A REPLACE FUNCTION }}
						String replace_params = setfun_params
								.substring(replace_function_index + 8);
						String[] params = GBIO.parseParams(replace_params);
						if (params.length != 2)
							throw new FieldFunctionException(
									"replace function needs two parameters ");
						GB.print("Replace function: " + search_string
								+ " replacing values for field: " + field_value
								+ " from " + params[0] + " to " + params[1]);

						abTable.replace(search_string, sortString, field_value,
								params[0], params[1]);
					}

				}

			} catch (NodeWrongTypeException e) {
				e.printStackTrace();
			} catch (FieldFunctionException e) {
				e.printStackTrace();
			}

		}

		String[] sf = setfun_params.split(",");
		if (sf == null || sf.length == 0) {
			return "No set performed on this search... as no params were parsed. ";
		}

		if (sf.length == 2) {
			String field = sf[0];

			String input = getInputString(sf);

			int count = 0;
			// columns.add("TMID");
			// String[] cols = columns.toArray(new String[columns.size()]);
			try {
				ABTable table = new ABTable(path);
				Map<String, String> node_props = GB.getNodeProps(path);
				SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
				config.setConfigProperties(node_props);
				if (sortString == null)
					GB.print("\tSearch-and-set\t Default sorting used. (last_edited)");
				else
					GB.print("\t-Search-and-set\t Sorting by : " + sortString);
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString, null,
								config);
				ArrayList<LinkedHashMap<String, Object>> first = it.next();
				if (first == null || first.size() <= 0) {
					GB.print("No results");
					return "No results";
				}
				count = first.size();
				// eg. it_cell_lines.search(*).set(name, cells.search(he*))
				update(table, field, input, first);
				while (it.hasNext()) {
					ArrayList<LinkedHashMap<String, Object>> increment = it
							.next();
					update(table, field, input, increment);

					count += increment.size();
					if (it instanceof GBSearchIterator) {
						GBSearchIterator itg = (GBSearchIterator) it;
						GB.print(count + " / " + itg.getTotal());
					} else
						GB.print(count + "  ");
				}
				if (it instanceof GBSearchIterator) {
					GBSearchIterator itg = (GBSearchIterator) it;
					GB.print(count + "  .....  " + itg.getTotal());
				}
				table.commit();
			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			String field = sf[0];
			String regex = sf[1];
			String input = sf[2];

			int count = 0;
			// columns.add("TMID");
			// String[] cols = columns.toArray(new String[columns.size()]);
			try {
				ABTable table = new ABTable(path);
				Map<String, String> node_props = GB.getNodeProps(path);
				SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
				config.setConfigProperties(node_props);
				if (sortString == null)
					GB.print("\t-Search-and-set: " + search_string
							+ " sortstring : {}");
				else
					GB.print("\t-Search : " + search_string + " sortstring : "
							+ sortString);
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, search_string, sortString, null,
								config);
				ArrayList<LinkedHashMap<String, Object>> first = it.next();
				if (first == null || first.size() <= 0) {
					GB.print("No results");
					return "No results";
				}
				count = first.size();
				// eg. it_cell_lines.search(*).set(name, cells.search(he*))

				// $gene.replace(kras --> regex
				// something) --> something

				replace(table, field, regex, input, first);
				while (it.hasNext()) {
					ArrayList<LinkedHashMap<String, Object>> increment = it
							.next();
					replace(table, field, regex, input, increment);

					count += increment.size();
					if (it instanceof GBSearchIterator) {
						GBSearchIterator itg = (GBSearchIterator) it;
						GB.print(count + " / " + itg.getTotal());
					} else
						GB.print(count + "  ");
				}
				if (it instanceof GBSearchIterator) {
					GBSearchIterator itg = (GBSearchIterator) it;
					GB.print(count + "  .....  " + itg.getTotal());
				}
				table.commit();
			} catch (NotASearchableTableException e) {
				e.printStackTrace();
			} catch (NodeNotFoundException e) {
				e.printStackTrace();
			}

		}

		return "Edit complete.";
	}

	private void replace(ABTable table, String field, String regex,
			String input, ArrayList<LinkedHashMap<String, Object>> increment) {
		for (LinkedHashMap<String, Object> ls : increment) {
			String tmid = (String) ls.get("TMID");
			Set<String> keys = ls.keySet();
			String ps = null;
			String value = ls.get(field).toString();
			if (value != null) {
				value = value.trim();
				if (value.contains(regex)) {
					ps = value.replaceAll(regex, input);
					if (ps != null) {
						ps = ps.trim();
						if (ps.length() > 0)
							table.set(tmid, field, ps.trim(), false);
						else {
							GB.print("no value for : " + field);
						}
					}
				}
			}
		}
	}

	private void update(ABTable table, String field, String input,
			ArrayList<LinkedHashMap<String, Object>> increment) {
		for (LinkedHashMap<String, Object> ls : increment) {
			String tmid = (String) ls.get("TMID");
			Set<String> keys = ls.keySet();
			String ps = input;
			for (String key : keys) {
				String value = ls.get(key).toString();
				if (value != null) {
					value = value.trim();
				}

				// cld_genes.search(genome_build:hg19 AND
				// vis:true).set(isis_cell_line,cells.search(species:$genome_build
				// AND cell_line:$directory){0-1})
				if (ps.startsWith("$")
						&& ps.matches(".*\\.([A-Za-z0-9_]*)(\\(.*\\))*$")) {

					// TODO... think about this.
				}

				if (ps.matches("\\$[A-Za-z0-9]*\\.replace\\s*\\(.*\\)")) {

					System.out.println("string " + ps);
					System.out.println(" let's replace some strings... ");
				} else {
				}

				// i am not sure what this is for.
				// for (String word : ps.split(" ")) {
				// if (word.startsWith("$")
				// && word.matches(".*\\.([A-Za-z0-9_]*)(\\(.*\\))*$")) {
				// int functionI = word.indexOf('.');
				// if (functionI > 0) {
				// String params = null;
				// int end = word.length();
				// if (word.contains("(")) {
				// end = word.lastIndexOf('(');
				// int param_end = word.lastIndexOf(')');
				// if (param_end > 0) {
				// params = word.substring(end + 1, param_end);
				// if (params != null)
				// params = params.trim();
				// }
				// }
				// String function = word
				// .substring(functionI + 1, end);
				//
				// FieldOperator oop = FieldOperatorFactory
				// .create(function);
				// if (oop != null)
				// value = oop.exec(value, params);
				// else {
				// GB.print("No function found for : " + function);
				// return;
				// }
				// }
				// }
				// }

				ps = ps.replace("$" + key, value);
			}

			// {{ here is where we get smart... or dumb. whichever comes first.
			// }}
			// find the functions and evaluate them...
			// we are going to keep this separate from the above function
			// because we
			// need to

			if (ps.trim().matches(GBRGX.SEARCH_FORMAT_OUTPUT)) {
				String sss = "";
				String word = ps;
				search2 s = new search2();
				GBV<Iterator> var = s.execGBVIn(word.trim(), null);
				GBSearchIterator it = (GBSearchIterator) var.get();
				int MAX = 10;
				while (it.hasNext()) {
					ArrayList<LinkedHashMap<String, Object>> itvalues = it
							.next();
					int count = 0;
					for (LinkedHashMap<String, Object> value : itvalues) {
						if (count > MAX) {
							sss += "...";
							break;
						}
						Set<Map.Entry<String, Object>> entry_set = value
								.entrySet();
						for (Map.Entry<String, Object> e : entry_set) {
							if (e.getKey().equals("TMID")
									|| e.getKey().equalsIgnoreCase(
											"TMID_lastUpdated")
									|| e.getKey().equalsIgnoreCase("_version_")
									|| e.getKey().endsWith("__900807")) {
							} else
								sss += e.getValue() + "\t";
						}
						count++;
					}
				}
				ps = ps.replace(word, sss);

			} else if (ps.trim().matches(GBRGX.FUNCTION)) {
				String sss = "";
				String word = ps;

				// IN the event of a function call... like:
				// fpkm_ct.search(gene:pten AND isis_cell_line:[* TO
				// *]).set(ct,mean(primer_probes.search(GENE:$gene AND
				// CELL_LINE:"$isis_cell_line")[CT]))
				// you can see that there may be a situation where the function
				// is trying operate on 0 values from the search..
				// so in this case we will skip anything that doesn't have a
				// value.
				// ---search fature---> then...
				// .set(ct,mean(primer_probes.search(GENE:$gene AND
				// CELL_LINE:"$isis_cell_line")[CT]))

				// complete example.
				// fpkm_ct.search(isis_cell_line:"HepG2").set(ct,mean(primer_probes.search(GENE:$gene
				// AND CELL_LINE:"$isis_cell_line")[CT]))

				// AB>
				// fpkm_ct.search(isis_cell_line:HepG2).set(ct,mean(primer_probes.search(GENE:gene$
				// AND CELL_LINE:"$isis_cell_line")[CT]))

				GBCommand _c_ = GB.getCommands();
				GBPlugin plug = _c_.getPlugin(ps.trim());
				GB.print("Function plugin : " + ps + " plug "
						+ plug.getClass().toString());
				try {
					GBV ivar = plug.execGBVIn(ps.trim(), null);
					if (ivar == null)
						ps = null;
					else if (ivar instanceof FloatVar) {
						FloatVar fv = (FloatVar) ivar;
						Float flv = fv.get();
						ps = flv.toString();
					} else
						ps = null;
				} catch (UsageException us) {
					us.printStackTrace();
				}
			} else {
				for (String word : ps.split(" ")) {
					String sss = "";
					// it_cell_lines.search(*).set(name,cells.search(isis_cell_line:2.0)[isis_cell_line])
					if (word.trim().matches(GBRGX.SEARCH_FORMAT_OUTPUT)) {
						// evaluate it and plug it in.
						search2 s = new search2();
						GBV<Iterator> var = s.execGBVIn(word.trim(), null);
						GBSearchIterator it = (GBSearchIterator) var.get();
						int MAX = 10;
						int count = 0;
						while (it.hasNext()) {
							ArrayList<LinkedHashMap<String, Object>> itvalues = it
									.next();
							for (LinkedHashMap<String, Object> value : itvalues) {

								if (count > MAX) {
									sss += "...";
									break;
								}
								Set<Map.Entry<String, Object>> entry_set = value
										.entrySet();
								for (Map.Entry<String, Object> e : entry_set) {
									if (e.getKey().equals("TMID")
											|| e.getKey().equalsIgnoreCase(
													"TMID_lastUpdated")
											|| e.getKey().equalsIgnoreCase(
													"_version_")
											|| e.getKey().endsWith("__900807")) {
									} else
										sss += e.getValue() + "\t";
								}
								count++;
							}
						}
						ps = ps.replace(word, sss);
					}

				}
			}

			if (ps != null) {
				ps = ps.trim();
				if (ps.length() > 0)
					table.set(tmid, field, ps.trim(), false);
				else {
					GB.print("no value for : " + field);
				}
			}
		}
	}

	private String operate(ArrayList<FieldOperator> operators, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getInputString(String[] sf) {
		String input = "";
		int index = 0;

		for (String t : sf) {
			t = t.trim();
			if (index > 0) {
				// if (t.contains("[") && t.contains("]")) {
				// if (t.matches("\\s*\\[\\s*"))
				// t = t.replaceAll("\\s*\\[\\s*", "$");
				// else if (t.matches("\\s*\\]")) {
				// t = t.replaceAll("\\s*\\]", "");
				// } else {
				// t = t.replace('[', '$');
				// t = t.replace("]", "");
				// }
				// }
				input += t + " ";
			}
			index++;
		}
		if (input != null)
			return input.trim();
		else
			return "";
	}

	private ArrayList<String> parseInputField(String input) {
		// TODO Auto-generated method stub
		return null;
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
				GB.print("\t\tHits " + count + ". ");
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

	public static void main(String[] args) {

		String test = "http://thisisatest.com?f[i][anothersomething]eld[mylink][lfield]andth[field1]enanother&[field2]";
		search2 s = new search2();
		ArrayList<String> cols = getColumns(test);
		for (String c : cols) {
			System.out.println(c);
		}

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

	private ArrayList<String> process(String post_string) {
		return null;
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
		String path = target;
		if (!path.startsWith("/"))
			path = GB.pwd() + "/" + target;

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
