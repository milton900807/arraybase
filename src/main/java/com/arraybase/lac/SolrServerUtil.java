package com.arraybase.lac;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.util.IOUTILs;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.xml.sax.InputSource;

import com.arraybase.flare.DictionaryXMLHandler;
import com.arraybase.flare.LoaderException;
import com.arraybase.flare.PatternHandler;
import com.arraybase.flare.TMID;
import com.arraybase.flare.XLS;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.util.ABProperties;

public class SolrServerUtil {

	public static ArrayList<GColumn> getFields(String _solr_core_url)
			throws ConnectException {
		URLConnection conn = null;
		InputStream istream = null;
		try {
			if (!_solr_core_url.endsWith("/")) {
				_solr_core_url += "/";
			}
//			URL url = new URL(_solr_core_url + "admin/file/?file=schema.xml");
			URL url = new URL(_solr_core_url + "admin/file/?managed-schema");
			conn = url.openConnection();
			istream = conn.getInputStream();
			org.xml.sax.XMLReader parser = new org.apache.xerces.parsers.SAXParser();
			PatternHandler handler = new PatternHandler();
			parser.setContentHandler(handler);
			parser.setErrorHandler(handler);
			org.xml.sax.InputSource input = new InputSource(istream);
			parser.parse(input);
			ArrayList<GColumn> cp = new ArrayList<GColumn>();
			ArrayList<LinkedHashMap<String, String>> data = handler.getData();
			for (HashMap<String, String> val : data) {
				String _key = val.get("name");
				String value = val.get("type");
				GColumn cprop = new GColumn();
				cprop.setTitle(_key);
				cprop.setType(value);
				if (!_key.endsWith("__900807"))
					cp.add(cprop);
			}
			return cp;
		} catch (ConnectException _ce) {
			_ce.printStackTrace();
			throw new ConnectException(_ce.getLocalizedMessage());
		} catch (Exception _e) {
			_e.printStackTrace();
			throw new ConnectException(_e.getLocalizedMessage());
		} finally {
			if (istream != null)
				try {
					istream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}


	public static boolean verifyFields(String string) {
		return true;
	}

	/**
	 * 
	 */
	public static void save(String _target, GRow _row) {
		HttpSolrClient server = null;
		try {
			server = new HttpSolrClient.Builder(_target).build();
			ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument solr_doc = new SolrInputDocument();
			HashMap row_d = _row.getData();
			Set<String> s = row_d.keySet();
			for (String key : s) {
				Object value = row_d.get(key);
				solr_doc.setField(key, value);
			}
			docs.add(solr_doc);
			
			String id = (String)_row.getData().get("TMID");
			
			server.deleteByQuery("TMID:"+id);
			
			server.add(docs);
			server.commit();

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            IOUTILs.closeResource(server);
        }
	}

	public static void search(String field, String value) {
		
	}
}
