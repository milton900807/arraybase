package com.arraybase.tm;

import com.arraybase.GB;
import com.arraybase.GBLinkManager;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.JDBC;
import com.arraybase.db.jdbc.DBConfigFactory;
import com.arraybase.db.jdbc.DBConfiguration;
import com.arraybase.db.jdbc.JDBCUtil;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.*;
import com.arraybase.lac.AnnotationLACAction;
import com.arraybase.lac.LAC;
import com.arraybase.lac.LacOperation;
import com.arraybase.tm.builder.TMLibBuilder;
import com.arraybase.tm.builder.TMLibSchedule;
import com.arraybase.tm.builder.TMSchedulerFactory;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import com.arraybase.util.Level;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrServer; deprecated as of 5 something.
import org.apache.solr.client.solrj.impl.XMLResponseParser ;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.TrieDateField;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/**
 * The server side implementation of the RPC service.
 */
public class TableManager {
	public final static String TMSOLR = "TMSOLR";
	private static GBLogger log = GBLogger.getLogger(TableManager.class);
	private DBConnectionManager dbcm = new DBConnectionManager();
	private TNode node = null;
	private HttpSolrClient solr = null;
	private ArrayList<GColumn> cols = new ArrayList<GColumn>();

	public TableManager(DBConnectionManager _dbcm) {
		dbcm = _dbcm;
	}

	public TableManager(TNode node) {
		this.node = node;
		initSolr();
	}

	private void initSolr() {
		String link = node.getLink();
		if (!GBLinkManager.isFullyQualifiedURL(link)) {
			String sore = TMSolrServer.getCore(node);
			link = GBLinkManager.concat(GB.getDefaultURL(), sore);
		}
		solr = new HttpSolrClient.Builder(link).build();
		try {
			cols = GBTables.describeTable(node);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * add the list of
	 *
	 * @param values
	 * @throws SolrTargetNotDefinedException
	 */
	public int addList(ArrayList<LinkedHashMap<String, Object>> values)
			throws SolrTargetNotDefinedException {
		if (solr == null)
			throw new SolrTargetNotDefinedException(
					"The solr target has not been defined ");
		int count = 0;
		for (LinkedHashMap<String, Object> row : values) {
			addWithNoCommit(row);
			count++;
		}
		try {
			solr.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	// t2.search(cellline:HepG2)[isisno][gene][title][percent_control][stdv]>HepG2
	public void addWithNoCommit(LinkedHashMap<String, Object> values)
			throws SolrTargetNotDefinedException {
		if (solr == null)
			throw new SolrTargetNotDefinedException(
					"The solr target has not been defined ");
		Set<String> fields = values.keySet();
		// possible bug.
		SolrInputDocument doc = new SolrInputDocument();
		for (String field : fields) {
			Object ob = values.get(field);
			if (ob != null) {
				if (ob.toString().length() <= 0) {
				} else {

					// {{ need to check to see if the field is valid. }}
					// {{ ONLY ADD THE VALID FIELDS }}
					if (isValid(field, ob)) {
						doc.addField(field, ob);
					}

				}
			}
		}
		try {
			// before we add.. make sure there are the two required fields:

			Object ob = doc.get("TMID");
			Object last_updated = doc.get("TMID_lastUpdated");
			if (last_updated == null) {
				Date last_updatedd = new Date();
				doc.addField("TMID_lastUpdated", last_updatedd);
			}
			if (ob == null) {
				doc.addField("TMID", TMID.create());
			}

			solr.add(doc);
		} catch (SolrServerException e) {
			e.printStackTrace();
			Collection<String> names = doc.getFieldNames();
			for (String name : names) {
				Object attr = doc.getFieldValue(name);
				GB.print(name + " --> " + attr);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Collection<String> names = doc.getFieldNames();
			for (String name : names) {
				Object attr = doc.getFieldValue(name);
				GB.print(name + " --> " + attr);
			}
		}
	}

	private boolean isValid(String field, Object ob) {

		// { KNOWN FIELDS WE DO NOT WANT TO PROP }}
		// {{ ADDING THIS TRIGGERS A KNOWN SOLR BUG }}
		// {{ SO WE NEED TO SKIP THE VERSION FIELD }}
		if (field.equals("_version_"))
			return false;

		for (GColumn col : cols) {
			String field_name = col.getName();
			if (field_name.equalsIgnoreCase(field)) {
				if (col.getType().equalsIgnoreCase(GColumn.DOUBLE)
						|| col.getType().equalsIgnoreCase(GColumn.FLOAT)
						|| col.getType().equalsIgnoreCase(GColumn.SFLOAT)) {
					if (ob instanceof Number) {
						return true;
					}
					try {
						double strd = Double.parseDouble(ob.toString());
						return true;
					} catch (NumberFormatException _33e) {
						return false;
					}
				} else if (col.getType().equalsIgnoreCase(GColumn.INTEGER)
						|| col.getType().equalsIgnoreCase(GColumn.SINT)) {
					if (ob instanceof Integer) {
						return true;
					}
					try {
						int strd = Integer.parseInt(ob.toString());
						return true;
					} catch (NumberFormatException _33e) {
						return false;
					}
				} else if (col.getType().equalsIgnoreCase(GColumn.DATE)) {
					if (ob instanceof Date)
						return true;
					else {
						// could add some intelligence here.
					}
				}
				return true;
			}

		}

		return false;
	}

	public ArrayList<TMURI> loadLinks(String userID, String _schema,
									  String _itemID) {
		return new ArrayList();
	}

	public String build(String _userName, String _type, String _name,
						String _description, String _security,
						HashMap<String, Map<String, String>> _params,
						HashMap<String, String> _init_raw_data) {

		String _lib_name = _name;
		TTable litem = null;
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			// lock it
			synchronized (hibernateSession) {
				hibernateSession.beginTransaction();
				Criteria c = hibernateSession.createCriteria(TTable.class);
				c.add(Restrictions.eq("title", _lib_name));
				List values = c.list();
				if (values != null && values.size() > 0) {
					litem = (TTable) values.get(0);
				} else {
					litem = new TTable();
				}
				litem.setDescription(_description);
				litem.setLastEdited(new Date());
				litem.setSecurityStatus(_security + ".png");
				litem.setUser(_userName);
				litem.setSourceType(_type);
				TMTableSettings tmset = litem.getSettings();
				if (tmset == null)
					tmset = new TMTableSettings();
				Set<String> keys = _params.keySet();
				LinkedHashMap<String, Integer> order = new LinkedHashMap<String, Integer>();
				int index = 0;
				for (String key : keys) {
					order.put(key, index++);
				}
				tmset.setCol_order(order);
				litem.setSettings(tmset);
				litem.setTitle(_lib_name);
				if (litem.getItemID() < 0)
					hibernateSession.save(litem);
				else {
					hibernateSession.update(litem);
				}
				hibernateSession.getTransaction().commit();
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return build(_type, _name, _params);
	}

	/**
	 * @param _userName
	 * @param _type
	 * @param _name
	 * @param _description
	 * @param _security
	 * @param _params
	 * @param _dbc
	 * @return
	 * @deprecated
	 */
	private String buildFromComposite(String _userName, String _type,
									  String _name, String _description, String _security,
									  HashMap<String, Map<String, String>> _params, TMDBConnection _dbc) {
		// {{ THE FIRST THING WE WANT TO DO IS MAKE A DATBASE CONNECTION }}
		HashMap<String, Map<String, String>> db_params = getDBParams(_dbc);
		Set<String> keys = _params.keySet();
		for (String k : keys) {
			Map<String, String> maps = _params.get(k);
			db_params.put(k, maps);
		}
		TTable litem = new TTable();
		litem.setDescription(_description);
		litem.setLastEdited(new Date());
		litem.setSecurityStatus(_security + ".png");
		litem.setUser(_userName);
		litem.setSourceType(_type);
		litem.setTitle(NameUtiles.strip(_userName, _name));
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			hibernateSession.saveOrUpdate(litem);
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return build(_type, _name, _params);
	}

	/**
	 * Get the database params given a sql-based tmdb object.
	 *
	 * @param _dbc
	 * @return
	 */
	private HashMap<String, Map<String, String>> getDBParams(TMDBConnection _dbc) {
		Connection conn = null;
		Statement st = null;
		ResultSet r = null;
		try {
			log.setLevel(Level.DEBUG);
			conn = JDBCUtil.createConnection(_dbc.getUsr(), _dbc.getPss(),
					_dbc.getUrl(), _dbc.getDvr());
			if (conn != null)
				log.debug(" We have the connection to " + _dbc.getUrl());
			String _sql = _dbc.getSql();
			st = conn.createStatement();
			r = st.executeQuery(_sql);
			// now we need to check the meta data to find out what additional
			// fields
			// we will need to add to the library.
			HashMap<String, Map<String, String>> fields = new HashMap<String, Map<String, String>>();
			ResultSetMetaData rms = r.getMetaData();
			int col_count = rms.getColumnCount();
			for (int i = 1; i <= col_count; i++) {
				String col_name = rms.getColumnName(i);
				log.debug("col_name " + col_name);
				String col_type = rms.getColumnTypeName(i);
				log.debug("col_type: " + col_type);
				int col_type_int = rms.getColumnType(i);
				log.debug("col_type_int " + col_type_int);
				HashMap<String, String> sql_fields = buildFieldSet(col_type,
						col_name);
				fields.put(col_name, sql_fields);
			}
			return fields;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBC.closeResultSet(r);
			JDBC.closeStatement(st);
			JDBC.closeConnection(conn);
		}
		return null;
	}

	/**
	 * This will build the field set for the sorl schema given the meta data
	 * from the sql query
	 *
	 * @param col_type
	 * @param col_name
	 * @return
	 */
	private HashMap<String, String> buildFieldSet(String col_type,
												  String col_name) {
		String type = "string";
		if (col_type.equalsIgnoreCase("VARCHAR")) {
			type = "string";
		} else if (col_type.equalsIgnoreCase("int")) {
			type = "integer";
		} else if (col_type.equalsIgnoreCase("float")) {
			type = "float";
		} else
			type = col_type;

		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", col_name);
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", "");
		uuidp.put("dataType", "" + type);
		uuidp.put("requiredField", "true");
		return uuidp;
	}

	public String save(TTable _item) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			hibernateSession.saveOrUpdate(_item);
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return "saved";
	}

	public String saveTableSettings(int _itemId, TMTableSettings _settings) {
		String msg = "ERROR saving table settings";
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cr = hibernateSession.createCriteria(TTable.class);
			cr.add(Restrictions.eq("itemID", _itemId));

			List ls = cr.list();
			if (ls.size() <= 0) {
				return "Failed to find the table.";
			} else {
				TTable li = (TTable) ls.get(0);

				TMTableSettings tm = li.getSettings();
				if (tm != null) {
					tm.setState(_settings.getState());
					tm.setHeight(_settings.getHeight());
					tm.setWidth(_settings.getWidth());
					tm.setDefault_width(_settings.getDefault_width());
					li.setSettings(tm);
					hibernateSession.saveOrUpdate(tm);
					hibernateSession.flush();
					// List<TMColumn> inC = _settings.getColumns();
					// List<TMColumn> cols = tm.getColumns();
					// int index = 0;
					// for ( TMColumn current : cols ){
					// TMColumn newv = inC.get(index);
					// if (newv != null) {
					// current.setColDesc(newv.getColDesc());
					// current.setColTitle(newv.getColTitle());
					// current.setColumnName(newv.getColumnName());
					// current.setVisible(newv.getVisible());
					// }
					// inC.set(index, current);
					// index++;
					// }
					// tm.setColumns(inC);
					// tm.setCol_order(_settings.getCol_order());
				}

				hibernateSession.getTransaction().commit();
				msg = "Table settings saved.";
			}
		} catch (Exception _e) {
			_e.printStackTrace();
			msg = "Error trying to save the table settings.";
			return msg;
		} finally {
			HBConnect.close(hibernateSession);
		}
		return msg;
	}

	private String build(String _type__, String _name,
						 HashMap<String, Map<String, String>> _params) {
		log.debug("Building...");
		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", TMID.create());
		uuidp.put("dataType", "string");
		uuidp.put("requiredField", "true");
		_params.put("TMID", uuidp);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");
		last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
		last_updated.put("dataType", "date");
		last_updated.put("requiredField", "true");
		_params.put("TMID_lastUpdated", last_updated);
		log.debug("Generating the solr schema... NAME: " + _name);

		String user = "unknown";
		String site = ABProperties.getSolrURL();

		return TMSolrServer.createSchema(user, site, _name, _params, true);
	}

	private static String writeCSVtoSTring(String[][] _data) {
		int rows = _data.length;
		int cols = _data[0].length;
		String file_string = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (j > 0)
					file_string += ",";
				file_string += _data[i][j];
			}
			file_string += "\n";
		}
		return file_string;
	}

	private static void postData(String _name, String[][] _data) {
		HttpURLConnection conn = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			String fs = writeCSVtoSTring(_data);
			// curl
			// http://localhost:8983/solr/update/csv?stream.file=exampledocs/books.csv&stream.contentType=text/plain;charset=utf-8
			String urlstring = solr_url + _name + "/update/csv";
			log.debug(" url string\n\t\t\t" + urlstring + "\n\n\n");
			URL url = new URL(urlstring);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// conn.setRequestProperty("stream.file", "hel");
			conn.setRequestProperty("Content-Type", "text/plain");
			conn.setRequestProperty("commit", "true");
			log.debug(" content stream : " + fs);

			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(fs);
			wr.flush();
			log.debug(" url : " + conn.getURL().toExternalForm());
			rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = rd.readLine()) != null) {
				log.debug(line);
			}
			String urlstring_commit = solr_url + _name + "/update/?commit=true";
			TMSolrServer.callSolr(urlstring_commit, "GET");
			// http://localhost:8983/solr/update?commit=true
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrCallException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(wr);
			IOUTILs.closeResource(rd);
			conn.disconnect();
		}
	}

	/**
	 * Perform the operation
	 */
	public TTable performOperation(String _operation_id,
								   ArrayList<String> lacs, ArrayList<LacOperation> _lac_ops) {
		log.debug("Perform operation : " + _operation_id);
		if (_operation_id.equalsIgnoreCase("join")) {
			LacOperation laco = _lac_ops.get(0);
			String msg = join(_lac_ops.get(0), 0);
			TTable litem = new TTable();

			litem.setDescription("Join Table " + laco.getNewTableName());
			litem.setLastEdited(new Date());
			litem.setSecurityStatus(1 + ".png");
			litem.setUser(_lac_ops.get(0).getUserID());
			litem.setSourceType(SourceType.DEFAULT.name);
			litem.setTitle(laco.getNewTableName());

			Session hibernateSession = null;
			try {
				hibernateSession = dbcm.getSession();
				hibernateSession.beginTransaction();
				hibernateSession.saveOrUpdate(litem);
				hibernateSession.getTransaction().commit();
				return getTable(litem.getItemID());
			} catch (Exception _e) {
				_e.printStackTrace();
			} finally {
				HBConnect.close(hibernateSession);
			}
			return litem;
		}
		return null;
	}

	public TTable getTable(Integer itemID) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("itemID", itemID));
			List list = criteria.list();
			if (list.size() > 0) {
				TTable tl = (TTable) list.get(0);
				tl = HibernateToCoreJava.convert(tl);
				return tl;
			}
			return null;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	/**
	 * Get the table itemid == PK. --from the connection manager.
	 *
	 * @param itemID
	 * @param _dbcm
	 * @return
	 */
	public static TTable getTable(Integer itemID, DBConnectionManager _dbcm) {
		Session hibernateSession = null;
		try {
			hibernateSession = _dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("itemID", itemID));
			List list = criteria.list();
			if (list.size() > 0) {
				TTable tl = (TTable) list.get(0);
				tl = HibernateToCoreJava.convert(tl);
				return tl;
			}
			return null;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	private String join(LacOperation laco, int _index) {

		// laco:params
		// {milton_Repository_subject_5070.search(*:*)1=SUBJECT_ALIAS,
		// milton_Repository_patient_number_5070.search(*:*)0=PATIENT_NUMBER}

		// laco:lacs
		// [milton_Repository_patient_number_5070.search(*:*),
		// milton_Repository_subject_5070.search(*:*)]
		String solr = ABProperties.get(ABProperties.SOLRSITE);
		String target1 = laco.getTarget(_index);
		String target2 = laco.getTarget(_index + 1);
		String data1 = laco.getData(_index);
		String data2 = laco.getData(_index + 1);

		String join_field1 = laco.getParams().get(laco.getLac(_index) + _index);
		String join_field2 = laco.getParams().get(
				laco.getLac(_index + 1) + (_index + 1));

		// String table = "{" + "\"l.SUBJECT_ALIAS\":\"watson_subject_alias\","
		// + "\"l.STUDY_ID\":\"watson_study_id\","
		// + "\"r.PATIENT_NUMBER\":\"htl_patient_number\","
		// + "\"r.PATIENT_ID\":\"htl_patient_id\","
		// + "\"r.SOURCE_NAME\":\"htl_source_name\","
		// + "\"r.TISSUE_DIAGNOSIS\":\"htl_TISSUE_DIAGNOSIS\","
		// + "\"l.SUBJECT_ID\":\"watson_subject_id\","
		// + "\"l.SUBJECT_VISIT\":\"watson_subject_visit\","
		// + "\"l.BIOLOGICAL_MATRIX\":\"watson_spec_type\"" + "" + "}"
		// + "&join_table_name=nimjoin6";
		// String inputURL = "http://localhost:8983/solr/admin/"
		// + "cores?action=join&" + "table_l=milton_Repository_watson&"
		// + "field_l=SUBJECT_ALIAS&"
		// + "table_r=milton_Repository_view_htb_sample&"
		// + "field_r=PATIENT_NUMBER" + "&select_cols=" + table;

		Map<String, String> alias1 = laco.getFieldAliasMap(target1);
		Map<String, String> alias2 = laco.getFieldAliasMap(target2);

		LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
		Set<String> keys1 = alias1.keySet();
		Set<String> keys2 = alias2.keySet();

		for (String k1 : keys1) {
			alias.put("l." + k1, alias1.get(k1));
		}
		for (String k2 : keys2) {
			alias.put("r." + k2, alias2.get(k2));
		}

		String new_table_name = laco.getUserId() + "_Repository_"
				+ laco.getNewTableName();
		// String join_table = "join_table_name=" + new_table_name;
		String lField = laco.getLfield();
		String rField = laco.getRfield();
		String rTable = LAC.getTarget(laco.getrLac());
		String lTable = LAC.getTarget(laco.getlLac());
		Gson g = new Gson();
		String selcol = g.toJson(alias);
		String params_wurl = solr + "/admin/cores?action=join&table_l="
				+ lTable + "&table_r=" + rTable + "&join_table_name="
				+ new_table_name + "&field_l=" + lField + "&field_r=" + rField
				+ "&select_cols=" + selcol;
		try {
			TMSolrServer.callSolr(params_wurl);
		} catch (SolrCallException e) {
			e.printStackTrace();
		}

		// http://localhost:8983/solr/admin/cores?action=join&table_l=milton_Repository_patient_number_5070&table_r=milton_Repository_subject_5070&join_table_name=adfasd&field_l=PATIENT_NUMBER&field_rSUBJECT_ALIAS&select_cols={"l.PATIENT_NUMBER":"l_PATIENT_NUMBER","r.SUBJECT_ALIAS":"r_SUBJECT_ALIAS","r.SUBJECT_ID":"r_SUBJECT_ID"}
		// try {
		//
		// HashMap<String, String> alias_params = laco.getColumnAlias ();

		// solr_server.join ( )

		// } catch (JoinException e) {
		// e.printStackTrace();
		// String values = "";
		// ArrayList<String> lacs = laco.getLacs();
		// for (String l : lacs) {
		// values += "   " + l;
		// }
		// log.error("Join failed for : " + values);
		// }
		return "JOIN TABLE COMPLETE";
	}

	/**
	 * @deprecated
	 */
	private GResults join_dep(GResults _left, String _left_field,
							  Stack<String> _q, Stack<String> _right_field) {
		try {
			String lacs = _q.pop();
			String right_field = _right_field.pop();
			String[] lac = LAC.parse(lacs);
			GResults right = search(lac[0], lac[2], 0, 1000);
			DBConnectionManager db = new DBConnectionManager();
			TableManager tbservice = new TableManager(db);
			GResults r = tbservice.join(_left, _left_field, right, right_field);
			return r;
		} catch (EmptyStackException _ese) {
			return _left;
		} catch (JoinException e) {
			e.printStackTrace();
			return _left;
		}
	}

	/**
	 * This calls a join on the web service.
	 *
	 * @param _l
	 * @param _l_f
	 * @param _r
	 * @param _r_f
	 * @return
	 * @throws JoinException
	 */
	public String join(String _l, String _l_f, String _r, String _r_f,
					   String _join_table_name) throws JoinException {
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			// http://localhost:8983/solr/admin/cores?action=join&table_l=table1&field_l=hello&table_r=$table2&field_r&$field_right
			boolean bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=join&table_l=" + _l + "&field_l="
					+ _l_f + "&table_r=" + _r + "&field_r=" + _r_f
					+ "&join_table_name=" + _join_table_name);

		} catch (Exception _e) {
			throw new JoinException("Join failed... in the web service layer.");
		}
		return "Join in progress";

	}

	/**
	 * @param _l
	 * @param _l_field
	 * @param _r
	 * @param _r_field
	 * @return
	 */
	public GResults join(GResults _l, String _l_field, GResults _r,
						 String _r_field) throws JoinException {

		ArrayList<GRow> r_rows = _r.getValues();
		ArrayList<GRow> l_rows = _l.getValues();
		ResultMetaData lrd = _l.getResultDescriptor();
		ResultMetaData rrd = _r.getResultDescriptor();
		HashMap<String, String> left_fields = ResultsFactory.mapFields(
				_l.getTarget(), lrd.getFieldMap());
		HashMap<String, String> right_fields = ResultsFactory.mapFields(
				_r.getTarget(), rrd.getFieldMap());
		HashMap<String, String> left_types = ResultsFactory.mapTypes(
				_l.getTarget(), left_fields, lrd.getTypeMap());
		HashMap<String, String> right_types = ResultsFactory.mapTypes(
				_r.getTarget(), right_fields, rrd.getTypeMap());
		ResultMetaData merged_descriptor = createdMergedDescriptor(left_fields,
				left_types, right_fields, right_types);

		ArrayList<GColumn> col_p_l = _l.getColumns();
		ArrayList<GColumn> col_p_r = _r.getColumns();
		ArrayList<GColumn> col_p_m = new ArrayList<GColumn>();

		for (GColumn lp : col_p_l) {
			String nname = left_fields.get(lp.getName());
			lp.setName(nname);
			col_p_m.add(lp);
		}

		for (GColumn rp : col_p_r) {
			String nname = right_fields.get(rp.getName());
			rp.setName(nname);
			col_p_m.add(rp);
		}
		GResults r = new GResults();
		r.setTarget("");
		r.setType(null);
		r.setSuccessfulSearch(true);
		r.setResultDescriptor(merged_descriptor);
		r.setColumns(col_p_m);

		// this algrithm is not working we need to fix this
		// we first need get then we are going to be able to put this
		// into practise.
		ArrayList resultList = new ArrayList();
		for (GRow lrow : l_rows) {
			HashMap ldata = lrow.getData();
			Object ldata_object = ldata.get(_l_field);
			// we should probably make sure there is a way cast this

			for (GRow rrow : r_rows) {
				HashMap rdata = rrow.getData();
				Object rdata_object = rdata.get(_r_field);
				if (isEqual(ldata_object, rdata_object)) {
					resultList.add(buildResult(ldata, left_fields, rdata,
							right_fields));
				}
			}
			log.debug(" result size : " + resultList.size());

		}
		r.setValues(resultList);
		r.setTotalHits(resultList.size());
		return r;
	}

	private ResultMetaData createdMergedDescriptor(
			HashMap<String, String> left_fields,
			HashMap<String, String> left_types,
			HashMap<String, String> right_fields,
			HashMap<String, String> right_types) {

		HashMap<String, String> merged = new HashMap<String, String>();
		HashMap<String, String> merged_types = new HashMap<String, String>();

		Set<String> coll = left_fields.keySet();
		Set<String> colr = right_fields.keySet();

		for (String l : coll) {
			merged.put(left_fields.get(l), left_fields.get(l));
			merged_types.put(left_fields.get(l), left_types.get(l));
		}
		for (String r : colr) {
			merged.put(right_fields.get(r), right_fields.get(r));
			merged_types.put(right_fields.get(r), right_types.get(r));
		}
		ResultMetaData newr = new ResultMetaData();
		newr.setFieldMap(merged);
		newr.setTypeMap(merged_types);

		return newr;

	}

	/**
	 * This will build a result row by taking the union except the _joinField
	 *
	 * @param lrow
	 * @param rrow
	 * @param right_fields
	 * @return
	 */
	private GRow buildResult(HashMap lrow, HashMap<String, String> left_fields,
							 HashMap rrow, HashMap<String, String> right_fields) {
		GRow r = new GRow();

		Set<String> l_fields = lrow.keySet();
		Set<String> r_fields = rrow.keySet();

		for (String field : r_fields) {
			Object rrow_object = rrow.get(field);
			r.add(right_fields.get(field), rrow_object);
		}
		for (String field : l_fields) {
			Object l_object = lrow.get(field);
			r.add(left_fields.get(field), l_object);
		}
		return r;
	}

	private boolean isEqual(Object ldata_object, Object rdata_object) {
		if (ldata_object == null || rdata_object == null)
			return false;
		if (ldata_object instanceof Number && rdata_object instanceof Number) {
			Number ln = (Number) ldata_object;
			Number rn = (Number) rdata_object;
			if (ln == rn)
				return true;
		} else if (ldata_object instanceof Date) {
			Date ld = (Date) ldata_object;
			Date rd = (Date) rdata_object;
			if (ld.compareTo(rd) == 0)
				return true;
		} else {
			String ls = ldata_object.toString();
			String rs = rdata_object.toString();
			return ls.equalsIgnoreCase(rs);
		}

		return false;
	}

	public static String deleteAll(String _schema) {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			solr.deleteByQuery("*:*");
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return "Delete successful";
	}

	/**
	 * Will return 0 if this schema is valid. -1 if not found in the running
	 * solr instance.
	 *
	 * @param _schema
	 * @return
	 */
	public static int ping(String _schema) {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			SolrPingResponse resp = solr.ping();
			int status = resp.getStatus();
			return status;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return -1;
	}

	/**
	 * does not use beans
	 */
	public String delete(String _schema, ArrayList<GRow> rows) {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}

		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			for (int i = 0; i < rows.size(); i++) {
				HashMap data = rows.get(i).getData();
				String id = (String) data.get("TMID");
				String query = "TMID:" + id;
				solr.deleteByQuery(query);
			}
			solr.commit();

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return null;
	}

	/***
	 * Uses beans but can use solrdocuments
	 */
	public static String update(String _schema, ArrayList<GRow> _row) {
		log.debug("\n\n__________________________\nSaving the rows to the solr schema: "
				+ _schema + "\n\n__________________________");
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			for (int i = 0; i < _row.size(); i++) {
				System.out.println("TMID : " + _row.get(i).getData());
				GRow rr = _row.get(i);
				rr.set("TMID_lastUpdated", new Date());
			}
			UpdateResponse response = solr.add(getDocs(_row, _schema));
			System.out.println(" response: "
					+ response.getResponseHeader().toString());
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return "Update complete.";
	}

	/***
	 * Uses beans but can use solrdocuments
	 */
	public static String update(String _schema, Map<String, Object> row) {
		log.debug("\n\n__________________________\nupdate Saving the rows to the solr schema: "
				+ _schema + "\n\n__________________________");
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			String tmid = (String) row.get("TMID");
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("TMID", tmid);
			Set<String> keys = row.keySet();
			for (String k : keys) {
				if (!k.equalsIgnoreCase("TMID")) {
					Map<String, String> partialUpdate = new HashMap<String, String>();
					partialUpdate.put("set", row.get(k).toString());
					doc.addField(k, partialUpdate);
				}
			}
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			docs.add(doc);
			UpdateResponse response = solr.add(docs);
			System.out.println(" update : "
					+ response.getResponseHeader().toString());
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return "Update complete.";
	}

	/***
	 * Uses beans but can use solrdocuments
	 */
	public static String remove(String _schema, Map<String, Object> row) {
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			String tmid = (String) row.get("TMID");
			String q = "TMID:" + tmid;
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			UpdateResponse response = solr.deleteByQuery(q);
			System.out.println(" update : "
					+ response.getResponseHeader().toString());
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return "Update complete.";
	}

	/***
	 * Uses beans but can use solrdocuments
	 */
	private String add(String _schema, ArrayList<GRow> _row,
					   ArrayList<String> _fields) {
		log.debug("\n\n__________________________\nSaving the rows to the solr schema: "
				+ _schema + "\n\n__________________________");
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			ArrayList<GRow> copyR = new ArrayList<GRow>();
			for (int i = 0; i < _row.size(); i++) {
				// System.out.println("TMID : " + _row.get(i).getData());
				GRow r = _row.get(i);
				GRow rr = new GRow();
				HashMap data = r.getData();
				rr.set("TMID_lastUpdated", new Date());
				rr.set("TMID", TMID.create());

				Set<String> keys = data.keySet();
				for (String f : _fields) {
					for (String rf : keys) {
						if (f.equals(rf)) {
							rr.set(f, data.get(rf));
						}
					}
				}
				copyR.add(rr);
			}
			UpdateResponse response = solr.add(getDocs(copyR, _schema));
			System.out.println(" response: "
					+ response.getResponseHeader().toString());
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return "Update complete.";
	}

	private String addRows(String _schema, ArrayList<GRow> _row) {
		log.debug("\t\t\t Adding to schema: " + _schema);
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			UpdateResponse response = solr.add(getDocs(_row, _schema));
			// response.getStatus();
			return solr.commit().toString();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return null;
	}

	/**
	 * Uses beans but does not have to.
	 */
	public String add(String _schema, ArrayList<GRow> _row) {

		return addRows(_schema, _row);

	}

	void remove_me(String _schema, ArrayList<GRow> _row) {
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			for (GRow r : _row) {
				HashMap map = r.getData();
				SolrInputDocument sid = new SolrInputDocument();
				Set<String> ma_set = (Set<String>) map.keySet();
				for (String ma : ma_set) {
					String ta = (String) map.get(ma);
					sid.setField(ma, ta);
				}
				UpdateResponse response = solr.add(sid);
			}
			UpdateResponse repo = solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public static void add(ArrayList row, String _schema) {
		String default_url = GB.getDefaultURL();

	}

	/**
	 * Add a row to the currently selected table
	 *
	 * @param row
	 */
	public void add(ArrayList row) {

	}

	public static void add(ArrayList row, String solr_url, String _schema) {
		HttpSolrClient solr = null;
		try {
			ArrayList<GColumn> list = describeCore(_schema);
			ArrayList<GColumn> gcolumns = new ArrayList<GColumn>();
			for (GColumn col : list) {
				if (inReservedList(col.getName())) {
				} else {
					gcolumns.add(col);
				}
			}

			String gbsolr = "";
			for (GColumn c : gcolumns) {
				gbsolr += "" + c.getName() + "\t";
			}
			GB.print("Inserting into : " + gbsolr);

			LinkedHashMap<String, Object> ol = new LinkedHashMap<String, Object>();
			for (int index = 0; index < row.size(); index++) {
				Object ob = row.get(index);
				GColumn col = gcolumns.get(index);
				ol.put(col.getName(), ob);
			}

			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			SolrInputDocument sid = new SolrInputDocument();
			Set<String> keys = ol.keySet();
			String tmid = TMID.create();
			for (String k : keys) {
				String name = k;
				if (name.equalsIgnoreCase("TMID")) {
					tmid = ol.get(name).toString();
				} else {
					Object value = ol.get(name);
					GB.print(name + " : " + value.toString());
					sid.setField(name, value);
				}
			}
			// add the db overhead ids.
			sid.setField("TMID_lastUpdated", new Date());
			sid.setField("TMID", tmid);

			solr.add(sid);
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public static void add(Map row, String _schema) {
		add(row, GB.getDefaultURL(), _schema);

	}

	public static void add(Map row, String solr_url, String _schema) {
		HttpSolrClient solr = null;
		try {
			ArrayList<GColumn> list = describeCore(_schema);
			ArrayList<GColumn> gcolumns = new ArrayList<GColumn>();
			for (GColumn col : list) {
				if (inReservedList(col.getName())) {
				} else {
					gcolumns.add(col);
				}
			}

			String gbsolr = "";
			for (GColumn c : gcolumns) {
				gbsolr += "" + c.getName() + "\t";
			}
			GB.print("Inserting into : " + gbsolr);

			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			SolrInputDocument sid = new SolrInputDocument();
			Set<String> keys = row.keySet();
			String tmid = TMID.create();
			for (String k : keys) {
				String name = k;
				if (name.equalsIgnoreCase("TMID")) {
					tmid = row.get(name).toString();
				} else {
					Object value = row.get(name);
					GB.print(name + " : " + value.toString());
					sid.setField(name, value);
				}
			}
			// add the db overhead ids.
			sid.setField("TMID_lastUpdated", new Date());
			sid.setField("TMID", tmid);

			solr.add(sid);
			solr.commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public static boolean inReservedList(String name) {
		return name.equals("TMID") || name.equals("TMID_lastUpdated")
				|| name.endsWith("__900807");
	}

	private static SolrInputDocument getDoc(Map<String, Object> _d) {
		SolrInputDocument doc = new SolrInputDocument();
		Set<String> keys = _d.keySet();
		for (String key : keys) {
			Object value = _d.get(key);
			doc.addField(key, value);
		}
		Date udate = new Date();
		doc.setField("TMID_lastUpdated", udate);
		return doc;
	}

	/**
	 * does not use beans
	 *
	 * @param _row
	 * @return
	 */
	private static ArrayList<SolrInputDocument> getDocs(ArrayList<GRow> _row,
														String _schema) {
		ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int i = 0; i < _row.size(); i++) {
			SolrInputDocument sid = new SolrInputDocument();
			GRow r = _row.get(i);
			HashMap mas = r.getData();
			Set keys = mas.keySet();
			for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
				String key = (String) iterator.next();
				if ((!key.equals("delete")) && (!key.equals("save"))
						&& (!key.equals("_checkboxField"))) {
					sid.addField(key, mas.get(key));
				}
			}
			if (sid.getField("TMID_lastUpdated") != null) {
				Date udate = new Date();
				sid.setField("TMID_lastUpdated", udate);
			}
			if (sid.getField("TMID") == null) {
				sid.addField("TMID", TMID.create());
			}
			if (sid.getField("TMID_lastUpdated") == null) {
				Date udate = new Date();
				sid.addField("TMID_lastUpdated", udate);
			}

			docs.add(sid);
		}
		return docs;
	}

	/**
	 * Does not use beans... not sure I use this method any more. Please note:
	 * this is not a case sensitive method: _searchString =
	 * _searchString.toLowerCase();\
	 */
	public String delete(String _schema, String _query) {
		log.debug("removing entries: schema: " + _schema);
		_query = _query.toLowerCase();

		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}

		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			UpdateResponse response = solr.deleteByQuery(_query);

			solr.commit();
			return "" + response.getStatus();

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return null;
	}

	public void check() {
		File f = new File(".");
		System.out.println("\n\n\n\n the location of the file is : \n"
				+ f.getAbsolutePath());

	}

	/**
	 * does not use beans This provides a hit count for searching each solr
	 * core.
	 */
	public Integer hitCount(String _type, String _schema, String _searchString) {
		HttpSolrClient solr = null;
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}

			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + _searchString);
			params.set("wt", "xml");
			XMLResponseParser pars = new XMLResponseParser();

			solr.setParser(pars);
			QueryResponse response = solr.query(params);
			System.out.println(_searchString + "  response = "
					+ response.getResults().size());

			SolrDocumentList list = response.getResults();
			NamedList tresponse = response.getResponse();
			SolrDocumentList response_object = (SolrDocumentList) tresponse
					.get("response");
			long numfound = response_object.getNumFound();
			return (new Integer((int) numfound));
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(solr);
		}
		return 0;
	}

	/**
	 * Get the libraries
	 */
	public ArrayList<TTable> getLibraries() {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			List list = criteria.list();
			ArrayList<TTable> slist = new ArrayList<TTable>();
			for (int i = 0; i < list.size(); i++) {
				TTable item = (TTable) list.get(i);
				slist.add(HibernateToCoreJava.convert(item));
			}

			hibernateSession.close();
			return slist;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return new ArrayList<TTable>();
	}

	public ArrayList<TTable> getOpenLibraries(String _userid) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("user", _userid));

			Criterion open = Restrictions.eq("securityStatus", "1.png");
			Criterion prot = Restrictions.eq("securityStatus", "2.png");
			LogicalExpression orExp = Restrictions.or(open, prot);
			criteria.add(orExp);

			List list = criteria.list();
			ArrayList<TTable> slist = new ArrayList<TTable>();
			for (int i = 0; i < list.size(); i++) {
				TTable item = (TTable) list.get(i);
				slist.add(HibernateToCoreJava.convert(item));
			}
			return slist;

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return new ArrayList<TTable>();
	}

	public TTable getLibrary(String _itemID) {
		Session hibernateSession = null;
		try {
			Integer itemID = Integer.parseInt(_itemID);
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("itemID", itemID));
			List list = criteria.list();
			TTable item = null;
			for (int i = 0; i < list.size(); i++) {
				item = (TTable) list.get(i);
				item = HibernateToCoreJava.convert(item);
			}
			return item;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	private TTable getLibraryBySchema(String _schema_name) {
		Session hibernateSession = null;
		try {
			String user = null;
			String title = _schema_name;
			if (_schema_name.contains("_Repository_")) {
				user = NameUtiles.stripUser(_schema_name);
				title = NameUtiles.getTitle_dep(_schema_name);
			}
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("title", title));
			if (user != null)
				criteria.add(Restrictions.eq("user", user));

			List list = criteria.list();
			TTable item = null;

			ArrayList<TTable> remove_these = new ArrayList<TTable>();

			if (list.size() > 0) {
				item = (TTable) list.get(0);
				item = HibernateToCoreJava.convert(item);
			}
			if (list.size() > 1) {
				for (int i = 1; i < list.size(); i++) {
					TTable lib = (TTable) list.get(i);
					hibernateSession.delete(lib);
				}
				hibernateSession.getTransaction().commit();
			}
			return item;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	/**
	 * Get the libraries for the user (does not use beans) (non-Javadoc)
	 */
	public ArrayList<TTable> getLibraries(String _userid) {
		// {{ BELOW IS THE OLD MECHANISM }}
		Session hibernateSession = null;
		try {

			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("user", _userid));
			List list = criteria.list();
			ArrayList<TTable> slist = new ArrayList<TTable>();
			for (int i = 0; i < list.size(); i++) {
				TTable item = (TTable) list.get(i);

				slist.add(HibernateToCoreJava.convert(item));
			}
			return slist;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return new ArrayList<TTable>();
	}

	//

	public String removeCore(String _userid, String _schema) {
		Session hibernateSession = null;
		HttpSolrClient solr = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("user", _userid));
			criteria.add(Restrictions.eq("title", _schema));
			List list = criteria.list();
			for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
				TTable olist = (TTable) iterator.next();
				hibernateSession.delete(olist);
			}
			hibernateSession.getTransaction().commit();

			String name = NameUtiles.prepend(_userid, _schema);

			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			String cor = solr_url;
			solr = new HttpSolrClient.Builder(cor).build();
			CoreAdminRequest request = new CoreAdminRequest();
			CoreAdminAction co = CoreAdminAction.UNLOAD;
			request.setAction(co);
			request.setCoreName(name);
			request.process(solr);

			return "Core removed successfully.";

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
			IOUTILs.closeResource(solr);
		}

		return "Failed to remove core.";
	}

	public Boolean setSecurityLevel(String _userid, String schema, Integer ift) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("user", _userid));
			criteria.add(Restrictions.eq("title", schema));
			List olist = criteria.list();
			for (int i = 0; i < olist.size(); i++) {
				TTable it = (TTable) olist.get(i);
				it.setSecurityStatus(ift.toString() + ".png");
				hibernateSession.update(it);
			}
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
			return false;
		} finally {
			HBConnect.close(hibernateSession);
		}
		return true;
	}

	// @deprecated
	// private String parseCore(String line) {
	//
	// String st = "<core name='";
	// String st2 = "' instanceDir=";
	// int index = line.indexOf(st);
	// int lastIndex = line.lastIndexOf(st2);
	//
	// if (index >= 0) {
	// String name = line.substring(index + 12, lastIndex);
	// System.out.println(" name : " + name);
	// return name;
	// }
	// return null;
	// }
	//

	/**
	 * This is where we are going to get the schema given a TMLib ID.
	 *
	 * @param _long
	 * @return
	 */
	public String getSchema(double _long) {
		String tl = "";
		return tl;
	}

	private static ArrayList<GColumn> describeCore(String _solrCore)
			throws ConnectException {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		return TMSolrServer.describeCore(solr_url, _solrCore);
	}

	public GResults search(String _user, String _volume, String _query) {
		String schema = NameUtiles.prepend(_user, _volume);
		return search(schema, _query, 0, 200);
	}

	public TTable extrudeTable(String _user_id, String _schema,
							   String _searchString, String _toSchema, ArrayList<String> _fields,
							   int _start, int _rows) {

		String f = _fields.get(0);
		return distinctBuild(_user_id, _schema, _searchString, f, _start,
				_rows, _toSchema);
	}

	public GResults distinct(String _user_id, String _schema,
							 String _searchString, ArrayList<String> _fields, int _start,
							 int _rows) {
		HttpSolrClient solr = null;
		try {
			// first is to make sure the 900807 fields are available for
			// all the _fields above.
			// String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			// if (!solr_url.endsWith("/")) {
			// solr_url += "/";
			// }
			log.debug("_searchString: " + _searchString);
			_searchString = _searchString.replaceAll(" and ", " AND ");
			_searchString = _searchString.replaceAll(" not ", " NOT ");
			_searchString = _searchString.replaceAll(" or ", " OR ");
			String url = ABProperties.get(ABProperties.SOLRSITE);
			if (!url.endsWith("/"))
				url += "/";

			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
			solr = new HttpSolrClient.Builder(url + _schema).build();
			if (_searchString == null || _searchString.length() <= 0)
				_searchString = "*:*";

			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + _searchString);
			params.set("start", _start);
			params.set("rows", _rows);
			// params.set("sort", "TMID_lastUpdated desc");
			params.set("facet", true);// &facet=true&facet.field=ca
			for (String field : _fields) {
				params.add("facet.field", field);
			}
			params.set("facet.limit", _rows);
			params.set("facet.mincount", 1);
			params.set("wt", "xml");
			XMLResponseParser pars = new XMLResponseParser();
			solr.setParser(pars);
			log.debug("Loading the XML parser"
					+ params.getParameterNames().toString());
			QueryResponse response = solr.query(params);
			// int numfound = response.getFacetFields().size();
			int numfound = 0;
			// log.debug("Launching: "
			// + solr.getHttpClient().getParams().toString());
			List<FacetField> fields = response.getFacetFields();

			int increment = _rows - _start;
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

			// BUID THE RESULT THROUGH THE RESULTS FACTORY
			log.debug("Building the results...");

			// Results results = ResultsFactory.buildResults(_schema, response);
			GResults results = new GResults();
			results.setSuccessfulSearch(true);
			results.setMessage("No values found");
			results.setFacet(f_results);
			results.setTotalHits(numfound);

			ArrayList<GColumn> desc = describeCore(_schema);
			if (desc == null || desc.size() <= 0) {
				GResults re = new GResults();
				re.setSuccessfulSearch(false);
				re.setMessage("Schema : "
						+ _schema
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
			log.debug("Search failed... null return");
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Failed to connect to the Bioinformatics database");
			return re;
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public TTable distinctBuild(String _user_id, String _schema,
								String _searchString, String _field, int _start, int _rows,
								String _toSchema) {
		HttpSolrClient solr = null;
		try {

			if (_field.endsWith("__900807"))
				_field = _field.substring(0, _field.length() - 8);

			log.setLevel(Level.DEBUG);
			TTable item = createTable(_schema, _toSchema, _user_id);
			log.debug("_searchString: " + _searchString);
			_searchString = _searchString.replaceAll(" and ", " AND ");
			_searchString = _searchString.replaceAll(" not ", " NOT ");
			_searchString = _searchString.replaceAll(" or ", " OR ");
			String url = ABProperties.get(ABProperties.SOLRSITE);
			if (!url.endsWith("/"))
				url += "/";

			String new_table = NameUtiles.prepend(_user_id, _toSchema);
			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
			solr = new HttpSolrClient.Builder(url + _schema).build();
			if (_searchString == null || _searchString.length() <= 0)
				_searchString = "*:*";
			int b_start = _start;
			int increment = 50000;

			ArrayList<GColumn> _schema_cols = describeCore(_schema);

			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + _searchString);
			// params.set("start", b_start);
			// params.set("rows", increment);
			// params.set("sort", _field + " desc");
			params.set("wt", "xml");
			XMLResponseParser pars = new XMLResponseParser();
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
						addRows(new_table, cached);
						cached = new ArrayList<GRow>();
					}

					index++;
				}

				if (cached.size() > 0) {
					addRows(new_table, cached);
					cached = new ArrayList<GRow>();
				}
			}
			return item;
		} catch (Exception _e) {
			_e.printStackTrace();
			return null;
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	private boolean equals(Object point_object, Object v, String type_object) {
		if (type_object.equalsIgnoreCase("string")
				|| type_object.equalsIgnoreCase("text")) {
			if (v == null)
				v = "";
			String po = point_object.toString();
			String vo = v.toString();
			if (vo.startsWith("60")) {
				System.out.println(" on");
			}
			po = po.trim();
			vo = vo.trim();
			if (po.equalsIgnoreCase(vo))
				return true;
		}
		return false;
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

	private GRow merge(String _schema, String _search_string, int _start,
					   int _count) {

		GResults rr = search(_schema, _search_string, 0, _count);
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

	public GResults search(String _schema, String _searchString, int _start,
						   int _rows) {
		return search(_schema, _searchString, _start, _rows, null);
	}

	/**
	 * Please note: this is not a case sensitive method... _searchString =
	 * _searchString.toLowerCase();
	 */
	public GResults search(String _schema, String _searchString, int _start,
						   int _rows, TMTableSettings _settings) {
		if (_schema.startsWith("http://")) {
			return searchCore(_schema, _searchString, _start, _rows, _settings);
		} else {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			log.debug("connecting... " + solr_url + _schema);

			return searchCore(solr_url + _schema, _searchString, _start, _rows,
					_settings);
		}

	}

	public GResults searchCore(String _solrCore, String _searchString,
							   int _start, int _rows, TMTableSettings _settings) {
		HttpSolrClient solr = null;
		try {
			String sortString = "TMID_lastUpdated desc"; // default
			if (_settings != null) {
				Map<String, String> props = _settings.getProperties();
				String sort_field = props.get("sort_field");
				String sort_direction = props.get("sort_direction");
				if (sort_field != null && sort_direction != null) {
					sortString = sort_field + " " + sort_direction;
				}
			}
			log.debug("\n\n\n");
			log.debug("_searchString: " + _searchString);
			log.debug("\n\n\n");
			// if (_searchString.indexOf(":") <= 0)
			// _searchString = _searchString.toLowerCase();
			// adjust the and or not
			_searchString = _searchString.replaceAll(" and ", " AND ");
			_searchString = _searchString.replaceAll(" not ", " NOT ");
			_searchString = _searchString.replaceAll(" or ", " OR ");

			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }}
			solr = new HttpSolrClient.Builder(_solrCore).build();

			if (_searchString == null || _searchString.length() <= 0)
				_searchString = "*:*";

			ModifiableSolrParams params = new ModifiableSolrParams();
			params.set("q", "" + _searchString);
			params.set("start", _start);
			params.set("rows", _rows);
			params.set("sort", sortString);
			params.set("wt", "xml");
			// params.set("facet", true);
			// params.set("facet.field", "location_exact",
			// "organ_exact", "type_exact", "disease_exact");
			//
			// params.set("facet.mincount", 1);
			XMLResponseParser pars = new XMLResponseParser();
			solr.setParser(pars);

			log.debug("Loading the XML parser"
					+ params.getParameterNames().toString());
//			solr.setAllowCompression(true);
			QueryResponse response = solr.query(params, METHOD.POST);

			// log.debug("Launching: "
			// + solr.getHttpClient().getParams().toString());

			// BUID THE RESULT THROUGH THE RESULTS FACTORY
			log.debug("Building the results...");
			GResults results = ResultsFactory.buildResults(_solrCore, _start,
					_rows, response);
			log.debug("Built the results ");
			log.debug("Get the descriptor core ");
			ArrayList<GColumn> desc = describeCore(_solrCore);
			if (desc == null || desc.size() <= 0) {
				GResults re = new GResults();
				re.setSuccessfulSearch(false);
				re.setMessage("Schema : "
						+ _solrCore
						+ " not found.  Failed to connect to the Bioinformatics database");
				return re;
			}
			results.setColumns(desc);
			return results;
		} catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
			_solrException.printStackTrace();
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Bioinformatics database error: "
					+ _solrException.getLocalizedMessage());
			return re;

		} catch (Exception _e) {
			_e.printStackTrace();
			log.debug("Search failed... null return");
			GResults re = new GResults();
			re.setSuccessfulSearch(false);
			re.setMessage("Failed to connect to the Bioinformatics database");
			return re;
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public TableDescriptor saveLibraryType(String _source_type,
										   int _ab_table_id, String _schema) {
		try {
			TableDescriptor sd = new TableDescriptor();
			try {
				ArrayList<GColumn> props = describeCore(_schema);
				sd.setColumns(props);
			} catch (ConnectException _ce) {
				_ce.printStackTrace();
				sd.setSchema_found(false);
				sd.setMsg("Failed to connect to the schema: " + _schema);
				return sd;
			}
			sd.setCoreName(_schema);
			Session hibernateSession = null;
			try {
				hibernateSession = dbcm.getSession();
				hibernateSession.beginTransaction();
				Criteria cer = hibernateSession.createCriteria(TTable.class);
				cer.add(Restrictions.eq("itemID", _ab_table_id));
				List list = cer.list();
				if (list != null && list.size() > 0) {
					Object object = list.get(0);
					if (object != null) {
						TTable tm = (TTable) object;
						tm.setSourceType(_source_type);
						tm = HibernateToCoreJava.convert(tm);
						TTable cp = (TTable) HibernateToCoreJava.deepCopy(tm);
						sd.setLibraryDescriptor(cp);
						sd.setSchema_found(true);
						sd.setMsg("Connected to the bioinformatics database.");

						hibernateSession.getTransaction().commit();

					}
				}
			} catch (Exception _ee) {
				_ee.printStackTrace();
				sd.setSchema_found(false);
				sd.setMsg("Failed to connect to the Bioinformatics database.  : "
						+ _ee.getLocalizedMessage());
			} finally {
				HBConnect.close(hibernateSession);
			}
			return sd;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public TTable getLibraryItem(String _title) {
		String name = NameUtiles.strip(_title);
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", name));
			List list = cer.list();
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					TTable tm = (TTable) object;
					tm = HibernateToCoreJava.convert(tm);
					hibernateSession.close();
					return tm;
				}
			}
			return null;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	/**
	 * Uses beans
	 */
	public TableDescriptor loadSchema(String _schema) {
		Session hibernateSession = null;
		try {

			// we have found a proxy object.. and need to convert this into an
			// actual schema
			if (_schema.startsWith("com.tissuematch.tm3.mylib.TMLibraryItem")) {
				String last_item = LAC.parseLastTarget(_schema);
				Integer itemID = Integer.parseInt(last_item);
				TTable tm = getTable(itemID);
				if (tm != null) {
					String title = tm.getTitle();
					String user = tm.getUser();
					String schema = NameUtiles.prepend(user, title);

					log.debug(" library item title : :" + schema);
					_schema = schema;
				}
			}

			TableDescriptor sd = new TableDescriptor();
			try {
				ArrayList<GColumn> props = describeCore(_schema);
				sd.setColumns(props);
			} catch (ConnectException _ce) {
				_ce.printStackTrace();
				sd.setSchema_found(false);
				sd.setMsg("Failed to connect to the schema: " + _schema);
				return sd;
			}

			sd.setCoreName(_schema);
			String name = NameUtiles.strip(_schema);

			try {
				hibernateSession = dbcm.getSession();
				hibernateSession.beginTransaction();
				Criteria cer = hibernateSession.createCriteria(TTable.class);
				cer.add(Restrictions.eq("title", name));
				List list = cer.list();
				if (list != null && list.size() > 0) {
					Object object = list.get(0);
					if (object != null) {
						TTable tm = (TTable) object;
						tm = HibernateToCoreJava.convert(tm);
						sd.setLibraryDescriptor(tm);
						sd.setMsg("Schema found... loading.");
						sd.setSchema_found(true);
					}
				}

				if (!sd.isSchema_found()) {
					TTable litem = new TTable();
					litem.setDescription("");
					litem.setTitle(name);

					litem.setLastEdited(new Date());
					litem.setSecurityStatus("3" + ".png");
					litem.setUser("");
					litem.setSourceType(SourceType.DB.name);
					save(litem);
					sd.setLibraryDescriptor(litem);
					sd.setSchema_found(true);
					litem = HibernateToCoreJava.convert(litem);
					sd.setLibraryDescriptor(litem);
					sd.setSchema_found(true);
					return sd;

				}
			} catch (Exception _ee) {
				_ee.printStackTrace();
			}
			return sd;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		TableDescriptor sd = new TableDescriptor();
		sd.setSchema_found(false);
		sd.setMsg("Failed to connect to the schema: " + _schema);
		return sd;
	}

	public String getSpecimenDetails(String specimenId) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("itemID", specimenId));
			List list = cer.list();
			Object object = list.get(0);

			if (object != null) {
				TTable tm = (TTable) object;
				String descr = tm.getDescription();
			}
			System.out.println(" list : " + list.size());
		} catch (Exception _ee) {
			_ee.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	public String saveURIList(ArrayList<TMURI> libs) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			for (int i = 0; i < libs.size(); i++) {
				TMURI tmuri = libs.get(i);

				hibernateSession.saveOrUpdate(tmuri);
			}
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Save Failed";
		} finally {
			HBConnect.close(hibernateSession);
		}
		return "Saved";
	}

	/**
	 * Retreive the list of uris for a particular owner
	 *
	 * @param _owner
	 * @return
	 */
	public ArrayList<TMURI> getTMURIList(String _owner, String _connectionType) {
		ArrayList<TMURI> list = new ArrayList<TMURI>();
		Session hibernateSession = null;
		try {
			try {
				hibernateSession = dbcm.getSession();
				hibernateSession.beginTransaction();

				Criteria cer = hibernateSession.createCriteria(TMURI.class);
				cer.add(Restrictions.eq("owner", _owner));
				cer.add(Restrictions.eq("connectionType", _connectionType));
				List queryList = cer.list();
				for (int i = 0; i < queryList.size(); i++) {
					TMURI tr = (TMURI) queryList.get(i);
					list.add(tr);
				}
			} catch (Exception _ee) {
				_ee.printStackTrace();
			}
		} catch (Exception _e) {
			_e.printStackTrace();
			return new ArrayList<TMURI>();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return list;
	}

	private String truncate(String _uri) {
		String t = "";
		String[] c = _uri.split(":");
		for (int i = 0; i < c.length - 1; i++) {
			t += c[i] + ":";
		}
		t = t.substring(0, t.length() - 1);
		return t;
	}

	/**
	 * We ned to create the index given a name and solr home server.
	 */
	public LoadStatus createIndexFromName(String _name, String _description,
										  String _userID, String _solr_home_url) {
		LoadStatus ls = new LoadStatus();
		TTable litem = new TTable();
		litem.setDescription(_description);
		litem.setLastEdited(new Date());
		litem.setSecurityStatus("1");
		litem.setUser(_userID);
		litem.setSourceType("default");
		litem.setTitle(_name);
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			hibernateSession.saveOrUpdate(litem);
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		ls.setMsg("Link created to " + _name);
		return ls;
	}


	/**
	 *  Uses the custome Admin action "rm" instead of unload and delete index
	 *  12/26/2017
	 * @param core
	 */
	public static void delete(String core) {
		String solr_url = ABProperties.getSolrURL();
		// this often fails with the delete index and data directory set to true.. so we
		// will need to work around this
//		String call_solr = solr_url + "admin/cores?action=UNLOAD&core=" + core
//				+ "&deleteIndex=true&deleteDataDir=true";
		String call_solr = solr_url + "admin/cores?action=rm&core=" + core;
		try {
			TMSolrServer.callSolr(call_solr);
			GB.print("Core removed.");
		} catch (SolrCallException e) {
//			e.printStackTrace();
			GB.print("Core " + core + " failed to execute delete function .");
		}
	}
	public static void rename(String core, String name) {
		String solr_url = ABProperties.getSolrURL();
//		solr/admin/cores?action=RENAME&core=core0&other=core5
		String call_solr = solr_url + "admin/cores?action=RENAME&core=" + core + "&" + name;
		try {
			TMSolrServer.callSolr(call_solr);
			GB.print("Core removed.");
		} catch (SolrCallException e) {
//			e.printStackTrace();
			GB.print("Core " + core + " not available to remove.");
		}
	}

	public static void swap(String core, String core2) {
		String solr_url = ABProperties.getSolrURL();
		String call_solr = solr_url + "admin/cores?action=SWAP&core="+core+"&other=" + core2;
		try {
			TMSolrServer.callSolr(call_solr);
			GB.print("Core swapped.");
		} catch (SolrCallException e) {
//			e.printStackTrace();
			GB.print("Core " + core + " not available to remove.");
		}
	}


	public static String createSchema(ArrayList<GColumn> cols, String user, String path) {


		HashMap<String, Map<String, String>> _params = new HashMap<String, Map<String, String>>();
		for (GColumn cor : cols) {
			String or = cor.getName();
			// do not copy the copy fields.
			if (!or.endsWith("__900807")) {
				String data_type = cor.getType();
				HashMap<String, String> uuidp = new HashMap<String, String>();
				uuidp.put("fieldName", or);
				uuidp.put("sortable", "true");
				uuidp.put("indexed", "true");
				uuidp.put("defaultString", "");
				uuidp.put("dataType", data_type);
				uuidp.put("requiredField", "false");
				_params.put(or, uuidp);
			}
		}

		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
		UUID idOne = UUID.randomUUID();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", idOne.toString());
		uuidp.put("dataType", "text");
		uuidp.put("requiredField", "true");
		_params.put("TMID", uuidp);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");


		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
		LocalDate date = LocalDate.now();
		String text = date.format(formatter);


		last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
		last_updated.put("dataType", "date");
		last_updated.put("requiredField", "true");
		_params.put("TMID_lastUpdated", last_updated);

		String core_name = NameUtiles.convertToValidCharName(path);
		String site = ABProperties.getSolrURL();
		String value = TMSolrServer.createSchema(user, site, core_name,
				_params, false);
		return value;

	}


	public TTable createLibraryItem(String _name, String _userID,
									String _solr_home_url) {
		TTable litem = new TTable();
		litem.setDescription("");
		litem.setLastEdited(new Date());
		litem.setSecurityStatus("1");
		litem.setUser(_userID);
		litem.setSourceType("default");
		litem.setTitle(_name);
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			hibernateSession.saveOrUpdate(litem);
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return litem;
	}

	public LoadStatus indexBlobs(String _user, String _tableName) {
		return new LoadStatus();
	}

	/**
	 * @deprecated Chron stuff is not working at the moment.. .method should not
	 * be referenced in gb code for the time being.
	 */
	public LoadStatus createIndex(String _user, String _tablename,
								  String _query, Map<String, String> config,
								  Map<String, Map<String, String>> _schema_config,
								  Map<String, String> chron_details) {

		int job_id = InMemoryJobManager.start();
		InMemoryJobManager.log(job_id, "Building the table : " + _tablename);

		SQLToSolr sql_solr = new SQLToSolr();
		String type = config.get("query_source");
		if (type == null)
			type = "local_dev";
		log.debug("query source : " + type);

		DBConfiguration db_configuration = DBConfigFactory.createConfig(type,
				config);

		Map<String, String> db_config = db_configuration.getProperties();
		// apply the users value here.
		String end_value = config.get("end");
		if (end_value != null)
			db_config.put("end", end_value);

		String solrRoot = ABProperties.get("solrSite");
		String chron = chron_details.get("chron");
		if (chron != null && (!chron.equalsIgnoreCase("none"))) {
			InMemoryJobManager
					.log(job_id, "Chron object saved for : " + _query);
			// create a schedule object
			TMLibBuilder builder = new TMLibBuilder();
			builder.setDatasource(solrRoot);
			builder.setQuery(_query);
			builder.setTable_name(_tablename);
			builder.setUser(_user);
			// now we need to set the appropriate schedule
			TMLibSchedule sch = TMSchedulerFactory.getSchedule(chron);
			long id = sch.getSchedule_id();
			builder.setSchedule(id);
			builder.setSchedule_name(sch.getName());
			save(builder);
			sch.start();
			InMemoryJobManager.log(job_id, "Chron object started (" + id + ")");
		}

		// SECOND IS TO RUN THE BUILD }}
		LoadStatus l = new LoadStatus();
		l.setJobId(job_id);
//
//		try {
//			String res = sql_solr.run(_user,path, _tablename,
//					db_configuration.getName(), db_config, _schema_config,
//					_query, job_id + "" + new Date(), new GBJobListener() {
//						public void jobComplete(String msg) {
//						}
//					});
//			l.setMsg(res);
//		} catch (DBProcessFailedException e) {
//			e.printStackTrace();
//			l.setMsg(e.getLocalizedMessage());
//		}
		return l;
	}

	public String poll(int _in_memory_job_status) {

		try {
			String line = InMemoryJobManager.readLine(_in_memory_job_status);
			return line;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Job not found for job_id:" + _in_memory_job_status;
	}

	/**
	 * This will save the TMLibBuilder Object. It does not replace existing
	 * values
	 *
	 * @param lib_builder
	 */
	private void save(TMLibBuilder lib_builder) {
		Session sess = null;
		try {
			sess = dbcm.getSession();
			sess.beginTransaction();
			Criteria c = sess.createCriteria(TMLibBuilder.class);
			c.add(Restrictions.eq("user", lib_builder.getUser()));
			c.add(Restrictions.eq("datasource", lib_builder.getDatasource()));
			c.add(Restrictions.eq("table_name", lib_builder.getTable_name()));
			List l = c.list();
			if (l.size() > 0) {
				TMLibBuilder inb = (TMLibBuilder) l.get(0);
				inb.setBuilder_type(lib_builder.getBuilder_type());
				inb.setPrps(lib_builder.getPrps());
				inb.setQuery(lib_builder.getQuery());
				inb.setSchedule(lib_builder.getSchedule());
				inb.setSchedule_name(lib_builder.getSchedule_name());
				sess.update(inb);
				sess.flush();
			} else
				sess.save(lib_builder);
			sess.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(sess);
		}
	}

	// http://localhost:8983/solr/admin/cores
	public String importLibrariesFromSolrInstance(String _url, String _path) {
		Session session = null;
		try {
			if (_url != null && _url.length() > 0)
				_url = _url.trim();
			URL solr = new URL(_url);
			URLConnection yc = solr.openConnection();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(yc.getInputStream());
			NodeList nl = dom.getElementsByTagName("response");
			int node_length = nl.getLength();

			ArrayList<String> libs = new ArrayList<String>();

			if (nl != null && node_length > 0) {
				for (int i = 0; i < node_length; i++) {
					Element el = (Element) nl.item(i);
					NodeList statusNode = el.getChildNodes();
					int core_length = statusNode.getLength();
					for (int j = 0; j < core_length; j++) {
						Node cc = statusNode.item(j);
						String name_ = cc.getNodeName();
						if (cc.hasAttributes()) {
							NamedNodeMap nm = cc.getAttributes();
							String st_test = nm.getNamedItem("name")
									.getNodeValue();
							if (st_test.equalsIgnoreCase("status")) {
								NodeList cores = cc.getChildNodes();
								int c_length = cores.getLength();
								for (int k = 0; k < c_length; k++) {
									Node corenode = cores.item(k);
									System.out.println(" corenodename : "
											+ corenode.getNodeName());
									if (corenode.hasAttributes()) {
										NamedNodeMap nm2 = corenode
												.getAttributes();
										String st_test2 = nm2.getNamedItem(
												"name").getNodeValue();
										libs.add(st_test2);
										// we have a name:

									}
								}
							}
						}
						System.out.println(" name : " + name_);
						System.out
								.println(" attributes: " + cc.hasAttributes());
						System.out.println(" children : " + cc.hasChildNodes());
					}

				}

			}

			session = dbcm.getSession();
			session.beginTransaction();

			for (String v : libs) {
				System.out.println(" lib : " + v);
				String[] spl = v.split("_Repository_");
				if (spl == null || spl.length < 2) {
					log.debug("Failed to import this core because there was no user assigned (i.e. _Repository_).  "
							+ "It does not appear to be an HTL Core.: " + v);
				} else {
					TTable tm = new TTable();
					tm.setServer(_url);
					tm.setPath(_path);
					tm.setTitle(spl[1].trim());
					tm.setDescription("Imported library");
					tm.setLastEdited(new Date());
					tm.setUser(spl[0]);
					tm.setSourceType("unknown");
					session.save(tm);
				}
			}
			session.getTransaction().commit();

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			HBConnect.close(session);
		}

		return "Import complete";
	}

	/*
	 * this is wehre we are going to add the column (non-Javadoc)
	 * 
	 * @see com.tissuematch.config.client.TMDataBuilderService#addColumn(int,
	 * java.lang.String, java.lang.String)
	 */
	public String addColumn(int _lib_item_id, String _field_name, String _type) {
		Session hibernateSession = null;
		String msg = "ERROR saving table settings";
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cr = hibernateSession.createCriteria(TTable.class);
			cr.add(Restrictions.eq("itemID", _lib_item_id));
			List ls = cr.list();
			if (ls.size() <= 0) {
				return "Failed to find the table.";
			} else {
				TTable li = (TTable) ls.get(0);

				String solr_url = ABProperties.get(ABProperties.SOLRSITE);
				String _schema = li.getTitle();
				String site = solr_url;
				if (GBLinkManager.isFullyQualifiedURL(_schema)) {
					site = GBLinkManager.getSolrRoot(_schema);
					_schema = GBLinkManager.getCoreLK(_schema);
				}

				// http://localhost:8983/solr/admin/cores?action=add_field&table=milton_Repository_v_c_studies&field_name=hello&field_type=sint
				boolean bb = TMSolrServer
						.callSolr(site + "/admin/cores?action=add_field&table="
								+ _schema + "&field_name=" + _field_name
								+ "&field_type=" + _type);
				TMTableSettings sets = li.getSettings();
				String state = sets.getState();
				Gson sog = new Gson();
				ArrayList mmls = new ArrayList();
				if (state != null) {
					mmls = sog.fromJson(state, ArrayList.class);
					java.util.LinkedHashMap lh = new LinkedHashMap<String, Object>();
					lh.put("name", _field_name);
					lh.put("width", new Double(200));
					mmls.add(1, lh);
				} else {
					java.util.LinkedHashMap lh = new LinkedHashMap<String, Object>();
					lh.put("name", _field_name);
					lh.put("width", new Double(200));
					mmls.add(lh);
				}
				String newjson = sog.toJson(mmls);
				sets.setState(newjson);
				saveTableSettings(sets);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to add field";
		} finally {
			HBConnect.close(hibernateSession);
		}
		return "Field Added";

	}

	protected static boolean callURL(String url) {
		URL u = null;
		try {
			u = new URL(url);
			URLConnection uc = u.openConnection();
			uc.setUseCaches(true);
			InputStream is = uc.getInputStream();
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// http://localhost:8983/solr/admin/cores?action=field_change&schema=milton_Repository_HTL&orig=CMU&dest=GNE
	public String renameField(String _schema, String _original, String _to_name) {

		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("action", "field_change");
		params.set("schema", _schema);
		params.set("orig", _original);
		params.set("dest", _to_name);
		solr_url += "admin/cores?action=field_change&schema=" + _schema
				+ "&orig=" + _original + "&dest=" + _to_name;
		callURL(solr_url);
		return "Field has been changed.";
	}

	public String renameTable(String _schema, String _newName, Integer _id) {
		Session hibernateSession = null;
		try {

			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			// http://localhost:8983/solr/admin/cores?action=RENAME&core=core0&other=core5
			boolean bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=RENAME&core=" + _schema + "&other="
					+ _newName);

			// http://localhost:8983/solr/admin/cores?action=RELOAD&core=core0
			bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=RELOAD&core=" + _newName);

			if (bb) {
				hibernateSession = dbcm.getSession();
				hibernateSession.beginTransaction();
				Criteria cer = hibernateSession.createCriteria(TTable.class);
				cer.add(Restrictions.eq("itemID", _id));
				List list = cer.list();
				Object object = list.get(0);
				if (object != null) {
					TTable tm = (TTable) object;
					tm.setTitle(_newName);
					hibernateSession.saveOrUpdate(tm);
					hibernateSession.flush();
					hibernateSession.getTransaction().commit();
				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return "Name changed";
	}

	/**
	 * Create a table from a table.
	 *
	 * @param _name
	 * @param _newTable
	 * @return
	 */
	public TTable createTable(String _name, String _newTable, String _user) {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}

		ArrayList<GColumn> cols = new ArrayList<GColumn>();
		try {
			cols = describeCore(_name);
		} catch (ConnectException e) {
			e.printStackTrace();
		}

		HashMap<String, Map<String, String>> _params = new HashMap<String, Map<String, String>>();
		log.debug("Building...");
		for (GColumn cor : cols) {
			String or = cor.getName();
			// do not copy the copy fields.
			if (!or.endsWith("__900807")) {
				String data_type = cor.getType();
				HashMap<String, String> uuidp = new HashMap<String, String>();
				uuidp.put("fieldName", or);
				uuidp.put("sortable", "true");
				uuidp.put("indexed", "true");
				uuidp.put("defaultString", "");
				uuidp.put("dataType", data_type);
				uuidp.put("requiredField", "false");
				_params.put(or, uuidp);
			}
		}

		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
		UUID idOne = UUID.randomUUID();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", idOne.toString());
		uuidp.put("dataType", "text");
		uuidp.put("requiredField", "true");
		_params.put("TMID", uuidp);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");
		last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
		last_updated.put("dataType", "date");
		last_updated.put("requiredField", "true");
		_params.put("TMID_lastUpdated", last_updated);
		log.debug("Generating the solr schema... NAME: " + _newTable);

		String new_table = NameUtiles.prepend(_user, _newTable);
		String site = ABProperties.getSolrURL();

		String value = TMSolrServer.createSchema(_user, site, new_table,
				_params, false);
		return createLibraryItem(_newTable, _user, solr_url);
	}

	public ArrayList<TableDescriptor> getSchema(ArrayList<String> _lacs) {
		ArrayList<TableDescriptor> ls = new ArrayList<TableDescriptor>();
		for (String l : _lacs) {
			String[] t = LAC.parse(l);
			TableDescriptor sc = loadSchema(t[0]);

			String target_ref = LAC.getTarget(l);
			sc.setLinkReference(target_ref);
			ls.add(sc);
		}
		return ls;
	}

	public String annotateNode(String _user, String lac, String _path) {
		log.setLevel(Level.DEBUG);
		log.debug(" user : " + _user + " lac " + lac + " path : " + _path);
		NodeManager node_service = new NodeManager();
		TNode alias = node_service
				.createAlias(_user, _path, SourceType.DB, lac);
		// log.debug("Alias has been created... if it did not already exist.");
		HashMap<String, String> annotation = AnnotationLACAction
				.getAnnotationMap(_path, _user);
		// log.debug ( " annotation map has been created ");
		String solr_table = alias.getLink();
		// log.debug ( " we hav the link from the alias " + solr_table );
		String[] lac_ = LAC.parse(solr_table);
		// log.debug(" annotation structure : ");
		Set<String> aky = annotation.keySet();
		for (String akys : aky) {
			log.debug("key: " + akys + " --> " + annotation.get(akys));
		}
		log.debug("solr table : " + solr_table);
		log.debug("lac : ");

		for (int i = 0; i < lac_.length; i++) {
			log.debug(" " + lac_[i] + " index = " + i);
		}
		// e.g.
		// [milton_Repository_SH_Treatment, search, g*]
		String table_name = lac_[0];
		String data = lac_[2];
		if (lac_[0].startsWith("com.tissuematch.tm3.mylib.TMLibraryItem")) {
			String lib_data = lac_[2].trim();
			Integer it = Integer.parseInt(lib_data);
			TTable item = getTable(it);

			String name = item.getTitle();
			String user = item.getUser();
			table_name = NameUtiles.prepend(user, name);
			data = "*:*";
			log.debug(" we are loading the libraryitem with teh current table name : : "
					+ table_name);

		}
		try {
			// get a handle on the solr server
			String solr = ABProperties.get(ABProperties.SOLRSITE, null);
			TMSolrServer solr_server = new TMSolrServer(solr);
			Set<String> fields = annotation.keySet();
			ArrayList<String> remove_fields = new ArrayList<String>();
			for (String f : fields) {
				remove_fields.add(f);
			}
			solr_server.dynamicAppendSchema(table_name, data, annotation);
			// solr_server.dynamicRemoveAnnotation(lac[0], remove_fields);
		} catch (LoaderException e) {
			e.printStackTrace();
		}
		return "Annotation complete.";
	}

	public static void testNodes(String[] _args) {
		// Logger log = Logger.getLogger(TMDataBuilderServiceImpl.class);
		// log.setLevel(Level.DEBUG);
		// TMDataBuilderServiceImpl imp = new TMDataBuilderServiceImpl();
		// Map<Integer, String> links = imp
		// .getNodes("milton_Repository_v_c_studies");
		// if (links.size() <= 0)
		// log.debug(" We have failed to find nodes with the value ");
		// Set<Integer> keys = links.keySet();
		// for (Integer link : keys) {
		// log.debug("link : " + link + " = " + links.get(link) + " ---- \n");
		// }
	}

	/**
	 * load the nodes for a target with the table name _table_name
	 */
	public Map<Integer, String> getNodes(Integer _node_id) {
		Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			Criteria cr = session.createCriteria(TNode.class);
			cr.add(Restrictions.like("node_id", _node_id));
			List<TNode> l = cr.list();

			// find all the tables that have these link nodes as children.
			// select * from ab_node node join ab_node_c c on
			// node.node_id=children_node_id
			String hql = "select n.node_id from ab_node n join ab_node_ref ref on n.node_id=ref.n_to_r where ";
			for (int i = 0; i < l.size(); i++) {
				TNode _node = l.get(i);
				hql += " ref.reference=" + _node.getNode_id();
				if (i + 1 < l.size())
					hql += " OR ";
			}
			log.debug("\n\n\n\n\n\n");
			log.debug("----------\n");
			log.debug(hql);
			log.debug("----------\n");
			SQLQuery q = session.createSQLQuery(hql);
			ArrayList<Integer> load_node_ids = new ArrayList<Integer>();
			List<Number> node_ids = q.list();
			for (Number ob : node_ids) {
				load_node_ids.add(ob.intValue());
			}
			NodeManager ser = new NodeManager();
			Map<Integer, TNode> _loaded = ser.load(load_node_ids);
			HashMap<Integer, String> map = new HashMap<Integer, String>();
			Set<Integer> ni_set = _loaded.keySet();

			for (Integer ni : ni_set) {
				TNode tm = _loaded.get(ni);
				map.put(ni, tm.getName());
			}
			return map;
			// session.close();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public static void testTableState() {
		String _schema = "_milton.sample_collaborations";
		DBConnectionManager dbcm = new DBConnectionManager();
		TableManager impl = new TableManager(dbcm);
		TTable item = impl.getLibraryBySchema(_schema);
		if (item != null) {
			TMTableSettings sets = item.getSettings();
			String state = sets.getState();
			System.out.println("\n\n\n\n state : " + state);

			Gson sog = new Gson();

			class myclass {
				String name = null;
				String width = null;

				public String getName() {
					return name;
				}

				public String getWidth() {
					return width;
				}

				public void setWidth(String _width) {
					width = _width;
				}

				public void setName(String _name) {
					name = _name;
				}
			}
			class gotest {
				ArrayList<myclass> state = new ArrayList<myclass>();
			}
			ArrayList mmls = sog.fromJson(state, ArrayList.class);
			System.out.println(" map size " + mmls.size());

			for (Object l : mmls) {
				System.out.println(" l : " + l.toString());
			}
			mmls.add("{name=_checkboxField, width=28.0}");

		}
	}

	// public static void main(String[] _args) {
	// testTableState();
	// }

	/**
	 * Add a column to the schema
	 *
	 * @param _schema
	 * @param _field_name
	 * @param _type
	 * @return
	 */
	public String addColumn(String _schema, String _field_name, String _type) {
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			// this is the old way
			// http://localhost:8983/solr/admin/cores?action=add_field&table=milton_Repository_v_c_studies&field_name=hello&field_type=sint
			// solr now has an api for this.

			//curl -X POST -H 'Content-type:application/json'
			// --data-binary
			// '{"add-field": {"name":"name", "type":"text_general", "multiValued":false, "stored":true}}' http://localhost:8983/solr/films/schema


			JSONObject fieldobj = new JSONObject( );
			fieldobj.put ( "name", _field_name);
			fieldobj.put ( "type", _type );
			fieldobj.put ( "multiValued", false);
			fieldobj.put ( "stored", true);
			JSONObject jb = new JSONObject();
			jb.put ( "add-field", fieldobj );

			String posturl = _schema  + "/schema";
			HttpPost post = new HttpPost(posturl);
			post.addHeader("content-type", "application/json" +
					"");
			post.setEntity(new StringEntity(jb.toString()));
			HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
			HttpResponse response = httpClient.execute( post );
			GB.print( "Field added" + response.getStatusLine() );

			/*
			if (GBLinkManager.isFullyQualifiedURL(_schema)) {
				if (_schema.endsWith("/")) {
					_schema = _schema.substring(0, _schema.length() - 1);
				}
				// TODO: COMPLETE THIS.
				// http://192.168.1.105:8983/solr/screening_data
				int lind = _schema.lastIndexOf('/');
				if (lind > 0) {
					int i = _schema.indexOf('/', 7);
					String server = _schema.substring(0, i);
					solr_url = server + "/solr";

				}
			}
			int last_index = _schema.lastIndexOf('/');
			String schema = _schema.substring(last_index + 1);

			boolean bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=add_field&table=" + schema
					+ "&field_name=" + _field_name + "&field_type=" + _type);

			log.debug(solr_url + "/admin/cores?action=add_field&table="
					+ schema + "&field_name=" + _field_name + "&field_type="
					+ _type);
			log.debug(solr_url + "/admin/cores?action=add_field&table="
					+ schema + "&field_name=" + _field_name + "&field_type="
					+ _type);
			log.debug(solr_url + "/admin/cores?action=add_field&table="
					+ schema + "&field_name=" + _field_name + "&field_type="
					+ _type);

			TTable item = getLibraryBySchema(_schema);

			if (item != null) {

				// table settings are optional so we may not have any here.
				TMTableSettings sets = item.getSettings();
				if (sets != null) {
					String state = sets.getState();
					if (state != null) {
						Gson sog = new Gson();
						ArrayList mmls = sog.fromJson(state, ArrayList.class);
						// debug output
						for (Object ob : mmls) {
							HashMap map = (HashMap) ob;
							Set<String> keys = map.keySet();
							for (String k : keys) {
								Object obb = map.get(k);
								System.out.println(" key : " + k + " value = "
										+ obb);
								if (obb != null)
									System.out
											.println(" value type "
													+ obb.getClass()
													.getCanonicalName());

							}
						}
						java.util.LinkedHashMap lh = new LinkedHashMap<String, Object>();
						lh.put("name", _field_name);
						lh.put("width", new Double(200));
						if (mmls != null) {
							mmls.add(1, lh);
							String newjson = sog.toJson(mmls);
							sets.setState(newjson);
						}
						saveTableSettings(sets);
					}
				}
			}
			*/
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to add field";
		}
		return "Field Added";
	}

	public String saveTableStateForField(String _schema, String _field_name) {

		TTable item = getLibraryBySchema(_schema);
		if (item != null) {
			TMTableSettings sets = item.getSettings();
			String state = sets.getState();
			if (state == null) {
				ArrayList mmls = new ArrayList();
				java.util.LinkedHashMap lh = new LinkedHashMap<String, Object>();
				lh.put("name", _field_name);
				lh.put("width", new Double(100));
				mmls.add(lh);
				Gson sog = new Gson();
				String newjson = sog.toJson(mmls);
				sets.setState(newjson);
				saveTableSettings(sets);
			} else {
				Gson sog = new Gson();
				ArrayList mmls = sog.fromJson(state, ArrayList.class);
				java.util.LinkedHashMap lh = new LinkedHashMap<String, Object>();
				lh.put("name", _field_name);
				lh.put("width", new Double(100));
				mmls.add(1, lh);
				String newjson = sog.toJson(mmls);
				sets.setState(newjson);
				saveTableSettings(sets);
				return "saved";
			}
		}
		return "failed";
	}

	/**
	 * Remove the field for a particular column
	 */
	public String removeColumn(String _schema, String _field_name) {
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			// http://localhost:8983/solr/admin/cores?action=add_field&table=milton_Repository_v_c_studies&field_name=hello&field_type=sint
			boolean bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=remove_field&table=" + _schema
					+ "&field_name=" + _field_name);
			TTable item = getLibraryBySchema(_schema);
			synchronized (item) {
				if (item != null) {
					TMTableSettings sets = item.getSettings();
					String state = sets.getState();
					Gson sog = new Gson();
					ArrayList<HashMap> mmls = sog.fromJson(state,
							ArrayList.class);
					for (HashMap<String, Object> m : mmls) {
						String name = (String) m.get("name");
						if (name.equalsIgnoreCase(_field_name)) {
							mmls.remove(m);
						}
					}
					String newjson = sog.toJson(mmls);
					sets.setState(newjson);
					saveTableSettings(sets);
				}
			}

		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to remove field";
		}
		return "Field Removed";
	}

	private String saveTableSettings(TMTableSettings sets) {
		String msg = "ERROR saving table settings";
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cr = hibernateSession
					.createCriteria(TMTableSettings.class);
			cr.add(Restrictions.eq("tbl_prop_id", sets.getTbl_prop_id()));

			List ls = cr.list();
			if (ls.size() <= 0) {
				return "Failed to find the table.";
			} else {
				TMTableSettings tm = (TMTableSettings) ls.get(0);
				if (tm != null) {
					tm.setState(sets.getState());
					tm.setHeight(sets.getHeight());
					tm.setWidth(sets.getWidth());
					tm.setDefault_width(sets.getDefault_width());
					hibernateSession.saveOrUpdate(tm);
					hibernateSession.flush();
				}
				hibernateSession.getTransaction().commit();
				msg = "Table settings saved.";
			}
		} catch (Exception _e) {
			_e.printStackTrace();
			msg = "Error trying to save the table settings.";
			return msg;
		} finally {
			HBConnect.close(hibernateSession);
		}
		return msg;
	}

	public String removeDisconnectedTables(final String _user) {
		Session session = null;
		try {
			String _url = ABProperties.get(ABProperties.SOLRSITE);
			if (!_url.endsWith("/")) {
				_url += "/";
			}
			if (_url != null && _url.length() > 0)
				_url = _url.trim();
			URL solr = new URL(_url + "admin/cores");
			URLConnection yc = solr.openConnection();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(yc.getInputStream());
			NodeList nl = dom.getElementsByTagName("response");
			int node_length = nl.getLength();
			ArrayList<String> libs = new ArrayList<String>();
			if (nl != null && node_length > 0) {
				for (int i = 0; i < node_length; i++) {
					Element el = (Element) nl.item(i);
					NodeList statusNode = el.getChildNodes();
					int core_length = statusNode.getLength();
					for (int j = 0; j < core_length; j++) {
						Node cc = statusNode.item(j);
						String name_ = cc.getNodeName();
						if (cc.hasAttributes()) {
							NamedNodeMap nm = cc.getAttributes();
							String st_test = nm.getNamedItem("name")
									.getNodeValue();
							if (st_test.equalsIgnoreCase("status")) {
								NodeList cores = cc.getChildNodes();
								int c_length = cores.getLength();
								for (int k = 0; k < c_length; k++) {
									Node corenode = cores.item(k);
									System.out.println(" corenodename : "
											+ corenode.getNodeName());
									if (corenode.hasAttributes()) {
										NamedNodeMap nm2 = corenode
												.getAttributes();
										String st_test2 = nm2.getNamedItem(
												"name").getNodeValue();
										libs.add(st_test2);
										// we have a name:
									}
								}
							}
						}
						System.out.println(" name : " + name_);
						System.out
								.println(" attributes: " + cc.hasAttributes());
						System.out.println(" children : " + cc.hasChildNodes());
					}
				}
			}

			session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(TTable.class);
			c.add(Restrictions.eq("user", _user));
			List<TTable> item = c.list();
			for (TTable l : item) {
				boolean found = false;
				String index_name = l.getIndex_id();
				for (String schema_name : libs) {
					if (schema_name.equalsIgnoreCase(index_name))
						found = true;
				}
				if (!found)
					session.delete(l);
			}
			session.getTransaction().commit();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			HBConnect.close(session);
		}

		return "sync complete";
	}

	/**
	 * Create an index_ed field for a _field value.
	 */
	public String index_field(String _user, String _schema, String _field) {
		// http://localhost:8983/solr/admin/cores?action=create_field_facet&schema=milton_Repository_HTL&field_name=$field_name&type=$field_type
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		boolean bb = false;
		try {
			bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=create_field_facet&schema="
					+ _schema + "&field_name=" + _field + "&type=string");
		} catch (SolrCallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (bb)
			return "Field is indexed";
		else
			return "Failed to index the field " + _field;
	}

	public String setDescription(Integer _itemId, String description) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cr = hibernateSession.createCriteria(TTable.class);
			cr.add(Restrictions.eq("itemID", _itemId));
			List ls = cr.list();
			if (ls.size() <= 0) {
				return "Failed to find the table." + _itemId;
			} else {
				TTable li = (TTable) ls.get(0);
				li.setDescription(description);
				hibernateSession.flush();
				hibernateSession.getTransaction().commit();
			}

			return "Saved";
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return "Save failed";
	}

	/**
	 * Save the sort for the table.
	 */
	public String setTableSort(int _itemId, String _sort_field,
							   String _sort_direction) {
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cr = hibernateSession.createCriteria(TTable.class);
			cr.add(Restrictions.eq("itemID", _itemId));
			List ls = cr.list();
			if (ls.size() <= 0) {
				return "Failed to find the table." + _itemId;
			} else {
				TTable li = (TTable) ls.get(0);
				TMTableSettings table_settings = li.getSettings();
				Map<String, String> table_props = table_settings
						.getProperties();
				table_props.put("sort_field", _sort_field);
				table_props.put("sort_direction", _sort_direction);
				table_settings.setProperties(table_props);
				li.setSettings(table_settings);
				hibernateSession.saveOrUpdate(table_settings);
				hibernateSession.getTransaction().commit();
			}
			return "Sort Saved";
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return "Save failed";
	}

	/**
	 * Quick search using an lucene syntax... etc. You must also know the name
	 * of the schema
	 */
	public ArrayList<String> quicksearch(String _schema, String _field,
										 String _searchString) {

		// this has to return the faceted value of the field.

		HttpSolrClient solr = null;
		ArrayList<String> lis = new ArrayList<String>();
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}

			if (_searchString.contains("and") || _searchString.contains("AND")
					|| _searchString.contains("OR")
					|| _searchString.contains("or")
					|| _searchString.contains("NOT")
					|| _searchString.contains("not")) {
				_searchString = _searchString.replaceAll(" and ", " AND ");
				_searchString = _searchString.replaceAll(" not ", " NOT ");
				_searchString = _searchString.replaceAll(" or ", " OR ");
			}
			// {{ TRY TO DO A POST QUERY INSTEAD OF A GET.... }},
			solr = new HttpSolrClient.Builder(solr_url + _schema).build();
			if (_searchString == null || _searchString.length() <= 0
					|| _searchString.equalsIgnoreCase("*"))
				_searchString = "a";
			// String field_search = _field + ":" + _searchString + "*";
			// if ( _searchString == null || _searchString.length() <= 0 )
			// field_search = "*:*";

			ModifiableSolrParams params = new ModifiableSolrParams();
			_searchString = _searchString.trim();
			// _searchString = _searchString.replace ("\r", "\r*");
			params.set("q", _field + ":\"" + _searchString
					+ "*\" TMID_lastUpdated desc");
			params.set("start", 0);
			params.set("rows", 150);

			// params.set("facet", true);// &facet=ctrue&facet.field=ca
			// params.add("facet.field", _field.toLowerCase());
			// params.set("facet.limit", 200);
			// params.set("facet.mincount", 1);

			params.set("wt", "xml");

			XMLResponseParser pars = new XMLResponseParser();
			solr.setParser(pars);
			QueryResponse response = solr.query(params);

			LinkedHashSet<String> dl = new LinkedHashSet<String>();
			ArrayList<String> l = new ArrayList<String>();
			SolrDocumentList list = response.getResults();
			long num_found = list.getNumFound();
			for (int i = 0; i < num_found && i < 150; i++) {
				SolrDocument ld = list.get(i);
				if (ld != null) {
					Object value = ld.getFieldValue(_field);
					if (value != null)
						dl.add(value.toString());
				}
			}
			for (String s : dl) {
				l.add(s);
			}

			// LinkedHashMap<String, Integer> facet_query = new
			// LinkedHashMap<String, Integer>();
			// List<FacetField> fields = response.getFacetFields();
			// for (FacetField f : fields) {
			// List<Count> counts = f.getValues();
			// for (Count c : counts) {
			// String name = c.getName();
			// Long count = c.getCount();
			// facet_query.put(name, count.intValue());
			// l.add(name);
			// }
			// }

			log.debug("Building the results..." + l.size());
			return l;
		} catch (org.apache.solr.client.solrj.SolrServerException _solrException) {
			_solrException.printStackTrace();
			return null;
		} catch (Exception _e) {
			_e.printStackTrace();
			return lis;
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public LinkedHashMap<String, String> getAvailableDBs() {
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		// "jdbc:mysql://localhost/shadw?user=shadw&password=shadw");
		// config.put("driver_class", "com.mysql.jdbc.Driver");
		// config.put("user", "shadw");
		// config.put("password", "shadw");
		// String query = "select * from copy_number limit 8";
		// String user = "milton";
		values.put("lappy",
				"jdbc:mysql://localhost/shadw?user=shadw&password=shadw");
		return values;
	}

	public LinkedHashMap<String, String> getAvailableDBURLs() {
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		// "jdbc:mysql://localhost/shadw?user=shadw&password=shadw");
		// config.put("driver_class", "com.mysql.jdbc.Driver");
		// config.put("user", "shadw");
		// config.put("password", "shadw");
		// String query = "select * from copy_number limit 8";
		// String user = "milton";
		values.put("lappy",
				"jdbc:mysql://localhost/shadw?user=shadw&password=shadw");
		return values;
	}

	public ArrayList<String> getAvailableDBsForUpdate() {
		// this arraylist should match the map below
		ArrayList<String> list = new ArrayList<String>();
		list.add("debug");
		list.add("biospec");
		return list;
	}

	private static LinkedHashMap<String, Map<String, String>> getConfigForDB(
			String _db) {

		HashMap<String, String> debug_c = new HashMap<String, String>();
		debug_c.put("url",
				"jdbc:mysql://localhost/shadw?user=shadw&password=shadw");
		debug_c.put("driver_class", "com.mysql.jdbc.Driver");
		debug_c.put("user", "shadw");
		debug_c.put("password", "shadw");

		HashMap<String, String> biospec = new HashMap<String, String>();
		biospec.put("url",
				"jdbc:oracle:thin:biospecimen/biospc@biodev1:1521:biodev1");
		biospec.put("driver_class", "oracle.jdbc.driver.OracleDriver");
		biospec.put("user", "biospecimen");
		biospec.put("password", "biospc");

		LinkedHashMap<String, Map<String, String>> config = new LinkedHashMap<String, Map<String, String>>();
		config.put("debug", debug_c);
		config.put("biospec", biospec);
		return config;
	}

	/*
	 * OK... we should be all hooked up on the server side.
	 */
	public String updateDB(String _db, String _table_name,
						   String _search_string, String _sql, String _user) {
		TMJDBCServer server = new TMJDBCServer();
		try {
			LinkedHashMap<String, String> map = getAvailableDBURLs();
			Map<String, Map<String, String>> _config_map = getConfigForDB(_db);
			Map<String, String> _config = _config_map.get(_db);
			server.updateDB(_config, _table_name, _search_string, _sql);
		} catch (LoaderException _e) {
			_e.printStackTrace();
		}
		return "Database updated";
	}

	public String publishLibrary(String _user, String _lac, String _path,
								 String _publish_table) {

		String target = LAC.getTarget(_lac);
		TableDescriptor sd = loadSchema(target);
		TTable item = sd.getLibraryDescriptor();
		// String table_name = _params.get("table_name");
		// String table_state = _params.get("state");
		// String path = _params.get("path");
		// String description = _params.get ( "description");
		// String user = _params.get ( "user");
		String solr = ABProperties.get(ABProperties.SOLRSITE);
		String desc = "";
		String state = "";
		try {
			String description = item.getDescription();
			if (description != null && description.length() > 0) {
				desc = URLEncoder.encode(item.getDescription(), "UTF-8");
			}
			TMTableSettings tm = item.getSettings();
			if (tm != null && tm.getState() != null) {
				state = URLEncoder.encode(item.getSettings().getState(),
						"UTF-8");
			}
			String params_wurl = solr + "/admin/cores?action=publish"
					+ "&table_name=" + target + "&path=" + _path
					+ "&publisher=" + _user + "&description=" + desc
					+ "&table_state=" + state + "&publish_table_name="
					+ _publish_table;
			TMSolrServer.callSolr(params_wurl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Publish failed... encoding not supported. ";
		} catch (SolrCallException e) {
			e.printStackTrace();
		}
		return "Published";
	}

	public String createPublishTable(String _table) {
		String solr = ABProperties.get(ABProperties.SOLRSITE);
		try {
			_table = URLEncoder.encode(_table, "UTF-8");

			String params_wurl = solr
					+ "/admin/cores?action=create_publish_table"
					+ "&table_name=" + _table;
			TMSolrServer.callSolr(params_wurl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Publish failed... encoding not supported. ";
		} catch (SolrCallException e) {
			e.printStackTrace();
		}
		return "Publishing table created...";
	}

	public LoadStatus createIndexFromTableDataString(String _user,
													 String _tableName, String _dataString, Map<String, String> _config) {

		StringTokenizer st = new StringTokenizer(_dataString, "\n");
		String line = null;
		LinkedHashMap<String, Map<String, String>> fields = null;
		while (st.hasMoreTokens()) {
			if (line == null) {
				line = st.nextToken();
				fields = buildFields(line);
			} else
				break;
		}
		String table_name = NameUtiles.prepend(_user, _tableName);
		if (fields != null) {
			build(_user,
					"test",
					table_name,
					"Copy and pasted database.  To change this description select edit table from the edit menu below. ",
					"1", fields, null);
		}
		addData(table_name, fields, _dataString);
		LoadStatus ls = new LoadStatus();
		ls.setMsg("Table created.. but the data is not loaded");
		return ls;
	}

	private void addData(String table_name,
						 LinkedHashMap<String, Map<String, String>> fields,
						 String _dataString) {

		StringTokenizer st = new StringTokenizer(_dataString, "\n");
		String line = null;

		ArrayList<GRow> rows = new ArrayList<GRow>();
		ArrayList<String> _f = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			if (line == null) {
				line = st.nextToken();

				StringTokenizer stln = new StringTokenizer(line, "\t");
				while (stln.hasMoreTokens()) {
					String f = stln.nextToken();
					_f.add(f);
				}
			} else {
				line = st.nextToken();
				StringTokenizer stln = new StringTokenizer(line, "\t");
				int i = 0;
				HashMap data = new HashMap();
				while (stln.hasMoreTokens()) {
					String key = _f.get(i++);
					String value = stln.nextToken();
					data.put(key, value);
				}
				GRow _row = new GRow();
				_row.setData(data);
				rows.add(_row);
			}
		}
		addRows(table_name, rows);
	}

	//
	// private void addDataFromHTML(String table_name,
	// LinkedHashMap<String, Map<String, String>> fields,
	// String _dataString) {
	//
	// _dataString = _dataString.replace("&nbsp", " ");
	//
	// org.jsoup.nodes.Document doc = Jsoup.parse(_dataString);
	// List<org.jsoup.nodes.Node> list = doc.childNodes();
	// for (org.jsoup.nodes.Node n : list) {
	// org.jsoup.nodes.Node table = find(n, "table");
	// if (table != null) {
	// List<org.jsoup.nodes.Node> child = table.childNodes();
	// int index = 0;
	// ArrayList<GRow> rows = new ArrayList<GRow>();
	//
	// for (org.jsoup.nodes.Node ch : child) {
	// org.jsoup.nodes.Node row = find(ch, "tr");
	// if (row != null) {
	// // skip the first row.
	// if (index > 0) {
	// ArrayList<String> ls = parseStringData(row);
	// Set<String> fi = fields.keySet();
	// HashMap data = new HashMap();
	// GRow _row = new GRow();
	// int i = 0;
	// for (String col : fi) {
	// Object value = ls.get(i);
	// data.put(col, value);
	// i++;
	// }
	// _row.setData(data);
	// rows.add(_row);
	// }
	//
	// // loop over the rows
	// index++;
	// }
	// }
	// }
	// }
	// }
	// public org.jsoup.nodes.Node find(org.jsoup.nodes.Node _node, String
	// _name) {
	// if (_node.nodeName().equalsIgnoreCase(_name)) {
	// return _node;
	// }
	// List<org.jsoup.nodes.Node> list = _node.childNodes();
	// for (org.jsoup.nodes.Node n : list) {
	// String node_name = n.nodeName();
	// if (node_name.equalsIgnoreCase(_name))
	// return n;
	// else {
	// org.jsoup.nodes.Node ch = find(n, _name);
	// if (ch != null) {
	// return ch;
	// }
	// }
	// }
	// return null;
	// }
	//
	// private LinkedHashMap<String, Map<String, String>>
	// buildFieldsFrom_HTMLTable(
	// String _line) {
	// LinkedHashMap<String, Map<String, String>> fields = new
	// LinkedHashMap<String, Map<String, String>>();
	// org.jsoup.nodes.Document doc = Jsoup.parse(_line);
	// List<org.jsoup.nodes.Node> list = doc.childNodes();
	// for (org.jsoup.nodes.Node n : list) {
	// org.jsoup.nodes.Node table = find(n, "table");
	// if (table != null) {
	// List<org.jsoup.nodes.Node> child = table.childNodes();
	// for (org.jsoup.nodes.Node ch : child) {
	// // find the first row
	// org.jsoup.nodes.Node row = find(ch, "tr");
	// if (row != null) {
	// fields = parseFields(row);
	// break;
	// }
	// }
	// }
	// }
	// return fields;
	// }

	private LinkedHashMap<String, Map<String, String>> buildFields(String _line) {
		LinkedHashMap<String, Map<String, String>> fields = new LinkedHashMap<String, Map<String, String>>();
		StringTokenizer st = new StringTokenizer(_line, "\t");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			Map<String, String> token_map = buildStringField(NameUtiles
					.convertToValidCharName(token).trim());
			fields.put(token, token_map);
		}
		return fields;
	}

	//
	// private LinkedHashMap<String, Map<String, String>> parseFields(
	// org.jsoup.nodes.Node row) {
	//
	// LinkedHashMap<String, Map<String, String>> fields = new
	// LinkedHashMap<String, Map<String, String>>();
	//
	// List<org.jsoup.nodes.Node> cols = row.childNodes();
	// for (org.jsoup.nodes.Node col : cols) {
	// if (col.nodeName().equalsIgnoreCase("td")) {
	// Attributes attrs = col.attributes();
	// List<org.jsoup.nodes.Node> l = col.childNodes();
	// for (org.jsoup.nodes.Node ln : l) {
	//
	// log.debug("node name :   " + ln.nodeName());
	// if (ln.nodeName().equalsIgnoreCase("#text")) {
	// String value = NameUtiles.convertToValidCharName(ln
	// .toString());
	// log.debug(" value :   " + value);
	// Map<String, String> map_value = buildStringField(value);
	// fields.put(value, map_value);
	// }
	// System.out.println(" --" + ln.toString());
	// }
	// }
	// }
	// return fields;
	// }

	/**
	 * Pull out the data as an array of string from the row
	 *
	 * @return
	 */
	// private ArrayList<String> parseStringData(org.jsoup.nodes.Node row) {
	// LinkedHashMap<String, Map<String, String>> fields = new
	// LinkedHashMap<String, Map<String, String>>();
	// List<org.jsoup.nodes.Node> cols = row.childNodes();
	// ArrayList<String> row_ = new ArrayList<String>();
	// for (org.jsoup.nodes.Node col : cols) {
	// if (col.nodeName().equalsIgnoreCase("td")) {
	// Attributes attrs = col.attributes();
	// List<org.jsoup.nodes.Node> l = col.childNodes();
	//
	// for (org.jsoup.nodes.Node ln : l) {
	// log.debug("node name :   " + ln.nodeName());
	// if (ln.nodeName().equalsIgnoreCase("#text")) {
	// String value = NameUtiles.convertToValidCharName(ln
	// .toString());
	// row_.add(value);
	// }
	// System.out.println(" --" + ln.toString());
	// }
	// }
	// }
	// return row_;
	// }
	private Map<String, String> buildStringField(String value) {
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", value);
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", "");
		uuidp.put("dataType", "string");
		uuidp.put("requiredField", "false");
		return uuidp;
	}

	public static String getTableState(String title) {
		Session hibernateSession = null;
		try {
			DBConnectionManager dbcm = GB.getConnectionManager();
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", title));
			List list = cer.list();
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					TTable tm = (TTable) object;
					TMTableSettings settings = tm.getSettings();
					if (settings != null)
						return settings.getState();
				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	public static Map<String, String> getTableProperties(String title) {
		Session hibernateSession = null;
		try {
			DBConnectionManager dbcm = GB.getConnectionManager();
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", title));
			List list = cer.list();
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					TTable tm = (TTable) object;
					TMTableSettings settings = tm.getSettings();
					if (settings != null) {
						Map<String, String> props = settings.getProperties();
						return HBConnect.convertToNonLazyObject(props);
					} else
						return null;
				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	public static void saveTableProperties(String title,
										   Map<String, String> _props) {
		Session hibernateSession = null;
		try {
			DBConnectionManager dbcm = GB.getConnectionManager();
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", title));
			List list = cer.list();
			TTable tm = null;
			TMTableSettings settings = null;
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					tm = (TTable) object;
					settings = tm.getSettings();
				}
			}
			if (tm == null) {
				tm = new TTable();
				settings = new TMTableSettings();
			}

			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> orig = settings.getProperties();
			if (orig != null) {
				map = orig;
			}
			// now we overwrite the previous settings... where the keys are
			// matched. (i.e. unique keys)
			Set keys = _props.keySet();
			for (Object key : keys) {
				map.put(key.toString(), _props.get(key).toString());
			}
			settings.setProperties(map);

			hibernateSession.saveOrUpdate(settings);
			tm.setSettings(settings);
			hibernateSession.saveOrUpdate(tm);
			hibernateSession.flush();
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
	}

	public static void main(String[] sr) {
		String tstate = "480815433";
		Properties p = new Properties();
		p.put("link", "hello_link");
		p.put("link1", "hello_link");
		p.put("link2", "hello_link");
		p.put("link3", "hello_link");

	}

	public static void saveTableState(String title, String state) {
		Session hibernateSession = null;
		try {
			DBConnectionManager dbcm = GB.getConnectionManager();
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", title));
			List list = cer.list();
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					TTable tm = (TTable) object;
					TMTableSettings settings = tm.getSettings();
					settings.setState(state);
					hibernateSession.saveOrUpdate(tm);
					hibernateSession.flush();
					hibernateSession.getTransaction().commit();
				}
			} else {
				TTable t = new TTable();
				t.setTitle(title);
				TMTableSettings sett = new TMTableSettings();
				sett.setState(state);
				t.setSettings(sett);
				hibernateSession.save(sett);
				hibernateSession.save(t);
				hibernateSession.getTransaction().commit();
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
	}

	/**
	 * Returns null if the schema table is not found.
	 */
	public static List<GColumn> getFieldOrder(String target,
											  DBConnectionManager _dbcm, ArrayList<GColumn> cols) {
		Map<String, String> props = TableManager.getTableProperties(target);
		if (props == null)
			return cols;
		Set<String> nps = props.keySet();
		for (String p : nps) {
			if (p.equalsIgnoreCase("state")) {
				ArrayList<String> _order = GBTables
						.parseOrder(props.get(p));
				if (_order != null) {
					ArrayList<GColumn> ncols = new ArrayList<GColumn>();
					int index = 0;
					for (String order : _order) {
						GColumn sorder = GBTables.getCol(order, cols);
						if (sorder != null)
							ncols.add(sorder);
						index++;
					}

					for (GColumn rcol : cols) {
						if (!isIn(rcol, ncols)) {
							ncols.add(rcol);
						}
					}


					// add the rest
					if (cols.size() > _order.size()) {
						for (int i = index; i < cols.size(); i++) {
							GColumn rcol = cols.get(i);
							ncols.add(rcol);
						}
					}
					return ncols;
				}
			}
		}
		return cols;
	}

	private static boolean isIn(GColumn rcol, ArrayList<GColumn> ncols) {
		for (GColumn n : ncols) {
			if (n.getName().equalsIgnoreCase(rcol.getName()))
				return true;
		}
		return false;
	}

	public static List<String> getFieldOrder___(String title,
												DBConnectionManager _dbcm, ArrayList<GColumn> cp) {
		Session hibernateSession = null;
		try {
			hibernateSession = _dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", title));
			List list = cer.list();
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					TTable tm = (TTable) object;
					TMTableSettings settings = tm.getSettings();
					if (settings == null) {
						saveDefaultSettings(tm, cp, hibernateSession);
						hibernateSession.getTransaction().commit();
					} else {
						Map<String, Integer> __order = settings.getCol_order();
						Set<String> keys = __order.keySet();
						if (keys.size() <= 0) {
							GB.print("Column orders are not defined. ");
							return null;
						}
						ArrayList<String> order = new ArrayList<String>(
								keys.size());
						String[] ss = new String[keys.size()];
						for (String K : keys) {
							Integer index = __order.get(K);
							ss[index] = K;
						}
						for (String s : ss) {
							order.add(s);
						}

						for (GColumn pc : cp) {
							if (in(pc.getName(), order)) {

							} else
								order.add(pc.getName());
						}

						return order;
					}
				}
			} else {
				GB.print("Saving default schema for table: " + title);
				GB.print("...");
				TTable tm = new TTable();
				tm.setTitle(title);
				saveDefaultSettings(tm, cp, hibernateSession);
				hibernateSession.getTransaction().commit();
			}
			return null;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	private static boolean in(String name, ArrayList<String> order) {
		for (String n : order) {
			if (n.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	private static boolean isIn(String s, ArrayList<GColumn> cp) {

		for (GColumn c : cp) {
			if (s.equalsIgnoreCase(c.getName()))
				return true;
		}

		return false;
	}

	private static void saveDefaultSettings(TTable tm, ArrayList<GColumn> cp,
											Session hibernateSession) {
		TMTableSettings settings = new TMTableSettings();
		Map<String, Integer> __order = new LinkedHashMap<String, Integer>();
		int index = 0;
		for (GColumn g : cp) {
			__order.put(g.getName(), index++);
		}
		settings.setCol_order(__order);
		tm.setSettings(settings);
		hibernateSession.save(tm);
		hibernateSession.flush();
	}

	public List<String> getColumnOrder(String linkl) {
		// the table framework needs work. This is currently a hack. no time for
		// now.
		// the primary key for accessing the table properties for a core
		if (linkl != null) {
			// i.e. this is the old link mechanism
			if (linkl.contains("com.tissuematch.tm3.mylib.TMLibraryItem.")) {
				String data = LAC.getData(linkl);
				try {
					int itemID = Integer.parseInt(data.trim());
					TTable table = getTable(itemID);
					TMTableSettings tableSettings = table.getSettings();
					Map<String, Integer> __order = tableSettings.getCol_order();
					Set<String> keys = __order.keySet();
					ArrayList<String> order = new ArrayList<String>(keys.size());
					for (String K : keys) {
						Integer index = __order.get(K);
						order.set(index, K);
					}
					return order;
				} catch (NumberFormatException ne) {
					ne.printStackTrace();
				}
			} else {
				String target = LAC.getTarget(linkl);
				// we are going after
				TTable t = getLibraryItem(target);
				TMTableSettings tableSettings = t.getSettings();
				Map<String, Integer> __order = tableSettings.getCol_order();
				Set<String> keys = __order.keySet();
				ArrayList<String> order = new ArrayList<String>(keys.size());
				for (String K : keys) {
					Integer index = __order.get(K);
					order.set(index, K);
				}
				return order;
				// List<String> order = tableSettings.getCol_order();
			}
		}
		return new ArrayList<String>();
	}

	public static void addTableProperty(String core, String key, String value) {
		Session hibernateSession = null;
		try {
			DBConnectionManager dbcm = GB.getConnectionManager();
			hibernateSession = dbcm.getSession();
			hibernateSession.beginTransaction();
			Criteria cer = hibernateSession.createCriteria(TTable.class);
			cer.add(Restrictions.eq("title", core));
			List list = cer.list();
			TTable tm = null;
			TMTableSettings settings = null;
			if (list != null && list.size() > 0) {
				Object object = list.get(0);
				if (object != null) {
					tm = (TTable) object;
					settings = tm.getSettings();
				}
			}
			if (tm == null) {
				tm = new TTable();
				settings = new TMTableSettings();
			}

			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> orig = settings.getProperties();
			if (orig != null) {
				map = orig;
			}
			// now we overwrite the previous settings... where the keys are
			// matched. (i.e. unique keys)
			map.put(key, value);
			settings.setProperties(map);

			hibernateSession.saveOrUpdate(settings);
			tm.setSettings(settings);
			hibernateSession.saveOrUpdate(tm);
			hibernateSession.flush();
			hibernateSession.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
	}

	/**
	 * Generate a schema object for table creation given a simple type, name
	 * schema
	 *
	 * @param schema
	 * @return
	 */
	public static LinkedHashMap<String, Map<String, String>> createSchema(
			Map<String, String> schema) {

		ArrayList<GColumn> gclist = new ArrayList<GColumn>();
		Set<String> v = schema.keySet();
		for (String name : v) {
			name = name.trim();
			String type = schema.get(name);
			GColumn column = new GColumn(name, type);
			gclist.add(column);
		}

		LinkedHashMap<String, Map<String, String>> params = new LinkedHashMap<String, Map<String, String>>();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", TMID.create());
		uuidp.put("dataType", "text");
		uuidp.put("requiredField", "true");
		params.put("TMID", uuidp);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");
		last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
		last_updated.put("dataType", "date");
		last_updated.put("requiredField", "true");
		params.put("TMID_lastUpdated", last_updated);
		for (GColumn k : gclist) {
			uuidp = new HashMap<String, String>();
			uuidp.put("fieldName", k.getName());
			uuidp.put("sortable", "true");
			uuidp.put("indexed", "true");
			uuidp.put("defaultString", "");
			uuidp.put("dataType", k.getType().toLowerCase());
			uuidp.put("requiredField", "false");
			params.put(k.getName(), uuidp);
		}
		return params;
	}

}