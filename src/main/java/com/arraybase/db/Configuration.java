package com.arraybase.db;

import com.arraybase.GB;
import com.arraybase.shell.iterminal.c.interal.Log;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class Configuration {

	public static final String GBPLUGIN = "gbplugin";
	public static final String LOCAL2 = "local2";
	public static final String LOCAL3 = "local3";
	public static final String OS = "OS";

	public static String LOCAL = "local";
	public static String DEV = "dev";
	public static String TST = "TST";
	public static String PRD = "prd";

	static GBLogger log = GBLogger.getLogger(Configuration.class);

	private static Properties loadConfig(File f) {
		Log.debug( "Loading properties from  "  + f.getAbsolutePath().toLowerCase()  );
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String s = reader.readLine();
			boolean jdbc_props = false;
			boolean hb_props = false;
			String jdbc_b = new String();
			String hib_b = new String();
			while (s != null) {
				Log.debug ( "\t\t "  + s );


				if (jdbc_props)
					jdbc_b += s + "\n";
				if (hb_props)
					hib_b += s + "\n";
				if (s.startsWith("#jdbc.config")) {
					jdbc_props = true;
					hb_props = false;
				}
				if (s.startsWith("#hb.config")) {
					jdbc_props = false;
					hb_props = true;
				}
				s = reader.readLine();
			}
			Properties pr = new Properties();
			pr.load(new StringReader(jdbc_b));
			pr.put("hibernate", hib_b);
			return pr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(reader);
		}
		return null;
	}

	public static Properties loadResources() {

//		GB.print ("Loading properties....");

		String properties_file = "ab.properties";
		String hibernate_file = "hibernate.cfg.xml";
		if (GB.VERSION == Configuration.OS) {
			String system_property = System.getProperty("user.home");
			File f = new File(system_property, ".ab.config");
			return loadConfig(f);
		} else if (GB.VERSION.equals(Configuration.LOCAL)) {
			properties_file = "ab.local.properties";
			hibernate_file = "hb.local.cfg.xml";
		} else if (GB.VERSION.equals(DEV)) {
			properties_file = "ab.dev.properties";
			hibernate_file = "hb.dev.cfg.xml";
		} else if (GB.VERSION.equals(TST)) {
			properties_file = "ab.tst.properties";
			hibernate_file = "hb.tst.cfg.xml";
		} else if (GB.VERSION.equals(PRD)) {
			properties_file = "ab.prd.properties";
			hibernate_file = "hb.prd.cfg.xml";
		} else {
			properties_file = "ab." + GB.VERSION + ".properties";
			hibernate_file = "hb." + GB.VERSION + ".cfg.xml";
		}

		Properties local = new Properties();
		URL resource = Configuration.class.getClassLoader().getResource(
				properties_file);
		if (resource != null) {
			try {
				
				local.load(resource.openStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		URL hibernate_resource = Configuration.class.getClassLoader()
				.getResource(hibernate_file);
		try {
			String hb_props = ABProperties.readFileAsString(hibernate_resource);
			// System.out.println( hb_props );

			local.put("hibernate", hb_props);
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		GB.print ( "Loading default configuration.\n\t" + properties_file);

		return local;
	}


	public static URL getResource(String _filename) {
		try {
			URL resource = Configuration.class.getClassLoader().getResource(
                    _filename);

			return resource;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public static String getResourceAsString(String _filename) {
		URL url = getResource(_filename);
//		System.out.println( " + " + _filename + " " + url.getPath());
        InputStream in = null;
        BufferedReader bufferedReader = null;
		try {
			in = url.openStream();
			StringBuilder inputStringBuilder = new StringBuilder();
		    bufferedReader = new BufferedReader(new InputStreamReader(in));
			String line = bufferedReader.readLine();
			while (line != null) {
				inputStringBuilder.append(line);
				inputStringBuilder.append('\n');
				line = bufferedReader.readLine();
			}
			return inputStringBuilder.toString();
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            IOUTILs.closeResource(bufferedReader);
            IOUTILs.closeResource(in);
        }
        return null;
	}

	public static Properties loadResources(String  _ab_config) throws GBConfigurationException {
		
		File f = new File (_ab_config);
		if (!f.exists ()){
			throw new GBConfigurationException ( _ab_config + " not found or is not accessible. ");
		}
		return loadConfig(f);
	}

}
