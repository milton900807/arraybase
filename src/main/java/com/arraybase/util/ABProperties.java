package com.arraybase.util;

import com.arraybase.GB;
import com.arraybase.db.Configuration;
import com.arraybase.db.GBConfigurationException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

public class ABProperties {
	private static GBLogger log = GBLogger.getLogger(ABProperties.class);
	public static final String PUBLISHED_CORE = "_Repository__published_libs_";
	public static final String SOLRSITE = "solrSite";
	public static final String SEARCH_CORE = "searchCore";
	private static Properties props = Configuration.loadResources();
	private static String solr_home;

	public ABProperties(String test_for_ab_config) throws GBConfigurationException {
		props = Configuration.loadResources(test_for_ab_config);
	}
	public ABProperties() {
		// TODO Auto-generated constructor stub
	}
	private void printProperties(Properties _props) {
		Set<Object> keys = _props.keySet();
		for (Object key : keys) {
			System.out.println("Key : " + key.toString() + " Value : "
					+ _props.get(key));
		}
	}

	public static String getSolrURL() {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		return solr_url;
	}

	public static Properties getProperties() {
		return props;
	}
	
	
	public static void setSolrSite ( String _url )
	{
		log.config( "Setting the global SOLRSITE: "+ SOLRSITE + "=" + _url);
		props.put ( SOLRSITE, _url);
	}
	

	/**
	 * Please NOTE: There is a special case if the name is "solrRoot". For this
	 * case it will first check to see if there is in fact a variable solrRoot
	 * stored. If not it will try to get this as a system property from the
	 * system variable: solr.solr.home
	 * 
	 * @param name
	 * @return
	 */
	public static String get(String name) {

		if ( props == null ){
			GB.print ( " No properties found... please check the configuration ");
			props = Configuration.loadResources();
			if ( props == null ) {
				return null;
			}
		}
		String value = props.getProperty(name);
		if (value != null)
			return value;
		if (name.equals("solrRoot")) {
			value = System.getenv("Dsolr.solr.home");
		}
		if (value == null)
			value = ".";
		return value;
	}

	public static boolean contains(String propname) {
		return propname != null && getProperties().contains(propname);
	}

	public static boolean containsKey(String propname) {
		return propname != null && getProperties().containsKey(propname);
	}

	public static String getSolrHome() {
		return solr_home;
	}

	private void setProperties(Properties p) {
		props = p;
	}
	private void setProperties(String key, String _value) {
		props.put ( key, _value);
	}

	public static String get(String _prop, String _qual) {
		return props.getProperty(_prop);
	}

	public static String readFileAsString(URL file) {
		StringBuffer data = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					file.openStream()));
			char[] buf = new char[1024];
			int numRead;
			while ((numRead = reader.read(buf)) != -1) {
				data.append(buf, 0, numRead);
			}
			return data.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(reader);
		}
		return null;
	}

	/**
	 * Load a file into a string from the resources directory
	 * 
	 * @param resource_file
	 * @return
	 */
	public static String readFileAsString(String resource_file) {
		URL file = Configuration.class.getClassLoader().getResource(
				resource_file);
		StringBuffer data = new StringBuffer(1000);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					file.openStream()));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				data.append(buf, 0, numRead);
			}
			reader.close();
			return data.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSearchCore() {
		String solr_url = ABProperties.get(ABProperties.SEARCH_CORE);
		if (solr_url == null) {
			String s = getSolrURL() + "search_index";
			props.put(SEARCH_CORE, s);
			return s;
		}
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		return solr_url;
	}

}
