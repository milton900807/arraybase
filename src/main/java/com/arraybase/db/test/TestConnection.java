package com.arraybase.db.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.arraybase.db.JDBC;

public class TestConnection {

	public static void main(String[] args) {

		File f = new File("db.abq");
		Properties pr = new Properties();
		try {
			pr.load(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
