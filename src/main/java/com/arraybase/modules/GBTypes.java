package com.arraybase.modules;

import java.util.LinkedHashMap;
import java.util.Map;

public class GBTypes {

	public final static String STRING = "String";
	public final static String INT = "int";
	public final static String DOUBLE = "double";
	public final static String FLOAT = "float";
	public final static String TEXT = "text";
	public final static String BOOLEAN = "boolean";
	public final static String NULL = "null";

	// need to add the xls bug to the list jira..
	public static Map<String, String> getSolrTypes(String[] sp) {
		LinkedHashMap<String, String> mapped = new LinkedHashMap<String, String>();
		for (String s : sp) {

			s = s.trim();
			String[] line = s.split("\\\r+");
			String t = line[0];
			String n = line[1];

			if (t.equalsIgnoreCase("int")) {
				mapped.put(n, "sint");
			} else if (t.equalsIgnoreCase("String")) {
				mapped.put(n, "string");
			} else {
				mapped.put(n, t.toLowerCase());
			}
		}
		return mapped;
	}

	// need to add the xls bug to the list jira..
	public static boolean isValidType(String s) {
		String temp = s.trim();

		return (temp.equalsIgnoreCase("string"))
				|| (temp.equalsIgnoreCase("integer"))
				|| (temp.equalsIgnoreCase("string_ci"))
				|| (temp.equalsIgnoreCase("sfloat"))
				|| (temp.equalsIgnoreCase("sint"))
				|| (temp.equalsIgnoreCase("sfloat"))
				|| (temp.equalsIgnoreCase("slong"))
				|| (temp.equalsIgnoreCase("text_ws"))
				|| (temp.equalsIgnoreCase("text"))
				|| (temp.equalsIgnoreCase("textTight"))
				|| (temp.equalsIgnoreCase("textSpell"))
				|| (temp.equalsIgnoreCase("int"))
				|| (temp.equalsIgnoreCase("long"))
				|| (temp.equalsIgnoreCase("double"))
				|| (temp.equalsIgnoreCase("float"))
				|| (temp.equalsIgnoreCase("boolean"))
				|| (temp.equalsIgnoreCase("date"));
	}
}
