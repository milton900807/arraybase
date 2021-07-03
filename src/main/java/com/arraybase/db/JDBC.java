package com.arraybase.db;

import com.arraybase.io.GBBlobFile;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JDBC {
	private static GBLogger log = GBLogger.getLogger(JDBC.class);
	private static HashMap<String, MYDriverManager> pool = new HashMap<String, MYDriverManager>();

	/**
	 * @deprecated
	 * @return
	 * @throws SQLException
	 */
	public static Connection getDefaultConnection() throws SQLException {
		Connection connection = createConnection();
		if (connection == null || connection.isClosed())
			connection = createDefaultConnection();
		return connection;
	}

	/**
	 * @deprecated
	 * @return
	 * @throws SQLException
	 */
	public static Connection createDefaultConnection() {
		String url = ABProperties.get("url");
		String pass = ABProperties.get("pass");
		String user = ABProperties.get("user");
		String driver = ABProperties.get("driver");
		if (driver == null)
			driver = "com.mysql.jdbc.Driver";
		try {
			Connection connection = createConnection(url, pass, user, driver, 0+"");
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Connection createConnection() {
		String url = ABProperties.get("url");
		String pass = ABProperties.get("pass");
		String user = ABProperties.get("user");
		String driver = ABProperties.get("driver");
		
		
		try {
			Connection connection = createConnection(url, pass, user, driver, 0+"");
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

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
	public static Connection createConnection(String url, String pass,
			String user, String _driver, String _job_id) throws SQLException {
		if (_driver == null || _driver.length() <= 0)
			_driver = "oracle.jdbc.driver.OracleDriver";
		String hashKey = url + user + _driver + _job_id;

		synchronized (pool) {

			MYDriverManager md = pool.get(hashKey);

			if (md != null)
				return md.getConnection();
			log.config("Connecting : " + url);
			log.config("\n");
			try {
				if (_driver != null) {
					log.config ( " loading the driver " + _driver );
					Class.forName(_driver);
				log.config("\t " + _driver  + " found and loaded.");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new SQLException(" Driver was not found for the class : "
						+ _driver);
			}
			url = url.trim();
			if (md == null) {
				md = new MYDriverManager(user, pass, _driver, url);
				pool.put(hashKey, md);
			}
			log.config ( " Connecting.... " + user + " " + pass + " url " + url );
			Connection c = md.getConnection();
			log.config("Connection complete");
			return c;
		}
	}

	public static void closeConnection(Connection con) {
		try {
			if ((con != null)) {
				con.close();
			}
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Error occured when closing database connection");
		}
	}

	public static void closeStatement(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeResultSet(ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static GBBlobFile loadFile(String fileuri) throws SQLException {
		Connection connection = getDefaultConnection();
		Statement st = null;
		ResultSet rs = null;
		try {
			int ids_index = fileuri.lastIndexOf('/');
			String ids = fileuri.substring(ids_index + 1);
			if (ids == null)
				return null;
			else
				ids = ids.trim();
			Integer id = Integer.parseInt(ids);
			String sql = "select file_id, attachment1, attachment_desc, attachment_name,"
					+ ""
					+ " last_saved_by_usr_id, last_updated_date from ab_raw_files where file_id="
					+ id;
			st = connection.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				long ll = rs.getLong(1);

				InputStream in = rs.getBinaryStream(2);
				ByteArrayOutputStream bin = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[100000];

				while ((nRead = in.read(data, 0, data.length)) != -1) {
					bin.write(data, 0, nRead);
				}

				bin.flush();
				byte[] atch = bin.toByteArray();
				// Blob blob = rs.getBlob(2);
				String desc = rs.getString(3);
				String attcn = rs.getString(4);
				String userid = rs.getString(5);
				Date d = rs.getDate(6);

				GBBlobFile bl = new GBBlobFile();

				bl.setAttachment_name(attcn);
				bl.setAttachment1(atch);
				bl.setAttachment_desc(desc);
				bl.setFile_id(ll);
				bl.setLast_saved_by_usr_id(userid);
				bl.setLast_updated_date(d);
				return bl;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(connection);
		}
		return null;
	}

	public static Connection createConnection(Map<String, String> p, int job_id) {
		String url = p.get("url");
		String pass = p.get("pass");
		String user = p.get("user");
		String driver = p.get("driver");
		if (driver == null)
			driver = "com.mysql.jdbc.Driver";
		try {
			Connection connection = createConnection(url, pass, user, driver, 0+"");
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void close() {
		synchronized (pool) {
			Set<String> conns = pool.keySet();
			for (String c : conns) {
				MYDriverManager con = pool.get(c);
				if (con != null) {
					con.close();
				}
			}
		}

	}
}
