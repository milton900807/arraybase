package com.arraybase.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.arraybase.GBModule;
import com.arraybase.GBUtil;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.file.GBFileStructure;
import com.arraybase.util.ABProperties;

public class SearchIndexInstaller implements GBModule {

	// sd.addField(GBFileStructure.FILE_NAME.name(), _name);
	// sd.addField(GBFileStructure.HEADER.name(), header);
	// sd.addField(GBFileStructure.CONTENT_TYPE.name(), content_type);
	// sd.addField(GBFileStructure.ATTRIBUTES.name(), header);
	// sd.addField(GBFileStructure.CONTENT.name(), file_content);
	// sd.addField(GBFileStructure.AUTHORS.name(), authors);
	// sd.addField(GBFileStructure.DATE_LAST_MODIFIED.name(), new Date());
	// sd.addField(GBFileStructure.DATE_CREATED.name(), new Date());

	
	public String getModName ()
	{
		return "Searchindex builder";
	}
	
	public void exec(Map<String, Object> l) throws UsageException
	{
		throw new UsageException ( "This is not implemented. ");
	}
	/**
	 * we need the solrindex
	 */
	public void exec(List<String> params) throws UsageException {

		HashMap<String, Map<String, String>> p = new HashMap<String, Map<String, String>>();
		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
		HashMap<String, String> path = new HashMap<String, String>();
		path.put("fieldName", GBFileStructure.PATH.name);
		path.put("sortable", "true");
		path.put("indexed", "true");
		path.put("defaultString", "");
		path.put("dataType", "string");
		path.put("requiredField", "false");
		p.put(GBFileStructure.PATH.name, path);

		HashMap<String, String> content = new HashMap<String, String>();
		content.put("fieldName", GBFileStructure.CONTENT.name);
		content.put("sortable", "false");
		content.put("indexed", "true");
		content.put("defaultString", "");
		content.put("dataType", "text");
		content.put("requiredField", "false");
		p.put(GBFileStructure.CONTENT.name, content);

		HashMap<String, String> file_name = new HashMap<String, String>();
		file_name.put("fieldName", GBFileStructure.FILE_NAME.name);
		file_name.put("sortable", "true");
		file_name.put("indexed", "true");
		file_name.put("defaultString", "");
		file_name.put("dataType", "string");
		file_name.put("requiredField", "false");
		p.put(GBFileStructure.FILE_NAME.name, file_name);

		HashMap<String, String> description = new HashMap<String, String>();
		description.put("fieldName", GBFileStructure.DESCRIPTION.name);
		description.put("sortable", "false");
		description.put("indexed", "true");
		description.put("defaultString", "");
		description.put("dataType", "text");
		description.put("requiredField", "false");
		p.put(GBFileStructure.DESCRIPTION.name, description);

		HashMap<String, String> ids = new HashMap<String, String>();
		ids.put("fieldName", GBFileStructure.IDS.name);
		ids.put("sortable", "true");
		ids.put("indexed", "true");
		ids.put("defaultString", "unknown");
		ids.put("dataType", "string");
		ids.put("requiredField", "false");
		p.put(GBFileStructure.IDS.name, ids);

		HashMap<String, String> datecreated = new HashMap<String, String>();
		datecreated.put("fieldName", GBFileStructure.DATE_CREATED.name);
		datecreated.put("sortable", "true");
		datecreated.put("indexed", "true");
		datecreated.put("defaultString", "unknown");
		datecreated.put("dataType", "date");
		datecreated.put("requiredField", "false");
		p.put(GBFileStructure.DATE_CREATED.name, datecreated);

		HashMap<String, String> mime = new HashMap<String, String>();
		mime.put("fieldName", GBFileStructure.MIME.name);
		mime.put("sortable", "true");
		mime.put("indexed", "true");
		mime.put("defaultString", "unknown");
		mime.put("dataType", "string");
		mime.put("requiredField", "false");
		p.put(GBFileStructure.MIME.name, mime);

		HashMap<String, String> author = new HashMap<String, String>();
		author.put("fieldName", GBFileStructure.AUTHORS.name);
		author.put("sortable", "true");
		author.put("indexed", "true");
		author.put("defaultString", "unknown");
		author.put("dataType", "string");
		author.put("requiredField", "false");
		p.put(GBFileStructure.AUTHORS.name, author);

		HashMap<String, String> title = new HashMap<String, String>();
		title.put("fieldName", GBFileStructure.TITLE.name);
		title.put("sortable", "true");
		title.put("indexed", "true");
		title.put("defaultString", "unknown");
		title.put("dataType", "string");
		title.put("requiredField", "false");
		p.put(GBFileStructure.TITLE.name, title);
		String site = ABProperties.getSearchCore();
		String solrurl = GBUtil.getSolrRootURL(site);
		TMSolrServer.clearCore ( site );
		TMSolrServer.createSchema("Administrator", solrurl, "search_index", p,
				false);

		if (params == null)
			throw new UsageException("install searchindex");
	}

}
