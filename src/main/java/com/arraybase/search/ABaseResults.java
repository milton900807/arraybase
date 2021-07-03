package com.arraybase.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;

public interface ABaseResults {

	/**
	 * Add a field to the results
	 * 
	 * @param type
	 *            : The type of field this is (i.e. int, string etc. )
	 * @param name2
	 *            : The field name
	 */
	void addField(String type, String fieldName);

	void setTotalHits(int size);

	void setSuccess(boolean b);

	void setValues(ArrayList resultList);

	String getAction();

	String hits();

	int getTotalHits();

	ArrayList<GColumn> getColumns();

	ArrayList<GRow> getValues();

	LinkedHashMap<String, LinkedHashMap<String, Integer>> getFacet();

}
