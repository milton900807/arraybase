package com.arraybase.admin;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.JDBC;
import com.arraybase.db.util.SourceType;
import com.arraybase.lac.LAC;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.IOUTILs;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The manager class will contain functions that are use for building search
 * indicies and replicating the database.
 * 
 * @author donaldm
 * 
 */
public class Manager {
	public final static Properties local = new Properties();
	static DBConnectionManager dbcm = new DBConnectionManager ();
	
	
	static {
		URL hbconfig = DBConnectionManager.class.getClassLoader().getResource(
				"pathManager.mysql.xml");
		String hibernateFile = ABProperties.readFileAsString(hbconfig);
		local.put("user", "tm");
		local.put("pass", "tm");
		local.put("url",
				"jdbc:mysql://localhost:3306/tmnodes?user=tm&password=tm");
		local.put("driver", "com.mysql.jdbc.Driver");
		local.put("hibernate", hibernateFile);
	}

	public final static Properties htl_admin = new Properties();
	static {
		URL hbconfig = DBConnectionManager.class.getClassLoader().getResource(
				"pathManager.oracle.xml");
		String oracleHibernate = ABProperties.readFileAsString(hbconfig);
		// local devel
		htl_admin.put("user", "htl_admin");
		htl_admin.put("pass", "htl_admin");
		htl_admin.put("url", "jdbc:oracle:thin:@biodev1:1521:biodev1");
		htl_admin.put("driver", "oracle.jdbc.driver.OracleDriver");
		htl_admin.put("hibernate", oracleHibernate);
	}

	public static void main(String[] _args) {
		// buildSearchIndicies("/gne/research/HumanDB/TDM4258G",
		// "http://localhost:9809");
		buildSearchIndicies("/gne/research/jeff", "http://localhost:9809");
	}

	private static void buildSearchIndicies(String path, String solrUrl) {
		NodeManager manager = new NodeManager();
		TNode node = manager.getNode(path);
		HttpSolrClient solr = null;
		try {
			solr = new HttpSolrClient.Builder(solrUrl).build();
			traverseTree(node, path, manager);
		} finally {
			IOUTILs.closeResource(solr);
		}
	}

	public String addPropertiesTable(Properties _p, String server) {
		try {
			Connection con = dbcm.getJDBCConnection();

			String ql = "insert into abproperties values (?, ?, ?, ?)";
			PreparedStatement pst = null;
			try {
				con.setAutoCommit(false);
				pst = con.prepareStatement(ql);
				Set<Object> keys = _p.keySet();
				int i = 1;
				for (Object key : keys) {
					String k = key.toString();
					String value = _p.getProperty(k);
					pst.setInt(1, i);
					pst.setString(2, server);
					pst.setString(3, k);
					pst.setString(4, value);
					pst.execute();
					i++;
				}
				con.commit();
				con.setAutoCommit(true);
				return "abproperties updated";
			} catch (Exception _e) {
				_e.printStackTrace();
			} finally {
				JDBC.closeStatement(pst);
				JDBC.closeConnection(con);
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return "abproperties failed to update.";

	}

	public String setPropertiesTable(Properties _p, String server) {
		try {
			Connection con =dbcm.getJDBCConnection();
			Statement st = con.createStatement();
			String sql = "delete from abproperties";
			st.execute(sql);
			String ql = "insert into abproperties values (?, ?, ?, ?)";
			PreparedStatement pst = null;
			try {
				con.setAutoCommit(false);
				pst = con.prepareStatement(ql);
				Set<Object> keys = _p.keySet();
				int i = 1;
				for (Object key : keys) {
					String k = key.toString();
					String value = _p.getProperty(k);
					pst.setInt(1, i);
					pst.setString(2, server);
					pst.setString(3, k);
					pst.setString(4, value);
					pst.execute();
					i++;
				}
				con.commit();
				con.setAutoCommit(true);
				return "abproperties updated";
			} catch (Exception _e) {
				_e.printStackTrace();
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return "abproperties failed to update.";
	}

	/**
	 * Construct the properties table
	 * 
	 * @return
	 */
	public String buildPropertiesTable() {
		try {
			String sql = "create table abproperties (id int, server varchar(2000), prop varchar(255), prop_value varchar(2000));";
			Connection con = dbcm.getJDBCConnection();
            Statement st = null;
            try {
                st = con.createStatement();
                st.execute(sql);
                con.commit();
            } finally {
                JDBC.closeStatement(st);
                JDBC.closeConnection(con);
            }
            return "Properties table created.. not populated";
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return "Failed to create the properties file";
	}

	/**
	 * Traverse the and find files to index.
	 * 
	 * @param node
	 * @param _path
	 * @param manager
	 */
	private static void traverseTree(TNode node, String _path,
			NodeManager manager) {
		GBFileManagerIndexer indexer = new GBFileManagerIndexer();
		if (node.getNodeType() == null) {
			// System.out.println("null node type ");
		} else if (node.getNodeType().equalsIgnoreCase(
				SourceType.RAW_FILE.getName())) {
			if (node.getLink() != null) {
				String[] lac = LAC.parse(node.getLink());
				if (lac[0].equalsIgnoreCase("file")) {
					indexer.buildSolrDoc(lac[2], "doc", null);
				}
			}

		} else if (node.getNodeType().equalsIgnoreCase(
				SourceType.NODE.getName())) {
			List<Integer> refs = node.getReference();
			for (int i : refs) {
				TNode n = manager.load(i);
				if (n != null) {
					String newpath = _path + "/" + n.getName();
					traverseTree(n, newpath, manager);
				}
			}
		}
	}

	public static void replaceNode ( String path ) {
		System.out.println( " sol root "  + ABProperties.get ( "solrRoot"));
	}

}
