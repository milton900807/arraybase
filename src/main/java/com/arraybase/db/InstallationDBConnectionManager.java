package com.arraybase.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.Session;

import com.arraybase.io.GBBlobFile;
import com.arraybase.modules.InstallHBConnection;

public class InstallationDBConnectionManager extends DBConnectionManager {

	private JDBCConnection con = null;

	public InstallationDBConnectionManager(File config_file, Properties _p) {
		InstallHBConnection.init(config_file);
		con = new JDBCConnection(_p);
	}

	public Connection getJDBCConnection() {
		return con.getConnection();
	}

	public Connection createConnection() {
		return con.createConnection();
	}

	public Session getSession() {
		return InstallHBConnection.getSession();
	}

	public void close(HBType type) {
		InstallHBConnection.close(type);
	}

	public Session getSession(HBType type) {
		return InstallHBConnection.getSession();
	}

	public void close() {
		InstallHBConnection.close();
	}

	public GBBlobFile loadFile(String fileuri) {
		try {
			return con.loadFile(fileuri);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
