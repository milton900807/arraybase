package com.arraybase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arraybase.db.util.SourceType;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.tree.TNode;

public class GBNodeSearchIterator implements
		Iterator<ArrayList<LinkedHashMap<String, Object>>> {

	private String path = null;
	private String searchString = null;
	private String sortString = null;
	private String[] cols = null;
	private SearchConfig config = null;
	private int increment = 10000;
	private GBSearchIterator maintable = null;
	private String configured_search_string = null;
	private String[] configured_columns = null;
	private Map<String, String[]> column_map;
	private Map<String, Map<String, String>> meta_map = null;
	private LinkedHashMap<String, ABaseResults> cache = new LinkedHashMap<String, ABaseResults>();
	private int start = 0;
	private int end = Integer.MAX_VALUE;
	private int count = 0;

	protected GBNodeSearchIterator(String _path, String searchString,
			String sortString, String[] _cols, int start, int end, int increment, SearchConfig config) {
		this.path = _path;
		this.start = start;
		this.searchString = searchString;
		this.sortString = sortString;
		this.cols = _cols;
		this.config = config;
		this.increment = increment;
		this.end = end;
		
		if ( this.increment > (this.end-this.start))
			this.increment = (this.end-this.start);

		
		registerMainSearchIterator();
	}
	protected GBNodeSearchIterator(String _path, String searchString,
			String sortString, String[] _cols, SearchConfig config) {
		this (_path, searchString, sortString, _cols, 0, Integer.MAX_VALUE, 10000, config);
	}

	public boolean hasNext() {
		return maintable.hasNext();
	}

	public ArrayList<LinkedHashMap<String, Object>> next() {
		ArrayList<LinkedHashMap<String, Object>> b = maintable.next();
		ArrayList<LinkedHashMap<String, Object>> cmap = new ArrayList<LinkedHashMap<String, Object>>();

		cmap = buildmap(b);
		// debug
		 GB.print(cmap);

		ArrayList<LinkedHashMap<String, Object>> cmap2 = new ArrayList<LinkedHashMap<String, Object>>();
		for (LinkedHashMap<String, Object> row : cmap) {
			Set<String> _row_fields = row.keySet();
			LinkedHashMap<String, Object> conserved = new LinkedHashMap<String, Object>();
			for (String row_field : _row_fields) {
				if (row_field.indexOf('$') > 0) {
					Object _d = row.get(row_field);
					LinkedHashMap<String, Object> cop = copyConserved(row);
					cop.put(convert(row_field), _d);
					cmap2.add(cop);
				} else {
					conserved.put(row_field, row.get(row_field));
				}
			}
			cmap2.add(conserved);
		}
		return cmap2;
	}

	private LinkedHashMap<String, Object> copyConserved(
			LinkedHashMap<String, Object> row) {
		LinkedHashMap<String, Object> conser = new LinkedHashMap<String, Object>();
		Set<String> ks = row.keySet();
		for (String k : ks) {
			if (k.indexOf('$') > 0) {

			} else {
				conser.put(k, row.get(k));
			}
		}
		return conser;
	}

	private String convert(String field) {
		int ind = field.lastIndexOf('$');
		if (ind > 0) {
			String sub = field.substring(0, ind);
			return sub.trim();
		}
		return field;
	}

	private ArrayList<LinkedHashMap<String, Object>> buildmap(
			ArrayList<LinkedHashMap<String, Object>> b) {

		ArrayList<LinkedHashMap<String, Object>> cmap = new ArrayList<LinkedHashMap<String, Object>>();
		for (LinkedHashMap<String, Object> bb : b) {
			LinkedHashMap<String, Object> c = new LinkedHashMap<String, Object>();
			Set<String> abc = bb.keySet();
			for (String s : abc) {
				if (s.equalsIgnoreCase("f1") || s.equalsIgnoreCase("f-1")) {
					// System.out.println("?");
				} else {
					
					ArrayList<String> mapv = getKey(s);
					// create the row
					for (String mapv_column : mapv) {
						Object jb = bb.get(s);
						String annotation_value = null;

						if (mapv_column.indexOf("{") > 0) {
							try {
								annotation_value = "";
								String[] col = getPrintField(mapv_column);
								if (col != null) {
									for (String cc : col) {
										if (annotation_value.length() <= 0)
											annotation_value = getAnnotationValue(
													cc, s);
										else
											annotation_value += "|| "
													+ getAnnotationValue(cc, s);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						if (jb != null) {

							// THIS IS WHERE WE ARE GOING TO ATTACH THE META
							Object is_there = c.get(mapv_column);
							if (is_there != null) {
								String increment = increment(mapv_column,
										c.keySet());
								if (annotation_value != null)
									c.put(increment, jb.toString() + "|| "
											+ annotation_value);
								else
									c.put(increment, jb);

							} else {
								if (annotation_value != null)
									c.put(mapv_column, jb.toString() + "|| "
											+ annotation_value);
								else
									c.put(mapv_column, jb);
							}
						}
					}
				}
				//
				//
				//
			}
			cmap.add(c);
		}
		return cmap;
	}

	private String getAnnotationValue(String col, String s) throws Exception {
		// / this is where we need to optimize !!!!!!
		if (s.startsWith("f")) {
			s = s.substring(1);
			s = s.trim();
		}
		// need to load the columns table into memory
		ABaseResults res = cache.get(col);
		if (res == null)
			res = buildCache(col);

		ArrayList<GRow> rows = res.getValues();
		for (GRow g : rows) {
			HashMap map = g.getData();
			Object va = map.get("field");
			if (va != null) {
				String gg = va.toString();
				if (gg.equalsIgnoreCase(s)) {
					Object vb = map.get(col);
					if (vb != null)
						return vb.toString();
				}
			}
		}
		return "????????????";
	}

	private ABaseResults buildCache(String col) throws NodeNotFoundException {
		GBSearch search = GB.getSearch();
		String[] fields_mapper_column = new String[2];
		fields_mapper_column[0] = "field";
		fields_mapper_column[1] = col;
		GBNodes nodes = GB.getNodes();
		TNode main = nodes.getNode(path);
		List<Integer> chs = main.getReference();
		for (int i : chs) {
			TNode n = nodes.getNode(i);
			if (n != null
					&& n.getNodeType().equalsIgnoreCase(
							SourceType.COLUMN_METATABLE.name)) {
				ABaseResults results = GBSearch.select(path + "/" + n.getName(),
						null, "*:*", 0, 10000, null);
				if (results != null && results.getValues().size() > 0) {
					cache.put(col, results);
				}
			}
		}
		return cache.get(col);
	}

	private String[] getPrintField(String c) throws Exception {
		String column = c;
		ArrayList<String> fiel = new ArrayList<String>();
		int st = column.indexOf('{');
		while (st >= 0) {
			int end = column.indexOf('}');
			String col = column.substring(st + 1, end);
			fiel.add(col);
			column = column.substring(end + 1);
			st = column.indexOf('{');
		}
		return fiel.toArray(new String[fiel.size()]);
	}

	private String increment(String v, Set<String> c) {
		for (String l : c) {
			if (l.equalsIgnoreCase(v)) {
				String vv = nextIncr(l);
				return increment(vv, c);
			}
		}
		return v;
	}

	public String nextIncr(String v) {
		if (v.indexOf("$") > 0) {
			int index = v.lastIndexOf('$');
			String pref = v.substring(0, index);
			String incr = v.substring(index + 1);
			if (incr != null && incr.length() > 0) {
				try {
					int value = Integer.parseInt(incr);
					value++;
					return pref + "$" + value;
				} catch (Exception _e) {
				}
				return v + "$" + 1;
			}
			return v + 1;
		} else
			return v + "$" + 1;
	}

	private ArrayList<String> getKey(String s) {
		ArrayList<String> f = new ArrayList<String>();
		Set<String> keys = column_map.keySet();
		for (String key : keys) {
			String[] obj = column_map.get(key);
			for (String ss : obj) {
				if (ss.equalsIgnoreCase(s)) {
					f.add(key);
				}
			}
		}
		return f;
	}

	public void remove() {
		maintable.remove();
	}

	
	private void registerMainSearchIterator() {
		GBNodes nodes = GB.getNodes();
		TNode main = nodes.getNode(path);
		List<Integer> chs = main.getReference();
		for (int i : chs) {
			TNode n = nodes.getNode(i);
			if (n != null
					&& n.getNodeType().equalsIgnoreCase(
							SourceType.VALUE_TABLE.name)) {
				maintable = new GBSearchIterator(path + "/" + n.getName(),
						configured_search_string, sortString,
						configured_columns, config, start, end, increment);
				maintable.setColumns(configured_columns);
			}
		}

	}
}
