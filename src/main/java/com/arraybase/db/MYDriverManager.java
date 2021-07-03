package com.arraybase.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.arraybase.GB;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MYDriverManager {

	private Connection con = null;
	private DataSource dataSource;
	private String DRIVER_NAME;
	private String URL;
	private String UNAME;
	private String PWD;

	public MYDriverManager() {

	}

	public MYDriverManager(String user, String pass, String driver, String url) {
		config(url, user, pass, driver);
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null) {
//			GB.print("Driver: " + DRIVER_NAME);
			dataSource = setupDataSource();
		}
		return dataSource.getConnection();
		
	}


	public void config(String _url, String _name, String _pass, String _driver) {
		DRIVER_NAME = _driver;
		URL = _url;
		UNAME = _name;
		PWD = _pass;
		dataSource = setupDataSource();
	}

	private DataSource setupDataSource() {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			if (DRIVER_NAME == null)
				DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
			cpds.setDriverClass(DRIVER_NAME.trim());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setJdbcUrl(URL);
		cpds.setUser(UNAME);
		cpds.setPassword(PWD);
		cpds.setMaxPoolSize(20);
		return cpds;
	}

	void close() {
		try {
			if (con != null)
				JDBC.closeConnection(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}