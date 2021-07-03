package com.arraybase.solr.plugin;

import com.arraybase.GB;
import com.arraybase.GBUtil;
import com.arraybase.tm.TableManager;
import com.arraybase.util.ABFileUtils;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.*;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerUtils;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.servlet.SolrRequestParsers;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorChain;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 *  @Deprecated
 *   This class is deprecated since we are now using a separate library for the solr plugin features.
 *
 */
@Deprecated
public class GAdmin extends org.apache.solr.handler.admin.CoreAdminHandler {
//    private static GBLogger lg = GBLogger.getLogger(GAdmin.class);
//    private SolrQueryResponse rsp = null;

    private SolrCoreCreatorService solrCoreCreatorService = new SolrCoreCreatorService(
            getCoreContainer().getSolrHome());

    public GAdmin(CoreContainer coreContainer) {
        super(coreContainer);
        //ABProperties.setSolrSite(System.getenv("solr_config_host"));
    }

    public GAdmin() {
        super();
        //ABProperties.setSolrSite(System.getenv("solr_config_host"));   --I can't remember why I did this.,but I don't like it.
    }

    public static HttpSolrClient getSolrServer(String _schema, String _solr_url)
            throws MalformedURLException {
        String solr_url = _solr_url;

        if (!solr_url.endsWith("/")) {
            solr_url += "/";
        }
        HttpSolrClient solr = new HttpSolrClient.Builder(solr_url + _schema).build();
        return solr;
    }

    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp)
            throws Exception {
        Iterator<String> it = req.getParams().getParameterNamesIterator();
        String action = req.getParams().get("action");
        System.out.println(" action " + action);

        if (action != null) {
            if (action.equalsIgnoreCase("create_table")) {
                handleCustomAction(req, rsp);
            } else if (action.equalsIgnoreCase("set_field_type")) {
                handleCustomAction(req, rsp);
            } else
                super.handleRequestBody(req, rsp);
        } else
            super.handleRequestBody(req, rsp);
    }

//    public SolrQueryResponse getSolrQueryResponse() {
//        return rsp;
//    }

    // http://localhost:8983/solr/admin/cores?action=test
    // this will allow us to do an action
    protected void handleCustomAction(SolrQueryRequest req,
                                      SolrQueryResponse rsp) {
        PluginProperties.setAdmin(this);
        PluginProperties.setSolrHome(super.getCoreContainer().getSolrHome());
        SolrParams _params = req.getParams();
        String action = _params.get(CoreAdminParams.ACTION);
        System.out.println(" action  " + action);
        if (action.equals("test"))
            return;
        // what is the action?

        if (action.equalsIgnoreCase("create_table")) {
            String userName = _params.get("user");
            String table_name = _params.get("table_name");
            String description = _params.get("description");
            String t_value = _params.get("table");
            HashMap<String, Map<String, String>> _params1 = createTable(t_value);
            SolrCore c = getCoreContainer().getCore(table_name);
            if (c != null) {
//                System.out.println(" unloading " + t_value);
//                getCoreContainer().unload(t_value, true, true, true);
            }
            String corename = TableManagerPlugin.build(userName, TableManager.TMSOLR, table_name, description,
                    "1", _params1, null);
            super.getCoreContainer().load();
            return;

        } else if (action.equalsIgnoreCase("refresh")) {


            System.out.println (" refresh : " );
            Collection<SolrCore> cores = getCoreContainer().getCores();
            for ( SolrCore sc : cores )
            {
                System.out.println ( " close the searcher : " + sc.getName() );
                sc.close();
                sc.closeSearcher();
            }

        } else if (action.equalsIgnoreCase("reset_table")) {
            String userName = _params.get("user");
            String table_name = _params.get("table_name");
            // we need to blow away the data directory
            CoreContainer container = super.getCoreContainer();
            SolrCore source_core = container.getCore(table_name);
            if (source_core != null) {

                String dds = source_core.getDataDir();
                File data_directory = new File(dds);
                if (data_directory.exists()) {
                    System.out.println(" we found the data directory : " + data_directory.getAbsolutePath());
                    data_directory.delete();
                    // solr will replace this once data starts coming in again.
                }
            }


            return;

        } else if (action.equalsIgnoreCase("cp_core")) {
            String from = _params.get("from");
            String to = _params.get("to");
            String temp_core = to;
            CoreContainer container = super.getCoreContainer();
            SolrCore source_core = container.getCore(from);
            try {


                String original_instance_directory = IOUTILs
                        .getDirectory(source_core.getResourceLoader()
                                .getInstancePath());
                String solr_home = super.getCoreContainer().getSolrHome();
                String create_instance_dir = trimLead(trimLead(
                        original_instance_directory, solr_home));
                String source_instance_directory = source_core
                        .getResourceLoader().getInstancePath().toString();
                solr_home = trimLead(solr_home);
                File source_directory = new File(source_instance_directory);
                File parent = source_directory.getParentFile();
                File new_directory = new File(parent, temp_core);

                // {{ COPY THE CONFIGURATION FROM THE SOURCE CORE TO THIS CORE
                // }}
                File source_dir = new File(source_instance_directory);


                //System.out.println(" copy configuration directory to new location : " + source_dir + " --> " + new_directory);
                IOUTILs.copyFolder(source_dir, new_directory);

                GB.print("\n\n\t\t New core directory: " + temp_core);
                GB.print("\t\t New core instance directory : "
                        + create_instance_dir);


                File new_conf = new File(new_directory, "conf");

                System.out.println(" new config : " + new_conf.getAbsolutePath());
                // now we need to edit the
                updateSchemaFile(new_conf, from, to);
                updateCorePropsFile(new_directory, from, to);


                File solr_config_file_for_new_core = new File(new_conf, "solrconfig.xml");
                String current_data_director = getDataDirectory(solr_config_file_for_new_core);


                String data_directory = source_core.getDataDir();
                File current_data_dir = new File(data_directory);


                updateData(current_data_dir, to, solr_config_file_for_new_core);
                File new_data_directory = new File(current_data_dir.getParent(), to);

                updateDataConfig(solr_config_file_for_new_core, current_data_director, new_data_directory);

                super.getCoreContainer().load();


//				SolrParams params = SolrRequestParsers
//						.parseQueryString("action=CREATE&name=" + temp_core
//								+ "&instanceDir=" + temp_core);
//				CoreDescriptor dcore = buildCoreDescriptor(params, container);
//				// TODO this should be moved into CoreContainer, really...
//				SolrCore newCore = null;
//				if (container.getZkController() != null) {
//					container.getZkController().preRegister(dcore);
//				}
//				container.getCoresLocator().create(container, dcore);
//				SolrCore core = container.create(dcore);
//				container.reload(dcore.getName());//, core, false);
//
//				rsp.add("core", core.getName());
//				container.getCoresLocator().persist(container, dcore);
//				newCore = core;
//
//				GB.print("Data Directory : " + newCore.getDataDir());
//

            } catch (Exception _e) {
                _e.printStackTrace();
            }
            return;

        } else if (action.equalsIgnoreCase("archive")) {
            String from = _params.get("core");

            if (from == null) {
                GB.print("Core not specified in archive run.");
            }
            GB.print("Archive the core : " + from);

            CoreContainer container = super.getCoreContainer();
            SolrCore source_core = container.getCore(from);
            try {
                String solr_home = super.getCoreContainer().getSolrHome();
                String source_instance_directory = IOUTILs.getDirectory(source_core
                        .getResourceLoader().getInstancePath());

                solr_home = trimLead(solr_home);
                File data_dir = new File(source_core.getDataDir());
                File archive_directory = new File("archive");
                if (!archive_directory.exists()) {
                    archive_directory.mkdir();
                }

                source_instance_directory = source_instance_directory.trim();

                if (source_instance_directory.endsWith("/"))
                    source_instance_directory.substring(0,
                            source_instance_directory.length() - 1);
                File source_conf = new File(source_instance_directory);
                if (!source_conf.exists()) {
                    GB.print("Failed to find the source file for archiving: "
                            + source_conf.getAbsolutePath());

                }

                String archive_conf = source_instance_directory.replace('/',
                        '_');
                archive_conf = archive_conf.replace('\\', '_');
                archive_conf = archive_conf.replace('.', '_');
                archive_conf = archive_conf.replace('-', '_');
                archive_conf = archive_conf.replace(' ', '_');
                SimpleDateFormat fro = new SimpleDateFormat("-yyMMdd-HHmmss");
                archive_conf += fro.format(new Date());

                ABFileUtils.tar(source_conf, "archive/" + archive_conf
                        + "_conf");
                ABFileUtils.tar(data_dir, "archive/" + archive_conf + "_data");

            } catch (Exception _e) {
                _e.printStackTrace();
            }
            return;

        } else if (action.equalsIgnoreCase("set_field_type")) {// set the type

            String table = _params.get("table");
            String field_name = _params.get("field_name");
            String to_type = _params.get("to_type");
            System.out
                    .println("-----------  " + table + " -------------------");
            setFieldType(table, field_name, to_type, req, rsp);
        } else if (action.equalsIgnoreCase("field_change")) {//
            // http://localhost:8983/solr/admin/cores?action=field_change&schema=milton_Repository_HTL&orig=$original_field&dest=$destination_field
            String schema = _params.get("schema");
            String orig = _params.get("orig");
            String dest = _params.get("dest");
            reNameField(schema, orig, dest, req, rsp);
        } else if (action.equalsIgnoreCase("copy")) { // copy a table from orig
            String orig = _params.get("orig");
            String dest = _params.get("dest");

            GB.print(" Copy : " + orig);
            GB.print("  To : " + dest);


            copySchema(orig, dest, req, rsp);


        } else if (action.equalsIgnoreCase("add_field")) { // add a field
            String table = _params.get("table");
            String new_field_name = _params.get("field_name");
            String new_field_type = _params.get("field_type");
            createNewField(table, new_field_name, new_field_type, req, rsp);
        } else if (action.equalsIgnoreCase("remove_field")) {// remove the field
            String table = _params.get("table");
            String new_field_name = _params.get("field_name");
            removeField(table, new_field_name, req, rsp);
        } else if (action.equalsIgnoreCase("create_table_facets")) {
            // http://localhost:8983/solr/admin/cores?action=create_field_facet&schema=milton_Repository_HTL&table=$original_fiel
            String table = _params.get("table");
            createFacetIndex(table, req, rsp);
        } else if (action.equalsIgnoreCase("create_field_facet")) {
            // http://localhost:8983/solr/admin/cores?action=create_field_facet&schema=milton_Repository_HTL&table=$original_field&field_name=$destination_field
            String table = _params.get("schema");
            String field_name = _params.get("field_name");
            createCopyIndex(table, field_name, "string", rsp);
        } else if (action.equalsIgnoreCase("join")) {
            // SUBJECT_ID
            // table_l=milton_Repository_view_htb_sample&field_l=PATIENT_ID&table_r=milton_Repository_watson&field_r=SUBJECT_ID


            // {{ THE JOIN OPERATION WAS REMOVED ON 05.19.2015 IN ORDER TO CLEAN UP AND REFACTOR ARRAYBASE. }}


//			String table_l = _params.get("table_l");
//			String field_l = _params.get("field_l");
//			String table_r = _params.get("table_r");
//			String field_r = _params.get("field_r");
//			String new_table_name = _params.get("join_table_name");
//			String select_cols = _params.get("select_cols");
//			HashMap<String, String> alias = new Gson().fromJson(select_cols,
//					HashMap.class);
//			// System.out.println("A list of the selected columns:");
//			CoreContainer container = super.getCoreContainer();
//			SolrCore left_core = container.getCore(table_l);
//			SolrCore right_core = container.getCore(table_r);
//			JoinManager jm = new JoinManager(this, alias, left_core, field_l,
//					right_core, field_r, new_table_name);
//			jm.performOperation(null);
//			lg.info("operation is complete");


        } else if (action.equalsIgnoreCase("published")) {
//            String publish_table_name = _params.get("publish_table_name");
//            getPublished(publish_table_name);
        } else if (action.equalsIgnoreCase("publish")) {
            String table_name = _params.get("table_name");
            String table_state = _params.get("table_state");
            String path = _params.get("path");
            String description = _params.get("description");
            String user = _params.get("publisher");
            String publish_table_name = _params.get("publish_table_name");
            PublishTable pb = getPublishTable(this, publish_table_name);
            pb.publish(table_name, table_state, path, user, description,
                    publish_table_name);
            return;
        } else if (action.equalsIgnoreCase("create_publish_table")) {
            String table_name = _params.get("table_name");

            if (table_name == null)
                table_name = ABProperties.PUBLISHED_CORE;

            PublishTable.createPublishTable(this, table_name);
        } else
            super.handleCustomAction(req, rsp);
    }

    private void updateData(File current_data_dir, String to, File solr_config_file_for_new_core) {


        File new_data_directory = new File(current_data_dir.getParent(), to);


        try {
            IOUTILs.copyFolder(current_data_dir, new_data_directory);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private String getDataDirectory(File solr_config_file) throws IOException {

        System.out.println("\n\n\n\n ---------------------------------------------------------------------------------------------------------\n\n\n");
        System.out.println(" ---------------------------------------------------------------------------------------------------------\n\n\n");
        BufferedReader reader = new BufferedReader(new FileReader(solr_config_file));
        try {
            String line = reader.readLine();
            while (line != null) {
                if (line.indexOf("<dataDir>") >= 0) {

                    int index = line.indexOf("Dir>") + 4;
                    int index2 = line.indexOf("<", index);
                    String path = line.substring(index, index2);
                    System.out.println("\t\t data path : " + path + "\n\n");
                    return path.trim();
                }
                line = reader.readLine();
            }
        } finally {
            System.out.println(" closing the reader > ");
            IOUTILs.closeResource(reader);
        }


        return null;

    }

    private void updateCorePropsFile(File core_directory, String from, String to) throws IOException {
        File props = new File(core_directory, "core.properties");
        System.out.println(" props : " + props.getAbsolutePath());
        FileReader file_reader = new FileReader(props);
        Properties pr = new Properties();
        pr.load(file_reader);
        pr.setProperty("name", to);
        FileWriter writer = new FileWriter(props);
        pr.store(writer, "" + new Date().toString());
    }

    private void updateSchemaFile(File conf, String previous_name, String new_name) throws IOException {
//        File schema = new File(conf, "schema.xml");
        File schema = new File(conf, "managed-schema.xml");
        System.out.println(" previous name = " + schema.getAbsolutePath());
        FileReader fl = new FileReader(schema);
//		<schema name="_ionis_ct"
        String mark = "<schema name=\"" + previous_name + "\"";
        BufferedReader br = new BufferedReader(fl);
        StringBuffer str = new StringBuffer();
        String line = br.readLine();
        while (line != null) {
            int index = line.indexOf(mark);
            if (index >= 0) {
                int in1 = line.indexOf("\"");
                int in2 = line.indexOf("\"", in1 + 1);
                String sub = line.substring(in1 + 1, in2);
                line = line.replace(sub, new_name);
                System.out.println(" Line replaced " + line);
            }
            str.append(line + "\n");
            line = br.readLine();
        }
        br.close();

        // now we have to write the file out
        File schema_out = new File(conf, "managed-schema.xml");
        FileWriter writer = new FileWriter(schema_out);
        writer.write(str.toString());
        writer.close();

    }

    private void updateDataConfig(File solr_config_file_for_new_core, String original_data_location,
                                  File new_data_location) {


        String npath = new_data_location.getPath();
        int index = npath.indexOf("..");
        if (index >= 0) {
            npath = npath.substring(index).trim();
        }

        File temp_out = new File(solr_config_file_for_new_core.getParent(), "temp.config.xml");
        BufferedReader reader = null;
        PrintStream pr = null;
        try {
            reader = new BufferedReader(new FileReader(solr_config_file_for_new_core));
            pr = new PrintStream(temp_out);
            String line = reader.readLine();
            while (line != null) {

                int dataDir = line.indexOf("dataDir");
                if (dataDir >= 0) {


                    int ind1 = line.indexOf('>');
                    int ind2 = line.indexOf('<', ind1 + 1);


                    String beg = line.substring(0, ind1 + 1);
                    String end = line.substring(ind2);
                    line = beg + npath + end;
                    System.out.println(" \n\n\n\nnew data diretory : " + line);
                }

                pr.println(line);
                line = reader.readLine();
            }
            pr.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            IOUTILs.closeResource(pr);
            IOUTILs.closeResource(reader);
        }

        IOUTILs.closeResource(pr);
        IOUTILs.closeResource(reader);

        IOUTILs.mv(temp_out, solr_config_file_for_new_core);


    }

    private HashMap<String, Map<String, String>> createTable(String t_value) {
        HashMap<String, Map<String, String>> _params1 = new HashMap<String, Map<String, String>>();
        // {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
        HashMap<String, Map<String, String>> value = GBUtil.fromGSON(t_value);
        return value;
    }

    private static PublishTable getPublishTable(GAdmin gadmin,
                                                String publish_table_name) {

        // here is where we need to validate the table is accessible
        CoreContainer container = gadmin.getCoreContainer();
        SolrCore published_core = container.getCore(publish_table_name);
        if (published_core == null) {
            PublishTable.createPublishTable(gadmin, publish_table_name);
        }
        return new PublishTable(gadmin);
    }

    private boolean createFacetIndex(String table, SolrQueryRequest req,
                                     SolrQueryResponse rsp2) {
        CoreContainer container = super.getCoreContainer();
        // System.out.println("\n\n\n we are getting the core for : " + table);
        SolrCore cs = container.getCore(table);
        String solr_home = container.getSolrHome();
        String configDir = solr_home + "/"
                + cs.getCoreDescriptor().getInstanceDir() + "/conf/";
        String _instanceDir = configDir + cs.getSchemaResource();
        // System.out.println(" we have the config resource : " + _instanceDir);
        File f = new File(_instanceDir);
        // System.out.println(" We have the schema : " + f.getAbsolutePath());
        BufferedReader reader = null;
        try {
            File dir = new File(_instanceDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            reader = new BufferedReader(new FileReader(f));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
            Node fields = doc.getElementsByTagName("fields").item(0);
            NodeList nList = doc.getElementsByTagName("field");
            ArrayList<Node> remove_elements = new ArrayList<Node>();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    // System.out.println(" field name : " + name);
                    String type = eElement.getAttribute("type");
                    if (name.endsWith("__900807")) {
                        remove_elements.add(eElement);
                    } else if (type.equalsIgnoreCase("text")) {
                    }
                }
            }

            NodeList list = fields.getChildNodes();
            // System.out.println("list size " + list.getLength());
            // System.out.println("list size " + list.getLength());
            // System.out.println("list size " + list.getLength());
            // remove list.
            ArrayList<Element> copy_elements = new ArrayList<Element>();
            ArrayList<String> has_copy = new ArrayList<String>();
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node nNode = list.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    String node_name = eElement.getNodeName();
                    String text = eElement.getElementsByTagName("name").item(0)
                            .getFirstChild().getNodeValue();
                    String node_value = eElement.getTagName();
                    String type = eElement.getAttribute("type");
                    if (node_value.equalsIgnoreCase("copyField")) {
                        if (eElement.getAttribute("dest").endsWith("__900807")) {
                            System.out.println("\n\n\n\t fields : name : "
                                    + name + " node_name : " + node_name
                                    + " tag_name : " + node_value + "type "
                                    + type);
                            has_copy.add(eElement.getAttribute("source"));
                        }
                    } else if (node_name.equalsIgnoreCase("field")
                            && type.equalsIgnoreCase("text")) {
                        copy_elements.add(eElement);
                    } else if (node_name.equalsIgnoreCase("field")
                            && type.equalsIgnoreCase("string")) {
                        copy_elements.add(eElement);
                    }
                }
            }
            ArrayList<Element> removeIndex = new ArrayList<Element>();
            for (Element field_e : copy_elements) {
                for (String v : has_copy) {
                    System.out.println(" has copy :  " + v);
                    if (field_e.getAttribute("name").equalsIgnoreCase(v))
                        removeIndex.add(field_e);
                }
            }
            // prune the elements.
            for (Element i : removeIndex) {
                copy_elements.remove(i);
            }

            for (Element field_e : copy_elements) {
                String f_name = field_e.getAttribute("name");
                if (!f_name.endsWith("__900807")) {
                    String lc_field_name = f_name + "__900807";
                    System.out.println("Appending the copy element : " + f_name
                            + "__900807");
                    Element facet_field = doc.createElement("field");
                    facet_field.setAttribute("name", lc_field_name);
                    facet_field.setAttribute("type", "string");
                    facet_field.setAttribute("indexed", "true");
                    facet_field.setAttribute("required", "false");
                    facet_field.setAttribute("stored", "false");
                    facet_field.setAttribute("multiValued", "true");
                    fields.appendChild(facet_field);
                    Element copy_facet_field = doc.createElement("copyField");
                    copy_facet_field.setAttribute("source", f_name);
                    copy_facet_field.setAttribute("dest", lc_field_name);
                    fields.appendChild(copy_facet_field);
                }
            }

            fields.normalize();
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File tmp = new File(configDir, "managed-schema.xml" + "tmp");
            StreamResult result = new StreamResult(tmp);
            transformer.transform(source, result);
            // NOW WE NEED TO COPY THE SCHEMA.XML.TMP TO SCHEMA.XML
            tmp.renameTo(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
        }

        return false;
    }

    /**
     * Create the copy index for the particular field and type.
     * <p>
     * The most common use for this is when we want to copy a text type into a
     * string type... this will make faceting on phrases not just on words.
     *
     * @param field_name
     * @return
     */
    private boolean createCopyIndex(String _table, String field_name,
                                    String _type, SolrQueryResponse rsp) {
        // 1. edit the schema
        // 2. reload the schema
        // http://localhost:8983/solr/admin/cores?action=create_field_facet&schema=milton_Repository_HTL&field_name=$field_name&type=$field_type
        CoreContainer container = super.getCoreContainer();
        SolrCore source_core = container.getCore(_table);
        try {
            String solr_home = container.getSolrHome();
            String instance_directory = IOUTILs.getDirectory(source_core
                    .getResourceLoader().getInstancePath());

            solr_home = trimLead(solr_home);
            // String create_instance_dir =
            // trimLead(trimLead(instance_directory,
            // solr_home));
            addCopyField(instance_directory, field_name, _type);
            reloadCore(source_core, rsp);
            reindex(_table, "*:*");

            return true;
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return false;
    }

    private void reindex(String _table, String _query) {
        try {
            // query request
            CoreContainer container = super.getCoreContainer();
            // get the source_core
            SolrCore source_core = container.getCore(_table);
            // create the temp core
            Date ds = new Date();
            SimpleDateFormat fro = new SimpleDateFormat("yyMMddHHmmss");
            String tagd = fro.format(ds);
            String _temp = "__u_idex_" + tagd;
            String new_core_name = _temp;
            String solr_home = container.getSolrHome();
            String instance_directory = IOUTILs.getDirectory(source_core.getResourceLoader()
                    .getInstancePath());

            System.out
                    .println("source_core.getResourceLoader().getInstancePath(): "
                            + instance_directory);

            instance_directory = trimLead(instance_directory);
            solr_home = trimLead(solr_home);
            String create_instance_dir = trimLead(trimLead(instance_directory,
                    solr_home));
            String source_instance_directory = IOUTILs.getDirectory(source_core.getResourceLoader()
                    .getInstancePath());
            // System.out.println( " CREATE THE NEW CORE CONFIG ");

            // System.out.println("source_core.getDataDir(): "
            // + source_core.getDataDir());

            configNewCore(source_instance_directory, source_core.getDataDir(),
                    instance_directory, new_core_name);
            // System.out.println("CREATE THE NEW CORE CONFIG DIR : " +
            // create_instance_dir);
            SolrParams newCore_params = SolrRequestParsers
                    .parseQueryString("action=CREATE&name=" + new_core_name
                            + "&instanceDir=" + create_instance_dir + "/"
                            + new_core_name);
            LocalSolrQueryRequest solrReq_create = new LocalSolrQueryRequest(
                    null, newCore_params);
            SolrQueryResponse rsp = new SolrQueryResponse();
            // System.out.println ( " handl create action ");

            handleCreateAction(solrReq_create, rsp);
            // System.out.println (
            // " handle create action complete about to build the temp index");
            SolrParams params = SolrRequestParsers.parseQueryString("q="
                    + _query);
            LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest(
                    source_core, params);

            SolrIndexSearcher searcher = solrReq.getSearcher();
            IndexSchema is = source_core.getLatestSchema();
            Map<String, SchemaField> source_fields = is.getFields();
            ArrayList<String> facet_fields = new ArrayList<String>();
            Set<String> source_keys = source_fields.keySet();
            for (String s : source_keys) {
                // System.out.println(s);
                if (s.endsWith("__900807"))
                    facet_fields.add(getNonfacetFieldName(s));
            }
            if (searcher != null) {
                int max_doc = searcher.maxDoc();
                SolrCore dest_core = container.getCore(new_core_name);
                SolrParams uparams = SolrRequestParsers
                        .parseQueryString("action=update&name="
                                + dest_core.getName() + "&instanceDir="
                                + dest_core.getName());

                LocalSolrQueryRequest update_solrReq = new LocalSolrQueryRequest(
                        dest_core, uparams);
                UpdateRequestProcessorChain upc = dest_core
                        .getUpdateProcessingChain(null);
                UpdateRequestProcessor processor = upc.createProcessor(
                        update_solrReq, null);
                AddUpdateCommand cmd = new AddUpdateCommand(update_solrReq);
                int increment = 30000;
                for (int j = 0; j < max_doc; j += increment) {
                    // System.out.println(" j " + j);
                    for (int i = j; (i < max_doc) && (i < (j + increment)); i++) {
                        Document d = searcher.doc(i);
                        SolrInputDocument inputdoc = new SolrInputDocument();
                        for (IndexableField fieldable : d
                                .getFields()) {
                            String f_name = fieldable.name();
                            // if the field ends in 900807 we should copy
                            // the
                            // value from the orignal field over
                            // to this one.
                            for (String kfey : facet_fields) {

                                if (kfey.equalsIgnoreCase(f_name)) {
                                    String f_n = f_name;
                                    IndexableField fie = d.getField(f_name);
                                    SchemaField schemaField = is
                                            .getFieldOrNull(f_name);
                                    FieldType ftyp = schemaField.getType();
                                    Object value = ftyp.toObject(fie);
                                    // System.out.println("\t\t\t name : "+
                                    // f_name +
                                    // " and value " + value );
                                    inputdoc.addField(f_n + "__900807", value);
                                }

                            }
                            // System.out.println("\t\t\t\t" + f_name);
                            SchemaField schemaField = is.getFieldOrNull(f_name);
                            if (schemaField != null) {
                                FieldType fieldType = schemaField.getType();
                                Object value = fieldType.toObject(fieldable);
                                inputdoc.addField(f_name, value);
                            }

                        }
                        cmd.clear();
                        cmd.solrDoc = inputdoc;
                        processor.processAdd(cmd);
                    }
                    CommitUpdateCommand com = new CommitUpdateCommand(
                            update_solrReq, true);
                    processor.processCommit(com);
                }
                // now we point the original table to the new version
                renameCore(dest_core, source_core, rsp);
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }

    /**
     *
     */
    private void renameCore(SolrCore source_core, SolrCore new_core_name,
                            SolrQueryResponse rsp) {

        try {
            // now we rename the existing core
            MultiMapSolrParams params = SolrRequestParsers
                    .parseQueryString("action=RENAME&core="
                            + source_core.getName() + "&other="
                            + new_core_name.getName());


            CoreContainer cs = getCoreContainer();
            cs.rename(source_core.getName(), new_core_name.getName());

//
//            LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest(
//                    source_core, params);
//            handleRenameAction(solrReq, rsp);

        } catch (Exception _e) {
            _e.printStackTrace();
        }

    }

    private String getNonfacetFieldName(String f_name) {

        int itr = f_name.indexOf("__900807");
        if (itr > 0)
            return f_name.substring(0, itr);
        return f_name;
    }

    private void addCopyField(String _instanceDir, String _field_name,
                              String _type) {
        _instanceDir = _instanceDir + "/conf";
        File f = new File(_instanceDir, "managed-schema");
        // System.out.println(" We have the schema : " + f.getAbsolutePath());
        BufferedReader reader = null;
        try {
            File dir = new File(_instanceDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            reader = new BufferedReader(new FileReader(f));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
            // System.out.println("Root element :"
            // + doc.getDocumentElement().getNodeName());
            Node fields = doc.getElementsByTagName("fields").item(0);
            NodeList nList = doc.getElementsByTagName("field");
            String lc_field_name = _field_name + "__900807";
            boolean foundit = false;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    if (name.equalsIgnoreCase(lc_field_name))
                        foundit = true;
                }
            }
            if (!foundit) {
                Element facet_field = doc.createElement("field");
                facet_field.setAttribute("name", lc_field_name);
                facet_field.setAttribute("type", "string");
                facet_field.setAttribute("indexed", "true");
                facet_field.setAttribute("required", "false");
                facet_field.setAttribute("stored", "false");
                facet_field.setAttribute("multiValued", "true");
                fields.appendChild(facet_field);
                Element copy_facet_field = doc.createElement("copyField");
                copy_facet_field.setAttribute("source", _field_name);
                copy_facet_field.setAttribute("dest", lc_field_name);
                fields.appendChild(copy_facet_field);
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            reader.close();
            // NOW WE NEED TO COPY THE SCHEMA.XML.TMP TO SCHEMA.XML
            File tmp = new File(_instanceDir, "managed-schema" + "tmp");
            tmp.renameTo(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
        }
    }

    private void addField(String _instanceDir, String _field_name, String _type) {
        _instanceDir = _instanceDir + "/conf";
        File f = new File(_instanceDir, "managed-schema");
        // System.out.println("\n\n\t\t\t We have the schema : "
        // + f.getAbsolutePath() + "\n\n\n\n\n");
        BufferedReader reader = null;
        try {

            File dir = new File(_instanceDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            reader = new BufferedReader(new FileReader(f));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
            // System.out.println("Root element :"
            // + doc.getDocumentElement().getNodeName());
            Node fields = doc.getElementsByTagName("fields").item(0);
            NodeList nList = doc.getElementsByTagName("field");
            // System.out.println("\n\n\n-----------------------");
            String lc_field_name = _field_name;
            boolean foundit = false;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    if (name.equalsIgnoreCase(lc_field_name))
                        foundit = true;
                    // String type = eElement.getAttribute ("type");
                    // System.out.println(" name : "+ name + " type : "+ type );
                    // System.out.println("First Name : "
                    // + getTagValue("name", eElement));
                    // System.out.println("Last Name : "
                    // + getTagValue("type", eElement));
                    // System.out.println("Nick Name : "
                    // + getTagValue("indexed", eElement));
                    // System.out.println("Salary : "
                    // + getTagValue("required", eElement));

                }
            }
            if (!foundit) {
                Element facet_field = doc.createElement("field");
                facet_field.setAttribute("name", lc_field_name);
                facet_field.setAttribute("type", _type);
                facet_field.setAttribute("indexed", "true");
                facet_field.setAttribute("required", "false");
                facet_field.setAttribute("stored", "true");
                facet_field.setAttribute("multiValued", "false");
                fields.appendChild(facet_field);
                Element copy_facet_field = doc.createElement("copyField");
                copy_facet_field.setAttribute("source", _field_name);
                copy_facet_field.setAttribute("dest", "allterms");
                fields.appendChild(copy_facet_field);
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
            reader.close();
            // NOW WE NEED TO COPY THE SCHEMA.XML.TMP TO SCHEMA.XML
            File tmp = new File(_instanceDir, "managed-schema" + "tmp");
            tmp.renameTo(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
        }
    }

    private boolean removeField(String _table, String field_name,
                                SolrQueryRequest req, SolrQueryResponse rsp) {
        CoreContainer container = super.getCoreContainer();
        SolrCore source_core = container.getCore(_table);
        BufferedReader reader = null;
        try {
            String solr_home = container.getSolrHome();
            String instance_directory = source_core.getResourceLoader()
                    .getInstancePath().toString();
            instance_directory = trimLead(instance_directory) + "/conf";
            solr_home = trimLead(solr_home);
            File f = new File(instance_directory, "managed-schema");
            // System.out.println(" we have the schema in the edit field : "
            // + f.getAbsolutePath());
            reader = new BufferedReader(new FileReader(f));
            System.out.println(" instance_dir " + instance_directory);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
            // Node fields = doc.getElementsByTagName("fields").item(0);
            NodeList nList = doc.getElementsByTagName("field");
            String lc_field_name = field_name;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    if (name.equalsIgnoreCase(lc_field_name)) {
                        eElement.getParentNode().removeChild(nNode);

                    }
                }
            }
            nList = doc.getElementsByTagName("copyField");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("source");
                    if (name.equalsIgnoreCase(lc_field_name)) {
                        eElement.getParentNode().removeChild(nNode);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);

            // NOW WE NEED TO COPY THE SCHEMA.XML.TMP TO SCHEMA.XML
            File tmp = new File(instance_directory, "managed-schema" + "tmp");
            tmp.renameTo(f);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
        }
        return false;
    }

    private void removeField(String _instanceDir, String field_name) {
        _instanceDir = _instanceDir + "/conf";
        File f = new File(_instanceDir, "managed-schema");
        // System.out.println(" we have the schema in the edit field : "
        // + f.getAbsolutePath());
        PrintStream t_file = null;
        BufferedReader reader = null;
        try {
            t_file = new PrintStream(new File(_instanceDir,
                    "managed-schema" + "tmp"));
            reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();
            while (line != null) {
                String tline = line.toLowerCase();
                tline = tline.trim();

                if (tline.startsWith("<field name=\"" + field_name + "\"")) {
                    line = "";
                }
                if (tline.startsWith("<copyfield source=\"" + field_name)) {
                    line = "";
                }
                // System.out.println(" LINE : " + line);
                t_file.println(line);
                line = reader.readLine();
            }
            t_file.flush();

            // NOW WE NEED TO COPY THE SCHEMA.XML.TMP TO SCHEMA.XML
            File tmp = new File(_instanceDir, "managed-schema" + "tmp");
            tmp.renameTo(f);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
            IOUTILs.closeResource(t_file);
        }

    }

    /**
     * set the field type
     *
     * @param _table
     * @param field_name
     * @param to_type
     * @param req
     * @param rsp
     * @return
     */
    private boolean setFieldType(String _table, String field_name,
                                 String to_type, SolrQueryRequest req, SolrQueryResponse rsp) {

        System.out.println("------ set the field type method is called.. this may not be functional wit hthe upgrade to solr6------------------------");

        SolrParams _params = req.getParams();
        Date ds = new Date();
        SimpleDateFormat fro = new SimpleDateFormat("yyMMddHHmmss");
        String tagd = fro.format(ds);
        CoreContainer container = super.getCoreContainer();
        SolrCore source_core = container.getCore(_table);
        Iterator<String> itss = _params.getParameterNamesIterator();
        String copy_strategy = null;
        while (itss.hasNext()) {
            String k = itss.next();
            if (k.equalsIgnoreCase("copy_strategy")) {
                copy_strategy = _params.get("copy_strategy");
            }
        }
        CopyStrategy copy_strat = new CopyStrategy();
        copy_strat.set(field_name, to_type);

        System.out.println("------------------------------");

        try {
            String original_instance_directory = IOUTILs
                    .getDirectory(source_core.getResourceLoader()
                            .getInstancePath());
            String solr_home = super.getCoreContainer().getSolrHome();
            String create_instance_dir = trimLead(trimLead(
                    original_instance_directory, solr_home));
            String source_instance_directory = source_core.getResourceLoader()
                    .getInstancePath().toString();
            solr_home = trimLead(solr_home);
            String temp_core = "__cp_index__" + tagd;
            // create_instance_dir = create_instance_dir + temp_core;
            File new_conf_directory = configNewCore(source_instance_directory,
                    source_core.getDataDir(), original_instance_directory,
                    temp_core);
            // 2. edit temp schema with new field.
            File new_schema_file = new File(new_conf_directory, "managed-schema");
            editFieldType(new_schema_file, field_name, to_type);
            // setFieldInSchema(source_config, instance_directory + "/"
            // + temp_core, field_name, to_type);
            // 3. CREATE THE NEW CORE
            // System.out.println("\n\n\t\t instance_dir " + temp_core);
            // System.out
            // .println("\t\t\tcreate instance : " + create_instance_dir);
            SolrParams params = SolrRequestParsers
                    .parseQueryString("action=CREATE&name=" + temp_core
                            + "&instanceDir=" + create_instance_dir + "/"
                            + temp_core);
//            // System.out.println("instance directory in solr home: "
//            // + create_instance_dir);
//
//            // String solrSite = ABProperties.getSolrURL();
//            // TMSolrServer.callSolr(solrSite +
//            // "admin/cores?action=CREATE&name="
//            // + temp_core + "&instanceDir=" + create_instance_dir);
//
//            // {{----------------------------------------------------------------------------}}
//            // this is really the only thing that works.. but I can not get the
//            // data directory
//
//
//
//            CoreDescriptor dcore = buildCoreDescriptor(params, container);
//            // TODO this should be moved into CoreContainer, really...
//            SolrCore newCore = null;
//            try {
//                if (container.getZkController() != null) container.getZkController().register(temp_core, dcore);
//                container.getCoresLocator().create(container, dcore);
////                SolrCore core = container.create(dcore);
//
//
////                container.create(dcore, false);
//
//
//                rsp.add("core", core.getName());
//                container.getCoresLocator().persist(container, dcore);
//                newCore = core;
//            } catch (Exception _e) {
//                _e.printStackTrace();
//            }
//
//            // handleCreateAction(req, rsp);
//            // CoreDescriptor dcore = new CoreDescriptor(container, temp_core,
//            // create_instance_dir);
//            SolrCore dest_core = container.getCore(temp_core);
//            // {{----------------------------------------------------------------------------}}
//            System.out.println("Copy All");
//            copyAll(source_core, dest_core, copy_strat);
//            CoreDescriptor source_core_desc = source_core.getCoreDescriptor();
//            container.getCoresLocator().delete(container, source_core_desc);
//            container.rename(newCore.getName(), source_core.getName());
            return true;
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return true;
    }

    private void editFieldType(File f, String field_name, String to_type) {
        System.out.println(" we have the schema in the edit field : "
                + f.getAbsolutePath());
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();
            Node fields = doc.getElementsByTagName("fields").item(0);
            System.out.println(" \n\t\thunting for the field list : \t\t\n"
                    + fields.getChildNodes().getLength());
            NodeList nList = fields.getChildNodes();

            String lc_field_name = field_name;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                // lg.debug(" node : " + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    if (name.equalsIgnoreCase(lc_field_name)) {
                        // System.out.println(" setting the field name " +
                        // lc_field_name
                        // + " && " + name + " to type : " + to_type);
                        eElement.setAttribute("type", to_type);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new field.
     *
     * @return
     */
    private boolean createNewField(String _table, String _new_field,
                                   String _type, SolrQueryRequest req, SolrQueryResponse rsp) {

        // 1. edit the schema
        // 2. reload the schema

        CoreContainer container = super.getCoreContainer();
        SolrCore source_core = container.getCore(_table);
        try {
            String solr_home = container.getSolrHome();
            String instance_directory = source_core.getResourceLoader()
                    .getInstancePath().toString();
            instance_directory = trimLead(instance_directory);
            solr_home = trimLead(solr_home);
            // String create_instance_dir =
            // trimLead(trimLead(instance_directory,
            // solr_home));
            addField(instance_directory, _new_field, _type);
            reloadCore(source_core, rsp);
            return true;
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return false;
    }

    private void reloadCore(SolrCore core, SolrQueryResponse _r) {
        SolrParams params = SolrRequestParsers
                .parseQueryString("action=RELOAD&core=" + core.getName());
        // http://localhost:8983/solr/admin/cores?action=RELOAD&core=core0
        try {
            CoreContainer cc = getCoreContainer();
            cc.reload(core.getName());
        } catch (Exception ex) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
                    "Error handling 'reload' action", ex);
        }
    }

    private boolean copySchema(String orig, String dest, SolrQueryRequest req,
                               SolrQueryResponse rsp) {
        SolrParams _params = req.getParams();
        Date ds = new Date();
        SimpleDateFormat fro = new SimpleDateFormat("yyMMddHHmmss");
        String tagd = fro.format(ds);

        // String _temp = "__updated_index_" + tagd;
        String temp_core = _params.get("dest");// + _temp;
        CoreContainer container = super.getCoreContainer();
        SolrCore source_core = container.getCore(orig);
        SolrCore core = req.getCore();
        try {
            String solr_home = container.getSolrHome();
            String instance_directory = source_core.getResourceLoader()
                    .getInstancePath().toString();
            instance_directory = trimLead(instance_directory);
            solr_home = trimLead(solr_home);
            String create_instance_dir = trimLead(trimLead(instance_directory,
                    solr_home));

            String source_instance_directory = source_core.getResourceLoader()
                    .getInstancePath().toString();
            // build the new core
            configNewCore(source_instance_directory, source_core.getDataDir(),
                    instance_directory, temp_core);
            // we need to create the new directory here.
            // editSchema(instance_directory + "/" + temp_core, orig, dest);

            System.out.println(" instance_dir " + instance_directory);
            System.out.println("create instance : " + create_instance_dir);

            SolrParams params = SolrRequestParsers
                    .parseQueryString("action=CREATE&name=" + temp_core
                            + "&instanceDir=" + create_instance_dir + "/"
                            + temp_core);

            LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest(core,
                    params);
            handleCreateAction(solrReq, rsp);
            SolrCore dest_core = container.getCore(temp_core);

            // 3. edit temp schema with new field.
            copyAll(source_core, orig, dest_core, dest);

            // now we rename the existing core
            // params = SolrRequestParsers
            // .parseQueryString("action=RENAME&core=" + source
            // + "&other=" + source + "_deprecated_");

            CoreContainer cs = getCoreContainer();
            cs.rename(orig, dest);
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return false;
    }

    /**
     * 1) find the schema 2) copy it to temp 3) edit temp schema with new field
     * name 4) index schema to the temp 5) rename the temp to the schema value
     * 6) clean up
     *
     * @param schema
     * @param orig
     * @param dest
     * @param req
     * @param rsp
     * @return
     */
    private boolean reNameField(String schema, String orig, String dest,
                                SolrQueryRequest req, SolrQueryResponse rsp) {
        SolrParams _params = req.getParams();
        Date ds = new Date();
        SimpleDateFormat fro = new SimpleDateFormat("yyMMddHHmmss");
        String tagd = fro.format(ds);
        String _temp = "__updated_index_" + tagd;
        String temp_core = _params.get("schema") + _temp;
        CoreContainer container = super.getCoreContainer();
        String source = _params.get("schema");
        SolrCore source_core = container.getCore(source);
        SolrCore core = req.getCore();

        try {

            String instance_directory = IOUTILs.getDirectory(source_core
                    .getResourceLoader().getInstancePath());

            String solr_home = super.getCoreContainer().getSolrHome();
            String create_instance_dir = trimLead(trimLead(instance_directory,
                    solr_home));
            String source_instance_directory = source_core.getResourceLoader()
                    .getInstancePath().toString();

            configNewCore(source_instance_directory, source_core.getDataDir(),
                    instance_directory, temp_core);
            File source_config = IOUTILs.getFile(instance_directory,
                    "managed-schema");
            editSchema__(source_config, instance_directory + "/" + temp_core,
                    orig, dest);
            // copy the values from the first field to the second
            // reindex(schema, "*:*", orig, dest);

            System.out.println(" instance_dir " + instance_directory);
            System.out.println("create instance : " + create_instance_dir);

            SolrParams params = SolrRequestParsers
                    .parseQueryString("action=CREATE&name=" + temp_core
                            + "&instanceDir=" + create_instance_dir + "/"
                            + temp_core);
            LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest(core,
                    params);
            handleCreateAction(solrReq, rsp);
            SolrCore dest_core = container.getCore(temp_core);

            // 3. edit temp schema with new field.
            copyAll(source_core, orig, dest_core, dest);

            // now we rename the existing core
            params = SolrRequestParsers.parseQueryString("action=RENAME&core="
                    + source + "&other=" + source + "_deprecated_");
            solrReq = new LocalSolrQueryRequest(core, params);

            CoreContainer cs = getCoreContainer();
            cs.rename(source, source + "_deprecated_");
//            handleRenameAction(solrReq, rsp);

            System.out.println("RENAME COMPLETE");

            params = SolrRequestParsers.parseQueryString("action=RENAME&core="
                    + temp_core + "&other=" + source);
            solrReq = new LocalSolrQueryRequest(dest_core, params);
//            handleRenameAction(solrReq, rsp);
            cs.rename(temp_core, source);

        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return false;
    }

    private String trimLead(String instance_directory, String solr_home) {
        if (instance_directory.startsWith(solr_home))
            instance_directory = instance_directory.substring(solr_home
                    .length());
        return instance_directory;
    }

    private String trimLead(String instance_directory) {
        System.out.println("\n isntance_directory \t " + instance_directory);
        // if (instance_directory.startsWith("./"))
        // instance_directory = instance_directory.substring(2);
        // if (instance_directory.startsWith("/"))
        // instance_directory = instance_directory.substring(1);
        return instance_directory;
    }

    private void copyAll(SolrCore source_core, String orig, SolrCore dest_core,
                         String dest) {
        try {
            // query request
            SolrParams params = SolrRequestParsers.parseQueryString("q=*:*");
            LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest(
                    source_core, params);
            SolrIndexSearcher searcher = solrReq.getSearcher();
            IndexSchema is = source_core.getLatestSchema();

            // update request
            SolrParams uparams = SolrRequestParsers
                    .parseQueryString("action=update&name="
                            + dest_core.getName() + "&instanceDir="
                            + dest_core.getName());
            LocalSolrQueryRequest update_solrReq = new LocalSolrQueryRequest(
                    dest_core, uparams);
            UpdateRequestProcessorChain upc = dest_core
                    .getUpdateProcessingChain(null);
            UpdateRequestProcessor processor = upc.createProcessor(
                    update_solrReq, null);

            AddUpdateCommand cmd = new AddUpdateCommand(update_solrReq);
            CommitUpdateCommand com = new CommitUpdateCommand(update_solrReq,
                    true);
            System.out.println(" we are searching : " + is.getSchemaName());
            if (searcher != null) {
                int max_doc = searcher.maxDoc();
                System.out.println(" this index has a max doc count of :"
                        + max_doc);

                for (int i = 0; i < max_doc; i++) {
                    Document d = searcher.doc(i);
                    SolrInputDocument inputdoc = new SolrInputDocument();
                    int col_index = 0;
                    for (IndexableField fieldable : d
                            .getFields()) {
                        String f_name = fieldable.name();
                        String[] values = d.getValues(f_name);
                        col_index++;
                        SchemaField schemaField = is.getFieldOrNull(f_name);
                        if (f_name.equalsIgnoreCase(orig)) {// this is the
                            // mapping right
                            // here!!!
                            f_name = dest;
                        }
                        if (schemaField != null) {
                            FieldType fieldType = schemaField.getType();
                            Object value = fieldType.toObject(fieldable);
                            System.out.println(" field type : " + value);
                            inputdoc.addField(f_name, value);
                        }
                    }
                    cmd.clear();
                    cmd.solrDoc = inputdoc;
                    processor.processAdd(cmd);
                }
                RequestHandlerUtils.handleCommit(update_solrReq, processor,
                        null, true);
                processor.processCommit(com);
                System.out.println("\n\n PROCESS IS COMMITTED \n");
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }

    private void copyAll(SolrCore source, SolrCore destination,
                         CopyStrategy _strat) {
        HttpSolrClient solr = null;
        HttpSolrClient ss = null;
        try {
            int INCREMENT = 1000;
            SolrParams params = SolrRequestParsers
                    .parseQueryString("q=*:*&start=0&rows=" + INCREMENT);
            // LocalSolrQueryRequest solrReq = new LocalSolrQueryRequest(
            // source_core, params);
            // SolrIndexSearcher searcher = solrReq.getSearcher();
            String solr_url = ABProperties.getSolrURL();
            solr = new HttpSolrClient.Builder(solr_url + destination.getName()).build();
            ss = new HttpSolrClient.Builder(solr_url + source.getName()).build();
            QueryResponse response = ss.query(params);
            SolrDocumentList sresult_list = response.getResults();
            long max_doc = sresult_list.getNumFound();
            // String path = config.getDataDir();

            IndexSchema sc = destination.getLatestSchema();

            for (int i = 0; i < max_doc; i += INCREMENT) {
                params = SolrRequestParsers.parseQueryString("q=*:*&start=" + i
                        + "&rows=" + INCREMENT);

                response = ss.query(params);
                List<SolrDocument> result_list = response.getResults();
                ArrayList<SolrInputDocument> godocs = new ArrayList<SolrInputDocument>();
                for (int j = 0; j < result_list.size(); j++) {
                    SolrDocument d = result_list.get(j);
                    System.out.println(" \n\n\n\t\t\t\t\tindex : " + j);
                    LinkedHashMap<String, SolrInputField> dfields = new LinkedHashMap<String, SolrInputField>();

                    Collection<String> indexed_fields = d.getFieldNames();
                    for (String f_name : indexed_fields) {
                        // System.out.println(" f_name " + f_name);
                        FieldType dest_type = sc.getFieldType(f_name);

                        if (f_name.equalsIgnoreCase("TMID_lastUpdated")) {
                            Object dated = d.get(f_name);
                            if (dated instanceof String) {
                                Date dd = parseDate(dated.toString());
                                SolrInputField inpt = new SolrInputField(f_name);
//                                inpt.setValue(dd, 1.0f);
                                dfields.put(f_name, inpt);
                            } else {
                                Date dd = (Date) dated;
                                SolrInputField inpt = new SolrInputField(f_name);
//                                inpt.setValue(dd, 1.0f);
                                dfields.put(f_name, inpt);
                            }

                        } else if (f_name.equalsIgnoreCase("TMID")) {
                            String uid = d.get(f_name).toString();
                            SolrInputField inpt = new SolrInputField(f_name);
//                            inpt.setValue(uid, 1.0f);
                            dfields.put(f_name, inpt);
                        } else if (f_name.equals("_version_")) {

                            // skip version

                        } else {
                            Object resvalue = d.get(f_name);
                            if (_strat.hasStrategy(f_name)) {
                                Object av_value = _strat.executed(f_name,
                                        resvalue.toString());
                                SolrInputField inpt = new SolrInputField(f_name);
//                                inpt.setValue(av_value, 1.0f);
                                dfields.put(f_name, inpt);
                            } else {
                                Object value = d.get(f_name);
                                SolrInputField inpt = new SolrInputField(f_name);
                                // try to convert a generic object to an sint
                                if (dest_type.getTypeName().equalsIgnoreCase("sint")
                                        || dest_type.getTypeName()
                                        .equalsIgnoreCase("int")) {
                                    if (value instanceof Number) {
                                        Double dv = Double.parseDouble(value
                                                .toString());
                                        Integer iv = dv.intValue();
//                                        inpt.setValue(iv, 1.0);
                                        dfields.put(f_name, inpt);
                                    } else {
                                        try {
                                            Double dv = Double
                                                    .parseDouble(value
                                                            .toString());
                                            Integer iv = dv.intValue();
//                                            inpt.setValue(iv, 1.0f);
                                            dfields.put(f_name, inpt);
                                        } catch (NumberFormatException nf) {
                                            nf.printStackTrace();
                                        }
                                    }
                                } else {
//                                    inpt.setValue(value, 1.0f);
                                    dfields.put(f_name, inpt);
                                }
                            }
                        }
                    }
                    SolrInputDocument sd = new SolrInputDocument(dfields);
                    godocs.add(sd);
                }
                // for (SolrInputDocument dssd : godocs) {
                // Set<String> keys = dssd.keySet();
                // for (String k : keys) {
                // // System.out.println("\t\t\t ADDING  \t\t\t\t "
                // // + dssd.getField(k));
                // }
                // }
                if (godocs.size() > 0) {
                    System.out.println("Adding " + godocs.size());
                    solr.add(godocs);
                }
                System.out.println("Committing...");
                solr.commit();
                System.out.println("Complete.");
            }

            GB.print("\n\n PROCESS IS COMMITTED \n");
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            IOUTILs.closeResource(solr);
            IOUTILs.closeResource(ss);
        }
    }

    // 2013-10-21T01:51:25.33
    static SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd'T'KK:mm:ss.SS");

    private Date parseDate(String date) {
        Date parse = new Date();
        try {
            parse = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    private void editSchema__(File _orginal_schema_file, String _instanceDir,
                              String orig, String dest) {

        _instanceDir = _instanceDir + "/conf";
        File f = new File(_instanceDir, "managed-schema");
        System.out.println(" We have the schema : " + f.getAbsolutePath());
        try {
            File dir = new File(_instanceDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();

            Node fields = doc.getElementsByTagName("fields").item(0);
            NodeList nList = doc.getElementsByTagName("field");
            String lc_field_name = orig;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    if (name.equalsIgnoreCase(lc_field_name)) {
                        eElement.setAttribute("name", dest);
                    }
                }
            }
            NodeList cList = doc.getElementsByTagName("copyField");
            for (int temp = 0; temp < cList.getLength(); temp++) {
                Node cNode = cList.item(temp);
                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) cNode;
                    String name = eElement.getAttribute("dest");
                    String source = eElement.getAttribute("source");
                    if (name.equalsIgnoreCase("allterms")) {
                        if (source.equalsIgnoreCase(orig)) {
                            eElement.setAttribute("source", dest);
                        }
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (Exception _e) {
            _e.printStackTrace();
        }

    }

    private void setFieldInSchema(File _orginal_schema_file,
                                  String _instanceDir, String lc_field_name, String type) {

        _instanceDir = _instanceDir + "/conf";
        File f = new File(_instanceDir, "managed-schema");
        System.out.println(" We have the schema : " + f.getAbsolutePath());
        try {
            File dir = new File(_instanceDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();

            Node fields = doc.getElementsByTagName("fields").item(0);
            NodeList nList = doc.getElementsByTagName("field");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("name");
                    if (name.equalsIgnoreCase(lc_field_name)) {
                        eElement.setAttribute("type", type);
                    }
                }
            }
            NodeList cList = doc.getElementsByTagName("copyField");
            for (int temp = 0; temp < cList.getLength(); temp++) {
                Node cNode = cList.item(temp);
                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) cNode;
                    String name = eElement.getAttribute("dest");
                    String source = eElement.getAttribute("source");
                }
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (Exception _e) {
            _e.printStackTrace();
        }

    }

    /**
     * this instancedir is the resourceloader directory for the core.
     *
     * @param _instanceDir
     * @param name
     */
    private static File configNewCore(String _source_instance,
                                      String source_data_directory, String _instanceDir, String name) {
        // dest conf
        // if (!_instanceDir.startsWith("/")) {
        // _instanceDir = "./" + _instanceDir;
        // }
        System.out.println("\n\n\n\n\n\n _instanceDir : " + _instanceDir);
        System.out.println(" name : " + name);
        File new_dir = new File(IOUTILs.getDirectory(_instanceDir), name);
        if (new_dir.mkdir()) {
            System.out.println(" dir " + new_dir.getAbsolutePath()
                    + " has been created ");
        } else
            System.out.println(" \n\n---\t\tdir " + new_dir.getAbsolutePath()
                    + " failed to create. \n\n\n");
        File new_conf_dir = new File(new_dir, "conf");
        System.out.println(" new_conf_dir absolutedirectory : :"
                + new_conf_dir.getAbsolutePath());
        System.out.println(" \n\n");
        if (new_conf_dir.mkdirs()) {
            System.out.println("Directory has been created : "
                    + new_conf_dir.getAbsolutePath());
        } else
            System.out.println(" Failed to create the new directory : "
                    + new_conf_dir.getAbsolutePath());
        File ofile = new File(IOUTILs.getDirectory(_source_instance), "conf");

        File conf_file = new File(new_conf_dir, "solrconfig.xml");
        try {

            System.out.println("from directory: " + ofile);
            System.out.println("to : " + new_conf_dir);

            FileUtils.copyDirectory(ofile, new_conf_dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" copy complete ...");
        System.out.println("Adjusting the solrconfig");
        setDataDir(source_data_directory, name, conf_file);
        return new_conf_dir;

    }

    private static void setDataDir(String dataDir, String name, File _conf) {
        System.out.println("\n\n\nconfig: \t\t" + _conf.getAbsolutePath());
        System.out.println("\n\n\n");
        PrintWriter pr = null;
        BufferedReader reader = null;
        try {
            if (_conf.exists()) {
                System.out.println(" We found the config file : "
                        + _conf.getAbsolutePath());
                try {

                    File outfile = new File(_conf.getParent(), "solrconfig.tmp");
                    pr = new PrintWriter(outfile);

                    reader = new BufferedReader(new FileReader(_conf));
                    String line = reader.readLine();
                    if (!dataDir.endsWith("/"))
                        dataDir = dataDir + "/";
                    while (line != null) {
                        if (line.contains("<dataDir>")) {
                            line = "<dataDir>" + dataDir + "" + name + "</dataDir>";
                        }
                        pr.println(line);
                        line = reader.readLine();
                    }
                    pr.flush();

                    File prod = new File(_conf.getParent(), "solrconfig.xml");
                    outfile.renameTo(prod);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } finally {
            IOUTILs.closeResource(pr);
            IOUTILs.closeResource(reader);
        }
    }

    public boolean createCoreAsGAdminPlugin(LocalSolrQueryRequest solrReq_create) {
//        handleCreateAction(solrReq_create, rsp);
        return true;
    }

    private void handleCreateAction(LocalSolrQueryRequest solrReq_create, SolrQueryResponse rsp) {

        CoreContainer container = getCoreContainer();
//        container.create()

    }

    private void handleCreateAction(String core_name, Path path, Map<String, String> params) {
        CoreContainer container = getCoreContainer();
//        container.create(core_name, path, params);
    }


    public static void main(String[] srgs) {

    }

}
