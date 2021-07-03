package com.arraybase.flare.solr;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.lac.LAC;
import com.arraybase.lac.LACReference;
import com.arraybase.lac.LacFactory;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.IOUTILs;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * A class that is used to encapsulate the different represetnations of a core.
 * 
 * @author donaldm
 * 
 */
public class GBSolr {

	/**
	 * This is a method that pulls the core object from the lac.
	 * 
	 * if a link points to a core this object will pull that out.
	 * 
	 * @param _lac
	 * @return
	 * @deprecated
	 */
	public static String getCoreFromLAC(String _lac, DBConnectionManager db) {

		// this is the structure of the legacy lac format:
		if (_lac.startsWith("com.tissuematch.tm3.mylib.TMLibrary")) {
			LACReference ref = LacFactory.getLACReference(_lac, db);
			if (ref == null)
				return null;
			else
				return ref.getReference();
		} else {
			String _schema = LAC.getTarget(_lac);
			return _schema;

		}
		// // the first condition is if the lac is a legacy link ( i.e. )
		// String data = LAC.getData(_lac);
		//
		// return data;
		//
	}

	public static String getCoreFromLAC(String lac) {
		DBConnectionManager dbcm = new DBConnectionManager();
		return getCoreFromLAC(lac, dbcm);
	}

	/**
	 * Print to standard out the key and values for the solrdocument
	 * 
	 * @param solr_doc
	 */
	public static void printDocument(SolrInputDocument solr_doc) {

		Collection<String> field_names = solr_doc.getFieldNames();
		for (String f : field_names) {
			System.out.println(f + " : " + solr_doc.get(f).toString());
		}
	}

	public static String getCore(String path) {
		GBNodes nodes = GB.getNodes();
		return nodes.getCore(path);
	}

	public static List<String> getColumnPropertiesAsList(String path) {
		GBNodes nodes = GB.getNodes();
		TNode nod = nodes.getNode(path);
		DBConnectionManager db = new DBConnectionManager();
		TableManager tmanager = new TableManager(db);
		String linkl = nod.getLink();
		List<String> col_order = tmanager.getColumnOrder(linkl);
		return col_order;
	}

	/**
	 * This will attempt to start the solr server as defined in the solrRoot
	 * param.
	 * 
	 * Currently this is only configured for the linux/mac dist. (Amazon)
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void startMasterInstance() throws IOException,
			InterruptedException {
		Properties pr = new Properties();
		// we need to determine the location of the init.properties file
		String initfile = "scripts/install_config_linux/init.properties";
		File fif = new File(initfile);
		if (!fif.exists()) {
			throw new FileNotFoundException("Failed to find properties file : "
					+ fif.getAbsolutePath());
		}
		FileReader reader = new FileReader(fif);
		try {
			pr.load(reader);
			String solr_root = pr.getProperty("solrStart");
			GB.print("Solr start=  " + solr_root);
			if (solr_root.endsWith("/")) {
				solr_root = solr_root.substring(0, solr_root.length() - 1);
			}
			File f = new File(solr_root);
			File parent = f.getParentFile();

			if (!f.exists())
				throw new FileNotFoundException(
						"Failed to find the solr root as defined in the properties file : "
								+ solr_root);
			File startf = new File(solr_root);

			String goSolr = "cd " + parent.getAbsolutePath() + " && java -Dsolr.solr.home=cores -jar start.jar &";
//				+ startf.getAbsolutePath() + " &";
			GB.print(">> " + goSolr);
			if (!startf.exists()) {
				throw new FileNotFoundException(
						"Failed to find the start script : start.sh in the solr root location ");
			} else
				GB.executeExternalProcess(goSolr);
		} finally {
			IOUTILs.closeResource(reader);
		}
	}
}
