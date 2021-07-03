package com.arraybase;

import com.arraybase.flare.TMSolrServer;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.MergeResultRow;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import com.arraybase.util.Level;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;

import java.util.*;

public class GBBuilders {

	private static GBLogger log = GBLogger.getLogger(GBBuilders.class);

	/**
	 * TODO: this methid is currently not complete... need to work on this.
	 * 
	 * 
	 * 
	 * imp.distinctBuild("milton", "milton_Repository_t6", "*:*",
	 * "TISSUE_DIAGNOSIS", 0, 10000, "testme");
	 * 
	 * @param _user_id
	 * @param _fromURL
	 * @param _searchString
	 * @param _field
	 * @param _start
	 * @param _rows
	 * @param _toURL
	 * @return
	 */
	public String distinctBuild(String _user_id, String _fromURL,
			String _searchString, String _field, int _start, int _rows,
			String _toURL) {
		HttpSolrClient solr = null;
		try {
			if (_field.endsWith("__900807"))
				_field = _field.substring(0, _field.length() - 8);
			log.setLevel(Level.DEBUG);

			String lac = createTable(_toURL, _user_id);
			log.info("_searchString: " + _searchString);
			_searchString = _searchString.replaceAll(" and ", " AND ");
			_searchString = _searchString.replaceAll(" not ", " NOT ");
			_searchString = _searchString.replaceAll(" or ", " OR ");

			if (_searchString == null || _searchString.length() <= 0)
				_searchString = "*:*";
			int b_start = _start;
			int increment = 50000;

			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			ArrayList<GColumn> _schema_cols = TMSolrServer.describeCore(
					solr_url, _fromURL);
			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + _searchString);
			// params.set("start", b_start);
			// params.set("rows", increment);
			// params.set("sort", _field + " desc");
			params.set("wt", "xml");

			XMLResponseParser pars = new XMLResponseParser();
			// HttpClient client = new HttpClient ();
			solr = new HttpSolrClient.Builder(_fromURL).build();
			solr.setParser(pars);
			QueryResponse response = solr.query(params);
			SolrDocumentList list = response.getResults();
			NamedList tresponse = response.getResponse();
			SolrDocumentList response_object = (SolrDocumentList) tresponse
					.get("response");
			int numfound = (int) response_object.getNumFound();
			while (b_start < numfound) {
				log.debug(" b_start:  " + b_start);
				params = new ModifiableSolrParams();
				params.set("q", "" + _searchString);
				params.set("start", b_start);
				params.set("rows", increment);
				params.set("sort", _field + " desc");
				params.set("wt", "xml");
				pars = new XMLResponseParser();
				solr.setParser(pars);
				response = solr.query(params);
				list = response.getResults();
				b_start += increment;

				Object point_object = null;
				Iterator<SolrDocument> it = list.iterator();
				ArrayList<SolrDocument> point_docs = new ArrayList<SolrDocument>();
				ArrayList<GRow> cached = new ArrayList<GRow>();
				int index = 0;
				String type_object = "string";
				while (it.hasNext()) {
					SolrDocument doc = it.next();
					Object v = doc.getFieldValue(_field);
					if (point_object == null) {
						point_object = v;
						point_docs.add(doc);
					} else if (equals(point_object, v, type_object)) {
						// CARRY ON...
						point_docs.add(doc);
					} else {
						GRow row = merge(point_docs, _schema_cols, _field);
						point_docs = new ArrayList<SolrDocument>();
						cached.add(row);
						point_object = v;
						point_docs.add(doc);
					}

					if (cached.size() >= increment) {
						addRows(_toURL, cached);
						cached = new ArrayList<GRow>();
					}

					index++;
				}

				if (cached.size() > 0) {
					addRows(_toURL, cached);
					cached = new ArrayList<GRow>();
				}
			}

			return "undefined lac... TODO!";

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return null;
	}

	private void addRows(String _toURL, ArrayList<GRow> cached) {
		// TODO Auto-generated method stub

	}

	private boolean equals(Object point_object, Object v, String type_object) {
		if (type_object.equalsIgnoreCase("string")
				|| type_object.equalsIgnoreCase("text")) {
			if (v == null)
				v = "";
			String po = point_object.toString();
			String vo = v.toString();
			po = po.trim();
			vo = vo.trim();
			if (po.equalsIgnoreCase(vo))
				return true;
		}
		return false;
	}

	private GRow merge(String _url, String _search_string, int _start,
			int _count) {
		GResults rr = GBSearch.search(_url, _search_string, 0, _count)
				.getResults();
		ArrayList<GRow> row_list = rr.getValues();
		GRow r = new MergeResultRow();
		ArrayList<GColumn> rd = rr.getColumns();
		for (GColumn p : rd) {
			String _key = p.getName();
			String _type = p.getType();
			r.setType(_key, _type);
		}

		for (GRow row : row_list) {
			HashMap values = row.getData();
			Set<String> keys = values.keySet();
			for (String key : keys) {
				if (key.equalsIgnoreCase("TMID_lastUpdated")) {
				}
				// r.add(key, new Date());
				else if (key.equalsIgnoreCase("TMID")) {
				} else {
					Object value = values.get(key);
					r.add(key, value);
				}
			}
		}
		return r;
	}

	private GRow merge(ArrayList<SolrDocument> _doc,
			ArrayList<GColumn> _schema_cols, String _field) {
		GRow r = new MergeResultRow();
		for (GColumn p : _schema_cols) {
			r.setType(p.getName(), p.getType());
		}

		for (SolrDocument doc : _doc) {
			Collection<String> fields = doc.getFieldNames();
			for (String key : fields) {

				if (key.equalsIgnoreCase("TMID_lastUpdated")) {
				} else if (key.equalsIgnoreCase(_field)) {
					Object value = doc.getFieldValue(key);
					r.set(key, value);
				} else if (key.equalsIgnoreCase("TMID")) {
				} else {

					Object value = doc.getFieldValue(key);
					r.add(key, value);
				}
			}
		}
		return r;
	}

	private String createTable(String _toURL, String _user_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
