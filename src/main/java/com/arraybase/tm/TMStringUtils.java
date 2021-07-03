package com.arraybase.tm;

public class TMStringUtils {

	public static String trimLead(String instance_directory, String solr_home) {
		if (instance_directory.startsWith(solr_home))
			instance_directory = instance_directory.substring(solr_home
					.length());
		return instance_directory;
	}

	public static String trimLead(String instance_directory) {
		if (instance_directory.startsWith("./"))
			instance_directory = instance_directory.substring(2);
		if (instance_directory.startsWith("/"))
			instance_directory = instance_directory.substring(1);
		return instance_directory;
	}
}
