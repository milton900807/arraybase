package com.arraybase.flare;

import com.arraybase.GB;
import com.arraybase.GBLinkManager;
import com.arraybase.GBSearch;
import com.arraybase.SearchConfig;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.JDBC;
import com.arraybase.io.ABQFile;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.schema.TrieDateField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;

// for reference these are the types and their values. 
//public static final int	ARRAY	2003
//public static final int	BIGINT	-5
//public static final int	BINARY	-2
//public static final int	BIT	-7
//public static final int	BLOB	2004
//public static final int	BOOLEAN	16
//public static final int	CHAR	1
//public static final int	CLOB	2005
//public static final int	DATALINK	70
//public static final int	DATE	91
//public static final int	DECIMAL	3
//public static final int	DISTINCT	2001
//public static final int	DOUBLE	8
//public static final int	FLOAT	6
//public static final int	INTEGER	4
//public static final int	JAVA_OBJECT	2000
//public static final int	LONGVARBINARY	-4
//public static final int	LONGVARCHAR	-1
//public static final int	NULL	0
//public static final int	NUMERIC	2
//public static final int	OTHER	1111
//public static final int	REAL	7
//public static final int	REF	2006
//public static final int	SMALLINT	5
//public static final int	STRUCT	2002
//public static final int	TIME	92
//public static final int	TIMESTAMP	93
//public static final int	TINYINT	-6
//public static final int	VARBINARY	-3
//public static final int	VARCHAR	12

/**
 * This is the same as the sqltosolr but it adds a where clause that determines
 * the update set
 * 
 * @author milton
 * 
 */
public class SQLToSolrUpdater {

	private GBLogger log = GBLogger.getLogger(SQLToSolrUpdater.class);
	private DBConnectionManager dbcm = new DBConnectionManager();

	public String run(String _user, String _core, String _description,
			Map<String, String> _query_config,
			Map<String, Map<String, String>> _schema_config, String _query,
			String job_id, UpdateListener updateListener) throws DBProcessFailedException {

		String core = _core;
		String end_core_name = _core;
		String stat_msg = "Error: ";
		// we want to spin off a series of threads that will do the inserts:
		int thread_count = 1;
		for (int i = 0; i < thread_count; i++) {
			new JeffsUpdateIndexer(_user, core, "", _query_config,
					_schema_config, _query, i + 1, job_id, updateListener).start();
		}
		return stat_msg;
	}

	static class JeffsUpdateIndexer extends Thread {

		private String user = null;
		private String table_name = null;
		private String source_type = null;
		private Map<String, String> query_config = null;
		private Map<String, Map<String, String>> schema_config = null;
		private String query = null;
		int index = 0;
		String job_id = -1+"";
		private static GBLogger log = GBLogger
				.getLogger(JeffsUpdateIndexer.class);

		private UpdateListener updateListener = null;



		public JeffsUpdateIndexer(String _user, String _table_name,
				String _source_type, Map<String, String> _query_config,
				Map<String, Map<String, String>> _schema_config, String _query,
				int i, String _job_id, UpdateListener _updateListener) {
			user = _user;
			this.updateListener = _updateListener;
			table_name = _table_name;
			query_config = _query_config;
			query = adjustQuery(_table_name, _query_config, _query);
			schema_config = _schema_config;
			index = i;
			job_id = _job_id;
		}

		public void run() {
			try {
				GB.print("Indexer started.");
				runIndex();
				GB.print("Indexer complete.");
			} catch (Exception _e) {
				_e.printStackTrace();
			}
		}

		public String runIndex() throws DBProcessFailedException {
			Connection conn = null;
			Statement st = null;
			ResultSet res = null;

			try {
				Calendar calendar = Calendar.getInstance();
				java.util.Date start_date = calendar.getTime();
				System.out.println(new Timestamp(start_date.getTime()));
				InMemoryJobManager.log(job_id, "Index start time : "
						+ new Timestamp(start_date.getTime()));
				String stat_msg = "Error";
				
				// {{ DBCONNECT }}
				// we need to capture connection problems here.
				conn = createConnection(query_config, ""+job_id);
				st = conn.createStatement();
				if (conn != null) {
					InMemoryJobManager.log(job_id,
							"Database connection was successful");
				}
				
				
				
				int increment = 200;
				int start = 0;
				boolean hasmore = true;
				int total_count = 0;
				int MAX_COUNT = Integer.MAX_VALUE;
				int column_count = 0;
				// we expec to find the destination scope in the top-level
				// _query_config
				String solr_url = getDefaultSolrURL();
				// String solr_dir = getDefaultSolrDir();
				if (solr_url == null) {
					InMemoryJobManager
							.log(job_id,
									"Failed... the web app is incorrectly configured (.properties)");
					throw new DBProcessFailedException(stat_msg,
							"Please provide a solr url in the properties: e.g. solr.url");
				}
				if (table_name == null || table_name.length() <= 0) {
					InMemoryJobManager
							.log(job_id,
									"Failed... the web app is incorrectly configured (.properties)");
					throw new DBProcessFailedException(
							stat_msg,
							"Table name was not defined.  Try putting a $table= variable in the \"new table script\" field.");
				}

				if (!solr_url.endsWith("/")) {
					solr_url += "/";
				}
				
				
				// {{ SET UP SOLR UPDATE }}
				// get the connection to the solr site.
				ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
				int end = start + 200;

				HttpSolrClient solr = null;
				try {
					solr = new HttpSolrClient.Builder(solr_url + table_name).build();
					String end_value = query_config.get(ABQFile.END);
					if (end_value == null) {
						end_value = "1000000000";
						InMemoryJobManager.log(job_id,
								"Max values was not found; MAX COUNT IS SET TO  "
										+ end_value);
					}
					end = Integer.parseInt(end_value);

					String start_index = query_config.get(ABQFile.START);
					if (start_index == null)
						start_index = 0 + "";
					String increment_ = query_config.get(ABQFile.INCREMENT);
					if (increment_ == null)
						increment_ = 1000 + "";
					start = Integer.parseInt(start_index);
					increment = Integer.parseInt(increment_);

				} catch (Exception _e) {
					log.info("We are assuming this particular database sql call doese not require the end, start and increment parameters");
					_e.printStackTrace();
					InMemoryJobManager.log(job_id,
							"Error" + _e.getLocalizedMessage());
				} finally {
					IOUTILs.closeResource(solr);
				}


				MAX_COUNT = end - start;
				String url = query_config.get(ABQFile.URL);
				url = url.toLowerCase();
				int run_index = 0;

				// VALIDATE
				try {
					if ((start + increment) > end) {
						increment = end - start;
						InMemoryJobManager
								.log(job_id,
										"The (start+increment) value is greater than the end value; increment is reset");
					}
				} catch (Exception _e) {
					_e.printStackTrace();
					InMemoryJobManager.log(job_id,
							"Failed... the web app is incorrectly configured ("
									+ _e.getMessage() + ")");
					System.err
							.println(" failed to parse the increment parameters from "
									+ "the config map"
									+ ".  This is required if "
									+ "you are going to declare start and increment variables "
									+ " in the query ");
					throw new DBProcessFailedException(stat_msg,
							"Failed to query. You need to define query init values.");
				}

				String exportlist = query_config.get(ABQFile.EXPORT);
				String[] exportFields = parseExportFields(exportlist);

				int i_start = (run_index * start);
				int i_end = (run_index * start) + increment;
				InMemoryJobManager.log(job_id, " MAX COUNT : " + MAX_COUNT);
				GB.print("Indexer total_count : " + total_count);

				// the first thing we will do is get the meta data from
				// the database and then be able to determine the types
				// here is where we build the solr configuration based on
				// query fields.
				LinkedHashMap<String, Integer> fields = new LinkedHashMap<String, Integer>();

				while (total_count < MAX_COUNT && hasmore) {
					// {{ CONSTRUCT THE QUERY }}
					String q = query;

					// the increment for the select is done differently for
					if (url.startsWith("jdbc:mysql")) {
						i_start = (run_index * increment) + start;
						i_end = increment;
						if (q.contains("$start")) {
							q = query.replace("$start", "" + i_start);
							q = q.replace("$increment", "" + i_end);
						} else {
							q = q + " limit " + i_start + ", " + i_end;
						}
					} else {

						i_start = (run_index * increment) + start;
						i_end = i_start + increment;
						q = buildOracleQuery(q, i_start, increment);
					}

					
					
					
					
					
					
					log.info("q: " + q);
					InMemoryJobManager.log(job_id, "Query executed: " + q);
					long secs = new Date().getTime();
					GBLogger.status("Searching...[" + i_start + "]");
					GBLogger.status("\t" + q + "\n");
					res = st.executeQuery(q);
					
					long fsecs = new Date().getTime();
					long desc = fsecs - secs;
					String times = "" + desc * 0.001;

					

					// {{ INITIAL GET THE METADATA }}
					if (total_count == 0) {
						ResultSetMetaData rm = res.getMetaData();
						column_count = rm.getColumnCount();
						// if the new tablename is still null the let's try to
						// derive it.
						if (table_name == null)
							table_name = rm.getTableName(1);
						for (int i = 0; i < column_count; i++) {
							int type = rm.getColumnType((1 + i));
							String type_name = rm.getColumnTypeName(i + 1);
							String field_name = rm.getColumnName(i + 1);
							log.config(type_name + " : " + field_name + " ");
							HashMap<String, String> field_properties = buildFieldProperties(
									type, type_name, field_name);
							// {{ HERE IS WHERE WE OVERRIDE THE DEFAULT CONFIG
							// BY USER-SUPPLIED VALUES}}
							if (schema_config != null) {
								Set<String> _schema_keys = schema_config
										.keySet();
								for (String _schema_key : _schema_keys) {
									if (_schema_key
											.equalsIgnoreCase(field_name)) {
										Map<String, String> _schema_value = schema_config
												.get(_schema_key);
										Set<String> _schema_value_keys = _schema_value
												.keySet();
										for (String _schema_value_key : _schema_value_keys) {
											String _schema_value_value = _schema_value
													.get(_schema_value_key);
											field_properties.put(
													_schema_value_key,
													_schema_value_value);
										}
									}
								}
							}
							// BUILD THE LIST WITH ONLY THE EXPORTED FIELDS --IF
							// THERE ARE ANY
							if (exportFields != null) {
								if (inlist(exportFields, field_name)) {
									fields.put(field_name, type);
								}
							} else {
								fields.put(field_name, type);
							}
						}
					}
					
					int count = 0;
					while (res.next()) {
						SolrInputDocument sid = new SolrInputDocument();
						// now loop over the expected results defined in the ru
						Set<String> schema_fields = fields.keySet();
						int hasValues = 0;
						for (String field_name : schema_fields) {
							//log.debug(field_name + "  ");
							int type = fields.get(field_name);
							//log.config(field_name + " -> type : " + type);
							hasValues += updateStatement(field_name, type, res,
									sid);
							index++;
						}
						if (hasValues > 0) {
							sid.addField("TMID", TMID.create());
							sid.addField("TMID_lastUpdated", new Date());
							docs.add(sid);
							count++;
						}
					}
					
					

					total_count += count;
					GBLogger.status("\tHits: " + count + "");

					InMemoryJobManager.log(job_id, "Hits: " + count);
					InMemoryJobManager.log(job_id, "Total Indexed: "
							+ total_count);
					log.config("\tTotal Hits: " + total_count);
					if ((count+1) < increment || count == 0) {
						hasmore = false;
					}

					if (docs != null && docs.size() > 0) {
						solr.add(docs);
						InMemoryJobManager
								.log(job_id, "Committing to index...");
						solr.commit();
						InMemoryJobManager.log(job_id, "Commit complete for "
								+ docs.size() + " documents.");
					}
					GB.print("Increment committing:\t: " + docs.size());
					GB.print("Total committed:\t: " + total_count);
					docs.clear();
					run_index++;
					InMemoryJobManager.log(job_id, "-----------------------");
				}
				InMemoryJobManager.log(job_id,
						"Job Complete. Cleaning resources... ");
				try {
					Runtime.getRuntime().gc();
					Thread.sleep(1000);
				} catch (Exception _e) {
					_e.printStackTrace();
				}

				// }
			} catch (SQLException e) {
				e.printStackTrace();
				InMemoryJobManager.log(job_id, "SQL:" + e.getMessage());
				return "SQL Failed:" + e.getMessage().toString();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				InMemoryJobManager.log(job_id, "URL:" + e.getMessage());
			} catch (SolrServerException e) {
				e.printStackTrace();
				InMemoryJobManager.log(job_id, "SOLR:" + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				InMemoryJobManager.log(job_id, "IO:" + e.getMessage());
				String s = new String("");
				PrintStream ss = null;
				try {
					ss = new PrintStream(s);
					e.printStackTrace(ss);
					InMemoryJobManager.log(job_id, "<p>Stack:</p>" + s);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				if (ss != null)
					ss.close();

			} catch (Exception e) {
				InMemoryJobManager.log(job_id, "IO:" + e.getMessage());
				e.printStackTrace();
				String s = new String("error");
				PrintStream ss = null;
				try {
					ss = new PrintStream(s);
					e.printStackTrace(ss);
					InMemoryJobManager.log(job_id, "<p>Stack:</p>" + s);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				if (ss != null)
					ss.close();

			} finally {
				InMemoryJobManager.kill();
				JDBC.closeResultSet(res);
				JDBC.closeStatement(st);
				JDBC.closeConnection(conn);
			}
			closeJob();

			if ( updateListener != null )
				updateListener.updateComplete("Complete");

			return "Load complete for SQLToSolrLoader... ";
		}

		
		
		/**
		 *  This will process the scriptlet that determines the correct where clause.
		 * This currently supports ORACLE only.. .need to make
		 * mysql format.
		 * @return
		 */
		

		private void closeJob() {
			// new JobCloser().start();
		}


		public String getDefaultSolrURL() {
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			return solr_url;
		}

		/**
		 * Configure the solr with the name and the fields (_params) using the
		 * URL and
		 * 
		 * @param _name
		 * @param _params
		 * @param _url
		 * @return
		 */
		private static String configureSolr(String _name,
				HashMap<String, Map<String, String>> _params, String _url) {
			log.config("Solr schema: ");

			Set<String> schema = _params.keySet();
			for (String sc : schema) {
				log.config("\t\t" + sc);
			}

			// {{ WE NEED TO ADD THE DEFAULT PRIMARY KEY COLUMN }}
			UUID idOne = UUID.randomUUID();
			HashMap<String, String> uuidp = new HashMap<String, String>();
			uuidp.put("fieldName", "TMID");
			uuidp.put("sortable", "true");
			uuidp.put("indexed", "true");
			uuidp.put("defaultString", idOne.toString());
			uuidp.put("dataType", "string");
			uuidp.put("requiredField", "true");
			_params.put("TMID", uuidp);
			Date dd = new Date();
			HashMap<String, String> last_updated = new HashMap<String, String>();
			last_updated.put("fieldName", "TMID_lastUpdated");
			last_updated.put("sortable", "true");
			last_updated.put("indexed", "true");
			last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
			last_updated.put("dataType", "date");
			last_updated.put("requiredField", "true");
			_params.put("TMID_lastUpdated", last_updated);
			log.info("Generating the solr schema... ");

			String solrSite = ABProperties.get("solrSite");
			return TMSolrServer.createSchema(_url, solrSite, _name, _params,
					false);
		}

		/**
		 * Build the field properties for the solr instance given the sql type
		 * int and type name
		 * 
		 * @param type
		 * @param type_name
		 * @param field_name
		 * @return
		 */
		public static HashMap<String, String> buildFieldProperties(int type,
				String type_name, String field_name) {
			HashMap<String, String> params = new HashMap<String, String>();
			System.out.println(" type : " + type + " type_name " + type_name
					+ " field name : " + field_name);
			HashMap<String, String> props = new HashMap<String, String>();
			props.put("caseControl", "true");
			props.put("indexed", "true");
			props.put("stored", "true");
			props.put("multiValued", "false");
			props.put("dataType", getDataType(type_name));
			return props;

			// temp = kvp.get("caseControl");
			// caseSensitive = true;
			// temp = kvp.get("sortable");
			// sortable = false;
			// temp = kvp.get("indexed");
			// indexed = false;
			// temp = kvp.get("stored");
			// stored = false;
			// temp = kvp.get("multiValued");
			// multi = true;
			// temp = kvp.get("defaultSearchField");
			// temp = kvp.get("requiredField");
			// if (temp != null && temp.equalsIgnoreCase("true"))
			// requiredField = true;
			// temp = kvp.get("dataType");
			// if (temp != null) {
			// if (temp.equalsIgnoreCase("string")) {
			// if (caseSensitive)
			// fieldLine = fieldLine.replaceAll("`TYPE`", "text");
			// else
			// fieldLine = fieldLine.replaceAll("`TYPE`", "string");
			// } else if (temp.equalsIgnoreCase("integer")) {
			// if (sortable)
			// fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
			// else
			// fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
			// } else if (temp.equalsIgnoreCase("float")) {
			// if (sortable)
			// fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
			// else
			// fieldLine = fieldLine.replaceAll("`TYPE`", "float");
			// } else
			// fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default
			//
		}

		private static String getDataType(String type_name) {
			if (type_name.equalsIgnoreCase("varchar"))
				return "string";
			else if (type_name.equalsIgnoreCase("text"))
				return "text";
			else if (type_name.equalsIgnoreCase("integer"))
				return "integer";
			else if (type_name.equalsIgnoreCase("double"))
				return "sfloat";
			else if (type_name.equalsIgnoreCase("number"))
				return "sfloat";
			else if (type_name.equalsIgnoreCase("sint"))
				return type_name;
			else
				return type_name;
		}

		// fieldLine = fieldLine.replaceAll("`TYPE`", "text");
		// else
		// fieldLine = fieldLine.replaceAll("`TYPE`", "string");
		// } else if (temp.equalsIgnoreCase("integer")) {
		// if (sortable)
		// fieldLine = fieldLine.replaceAll("`TYPE`", "sint");
		// else
		// fieldLine = fieldLine.replaceAll("`TYPE`", "integer");
		// } else if (temp.equalsIgnoreCase("float")) {
		// if (sortable)
		// fieldLine = fieldLine.replaceAll("`TYPE`", "sfloat");
		// else
		// fieldLine = fieldLine.replaceAll("`TYPE`", "float");
		// } else
		// fieldLine = fieldLine.replaceAll("`TYPE`", "string"); // default

		private int updateStatement(String _field_name, int type,
				ResultSet res, SolrInputDocument sid) throws SQLException {

			if (type == Types.VARCHAR) {
				String gene = res.getString(_field_name);
				sid.addField(_field_name, gene);
			} else if (type == Types.INTEGER) {
				int int_value = res.getInt(_field_name);
				sid.addField(_field_name, int_value);
			} else if (type == Types.DATE) {
				Date d = res.getDate(_field_name);
				sid.addField(_field_name, d);
			} else if (type == Types.TIMESTAMP) {
				try {
					Object date = res.getObject(_field_name);
					if (date != null) {
						String str = date.toString();
						// System.out.println("__ ++" + str);
						sid.addField(_field_name, date);
					} else {
						String ds = res.getString(_field_name);
						if (ds != null) {
							Date datev = try_to_parse_date_field(ds);
							if (datev != null)
								sid.addField(_field_name, datev);
						}
					}
				} catch (Exception _e) {
					GB.print(" Failed to load the field : " + _field_name);
					_e.printStackTrace();
					return 0;
				}
			} else if (type == Types.TIME) {
				Date d = res.getTime(_field_name);
				sid.addField(_field_name, d);
			} else if (type == Types.NUMERIC) {
				Long l = res.getLong(_field_name);
				if (l == null)
					l = res.getLong(_field_name.toUpperCase());
				if (l == null)
					l = res.getLong(_field_name.toLowerCase());
				sid.addField(_field_name, l);
			} else {
				try {
					Object object = res.getObject(_field_name);
					if (object != null) {
						sid.addField(_field_name, object);
					} else {
						GB.print("Failed to add " + _field_name
								+ "\t with TYPE ID: " + type);

						return 0;
					}
				} catch (Exception _e) {
					GB.print(_field_name + " type = " + type
							+ " error getting index : " + index + 1);
					_e.printStackTrace();
				}
			}
			return 1;
		}
	}

	public static Connection createConnection(Map<String, String> scope_config,
			String job_id) throws SQLException {
		String url = scope_config.get(ABQFile.URL);
		String pass = scope_config.get(ABQFile.PASSWORD);
		String user = scope_config.get(ABQFile.USER);
		String driver = scope_config.get(ABQFile.DRIVER_CLASS);
		InMemoryJobManager.log(job_id, "DB Connection: connecting... " + url);
		return createConnection(url, pass, user, driver, job_id);
	}


	// replace --> "$"+ABQFile.CURRENT_DATE --> oracle date representation
	public static String dateFix(String where_clause) {
		Date current = new Date ();
		SimpleDateFormat simp = new SimpleDateFormat("YYYY-MM-DD");
		String dats = simp.format(current);
		String temp = where_clause.replace("$"+ABQFile.CURRENT_DATE, "TO_DATE('" + dats + "', 'YYYY-MM-DD')");
		return temp;
	}

	public static String deriveWhereClause(Map<String, String> query_config) {
		
		
		
		return null;
	}

	
	
	/**
	 *  This adds the where condition for searching based on the last primary key.
	 * @param _table_name
	 * @param _query_config
	 * @param _query
	 * @return
	 */
	public static String adjustQuery(String _table_name,
			Map<String, String> _query_config, String _query) {

		String pk = _query_config.get(ABQFile.PK);
		if (pk != null) {
			String last_pk = fetchLastPK(_table_name, pk);
			String q = _query + " where " + pk + " > " + last_pk;
			return q;
		}

		return _query;
	}

	private static String fetchLastPK(String _table_name, String pk) {

		String site = GB.getDefaultURL();
		if ( GBLinkManager.isFullyQualifiedURL( _table_name )){
			site = GBLinkManager.getSolrRoot ( _table_name );
			_table_name = GBLinkManager.getCoreLK ( _table_name );
		}

		GBSearch se = GB.getSearch();
		
		try {
			// we do this so we can match the actual case of the solr index.
			ArrayList<GColumn> core_files = GB.getGBTables().describeCore(_table_name, null);
			for ( GColumn c : core_files ){
				if ( c.getName().equalsIgnoreCase(pk))
					pk = c.getName(); 
			}
		} catch (ConnectException e) {
			e.printStackTrace();
		}
		String[] cols = { "" + pk };
		ABaseResults result = GBSearch.searchCore(site, _table_name, "*:*", 0, 10, ""
				+ pk + " desc", cols, new SearchConfig(SearchConfig.RAW_SEARCH));
		ArrayList<GRow> rows = result.getValues();
		GRow row = rows.get(0);
		HashMap data = row.getData();
		Object value = data.get(pk);
		if (value != null)
			return value.toString();
		else
			return ""+0;
	}

	public static Date try_to_parse_date_field(String ds) {
		SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd k:mm:ss");
		try {

			Date d = s1.parse(ds);
			return d;

		} catch (Exception _e) {
			_e.printStackTrace();
			System.out.println(" failed to parse date : " + ds);
		}

		return null;
	}

	public static boolean inlist(String[] exportFields, String field_name) {
		if (exportFields == null || exportFields.length <= 0)
			return true;
		else {
			field_name = field_name.trim();
			for (String s : exportFields) {
				if (s.equalsIgnoreCase(field_name))
					return true;
			}
		}
		return false;
	}

	public static String[] parseExportFields(String exportlist) {
		String[] sp = exportlist.split(",");
		if (sp != null) {
			int i = 0;
			for (String s : sp) {
				sp[i++] = s.trim();
			}
			return sp;
		}
		return null;
	}

	/**
	 * Method to encapsulate the various ways of making a jdbc connection given
	 * a url.
	 * 
	 * @param url
	 * @param pass
	 * @param user
	 * @param _driver
	 * @return
	 * @throws SQLException
	 */
	private static Connection createConnection(String url, String pass,
			String user, String _driver, String _job_id) throws SQLException {
		return JDBC.createConnection(url, pass, user, _driver, _job_id);
	}

	/**
	 * @param q
	 * @return
	 */
	public static String buildOracleQuery(String q, int start, int length) {
		q = q.trim();
		if (q.startsWith("select")) {
			q = q.replaceFirst("select", "select ROWNUM,");
		}
		String query = "select * from " + "(select a.*, ROWNUM r__ from " + ""
				+ "(" + q + ") a where ROWNUM <" + (start + length)
				+ ") where r__ >=" + start;
		return query;
	}

	public static <T, E> List<T> getKeysByValue(Map<T, E> map, E value) {
		List<T> keys = new ArrayList<T>();
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}


	/**
	 * This will query the database and convert the results to a map object
	 * 
	 * @return
	 */
	private LinkedHashMap<String, Object> query(Map<String, String> _db_config,
			LinkedHashMap<String, Integer> types, String sql_string) {
		Connection _connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			_connection = JDBC.createConnection(_db_config, 1);

			LinkedHashMap<String, Object> row_d = new LinkedHashMap<String, Object>();
			st = _connection.createStatement();
			if (sql_string.contains(";")) {
				log.info("\t " + sql_string);
				String[] pre_q = sql_string.split(";");
				for (int i = 0; i < pre_q.length - 1; i++) {
					st.execute(pre_q[i]);
				}
				sql_string = pre_q[pre_q.length - 1];
			}

			log.info("\t " + sql_string);
			rs = st.executeQuery(sql_string);
			// get the results
			while (rs.next()) {
				Set<String> fields = types.keySet();
				for (String field : fields) {
					int type = types.get(field);
					if (type == Types.VARCHAR) {
						String value = rs.getString(field);
						row_d.put(field, value);
					} else if (type == Types.INTEGER) {
						int int_value = rs.getInt(field);
						row_d.put(field, int_value);
					} else {
						String value = rs.getString(field);
						row_d.put(field, value);
					}
				}
			}
			return row_d;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(_connection);
		}

		return null;
	}

	private LinkedHashMap<String, String> parse(String where_clause) {
		String[] sp = where_clause.split("\\s+");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String s : sp) {

			log.debug(" s : " + s);
			if (s.contains("=")) {
				String[] v = s.split("=");
				map.put(v[0], v[1]);
			}
		}
		return map;
	}

	private String updateStatement(String _field_name, int type, ResultSet res,
			SolrInputDocument sid, int index) throws SQLException {

		if (type == Types.VARCHAR) {
			String gene = res.getString(index + 1);
			sid.setField(_field_name, gene);
			return gene;
		} else if (type == Types.INTEGER) {
			int int_value = res.getInt(index + 1);
			sid.setField(_field_name, int_value);
			return int_value + "";
		} else {
			String value = res.getString(index + 1);
			sid.setField(_field_name, value);
			return value;
		}
	}

	public static String setRange(String url, String q, int start, int increment) {
		if (url.startsWith("jdbc:mysql")) {
			if (q.contains("$start")) {
				q = q.replace("$start", "" + start);
				q = q.replace("$increment", "" + increment);
			} else {
				q = q + " limit " + start + ", " + increment;
			}
		} else {
			q = buildOracleQuery(q, start, increment);
		}
		return q;
	}

}
