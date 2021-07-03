package com.arraybase.modules;

import com.arraybase.*;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.DBProcessFailedException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.parse.TypeNotFoundException;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.io.ABQFile;
import com.arraybase.tm.*;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.*;

public class GBSolrDB implements GBModule {

	private static GBLogger log = GBLogger.getLogger(GBSolrDB.class);

	private Properties prop = new Properties();

	public GBSolrDB(Properties p) {
		this.prop = p;
	}

	public void exec(List<String> l) throws UsageException {
	}

	public void exec(Map<String, Object> l) throws UsageException {

		// {{ MAP VALUES }}
		String[] exported_values = (String[]) l.get(EXPORT);
		String path = (String) l.get(PATH);
		ArrayList<WhereClause> wherehm = (ArrayList<WhereClause>) l
				.get(WHERE_CLAUSE);
		String query = (String) l.get(QUERY);
		String user = (String) l.get(GBModule.USER);
		String url = prop.getProperty("url");

		if (url == null)
			url = prop.getProperty("URL");
		int path_start_index = url.indexOf("//");
		String abq_node = url.substring(path_start_index + 1);

		// url=ab://test/rpkm/experiment4

		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(path);
		TNode lnode = nodes.getNode(abq_node);

		if (lnode == null)
			throw new UsageException(" The path : " + abq_node
					+ " was not found.");
		if (lnode.getNodeType().equalsIgnoreCase(SourceType.DB.name)
				|| lnode.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)) {
		} else {
			throw new UsageException(" The path : " + abq_node
					+ " was found but is not the correct type.");
		}

		if (node == null)
			throw new UsageException(" The path : " + path + " was not found.");

		if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
				|| node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			Set<Object> keys = prop.keySet();
			// convert the type
			for (Object o : keys) {
				map.put(o.toString(), prop.getProperty(o.toString()));
			}
			String lac = node.getLink();
			String core = GBSolr.getCoreFromLAC(lac);
			// see if the columns are in this core.
			String solrSite = ABProperties.getSolrURL();
			try {
				ArrayList<String> new_cols = new ArrayList<String>();
				ArrayList<GColumn> cols = TMSolrServer.describeCore(solrSite,
						core);
				// see if the exported values from the db table are in the gb
				// core?
				for (String s : exported_values) {
					boolean found = false;
					for (GColumn col : cols) {
						if (s.equalsIgnoreCase(col.getName()))
							found = true;
					}
					if (!found) {
						new_cols.add(s);
					}
				}
				if (new_cols.size() <= 0) {
					log.info("\n\n\t NO NEW FIELDS WERE FOUND \n\n");
					for (String s : exported_values) {
						log.info("Exported field : " + s);
					}
					for (GColumn col : cols) {
						log.info("Current Field: " + col.getName());
					}
				}
				TableManager manager = new TableManager(
						GB.getConnectionManager());
				for (String v : new_cols) {
					manager.addColumn(core, v, "string");
				}
			} catch (ConnectException e) {
				e.printStackTrace();
			}
		} else
			throw new UsageException(
					" The path : "
							+ path
							+ " was found but it seems it is not a valid obuject for this type of action.  Please make sure this object is a table or a database type.");

		String left_lac = lnode.getLink();
		String right_lac = node.getLink();

		if (left_lac == null)
			throw new UsageException("Link is not defined for the node id= "
					+ lnode.getNode_id());

		if (right_lac == null)
			throw new UsageException("Link is not defined for the node id= "
					+ node.getNode_id());

		String left_core = GBSolr.getCoreFromLAC(left_lac);
		String right_core = GBSolr.getCoreFromLAC(right_lac);
		// url=ab://test/isis/rpkm/experiment4
		// export=Localization,Platform,RNA_Seq_expid,comments,date
		// query=*:*
		// increment=1000
		try {
			performInsert(left_core, "*:*", right_core, "*:*", wherehm,
					exported_values);
		} catch (ConnectException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This will
	 * 
	 * @param _abqfile
	 * @param _path
	 * @throws UsageException
	 * @throws IOException
	 */
	public void loadABQFile(String _abqfile, String _path)
			throws NodeExistsException, IOException, UsageException {

		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(_path);
		if (node != null) {
			throw new NodeExistsException(_path);
		}
		File abqfile = new File(_abqfile);
		Properties pr = ABQFile.load(abqfile);

		String query = pr.getProperty(ABQFile.QUERY);
		String url = pr.getProperty(ABQFile.URL);
		String export = pr.getProperty(ABQFile.EXPORT_FIELDS);
		String[] exported_values = export.split(ABQFile.EXPORT_FIELDS_DELIM);
		// this will create a build module from the url object..
		// this is different than the insert module
		GBModule insert_module_type = GBModuleBuildFactory.create(url, node);
		if (insert_module_type == null) {
			GB.printUsage("Please provide a valid connect url in the abq file.  It appears the url "
					+ url
					+ " is not correct, as I can't seem to find a module that will be able to load the data");
			return;
		}
		// QUERY IS NOT BEING SET AT THE MOMENT NEED TO DO THIS.
		HashMap<String, Object> param_map = new HashMap<String, Object>();
		param_map.put(GBModule.EXPORT, exported_values);
		param_map.put(GBModule.PATH, _path);
		param_map.put(GBModule.QUERY, query);
		insert_module_type.exec(param_map);
		
		
		
		
	}

	/**
	 * This will insert one table into another. The A --> B where A is preserved
	 * and B is modified to append any of A
	 * @throws ConnectException
	 * @throws DBProcessFailedException
	 */
	private String performInsert(String _core, String _core_query,
			String _into_core, String _intoQuery,
			ArrayList<WhereClause> _where, String[] _exported_values)
			throws ConnectException {
		String solr_url = ABProperties.get(ABProperties.SOLRSITE);
		if (!solr_url.endsWith("/")) {
			solr_url += "/";
		}
		int increment = 1000;
		int i_start = 0;
		boolean complete = false;

		// we need the left core col types so we can try to cast if we need to.
		ArrayList<GColumn> left_cols = TMSolrServer.describeCore(solr_url,
				_core);
		ArrayList<GColumn> right_cols = TMSolrServer.describeCore(solr_url,
				_into_core);

		PrintStream status_log = GBIO.createLogStream("GBSolrDB.Join.status");

		// {{ SEARCH THE RIGHT CORE
		GResults r = TMSolrServer.search(solr_url, _into_core, _intoQuery,
				i_start, increment, new SearchConfig(SearchConfig.RAW_SEARCH));
		int index = 0;
		status_log.println("Initial total hits: " + r.getTotalHits());
		status_log.println("Iteration count: " + r.getValues().size());
		HttpSolrClient solr = new HttpSolrClient.Builder(solr_url + _into_core).build();
		try {
			solr.setParser(new XMLResponseParser());
			while (!complete) {
				ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

				if (r == null)
					break;

				// {{ LOOP OVER THE RESULTS }}
				if (r != null && (r.getValues() != null)
						&& (r.getValues().size() > 0)) {

					if (r.getValues() == null)
						break;

					if (r.getValues().size() < increment) {
						complete = true;
					}

					ArrayList<GRow> rows = r.getValues();
					if (rows.size() < increment) {
						complete = true;
					}
					status_log.println("Current set: " + index + " --> "
							+ rows.size());
					for (GRow row : rows) {
						String sql_whereclause = "";
						index++;
						HashMap row_d = row.getData();

						Set<String> s = row_d.keySet();
						boolean found = false;

						for (String row_field : s) {
							if (row_field != null) {
								Object o_value = row_d.get(row_field);
								if (o_value == null)
									o_value = "";
								String value = o_value.toString();
								for (WhereClause w : _where) {
									String right = w.getRight();
									if (right.equalsIgnoreCase(row_field)) {

										sql_whereclause += w.getLeft()
												+ ""
												+ mapToLucene(w.getOperator())
												+ ""
												+ castValue(value, right,
														right_cols) + " ";
										found = true;
										if (w.getJoinExpression() != null) {
											sql_whereclause += w
													.getJoinExpression() + " ";
										}
									}
								}

								if (!found) {
									Object oval = row_d.get("TMID").toString();
									if (oval != null) {
										status_log
												.println("Record failed to find join: "
														+ index
														+ "TMID : "
														+ oval.toString());
									}
								}
							}
						}
						// {{ NOW QUERY FOR THE CRITERIA IN THIS ROW }}
						sql_whereclause = sql_whereclause.trim();
						String left_query = _core_query;
						if (sql_whereclause != null
								&& sql_whereclause.length() > 0)
							left_query += " AND " + sql_whereclause;

						status_log.println("\tQuery : " + left_query);
						// {{ SEARCH THE LEFT CORE
						GResults inserts = TMSolrServer.search(solr_url, _core,
								left_query, 0, 1, new SearchConfig(SearchConfig.RAW_SEARCH));// we are only updated one row
													// so we only need one row
													// returned. //no?

						// {{ WE HAVE A POSITIVE HIT }}
						if (inserts != null && inserts.getTotalHits() > 0) {
							SolrInputDocument doc = new SolrInputDocument();
							// add the left values
							ArrayList<GRow> irow = inserts.getValues();
							// for now we're only going to get the first
							// value
							GRow first = irow.get(0);
							HashMap data = first.getData();
							// Set<String> keys = (Set<String>) data.keySet();
							// keys = GBSearch.removeTrackingColumns ( keys );

							// the left (abq) exported values
							// the insert key has to be used
							// because I want to make sure the
							// destination key of the correct case.
							// OTHERWISE this will fail. sux
							for (String k : _exported_values) {
								boolean kfound = false;
								for (String i_insert_key : s) {
									if (i_insert_key.equalsIgnoreCase(k)) {
										doc.setField(i_insert_key, data.get(k));
										kfound = true;
									}
								}
								if (!kfound)// for now we have to put it to
											// lowercase!
									doc.setField(k.trim(), data.get(k));
							}
							// add the right values
							Set key2 = row_d.keySet();
							for (Object k : key2) {
								String kstring = (String) k;
								doc.setField(kstring, row_d.get(k));
							}
							docs.add(doc);
						} else {
							status_log.println(" No documents found for : "
									+ _core_query + " " + sql_whereclause);
							log.info("No documents found for : " + _core
									+ "with query : " + _core_query + " "
									+ sql_whereclause);
						}
					}
					try {

						if (docs.size() < increment) {
							log.debug(" size : " + docs.size());
						}
						if (docs.size() > 0)
							solr.add(docs);

					} catch (Exception _e) {
						_e.printStackTrace();
					}
					try {
						status_log.println("Committing " + docs.size()
								+ " documents. ");
						log.info("\n\n\n\t\t\t-----------------\n\t\t\tCommit... \n\n\n\n\n");
						solr.commit();
					} catch (SolrServerException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					docs.clear();
				}

				i_start = i_start + increment;
				status_log.println(" Next start : " + i_start);
				r = TMSolrServer.search(solr_url, _into_core, _intoQuery,
						i_start, increment, new SearchConfig(SearchConfig.RAW_SEARCH));
			}
		} catch (TypeNotFoundException _e) {
			_e.printStackTrace();
			_e.printMessage();
		} finally {
            IOUTILs.closeResource(solr);
		}
		return "Join complete";
	}

	private String castValue(String value, String _field,
			ArrayList<GColumn> cols) throws TypeNotFoundException {

		String type = null;
		for (GColumn c : cols) {
			if (c.getName().equalsIgnoreCase(_field))
				type = c.getType();
		}

		if (type == null)
			throw new TypeNotFoundException(_field, cols);

		if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("sint")
				|| type.equalsIgnoreCase("integer")) {

			if (value.startsWith("."))
				value = "0" + value;

			if (value.indexOf(".") > 0) {
				String[] nsp = value.split("\\.");
				String iv = nsp[0];
				String dv = nsp[1];
				Integer iiv = Integer.parseInt(iv);
				Integer ddv = Integer.parseInt(dv);
				if (ddv >= 5) {
					iiv++;
					return iiv + "";
				} else
					return iiv + "";

			}

			Integer i = Integer.parseInt(value);

			return i + "";
		} else if (type.equalsIgnoreCase("float")
				|| type.equalsIgnoreCase("sfloat")
				|| type.equalsIgnoreCase("double")) {
			Float i = Float.parseFloat(value);
			return i + "";
		}
		return value;
	}

	private String mapToLucene(String operator) {
		if (operator.equals("="))
			return ":";
		return operator;
	}

	public String getModName() {
		return null;
	}
}
