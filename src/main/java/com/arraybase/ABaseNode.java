package com.arraybase;


import com.arraybase.flare.PatternHandler;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.*;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author donaldm
 * 
 * General methods for accessing nodes in arraybase.
 * 
 */
public class ABaseNode {


	// WE NEED TO DO THIS PROPERLY BY LIMITING THE SIZE.. THIS COULD BE A HEAP PROBLEM IF GB GROWS. }}
	private static LinkedHashMap<String, ArrayList<GColumn>> schemaCache = new LinkedHashMap<String, ArrayList<GColumn>>();


//	static {
//		new CacheManager ( schemaCache );
//	}


	private TNode node = null;
	
	
	public ABaseNode ()
	{
		
	}
	public ABaseNode ( TNode node ){
		this.node = node;
	}
	
	
	
	public static void main(String[] args){
//		String[] cols = {"index", "content", "another"};
//		String json = getJson ( "/isis/experimental/Test", "*:*", cols, 0, 1000);
//		Gson g = new Gson ();
		//System.out.println ( json );
		
		String db = "hg19";
		
		String ISIS_NO = "isisno";
		String START = "start";
		String END = "end";
		String FSTART = "feature_start";
		String FEND = "feature_end";
		String feature_type = "feature";
		String gene = "gene";
		String chromosome = "chrom";
		String annotation_source = "data_src";
		String strand = "strand";
		String feature_strand = "feature_strand";
		String transcript_id = "transcipt_id";
		String exon_number = "exon_number";
		String exon_id = "exon_id";
		String intron_id = "intron_id";
		String gene_name = "gene_name";
		String alignment_no = "alignment_id";
		String[] cols = { ISIS_NO, START, END, FSTART, FEND, feature_type,
				gene, chromosome, annotation_source, strand, feature_strand,
				transcript_id, exon_number, exon_id, intron_id, gene_name, alignment_no };
		ABaseResults results = ABaseNode.get(
				"/isis/data/genomic/mapping/hg19/direct1", "isisno:" + 10138,
				cols, 0, 1000);
		ArrayList<GRow> list = results.getValues();
		if (list == null) {
			System.out.println ( " no results?");
		}
		
		
	}
	
	/**
	 * get the json string representation of a query for a particular path
	 * @param path
	 * @return
	 */
	public static String getJson ( String path, String searchString, String[] cols, int start_document, int document_count){
		
		String core = TMSolrServer.getCore(path);
		String host = ABProperties.getSolrURL();
		
		String sort = "TMID_lastUpdated desc";
        HttpSolrClient solr = null;
		try {
			String fl = null;
			if (cols != null && cols.length > 0) {
				fl = "";
				for (String col : cols) {
					fl += col + ",";
				}
				fl = fl.substring(0, fl.length() - 1);// trim the comma
			}
			String solr_url = host;
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			
			searchString = searchString.replaceAll(" and ", " AND ");
			searchString = searchString.replaceAll(" not ", " NOT ");
			searchString = searchString.replaceAll(" or ", " OR ");
			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
			solr = new HttpSolrClient.Builder(solr_url + core).build();
			solr.setUseMultiPartPost(true);
			try {
				SolrPingResponse pingResp = solr.ping();
				if (pingResp.getStatus() < 0) {
					return null;
				}
			} catch (Exception _se) {
				GB.print(" Core not available : " + core + " with solr base url : " + solr.getBaseURL());
				return null;
			}
			if (searchString == null || searchString.length() <= 0)
				searchString = "*:*";
			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + searchString);
			params.set("start", start_document);
			params.set("rows", document_count);
			params.set("sort", sort);
			if (fl != null) {
				params.set("fl", fl.trim());
			} else
				params.set("fl", "*");

			params.set("wt", "json");
			// params.set("facet", true);
			QueryResponse response = solr.query(params);
//			GB.updateStats(path, "search:" + searchString);
			String json = response.toString();
			return json;
		} catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
			_solrException.printStackTrace();

		} catch (Exception _e) {
			_e.printStackTrace();
			GB.print("Failed to connect to the Bioinformatics database");
			return null;
		} finally {
            IOUTILs.closeResource(solr);
        }
        return null;
	}

	/**
	 * get the json string representation of a query for a particular path
	 * @param path
	 * @return
	 */
	public static ABaseResults get( String path, String searchString, String[] cols, int start_document, int document_count){
		if ( path == null || searchString == null )
			return null;
		String core = TMSolrServer.getCore(path);
		String host = ABProperties.getSolrURL();
		String sort = "TMID_lastUpdated desc";
		HttpSolrClient solr = null;
		try {
			String fl = null;
			if (cols != null && cols.length > 0) {
				fl = "";
				for (String col : cols) {
					fl += col + ",";
				}
				fl = fl.substring(0, fl.length() - 1);// trim the comma
			}
			String solr_url = host;
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			
			searchString = searchString.replaceAll(" and ", " AND ");
			searchString = searchString.replaceAll(" not ", " NOT ");
			searchString = searchString.replaceAll(" or ", " OR ");
			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
			solr = new HttpSolrClient.Builder(solr_url + core).build();
			try {
				SolrPingResponse pingResp = solr.ping();
				if (pingResp.getStatus() < 0) {
					return null;
				}
			} catch (Exception _se) {
				GB.print(" Core not available : " + core + " with solr base url : " + solr.getBaseURL());
				return null;
			}
			if (searchString == null || searchString.length() <= 0)
				searchString = "*:*";
			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + searchString);
			params.set("start", start_document);
			params.set("rows", document_count);
			params.set("sort", sort);
			if (fl != null) {
				params.set("fl", fl.trim());
			} else
				params.set("fl", "*");

			params.set("wt", "json");
			// params.set("facet", true);
			QueryResponse response = solr.query(params);
//			GB.updateStats(path, "search:" + searchString);


			// BUID THE RESULT THROUGH THE RESULTS FACTORY
			GResults results = ResultsFactory.buildResults(core, start_document, document_count, response);
			ArrayList<GColumn> desc = describeCore(host, core);
			// TODO: Test search and pay attention to all columns.
			// TODO: need to make sure this works as advertised.
			desc = GBSearch.removeTrackingColumns(desc);
			if (desc == null || desc.size() <= 0) {
				GResults re = new GResults();
				re.setSuccessfulSearch(false);
				re.setMessage("Schema : "
						+ core
						+ " not found.  Failed to connect to the Bioinformatics database");
				return re;
			}
			results.setColumns(desc);
			return results;
		} catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Failed to connect to the Bioinformatics database");
			return re;

		} catch (Exception _e) {
			_e.printStackTrace();
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Failed to connect to the Bioinformatics database");
			return re;
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	/**
	 * get the json string representation of a query for a particular path
	 * @param path
	 * @return
	 */
	public static ABaseResults get( String path, String searchString, String[] cols, int start_document, int document_count,
									HttpSolrClient solr){
		String core = TMSolrServer.getCore(path);
		String host = ABProperties.getSolrURL();
		
		String sort = "TMID_lastUpdated desc";
		try {
			String fl = null;
			if (cols != null && cols.length > 0) {
				fl = "";
				for (String col : cols) {
					fl += col + ",";
				}
				fl = fl.substring(0, fl.length() - 1);// trim the comma
			}
			String solr_url = host;
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			
			searchString = searchString.replaceAll(" and ", " AND ");
			searchString = searchString.replaceAll(" not ", " NOT ");
			searchString = searchString.replaceAll(" or ", " OR ");
			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
			solr.setUseMultiPartPost(true);
			try {
				SolrPingResponse pingResp = solr.ping();
				if (pingResp.getStatus() < 0) {
					return null;
				}
			} catch (Exception _se) {
				GB.print(" Core not available : " + core + " with solr base url : " + solr.getBaseURL());
				return null;
			}
			if (searchString == null || searchString.length() <= 0)
				searchString = "*:*";
			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + searchString);
			params.set("start", start_document);
			params.set("rows", document_count);
			params.set("sort", sort);
			if (fl != null) {
				params.set("fl", fl.trim());
			} else
				params.set("fl", "*");

			params.set("wt", "json");
			// params.set("facet", true);
			QueryResponse response = solr.query(params);
//			GB.updateStats(path, "search:" + searchString);



			// BUID THE RESULT THROUGH THE RESULTS FACTORY
			GResults results = ResultsFactory.buildResults(core, start_document, document_count, response);
			ArrayList<GColumn> desc = describeCore(host, core);
			// TODO: Test search and pay attention to all columns.
			// TODO: need to make sure this works as advertised.
			desc = GBSearch.removeTrackingColumns(desc);
			if (desc == null || desc.size() <= 0) {
				GResults re = new GResults();
				re.setSuccessfulSearch(false);
				re.setMessage("Schema : "
						+ core
						+ " not found.  Failed to connect to the Bioinformatics database");
				return re;
			}
			results.setColumns(desc);
			return results;
		} catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Failed to connect to the Bioinformatics database");
			return re;

		} catch (Exception _e) {
			_e.printStackTrace();
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Failed to connect to the Bioinformatics database");
			return re;
		}	
	}
	



	public static ArrayList<GColumn> describeCore(String _server, String _schema)
			throws ConnectException {
		_server = _server.trim();
		_schema = _schema.trim();
		_server = _server.toLowerCase();
		String hash  = _server + _schema;
		ArrayList<GColumn> cols = schemaCache.get ( hash );
		if ( cols != null )
		{
			return cols;
		}

		InputStream istream = null;
		URLConnection conn = null;
		URL url = null;
		try {
			String solr_url = _server;
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			 url = new URL(solr_url + _schema
					+ "/admin/file/?file=managed-schema");
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
				cp.add(cprop);
			}
			if ( cp != null && cp.size() > 0 )
			{
				schemaCache.put ( hash, cp );
			}
			return cp;
			
//			ArrayList<GColumn> norder = new ArrayList<GColumn>();
//			List<String> order = TableManager.getFieldOrder(_schema,
//					GB.getConnectionManager(), cp);
//
//			if (order != null) {
//				for (String i : order) {
//					for (GColumn c : cp) {
//						if (c.getName().equalsIgnoreCase(i))
//							norder.add(c);
//					}
//				}
//				return norder;
//			}// we don't have an order defined...
//			else {
//				// System.err
//				// .println(" Warning:  The field order object is not available. ");
//				for (GColumn c : cp) {
//					norder.add(c);
//				}
//				return norder;
//			}
		} catch (ConnectException _ce) {
			_ce.printStackTrace();
			throw new ConnectException(_ce.getLocalizedMessage());
		} catch (Exception _e) {
			_e.printStackTrace();
			throw new ConnectException(_e.getLocalizedMessage());
		} finally {
			try {
				if ( istream != null )
				{
					istream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ABaseNode getNode(String path) {
		
		NodeManager man = new NodeManager ();
		TNode node = man.getNode(path);
		ABaseNode ano = new ABaseNode ( node );
		
		return ano;
	}
	
	// looks like some dead-end work
	public Map<String, String> getFields() {
		String link = node.getLink();
//		String core_name = TMSolrServer.getCore(gb_file);
		// {{ NOW WE NEED TO COMPARE THE FILE WITH THE SCHEMA }}
//		ArrayList<GColumn> cols = GB.getGBTables().describe(core_name);
		return null;
	}
	
	
	
	
	
}
