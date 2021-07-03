package com.arraybase;

import java.io.PrintStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.GBRelationalDB;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.file.GBFileStructure;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.lac.LAC;
import com.arraybase.db.util.SourceType;
import com.arraybase.search.GBResponse;
import com.arraybase.search.ABaseResults;
import com.arraybase.search.ResultsNotFoundException;
import com.arraybase.search.SearchPathFailedExeption;
import com.arraybase.tab.field.FieldNotFoundException;
import com.arraybase.tm.DefaultWhereClause;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.WhereClause;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;

public class GBSearch {

	private static GBLogger log = GBLogger.getLogger(GBSearch.class);
	private DBConnectionManager dbcm = new DBConnectionManager();

	/**
	 * @deprecated
	 * @param _path
	 * @param _search
	 * @param _stream
	 * @return
	 */
	public ABaseResults searchPath(String _path, String _search,
			PrintStream _stream) {

		SearchConfig mode = new SearchConfig(SearchConfig.RAW_SEARCH);

		if (_search.equals("*:*")) {
			_search = "*";
		}
		if (!_path.endsWith("*")) {
			_path += "*";
		}
		String searchString = "(PATH:" + _path + ") AND (CONTENT:" + _search
				+ " OR AUTHORS:" + _search + " OR DESCRIPTION:" + _search
				+ " OR FILE_NAME:" + _search + " OR TITLE:" + _search + ")";
		int increment = 1000;

		String site = ABProperties.getSolrURL();
		ABaseResults results = TMSolrServer.search(site, "search_index",
				searchString, 0, increment, mode);

		try {
			if (_stream != null) {
				printPathResults(results);
			}
		} catch (ResultsNotFoundException e) {
			e.printStackTrace();
			log.error(site + " search_index " + "" + searchString);

		}
		if (results == null)
			return null;
		int total = results.getTotalHits();
		int index = increment;
		while (index < total) {
			results = TMSolrServer.search(site, "search_index", searchString,
					index, increment, mode);
			try {
				printPathResults(results);
			} catch (ResultsNotFoundException e) {
				e.printStackTrace();
			}
			index += increment;
		}
		log.println("\tHits: " + total);
		return results;
	}

	/**
	 * @deprecated
	 * @param _path
	 * @param _search
	 * @param _start
	 * @param _rows
	 * @param props
	 * @return
	 */
	private GResults searchPath(String _path, String _search, int _start,
			int _rows, Map<String, String> props, SearchConfig mode) {

		// {{ GET THE NODE OBJECT AT THIS PATH }}
		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(_path);
		// TODO: make this a more useful feedback mechanism
		if (node == null) {
			System.err
					.println("Search failed: the path does not seem to be correct.");
			return null;
		}

		String lac = node.getLink();
		String _schema = LAC.getTarget(lac);
		GBSolr.getCoreFromLAC(lac, dbcm);

		String site = ABProperties.getSolrURL();
		return TMSolrServer.search(site, _schema, _search, 0, 1000, mode);
		// return search(site, _search, _start, _rows, props);
	}

	public static ArrayList<GColumn> removeTrackingColumns(
			ArrayList<GColumn> columns) {

		ArrayList<GColumn> gg = new ArrayList<GColumn>();
		for (GColumn g : columns) {
//			if ((!g.getName().equals("TMID"))
//					&& 
					if ((!g.getName().endsWith("__900807"))
					&& (!g.getName().endsWith("_version_"))
					&& (!g.getName().equals("TMID_lastUpdated"))) {
				String name = g.getName();
				// System.out.println(name);
				gg.add(g);
			}
		}
		return gg;
	}

	public void searchAllPaths(String searchString, PrintStream out,
			SearchConfig mode) {

		int increment = 1000;
		String site = ABProperties.getSolrURL();
		ABaseResults results = TMSolrServer.search(site, "search_index",
				searchString, 0, increment, mode);

		try {
			printPathResults(results);
		} catch (ResultsNotFoundException e) {
			e.printStackTrace();
		}
		int total = results.getTotalHits();
		int index = increment;
		while (index < total) {
			results = TMSolrServer.search(site, "search_index", searchString,
					index, increment, mode);
			try {
				printPathResults(results);
			} catch (ResultsNotFoundException e) {
				e.printStackTrace();
			}
			index += increment;
		}
		log.println("\tHits: " + total);
	}

	/**
	 * *
	 * 
	 * @param _path
	 * @param searchString
	 * @throws NodeNotFoundException
	 */
	public static Iterator<ArrayList<LinkedHashMap<String, Object>>> searchAndDeploy(
			String _path, String searchString, String sortString,
			String[] _cols, SearchConfig config)
			throws NotASearchableTableException, NodeNotFoundException {
		// if we are searching a node then we need to get the node search
		// iterator... which is very different than the default
		// gbsearch iterator
		GBNodes nodes = GB.getNodes();
		TNode main = nodes.getNode(_path);
		if (main == null)
			throw new NodeNotFoundException(_path);
		if (main.getNodeType().equalsIgnoreCase(SourceType.NODE.name()))
			return new GBNodeSearchIterator(_path, searchString, sortString,
					_cols, config);
		else
			return new GBSearchIterator(_path, searchString, sortString, _cols,
					config, 10000);
	}
	

	/**
	 * @param _path
	 * @param searchString
	 * @throws NodeNotFoundException
	 */
	public static Iterator<ArrayList<LinkedHashMap<String, Object>>> searchAndDeploy(
			String _path, String searchString, String sortString,
			String[] _cols, int start, int end, SearchConfig config)
			throws NotASearchableTableException, NodeNotFoundException {
		// if we are searching a node then we need to get the node search
		// iterator... which is very different than the default
		// gbsearch iterator
		
		
		
//		GB.updateStats ( _path, "search");
		
		
		GBNodes nodes = GB.getNodes();
		TNode main = nodes.getNode(_path);
		if (main == null)
			throw new NodeNotFoundException(_path);
		
		int increment = 10000;
		if ( increment > (end-start))
			increment = (end-start);
		
		
		if (main.getNodeType().equalsIgnoreCase(SourceType.NODE.name()))
			return new GBNodeSearchIterator(_path, searchString, sortString,
					_cols, start, end, increment, config);
		else{
			return new GBSearchIterator(_path, searchString, sortString, _cols,
					config, start, end, increment);
			
		}
	}
	

	/**
	 * Seares a solr table
	 * 
	 * @param _path
	 * @param searchString
	 * @param out
	 */
	public ABaseResults searchTable(String _path, String searchString,
			String sortString, PrintStream out, String[] _cols,
			SearchConfig mode) throws NotASearchableTableException {
		log.debug(" " + _path + ".search(" + searchString + ")");
		int increment = 10000;
		String site = ABProperties.getSolrURL();

		GBNodes nodes = new GBNodes(dbcm);
		String core = nodes.getCore(_path);

		if (GBLinkManager.isFullyQualifiedURL(core)) {
			site = GBLinkManager.getSolrRoot(core);
			core = GBLinkManager.getCoreLK(core);
		}

		ABaseResults results = searchCore(site, core, searchString, 0,
				increment, sortString, _cols, mode);
		if (results == null) {
			out.println("No results");
			return results;
		}
		if (out == null)
			return results;

		if (out != null)
			GBIO.printResults(results, out, _cols);
		int total = results.getTotalHits();
		int index = increment;
		if (out != null) {
			while (index < total) {
				results = searchCore(site, core, searchString, index,
						increment, sortString, _cols, mode);
				if (out != null)
					GBIO.printResults(results, out, _cols);
				index += increment;
			}
		}
		log.println("\tHits: " + total);
		return results;
	}

	/**
	 * Seares a solr table
	 * 
	 * @param _path
	 * @param searchString
	 * @param out
	 */
	public ABaseResults searchTable(String _path, String searchString,
			PrintStream out, String[] _cols, int start, int increment,
			String sort, SearchConfig config)
			throws NotASearchableTableException {

		String site = ABProperties.getSolrURL();
		String core = TMSolrServer.getCore(_path);

		ABaseResults results = searchCore(site, core, searchString, _cols,
				start, increment, sort, config);
		if (results == null) {
			// out.println("No results");
			// log.print ( "Nor results");
			return null;
		}
		log.debug(" " + _path + ".search(" + searchString + ")" + start + ","
				+ increment + ", total=" + results.getTotalHits());
		// GBIO.printResults(results, out);
		return results;

	}

	/**
	 * Seares a solr table
	 * 
	 * @param _path
	 * @param searchString
	 * @param out
	 */
	public ABaseResults searchTable(TNode thisnode, String searchString,
			PrintStream out, String[] _cols, int start, int increment,
			String sort, SearchConfig config)
			throws NotASearchableTableException {
		String site = ABProperties.getSolrURL();
		String lac = thisnode.getLink();
		String core = GBSolr.getCoreFromLAC(lac, dbcm);
		ABaseResults results = searchCore(site, core, searchString, _cols,
				start, increment, sort, config);
		if (results == null) {
			// log.print ( "Nor results");
			return null;
		}
		// GBIO.printResults(results, out);
		return results;

	}

	private ABaseResults searchCore(String site, String core,
			String searchString, String[] _cols, int start, int increment,
			String sort, SearchConfig mode) {
		ABaseResults results = TMSolrServer.search(site, core, _cols,
				searchString, start, increment, sort, mode);
		return results;
	}

	public ABaseResults searchCore(String _site, String _core,
			String _searchString, int _start, int _incrememnt, String sort,
			SearchConfig mode) {

		if (GBLinkManager.isFullyQualifiedURL(_core)) {
			_site = GBLinkManager.getSolrRoot(_core);
			_core = GBLinkManager.getCoreLK(_core);
		}

		return searchCore(_site, _core, _searchString, _start, _incrememnt,
				sort, null, mode);
	}

	public static ABaseResults searchCore(String _site, String _core,
			String _searchString, int _start, int _increment, String sort,
			String[] cols, SearchConfig mode) {
		ABaseResults results = TMSolrServer.search(_site, _core, _searchString,
				_start, _increment, sort, cols, mode);
		return results;
	}

	private void printResults(boolean _columns, ABaseResults results) {
		ArrayList<GColumn> cols = results.getColumns();
		ArrayList<GRow> rows = results.getValues();

		if (_columns) {
			for (GColumn c : cols) {
				log.debug(c.getName() + "\t");
			}
		}
		log.prinln();
		for (GRow r : rows) {
			Map values = r.getData();
			String content = "";
			for (GColumn c : cols) {
				Object ov = values.get(c.getName());
				if (ov != null) {
					String v = ov.toString();
					content += "\t" + v;
				}
			}
			log.println(content);
		}
	}

	private void printPathResults(ABaseResults results)
			throws ResultsNotFoundException {
		if (results == null) {
			throw new ResultsNotFoundException();
		}
		ArrayList<GRow> rows = results.getValues();
		SortedMap<String, String> map = new TreeMap<String, String>();
		for (GRow r : rows) {
			Map values = r.getData();
			String content = "";
			Object ov = values.get("PATH");
			Object file = values.get(GBFileStructure.FILE_NAME.name);
			if (ov != null) {
				String v = ov.toString();
				map.put(v + "/" + file, v + "/" + file);
				content += "\t" + v + "/" + file;
			}
		}
		Set<String> ss = map.keySet();
		for (String s : ss) {
			log.println(s);
		}
	}

	public void searchCore(String lac, String searchString, int _start,
			int _count, PrintStream out, String[] _cols, SearchConfig mode) {

		int increment = 1000;
		String site = ABProperties.getSolrURL();
		String core = GBSolr.getCoreFromLAC(lac, dbcm);
		if (core == null || core.length() <= 0)
			return;
		log.config("searching lac " + lac);
		log.config("searching core " + core);
		log.config("searching site " + site);
		log.config("\tSearch string" + searchString);
		ABaseResults results = TMSolrServer.search(site, core, searchString, 0,
				increment, mode);
		if (results == null)
			return;

		GBIO.printResults(results, out);

		int total = results.getTotalHits();
		int index = increment;
		while (index < total) {
			results = TMSolrServer.search(site, core, searchString, index,
					increment, mode);
			GBIO.printResults(results, out);
			index += increment;
		}
		log.println("\tHits: " + total);
	}

	public void searchCore(String lac, String searchString, int _start,
			int _count, Properties pr, String[] _cols, SearchConfig mode) {
		GBRelationalDB gbd = new GBRelationalDB(pr);
		int increment = 1000;
		String site = ABProperties.getSolrURL();
		String core = GBSolr.getCoreFromLAC(lac, dbcm);
		if (core == null || core.length() <= 0)
			return;
		log.config("searching lac " + lac);
		log.config("searching core " + core);
		log.config("searching site " + site);
		log.config("\tSearch string" + searchString);
		ABaseResults results = TMSolrServer.search(site, core, searchString, 0,
				increment, mode);
		if (results == null)
			return;
		if (_cols == null || _cols.length <= 0) {
			_cols = extractColOrder(results);
		}
		gbd.printResults(results, _cols);
		int total = results.getTotalHits();
		int index = increment;
		while (index < total) {
			results = TMSolrServer.search(site, core, searchString, index,
					increment, mode);
			gbd.printResults(results, _cols);
			index += increment;
		}
		log.println("\tHits: " + total);
	}

	public static String[] extractColOrder(ABaseResults results) {
		ArrayList<GColumn> col = results.getColumns();
		String[] c = new String[col.size()];
		int index = 0;
		for (GColumn cc : col) {
			c[index++] = cc.getName();
		}
		return c;
	}

	public static Set<String> removeTrackingColumns(Set<String> keys) {
		HashSet<String> sett = new HashSet<String>();
		for (String s : keys) {
			if (isNotTrackingCol(s))
				sett.add(s);
		}
		return sett;
	}

	private static boolean isNotTrackingCol(String g) {
		return (!g.equals("TMID")) && (!g.endsWith("__900807"))
				&& (!g.equals("TMID_lastUpdated"));
	}

	public static String select(String[] _args, PrintStream pmr) {
		ArrayList<String> select_vals = parseSelect(_args);
		int search_path_index = _args.length - 2;
		int search_string_ = _args.length - 1;
		String path = _args[search_path_index];
		String search_string = _args[search_string_];
		String[] columns = new String[select_vals.size()];
		for (int i = 0; i < select_vals.size(); i++) {
			columns[i] = select_vals.get(i);
		}
		try {
			TNode node = GB.getNodes().getNode(path);
			if (node == null) {
				GB.print("Path was not found " + path);
				return "Path not found : " + path;
			}
			GBNodes.searchPath(path, search_string, columns, pmr,
					new SearchConfig(SearchConfig.RAW_SEARCH));
		} catch (NotASearchableTableException e) {
			e.printStackTrace();
		}
		return "Path not found : " + path;
	}

	/**
	 * Return the list of items with given start and stop index
	 * 
	 * @param path
	 * @param search_string
	 * @param pmr
	 * @return
	 * @throws NodeNotFoundException
	 */
	public static ABaseResults select(String path, String[] fields,
			String search_string, int start, int increment, String sort)
			throws NodeNotFoundException {
		try {
			return GBNodes.searchPath(path, search_string, fields, null, start,
					increment, sort, new SearchConfig(SearchConfig.RAW_SEARCH));
		} catch (SearchPathFailedExeption e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return the list of items with given start and stop index
	 * 
	 * @param path
	 * @param search_string
	 * @param pmr
	 * @return
	 * @throws NodeNotFoundException
	 */
	public static ABaseResults select(TNode node, String[] fields,
			String search_string, int start, int increment, String sort)
			throws NodeNotFoundException {
		try {
			return GBNodes.searchPath(node, search_string, fields, null, start,
					increment, sort, new SearchConfig(SearchConfig.RAW_SEARCH));
		} catch (SearchPathFailedExeption e) {
			e.printStackTrace();
		} catch (NotASearchableTableException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Working on parsing the select statement. This should be moved out of the
	 * GB class as soon as it is complete.
	 * 
	 * 
	 * This is an incomplete method and... TODO: complete this by implementing
	 * the select statement. Here ist he select statement that I'm working with
	 * right now: java -jar gb.jar select columns=FPM,TMID from
	 * /isis/test/mytesttable "*:*"
	 * 
	 * I need to get back to the join feature. I'll come back to this.
	 * 
	 * @param _args
	 * @return
	 */
	private static ArrayList<String> parseSelect(String[] _args) {
		boolean cols = false;
		ArrayList<String> sf = new ArrayList<String>();
		for (String s : _args) {
			s = s.trim();
			if (s.equals("select")) {
				// next will be the columns
				cols = true;
			} else if (cols) {
				// System.out.println(" col " + s);
				sf.add(s);
				// // parse the columns
				// if (s.endsWith(",")) {
				// s = strip(s, ",");
				// sf.add(s);
				// } else {
				// cols = false;
				// sf.add(s);
				// }
			}
		}
		sf.remove(sf.size() - 1);
		sf.remove(sf.size() - 1);
		// for (String s : sf) {
		// System.out.println(s);
		// }
		return sf;
	}

	public static ArrayList<WhereClause> buildWhere(ArrayList<String> wherec) {
		ArrayList<Integer> remove_index = new ArrayList<Integer>();
		int index = 0;
		for (String s : wherec) {
			if (s.equalsIgnoreCase("WHERE")) {
				remove_index.add(index);
			}
			index++;
		}
		// prune it.
		for (int removeIndex : remove_index) {
			wherec.remove(removeIndex);
		}

		// TODO: work on this a bit. We need to make this a recursive method to
		// handle the
		// recursive nature of the where clauses.
		// at the moment this just handles a linear list of where clauses;
		ArrayList<WhereClause> wc = new ArrayList<WhereClause>();
		for (int i = 0; i < wherec.size(); i += 4) {
			String i1 = wherec.get(i);
			String i2 = wherec.get(i + 1);
			String i3 = wherec.get(i + 2);

			DefaultWhereClause w = new DefaultWhereClause(i1, i2, i3);
			if ((i + 3) < wherec.size()) {
				w.setJoinExpression(wherec.get(i + 3));
			}
			wc.add(w);
		}
		return wc;
	}

	/**
	 * Search
	 * 
	 * @param _args
	 * @param _print
	 */
	public static void search(String[] _args, PrintStream _print,
			SearchConfig mode) {
		if (_args.length > 4 || _args.length < 2) {
			GB.printUsage("gb search $path $searchstring --> to Search within a path");
			GB.printUsage(" OR ");
			GB.printUsage("gb search $searchstring --> to search all");
			GB.printUsage("EXAMPLE ");
			GB.printUsage("gb search /gne/research/test_table print_columns=column1,column2");
			return;
		}
		if (_args.length == 2) {
			log.debug("searching all...");// not sure this feature is ever
											// used... should prob deprecate
			searchAll(_args[1], mode);
		} else {
			String path = _args[1];
			String search_string = _args[2];
			String cols = null;
			String[] columns = null;
			if (_args.length == 4) {
				cols = _args[3];
				columns = GBUtil.parse("print_columns", cols);
			}
			try {
				GBNodes.searchPath(path, search_string, columns, _print, mode);
			} catch (NotASearchableTableException e) {
				e.printStackTrace();
				GB.printUsage(GB.SEARCH);
			}
		}
	}

	public static ABaseResults search(String path, String search_string,
			PrintStream _print, SearchConfig mode) {
		String cols = null;
		String[] columns = null;
		try {
			return GBNodes.searchPath(path, search_string, columns, _print,
					mode);
		} catch (NotASearchableTableException e) {
			e.printStackTrace();
			GB.printUsage(GB.SEARCH);
		}
		return null;
	}

	/**
	 * not sure about this method. I think the one above is a better approach
	 * 
	 * @param url
	 * @param _searchString
	 * @param _start
	 * @param _rows
	 * @return
	 */
	public static GBResponse search(String url, String _searchString,
			int _start, int _rows) {
		// TMSolrServer.search(url, _schema, _searchString, _start, _rows)
		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(url).build();
			if (_searchString == null || _searchString.length() <= 0)
				_searchString = "*:*";

			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + _searchString);
			params.set("start", _start);
			params.set("rows", _rows);
			params.set("sort", "TMID_lastUpdated desc");
			params.set("wt", "xml");
			// params.set("facet", true);
			// params.set("facet.field", "location_exact",
			// "organ_exact", "type_exact", "disease_exact");
			//
			// params.set("facet.mincount", 1);
			XMLResponseParser pars = new XMLResponseParser();
			solr.setParser(pars);
			// log.info("Loading the XML parser"
			// + params.getParameterNames().toString());
			QueryResponse response = solr.query(params);
			GBResponse r = new GBResponse(response, _start, _rows);
			return r;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return null;
	}

	private static void searchAll(String searchString, SearchConfig mode) {
		GBSearch se = GB.getSearch();
		PrintStream out = System.out;
		se.searchAllPaths(searchString, out, mode);
	}

	public static void searchTable(String[] _args, SearchConfig mode) {
		GBSearch search = GB.getSearch();
		try {
			String sortString = null;
			if (_args.length == 4) {
				sortString = _args[3];
			}

			search.searchTable(_args[1], _args[2], sortString, System.out,
					null, mode);
		} catch (NotASearchableTableException e) {
			GB.print(" Are you sure this is a valid path to a table?");
			GB.print(" path:\t " + _args[1]);
			e.printStackTrace();
		}
	}

	/**
	 * Currently only works on a single table
	 * 
	 * @param path
	 * @param searchString
	 * @return
	 */
	public static LinkedHashMap<String, Long> facet(String path,
			String searchString, String facet_field) {
		String site = ABProperties.getSolrURL();
		GBNodes nodes = GB.getNodes();
		String schema = nodes.getCore(path);
		return TMSolrServer.facets(site, schema, searchString, 0, 1000, null,
				facet_field, new SearchConfig(SearchConfig.RAW_SEARCH));
	}

	
	public static void main(String[] args){
		LinkedHashMap<String, Integer> hits = hitCount("/isis/search/quick/oligos", "301012",
                new SearchConfig(SearchConfig.NODE_CONFIG));
		if ( hits != null )
		{
			
			Set<String> s = hits.keySet();
			for ( String set : s ){
				Integer i = hits.get ( set );
				System.out.println( s + " == > " + i );
			}
			
		}else
		{
			System.out.println ( " No hits found . ");
		}
	}
	
	
	public static LinkedHashMap<String, Integer> hitCount(String path,
			String search_string, SearchConfig mode) {
		int increment = 1;
		LinkedHashMap<String, Integer> ls = new LinkedHashMap<String, Integer>();
		String site = ABProperties.getSolrURL();
		GBNodes nodes = GB.getNodes();
		List<TNode> ch = nodes.getReferenceNodes(path);
		if (ch != null && ch.size() > 0) {
			for (TNode node : ch) {
				if (node != null) {
					updateCounts(node, mode, nodes, ls, path, search_string,
							increment, site);
				}
			}
		} else {
			TNode node_____ = nodes.getNode(path);
			if (node_____ != null) {
				String parentPath = GBPathUtils.getParent(path);
				updateCounts(node_____, mode, nodes, ls, parentPath,
						search_string, increment, site);
			}
		}
		return ls;
	}

	private static void updateCounts(TNode node, SearchConfig mode,
			GBNodes nodes, LinkedHashMap<String, Integer> ls, String path,
			String search_string, int increment, String site) {
		if (mode != null) {
			Map<String, String> props = NodeManager.getNodePropertyMap(node
					.getNode_id());
			mode.setConfigProperties(props);
		} else {
			mode = new SearchConfig(SearchConfig.RAW_SEARCH);
		}
		if (isSearchable(node)) {
			String core = nodes.getCore(path + "/" + node.getName());
			// System.out.println("core : " + core + "\t" +
			// node.getName()
			// + " \t " + path);
			ABaseResults results = TMSolrServer.search(site, core,
					search_string, 0, increment, mode);
			if (results == null)
				GB.print(" error .. no results " + search_string);

			String description = node.getDescription();

			if (results != null) {
				int total = results.getTotalHits();
				if (description != null && description.length() > 0)
					ls.put(node.getName() + ":" + description, total);
				else
					ls.put(node.getName(), total);

			} else
				ls.put(node.getName(), -1);
		}

	}

	public static boolean isSearchable(TNode node) {
		return node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
				|| node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)
				|| node.getNodeType().equalsIgnoreCase(
				SourceType.COLUMN_METATABLE.name)
				|| node.getNodeType().equalsIgnoreCase(
				SourceType.VALUE_TABLE.name);
	}

	public static void select(String path, String search, PrintStream out,
			ArrayList<String> _cols, SearchConfig mode) {
		int increment = 1000;
		String site = ABProperties.getSolrURL();
		GBNodes nodes = GB.getNodes();
		String core = nodes.getCore(path);
		ABaseResults results = TMSolrServer.search(site, core, search, 0,
				increment, mode);
		if (results == null)
			return;

		print(results, out, _cols);

		int total = results.getTotalHits();
		int index = increment;
		while (index < total) {
			results = TMSolrServer.search(site, core, search, index, increment,
					mode);
			print(results, out, _cols);
			index += increment;
		}
		log.println("\tHits: " + total);
	}

	private static void print(ABaseResults results, PrintStream out,
			ArrayList<String> _cols) {
		ArrayList<GRow> rows = results.getValues();

		String cols = "";
		for (String s : _cols) {
			cols += "[" + s + "]\t";
		}
		GB.print(cols);
		GB.print("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _");
		for (GRow r : rows) {
			HashMap data = r.getData();
			if (data != null) {
				String line = "";
				for (String col : _cols) {
					line += data.get(col) + "\t";
				}
				GB.print(line);
			}
		}
	}

	public void searchTable(String path, String searchString,
			String sortString, PrintStream out, int start, int increment, SearchConfig mode) {
		String site = ABProperties.getSolrURL();
		GBNodes nodes = new GBNodes(dbcm);
		String core = nodes.getCore(path);
		Map<String, String> node_props = GB.getNodeProps(path);
		if (node_props != null)
			mode.setConfigProperties(node_props);

		if (GBLinkManager.isFullyQualifiedURL(core)) {
			site = GBLinkManager.getSolrRoot(core);
			core = GBLinkManager.getCoreLK(core);
		}

		ABaseResults results = searchCore(site, core, searchString, start,
				increment, sortString, null, mode);
		if (results == null) {
			out.println("No results");
			return;
		}

		ArrayList<GColumn> columns = results.getColumns();
		String[] _cols = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			_cols[i] = columns.get(i).getName();
		}
		GBIO.printResults(results, out);
		int total = results.getTotalHits();
		int index = increment;
		while (index < total) {
			results = searchCore(site, core, searchString, index, increment,
					sortString, null, mode);
			GBIO.printResults(results, out);
			index += increment;
		}
		GB.print("\t\tHits: " + total);
	}

	public void printDistinct(String path, String searchString, String field) throws ConnectException, FieldNotFoundException {
		String server = ABProperties.getSolrURL();
		String core = TMSolrServer.getCore(path);
		this.distinct(server, core, searchString, field);
	}

	private void distinct(String server, String schema, String search_string,
			String field) throws ConnectException, FieldNotFoundException {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(field);
		ABaseResults results = TMSolrServer.distinct(server, schema,
				search_string, fields, 0, 10000);
		GBIO.printFacets(results, System.out, "\t");
		GB.print("Hits: " + results.getTotalHits());
	}

	public ABaseResults getDistinct(String path, String searchString,
			String field) throws ConnectException, FieldNotFoundException {
		String server = ABProperties.getSolrURL();
		String core = TMSolrServer.getCore(path);
		return this.getDistinct(server, core, searchString, field);
	}

	private ABaseResults getDistinct(String server, String schema,
			String search_string, String field) throws ConnectException, FieldNotFoundException {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(field);
		ABaseResults results = TMSolrServer.distinct(server, schema,
				search_string, fields, 0, 10000);
		return results;
	}

	public static ABaseResults search(SolrClient server,
			ArrayList<GColumn> desc, String core, String searchString,
			int _start, int _rows, String _sort, String[] cols,
			SearchConfig mode) {
		return TMSolrServer.search(server, desc, core, searchString, _start,
				_rows, _sort, cols, mode);
	}


	public static ABaseResults search(HttpSolrClient server,String searchString,
			int _start, int _rows, String _sort, String[] cols,
			SearchConfig mode) {
		
		String core= null;
		String lastString = server.getBaseURL();
		if ( lastString.endsWith("/"))
			lastString = lastString.substring(0, lastString.length()-1);
		int ind = lastString.indexOf('/');
		if ( ind > 0 ){
			core = lastString.substring(ind+1);
		}
		return TMSolrServer.search(server, searchString, _start,
				_rows, _sort, cols, mode);
	}

}
