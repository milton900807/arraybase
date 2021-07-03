package com.arraybase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.arraybase.io.ABQFile;
import com.arraybase.lac.LAC;
import com.arraybase.modules.InsertFactory;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.WhereClause;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class GBInsertManager {

	private static String TYPE = "TYPE";
	private static String TYPES = "TYPES";

	private static GBLogger log = GBLogger.getLogger(GBInsertManager.class);

	public static void insert(String[] _args) {
		// java -jar gb.jar insert file.abq into /isis/test/mytesttable
		// where
		// refid=refseq_id
		if (_args.length < 2) {
			log.error("Insert is used to insert "
					+ "something into anther object.  "
					+ "In this case you have "
					+ "a lot of options.  "
					+ ""
					+ "To Insert into a AB object then you must provide a abquery file: *.abq");
			return;
		}

		if (_args[1].equalsIgnoreCase(TYPE) || _args[1].equalsIgnoreCase(TYPES)) {
			try {
				
				// insert type values into a file. 
				// this is a utiltity application
				
				GBIO.insertTypeValues(_args);
				File file = new File(_args[2]);
				if (!file.exists()) {
					GB.print("" + file.getAbsolutePath()
							+ " is not a valid file.  ");
					return;
				} else {
					GBIO.promptUserForFieldsAndTypes(file);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		String file_name = _args[1];
		ArrayList<String> vals = GB.parseArgs(_args);
		String path = null;
		boolean into_kw = false;
		for (String s : vals) {
			if (s.equalsIgnoreCase("into")) {
				into_kw = true;
			} else if (into_kw) {
				path = s;
				break;
			}
		}
		ArrayList<String> wherec = new ArrayList<String>();
		into_kw = false;
		// {{ parse all where values }}
		for (String s : vals) {
			if (s.equalsIgnoreCase("where")) {
				into_kw = true;
			}
			if (into_kw) {
				wherec.add(s);
			}
		}
		ArrayList<WhereClause> wcl = GBSearch.buildWhere(wherec);
		if (path == null) {
			log.error("Failed to find the path object in the command ");
			return;
		}
		/* ********************* */
		/* switch for load type */
		/* ********************* */
		log.debug("path : " + path);
		// =======
		try {
			String t = file_name.toLowerCase();
			if (t.startsWith("ab:/")) {
				log.error("Unimplemented protocol.. in development.");
			}
			Properties p = null;
			GBNodes nodes = GB.getNodes();
			TNode n = nodes.getNode(file_name);
			File f = new File(file_name);
			if (n != null && f.exists()) {
				log.error("The file exists as a local file and as an AB file.  "
						+ "You should specify which one explicitly by using one of the following: "
						+ "file://" + file_name + " or ab://" + file_name);
			} else if (n == null) {
				try {
					p = ABQFile.load(f);
				} catch (UsageException e) {
					e.printStackTrace();
					log.error("Faild to load the abq file. " + file_name);
					return;
				}
			} else {
				// load the property file
				p = new Properties();
				p.setProperty("query", "*:*");
				if (file_name.startsWith("/"))
					file_name = file_name.substring(1);
				p.setProperty("url", "ab://" + file_name);
				String target = LAC.getTarget(n.getLink());
				GB.print("Table : " + target);
				String exported = "";
				try {
					ArrayList<GColumn> cols = GB.getGBTables().describeCore(target, null);
					for (GColumn col : cols) {
						exported += col.getName() + ",";
					}
					if (exported.lastIndexOf(',') > 0)
						exported = exported.substring(0,
								exported.lastIndexOf(','));
				} catch (ConnectException e) {
					e.printStackTrace();
				}
				p.setProperty("export", exported);
			}

			String query = p.getProperty("query");
			String url = p.getProperty("url");
			String export = p.getProperty("export");
			String[] exported_values = export.split(",\\s*");

			String insert_module_type = getInsertModuleType(url);
			if (insert_module_type == null) {
				GB.printUsage("Please provide a valid connect url in the abq file.  It appears the url "
						+ url
						+ " is not correct, as I can't seem to find a module that will be able to load the data");
				return;
			}
			// QUERY IS NOT BEING SET AT THE MOMENT NEED TO DO THIS.
			GBModule ins = Mod.getModule(GB.INSERT, insert_module_type, p);
			HashMap<String, Object> param_map = new HashMap<String, Object>();
			param_map.put(GBModule.EXPORT, exported_values);
			param_map.put(GBModule.WHERE_CLAUSE, wcl);
			param_map.put(GBModule.PATH, path);
			param_map.put(GBModule.QUERY, query);
			try {
				ins.exec(param_map);
			} catch (UsageException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException _e) {
			_e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GBModuleNotFoundException e) {
			e.printStackTrace();
			log.debug(e.getLocalizedMessage());
			GB.print(e.getLocalizedMessage());
		}

	}

	private static String getInsertModuleType(String url) {
		String u = url.toLowerCase();
		if (u.startsWith("jdbc")) {
			return InsertFactory.RELATIONAL;
		} else if (u.startsWith("file"))
			return InsertFactory.FILE;
		else if (u.startsWith("ab:")) {
			return InsertFactory.INDEXED;
		}
		return null;
	}

}
