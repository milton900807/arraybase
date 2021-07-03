package com.arraybase.db;

import com.arraybase.GB;
import com.arraybase.io.GBBlobFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

public class JDBCConnection {
	private Connection connection = null;
	private static Properties prop = null;

	public JDBCConnection(Properties _p) {
		prop = _p;
		GB.print(prop);

	}

	public Connection createDefaultConnection() {
		String url = prop.getProperty("url");
		String pass = prop.getProperty("pass");
		String user = prop.getProperty("user");
		String driver = prop.getProperty("driver");
		if (driver == null)
			driver = "com.mysql.jdbc.Driver";
		try {
			GB.print(prop);
			Connection connection = createConnection(url, pass, user, driver, 0);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Connection createConnection() {
		String url = prop.getProperty("url");
		String pass = prop.getProperty("pass");
		String user = prop.getProperty("user");
		String driver = prop.getProperty("driver");
		if (driver == null)
			driver = "com.mysql.jdbc.Driver";
		try {
			Connection connection = createConnection(url, pass, user, driver, 0);
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
											  String user, String _driver, int _job_id) throws SQLException {
		Connection conn = null;
		try {
			if (_driver != null){
				System.out.println ( "\n\n\n\n\n \t\t\t\t loading driver : " + _driver + "\n\n\n\n");
				Class.forName(_driver);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException(" Driver was not found for the class : "
					+ _driver);
		}
		if (pass == null && url.indexOf(user) > 0) {
			if (url.contains("password=")) {
				url = url.replace("password=", "");
			}
			if (url.contains("null")) {
				url = url.replace("null", "");
			}
			if (url.endsWith("&"))
				url = url.substring(0, url.length() - 1);
			url = url.trim();

			conn = DriverManager.getConnection(url);
		} else if (url.indexOf(user) > 0 && url.indexOf(pass) > 0) {
			conn = DriverManager.getConnection(url);
		} else {

			if (_driver.contains("oracle")) {
				conn = DriverManager.getConnection(url, user, pass);
			} else if (!url.endsWith("?")) {
				url += "?";
				conn = DriverManager.getConnection("" + url + "user=" + user
						+ "&password=" + pass);
			}
		}
		return conn;
	}

	/**
	 * @deprecated This is now pulled from the database. If you need to
	 *             construct the database for this please run:
	 * 
	 *             GB setproperties admin
	 */
	// private static void loadResourceFile___() {
	// String config_file = "ab.properties";
	// URL resource = HBConnect.class.getClassLoader()
	// .getResource(config_file);
	// if (resource != null) {
	// try {
	// ABProperties.load(resource.openStream());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	public GBBlobFile loadFile(String fileuri) throws SQLException {
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
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(connection);
		}
		return null;
	}

	public Connection getConnection() {
		return connection;
	}

}
