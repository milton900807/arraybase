package com.arraybase.db.jdbc;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DBConfigFactory {

	public static DBConfiguration createConfig(String type,
			Map<String, String> _config) {

		DBConfiguration db_config = new DBConfiguration(type);

		// {{ FIRST CHECK FOR ANY CONFIG }}
		HashMap<String, String> config = new HashMap<String, String>();
		if (type.equalsIgnoreCase("sample_hub")) {
		} else if (type.equalsIgnoreCase("watsonlims")) {
		} else if (type.startsWith("UCSC_Genome_Browser")) {
		} else if (type.equalsIgnoreCase("Gene Ontology")) {
		} else if (type.equalsIgnoreCase("pathlims")) {
		} else if (type.equalsIgnoreCase("ipd")) {
		} else if (type.equalsIgnoreCase("local_dev_shadw")) {
		} else if (type != null && type.equalsIgnoreCase("local_dev")) {
		} else if (type.equalsIgnoreCase("mvp")) {
		} else if (type.equalsIgnoreCase("any")) {

			String db = _config.get("database_type");
			String host = _config.get("host");
			String driver = _config.get("driver_class");
			String port = _config.get("port");
			String user = _config.get("user");
			String pass = _config.get("password");
			String database = _config.get("database");

			if (db.equalsIgnoreCase("Mysql")) {
				String url = "jdbc:" + db + "://" + host + ":" + port + "/"
						+ database + "?" + "user=" + user + "&password=" + pass;
				config.put("url", url);
				config.put("driver_class", "com.mysql.jdbc.Driver");
			} else {
				String url = "jdbc:oracle:thin:" + user + "/" + pass + "@"
						+ host + ":" + port + ":" + host;
				config.put("url", url);
				config.put("driver_class", "oracle.jdbc.driver.OracleDriver");
			}
			config.put("user", user);
			config.put("password", pass);
			config.put("start", "0");
			config.put("increment", "10000");
		} else {
			Set<String> keys = _config.keySet();
			for (String s : keys) {
				config.put(s, _config.get(s));
			}
		}
		db_config.setConfig(config);
		return db_config;
	}
}
