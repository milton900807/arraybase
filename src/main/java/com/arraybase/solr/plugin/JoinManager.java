package com.arraybase.solr.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocSlice;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.servlet.SolrRequestParsers;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorChain;

import com.arraybase.flare.TMID;
import com.arraybase.tm.DefaultWhereClause;
import com.arraybase.tm.OperationListener;
import com.arraybase.tm.SchemaDescriptor;
import com.arraybase.tm.TMSchema;
import com.arraybase.tm.TableResultManager;
import com.arraybase.tm.WhereClause;
import com.arraybase.util.GBLogger;
import com.google.gson.Gson;

public class JoinManager {

	private SolrCore table_l = null;
	private SolrCore table_r = null;
	private String new_table_name = null;
	private String field_l = null;
	private String field_r = null;
	private HashMap<String, String> alias = new HashMap<String, String>();
	private GAdmin admin = null;
	private static GBLogger lg = GBLogger.getLogger(JoinManager.class);

	public JoinManager(GAdmin _admin, HashMap<String, String> _alias,
			SolrCore table_l, String field_l, SolrCore table_r, String field_r,
			String new_table_name) {
		admin = _admin;
		alias = _alias;
		this.table_l = table_l;
		this.field_l = field_l;
		this.field_r = field_r;
		this.table_r = table_r;
		this.new_table_name = new_table_name;
	}

	public static void main(String[] _args) {
		// table_l=milton_Repository_view_htb_sample&field_l=PATIENT_ID&table_r=milton_Repository_watson&field_r=SUBJECT_ID
		String v = "table_l=milton_Repository_view_htb_sample&field_l=PATIENT_ID&table_r=milton_Repository_watson&field_r=SUBJECT_ID";
		HashMap<String, String> alias = new HashMap<String, String>();
		alias.put("l.TMID", "TMID");
		alias.put("l.SUBJECT_ID", "watson_subject_id");
		alias.put("r.PATIENT_NUMBER", "htl_patient_number");
		alias.put("r.PATIENT_ID", "htl_patient_id");
		alias.put("r.SOURCE_NAME", "htl_source_name");
		alias.put("r.TISSUE_DIAGNOSIS", "htl_TISSUE_DIAGNOSIS");
		String gson = new Gson().toJson(alias);
		v += "&select_cols=" + gson;
		v += "&join_table_name=nimjoin";
		// try {
		// v = URLEncoder.encode(v, "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		System.out.println(v);

	}

	/**
	 * http://localhost:8983/solr/admin/cores?action=join&
	 * table_l=milton_Repository_view_htb_sample& field_l=PATIENT_ID&
	 * table_r=milton_Repository_watson& field_r=SUBJECT_ID&
	 * select_cols={%22r.BIOLOGICAL_MATRIX
	 * %22:%22biological_matrix%22,%22l.TMID%22
	 * :%22previous_random_number%22,%22r
	 * .SUBJECT_ID%22:%22subject_id%22}&join_table_name=nimjoin *
	 * 
	 */

	// {{ THE JOIN OPERATION WAS REMOVED ON 05.19.2015 IN ORDER TO CLEAN UP AND REFACTOR ARRAYBASE. }}

//
//
//	public void performOperation(OperationListener _listener) {
//		// 1. We need to merge the schemas
//		SchemaDescriptor left_d = new SchemaDescriptor(table_l);
//		SchemaDescriptor right_d = new SchemaDescriptor(table_r);
//
//		// 2. Generate the new table
//		SchemaDescriptor new_schema = TMSchema.createJoinSchema(
//				new_table_name, alias, left_d, right_d, admin);
//
//		if (new_schema != null) {
//			lg.debug("you have created the new schema");
//		}
//
//		DefaultWhereClause df = new DefaultWhereClause(field_l, field_r);
//		// 3. run thru the join method.
//		join(left_d, right_d, new_schema, df, alias);
//	}

	// http://localhost:8983/solr/admin/cores?action=
	// join&table_l=milton_Repository_watson&
	// field_l=SUBJECT_ID&table_r=milton_Repository_view_htb_sample&
	// field_r=PATIENT_NUMBER&select_cols={}&join_table_name=nimjoin21

	// http://localhost:8983/solr/admin/cores?action=join&table_l=milton_Repository_watson&field_l=SUBJECT_ID&table_r=milton_Repository_view_htb_sample&field_r=PATIENT_NUMBER&select_cols={%22r.PATIENT_NUMBER%22:%22htl_patient_number%22,%22r.PATIENT_ID%22:%22htl_patient_id%22,%22r.SOURCE_NAME%22:%22htl_source_name%22,%22r.TISSUE_DIAGNOSIS%22:%22htl_TISSUE_DIAGNOSIS%22,%22l.TMID%22:%22TMID%22,%22l.SUBJECT_ID%22:%22watson_subject_id%22}&join_table_name=nimjoin

	private void join(SchemaDescriptor left_d, SchemaDescriptor right_d,
			SchemaDescriptor new_schema, WhereClause _where,
			HashMap<String, String> alias) {

		SolrCore left_core = left_d.getCore();
		SolrCore right_core = right_d.getCore();
		SolrCore dest_core = new_schema.getCore();
		String _query = "*:*";
		// http://localhost:8983/solr/admin/cores?action=create_field_facet&schema=milton_Repository_HTL&table=$original_field&field_name=$destination_field
		SolrParams left_q = SolrRequestParsers.parseQueryString("q=" + _query);
		LocalSolrQueryRequest left_Req = new LocalSolrQueryRequest(left_core,
				left_q);
		SolrIndexSearcher left_searcher = left_Req.getSearcher();
		if (left_searcher != null) {
			int increment = 30000;
//			IndexSchema is = left_core.getSchema();
			int max_doc = left_searcher.maxDoc();
			String where_fields_left = _where.getLeft();
			String where_fields_right = _where.getRight();

			try {

				for (int j = 0; j < max_doc; j += increment) {
					for (int i = j; (i < max_doc) && (i < (j + increment)); i++) {
						Document d = left_searcher.doc(i);
						String value_st = d.get(where_fields_left);
						
						System.out
								.println("n\n\n\n\n\n\n\n\n\t\t\t\t field name : "
										+ where_fields_left
										+ "\t   ----------------------------------------------------------> "
										+ value_st);
						if (value_st != null && value_st.length() > 0) {
							TableResultManager right_search = search(right_core,
									where_fields_right, value_st);
							append(d, right_search, left_core.getLatestSchema(),
									right_core.getLatestSchema(), dest_core,
									increment, alias);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 *  Append a table with the result.
	 * @param d
	 * @param right_search
	 * @param lSchema
	 * @param rSchema
	 * @param dest_core
	 * @param increment
	 * @param _alias
	 * @throws IOException
	 */
	private void append(Document d, TableResultManager right_search,
			IndexSchema lSchema, IndexSchema rSchema, SolrCore dest_core,
			int increment, HashMap<String, String> _alias) throws IOException {

		String r_where_field = right_search.getWhereField();
		String r_where_value = right_search.getWhereValue();
		SolrParams uparams = SolrRequestParsers
				.parseQueryString("action=update&name=" + dest_core.getName()
						+ "&instanceDir=" + dest_core.getName());
		LocalSolrQueryRequest update_solrReq = new LocalSolrQueryRequest(
				dest_core, uparams);
		UpdateRequestProcessorChain upc = dest_core
				.getUpdateProcessingChain(null);
		UpdateRequestProcessor processor = upc.createProcessor(update_solrReq,
				null);
		
		
		AddUpdateCommand cmd = new AddUpdateCommand(update_solrReq);
		// select_cols={"r.BIOLOGICAL_MATRIX":"biological_matrix","l.TMID":"previous_random_number","r.SUBJECT_ID":"subject_id"}
		boolean hit = false;
		boolean where_clause = false;
		boolean commit_it = false;
		DocIterator right_List = right_search.getDocIterator();
		if (right_List != null) {

			while (right_List.hasNext()) {
				where_clause = false;
				SolrInputDocument inputdoc = new SolrInputDocument();

				for (IndexableField fieldable : d.getFields()) {

					String f_name = "l." + fieldable.name();
					String alias_name = _alias.get(f_name);
					if (alias_name == null) {
						Set<String> keys = _alias.keySet();
						for (String k : keys) {
							if (f_name.equalsIgnoreCase(k))
								alias_name = _alias.get(k);
						}
					}

					if (alias_name != null) {
						SchemaField schemaField = lSchema
								.getFieldOrNull(fieldable.name());
						if (schemaField != null) {
							FieldType fieldType = schemaField.getType();
							Object value = fieldType.toObject(fieldable);
							hit = true;
							inputdoc.addField(alias_name, value);
						}
					}
				}

				int i = right_List.next();
				Document right_doc = right_search.getDocument(i);
				for (IndexableField fieldable : right_doc
						.getFields()) {

					String field_name = fieldable.name();
					// verify the where clause is ...
					if (field_name.equalsIgnoreCase(r_where_field)) {
						SchemaField schemaField = rSchema
								.getFieldOrNull(fieldable.name());
						if (schemaField != null) {
							FieldType fieldType = schemaField.getType();
							Object value = fieldType.toObject(fieldable);
							String vs = value.toString();
							if (vs.equalsIgnoreCase(r_where_value)) {
								System.out
										.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\t field name : "
												+ field_name);
								System.out.println(" r where field "
										+ r_where_field + " value "
										+ r_where_value);
								where_clause = true;
							}
						} else {
							// would love to break the loop here... but this
							// might open bug doors.
						}
					}

					String f_name = "r." + fieldable.name();
					String alias_name = _alias.get(f_name);
					if (alias_name == null) {
						Set<String> keys = _alias.keySet();
						for (String k : keys) {
							if (f_name.equalsIgnoreCase(k))
								alias_name = _alias.get(k);
						}
					}
					lg.debug(" RIGHT alias name  " + alias_name);
					if (alias_name != null) {
						SchemaField schemaField = rSchema
								.getFieldOrNull(fieldable.name());
						if (schemaField != null) {
							FieldType fieldType = schemaField.getType();
							Object value = fieldType.toObject(fieldable);
							hit = true;
							inputdoc.addField(alias_name, value);
						}
					}
				}
				if (hit && where_clause) {
					commit_it = true;
					inputdoc.addField("TMID_lastUpdated", new Date());// new
					inputdoc.addField("TMID", TMID.create());
					cmd.clear();
					cmd.solrDoc = inputdoc;
					processor.processAdd(cmd);
				}
			}
		}

		if (commit_it) {
			CommitUpdateCommand com = new CommitUpdateCommand(update_solrReq, true);
			processor.processCommit(com);
		}
	}

	private void append(TableResultManager left_search, IndexSchema lSchema,
			TableResultManager right_search, IndexSchema rSchema,
			SolrCore dest_core, int increment, HashMap<String, String> _alias)
			throws IOException {

		int max_doc = left_search.size();
		if (max_doc < right_search.size())
			max_doc = right_search.size();

		increment = 1;

		// {{ ----- }}
		SolrParams uparams = SolrRequestParsers
				.parseQueryString("action=update&name=" + dest_core.getName()
						+ "&instanceDir=" + dest_core.getName());
		LocalSolrQueryRequest update_solrReq = new LocalSolrQueryRequest(
				dest_core, uparams);
		UpdateRequestProcessorChain upc = dest_core
				.getUpdateProcessingChain(null);
		UpdateRequestProcessor processor = upc.createProcessor(update_solrReq,
				null);
		AddUpdateCommand cmd = new AddUpdateCommand(update_solrReq);
		// select_cols={"r.BIOLOGICAL_MATRIX":"biological_matrix","l.TMID":"previous_random_number","r.SUBJECT_ID":"subject_id"}
		boolean hit = false;

		for (int j = 0; j < max_doc; j += increment) {
			hit = false;
			SolrInputDocument inputdoc = new SolrInputDocument();
			DocList left_List = left_search.subset(j, increment);
			if (left_List != null) {
				Iterator<Integer> it = left_List.iterator();
				int count = 0;
				while (it.hasNext()) {
					int docuId = it.next();
					Document left_doc = left_search.getDocument(docuId);
					System.out.println(" \t\t\t\tdocument count : " + count++);
					for (IndexableField fieldable : left_doc
							.getFields()) {
						String f_name = "l." + fieldable.name();
						String alias_name = _alias.get(f_name);
						if (alias_name == null) {
							Set<String> keys = _alias.keySet();
							for (String k : keys) {
								if (f_name.equalsIgnoreCase(k))
									alias_name = _alias.get(k);
							}
						}
						lg.debug(" LEFT alias name  " + alias_name);
						if (alias_name != null) {
							SchemaField schemaField = lSchema
									.getFieldOrNull(f_name);
							if (schemaField != null) {
								FieldType fieldType = schemaField.getType();
								Object value = fieldType.toObject(fieldable);
								hit = true;
								inputdoc.addField(alias_name, value);
							}
						}
					}
				}
			}
			DocList right_List = right_search.subset(j, increment);
			if (right_List != null) {
				Iterator<Integer> rit = right_List.iterator();
				while (rit.hasNext()) {
					int i = rit.next();
					Document right_doc = right_search.getDocument(i);
					for (IndexableField fieldable : right_doc
							.getFields()) {
						String f_name = "r." + fieldable.name();
						String alias_name = _alias.get(f_name);
						if (alias_name == null) {
							Set<String> keys = _alias.keySet();
							for (String k : keys) {
								if (f_name.equalsIgnoreCase(k))
									alias_name = _alias.get(k);
							}
						}
						lg.debug(" RIGHT alias name  " + alias_name);
						if (alias_name != null) {
							SchemaField schemaField = rSchema
									.getFieldOrNull(f_name);
							if (schemaField != null) {
								FieldType fieldType = schemaField.getType();
								Object value = fieldType.toObject(fieldable);
								hit = true;
								inputdoc.addField(alias_name, value);
							}
						}
					}
				}
			}

			if (hit) {
				inputdoc.addField("TMID_lastUpdated", new Date());// new
																	// TrieDateField
																	// ().toExternal(new
																	// Date
																	// ());
				inputdoc.addField("TMID", TMID.create());
				cmd.clear();
				cmd.solrDoc = inputdoc;
				System.out.println("\t\t j : " + j);
				processor.processAdd(cmd);
			}
		}

		CommitUpdateCommand com = new CommitUpdateCommand(update_solrReq, true);
		processor.processCommit(com);
	}

	private TableResultManager search(SolrCore _core, String _field, String _search) {
		String _search_string = _field + ":" + fixSearchChars ( _search );
		SolrRequestHandler handler = _core.getRequestHandler("standard");
		SolrParams left_q = SolrRequestParsers.parseQueryString("q="
				+ _search_string);
		
		System.out.println ( " search string : "+ _search_string);
		SolrQueryRequest left_Req = new LocalSolrQueryRequest(_core, left_q);
		SolrQueryResponse rsp = new SolrQueryResponse();
		handler.handleRequest(left_Req, rsp);

		SimpleOrderedMap list1 = (SimpleOrderedMap) rsp.getValues();
		SolrIndexSearcher search = left_Req.getSearcher();
		DocSlice list = (DocSlice) list1.get("response");
		TableResultManager rs = new TableResultManager(search, list, _field, _search);

		return rs;
	}

	private String fixSearchChars(String _search) {
		
		if ( _search.contains(" ")){
			return "\""+_search + "\"";
		}
		_search=_search.replace("(", "\\(");
		_search=_search.replace(")", "\\)");
		_search=_search.replace("[", "\\[");
		_search=_search.replace("]", "\\]");
		_search=_search.replace("{", "\\{");
		_search=_search.replace("}", "\\}");
		_search=_search.replace("+", "\\+");
		_search=_search.replace("-", "\\-");
		_search=_search.replace("^", "\\^");
		_search=_search.replace("*", "\\*");
		_search=_search.replace("\"", "\\\"");
		_search=_search.replace("&", "\\&");
		_search=_search.replace("!", "\\!");
		_search=_search.replace("?", "\\?");

		return _search;
	}

	private boolean queryRight(SolrCore right_core, String where_fields_right,
			Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * What we want tod o is search the right and find the value of 22 * @param
	 * right_core
	 * 
	 * @param value
	 * @return
	 */
	private boolean queryRight(SolrCore right_core, Object value) {

		return false;
	}

}
