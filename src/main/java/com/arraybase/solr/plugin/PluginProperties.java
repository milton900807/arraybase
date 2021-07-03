package com.arraybase.solr.plugin;

public class PluginProperties {

	private static GAdmin admin = null;
	private static String solrHome = null;
	
	public static void setAdmin(GAdmin gAdmin) {
		admin = gAdmin;
	}
	
	public static void setSolrHome(String _solrHome) {
		solrHome = _solrHome;
	}

}
