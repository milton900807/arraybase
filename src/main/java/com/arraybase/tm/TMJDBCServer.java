package com.arraybase.tm;

import com.arraybase.SearchConfig;
import com.arraybase.db.JDBC;
import com.arraybase.flare.ErrorLog;
import com.arraybase.flare.LoaderException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TMJDBCServer {

	private static GBLogger log = GBLogger.getLogger(TMJDBCServer.class);

	public TMJDBCServer() {
	}

	private Connection createConnection(Map<String, String> scope_config)
			throws SQLException {
		String url = scope_config.get("url");
		String pass = scope_config.get("password");
		String user = scope_config.get("user");
		String driver = scope_config.get("driver_class");
		return createConnection(url, pass, user, driver);
	}

	/**
	 * Method to encapsulate the various ways of making a jdbc connection given
	 * a url.
	 * 
	 * @param url
	 * @param pass
	 * @param user
	 * @param _driver
	 * @return
	 * @throws SQLException
	 */
	private Connection createConnection(String url, String pass, String user,
			String _driver) throws SQLException {
		Connection conn = null;

		// if we have a driver load it.
		try {
			if (_driver != null)
				Class.forName(_driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException(" Driver was not found for the class : "
					+ _driver);
		}

		if (url.indexOf(user) > 0 && url.indexOf(pass) > 0) {
			conn = DriverManager.getConnection(url);
		} else {

			if (!url.endsWith("?"))
				url += "?";

			conn = DriverManager.getConnection("" + url + "user=" + user
					+ "&password=" + pass);
		}
		return conn;
	}

	public static void main(String[] args) {

		TMJDBCServer server = new TMJDBCServer();

		HashMap<String, String> config = new HashMap<String, String>();
		// String url = scope_config.get("url");
		// String pass = scope_config.get("password");
		// String user = scope_config.get("user");
		// String driver = scope_config.get("driver_class");
		config.put("url",
				"jdbc:mysql://localhost/shadw?user=shadw&password=shadw");
		config.put("driver_class", "com.mysql.jdbc.Driver");
		config.put("user", "shadw");
		config.put("password", "shadw");
		String table_name = "milton_Repository_patient_samples";
		String _sql = "update gridbase_test set name=$TISSUE_NAME where bioid=$RNM";
//		String _sql = "insert into gridbase_test set name=$TISSUE_NAME where bioid=$RNM";

		// THE CODE IN THE TEST METHOD IS VERY GOOD. ..IT DOES PASS THE TEST... THERE ARE
		// SOME SQL STRING STHAT WILL
		// BREAK GIVEN THIS PARSING STRUCTURE.. BUT 90% WORKS WELL.. AND THAT'S
		// 100% OF WHAT I AM DOING.
		// insert tokens
//			test (_sql);
		//
		try {
			server.updateDB(config, table_name, "TISSUE_NAME:liver", _sql);
		} catch (LoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void test(String _sql) {
		_sql = _sql.toLowerCase();
		int insert_end = _sql.indexOf("where");
		int set_start = _sql.indexOf("set") + 3;
		String _insert_ = _sql.substring(set_start, insert_end);
		ArrayList<String> field_names = new ArrayList<String>();
		_insert_ = _insert_.trim();
		Pattern p2 = Pattern.compile("[^A-Za-z0-9 ]");
		System.out.println(" _insert " + _insert_);
		Matcher m = p2.matcher(_insert_);
		while (m.find()) {
			_insert_ = _insert_.replaceAll("[\\W]|_", " ");
		}
		int index = 0;
		StringTokenizer str = new StringTokenizer(_insert_);
		while (str.hasMoreTokens()) {
			String next_token = str.nextToken();
			if (index % 2 == 1) {
				field_names.add(next_token.trim());
			}
			index++;
		}

		index = 0;
		ArrayList<String> where_fields = new ArrayList<String>();
		String where_string = _sql.substring(insert_end + 5);
		Matcher m2 = p2.matcher(where_string);
		while (m2.find()) {
			where_string = where_string.replaceAll("[\\W]|_", " ");
		}
		System.out.println(" where string" + where_string);
		StringTokenizer str2 = new StringTokenizer(where_string);
		while (str2.hasMoreTokens()) {
			String token = str2.nextToken();
			if (index % 2 == 1) {
				where_fields.add(token);
			}
			index++;
		}

		String sql = _sql;
		for (String s : field_names) {
			System.out.println(" field names :" + s);
			sql = sql.replace("$" + s, "?");
		}
		for (String s : where_fields) {
			System.out.println(" where names : " + s);
			sql = sql.replace("$" + s, "?");
		}

		System.out.println(sql);
		
	}

	public void updateDB(Map<String, String> db_connection, String _table_name,
			String _search, String _sql) throws LoaderException {

		Connection con = null;
		PreparedStatement st = null;
		ErrorLog el = new ErrorLog();
		try {
			log.debug("\n\n\n\t Append the schema with the new fields\n\n");

			TMSolrServer solr = new TMSolrServer(
					ABProperties.get(ABProperties.SOLRSITE));
			// ArrayList<SolrInputDocument> docs = new
			// ArrayList<SolrInputDocument>();
			// http://localhost:8983/solr/admin/cores?action=CREATE&name=coreX&instanceDir=path_to_instance_directory&config=config_file_name.xml&schema=schem_file_name.xml&dataDir=data
			// make the jdbc connection here.
			con = createConnection(db_connection);
			// Statement st = con.createStatement();
			// insert tokens
			_sql = _sql.toLowerCase();
			_sql = _sql.toLowerCase();
			int insert_end = _sql.indexOf("where");
			int set_start = _sql.indexOf("set") + 3;
			String _insert_ = _sql.substring(set_start, insert_end);
			ArrayList<String> field_names = new ArrayList<String>();
			_insert_ = _insert_.trim();
			Pattern p2 = Pattern.compile("[^A-Za-z0-9]");
			System.out.println(" _insert " + _insert_);
			Matcher m = p2.matcher(_insert_);
			while (m.find()) {
				_insert_ = _insert_.replaceAll("[\\W]", " ");
			}
			int index = 0;
			StringTokenizer str = new StringTokenizer(_insert_);
			while (str.hasMoreTokens()) {
				String next_token = str.nextToken();
				if (index % 2 == 1) {
					field_names.add(next_token.trim());
				}
				index++;
			}

			index = 0;
			ArrayList<String> where_fields = new ArrayList<String>();
			String where_string = _sql.substring(insert_end + 5);
			Matcher m2 = p2.matcher(where_string);
			while (m2.find()) {
				where_string = where_string.replaceAll("[^A-Za-z0-9_]", " ");
			}
			System.out.println(" where string" + where_string);
			StringTokenizer str2 = new StringTokenizer(where_string);
			while (str2.hasMoreTokens()) {
				String token = str2.nextToken();
				if (index % 2 == 1) {
					where_fields.add(token);
				}
				index++;
			}

			String sql = _sql;
			for (String s : field_names) {
				System.out.println(" field names :" + s);
				sql = sql.replace("$" + s, "?");
			}
			for (String s : where_fields) {
				System.out.println(" where names : " + s);
				sql = sql.replace("$" + s, "?");
			}
			System.out.println(sql);
			System.out.println(sql);

			st = con.prepareStatement(sql);
			int increment = 10;
			int t_index = 0;
			boolean complete = false;

			while (!complete) {
				// DO THE SEARCH
				GResults r = TMSolrServer
						.search(_table_name, _search, "", t_index, increment,
								new SearchConfig(SearchConfig.RAW_SEARCH));
				if (r == null) {
					log.info("\n\n results are null  for " + _table_name
							+ " Param : " + _search + " t_index: " + t_index);
					break;
				}

				HashMap<String, String> insert_map = new HashMap<String, String>();
				HashMap<String, String> where_map = new HashMap<String, String>();

				ArrayList<GColumn> col_property = r.getColumns();
				for (GColumn p : col_property) {
					String datatype = p.getType();
					for (String f : field_names) {
						if (f.equalsIgnoreCase(p.getName())) {
							insert_map.put(f, datatype);
						}
					}
					for (String w : where_fields) {
						if (w.equalsIgnoreCase(p.getName())) {
							where_map.put(w, datatype);
						}
					}
				}
				// we need to throw an exception if the types are null;

				t_index += increment;
				ArrayList<GRow> rows = r.getValues();
				if (rows.size() < increment) {
					complete = true;
				}
				// loop over the rows.
				for (GRow row : rows) {
					HashMap row_d = row.getData();
					Set<String> s = row_d.keySet();
					for (String ke : s) {
						// loop over the insert fields first
						Object value = row_d.get(ke);
						for (int i = 0; i < field_names.size(); i++) {
							String ik = field_names.get(i);
							if (ke.equalsIgnoreCase(ik)) {
								String type = insert_map.get(ik);
								updatePrepSt(st, i, type, value);
							}
						}
						// where clause
						for (int i = 0; i < where_fields.size(); i++) {
							String ik = where_fields.get(i);
							if (ke.equalsIgnoreCase(ik)) {
								String type = where_map.get(ik);
								updatePrepSt(st, (field_names.size () + i), type, value);
							}
						}
					}
					log.debug( "prep : " + st.toString() );
					st.executeUpdate();
					// next row
				}
				
				complete =true;
				if (el.count() > 0)
					throw new LoaderException(el);
				
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeStatement(st);
            JDBC.closeConnection(con);
		}
	}

	// because jdbc idiots didn't know that programmers start with 0... we have to add one to
	// the index.
	private void updatePrepSt(PreparedStatement st, int parameterIndex,
			String type, Object value) throws SQLException {
		if (type.equalsIgnoreCase("text") || type.equalsIgnoreCase("string")) {
			st.setString(parameterIndex+1, value.toString());
		}else if ( type.equalsIgnoreCase("int") || type.equalsIgnoreCase("sint") || 
				type.equalsIgnoreCase("integer")){
			st.setInt(parameterIndex+1, Integer.parseInt(value.toString()));
		}
	}
}
