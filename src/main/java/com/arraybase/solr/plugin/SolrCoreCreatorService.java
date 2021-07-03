package com.arraybase.solr.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arraybase.solr.plugin.jaxb.JaxbUtils;
import com.arraybase.solr.plugin.jaxb.Schema;



public class SolrCoreCreatorService {

	private Logger log = LoggerFactory.getLogger(SolrCoreCreatorService.class);
	
	private static final String LIST_SEPARATOR = ";";

	private String solrHome;
	
	private static final String CONFIG_FOLDER = "conf";
	private static final String SCHEMA_XML = "managed-schema";
	private static final String SOLRCONFIG_XML = "solrconfig.xml";
	
	private static final String SOLR_TEMPLATE_DIR = "configTemplates" + File.separator;
	private static final String SOLR_CONFIG_SCHEMA_XSD = SOLR_TEMPLATE_DIR + "schema.xsd";
	private static final String RES_TEMPLATE_SCHEMA_XML = SOLR_TEMPLATE_DIR + "template_schema.xml";
	private static final String RES_SOLRCONFIG_XML =  SOLR_TEMPLATE_DIR + "template_solrconfig.xml";
	
	public SolrCoreCreatorService() {
		super();
	}
	public SolrCoreCreatorService(String solrHome) {
		super();
		this.solrHome = solrHome;
	}
	

	public void createSolrCoreConfigStructure(String coreName,
			String instanceDir, String[] fields, String[] types) {
		log.info("CREATE STUB CORE:{} in {} with fields:{} and types:{}", coreName, instanceDir,fields,types);
		createNewSolrFolderStructure(coreName, instanceDir);
		// Generate schema.xml
		Schema schema = createNewSolrSchema(coreName, fields, types);

		JaxbUtils.jaxbToFile(schema, getSchemaPath(instanceDir, coreName),
				SOLR_CONFIG_SCHEMA_XSD);
		// Generate solrconfig.xml
		generateSolrConfigXml(instanceDir, coreName);

	}
	
	private void createNewSolrFolderStructure(String coreName, String parentPath) {
		File parent = new File(parentPath);
		if (!parent.exists()) {
			throw new RuntimeException("Folder " + parentPath + " does not exist!");
		}
		String coreFolderPath = parentPath + File.separator + coreName;
		File coreFolder = new File(coreFolderPath);
		if(!coreFolder.exists()) {
			boolean created = coreFolder.mkdir();
			if (!created) {
				throw new RuntimeException("Folder " + coreFolderPath + " has not been created!");
			}
		}
		String confFolderPath = getConfigFolder(parentPath, coreName);
		
		File confFolder = new File(confFolderPath);
		if(!confFolder.exists()) {
			confFolder.mkdir();			
		}
		confFolder.setWritable(true);

	}
	
	public Schema getTemplateSchema() {
		InputStream is = getFileInputStream(RES_TEMPLATE_SCHEMA_XML);
		return (Schema) JaxbUtils.inputStreamToJaxb(is);
	}

	private Schema createNewSolrSchema(String coreName, String[] fields,
			String[] types) {
		int fieldLen = fields.length;
		if (fieldLen != types.length) {
			throw new RuntimeException("Number of fields(" + fieldLen
					+ ") is different than number of types(" + types.length
					+ ")!");
		}
		Schema schema = getTemplateSchema();
		schema.setName(coreName);
		for (int i = 0; i < types.length; i++) {
			schema.addField(fields[i], types[i]);
		}

		schema.setUniqueKey(fields[0]);
		schema.setDefaultSearchField(fields[1]);
		return schema;
	}
	
	private String getConfigFolder(String parentPath, String coreName) {
		return parentPath + File.separator + coreName + File.separator + CONFIG_FOLDER; 
	}
	
	private String getSchemaPath(String parentPath, String coreName) {
		return getConfigFolder(parentPath, coreName) + File.separator + SCHEMA_XML; 
	}
	
	private String getSolrConfigPath(String parentPath, String coreName) {
		return getConfigFolder(parentPath, coreName) + File.separator + SOLRCONFIG_XML; 
	}
	

	// transfer data from template file to core solrconfig file
	private void generateSolrConfigXml(String parentPath, String coreName) {
		String solrConfigPath = getSolrConfigPath(parentPath, coreName);
		// OUTPUT
		File solrConfigFile = new File(solrConfigPath);

		//INPUT
		InputStream configTemplateInput = getFileInputStream(RES_SOLRCONFIG_XML);
		try {
			FileOutputStream outStr = new FileOutputStream(solrConfigFile);
			//Files.copy(configTemplateIS, solrConfigFile);
			IOUtils.copy(configTemplateInput, outStr);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private InputStream getFileInputStream(String filePath) {
		//Original idea: ServiceFactory.getResourceInputStream(filePath);
		File file = new File(solrHome + File.separator + filePath);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return fis;
		
	}
	
	
	
	private Map<String, String> typesMap;

	private Map<String, String> getTypesMap() {
		if (typesMap == null) {
			typesMap = new HashMap<String, String>();
			typesMap.put("logical", "boolean");
			typesMap.put("string", "string");
			typesMap.put("factor", "string");
			typesMap.put("integer", "int");
			typesMap.put("int", "int");
			typesMap.put("numeric", "float");
			typesMap.put("float", "float");
		}
		return typesMap;
	}

	// Read R language object types from parameter
	// and covnert them to corrensponding Solr Types
	private String[] convertTypes(SolrParams params) {
		String typeList = params.get("typeList");
		String[] typesArr = typeList.split(LIST_SEPARATOR);
		log.info("typeList from R: {}", StringUtils.join(typesArr, ";"));
		for (int i = 0; i < typesArr.length; i++) {
			typesArr[i] = getTypesMap().get(typesArr[i]);
		}
		log.info("Converted Solar Types:{}", StringUtils.join(typesArr, ";"));
		return typesArr;
	}
}
