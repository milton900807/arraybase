package com.arraybase.modules;

import com.arraybase.*;
import com.arraybase.db.Configuration;
import com.arraybase.db.HBConnect;
import com.arraybase.db.HBType;
import com.arraybase.db.InstallationDBConnectionManager;
import com.arraybase.lac.LACReference;
import com.arraybase.lac.LacFactory;
import com.arraybase.lac.LacReferenceSaveException;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GBInstaller implements GBModule {

	private static GBLogger log = GBLogger.getLogger(GBInstaller.class);
	private String CONNECTION_PROPERTIES = "install_db.properties";

	public String getModName() {
		return "GB Installer";
	}

	public void exec(Map<String, Object> l) throws UsageException {
		throw new UsageException("This is not implemented. ");
	}

	public void exec(List<String> l) throws UsageException {

		String inst_dir = "install_config";

		// {{ WE CAN GENERALIZE THIS }}
		if (l.size() > 0)
			inst_dir = l.get(0);

		// {{ make sure the installation configuration directory is there.
		File f = new File(inst_dir);
		if (f.exists() && f.isDirectory()) {
			log.install("Installation directory was found ");
		} else {
			throw new UsageException("There was no installation directory "
					+ f.getAbsolutePath());
		}
		File config_file;
		try {
			config_file = buildSchema(f);
		} catch (InstallationProblemException e1) {
			e1.printStackTrace();
			throw new UsageException("Config file read exception. "
					+ f.getAbsolutePath());
		}
		// initialize the hb config file with the
		// new database hibernate config file
		// first we have to build the directory
		GBNodes gbn = GB.getNodes();

		// load the jdbc connection properties
		File pfile = new File(f, CONNECTION_PROPERTIES);
		Properties props = new Properties();
		FileReader reader = null;
		try {
			reader = new FileReader(pfile);
			props.load(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new UsageException(
					"Failed to load the properties file for the database connection");
		} catch (IOException e) {
			e.printStackTrace();
			throw new UsageException(
					"Failed to load the properties file for the database connection");
		} finally {
			IOUTILs.closeResource(reader);
		}

		GBNodes to = new GBNodes(new InstallationDBConnectionManager(
				config_file, props));

		String[] rotos = GB.getNodes().getRoots();
		for (String root : rotos) {
			TNode rootn = gbn.getNode(root);
			TPath p = gbn.getTPath(rootn);
			if (p != null && rootn != null)
				saveNode(rootn, p, gbn, to);
			// first thing is to create the schema for the new database
		}
	}

	/**
	 * The heap method that works
	 * 
	 * @param _node
	 * @param _path
	 * @param from
	 * @param _to
	 */
	private void saveNode(TNode _node, TPath _path, GBNodes from, GBNodes _to) {
		List<Integer> list = _node.getReference();
		_node.setNode_id(-1l);
		_path.setPath_id(-1l);
		Session sess = _to.getDBConnectionManager().getSession();
		try {
			sess.beginTransaction();
			// save the node and the coresponding path object
			_to.save(_node, sess);
			_path.setNode_id(_node.getNode_id());
			_to.save(_path, sess);
			sess.getTransaction().commit();
			if (_node.getLink() != null) {
				// {{ NOW WE NEED TO OPERATE THE LAC }}
				// 1. load the lac reference from the 'from' database
				LACReference lo = LacFactory.getLACReference(_node.getLink(),
						from.getDBConnectionManager());
				if (lo != null) {
					// 2. save the lac reference to the 'to' database
					try {
						lo.save(_to.getDBConnectionManager());
					} catch (LacReferenceSaveException e) {
						e.printStackTrace();
						System.exit(1);
					}
				} else {
					log.error(" FAILED TO FIND A LAC REFERENCE OBJECTS ");
				}
			}
			ArrayList<Integer> new_references = new ArrayList<Integer>();
			for (int i : list) {
				TNode ch = from.getNode(i);
				if (ch != null) {
					TPath p = from.getTPath(ch);
					if (p != null)
						saveNode(ch, p, from, _to);
					new_references.add((int) ch.getNode_id());
				}
			}

			sess = _to.getDBConnectionManager().getSession();
			sess.beginTransaction();
			_node.setReference(new_references);
			_to.save(_node, sess);
			sess.getTransaction().commit();
		} finally {
			HBConnect.close(sess);
		}
	}

	@Deprecated
	protected File buildSchema(File f) throws InstallationProblemException {

		// look for the hibrernate file
		log.install("Creating the GB schema  " + f.getAbsolutePath());
		File hb = new File(f, "hb.cfg.xml");
		if (!hb.exists())
			throw new InstallationProblemException(
					"Failed to find the hibernate install file "
							+ hb.getAbsolutePath());
		// @SuppressWarnings("deprecation")
		AnnotationConfiguration config = new AnnotationConfiguration();
		Class[] cls = HBType.All.getClasses();
		for (Class cl : cls) {
			config.addAnnotatedClass(cl);
		}
		config.configure(hb);

		Properties p = config.getProperties();
		String connection_url = p.getProperty("connection.url");
		log.install("New database is : " + connection_url);
		String user = p.getProperty("connection.username");
		log.install("connection.username : " + user);

		if (connection_url == null)
			p.getProperty("url");
		if (connection_url.contains("biodev")
				|| connection_url.contains("bioprd")
				|| connection_url.contains("biotst")) {

			if (user.equalsIgnoreCase("htl_admin")) {

				throw new InstallationProblemException(
						"Please verify this connection.  You are about to "
								+ "blow away a significant amount of data.  Are you sure?  If so.  Please change this code in "
								+ "SVN i.e. commit the commented out version run again and return this exception to its uncommented state."
								+ "---and check it back into svn.");
			}
		}
		new SchemaExport(config).create(true, true);
		return hb;
	}

	public static void run(String[] _args) {
		// {{ SEVERAL DIFFERENT INSTALL TYPES }}
		String type = _args[1];
		if (type.equalsIgnoreCase("solrplugin")) {
			String jar = _args[2];
			String server_root = _args[3];
			System.out.println(installSolr(jar, server_root));
		} else {
			try {
				GBModule ins = Mod.getModule(GB.INSTALL, type, null);
				ArrayList<String> l = new ArrayList<String>();
				for (int i = 2; i < _args.length; i++) {
					l.add(_args[i]);
				}
				if (ins == null) {
					System.out
							.println(" Failed to find the install module for "
									+ type);
					return;
				}
				ins.exec(l);
			} catch (UsageException e) {
				GB.print(e.getLocalizedMessage());
			} catch (GBModuleNotFoundException e) {
				e.printStackTrace();
			}
			return;
		}
	}

	private static String installSolr(String jar, String server_root) {

		File jar_file = new File(jar);
		if (!jar_file.exists()) {
			System.err.println("Could not find the jar file " + jar);
			return "Installation failed.";
		}

		File f = new File(server_root, "solr.xml");
		if (!f.exists()) {
			System.err
					.println("Server root is not correct.  Failed to find the solr.xml");
			return "Failed to install.";
		}
		File lib = new File(server_root, "lib");
		if (!lib.exists()) {
			System.err
					.println("Server root is not correct.  Failed to find the lib directory in the solr home");
			return "Failed to install.";
		}

        BufferedReader bred = null;
        PrintStream str = null;
		try {
			File writeFile = new File(server_root, "solr.xml.cp");
			FileReader reader = new FileReader(f);
			bred = new BufferedReader(reader);

			str = new PrintStream(writeFile);
			String line = bred.readLine();
			while (line != null) {

				String t = line.toLowerCase().trim();
				if (t.startsWith("<cores")) {
					line = "<cores adminPath=\"/admin/cores\" adminHandler=\"com.arraybase.solr.plugin.GAdmin\">";
				} else if (t.contains("persistent=\"")) {
					line = "<solr sharedLib=\"lib\" persistent=\"true\">";
				}
				str.println(line);
				line = bred.readLine();
			}
			str.flush();
			str.close();
			FileUtils.copyFile(writeFile, f);
			FileUtils.copyFileToDirectory(jar_file, lib);

			// {{ COPY THE CONF.ZIP FILE TO THE SOLR DIRECTORY }}
			// {{ WE WILL MOVE THIS TO A DIRECTORY WITHOUT THE WIERDNESS OF ZIP
			// TECHNOLOGY }}
			File conf_dir = new File(server_root, "gbplugin");
			if (!conf_dir.exists())
				if (!conf_dir.mkdir()) {
					return "Failed to create the gbplugin configuration directory ";
				} else {
					GBUtil.write(Configuration
							.getResourceAsString("conf/admin-extra.html"),
							new File(conf_dir, "admin-extra.html"));
					GBUtil.write(Configuration
							.getResourceAsString("conf/elevate.xml"), new File(
							conf_dir, "elevate.xml"));
					GBUtil.write(Configuration
							.getResourceAsString("conf/protwords.txt"),
							new File(conf_dir, "protwords.txt"));
					GBUtil.write(Configuration
							.getResourceAsString("conf/schema.xml"), new File(
							conf_dir, "schema.xml"));
					GBUtil.write(Configuration
							.getResourceAsString("conf/solrconfig.xml"),
							new File(conf_dir, "solrconfig.xml"));
					GBUtil.write(Configuration
							.getResourceAsString("conf/stopwords.txt"),
							new File(conf_dir, "stopwords.txt"));
					GBUtil.write(Configuration
							.getResourceAsString("conf/synonyms.txt"),
							new File(conf_dir, "synonyms.txt"));
				}
			File solr_template_file = new File(server_root, "solr_template.xml");
			File solr_config_template_file = new File(server_root,
					"solrconfig_template.xml");
			String solrconfig_template = Configuration
					.getResourceAsString("solrconfig_template.xml");
			String solr_template = Configuration
					.getResourceAsString("solr_template.xml");

			GBUtil.write(solr_template, solr_template_file);
			GBUtil.write(solrconfig_template, solr_config_template_file);

			return "Installation successful.  Please restart the server.";

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "Failed to install" + e.getLocalizedMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return "Failed to install" + e.getLocalizedMessage();
		} finally {
            IOUTILs.closeResource(bred);
            IOUTILs.closeResource(str);
        }
    }

	public static void build(String[] _args) {
		// {{ SEVERAL DIFFERENT INSTALL TYPES }}
		String type = _args[1];
		try {
			GBModule ins = Mod.getModule(GB.BUILD, type, null);
			if (ins == null)
				new GBModuleNotFoundException("Failed to find the module for "
						+ type);
			ArrayList<String> l = new ArrayList<String>();
			for (int i = 2; i < _args.length; i++) {
				l.add(_args[i]);
			}
			log.debug("Build Module loaded : " + ins.getModName());
			if (l.size() <= 0) {
				log.debug("No specific paths specified");
				// l.add("/gne");
				String[] roots = GB.getRoots();
				if (roots == null || roots.length <= 0)
					log.debug("Failed to find roots ");
				for (String p : roots) {
					l = new ArrayList<String>();
					l.add(p);
					ins.exec(l);
				}
			} else {
				log.debug("Indexing paths ");
				for (String p : l) {
					log.debug("\t" + p);
				}
				ins.exec(l);
			}
		} catch (UsageException e) {
			GB.print(e.getLocalizedMessage());
		} catch (GBModuleNotFoundException e) {
			e.printStackTrace();
		}
	}

}
