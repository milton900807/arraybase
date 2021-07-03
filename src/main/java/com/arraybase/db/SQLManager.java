package com.arraybase.db;

import com.arraybase.flare.InMemoryJobManager;
import com.arraybase.flare.parse.GBParseException;
import com.arraybase.io.ABQFile;
import com.arraybase.tm.GColumn;
import com.arraybase.util.GBLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SQLManager {

	private static GBLogger log = GBLogger.getLogger(SQLManager.class);

	/**
	 * @param reader
	 * @return
	 * @throws GBParseException
	 * @throws IOException
	 */
	public HashMap<String, String> parseSql(BufferedReader reader)
			throws GBParseException, IOException {
		String selectStatement = "";
		String g = reader.readLine();
		while (g != null) {
			g = g.trim();
			// {{ PARSE THE SELECT STATEMENT }}
			if (g.startsWith("select ")) {
				while (g != null) {
					selectStatement += g;
					// if the line contains a from statement
					if (g.contains(" from ")) {
						selectStatement = reader.readLine();
						break;
					}
					g = reader.readLine();
				}
			}
			g = reader.readLine();
		}
		if (selectStatement != null && selectStatement.length() > 0)
			return getSelectValues(selectStatement);
		else
			throw new GBParseException(
					"Failed to find the select statement in the file.  Please edit the file and make sure the "
							+ "select statement starts on it's own line.  ");
	}

	public static ArrayList<GColumn> desc(Map<String, String> abq) {
		ArrayList<GColumn> coll = new ArrayList<GColumn>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = createConnection(abq, ""+900807);
			if (con != null) {
				String url = abq.get(ABQFile.URL);
				String query = abq.get(ABQFile.QUERY);
				// we are just going to search the first row... since that's all
				// we need
				// to get a handle on the metadata associated with the query.
				String sql = processQuery(query, url, 0, 0, 1, 1);
				st = con.createStatement();
				rs = st.executeQuery(sql);
				ResultSetMetaData rm = rs.getMetaData();
				int column_count = rm.getColumnCount();

				for (int i = 0; i < column_count; i++) {
					int type = rm.getColumnType((1 + i));
					String type_name = rm.getColumnTypeName(i + 1);
					String field_name = rm.getColumnName(i + 1);
					GColumn column = new GColumn(field_name, type_name);
					coll.add(column);
				}
			}

			return coll;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(con);
		}
		return coll;
	}

	public static Connection createConnection(Map<String, String> scope_config,
			String job_id) throws SQLException {
		String url = scope_config.get(ABQFile.URL);
		String pass = scope_config.get(ABQFile.PASSWORD);
		String user = scope_config.get(ABQFile.USER);
		String driver = scope_config.get(ABQFile.DRIVER_CLASS);
		InMemoryJobManager.log(job_id, "DB Connection: connecting... " + url);
		return createConnection(url, pass, user, driver, job_id);
	}

	private static Connection createConnection(String url, String pass,
			String user, String _driver, String _job_id) throws SQLException {
		return JDBC.createConnection(url, pass, user, _driver, _job_id);
	}

	private HashMap<String, String> getSelectValues(String selectStatement) {
		// we need to convert the string of values
		String[] vals = selectStatement.split("\\s+");

		// do some debug printing.
		for (String v : vals) {
			log.debug(" vals : " + v);
		}
		return null;
	}

	public static String processQuery(String query, String url, int run_index,
			int start, int end, int increment) {
		int i_start = (run_index * start);
		int i_end = (run_index * start) + increment;

		String q = query;
		// the increment for the select is done differently for
		if (url.startsWith("jdbc:mysql")) {
			i_start = (run_index * increment) + start;
			i_end = increment;
			if (q.contains("$start")) {
				q = query.replace("$start", "" + i_start);
				q = q.replace("$increment", "" + i_end);
			} else {
				q = q + " limit " + i_start + ", " + i_end;
			}
		} else {

			i_start = (run_index * increment) + start;
			i_end = i_start + increment;
			q = buildOracleQuery(q, i_start, increment);
		}
		log.info("q: " + q);
		return q;
	}

	/**
	 * 
	 * @param q
	 * @return
	 */
	public static String buildOracleQuery(String q, int start, int length) {
		q = q.trim();
		if (q.startsWith("select")) {
			q = q.replaceFirst("select", "select ROWNUM,");
		}
		String query = "select * from " + "(select a.*, ROWNUM r__ from " + ""
				+ "(" + q + ") a where ROWNUM <" + (start + length)
				+ ") where r__ >=" + start;
		return query;
	}

	public static ArrayList<GColumn> desc(LinkedHashMap<String, String> map,
			String[] exported_values) {
		ArrayList<GColumn> exported_rows = new ArrayList<GColumn> ();
		ArrayList<GColumn> raw = desc ( map );
		for ( String exported : exported_values ){
			for ( GColumn gc : raw ){
				if ( gc.getName().equalsIgnoreCase(exported)){
					exported_rows.add(gc);
				}
			}
		}
		return exported_rows;
	}

}
