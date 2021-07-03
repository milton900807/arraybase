package com.arraybase.db.jdbc;

import com.arraybase.db.JDBC;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

public class JDBCUtil {

	private static GBLogger log = GBLogger
			.getLogger(JDBCUtil.class);

	public static Connection createConnection() {
		String db_connect_string = ABProperties.get("jdbcConnection");
		log.info(" creating the db connection : " + db_connect_string);
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection connection = DriverManager.getConnection(
					db_connect_string, "tp", "tp");
			if (connection != null) {
				log.info(" connection : " + db_connect_string + " successful ");
			}
			return connection;
		} catch (Exception _e) {
			_e.printStackTrace();
		}

		return null;
	}

	public static Connection createConnection(String _usr, String _pss,
			String _url, String _driver) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		if (_driver == null)
			_driver = "com.mysql.jdb.Driver";
		Class.forName(_driver).newInstance();
		Connection connection = DriverManager.getConnection(_url, _usr, _pss);
		if (connection != null) {
			log.info(" connection : " + _url + " successful ");
		}
		return connection;
	}

	private static Connection createConnection(String _schema) {
		String db_connect_string = ABProperties.get("jdbcConnection");
		log.info(" creating the db connection : " + db_connect_string);
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection _connection = DriverManager.getConnection(
					db_connect_string, "tp", "tp");
			if (_schema != null)
				_connection.setCatalog(_schema);

			if (_connection != null) {
				log.info(" creating the db connection : " + db_connect_string
						+ " successful ");
			} else {
				log.error(" creating the db connection : " + db_connect_string
						+ " failed ");
			}
			return _connection;
		} catch (Exception _e) {
			_e.printStackTrace();
			log.error(" creating the db connection : " + db_connect_string
					+ " failed ");
		}
		return null;
	}

	static HashMap<String, Statement> map = new HashMap<String, Statement>();

	public static JSONObject search(String catalog, String table, String col,
			String searchString, int _start, int _limit, int _totalCount,
			String _id) {
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			connection = createConnection(catalog);
			st = map.get(_id);
			if (st != null) {
				st.cancel();
			} else {
				st = connection.createStatement();
				map.put(_id, st);
			}

			String sql = "select distinct " + col + " from " + table
					+ " where " + col + " like '%" + searchString + "%'";

			if (searchString == null || searchString.length() <= 0) {
				sql = "select distinct " + col + " from " + table;
			}

			if (_totalCount > 0) {
				sql = sql + " limit " + _start + "," + _limit;
			}

			System.out.println(" " + sql);

			rs = st.executeQuery(sql);

			JSONArray jarray = new JSONArray();
			int index = 0;
			int start = _start;
			System.out.println(" start : " + start);
			while (rs.next()) {
				if (index >= start && (index) < (start + _limit)) {
					String s = rs.getString(1);
					JSONObject jo = new JSONObject();
					jo.put("key", s);
					jarray.put(jo);
				}
				index++;
			}
			int totalCount = index;
			JSONObject root = new JSONObject();
			root.put("root", jarray);
			root.put("totalCount", totalCount);
			System.out.println(root);
			map.remove(_id);
			st.close();
			connection.close();
			return root;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(connection);
		}
		return null;
	}

	public static Connection createConnection(String _url, String _driver) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String[] ur = _url.split("?");
		StringTokenizer st  = new StringTokenizer ( ur[1], "&");
		Properties pr = new Properties ();
		while ( st.hasMoreTokens() ){
			String element = st.nextToken ();
			String[] k_v = element.split("=");
			pr.put(k_v[0], k_v[1]);
		}
		return createConnection ( pr.getProperty("user"), pr.getProperty("password"), _url, _driver);
	}

}
