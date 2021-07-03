package com.arraybase.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import com.arraybase.tm.GResults;
import com.arraybase.tm.GRow;
import com.arraybase.util.GBLogger;

/**
 * The solr server query response.
 * 
 * @author donaldm
 * 
 */
public class GBResponse {

	private QueryResponse response = null;
	private GBLogger log = GBLogger.getLogger(GBResponse.class);
	private int start = 0;
	private int rows = 0;
	private int hitcount = 0;
	private String target = "unknown";

	public GBResponse(QueryResponse _response, int _start, int _rows) {
		response = _response;
		start = _start;
		rows = _rows;
	}

	long getHitCount() {
		SolrDocumentList list = response.getResults();
		NamedList tresponse = response.getResponse();
		org.apache.solr.common.SolrDocumentList response_object = (SolrDocumentList) tresponse
				.get("response");
		return response_object.getNumFound();
	}

	public LinkedHashMap<String, LinkedHashMap<String, Integer>> getFacetResults() {
		int numfound = 0;
		List<FacetField> fields = response.getFacetFields();
		int increment = rows - start;
		int index = 0;

		// crazy facet hashmap
		LinkedHashMap<String, LinkedHashMap<String, Integer>> f_results = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
		for (FacetField f : fields) {
			index = 0;
			String field_name = f.getName();
			// int field_count = f.getValueCount();
			// log.debug(" name : " + field_name + " count : " +
			// field_count);
			List<Count> counts = f.getValues();
			if (counts != null) {
				LinkedHashMap<String, Integer> facet_query = new LinkedHashMap<String, Integer>();
				for (Count c : counts) {
					if (index < increment) {
						String name = c.getName();
						Long count = c.getCount();
						facet_query.put(name, count.intValue());
					}
					index++;
					f_results.put(field_name, facet_query);
				}
			}
		}
		numfound = index;
		hitcount = numfound;
		return f_results;
	}

	public ABaseResults buildResults(ABaseResults r) {
		try {
			ArrayList resultList = new ArrayList();

			SolrDocumentList l = response.getResults();
			long count = l.getNumFound();

			log.setLevel(GBLogger.DEBUG);
//			log.debug("Schema : " + _schema);
			log.debug("Schema : " + r.getTotalHits());

			// we need to determine the fields.
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < l.size(); i++) {
				SolrDocument sd = l.get(i);
				GRow row = new GRow();
				Collection<String> field_names = sd.getFieldNames();
				for (String f : field_names) {
					Object value = sd.getFieldValue(f);
					// map.put(f, value);
					row.add(f, value);
				}
				resultList.add(row);
			}
			// r.addField(type.getName(), fields[i].getName());
			r.setTotalHits(response.getResults().size());

			r.setSuccess(true);
			r.setValues(resultList);
			return r;
		} catch (Exception _e) {
			_e.printStackTrace();
		}

		log.info("Failed to bulid the results.. returning null results");
		return null;
	}

	public int hitCount() {
		return hitcount;
	}

	public GResults getResults() {
		GResults res = new GResults();
		buildResults(res);
		return res;
	}
}
