package com.arraybase.solr.plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.arraybase.flare.CurrentTimeForSolr;
import org.apache.lucene.document.Document;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TrieDateField;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.servlet.SolrRequestParsers;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorChain;

import com.arraybase.tm.TMSchema;
import com.arraybase.util.GBLogger;


public class PublishTable {

	private GAdmin admin = null;
	public final static GBLogger lg = GBLogger.getLogger(PublishTable.class);
	public final static String table_name = "table_name";
	public final static String table_state = "table_state";
	public final static String description = "description";
	public final static String url = "url";
	public final static String publisher = "publisher";
	public final static String path = "path";

	public PublishTable(GAdmin _admin) {
		admin = _admin;
	}

	public static void createPublishTable(GAdmin admin, String _table_name) {
		try {
			CoreContainer container = admin.getCoreContainer();
			String solr_home = container.getSolrHome();
			solr_home = trimEdge(solr_home);
			String create_instance_dir = _table_name;
			lg.debug("create instancedirectory : " + create_instance_dir);
			createPublished_schema(admin, _table_name, solr_home);
//			SolrParams newCore_params = SolrRequestParsers
//					.parseQueryString("action=CREATE&name=" + _table_name
//							+ "&instanceDir=" + create_instance_dir);
//			LocalSolrQueryRequest solrReq_create = new LocalSolrQueryRequest(
//					null, newCore_params);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	private static String trimEdge(String solr_home) {
		if (solr_home.startsWith("/"))
			return solr_home.substring(1);
		if (solr_home.endsWith("/"))
			return solr_home.substring(0, solr_home.length() - 1);
		return solr_home;
	}

	/**
	 * Publish the table to the core.
	 * 
	 * 
	 * 
	 * e.g.
	 */
	@Deprecated
	public void publish(String _table_name, String _table_state, String _path,
			String _publisher, String _description, String publish_table_name) {
		try {
			CoreContainer container = admin.getCoreContainer();
			SolrCore published_core = container.getCore(publish_table_name);
			if (published_core == null) {
				lg.error("Failed to find the publish core... creating now" + "");

			}

			lg.debug("published_core : " + published_core.getName());

			SolrParams uparams = SolrRequestParsers
					.parseQueryString("action=update&name="
							+ published_core.getName() + "&instanceDir="
							+ published_core.getName());
			LocalSolrQueryRequest update_solrReq = new LocalSolrQueryRequest(
					published_core, uparams);
			UpdateRequestProcessorChain upc = published_core
					.getUpdateProcessingChain(null);
			UpdateRequestProcessor processor = upc.createProcessor(
					update_solrReq, null);
			AddUpdateCommand cmd = new AddUpdateCommand(update_solrReq);
			CommitUpdateCommand com = new CommitUpdateCommand(update_solrReq,
					true);
			SolrInputDocument inputdoc = new SolrInputDocument();
			inputdoc.addField(table_name, _table_name);
			inputdoc.addField(table_state, _table_state);
			inputdoc.addField(path, _path);
			inputdoc.addField(publisher, _publisher);
			inputdoc.addField(description, _description);

			UUID idOne = UUID.randomUUID();
			String tmid = idOne.toString();
			inputdoc.addField("TMID", tmid);
			inputdoc.addField("TMID_lastUpdated", new Date());
			cmd.solrDoc = inputdoc;
			processor.processAdd(cmd);
			processor.processCommit(com);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	/**
	 * Create the schema for publishing tables
	 * 
	 * @param publishedCore
	 * @param solrHome
	 */
	private static void createPublished_schema(GAdmin gadmin,
			String publishedCore, String solrHome) {
		HashMap<String, HashMap<String, String>> _params1 = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "table_name");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", "");
		uuidp.put("dataType", "string");
		uuidp.put("requiredField", "true");
		_params1.put("table_name", uuidp);

		HashMap<String, String> state = new HashMap<String, String>();
		state.put("fieldName", "table_state");
		state.put("sortable", "true");
		state.put("indexed", "true");
		state.put("defaultString", "");
		state.put("dataType", "string");
		state.put("requiredField", "false");
		_params1.put("table_state", state);

		HashMap<String, String> group = new HashMap<String, String>();
		group.put("fieldName", "url");
		group.put("sortable", "true");
		group.put("indexed", "true");
		group.put("defaultString", "");
		group.put("dataType", "string");
		group.put("requiredField", "false");
		_params1.put("url", group);

		HashMap<String, String> path = new HashMap<String, String>();
		path.put("fieldName", "path");
		path.put("sortable", "true");
		path.put("indexed", "true");
		path.put("defaultString", "unknown");
		path.put("dataType", "string");
		path.put("requiredField", "false");
		_params1.put("path", path);

		HashMap<String, String> user = new HashMap<String, String>();
		user.put("fieldName", "publisher");
		user.put("sortable", "true");
		user.put("indexed", "true");
		user.put("defaultString", "unknown");
		user.put("dataType", "string");
		user.put("requiredField", "true");
		_params1.put("publisher", user);

		HashMap<String, String> desc = new HashMap<String, String>();
		desc.put("fieldName", "description");
		desc.put("sortable", "true");
		desc.put("indexed", "true");
		desc.put("defaultString", "");
		desc.put("dataType", "text");
		desc.put("requiredField", "false");
		_params1.put("description", desc);

		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
		UUID idOne = UUID.randomUUID();
		HashMap<String, String> uuidp1 = new HashMap<String, String>();
		uuidp1.put("fieldName", "TMID");
		uuidp1.put("sortable", "true");
		uuidp1.put("indexed", "true");
		uuidp1.put("defaultString", idOne.toString());
		uuidp1.put("dataType", "text");
		uuidp1.put("requiredField", "true");
		_params1.put("TMID", uuidp1);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");
		last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
		last_updated.put("dataType", "date");
		last_updated.put("requiredField", "true");
		_params1.put("TMID_lastUpdated", last_updated);

		lg.debug(" CREATING THE PUBLISHING CORE: " + publishedCore);
		TMSchema.createSchemaXML(publishedCore, _params1, true, gadmin);
	}









	// public void testUpdate ()
	// {
	// SolrParams params =
	// SolrRequestParsers.parseQueryString("action=UPDATE&name="+name+"3"+"&instanceDir=.");
	// LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest ( core, params
	// );
	// SolrQueryResponse solrRsp = new SolrQueryResponse();
	// try {
	// SolrRequestHandler handler =
	// core.getRequestHandler(solrReq.getQueryType());
	// if (handler == null) {
	// lg.log("Unknown Request Handler '" + solrReq.getQueryType()
	// + "' :" + solrReq);
	// throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
	// "Unknown Request Handler '" + solrReq.getQueryType() + "'", true);
	// }
	// c.execute(handler, solrReq, solrRsp);
	// if (solrRsp.getException() == null) {
	// // QueryResponseWriter responseWriter =
	// core.getQueryResponseWriter(solrReq);
	// // responseWriter.write(writer, solrReq, solrRsp);
	// } else {
	// Exception e = solrRsp.getException();
	// lg.log("Did not work");
	//
	// }
	// } catch (SolrException e) {
	// e.printStackTrace();
	// }
	//
	//
	// }

}
