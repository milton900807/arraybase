package com.arraybase;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.util.ABProperties;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class GBSearchIterator implements
		Iterator<ArrayList<LinkedHashMap<String, Object>>> {
	private String site = ABProperties.getSolrURL();
	private DBConnectionManager dbcm = new DBConnectionManager();
	private GBNodes nodes = null;
	private String core = null;
	int current = 0;
	int total = -1;
	private String searchString = null;
	private int increment = 1000000;
	private String[] cols = null;
	private String sortString = null;
	private SearchConfig mode = new SearchConfig(SearchConfig.RAW_SEARCH);
	private SolrClient server = null;
	private ArrayList<GColumn> desc = null;
	HttpClient client = new DefaultHttpClient();
	private String path = null;
	private int start = 0;
	private int end = Integer.MAX_VALUE;
	
	

	public GBSearchIterator(String _path, String searchString,
			String sortString, String[] _cols, SearchConfig config, int start, int end,
			int increment) {
		path = _path;
		nodes = new GBNodes(dbcm);
		mode = config;
		this.sortString = sortString;
		this.searchString = searchString;
		this.increment = increment;
		core = nodes.getCore(_path);

		if (GBLinkManager.isFullyQualifiedURL(core)) {
			site = GBLinkManager.getSolrRoot(core);
			core = GBLinkManager.getCoreLK(core);
		}
		try {
			desc = TMSolrServer.describeCore(site, core);
		} catch (ConnectException e) {
			e.printStackTrace();
		}
		if (_cols == null) {
			setColumns(desc);
		} else
			cols = _cols;
//		server = new HttpSolrClient(site + core, client);
		SolrClient solr = new HttpSolrClient.Builder(site + core).build();
		server = solr;
// 		server = new HttpSolrClient( new HttpSolrClient.Builder(site ));
		ABaseResults results = GBSearch.search(server, desc, core,
				searchString, 0, 1, sortString, cols, mode);
		total = results.getTotalHits();
		current = start;
		this.start = start;
		this.end = end;
		// make sure the end is not more than the total.
		if ( this.end > total )
			this.end = total;
		// make sure the increment is not too big!
		if ( this.increment > (this.end-this.start))
			this.increment = (this.end-this.start);
	}
	public GBSearchIterator(String _path, String searchString,
			String sortString, String[] _cols, SearchConfig config,
			int increment) {
		this (_path, searchString, sortString, _cols, config, 0, Integer.MAX_VALUE, increment);
	}
	
	public String getPath() {
		return path;
	}

	private void setColumns(ArrayList<GColumn> desc) {
		String[] cc = new String[desc.size()];
		int index = 0;
		for (GColumn g : desc) {
			cc[index++] = g.getName();
		}
		cols = cc;
	}

	public boolean hasNext() {
		return current < end;
	}

	public int getTotal() {
		if (GBLinkManager.isFullyQualifiedURL(core)) {
			site = GBLinkManager.getSolrRoot(core);
			core = GBLinkManager.getCoreLK(core);
		}
		ABaseResults results = GBSearch.search(server, desc, core,
				searchString, 0, increment, sortString, cols, mode);
		if (results != null) {
			return results.getTotalHits();
		}
		return -1;
	}

	public void reset() {
		current = 0;
	}

	public ArrayList<LinkedHashMap<String, Object>> next() {

		if (!hasNext())
			return new ArrayList<LinkedHashMap<String, Object>>();

		if (GBLinkManager.isFullyQualifiedURL(core)) {
			site = GBLinkManager.getSolrRoot(core);
			core = GBLinkManager.getCoreLK(core);
		}
		ABaseResults results = GBSearch.search(server, desc, core,
				searchString, current, increment, sortString, cols, mode);
		if (results == null) {
			return null;
		}
		if (cols == null || cols.length <= 0 ) {
			setColumns(results.getColumns());
		}
		current += increment;
		ArrayList<GRow> rows = results.getValues();
		
//		System.out.println ( " row count : " + rows.size() );
		
		
		ArrayList<LinkedHashMap<String, Object>> value = new ArrayList<LinkedHashMap<String, Object>>();
		for (GRow r : rows) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			Map values = r.getData();
			for (String c : cols) {
				Object ov = values.get(c);
				if (ov != null) {
					map.put(c, ov);
				} else
					map.put(c, "");
			}
			value.add(map);
		}
		return value;
	}

	public void remove() {
		total = -1;
		current = 0;
	}

	public void setColumns(String[] _cols) {
		cols = _cols;
	}

	public void setSearchSort(String _sort) {
		sortString = _sort;
	}

	public String[] getFields() {
		return cols;
	}

	public String getSearchString() {
		return searchString;
	}
}
