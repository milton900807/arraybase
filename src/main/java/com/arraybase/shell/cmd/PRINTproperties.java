package com.arraybase.shell.cmd;

import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.JDBC;
import com.arraybase.util.ABProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class PRINTproperties implements GBPlugin {

	public String exec(String command, String variable_key) {

		Properties pr = ABProperties.getProperties();
		Set key = pr.keySet();
		System.out.println("Number of ABProperties: " + pr.size());
		for (Object k : key) {
			System.out.println("K-" + key.toString() + " : "
					+ pr.getProperty(k.toString()));
		}
		String solrurl = ABProperties.getSolrURL();
		System.out.println("Solr URL: " + solrurl);
		System.out.println(" Solr HOME: " + ABProperties.getSolrHome());

		DBConnectionManager cb = new DBConnectionManager();
		Connection conn = cb.getJDBCConnection();
		if (conn != null) {
			try {
				System.out.println(" We have a valid jdbc connection : " + conn.getCatalog());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JDBC.closeConnection(conn);

		return "complete";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
