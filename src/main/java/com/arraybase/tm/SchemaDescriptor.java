package com.arraybase.tm;

import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
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
import java.util.HashMap;

public class SchemaDescriptor {

	private GBLogger lg = GBLogger.getLogger(SchemaDescriptor.class);
	private HashMap<String, HashMap<String, String>> sc_fields = new HashMap<String, HashMap<String, String>>();
	private SolrCore core = null;

	public SchemaDescriptor(SolrCore _core) {
		build(_core);
	}

	public SolrCore getCore() {
		return core;
	}

	private void build(SolrCore _core) {
		BufferedReader reader = null;
		try {
			core = _core;
			sc_fields = new HashMap<String, HashMap<String, String>>();
			String _instanceDir = IOUTILs.getDirectory ( _core.getResourceLoader().getInstancePath());
			CoreContainer container = _core.getCoreContainer();
			String solr_home = container.getSolrHome();
			_instanceDir = TMStringUtils.trimLead(_instanceDir);
			lg.debug("INSTANCE DIRECTORY ------- : " + _instanceDir);
			if (!_instanceDir.startsWith("./")) {
				// _instanceDir = "./" + _instanceDir;
			}
			solr_home = TMStringUtils.trimLead(solr_home);
			lg.debug("INSTANCE DIRECTORY ------- : " + _instanceDir);
			_instanceDir = _instanceDir + "/conf";

			File dir = new File(_instanceDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(_instanceDir, "schema.xml");
			if (!f.exists()) {
				f = new File("/" + _instanceDir, "schema.xml");
			} else
				lg.debug(" We have the schema : " + f.getAbsolutePath());

			if (!f.exists()) {
				f = new File(dir.getCanonicalPath(), "schema.xml");

			}

			if (f.exists()) {
				lg.debug(" we found the file ");
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

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					HashMap<String, String> params = new HashMap<String, String>();

					Element eElement = (Element) nNode;
					String name = eElement.getAttribute("name");
					String type = eElement.getAttribute("type");
					String indexed = eElement.getAttribute("indexed");
					String stored = eElement.getAttribute("stored");
					String required = eElement.getAttribute("required");
					String multiValued = eElement.getAttribute("multiValued");
					params.put("name", name);
					params.put("type", type);
					params.put("indexed", indexed);
					params.put("stored", stored);
					params.put("required", required);
					params.put("multiValued", multiValued);
					sc_fields.put(name, params);

					lg.debug(" name : " + name + " type : " + type);
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
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
			// NOW WE NEED TO COPY THE SCHEMA.XML.TMP TO SCHEMA.XML
			File tmp = new File(_instanceDir, "managed-schema.xml" + "tmp");
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

	public HashMap<String, HashMap<String, String>> getFields() {
		return sc_fields;
	}

}
