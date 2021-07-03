package com.arraybase.db;

import com.arraybase.io.GBBlobFile;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Delegatte for database connections. This permits us to extend the connection
 * framework.
 * 
 * @author donaldm
 * 
 */
public class DBConnectionManager {

	public DBConnectionManager() {

	}

	public Connection getJDBCConnection() {
		return JDBC.createConnection();
	}

	public Connection createConnection() {
		return JDBC.createConnection();
	}

	public Session getSession() {
		return HBConnect.getSession();
	}

	public void close() {
		HBConnect.close();
		JDBC.close();
	}

	public GBBlobFile loadFile(String fileuri) {
		try {
			return JDBC.loadFile(fileuri);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * TODO: implement the naming scheme for this framework
	 * 
	 * @return
	 */
	public String getConnectionName() {

		return "DBc";

	}


}
