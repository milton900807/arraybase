package com.arraybase.patches.scripts;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.JDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PathRootFix {

	
	/**
	 *  Script to fix the path roots
	 * @param _args
	 */
	public static void main(String[] _args) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			DBConnectionManager dbcm = new DBConnectionManager();
			con = dbcm.getJDBCConnection();
			String paths = "select path_name, group_name, path_id from ab_path";

			st = con.createStatement();
			rs = st.executeQuery(paths);
			while (rs.next()) {

				String path_name = rs.getString(1);
				String group_name = rs.getString(2);
				long path_id = rs.getLong(3);
				System.out.println(path_name);
				if (path_name.contains("/")) {
					int first_index = path_name.indexOf('/', 2);
					if (first_index > 0) {
						String root = path_name.substring(1, first_index);
						if (!root.equals(group_name)) {
							String update = "update ab_path set group_name='"
									+ root + "' where path_id=" + path_id;
							System.out.println(update);
							Statement st2 = con.createStatement();
							st2.execute(update);
							st2.close();
						}
					}
				}

			}

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(con);
		}
	}
}
