package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.db.JDBC;
import com.arraybase.flare.SQLToSolr;
import com.arraybase.io.ABQFile;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class Diff implements GBPlugin {

	public String exec(String command, String variable_key) {

		String[] l = LAC.parse(command);
		String target = l[0];
		String data = l[2];

		String local_file = GB.getLocalPath().getPath() + "/" + data.trim();
		String gb_file = GB.pwd() + "/" + target.trim();

		File f = new File(local_file);
		Properties paf = new Properties();
		try {
			paf = ABQFile.load(f);
		} catch (IOException e) {
			GB.print("IO Error: Failed to load the abq file.  Appears I cannot read it.");
			e.printStackTrace();
		} catch (UsageException e) {
			e.printStackTrace();
			GB.print("IO Error: Failed to load the abq file.  Appears I cannot read it.");
		}
		Map<String, String> af = convert_(paf);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		// we have to make sure the schema match
		try {
			ArrayList<GColumn> list = GB.describeTable(gb_file);
			list = GBSearch.removeTrackingColumns(list);
			conn = SQLToSolr.createConnection(af, new Date().toString());
			st = conn.createStatement();
			String query = af.get(ABQFile.QUERY);
			String url = paf.getProperty(ABQFile.URL);
			query = SQLToSolr.setRange(url, query, 0, 1);
			rs = st.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();

			int col_count = meta.getColumnCount();
			int col_count2 = list.size();

			int count_m = Math.max(col_count, col_count2);

			for (int index = 1; index < count_m; index++) {

				String column_name = "[null]";
				String type = "";

				if (index < col_count) {
					type = meta.getColumnTypeName(index);
					column_name = meta.getColumnName(index);
					if (index < list.size()) {
						GColumn column = list.get(index);
						GB.print(type + ":" + column_name + " --> "
								+ column.getType() + ":" + column.getName());
					} else {
						GB.print(type
								+ ":"
								+ column_name
								+ " --> does not map to existing schema and will not be loaded");
					}
				} else {
					if (index < list.size()) {
						GColumn column = list.get(index);
						GB.print("No field available --> " + column.getType()
								+ ":" + column.getName());
					} else {
						GB.print(type
								+ ":"
								+ column_name
								+ " --> does not map to existing schema and will not be loaded");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(conn);
		}

		return "diff";
	}

	private Map<String, String> convert_(Properties paf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		Set keys = paf.keySet();
		for (Object k : keys) {
			values.put(k.toString(), paf.getProperty(k.toString()));
		}
		return values;
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
