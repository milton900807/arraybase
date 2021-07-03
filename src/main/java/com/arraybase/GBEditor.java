package com.arraybase;

import com.arraybase.flare.CurrentTimeForSolr;
import com.arraybase.flare.TMID;
import com.arraybase.tm.tables.RowData;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.schema.TrieDateField;

import java.io.IOException;
import java.util.*;

/**
 * @deprecated
 * @author milton
 *
 */
public class GBEditor {

	private String solrUrl = null;
	private HttpSolrClient solr = null;
	private GGrid grid = null;

	public GBEditor(String url) {
		solrUrl = url;
		solr = new HttpSolrClient.Builder(solrUrl).build();
		grid = new GGrid(solr);
	}

	public static String removeByQueryNoCommit(String solr_url, String _query)
			throws UpdateIndexFailed {
		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(solr_url).build();
			org.apache.solr.client.solrj.response.UpdateResponse response = solr
					.deleteByQuery(_query);
			return "" + response.getStatus();
		} catch (Exception _e) {
			_e.printStackTrace();
			throw new UpdateIndexFailed(_e.getLocalizedMessage());
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public String commit() throws EditFailedException {
		try {
			solr.commit();
			return "Commit successful";
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new EditFailedException("Failed to add the data to " + solrUrl);
	}

	public String add(ArrayList<RowData> rows) throws EditFailedException {
		ArrayList<SolrInputDocument> docs = getDocs(rows);
		try {
			solr.add(docs);
			return "Added but not yet committed";
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new EditFailedException("Failed to add the data to " + solrUrl);
	}

	/**
	 * does not use beans
	 * 
	 * @param _row
	 * @return
	 */
	protected static ArrayList<SolrInputDocument> getDocs(
			ArrayList<RowData> _row) {
		ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int i = 0; i < _row.size(); i++) {
			SolrInputDocument sid = new SolrInputDocument();
			RowData r = _row.get(i);
			HashMap mas = r.getData();
			Set keys = mas.keySet();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();

				if ((!key.equals("delete")) && (!key.equals("save"))
						&& (!key.equals("_checkboxField"))) {
					sid.addField(key, mas.get(key));
				}
			}
			if (sid.getField("TMID_lastUpdated") != null) {
				Date udate = new Date();
				TrieDateField df = new TrieDateField();
				// System.out.println(" we have the last udate field " + dd);
				sid.setField("TMID_lastUpdated", CurrentTimeForSolr.time());
			}
			if (sid.getField("TMID") == null) {
				sid.addField("TMID", TMID.create());
			}
			if (sid.getField("TMID_lastUpdated") == null) {
				Date udate = new Date();
				TrieDateField df = new TrieDateField();
				sid.addField("TMID_lastUpdated", CurrentTimeForSolr.time());
			}

			docs.add(sid);
		}
		return docs;
	}

	public GGrid getGrid() {
		return grid;
	}

	/**
	 * Rename the field of a core.
	 * @deprecated
	 * @param _original
	 */
	public String renameField(String _original, String _newName) {
		String _schema = GBUtil.getSchema(solrUrl);
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("action", "field_change");
		params.set("schema", _schema);
		params.set("orig", _original);
		params.set("dest", _newName);
		String url = solrUrl + "/admin/cores?action=field_change&schema="
				+ _schema + "&orig=" + _original + "&dest=" + _newName;
		GB.callSolr(url);
		return "Field has been changed.";
	}

}
