package com.arraybase.solr.plugin;

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
import com.arraybase.tm.*;
import com.arraybase.tm.builder.TMLibBuilder;
import com.arraybase.tm.builder.TMLibSchedule;
import com.arraybase.tm.builder.TMSchedulerFactory;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import com.arraybase.util.Level;
import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
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
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * The server side implementation of the RPC service.
 */
public class TableManagerPlugin {
	public final static String TMSOLR = "TMSOLR";
	private static GBLogger log = GBLogger.getLogger(TableManagerPlugin.class);
	private DBConnectionManager dbcon = new DBConnectionManager();
	static {
		log.setLevel(Level.DEBUG);
	}

	public ArrayList<TMURI> loadLinks(String userID, String _schema,
			String _itemID) {
		return new ArrayList();
	}

	public static String build(String _userName, String _type, String _name,
			String _description, String _security,
			HashMap<String, Map<String, String>> _params,
			HashMap<String, String> _init_raw_data) {
		DBConnectionManager dbcon = new DBConnectionManager();
		String _lib_name = NameUtiles.strip(_userName, _name);
		TTable litem = new TTable();
		Session hibernateSession = null;
		try {
			hibernateSession = dbcon.getSession();
			synchronized (hibernateSession) {
				hibernateSession.beginTransaction();
				Criteria c = hibernateSession.createCriteria(TTable.class);
				c.add(Restrictions.eq("title", _lib_name));
				List values = c.list();
				if (values != null && values.size() > 0) {
					litem = (TTable) values.get(0);
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
					hibernateSession.flush();
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
			hibernateSession = dbcon.getSession();
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
        Session hibernateSession = null;
		String msg = "ERROR saving table settings";
		try {
			hibernateSession = dbcon.getSession();
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

	private static String build(String _type__, String _name,
			HashMap<String, Map<String, String>> _params) {
		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
		UUID idOne = UUID.randomUUID();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", idOne.toString());
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
		return TMSchema.createSchemaXMLFromSolrRoot(_name, _params, true);
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
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
				hibernateSession = dbcon.getSession();
				hibernateSession.beginTransaction();
				hibernateSession.saveOrUpdate(litem);
				hibernateSession.getTransaction().commit();
				return getLibraries(litem.getItemID());
			} catch (Exception _e) {
				_e.printStackTrace();
			} finally {
				HBConnect.close(hibernateSession);
			}
			return litem;
		}
		return null;
	}

	public TTable getLibraries(Integer itemID) {
        Session hibernateSession = null;
		try {
			hibernateSession = dbcon.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("itemID", itemID));
			List list = criteria.list();
			if (list.size() > 0) {
				TTable tl = (TTable) list.get(0);
				tl = HibernateToCoreJava.convert(tl);
				hibernateSession.close();
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

	private static boolean isEqual(Object ldata_object, Object rdata_object) {
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
	 * one-off data test for checking the ping function. This is not a unit test
	 * because it is testing a db connection
	 * 
	 * @param _args
	 */
	public static void main(String[] _args) {
		TableManagerPlugin stu = new TableManagerPlugin();
		int value = ping("__test_table__");
		System.out.println(" value : " + value);
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
	public String update(String _schema, ArrayList<GRow> _row) {
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

		log.debug("schema: " + _schema);
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

	/**
	 * does not use beans
	 * 
	 * @param _row
	 * @return
	 */
	private ArrayList<SolrInputDocument> getDocs(ArrayList<GRow> _row,
			String _schema) {
		ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int i = 0; i < _row.size(); i++) {
			SolrInputDocument sid = new SolrInputDocument();
			GRow r = _row.get(i);
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
				sid.setField("TMID_lastUpdated", udate);
			}
			if (sid.getField("TMID") == null) {
				UUID idOne = UUID.randomUUID();
				sid.addField("TMID", idOne);
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
			hibernateSession = dbcon.getSession();
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
			hibernateSession = dbcon.getSession();
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
			hibernateSession = dbcon.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("itemID", itemID));
			List list = criteria.list();
			TTable item = null;
			for (int i = 0; i < list.size(); i++) {
				item = (TTable) list.get(i);
				item = HibernateToCoreJava.convert(item);
			}
			hibernateSession.close();
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
			hibernateSession = dbcon.getSession();
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
	 * 
	 */
	public ArrayList<TTable> getLibraries(String _userid) {
		// {{ BELOW IS THE OLD MECHANISM }}
        Session hibernateSession = null;
		try {

			hibernateSession = dbcon.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("user", _userid));
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

	//

	public String removeCore(String _userid, String _schema) {
        Session hibernateSession = null;
        HttpSolrClient solr = null;
		try {
			hibernateSession = dbcon.getSession();
			hibernateSession.beginTransaction();
			Criteria criteria = hibernateSession.createCriteria(TTable.class);
			criteria.add(Restrictions.eq("user", _userid));
			criteria.add(Restrictions.eq("title", _schema));
			List list = criteria.list();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
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
			hibernateSession = dbcon.getSession();
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

	private ArrayList<GColumn> describeCore(String _solrCore)
			throws ConnectException {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		return TMSolrServer.describeCore(solr_url, _solrCore);

	}

	public GResults search(String _user, String _volume, String _query) {
		String schema = NameUtiles.prepend(_user, _volume);
		return search(schema, _query, 0, 200);
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
			GResults results = ResultsFactory.buildResults(_solrCore, _start, _rows, response);
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
			log.debug("Results are built.  Returning the results.");
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
				hibernateSession = dbcon.getSession();
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
				// hibernateSession.close();
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
			hibernateSession = dbcon.getSession();
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
		try {

			// we have found a proxy object.. and need to convert this into an
			// actual schema
			if (_schema.startsWith("com.tissuematch.tm3.mylib.TMLibraryItem")) {
				String last_item = LAC.parseLastTarget(_schema);
				Integer itemID = Integer.parseInt(last_item);
				TTable tm = getLibraries(itemID);
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

            Session hibernateSession = null;
			try {
				hibernateSession = dbcon.getSession();
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
			} finally {
				HBConnect.close(hibernateSession);
			}
			return sd;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		TableDescriptor sd = new TableDescriptor();
		sd.setSchema_found(false);
		sd.setMsg("Failed to connect to the schema: " + _schema);
		return sd;
	}

	public String getSpecimenDetails(String specimenId) {
        Session hibernateSession = null;
		try {
			hibernateSession = dbcon.getSession();
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
            hibernateSession = dbcon.getSession();
            hibernateSession.beginTransaction();
            for (int i = 0; i < libs.size(); i++) {
                TMURI tmuri = libs.get(i);

                hibernateSession.saveOrUpdate(tmuri);
            }
            hibernateSession.getTransaction().commit();
        } catch (Exception _ee) {
            _ee.printStackTrace();
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
				hibernateSession = dbcon.getSession();
				hibernateSession.beginTransaction();

				//
				Criteria cer = hibernateSession.createCriteria(TMURI.class);
				cer.add(Restrictions.eq("owner", _owner));
				cer.add(Restrictions.eq("connectionType", _connectionType));
				List queryList = cer.list();
				for (int i = 0; i < queryList.size(); i++) {
					TMURI tr = (TMURI) queryList.get(i);
					list.add(tr);
				}
				hibernateSession.close();
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
			hibernateSession = dbcon.getSession();
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
			hibernateSession = dbcon.getSession();
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
	 * unknown status of this method... currently this is not referenced in the
	 * gb code base.
	 * 
	 * @deprecated
	 */
	private LoadStatus createIndex(String _user, String _tablename,
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

//		try {



//			String res = sql_solr.run(_user, _tablename,
//					db_configuration.getName(), db_config, _schema_config,
//					_query, job_id+"", new GBJobListener() {
//				public void jobComplete(String msg) {
//				}
//			});
//			l.setMsg(res);
//		} catch (DBProcessFailedException e) {
//			e.printStackTrace();
//			l.setMsg(e.getLocalizedMessage());
//		}
//		return l;
		return null;
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
			sess = dbcon.getSession();
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

			session = dbcon.getSession();
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
		String msg = "ERROR saving table settings";
		Session hibernateSession = null;
		try {
			hibernateSession = dbcon.getSession();
			hibernateSession.beginTransaction();
			Criteria cr = hibernateSession.createCriteria(TTable.class);
			cr.add(Restrictions.eq("itemID", _lib_item_id));
			List ls = cr.list();
			if (ls.size() <= 0) {
				return "Failed to find the table.";
			} else {
				TTable li = (TTable) ls.get(0);
				String _schema = li.getTitle();
				if (!_schema.contains("_Repository_"))
					_schema = NameUtiles.prepend(li.getUser(), _schema);

				String solr_url = ABProperties.get(ABProperties.SOLRSITE);
				// http://localhost:8983/solr/admin/cores?action=add_field&table=milton_Repository_v_c_studies&field_name=hello&field_type=sint
				boolean bb = TMSolrServer
						.callSolr(solr_url
								+ "/admin/cores?action=add_field&table="
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

				if (hibernateSession.isOpen())
					hibernateSession.close();
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
				hibernateSession = dbcon.getSession();
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

	public String createTable(String _userid, String _name, GResults _r) {
		ResultMetaData desc = _r.getResultDescriptor();
		ArrayList<GRow> rows = _r.getValues();

		HashMap<String, Map<String, String>> _params = new HashMap<String, Map<String, String>>();
		log.debug("Building...");
		GRow r = rows.get(0);
		HashMap<String, String> vtypes = r.getDataTypes();
		if (r == null)
			return "No results found table not created";

		ArrayList<String> order = r.getOrder();
		for (String or : order) {
			String data_type = vtypes.get(or);
			HashMap<String, String> uuidp = new HashMap<String, String>();
			uuidp.put("fieldName", or);
			uuidp.put("sortable", "true");
			uuidp.put("indexed", "true");
			uuidp.put("defaultString", "");
			uuidp.put("dataType", data_type);
			uuidp.put("requiredField", "true");
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
		log.debug("Generating the solr schema... NAME: " + _name);
		String site = ABProperties.getSolrURL();
		return TMSolrServer.createSchema(_userid, site, _name, _params, false);
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
		last_updated.put("defaultString",CurrentTimeForSolr.timeStr());
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
			TTable item = getLibraries(it);

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
			session = dbcon.getSession();
			session.beginTransaction();
			Criteria cr = session.createCriteria(TNode.class);
			cr.add(Restrictions.like("node_id", _node_id));
			List<TNode> l = cr.list();

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
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public static void testTableState() {
		String _schema = "_milton.sample_collaborations";
		TableManagerPlugin impl = new TableManagerPlugin();
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

	public String addColumn(String _schema, String _field_name, String _type) {
		try {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			// http://localhost:8983/solr/admin/cores?action=add_field&table=milton_Repository_v_c_studies&field_name=hello&field_type=sint
			boolean bb = TMSolrServer.callSolr(solr_url
					+ "/admin/cores?action=add_field&table=" + _schema
					+ "&field_name=" + _field_name + "&field_type=" + _type);

			TTable item = getLibraryBySchema(_schema);
			if (item != null) {
				TMTableSettings sets = item.getSettings();
				String state = sets.getState();
				Gson sog = new Gson();
				ArrayList mmls = sog.fromJson(state, ArrayList.class);
				// debug output
				// for (Object ob : mmls) {
				// HashMap map = (HashMap) ob;
				// Set<String> keys = map.keySet();
				// for (String k : keys) {
				// Object obb = map.get(k);
				// System.out.println ( " key : " + k + " value = " + obb);
				// if (obb != null)
				// System.out.println(" value type " +
				// obb.getClass().getCanonicalName());
				//
				// }
				// }
				java.util.LinkedHashMap lh = new LinkedHashMap<String, Object>();
				lh.put("name", _field_name);
				lh.put("width", new Double(200));
				mmls.add(1, lh);
				String newjson = sog.toJson(mmls);
				sets.setState(newjson);
				saveTableSettings(sets);

			}

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
	 * 
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
			hibernateSession = dbcon.getSession();
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

			session = dbcon.getSession();
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
	 * 
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
			hibernateSession = dbcon.getSession();
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
			hibernateSession = dbcon.getSession();
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

		ArrayList<String> lis = new ArrayList<String>();
        HttpSolrClient solr = null;
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
//	private void addDataFromHTML(String table_name,
//			LinkedHashMap<String, Map<String, String>> fields,
//			String _dataString) {
//
//		_dataString = _dataString.replace("&nbsp", " ");
//
//		org.jsoup.nodes.Document doc = Jsoup.parse(_dataString);
//		List<org.jsoup.nodes.Node> list = doc.childNodes();
//		for (org.jsoup.nodes.Node n : list) {
//			org.jsoup.nodes.Node table = find(n, "table");
//			if (table != null) {
//				List<org.jsoup.nodes.Node> child = table.childNodes();
//				int index = 0;
//				ArrayList<GRow> rows = new ArrayList<GRow>();
//
//				for (org.jsoup.nodes.Node ch : child) {
//					org.jsoup.nodes.Node row = find(ch, "tr");
//					if (row != null) {
//						// skip the first row.
//						if (index > 0) {
//							ArrayList<String> ls = parseStringData(row);
//							Set<String> fi = fields.keySet();
//							HashMap data = new HashMap();
//							GRow _row = new GRow();
//							int i = 0;
//							for (String col : fi) {
//								Object value = ls.get(i);
//								data.put(col, value);
//								i++;
//							}
//							_row.setData(data);
//							rows.add(_row);
//						}
//
//						// loop over the rows
//						index++;
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * Recursive find on the nodes.
//	 * 
//	 * @param _node
//	 * @param _name
//	 * @return
//	 */
//	public org.jsoup.nodes.Node find(org.jsoup.nodes.Node _node, String _name) {
//		if (_node.nodeName().equalsIgnoreCase(_name)) {
//			return _node;
//		}
//		List<org.jsoup.nodes.Node> list = _node.childNodes();
//		for (org.jsoup.nodes.Node n : list) {
//			String node_name = n.nodeName();
//			if (node_name.equalsIgnoreCase(_name))
//				return n;
//			else {
//				org.jsoup.nodes.Node ch = find(n, _name);
//				if (ch != null) {
//					return ch;
//				}
//			}
//		}
//		return null;
//	}
//
//	private LinkedHashMap<String, Map<String, String>> buildFieldsFrom_HTMLTable(
//			String _line) {
//		LinkedHashMap<String, Map<String, String>> fields = new LinkedHashMap<String, Map<String, String>>();
//		org.jsoup.nodes.Document doc = Jsoup.parse(_line);
//		List<org.jsoup.nodes.Node> list = doc.childNodes();
//		for (org.jsoup.nodes.Node n : list) {
//			org.jsoup.nodes.Node table = find(n, "table");
//			if (table != null) {
//				List<org.jsoup.nodes.Node> child = table.childNodes();
//				for (org.jsoup.nodes.Node ch : child) {
//					// find the first row
//					org.jsoup.nodes.Node row = find(ch, "tr");
//					if (row != null) {
//						fields = parseFields(row);
//						break;
//					}
//				}
//			}
//		}
//		return fields;
//	}

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
//	private LinkedHashMap<String, Map<String, String>> parseFields(
//			org.jsoup.nodes.Node row) {
//
//		LinkedHashMap<String, Map<String, String>> fields = new LinkedHashMap<String, Map<String, String>>();
//
//		List<org.jsoup.nodes.Node> cols = row.childNodes();
//		for (org.jsoup.nodes.Node col : cols) {
//			if (col.nodeName().equalsIgnoreCase("td")) {
//				Attributes attrs = col.attributes();
//				List<org.jsoup.nodes.Node> l = col.childNodes();
//				for (org.jsoup.nodes.Node ln : l) {
//
//					log.debug("node name :   " + ln.nodeName());
//					if (ln.nodeName().equalsIgnoreCase("#text")) {
//						String value = NameUtiles.convertToValidCharName(ln
//								.toString());
//						log.debug(" value :   " + value);
//						Map<String, String> map_value = buildStringField(value);
//						fields.put(value, map_value);
//					}
//					System.out.println(" --" + ln.toString());
//				}
//			}
//		}
//		return fields;
//	}
//
//	/**
//	 * Pull out the data as an array of string from the row
//	 * 
//	 * @param row
//	 * @return
//	 */
//	private ArrayList<String> parseStringData(org.jsoup.nodes.Node row) {
//		LinkedHashMap<String, Map<String, String>> fields = new LinkedHashMap<String, Map<String, String>>();
//		List<org.jsoup.nodes.Node> cols = row.childNodes();
//		ArrayList<String> row_ = new ArrayList<String>();
//		for (org.jsoup.nodes.Node col : cols) {
//			if (col.nodeName().equalsIgnoreCase("td")) {
//				Attributes attrs = col.attributes();
//				List<org.jsoup.nodes.Node> l = col.childNodes();
//
//				for (org.jsoup.nodes.Node ln : l) {
//					log.debug("node name :   " + ln.nodeName());
//					if (ln.nodeName().equalsIgnoreCase("#text")) {
//						String value = NameUtiles.convertToValidCharName(ln
//								.toString());
//						row_.add(value);
//					}
//					System.out.println(" --" + ln.toString());
//				}
//			}
//		}
//		return row_;
//	}

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

}
