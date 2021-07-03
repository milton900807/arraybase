package com.arraybase.db;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.DBProcessFailedException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.io.ABQFile;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.*;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * This is a class that exports a solr index into a relational database. this
 * expects a mapping file: properties expected: String url =
 * ABProperties.get("url"); String pass = ABProperties.get("pass"); String user
 * = ABProperties.get("user"); String driver =mysql driver etc.
 * 
 * @author milton
 */
public class GBRelationalDB {
	public final static String TABLE_NAME = "table";
	private Properties prop = new Properties();
	private String tableName = null;
	private static GBLogger log = GBLogger.getLogger(GBRelationalDB.class);

	/*
	 * properties expected: String url = ABProperties.get("url"); String pass =
	 * ABProperties.get("pass"); String user = ABProperties.get("user"); String
	 * driver = ABProperties.get("driver");
	 */
	/**
	 * @param pr
	 */
	public GBRelationalDB(Properties pr) {
		setProperties(pr);
	}

	private void setProperties(Properties _p) {
		prop = _p;
		tableName = prop.getProperty(TABLE_NAME);
	}

	public void printResults(ABaseResults results, String[] _cols) {
		Map<String, String> map = getColumns(_cols);
		String preparedStatementVariables = "";
		for (int i = 0; i < map.size(); i++) {
			preparedStatementVariables += "?,";
		}
		if (preparedStatementVariables.endsWith(","))
			preparedStatementVariables = preparedStatementVariables.substring(
					0, preparedStatementVariables.length() - 1);
		String prep = "insert into " + tableName + " (" + getVariables(map)
				+ ") values ( " + preparedStatementVariables + ")";
		log.debug(" Prepared statement : " + prep);
		commitResults(results, map, prep);
	}

	private void commitResults(ABaseResults results, Map<String, String> map,
			String prep) {
		ArrayList<GRow> rows = results.getValues();
		ArrayList<GColumn> cols = results.getColumns();
		PreparedStatement pr = null;
		Connection connection = createConnection();
		try {
			pr = connection.prepareStatement(prep);
			connection.setAutoCommit(false);
			Set<String> _cols = map.keySet();
			for (GRow r : rows) {
				Map values = r.getData();
				int index = 1;
				for (String columnstr : _cols) {
					// preserve the order of the columns used.
					GColumn c = getColumn(columnstr, cols);
					if (c != null) {
						Object ov = values.get(c.getName());
						if (ov != null) {
							// no need to cast this as a string.. but I
							// need
							// to get this proofed.
							String mapped_col = map.get(columnstr);
							String v = ov.toString();
							setValue(index, mapped_col, c.getType(), v, pr);
						}
					}
				}
				try {
					pr.executeUpdate();
				} catch (Exception _e) {
					_e.printStackTrace();
					log.error("Had a problem with the data... ");
					// NEED TO ADD MORE INFORMATION HERE ,...I.E. IMPORT LOG
					// INFORMATION
				}
			}
			// commit it!
			connection.commit();
		} catch (SQLException _e) {
			_e.printStackTrace();
			log.error("Failed to commit the results! ");
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} finally {
			JDBC.closeStatement(pr);
			JDBC.closeConnection(connection);
		}
	}

	private GColumn getColumn(String string, ArrayList<GColumn> cols) {
		for (GColumn c : cols) {
			if (c.getName().equalsIgnoreCase(string))
				return c;
		}
		return null;
	}

	/**
	 * The type marshaller
	 * 
	 * @throws SQLException
	 */
	private void setValue(int _columnIndex, String _name, String type,
			String v, PreparedStatement pr) throws SQLException {

		if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("text")
				|| type.equalsIgnoreCase("s")) {
			pr.setString(_columnIndex, v);
		} else if (type.equalsIgnoreCase("int")
				|| type.equalsIgnoreCase("sint") || type.equalsIgnoreCase("i")) {
			Integer i = Integer.parseInt(v);
			pr.setInt(_columnIndex, i);
		} else if (type.equalsIgnoreCase("float")
				|| type.equalsIgnoreCase("sfloat")
				|| type.equalsIgnoreCase("f")) {
			Float i = Float.parseFloat(v);
			pr.setFloat(_columnIndex, i);

		} else if (type.equalsIgnoreCase("double")
				|| type.equalsIgnoreCase("sdouble")
				|| type.equalsIgnoreCase("s")) {
			Double i = Double.parseDouble(v);
			pr.setDouble(_columnIndex, i);
		} else {
			pr.setString(_columnIndex, v);
		}
	}

	/**
	 * Create a connection from the properties file.
	 * 
	 * @return
	 */
	private Connection createConnection() {
		String url = prop.getProperty("url");
		String pass = prop.getProperty("pass");
		String user = prop.getProperty("user");
		String driver = prop.getProperty("driver");
		if (driver == null)
			driver = "com.mysql.jdbc.Driver";
		try {
			Connection connection = JDBC.createConnection(url, pass, user,
					driver, ""+new Date().toString());
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}
	


	/**
	 * Get a line that contains the column values:
	 * 
	 * This will check the loaded properties (i.e. field mapping file) for the
	 * correct table mapping field
	 * 
	 * Returns a map of solr=rdb
	 * 
	 * 
	 * @param _cols
	 * @return
	 */
	private Map<String, String> getColumns(String[] _cols) {
		String tableName_t = tableName + ".";
		Set<String> keys = prop.stringPropertyNames();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String k : keys) {
			if (k.startsWith(tableName_t)) {
				int pointindex = k.indexOf('.');
				// this is the relational table field
				String field = k.substring(pointindex + 1);
				// this is the mapped value that is the in the solr index
				String mapped_value = prop.getProperty(k);
				map.put(mapped_value, field);
			}
		}
		return map;
	}

	/**
	 * Get a line that contains the column values:
	 * 
	 * This will check the loaded properties (i.e. field mapping file) for the
	 * correct table mapping field
	 * @return
	 */
	private String getVariables(Map<String, String> map) {
		String col = "";
		Set<String> solr_field = map.keySet();
		for (String c : solr_field) {
			col += "" + map.get(c) + ",";
		}
		if (col.endsWith(","))
			col = col.substring(0, col.length() - 1);
		return col;
	}

	public void insertDBQueryIntoGBNode(String _query,
			String[] exported_values, String path, ArrayList<WhereClause> where)
			throws NodeNotFoundException, NodeWrongTypeException,
			DBProcessFailedException {
		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(path);
		if (node == null)
			throw new NodeNotFoundException(path);
		if (node.getNodeType().equalsIgnoreCase(SourceType.DB.name)
				|| node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name)) {
			String user = prop.getProperty(ABQFile.USER);
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
						new_cols.add(s.toLowerCase());
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
				// {{ GO CREATE THEW NEW COLUMNS }}
				createNewCols(new_cols, core);
			} catch (ConnectException e) {
				e.printStackTrace();
			}
//			String s = new SQLToSolr().join(user, core, "*:*", _query, map,
//					where);
		} else
			throw new NodeWrongTypeException(path);
	}

	/**
	 * This will create a new table from an ABQ file.
	 * 
	 * @param _query
	 * @param exported_values
	 * @param path
	 * @param where
	 * @throws NodeExistsException
	 * @throws DBProcessFailedException
	 */
	public void createGBNodeFromDBQuery(String _query,
			String[] exported_values, String path, ArrayList<WhereClause> where)
			throws NodeExistsException, DBProcessFailedException {
		GBNodes nodes = GB.getNodes();
		String link = null;
		TNode node = nodes.getNode(path);
		if (node != null)
			throw new NodeExistsException(path);

		Map<String, String> map = new LinkedHashMap<String, String>();
		Set<Object> keys = prop.keySet();
		// convert the type
		for (Object o : keys) {
			map.put(o.toString(), prop.getProperty(o.toString()));
		}

		String user = prop.getProperty(ABQFile.USER);
		node = GBNodes.createNode(user, path, link, SourceType.DB);

	}

	/**
	 * default is to add the new column with the string type
	 * 
	 * @param new_cols
	 * @param core
	 */
	private void createNewCols(ArrayList<String> new_cols, String core) {
		TableManager manager = new TableManager(GB.getConnectionManager());
		for (String v : new_cols) {
			log.info("Creating new col " + v);
			manager.addColumn(core.toLowerCase(), v, "string");
		}
	}

	/**
	 * Query the types (metadata) for a particular query.
	 * 
	 * @param _connection
	 * @param db_query
	 * @return
	 */
	public static LinkedHashMap<String, Integer> getTypes(
			Connection _connection, String db_query) {
		LinkedHashMap<String, Integer> types = new LinkedHashMap<String, Integer>();
		Statement st = null;
		ResultSet res = null;
		String sql_string = db_query;
		try {
			st = _connection.createStatement();

			if (sql_string.contains(";")) {
				String[] pre_q = sql_string.split(";");
				for (int i = 0; i < pre_q.length - 1; i++) {
					st.execute(pre_q[i]);
				}
				sql_string = pre_q[pre_q.length - 1];
			}

			log.debug("Query Fields");
			res = st.executeQuery(sql_string);
			ResultSetMetaData rm = res.getMetaData();
			int column_count = rm.getColumnCount();
			for (int i = 0; i < column_count; i++) {
				int type = rm.getColumnType((1 + i));
				String type_name = rm.getColumnTypeName(i + 1);
				// String field_name = rm.getColumnName(i + 1); // this is not
				// correct as it will create a problem if select name as
				// gene_name is called.
				String field_name = rm.getColumnLabel(i + 1);
				log.debug(type_name + " " + field_name);
				types.put(field_name, type);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			JDBC.closeResultSet(res);
			JDBC.closeStatement(st);
		}
		return types;
	}

	public static void startMasterInstance() {
		GB.print ( "Failed to connect... attempting to start.");
		Process p;
		BufferedReader reader = null;
		try {
			p = Runtime.getRuntime().exec("sudo /etc/init.d/mysqld start");
			p.waitFor();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(reader);
		}
	}
}
