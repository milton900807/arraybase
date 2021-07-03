package com.arraybase.tm;

public class SolrConfigTemplate {

	public static final String fieldTemplate = " <field name=\"`NAME`\" type=\"`TYPE`\" indexed=\"`INDEXED`\" stored=\"`STORED`\" required=\"`REQUIRED`\" multiValued=\"`MULTI`\" /> ";
	public static final String copyTemplate = "<copyField source=\"`NAME`\" dest=\"allterms\" />";

}
