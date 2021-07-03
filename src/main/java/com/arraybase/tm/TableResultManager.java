package com.arraybase.tm;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocSlice;
import org.apache.solr.search.SolrIndexSearcher;

public class TableResultManager {

	private SolrIndexSearcher search = null;
	private DocSlice list = null;
	private String where_field = null;
	private String where_value = null;

	
	/**
	 * @param _search
	 * @param _list
	 * @param _where_field
	 * @param _where_value
	 */
	public TableResultManager(SolrIndexSearcher _search, DocSlice _list,
			String _where_field, String _where_value) {
		search = _search;
		list = _list;
		where_value = _where_value;
		where_field = _where_field;
	}

	public SolrIndexSearcher getIndexer() {
		return search;
	}

	public DocSlice getDocSlice() {
		return list;
	}

	public DocList subset(int j, int increment) {
		return list.subset(j, increment);

	}

	public DocIterator getDocIterator() {
		return list.iterator();
	}

	public Document getDocument(int docuId) throws IOException {
		return search.doc(docuId);
	}

	public int size() {
		return list.size();
	}

	public String getWhereField() {
		return where_field;
	}

	public String getWhereValue() {
		return where_value;
	}

}
