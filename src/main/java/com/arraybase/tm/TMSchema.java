package com.arraybase.tm;

import com.arraybase.GBUtil;
import com.arraybase.db.Configuration;
import com.arraybase.flare.CurrentTimeForSolr;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMID;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.solr.plugin.GAdmin;
import com.arraybase.util.ABFileUtils;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.schema.TrieDateField;
import org.apache.solr.servlet.SolrRequestParsers;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author donaldm
 */
public class TMSchema {

    private static GBLogger log = GBLogger.getLogger(TMSchema.class);

    public static HashMap<String, Map<String, String>> appendTMFields(
            HashMap<String, Map<String, String>> _params) {
        // {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
        HashMap<String, String> uuidp = new HashMap<String, String>();
        uuidp.put("fieldName", "TMID");
        uuidp.put("sortable", "true");
        uuidp.put("indexed", "true");
        uuidp.put("defaultString", TMID.create());
        uuidp.put("dataType", "string");
        uuidp.put("requiredField", "true");
        _params.put("TMID", uuidp);
        Date dd = new Date();
        HashMap<String, String> last_updated = new HashMap<String, String>();
        last_updated.put("fieldName", "TMID_lastUpdated");
        last_updated.put("sortable", "true");
        last_updated.put("indexed", "true");
        last_updated.put("defaultString",CurrentTimeForSolr.timeStr());
        last_updated.put("dataType", "date");
        last_updated.put("requiredField", "true");
        _params.put("TMID_lastUpdated", last_updated);
        return _params;
    }
    // {{ THE JOIN OPERATION WAS REMOVED ON 05.19.2015 IN ORDER TO CLEAN UP AND REFACTOR ARRAYBASE. }}
//
//	public static SchemaDescriptor createJoinSchema(String new_table_name,
//			HashMap<String, String> aliases, SchemaDescriptor left_d,
//			SchemaDescriptor right_d, GAdmin _admin) {
//		Map<String, HashMap<String, String>> fields_l = left_d.getFields();
//		Map<String, HashMap<String, String>> fields_r = right_d.getFields();
//		Set<String> keysl = fields_l.keySet();
//		Set<String> keysr = fields_r.keySet();
//		HashMap<String, HashMap<String, String>> params = new HashMap<String, HashMap<String, String>>();
//		log.debug("\n\n LOADING THE ALIAS KEYS\n ");
//		for (String l : keysl) {
//			String new_value = aliases.get("l." + l);
//
//			log.debug(l + " ==> : " + new_value);
//
//			if (new_value != null) {
//				HashMap<String, String> add_field = createField(fields_l, l,
//						new_value);
//				params.put(new_value, add_field);
//			}
//		}
//		for (String r : keysr) {
//			String new_value = aliases.get("r." + r);
//			log.debug(r + " ==> : " + new_value);
//			if (new_value != null) {
//				HashMap<String, String> add_field = createField(fields_r, r,
//						new_value);
//				params.put(new_value, add_field);
//			}
//		}
//		buildSchema(new_table_name, params, true, _admin);
//		log.debug(" \t\t\t " + new_table_name + " is the core name ");
//		CoreContainer container = _admin.getCoreContainer();
//		// SolrCore source_core = container.getCore(new_core_name);
//		// need to load the core
//		SolrCore sc = container.getCore(new_table_name);
//		if (sc == null) {
//			log.debug(" The core is null " + "");
//		} else
//			log.debug(" WE HAVE THE SOLR CORE ");
//
//		SchemaDescriptor scd = new SchemaDescriptor(sc);
//		return scd;
//	}

    // {{ THE JOIN OPERATION WAS REMOVED ON 05.19.2015 IN ORDER TO CLEAN UP AND REFACTOR ARRAYBASE. }}
//	private static HashMap<String, String> createField(
//			Map<String, HashMap<String, String>> fields_l, String l,
//			String new_value) {
//		HashMap<String, String> uuidp = fields_l.get(l);
//		uuidp.put("fieldName", "" + new_value);
//		// uuidp.put("sortable", "true");
//		// uuidp.put("indexed", "true");
//		// uuidp.put("defaultString", "");
//		// uuidp.put("dataType", "text");
//		// multiValued="true"
//		// uuidp.put ( "multiValued", "false");
//		uuidp.put("requiredField", "false");
//		return uuidp;
//	}
//	// {{ THE JOIN OPERATION WAS REMOVED ON 05.19.2015 IN ORDER TO CLEAN UP AND REFACTOR ARRAYBASE. }}
//	public static String buildSchema(String _name,
//			HashMap<String, HashMap<String, String>> _params,
//			boolean _defaultOperatorAND, GAdmin _admin) {
//		log.info("Building...");
//
//		// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
//		HashMap<String, String> uuidp = new HashMap<String, String>();
//		uuidp.put("fieldName", "TMID");
//		uuidp.put("sortable", "true");
//		uuidp.put("indexed", "true");
//		uuidp.put("defaultString", TMID.create());
//		uuidp.put("dataType", "string");
//		uuidp.put("requiredField", "true");
//		_params.put("TMID", uuidp);
//		Date dd = new Date();
//		HashMap<String, String> last_updated = new HashMap<String, String>();
//		last_updated.put("fieldName", "TMID_lastUpdated");
//		last_updated.put("sortable", "true");
//		last_updated.put("indexed", "true");
//		last_updated.put("defaultString", new TrieDateField().toExternal(time));
//		last_updated.put("dataType", "date");
//		last_updated.put("requiredField", "true");
//		_params.put("TMID_lastUpdated", last_updated);
//		log.info("Generating the solr schema... ");
//		return TMSchema.createSchemaXML(_name, _params, _defaultOperatorAND,
//				_admin);
//	}

    /***
     * createSchemalXML is designed to create the 2 XML files needed to add a
     * new index to a solr repository
     * <p/>
     * <p/>
     * This method is very similar to the TMSolrServer.createSchemaXML except
     * that this one is a different context, ( i.e. it is used in the solrplugin
     * context).
     * <p/>
     * Therefore, it requires a GAdmin argument.
     */
    public static String createSchemaXML(String indexPath,
                                         HashMap<String, HashMap<String, String>> inputParms,
                                         boolean defaultOperatorAND, GAdmin _admin) {
        // {{ WE NEED TO GET THE SOLR HOME VARIABLE }}
        String solr_home = System.getProperty("solr.solr.home");
        log.debug("Solr Home: " + solr_home);
        int l = indexPath.lastIndexOf("/");
        if (l == -1)
            l = indexPath.lastIndexOf("\\");
        String indexName = indexPath.substring(l + 1);
        if (!createConfFolder(indexPath)) {
            return "Conf folder creation problem. Aborting";
        }
        final String fieldTemplate = SolrConfigTemplate.fieldTemplate;
        final String copyTemplate = SolrConfigTemplate.copyTemplate;

        StringBuffer solr_template_data = new StringBuffer(1000);
        StringBuffer config_template_data = new StringBuffer(1000);

        // {{ FIRST WE NEED TO TRY TO GET THIS FROM THE plugin JAR }}
        URL config_template = Object.class.getClass().getResource(
                "solrconfig_template.xml");
        URL solr_template = Object.class.getClass().getResource(
                "solr_template.xml");
        if (config_template == null) {
            log.error("failed to load the solrconfig_template from the plugin.  We will now attempt to load it from the solr_home: "
                    + solr_home);
            read(solr_template_data, "solrconfig_template.xml");
        } else {
            // {{ config_template_data }}
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        config_template.openStream()));
                char[] buf = new char[1024];
                int numRead = 0;
                while ((numRead = reader.read(buf)) != -1) {
                    config_template_data.append(buf, 0, numRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUTILs.closeResource(reader);
            }
        }

        // {{ solr template }}
        if (solr_template == null) {
            log.error("failed to load the solr_template from the plugin.  We will now attempt to load it from the solr_home: "
                    + solr_home);
            read(solr_template_data, "solr_template.xml");
        } else {
            // {{ READ solr_template --> solr_template_data }}
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(solr_template.openStream()));
                try {
                    char[] buf = new char[1024];
                    int numRead = 0;
                    while ((numRead = reader.read(buf)) != -1) {
                        solr_template_data.append(buf, 0, numRead);
                    }
                } finally {
                    IOUTILs.closeResource(reader);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        String sConfigTemplate = config_template.toString();

        if (indexPath.indexOf("\\") > 0) {
            indexPath = indexPath.replaceAll("\\\\\\\\", "\\\\"); // first
            // revert
            // any
            // doubles
            // into
            // singles
            // to ensure
            // we get
            // all
            // doubles
            // in the
            // end
            indexPath = indexPath.replaceAll("\\\\", "\\\\\\\\"); // make
            // singles
            // doubles
        }

        sConfigTemplate = sConfigTemplate.replaceAll("`INDEX_NAME`", indexPath);

        try {
            FileReader fr = null;
            try {
                fr = new FileReader(solr_home + "/solr_template.xml");
            } catch (FileNotFoundException fne) {
                fr = new FileReader("solr_template.xml");
            }
            BufferedReader reader = new BufferedReader(fr);
            try {
                char[] buf = new char[1024];
                int numRead = 0;
                while ((numRead = reader.read(buf)) != -1) {
                    config_template_data.append(buf, 0, numRead);
                }
            } finally {
                IOUTILs.closeResource(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sTemplate = config_template_data.toString();
        sTemplate = sTemplate.replaceAll("`INDEX_NAME`", indexName);
        sTemplate = sTemplate
                .replaceAll("`DEFAULT_OPERATOR_DISABLE_BEGIN`", "");
        sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR_DISABLE_END`", "");
        if (defaultOperatorAND) {
            sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR`", "AND");
        } else {
            sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR`", "OR");
        }
        /*
		 * 
		 * $INDEX_NAME$
		 * 
		 * $DEFAULT_OPERATOR_DISABLE_BEGIN$ $DEFAULT_OPERATOR$
		 * $DEFAULT_OPERATOR_DISABLE_END$
		 * 
		 * $DEFAULT_SEARCH_FIELD_DISABLE_BEGIN$ $DEFAULT_SEARCH_FIELD$
		 * $DEFAULT_SEARCH_FIELD_DISABLE_END$
		 * 
		 * $FIELDS$
		 * 
		 * // caseControl (false) // dataType (string) // sortable (true) //
		 * indexed (true) // stored (true) // multiValued (false) //
		 * defaultSearchField (false) // requiredField (false)
		 */
        String allTerms = "";
        Iterator<String> ii = inputParms.keySet().iterator();
        while (ii.hasNext()) {

            String fieldLine = fieldTemplate;
            String copyLine = copyTemplate;

            Boolean caseSensitive = false;
            Boolean sortable = true;
            Boolean indexed = true;
            Boolean multi = false;
            Boolean requiredField = false;
            Boolean stored = true;

            String field = ii.next();
            HashMap<String, String> kvp = inputParms.get(field);
            String temp = "";

            // // {{ TAKE CARE OF UBER SOLR STUFF }}
            // String dictionary = kvp.get("dictionary");
            // if (dictionary != null) {
            // buildDictionaryConfiguration(field, indexPath, dictionary);
            // }

            temp = kvp.get("caseControl");
            if (temp != null && temp.equalsIgnoreCase("true"))
                caseSensitive = true;
            temp = kvp.get("sortable");
            if (temp != null && temp.equalsIgnoreCase("false"))
                sortable = false;
            temp = kvp.get("indexed");
            if (temp != null && temp.equalsIgnoreCase("false"))
                indexed = false;
            if (temp == null)
                indexed = true;

            temp = kvp.get("stored");
            if (temp != null && temp.equalsIgnoreCase("false"))
                stored = false;
            temp = kvp.get("multiValued");
            if (temp != null && temp.equalsIgnoreCase("true"))
                multi = true;
            temp = kvp.get("defaultSearchField");
            if (temp != null && temp.length() > 0) {
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_BEGIN`", "");
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_END`", "");
                sTemplate = sTemplate.replaceAll("`DEFAULT_SEARCH_FIELD`",
                        field);
                // sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                // "<uniqueKey required=\"false\"></uniqueKey>");

                if (inputParms.containsKey("TMID")) {
                    sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                            "<uniqueKey>TMID</uniqueKey>"); // not
                }
                // gonna
                // use
                // it
                // for
                // now,
                // as
                // copyFields
                // dont
                // work
                // as
                // unique
                // ids
            } else {
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_BEGIN`", "");
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_END`", "");
                sTemplate = sTemplate.replaceAll("`DEFAULT_SEARCH_FIELD`",
                        "allterms");
                // sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                // "<uniqueKey>allterms</uniqueKey>");
                if (inputParms.containsKey("TMID")) {
                    sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                            "<uniqueKey>TMID</uniqueKey>"); // not
                }
            }
            temp = kvp.get("requiredField");
            if (temp != null && temp.equalsIgnoreCase("true"))
                requiredField = true;
            temp = kvp.get("dataType");
            if (temp != null) {
                if (temp.equalsIgnoreCase("string")) {
                    if (caseSensitive)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "string");
                } else if (temp.equalsIgnoreCase("integer")) {
                    if (sortable)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
                } else if (temp.equalsIgnoreCase("float")) {
                    if (sortable)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "float");
                } else if (temp.equalsIgnoreCase("sint")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                } else if (temp.equalsIgnoreCase("sdouble")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sdouble");
                } else if (temp.equalsIgnoreCase("sfloat")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
                } else if (temp.equalsIgnoreCase("slong")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "slong");
                } else if (temp.equalsIgnoreCase("text_ws")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text_ws");
                } else if (temp.equalsIgnoreCase("text")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                } else if (temp.equalsIgnoreCase("textTight")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "textTight");
                } else if (temp.equalsIgnoreCase("textSpell")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "textSpell");
                } else if (temp.equalsIgnoreCase("int")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
                } else if (temp.equalsIgnoreCase("long")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "long");
                } else if (temp.equalsIgnoreCase("double")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "double");
                } else if (temp.equalsIgnoreCase("float")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "float");
                } else if (temp.equalsIgnoreCase("boolean")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "boolean");
                } else if (temp.equalsIgnoreCase("date")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "date");
                } else if (temp.equalsIgnoreCase("bigint")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                } else
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
                // if
                // they
                // pass
                // in
                // some
                // other
                // data
                // type
            } else
                fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
            // if
            // they
            // pass
            // in no
            // data
            // type
            fieldLine = fieldLine.replaceAll("`NAME`", field);
            copyLine = copyLine.replace("`NAME`", field);
            allTerms += "\t" + copyLine + "\n";
            fieldLine = fieldLine.replaceAll("`INDEXED`", (indexed ? "true"
                    : "false"));
            fieldLine = fieldLine.replaceAll("`STORED`", (stored ? "true"
                    : "false"));
            fieldLine = fieldLine.replaceAll("`REQUIRED`",
                    (requiredField ? "true" : "false"));
            fieldLine = fieldLine.replaceAll("`MULTI`", (multi ? "true"
                    : "false"));
            sTemplate = sTemplate.replaceAll("`FIELDS`", fieldLine
                    + "\n\t`FIELDS`");

        }
        sTemplate = sTemplate
                .replaceAll(
                        "`FIELDS`",
                        "<field name=\"allterms\" type=\"text\" indexed=\"true\" stored=\"false\" required=\"false\" multiValued=\"true\" />\n"
                                + allTerms);
        // String fldr = solrRoot + indexName + File.separator + "conf" +
        // File.separator;
        // {{ BUILD THE DICTIONARY STRING }}
        ii = inputParms.keySet().iterator();
        String dictionaries = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dictionaries>\n\n";
        while (ii.hasNext()) {
            String field = ii.next();
            HashMap<String, String> kvp = inputParms.get(field);
            String temp = "";
            String dict = kvp.get("dictionary");
            if (dict != null)
                dictionaries += getDictionaryFieldLine(dict, field);
        }
        dictionaries += "</dictionaries>";

        String fldr = solr_home + "/" + indexPath;
        if (!indexPath.endsWith(File.separator))
            fldr += File.separator;
        fldr += "conf" + File.separator;

        String fn = fldr + "schema.xml";
        String fn2 = fldr + "solrconfig.xml";
        String fn3 = fldr + "dictionary.xml";
        File dir = new File(fldr);
        if (!dir.exists())
            dir.mkdirs();
        printSupportingFiles(dir);
        log.debug("->Writting: " + fn);
        log.debug("->Writting: " + fn2);
        log.debug("->Writting: " + fn3);
        FileWriter fw = null;
        try {
            fw = new FileWriter(fn);
            fw.write(sTemplate);
            fw.close();

            fw = new FileWriter(fn2);
            fw.write(sConfigTemplate);
            fw.close();

            fw = new FileWriter(fn3);
            fw.write(dictionaries);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(fw);
        }
        indexPath = indexPath.replaceAll("\\\\\\\\", "\\\\"); // first revert
        // any doubles
        // into singles
        // to ensure we
        // get all
        // doubles in
        // the end
        SolrParams newCore_params = SolrRequestParsers
                .parseQueryString("action=CREATE&name=" + indexName
                        + "&instanceDir=" + indexPath);
        LocalSolrQueryRequest solrReq_create = new LocalSolrQueryRequest(null,
                newCore_params);
        if (_admin.createCoreAsGAdminPlugin(solrReq_create)) {

            return "Core has been created";
        } else
            return "Core creation failed.";
    }

    private static void printSupportingFiles(File conf_dir) {
        log.debug("\n\t\t-------writting to : " + conf_dir.getAbsolutePath());

        try {
            GBUtil.write(Configuration.getResourceAsString("conf/elevate.xml"),
                    new File(conf_dir, "elevate.xml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            GBUtil.write(
                    Configuration.getResourceAsString("conf/protwords.txt"),
                    new File(conf_dir, "protwords.txt"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //  thi sis no longer needed 02.26.2016
//		try {
//			GBUtil.write(Configuration.getResourceAsString("conf/schema.xml"),
//					new File(conf_dir, "schema.xml"));
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
        try {
            GBUtil.write(
                    Configuration.getResourceAsString("conf/solrconfig.xml"),
                    new File(conf_dir, "solrconfig.xml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            GBUtil.write(
                    Configuration.getResourceAsString("conf/stopwords.txt"),
                    new File(conf_dir, "stopwords.txt"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            GBUtil.write(
                    Configuration.getResourceAsString("conf/synonyms.txt"),
                    new File(conf_dir, "synonyms.txt"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private static void read(StringBuffer buffer, String _file) {
        String solr_home = ABProperties.get("solrRoot");
        try {
            // {{ WE NEED TO GET THE SOLR HOME VARIABLE }}
            FileReader fr = null;
            try {
                log.info("Loading the solr config template : " + solr_home
                        + "/" + "solrconfig_template.xml");
                fr = new FileReader(solr_home + "/" + _file);

            } catch (FileNotFoundException eee) {
                fr = new FileReader(_file);
            }
            BufferedReader reader = new BufferedReader(fr);
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                buffer.append(buf, 0, numRead);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            File f = new File(solr_home, _file);
            log.error(" Failed to load the file " + _file + " in the path "
                    + f.getAbsolutePath());
        }

    }

    public static Boolean createConfFolder(String indexPath) {
        String solrRoot = ABProperties.get("solrRoot");
        if (solrRoot == null) {
            solrRoot = ".";
        }
        log.info("solrRoot: " + solrRoot);

        if (!solrRoot.endsWith(File.separator))
            solrRoot += File.separator;
		/*
		 * if (solrRoot==null) return false; String fldr = solrRoot + indexName
		 * + File.separator;
		 */
        String fldr = solrRoot + indexPath;
        if (fldr.indexOf("\\") > 0) {
            fldr = fldr.replaceAll("\\\\\\\\", "\\\\"); // first revert any
            // doubles into singles
            // to ensure we get all
            // doubles in the end
            fldr = fldr.replaceAll("\\\\", "\\\\\\\\"); // make singles doubles
        }
        try {
            File dir = new File(fldr);
            if (!dir.exists())
                dir.mkdirs();
            else {
                dir.delete();
                dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!fldr.endsWith(File.separator))
            fldr += File.separator;

        File fldrf = new File(fldr);
        return expandPlugin(fldrf);
    }

    private static boolean expandPlugin(File fldr) {
        try {

            File dest = new File(fldr, "conf");
            dest.mkdir();
            log.debug(" Making the core configuration directory : "
                    + dest.getAbsolutePath() + " ");
            // File configDir = new File(solrRoot, Configuration.GBPLUGIN);
            // FileUtils.copyDirectory(configDir, dest);
            printSupportingFiles(dest);
            return true;
        } catch (Exception _e) {
            _e.printStackTrace();
            System.err
                    .println(" GB solr Plugin is not installed on this server.  Please run gb install solrplugin as admin.");
            return false;
        }

    }

    /***
     * createSchemalXML is designed to create the 2 XML files needed to add a
     * new index to a solr repository
     *
     * @param inputParms         A hashmap of parameters. The key is the field name and then
     *                           that field name has a hashmap of parameters to define how to
     *                           treat it.
     * @param defaultOperatorAND Whether or not the default is to AND or OR. A true value
     *                           defaults to AND. False defaults to OR
     * @return Name of the index created
     */
    public static String createSchemaXMLFromSolrRoot(String indexPath,
                                                     Map<String, Map<String, String>> inputParms,
                                                     Boolean defaultOperatorAND) {
        String solrRoot = System.getProperty("solr.solr.home");
        log.debug("create anew file form xml solr schema --> solrRoot: " + solrRoot);
        if (solrRoot == null) {
            solrRoot = ABProperties.get("solrRoot");
            log.debug("solrRoot: (contingency)" + solrRoot);
        }
        System.out.println ( " system property for helm : " + solrRoot );
        if (solrRoot == null)
            solrRoot = "./";
//        log.config(solrSite);
//        if (solrSite == null || solrSite.trim().length() <= 0) {
//            solrSite = System.getProperty("solr_config_host");
//        }
//        if ( solrSite == null )
//            log.debug ( " Solr site is not defined " + solrSite );





//        if (!solrSite.endsWith("/"))
//            solrSite += "/";
        int l = indexPath.lastIndexOf("/");
        if (l == -1)
            l = indexPath.lastIndexOf("\\");
        String indexName = indexPath.substring(l + 1);
        if (!createConfFolder(indexPath)) {
            return "Conf folder creation problem. Aborting";
        }
        log.config("Creating the schema : :" + indexPath);

        final String facetTamplet = "<field name=\"`NAME`__900807\" type=\"string\" indexed=\"true\" required=\"false\" multiValued=\"false\" stored=\"false\" />";
        final String facetCopyTamplet = "<copyField source=\"`NAME`\" dest=\"`NAME`__900807\"/>";
        final String fieldTemplate = SolrConfigTemplate.fieldTemplate;
        final String copyTemplate = SolrConfigTemplate.copyTemplate;
        StringBuffer fileData2 = new StringBuffer(10000);
        BufferedReader reader = null;
        FileReader fr = null;
        try {


            File solrrootdirectory =new File ( solrRoot );
            System.out.println ( " solr root directory : "+ solrrootdirectory.getAbsolutePath());
            try {
                log.debug("Loading the solr config template : " + solrRoot
                        + "solrconfig_template.xml");
                File solrconfig_file = new File ( solrrootdirectory, "solrconfig_template.xml");
                System.out.println ( " solr config file : "+ solrconfig_file.getAbsolutePath());

                fr = new FileReader(solrconfig_file);
            } catch (FileNotFoundException eee) {
                fr = new FileReader("solrconfig_template.xml");
            }
            reader = new BufferedReader(fr);
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                fileData2.append(buf, 0, numRead);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close ();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String sConfigTemplate = fileData2.toString();
        //System.out.println ( " solrconfig template " + sConfigTemplate );



        if (indexPath.indexOf("\\") > 0) {
            indexPath = indexPath.replaceAll("\\\\\\\\", "\\\\"); // first
            // revert
            // any
            // doubles
            // into
            // singles
            // to ensure
            // we get
            // all
            // doubles
            // in the
            // end
            indexPath = indexPath.replaceAll("\\\\", "\\\\\\\\"); // make
            // singles
            // doubles
        }

        sConfigTemplate = sConfigTemplate.replaceAll("`INDEX_NAME`", indexPath);

        StringBuffer fileData = new StringBuffer(10000);
        try {
            try {
                fr = new FileReader(solrRoot + "/solr_template.xml");
            } catch (FileNotFoundException fne) {
                fne.printStackTrace();
                fr = new FileReader("solr_template.xml");
            }
            reader = new BufferedReader(fr);
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String sTemplate = fileData.toString();
        sTemplate = sTemplate.replaceAll("`INDEX_NAME`", indexName);
        sTemplate = sTemplate
                .replaceAll("`DEFAULT_OPERATOR_DISABLE_BEGIN`", "");
        sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR_DISABLE_END`", "");
        if (defaultOperatorAND) {
            sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR`", "AND");
        } else {
            sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR`", "OR");
        }
		/*
		 *
		 * $INDEX_NAME$
		 *
		 * $DEFAULT_OPERATOR_DISABLE_BEGIN$ $DEFAULT_OPERATOR$
		 * $DEFAULT_OPERATOR_DISABLE_END$
		 *
		 * $DEFAULT_SEARCH_FIELD_DISABLE_BEGIN$ $DEFAULT_SEARCH_FIELD$
		 * $DEFAULT_SEARCH_FIELD_DISABLE_END$
		 *
		 * $FIELDS$
		 *
		 * // caseControl (false) // dataType (string) // sortable (true) //
		 * indexed (true) // stored (true) // multiValued (false) //
		 * defaultSearchField (false) // requiredField (false)
		 */
        String allTerms = "";
        Iterator<String> ii = inputParms.keySet().iterator();
        while (ii.hasNext()) {

            String fieldLine = fieldTemplate;
            String copyLine = copyTemplate;

            Boolean caseSensitive = false;
            Boolean sortable = true;
            Boolean indexed = true;
            Boolean multi = false;
            Boolean requiredField = false;
            Boolean stored = true;

            String field = ii.next();
            Map<String, String> kvp = inputParms.get(field);
            String temp = "";

            // // {{ TAKE CARE OF UBER SOLR STUFF }}
            // String dictionary = kvp.get("dictionary");
            // if (dictionary != null) {
            // buildDictionaryConfiguration(field, indexPath, dictionary);
            // }

            temp = kvp.get("caseControl");
            if (temp != null && temp.equalsIgnoreCase("true"))
                caseSensitive = true;
            temp = kvp.get("sortable");
            if (temp != null && temp.equalsIgnoreCase("false"))
                sortable = false;
            temp = kvp.get("indexed");
            if (temp != null && temp.equalsIgnoreCase("false"))
                indexed = false;
            if (temp == null)
                indexed = true;

            temp = kvp.get("stored");
            if (temp != null && temp.equalsIgnoreCase("false"))
                stored = false;
            temp = kvp.get("multiValued");
            if (temp != null && temp.equalsIgnoreCase("true"))
                multi = true;
            temp = kvp.get("defaultSearchField");
            if (temp != null && temp.length() > 0) {
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_BEGIN`", "");
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_END`", "");
                sTemplate = sTemplate.replaceAll("`DEFAULT_SEARCH_FIELD`",
                        field);
                // sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                // "<uniqueKey required=\"false\"></uniqueKey>");
                sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                        "<uniqueKey>TMID</uniqueKey>"); // not
                // gonna
                // use
                // it
                // for
                // now,
                // as
                // copyFields
                // dont
                // work
                // as
                // unique
                // ids
            } else {
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_BEGIN`", "");
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_END`", "");
                sTemplate = sTemplate.replaceAll("`DEFAULT_SEARCH_FIELD`",
                        "allterms");
                // sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                // "<uniqueKey>allterms</uniqueKey>");
                sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                        "<uniqueKey>TMID</uniqueKey>"); // not
                // gonna
                // use
                // it
                // for
                // now,
                // as
                // copyFields
                // dont
                // work
                // as
                // unique
                // ids
            }

            temp = kvp.get("requiredField");
            if (temp != null && temp.equalsIgnoreCase("true"))
                requiredField = true;

            temp = kvp.get("dataType");
            if (temp != null) {
                if (temp.equalsIgnoreCase("string")) {
                    if (caseSensitive)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "string");
                } else if (temp.equalsIgnoreCase("integer")) {
                    if (sortable)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
                } else if (temp.equalsIgnoreCase("float")) {
                    if (sortable)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "float");
                } else if (temp.equalsIgnoreCase("sint")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                } else if (temp.equalsIgnoreCase("sdouble")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sdouble");
                } else if (temp.equalsIgnoreCase("sfloat")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
                } else if (temp.equalsIgnoreCase("slong")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "slong");
                } else if (temp.equalsIgnoreCase("text_ws")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text_ws");
                } else if (temp.equalsIgnoreCase("text")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                } else if (temp.equalsIgnoreCase("textTight")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "textTight");
                } else if (temp.equalsIgnoreCase("textSpell")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "textSpell");
                } else if (temp.equalsIgnoreCase("int")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
                } else if (temp.equalsIgnoreCase("long")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "long");
                } else if (temp.equalsIgnoreCase("double")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "double");
                } else if (temp.equalsIgnoreCase("float")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "float");
                } else if (temp.equalsIgnoreCase("boolean")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "boolean");
                } else if (temp.equalsIgnoreCase("date")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "date");
                } else if (temp.equalsIgnoreCase("varchar")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string_ci");
                } else if (temp.equalsIgnoreCase("varchar2")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string_ci");
                } else if (temp.equalsIgnoreCase("char")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string_ci");
                } else if (temp.equalsIgnoreCase("nchar")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string_ci");
                } else if (temp.toLowerCase().startsWith("varchar")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string_ci");
                } else if (temp.toLowerCase().startsWith("nvarchar")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string_ci");
                } else if (temp.toLowerCase().startsWith("longblob")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                } else if (temp.toLowerCase().startsWith("blob")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                } else if (temp.toLowerCase().startsWith("int")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                } else if (temp.toLowerCase().startsWith("longblob")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                } else if (temp.toLowerCase().startsWith("blob")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                } else if (temp.toLowerCase().startsWith("int")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                } else if (temp.toLowerCase().startsWith("bigint")) {
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                } else
                    fieldLine = fieldLine.replaceAll("`TYPE`", temp); // default
            } else
                fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
            // if
            // they
            // pass
            // in no
            // data
            // type

            fieldLine = fieldLine.replaceAll("`NAME`", field);
            copyLine = copyLine.replace("`NAME`", field);

            // {{ HERE IS WHERE WE ADD THE FACET FIELDS }}
            if (temp != null
                    && (temp.equalsIgnoreCase("text") || temp
                    .equalsIgnoreCase("string"))) {
                String facet_field = facetTamplet.replaceAll("`NAME`", field);
                String facet_copy_field = facetCopyTamplet.replace("`NAME`",
                        field);
                allTerms += facet_field + "\n" + facet_copy_field;
            }

            allTerms += "\t" + copyLine + "\n";
            fieldLine = fieldLine.replaceAll("`INDEXED`", (indexed ? "true"
                    : "false"));
            fieldLine = fieldLine.replaceAll("`STORED`", (stored ? "true"
                    : "false"));
            fieldLine = fieldLine.replaceAll("`REQUIRED`",
                    (requiredField ? "true" : "false"));
            fieldLine = fieldLine.replaceAll("`MULTI`", (multi ? "true"
                    : "false"));
            sTemplate = sTemplate.replaceAll("`FIELDS`", fieldLine
                    + "\n\t`FIELDS`");
        }
        sTemplate = sTemplate
                .replaceAll(
                        "`FIELDS`",
                        "<field name=\"allterms\" type=\"text\" indexed=\"true\" stored=\"false\" required=\"false\" multiValued=\"true\" />\n"
                                + allTerms

                );
        // String fldr = solrRoot + indexName + File.separator + "conf" +
        // File.separator;
        // {{ BUILD THE DICTIONARY STRING }}
        ii = inputParms.keySet().iterator();
        String dictionaries = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dictionaries>\n\n";
        while (ii.hasNext()) {
            String field = ii.next();
            Map<String, String> kvp = inputParms.get(field);
            String temp = "";
            String dict = kvp.get("dictionary");
            if (dict != null)
                dictionaries += getDictionaryFieldLine(dict, field);
        }
        dictionaries += "</dictionaries>";

        String fldr = solrRoot + "/" + indexPath;
        if (!indexPath.endsWith(File.separator))
            fldr += File.separator;

        fldr += "conf" + File.separator;

        String fn = fldr + "schema.xml";
        String fn2 = fldr + "solrconfig.xml";
        String fn3 = fldr + "dictionary.xml";
        // this is a new addition required as of solr5x.
        // no longer need to edit the solr.xml file.


        String core_main_directory = solrRoot + "/" + indexPath;
        if ( !core_main_directory.endsWith("/"))
            core_main_directory=core_main_directory+"/";

        String core_properties_file = core_main_directory + "core.properties";
        System.out.println ( " adding the core.properties file" + core_properties_file);
        File core_prop_f = new File(core_properties_file);
        try {
            FileWriter cfw = new FileWriter(core_prop_f);
            cfw.write("\n");
            cfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String synonyms = fldr + "synonyms.txt";
        File synonyms_f = new File(synonyms);
        try {
            FileWriter cfw = new FileWriter(synonyms_f);
            cfw.write("\n");
            cfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // is we do not have the directories we need to create them:
        File dir = new File(fldr);
        log.debug("CREATE: " + dir.getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();

        log.info("Writting: " + fn);
        log.info("Writting: " + fn2);
        log.info("Writting: " + fn3);
        FileWriter fw = null;
        try {
            fw = new FileWriter(fn);
            fw.write(sTemplate);
            fw.close();

            fw = new FileWriter(fn2);
            fw.write(sConfigTemplate);
            fw.close();

            fw = new FileWriter(fn3);
            fw.write(dictionaries);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        indexPath = indexPath.replaceAll("\\\\\\\\", "\\\\"); // first revert
        // any doubles
        // into singles
        // to ensure we
        // get all
        // doubles in
        // the end
        return indexName;
    }

    // __________________________________________________________________________________-

    private static String getDictionaryFieldLine(String dict, String field) {
        String sd = "<dictionaryField name=\"" + field + "\" uri=\"" + dict
                + "\"> </dictionaryField>\n";
        return sd;
    }

    private static void buildDictionaryConfiguration(String _dictionary,
                                                     String _indexPath, String _field) {

        String solrRoot = ABProperties.get("solrRoot");
        // {{ }}
        String field = _field.replace(' ', '_');
        String fldr = solrRoot + "/" + _indexPath + "/conf/schema.xml";
        try {
            File file = new File(fldr);
            ABFileUtils.appendLineToFile(file, "<dictionaryField field=\""
                    + field + "\" uri=\"" + _dictionary
                    + "\"></dictionaryField>");
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }

    public static String createFieldLine(HashMap<String, String> _params) {
        String allTerms = "";
        String fieldLine = SolrConfigTemplate.fieldTemplate;
        String copyLine = SolrConfigTemplate.copyTemplate;

        Boolean caseSensitive = false;
        Boolean sortable = true;
        Boolean indexed = true;
        Boolean multi = false;
        Boolean requiredField = false;
        Boolean stored = true;
        HashMap<String, String> kvp = _params;
        String temp = "";

        String field = _params.get("fieldName");

        temp = kvp.get("caseControl");
        if (temp != null && temp.equalsIgnoreCase("true"))
            caseSensitive = true;
        temp = kvp.get("sortable");
        if (temp != null && temp.equalsIgnoreCase("false"))
            sortable = false;
        temp = kvp.get("indexed");
        if (temp != null && temp.equalsIgnoreCase("false"))
            indexed = false;
        if (temp == null)
            indexed = true;

        temp = kvp.get("stored");
        if (temp != null && temp.equalsIgnoreCase("false"))
            stored = false;
        temp = kvp.get("multiValued");
        if (temp != null && temp.equalsIgnoreCase("true"))
            multi = true;
        temp = kvp.get("requiredField");
        if (temp != null && temp.equalsIgnoreCase("true"))
            requiredField = true;

        temp = kvp.get("dataType");
        if (temp != null) {
            if (temp.equalsIgnoreCase("string")) {
                if (caseSensitive)
                    fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                else
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string");
            } else if (temp.equalsIgnoreCase("integer")) {
                if (sortable)
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
                else
                    fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
            } else if (temp.equalsIgnoreCase("float")) {
                if (sortable)
                    fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
                else
                    fieldLine = fieldLine.replaceAll("`TYPE`", "float");
            } else
                fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
        } else
            fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
        // if
        // they
        // pass
        // in no
        // data
        // type

        fieldLine = fieldLine.replaceAll("`NAME`", field);
        copyLine = copyLine.replace("`NAME`", field);
        allTerms += "\t" + copyLine + "\n";
        fieldLine = fieldLine.replaceAll("`INDEXED`", (indexed ? "true"
                : "false"));
        fieldLine = fieldLine.replaceAll("`STORED`",
                (stored ? "true" : "false"));
        fieldLine = fieldLine.replaceAll("`REQUIRED`", (requiredField ? "true"
                : "false"));
        fieldLine = fieldLine.replaceAll("`MULTI`", (multi ? "true" : "false"));

        return fieldLine;
    }

    public static void main(String[] args) {
        HashMap<String, Map<String, String>> foo = new HashMap<String, Map<String, String>>();
        HashMap<String, String> tt1 = new HashMap<String, String>();
        tt1.put("caseControl", "true");
        tt1.put("dataType", "string");

        HashMap<String, String> tt2 = new HashMap<String, String>();
        tt2.put("sortable", "false");
        tt2.put("dataType", "integer");
        tt2.put("requiredField", "true");
        tt2.put("multiValued", "true");
        // tt2.put("defaultSearchField", "true");

        HashMap<String, String> tt3 = new HashMap<String, String>();
        tt3.put("sortable", "true");
        tt3.put("dataType", "integer");

        HashMap<String, String> tt4 = new HashMap<String, String>();
        tt4.put("sortable", "false");
        tt4.put("dataType", "float");

        HashMap<String, String> tt5 = new HashMap<String, String>();
        tt5.put("sortable", "true");
        tt5.put("dataType", "float");

        HashMap<String, String> tt6 = new HashMap<String, String>();
        tt6.put("caseControl", "false");
        tt6.put("dataType", "string");

        foo.put("strCaseSens", tt1);
        foo.put("intNotSort", tt2);
        foo.put("intSort", tt3);
        foo.put("floatNotSort", tt4);
        foo.put("floatSort", tt5);
        foo.put("strNotCaseSense", tt6);
        //
        // System.err.println(createSchemaXML("/Volumes/Bay4/test/test2", foo,
        // true));

    }

    public static String createSchemaXML(String indexPath,
                                         Map<String, Map<String, String>> inputParms,
                                         Boolean defaultOperatorAND, String solrSite, String solrRoot) {
        log.info("solrRoot: " + solrRoot);
        if (solrRoot == null) {
            return "ERROR: solrRoot not defined";
        }
        if (solrSite == null) {
            return "ERROR: solrSite not defined";
        }

        if (!solrSite.endsWith("/"))
            solrSite += "/";

        int l = indexPath.lastIndexOf("/");
        if (l == -1)
            l = indexPath.lastIndexOf("\\");

        String indexName = indexPath.substring(l + 1);

        if (!createConfFolder(indexPath, solrRoot)) {
            return "Conf folder creation problem. Aborting";
        }

        final String facetTamplet = "<field name=\"`NAME`__900807\" type=\"string\" indexed=\"true\" required=\"false\" multiValued=\"false\" stored=\"false\" />";
        final String facetCopyTamplet = "<copyField source=\"`NAME`\" dest=\"`NAME`__900807\"/>";
        final String fieldTemplate = " <field name=\"`NAME`\" type=\"`TYPE`\" indexed=\"`INDEXED`\" stored=\"`STORED`\" required=\"`REQUIRED`\" multiValued=\"`MULTI`\" /> ";
        final String copyTemplate = "<copyField source=\"`NAME`\" dest=\"allterms\" />";

        StringBuffer fileData2 = new StringBuffer(1000);
        try {
            FileReader fr = null;
            try {
                log.info("Loading the solr config template : " + solrRoot
                        + "solrconfig_template.xml");
                fr = new FileReader(solrRoot + "/solrconfig_template.xml");

            } catch (FileNotFoundException eee) {
                fr = new FileReader("solrconfig_template.xml");
            }
            BufferedReader reader = new BufferedReader(fr);
            try {
                char[] buf = new char[1024];
                int numRead = 0;
                while ((numRead = reader.read(buf)) != -1) {
                    fileData2.append(buf, 0, numRead);
                }
            } finally {
                IOUTILs.closeResource(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sConfigTemplate = fileData2.toString();

        if (indexPath.indexOf("\\") > 0) {
            indexPath = indexPath.replaceAll("\\\\\\\\", "\\\\"); // first
            // revert
            // any
            // doubles
            // into
            // singles
            // to ensure
            // we get
            // all
            // doubles
            // in the
            // end
            indexPath = indexPath.replaceAll("\\\\", "\\\\\\\\"); // make
            // singles
            // doubles
        }

        sConfigTemplate = sConfigTemplate.replaceAll("`INDEX_NAME`", indexPath);

        StringBuffer fileData = new StringBuffer(1000);
        try {
            FileReader fr = null;
            try {
                fr = new FileReader(solrRoot + "/solr_template.xml");
            } catch (FileNotFoundException fne) {
                fr = new FileReader("solr_template.xml");
            }
            BufferedReader reader = new BufferedReader(fr);
            try {
                char[] buf = new char[1024];
                int numRead = 0;
                while ((numRead = reader.read(buf)) != -1) {
                    fileData.append(buf, 0, numRead);
                }
            } finally {
                IOUTILs.closeResource(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sTemplate = fileData.toString();
        sTemplate = sTemplate.replaceAll("`INDEX_NAME`", indexName);
        sTemplate = sTemplate
                .replaceAll("`DEFAULT_OPERATOR_DISABLE_BEGIN`", "");
        sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR_DISABLE_END`", "");
        if (defaultOperatorAND) {
            sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR`", "AND");
        } else {
            sTemplate = sTemplate.replaceAll("`DEFAULT_OPERATOR`", "OR");
        }
		/*
		 * 
		 * $INDEX_NAME$
		 * 
		 * $DEFAULT_OPERATOR_DISABLE_BEGIN$ $DEFAULT_OPERATOR$
		 * $DEFAULT_OPERATOR_DISABLE_END$
		 * 
		 * $DEFAULT_SEARCH_FIELD_DISABLE_BEGIN$ $DEFAULT_SEARCH_FIELD$
		 * $DEFAULT_SEARCH_FIELD_DISABLE_END$
		 * 
		 * $FIELDS$
		 * 
		 * // caseControl (false) // dataType (string) // sortable (true) //
		 * indexed (true) // stored (true) // multiValued (false) //
		 * defaultSearchField (false) // requiredField (false)
		 */
        String allTerms = "";
        Iterator<String> ii = inputParms.keySet().iterator();
        while (ii.hasNext()) {

            String fieldLine = fieldTemplate;
            String copyLine = copyTemplate;

            Boolean caseSensitive = false;
            Boolean sortable = true;
            Boolean indexed = true;
            Boolean multi = false;
            Boolean requiredField = false;
            Boolean stored = true;

            String field = ii.next();
            Map<String, String> kvp = inputParms.get(field);
            String temp = "";

            // // {{ TAKE CARE OF UBER SOLR STUFF }}
            // String dictionary = kvp.get("dictionary");
            // if (dictionary != null) {
            // buildDictionaryConfiguration(field, indexPath, dictionary);
            // }

            temp = kvp.get("caseControl");
            if (temp != null && temp.equalsIgnoreCase("true"))
                caseSensitive = true;
            temp = kvp.get("sortable");
            if (temp != null && temp.equalsIgnoreCase("false"))
                sortable = false;
            temp = kvp.get("indexed");
            if (temp != null && temp.equalsIgnoreCase("false"))
                indexed = false;
            temp = kvp.get("stored");
            if (temp != null && temp.equalsIgnoreCase("false"))
                stored = false;
            temp = kvp.get("multiValued");
            if (temp != null && temp.equalsIgnoreCase("true"))
                multi = true;
            temp = kvp.get("defaultSearchField");
            if (temp != null && temp.length() > 0) {
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_BEGIN`", "");
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_END`", "");
                sTemplate = sTemplate.replaceAll("`DEFAULT_SEARCH_FIELD`",
                        field);
                // sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                // "<uniqueKey required=\"false\"></uniqueKey>");
                sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                        "<uniqueKey>TMID</uniqueKey>"); // not
                // gonna
                // use
                // it
                // for
                // now,
                // as
                // copyFields
                // dont
                // work
                // as
                // unique
                // ids
            } else {
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_BEGIN`", "");
                sTemplate = sTemplate.replaceAll(
                        "`DEFAULT_SEARCH_FIELD_DISABLE_END`", "");
                sTemplate = sTemplate.replaceAll("`DEFAULT_SEARCH_FIELD`",
                        "allterms");
                // sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                // "<uniqueKey>allterms</uniqueKey>");
                sTemplate = sTemplate.replaceAll("`UNIQUE_KEY`",
                        "<uniqueKey>TMID</uniqueKey>"); // not
                // gonna
                // use
                // it
                // for
                // now,
                // as
                // copyFields
                // dont
                // work
                // as
                // unique
                // ids
            }

            temp = kvp.get("requiredField");
            if (temp != null && temp.equalsIgnoreCase("true"))
                requiredField = true;

            temp = kvp.get("dataType");
            if (temp != null) {
                if (temp.equalsIgnoreCase("string")) {
                    if (caseSensitive)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "text");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "string");
                } else if (temp.equalsIgnoreCase("integer")) {
                    if (sortable)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
                } else if (temp.equalsIgnoreCase("float")) {
                    if (sortable)
                        fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
                    else
                        fieldLine = fieldLine.replaceAll("`TYPE`", "float");
                } else
                    fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
                // if
                // they
                // pass
                // in
                // some
                // other
                // data
                // type
            } else
                fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
            // if
            // they
            // pass
            // in no
            // data
            // type

            fieldLine = fieldLine.replaceAll("`NAME`", field);
            copyLine = copyLine.replace("`NAME`", field);
            // {{ HERE IS WHERE WE ADD THE FACET FIELDS }}
            if (!field.equalsIgnoreCase("TMID")) {
                if (temp != null
                        && (temp.equalsIgnoreCase("text") || temp
                        .equalsIgnoreCase("string"))) {
                    String facet_field = facetTamplet.replaceAll("`NAME`",
                            field);
                    String facet_copy_field = facetCopyTamplet.replace(
                            "`NAME`", field);
                    allTerms += facet_field + "\n" + facet_copy_field;
                }
            }
            allTerms += "\t" + copyLine + "\n";
            fieldLine = fieldLine.replaceAll("`INDEXED`", (indexed ? "true"
                    : "false"));
            fieldLine = fieldLine.replaceAll("`STORED`", (stored ? "true"
                    : "false"));
            fieldLine = fieldLine.replaceAll("`REQUIRED`",
                    (requiredField ? "true" : "false"));
            fieldLine = fieldLine.replaceAll("`MULTI`", (multi ? "true"
                    : "false"));
            sTemplate = sTemplate.replaceAll("`FIELDS`", fieldLine
                    + "\n\t`FIELDS`");

        }

        sTemplate = sTemplate
                .replaceAll(
                        "`FIELDS`",
                        "<field name=\"allterms\" type=\"text\" indexed=\"true\" stored=\"false\" required=\"false\" multiValued=\"true\" />\n"
                                + allTerms);

        // String fldr = solrRoot + indexName + File.separator + "conf" +
        // File.separator;

        // {{ BUILD THE DICTIONARY STRING }}
        ii = inputParms.keySet().iterator();
        String dictionaries = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dictionaries>\n\n";
        while (ii.hasNext()) {
            String field = ii.next();
            Map<String, String> kvp = inputParms.get(field);
            String temp = "";
            String dict = kvp.get("dictionary");
            if (dict != null)
                dictionaries += getDictionaryFieldLine(dict, field);
        }
        dictionaries += "</dictionaries>";

        String fldr = solrRoot + "/" + indexPath;
        if (!indexPath.endsWith(File.separator))
            fldr += File.separator;
        fldr += "conf" + File.separator;

        String fn = fldr + "schema.xml";
        String fn2 = fldr + "solrconfig.xml";
        String fn3 = fldr + "dictionary.xml";

        // is we do not have the directories we need to create them:
        File dir = new File(fldr);
        log.info("CREATE: " + dir.getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();

        log.info("Writting: " + fn);
        log.info("Writting: " + fn2);
        log.info("Writting: " + fn3);
        FileWriter fw = null;
        try {
            fw = new FileWriter(fn);
            fw.write(sTemplate);
            fw.close();

            fw = new FileWriter(fn2);
            fw.write(sConfigTemplate);
            fw.close();

            fw = new FileWriter(fn3);
            fw.write(dictionaries);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(fw);
        }

        log.info("Write complete.");

        indexPath = indexPath.replaceAll("\\\\\\\\", "\\\\"); // first revert
        // any doubles
        // into singles
        // to ensure we
        // get all
        // doubles in
        // the end

        log.info("Calling solr with the following solr index path : "
                + indexPath);

        // {{ THIS IS A TEST CASE FOR THE LOGS }}
        // {{ THIS IS A TEST CASE FOR THE LOGS }}
        // {{ THIS IS A TEST CASE FOR THE LOGS }}
        // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
        // the block of code below is to test the http connection from a
        // servlet on resdev. This works consistently so it's now commented out.
        // however I'm going to leave this test here for a few days. ...
        // URL u;
        // try {
        // u = new URL("http://resdev/samplehub_ws/pathlims/getsourcenames");
        // URLConnection uc = u.openConnection();
        // log.info("resdev/samplehub_ws/pathlims/getsourcenames Connection open ");
        // uc.setUseCaches(false);
        // InputStream is = uc.getInputStream();
        // BufferedReader reader = new BufferedReader(
        // new InputStreamReader(is));
        // System.out
        // .println(" we have the inputStream from the url connection ");
        // String line;
        // while ((line = reader.readLine()) != null) {
        // System.out.print("" + line);
        // }
        // reader.close();
        // } catch (MalformedURLException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        // {{ THIS IS A TEST CASE FOR THE LOGS }}
        // {{ THIS IS A TEST CASE FOR THE LOGS }}
        boolean bb = false;
        try {
            bb = TMSolrServer.callSolr(solrSite
                    + "admin/cores?action=CREATE&name=" + indexName
                    + "&instanceDir=" + indexPath);
        } catch (SolrCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // http://localhost:8983/solr/admin/cores?action=create_table_facets&table=milton_Repository_patient_samples
        // if (bb) {
        // bb = callSolr(solrSite
        // + "admin/cores?action=create_table_facets&table="
        // + indexName);
        // }
        if (bb)
            return indexName;

        else
            return null;
    }

    protected static Boolean createConfFolder(String indexPath, String _solrRoot) {
        String solrRoot = _solrRoot;
        // log.info("solrRoot: " + solrRoot);

        if (!solrRoot.endsWith(File.separator))
            solrRoot += File.separator;
		/*
		 * if (solrRoot==null) return false; String fldr = solrRoot + indexName
		 * + File.separator;
		 */
        String fldr = solrRoot + indexPath;
        if (fldr.indexOf("\\") > 0) {
            fldr = fldr.replaceAll("\\\\\\\\", "\\\\"); // first revert any
            // doubles into singles
            // to ensure we get all
            // doubles in the end
            fldr = fldr.replaceAll("\\\\", "\\\\\\\\"); // make singles doubles
        }
        try {
            File dir = new File(fldr);
            if (!dir.exists())
                dir.mkdirs();
            else {
                dir.delete();
                dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!fldr.endsWith(File.separator))
            fldr += File.separator;

        // File fff = new File(solrRoot + "conf.zip");
        // if (!fff.exists()) {
        // FileUnzipper.unzip("conf.zip", fldr);
        // } else
        // FileUnzipper.unzip(solrRoot + "conf.zip", fldr);
        File fldrf = new File(fldr);
        return expandPlugin(fldrf);

    }

    public static String createSchemaXML(String indexName,
                                         Map<String, Map<String, String>> inputParms, String _url,
                                         String _dir) {
        return createSchemaXML(indexName, inputParms, false, _url, _dir); // default
    }

}
