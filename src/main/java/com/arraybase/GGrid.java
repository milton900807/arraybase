package com.arraybase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import com.arraybase.flare.XLS;
import com.arraybase.tm.GRow;

public class GGrid {
	private ArrayList<GRow> rows = new ArrayList<GRow>();
	private HttpSolrClient editor = null;

	protected GGrid(HttpSolrClient _editor) {
		editor = _editor;
	}

	public void add(ArrayList<GRow> docs) {
		rows = docs;
	}

	public void commit() throws IOException, EditFailedException {
		ArrayList<SolrDocument> docs = new ArrayList<SolrDocument> ();
		for (GRow r : rows) {
			String field = r.getTitle();
			SolrInputDocument solr_doc = new SolrInputDocument();
			HashMap values = r.getData();
			Set<String> keys = values.keySet();

			for (String key : keys) {
				Object object = values.get(key);
				solr_doc.addField(key, object);
			}
			try {	
				editor.add ( solr_doc );
			} catch (SolrServerException e) {
				e.printStackTrace();
				throw new EditFailedException("Failed to add the data to " + editor.getBaseURL());
			}
		}
		try {
			editor.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
			throw new EditFailedException("Failed to commit the data to " + editor.getBaseURL());
		}
	}
	
	public void clear ()
	{
		rows.clear();
	}
	
}
