package com.arraybase.tm;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBUtil;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.JDBC;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.LoaderException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.io.ABQFile;
import com.arraybase.io.GBFileManager;
import com.arraybase.lac.*;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TMNodeLink;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.google.gson.Gson;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * NodeManager is an object that uses a combination of hibernate (for
 * simplicity) and jdbc (for speed) to build and manage a tree in a database.
 * 
 * @author donaldm
 */
public class NodeManager {

	private static SimpleDateFormat sf = new SimpleDateFormat(
			"yyyy.MM.dd G   HH:mm");
	private static GBLogger log = GBLogger.getLogger(NodeManager.class);
	private DBConnectionManager dbcm = new DBConnectionManager();

	/**
	 * Initialize the node manager with a destination database pointer
	 * 
	 * @param _dbcm
	 */
	public NodeManager(DBConnectionManager _dbcm) {
		dbcm = _dbcm;
	}

	public NodeManager() {
	}

	public static Map<String, String> getNodePropertyMap(long node_id) {
		Session session = HBConnect.getSession();
		try {
			session.beginTransaction();
			Criteria c = session.createCriteria(NodeProperty.class);
			c.add(Restrictions.eq("node_id", node_id));
			List list = c.list();
			if (list == null || list.size() <= 0) {
				return null;
			} else {
				LinkedHashMap<String, String> mapli = new LinkedHashMap<String, String>();
				for (Object l : list) {
					NodeProperty p = (NodeProperty) l;
					mapli.put(p.getName(), p.getProperty());
				}
				return mapli;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public static List<NodeProperty> getNodeProperties(long node_id) {
		Session session = HBConnect.getSession();
		try {
			session.beginTransaction();
			Criteria c = session.createCriteria(NodeProperty.class);
			c.add(Restrictions.eq("node_id", node_id));
			List list = c.list();
			if (list == null || list.size() <= 0) {
				return null;
			} else {
				ArrayList<NodeProperty> nl = new ArrayList<NodeProperty>();
				for (Object l : list) {
					NodeProperty p = (NodeProperty) l;
					nl.add(p);
				}
				return nl;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;

	}

	// ArrayList<NodeFacet> node_property =
	// NodeManager.getNodeFacets(node.getNode_id());

	public static List<NodeFacet> getNodeFacets(long node_id) {
		Session session = HBConnect.getSession();
		try {
			session.beginTransaction();
			Criteria c = session.createCriteria(NodeProperty.class);
			c.add(Restrictions.eq("node_id", node_id));
			List list = c.list();
			if (list == null || list.size() <= 0) {
				return null;
			} else {
				// look for facet properties.
				ArrayList<NodeFacet> nl = new ArrayList<NodeFacet>();
				for (Object l : list) {
					NodeProperty p = (NodeProperty) l;
					if (p.getName().startsWith("facet.")) {
						String json = p.getProperty();
						NodeFacet facet = new NodeFacet(json);
						nl.add(facet);
					}
				}
				return nl;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	private static NodeProperty getNodeFacetProperty(Session session,
			long node_id, String facet) {
		Criteria c = session.createCriteria(NodeProperty.class);
		c.add(Restrictions.eq("node_id", node_id));
		List list = c.list();
		if (list == null || list.size() <= 0) {
			return null;
		} else {
			// look for facet properties.
			ArrayList<NodeFacet> nl = new ArrayList<NodeFacet>();
			for (Object l : list) {
				NodeProperty p = (NodeProperty) l;
				if (p.getName().equalsIgnoreCase("facet." + facet)) {
					return p;
				}
			}
		}
		return null;
	}

	public static void setFacet(long node_id, String _facet_name,
			List<String> facets) {
		Session session = HBConnect.getSession();
		try {
			session.beginTransaction();
			// {{ see if the facet already exists for this node }}
			NodeProperty f = getNodeFacetProperty(session, node_id, _facet_name);
			if (f != null) {
				NodeFacet facet = new NodeFacet();
				facet.setName(_facet_name);
				facet.setValues(facets);
				Gson g = new Gson();
				String json = g.toJson(facet);
				f.setProperty(json);
				session.update(f);
			} else {
				NodeProperty n = new NodeProperty();
				n.setName("facet." + _facet_name);
				NodeFacet facet = new NodeFacet();
				facet.setName(_facet_name);
				facet.setValues(facets);
				Gson g = new Gson();
				String json = g.toJson(facet);
				n.setProperty(json);
				n.setNode_id(node_id);
				session.save(n);
			}
			session.getTransaction().commit();

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
	}

	public static void deleteFacet(long node_id, String _facet_name) {
		Session session = HBConnect.getSession();
		try {
			session.beginTransaction();
			// {{ see if the facet already exists for this node }}
			NodeProperty f = getNodeFacetProperty(session, node_id, _facet_name);
			if (f != null) {
				session.delete(f);
			}
			session.getTransaction().commit();
			GB.print(f.getName() + "Removed");
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
	}

	/**
	 * @param _path
	 * @return
	 */
	public TNode getNode(String _path, Connection connection) {
		if (_path == null)
			return null;
		_path = _path.trim();
		if (_path.endsWith("/"))
			_path = _path.substring(0, _path.length() - 1);

		try {
			// select the node id from the path table.. as this will allow us to
			// get the children
			String st = "select node_id from ab_path where path_name=\'"
					+ _path + "\'";
			if (connection == null)
				connection = dbcm.getJDBCConnection();
			Statement statement = connection.createStatement();
			// System.out.println(st);
			// get the node if for the given path object
			ResultSet rs = statement.executeQuery(st);
			if (rs.next()) {
				int node_id = rs.getInt(1);
				// System.out.println ( " path node id " + node_id );
				TNode node = getNode(node_id, connection);
				return node;
			} else {

				// alt & transient nodes are here.
				String _parent_path = GBPathUtils.getParent(_path);
				TNode parent = getNode(_parent_path, connection);
				if (parent != null) {
					if (parent.getNodeType() != null) {
						if (parent.getNodeType().equalsIgnoreCase(
								SourceType.DB.name)
								|| parent.getNodeType().equalsIgnoreCase(
										SourceType.TABLE.name)) {
							FieldNode field = new FieldNode(parent, _path);
							// String type = field.getNodeType();
							return field;
						}
					}
				}

				// if (!_path.endsWith("/"))
				// getNode(_path + "/", connection); // try to find the path
			}
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
		}
		return null;
	}



	private LinkedHashMap<String, TNode> cache = new LinkedHashMap<String, TNode> ();
	/**
	 * @param _path
	 * @return
	 */
	public TNode getNode(String _path) {
		TNode tnode = cache.get ( _path );
		if ( tnode != null )
		{
			return tnode;
		}

		if ( _path == null || _path.length () <= 0 )
			return null;
		_path = _path.trim();
		Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			// select the node id from the path table.. as this will allow us to
			// get the children
			String st = "select node_id from ab_path where path_name=\'"
					+ _path + "\'";

			connection = dbcm.createConnection();

			if (connection == null) {
				GB.print("Failed to get the connection from the connection manager : "
						+ dbcm.getConnectionName());
				return null;
			}
			statement = connection.createStatement();
			// get the node if for the given path object
			rs = statement.executeQuery(st);
			if (rs == null)
				return null;
			if (rs.next()) {
				int node_id = rs.getInt(1);
				// System.out.println ( " path node id " + node_id );
				TNode node = getNode(node_id, connection);
				if ( node != null )
					cache.put ( _path, node );
				return node;
			} else {
				if (!_path.endsWith("/")) {
					TNode node = getNode(_path + "/", connection); // try to
																	// find the
																	// path
					// otherwise.
					if (connection != null && (!connection.isClosed())) {
						connection.close();
					}

					if ( node != null )
						cache.put ( _path, node );


					return node;

				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	/**
	 * @param _path
	 * @return
	 */
	public List<TNode> getNodes(String _path) {
		Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			String p = _path.replace('*', '%');
			String st = "select node_id from ab_path where path_name like \'"
					+ p + "\'";

			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(st);
			ArrayList<Integer> nodeids = new ArrayList<Integer>();
			while (rs.next()) {
				int node_id = rs.getInt(1);
				nodeids.add(node_id);
			}
			return getNodes(nodeids);
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	/**
	 * @param _path
	 * @return
	 */
	public List<TNode> getRefNodes(String _path) {
		Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			String p = _path.replace('*', '%');
			String st = "select node_id, ref.reference from ab_path "
					+ "left outer join ab_node_ref ref on ref.n_to_r=node_id "
					+ "where path_name like \'" + p + "\'";

			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(st);
			ArrayList<Integer> nodeids = new ArrayList<Integer>();
			while (rs.next()) {
				int node_id = rs.getInt(2);
				nodeids.add(node_id);
			}
			return getNodes(nodeids);
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	/**
	 * Return all the nodes... Be aware that this doesn't lazy load. It could be
	 * a memory hog
	 * 
	 * @return
	 */
	public List<TNode> getAllNodes() {
		Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			String st = "select node_id from ab_path";
			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(st);
			ArrayList<Integer> nodeids = new ArrayList<Integer>();
			while (rs.next()) {
				int node_id = rs.getInt(1);
				nodeids.add(node_id);
			}
			return getNodes(nodeids);
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getPath(long _nodeid) {
		Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			String st = "select path_name from ab_path where node_id=\'"
					+ _nodeid + "\'";
			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			// System.out.println(st);
			// get the node if for the given path object
			rs = statement.executeQuery(st);
			if (rs.next()) {
				String path = rs.getString(1);
				if (path != null)
					return path;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	public TPath getTPath(long _nodeid) {
		Connection connection = dbcm.createConnection();
        Statement statement = null;
        ResultSet rs = null;
		try {
			String st = "select path_id, parent, path_description, group_name, path_name, node_id from ab_path where node_id=\'"
					+ _nodeid + "\'";
			statement = connection.createStatement();
			// System.out.println(st);
			// get the node if for the given path object
			rs = statement.executeQuery(st);
			if (rs.next()) {
				long pid = rs.getLong(1);
				long parentid = rs.getLong(2);
				String desc = rs.getString(3);
				String group = rs.getString(4);
				String name = rs.getString(5);
				long node_id = rs.getLong(6);
				TPath path = new TPath();
				path.setDescription(desc);
				path.setGroup_name(group);
				path.setName(name);
				path.setNode_id(node_id);
				path.setTMParent(parentid);
				path.setPath_id(pid);
				connection.close();
				return path;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	/**
	 * @param _path
	 * @return
	 */
	public TPath getPath(String _path) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			String st = "select path_id, parent, path_description, group_name, path_name, node_id from ab_path where path_name=\'"
					+ _path + "\'";
			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			// System.out.println(st);
			// get the node if for the given path object
			rs = statement.executeQuery(st);
			if (rs.next()) {
				long pid = rs.getLong(1);
				long parentid = rs.getLong(2);
				String desc = rs.getString(3);
				String group = rs.getString(4);
				String name = rs.getString(5);
				long node_id = rs.getLong(6);
				TPath path = new TPath();
				path.setDescription(desc);
				path.setGroup_name(group);
				if (name.endsWith("/")) {
					name = name.substring(0, name.length() - 1);
				}
				path.setName(name);
				path.setNode_id(node_id);
				path.setTMParent(parentid);
				path.setPath_id(pid);
				return path;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	public TNode getNode(long _node_id) {
		Connection con = dbcm.createConnection();
		try {
			TNode node = getNode(_node_id, con);
			con.close();
			return node;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeConnection(con);
		}
		return null;
	}

	public List<TNode> getNodes(List<Integer> _ids) {
		Connection connection = dbcm.getJDBCConnection();
        Statement st = null;
        ResultSet rs = null;
		try {
			// return an empty array if there are no ids
			if (_ids == null || _ids.size() <= 0)
				return new ArrayList<TNode>();

			ArrayList<TNode> l = new ArrayList<TNode>();
			String ws = "where ";
			for (int i : _ids) {
				ws += "node_id=" + i + " or ";
			}
			ws = ws.substring(0, ws.length() - 4);
			String where_clause = ws.trim();

			st = connection.createStatement();
			String q = "select createdDate, created_by, node_description, lastEditedDate, "
					+ "node_link, node_name, node_type, node_value, "
					+ "node_owner, node_id,"
					+ "synonyms, ref.reference from ab_node "
					+ "left outer join ab_node_ref ref on ref.n_to_r=node_id "
					+ where_clause;
//			log.debug(q);
			rs = st.executeQuery(q);
			while (rs.next()) {
				TNode node = new TNode();
				Date created_date = rs.getDate(1);
				String created_by = rs.getString(2);
				String desc = rs.getString(3);
				Date last_edited = rs.getDate(4);
				String node_link = rs.getString(5);
				String node_name = rs.getString(6);
				String node_type = rs.getString(7);
				String node_owner = rs.getString(9);
				long node_id = rs.getLong(10);
				node.setNode_id(node_id);
				node.setCreated_by(created_by);
				node.setCreatedDate(created_date);
				node.setDescription(desc);
				node.setLastEditedDate(last_edited);
				node.setLink(node_link);
				node.setName(node_name);
				node.setNodeType(node_type);
				// node.setNode_value ( node_value );
				node.setOwner(node_owner);

				List<Integer> reference = new ArrayList<Integer>();
				int reference_l = rs.getInt(11);
				reference.add(reference_l);
				int ref = rs.getInt(12);
				reference.add(ref);

				while (rs.next()) {
					String node_name_ = rs.getString(6);
					if (node_name_.equals(node_name)) {
						int iref = rs.getInt(12);
						reference.add(iref);
					} else {
						buildNode(rs, l);
					}
				}
				node.setReference(reference);
				l.add(node);
			}
			return l;
		} catch (JDBCException _e) {
			_e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	private void buildNode(ResultSet rs, ArrayList<TNode> list)
			throws SQLException {
		if (rs == null)
			return;
		TNode node = new TNode();
		Date created_date = rs.getDate(1);
		String created_by = rs.getString(2);
		String desc = rs.getString(3);
		Date last_edited = rs.getDate(4);
		String node_link = rs.getString(5);
		String node_name = rs.getString(6);
		String node_type = rs.getString(7);
		String node_owner = rs.getString(9);
		long node_id = rs.getLong(10);
		node.setNode_id(node_id);
		node.setCreated_by(created_by);
		node.setCreatedDate(created_date);
		node.setDescription(desc);
		node.setLastEditedDate(last_edited);
		node.setLink(node_link);
		node.setName(node_name);
		node.setNodeType(node_type);
		// node.setNode_value ( node_value );
		node.setOwner(node_owner);
		List<Integer> reference = new ArrayList<Integer>();
		int reference_l = rs.getInt(11);
		reference.add(reference_l);
		int ref = rs.getInt(12);
		reference.add(ref);

		while (rs.next()) {
			String node_name_ = rs.getString(6);
			if (node_name_.equals(node_name)) {
				int iref = rs.getInt(12);
				reference.add(iref);
			} else {
				buildNode(rs, list);

			}
		}
		node.setReference(reference);
		list.add(node);
	}

	public TNode getNode(long _node_id, Connection _con) {
		try {
			Statement st = _con.createStatement();
			String q = "select createdDate, created_by, node_description, lastEditedDate, "
					+ "node_link, node_name, node_type, node_value, "
					+ "node_owner, node_id,"
					+ "synonyms, ref.reference from ab_node "
					+ "left outer join ab_node_ref ref on ref.n_to_r=node_id "
					+ "" + "where node_id=" + _node_id;

			ResultSet rs = st.executeQuery(q);
			// System.out.println(q);
			if (rs.next()) {
				// construct the node object.
				TNode node = new TNode();
				Date created_date = rs.getDate(1);
				String created_by = rs.getString(2);
				String desc = rs.getString(3);
				Date last_edited = rs.getDate(4);
				String node_link = rs.getString(5);
				String node_name = rs.getString(6);
				String node_type = rs.getString(7);
				String node_owner = rs.getString(9);
				long node_id = rs.getLong(10);
				node.setNode_id(node_id);
				node.setCreated_by(created_by);
				node.setCreatedDate(created_date);
				node.setDescription(desc);
				node.setLastEditedDate(last_edited);
				node.setLink(node_link);
				node.setName(node_name);
				node.setNodeType(node_type);

				// node.setNode_value ( node_value );
				node.setOwner(node_owner);
				List<Integer> reference = new ArrayList<Integer>();
				int reference_l = rs.getInt(11);
				reference.add(reference_l);
				int ref = rs.getInt(12);
				reference.add(ref);
				while (rs.next()) {
					ref = rs.getInt(12);
					reference.add(ref);
				}
				node.setReference(reference);
				return node;
			}

			// the following is a description of the ab_node table
			// node_id | bigint(20) | NO | PRI | NULL | auto_increment |
			// | createdDate | datetime | YES | | NULL | |
			// | created_by | varchar(255) | YES | | NULL | |
			// | node_description | longtext | YES | | NULL | |
			// | last_edited | datetime | YES | | NULL | |
			// | lastEditedDate | datetime | YES | | NULL | |
			// | node_link | longtext | YES | | NULL | |
			// | node_name | longtext | YES | | NULL | |
			// | node_type | longtext | YES | | NULL | |
			// | node_value | float | NO | | NULL | |
			// | node_owner | varchar(255) | YES | | NULL | |
			// | qualifier | longtext | YES | | NULL | |
			// | node_status | varchar(255) | YES | | NULL | |
			// | synonyms List l = c.list();
			return null;
		} catch (Exception _exception) {
			_exception.printStackTrace();
		} finally {
		}
		return null;
	}

	public TNode getNodeViaTreeStructure(String _path) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			TNode tm = getNode(_path, session);
			session.close();
			return tm;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	/**
	 * @deprecated use getNode ( String _path );
	 * @param _param
	 * @param _value
	 * @return
	 */
	private TNode getNode(String _param, String _value) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			TNode tm = getNode(_param, _value, session);
			session.close();
			log.debug("\n\n\n\n");
			return tm;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public TNode getNode(String _param, String _value, Session session) {
		try {
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq(_param, _value));
			List<TNode> lvalue = (List<TNode>) c.list();
			if (lvalue == null || lvalue.size() <= 0)
				return null;
			TNode value = lvalue.get(0);
			if (value == null)
				return null;
			else
				return value;

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {

		}
		return null;
	}

	public TNode getNode(String _path, Session session) {
		try {
			if (_path.startsWith("/"))
				_path = _path.substring(1);
			if (_path.endsWith("/"))
				_path = _path.substring(0, _path.length());

			String[] path = _path.split("/");
			log.debug("Accessing TMNode path : " + "");
			int index = 0;
			for (String s : path) {
				path[index++] = s.trim();
			}

			String root = path[0];
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("name", root));
			List<TNode> lvalue = (List<TNode>) c.list();
			if (lvalue == null || lvalue.size() <= 0)
				return null;
			TNode value = lvalue.get(0);
			if (value == null)
				return null;
			TNode cvalue = findRefNode(value, path, 1, session);
			if (cvalue == null)
				return null;
			// session.flush();
			// session.getSessionFactory().openSession();
			cvalue = HibernateToCoreJava.convert(cvalue);
			log.debug("\n\n\n\n");
			return cvalue;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
		}
		return null;
	}

	/**
	 * 
	 * @param _path
	 * @return
	 */

	public TNode createPath(String _path, String _userId, SourceType _leaf) {
		try {
			String path_name = _path;
			if (path_name.endsWith("/"))
				path_name = path_name.substring(0, path_name.length() - 1);
			if (!path_name.startsWith("/"))
				path_name = "/" + path_name;
			TNode cnode = getNode(_path);
			if (cnode != null) {
				System.err.print("Node : -[" + cnode.getNode_id()
						+ "]- was found.");
				return cnode;
			}
			if (_path.startsWith("/"))
				_path = _path.substring(1);
			if (_path.endsWith("/"))
				_path = _path.substring(0, _path.length());
			// if (_path.contains("/")) {
			// String[] path = _path.split("/");
			// log.debug("Accessing path : " + "");
			// int index = 0;
			// for (String s : path) {
			// path[index++] = s.trim();
			// }
			// }
			// String root = path[0];
			TNode value = mknode(_userId, _path, _leaf);
			return value;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public TNode createPath(String _path, String _userId, SourceType _leaf,
			Session _session) {
		try {

			String path_name = _path;
			if (path_name.endsWith("/"))
				path_name = path_name.substring(0, path_name.length() - 1);
			if (!path_name.startsWith("/"))
				path_name = "/" + path_name;

			TNode cnode = getNode(_path);
			if (cnode != null)
				return cnode;

			if (_path.startsWith("/"))
				_path = _path.substring(1);
			if (_path.endsWith("/"))
				_path = _path.substring(0, _path.length());

			String[] path = _path.split("/");
			log.debug("Accessing TMNode path : " + "");
			int index = 0;
			for (String s : path) {
				path[index++] = s.trim();
			}
			// String root = path[0];
			TNode value = mknode(_userId, _path, _session, _leaf);
			return value;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	/**
	 * @deprecated
	 * @param tpath
	 */
	public void save(TPath tpath) {
        Session s = null;
		try {
			s = dbcm.getSession();
			s.beginTransaction();
			// {{ make sure the name is not saved with a reserved char }}
			String t = tpath.getName();
			if (t.endsWith("/")) {
				t = t.substring(0, t.length() - 1);
			}
			tpath.setName(t);

			s.save(tpath);
			s.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(s);
		}
	}

	public void save(TPath tpath, Session s) {
		try {

			// {{ make sure the name is not saved with a reserved char }}
			String t = tpath.getName();
			if (t.endsWith("/")) {
				t = t.substring(0, t.length() - 1);
			}
			tpath.setName(t);

			s.save(tpath);
			s.flush();
			s.clear();
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	private TNode findOrMKNode(String _userid, TNode value, String[] path,
			int _index, Session s, SourceType _leaf) {
		if (_index >= path.length)
			return value;
		TNode node = findRefNode(value, path, _index, s);
		if (node == null) {
			return mktags(_userid, value, path, _index, s, _leaf);
			// return value;
		}

		List<TNode> l = getReferenceNodes(node);
		if (l == null || l.size() <= 0) {
			TNode sub_path = mktags(_userid, value, path, _index, s, _leaf);
			return sub_path;
		}
		for (TNode ll : l) {
			if (ll.getName().equalsIgnoreCase(path[_index])) {
				return findOrMKNode(_userid, value, path, _index + 1, s, _leaf);
			}
		}
		TNode sub_path = mktags(_userid, value, path, _index, s, _leaf);
		return findOrMKNode(_userid, value, path, _index + 1, s, _leaf);

	}

	public List<TNode> getReferenceNodes_dep(TNode node) {
		List<Integer> refs = node.getReference();
		Map<Integer, TNode> ref_nodes = load(refs);
		ArrayList<TNode> list = new ArrayList<TNode>();
		Collection<TNode> col = ref_nodes.values();
		for (TNode t : col) {
			list.add(t);
		}
		return list;
	}

	protected List<TNode> getReferenceNodes(TNode node, Session _s) {
		List<Integer> refs = node.getReference();
		Map<Integer, TNode> ref_nodes = load(refs, _s);
		ArrayList<TNode> list = new ArrayList<TNode>();
		Collection<TNode> col = ref_nodes.values();
		for (TNode t : col) {
			list.add(t);
		}
		return list;
	}

	private TNode mknode(String _user, String _path, SourceType _leaf) {
        Connection con = null;
        Session session = null;
		try {
			con = dbcm.getJDBCConnection();
			session = dbcm.getSession();
			session.beginTransaction();
			TNode no = mknode(_user, _path, con, session, _leaf);
			session.getTransaction().commit();
			return no;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeConnection(con);
            HBConnect.close(session);
        }
        return null;
	}

	// take a path and add if it doesn't exist.
	private TNode mknode(String _user, String _path, Session session,
			SourceType _leaf) {
		try {
			if (_path.startsWith("/"))
				_path = _path.substring(1);
			if (_path.endsWith("/"))
				_path = _path.substring(0, _path.length());

			String[] path = _path.split("/");
			int index = 0;
			for (String s : path) {
				path[index++] = s.trim();
			}
			String p = path[0];
			Criteria c = session.createCriteria(TNode.class);
			c.add(Restrictions.eq("name", p));
			// get the root.
			List<TNode> values = (List<TNode>) c.list();
			TNode root = null;
			if (values.size() <= 0) {
				root = new TNode(p);
			} else
				root = values.get(0);
			TNode back = mktags(_user, root, path, 1, session, _leaf);
			return back;

		} catch (Exception _e) {
			_e.printStackTrace();

		}
		return null;
	}

	// take a path and add if it doesn't exist.
	private TNode mknode(String _user, String _path, Connection con,
			Session _session, SourceType _leaf) {
        Statement st = null;
        ResultSet rs = null;
		try {
			if (_path.startsWith("/"))
				_path = _path.substring(1);
			if (_path.endsWith("/"))
				_path = _path.substring(0, _path.length());

			String[] path = _path.split("/");
			int index = 0;
			for (String s : path) {
				path[index++] = s.trim();
			}
			String root_name = path[0];

			// Criteria c = session.createCriteria(TNode.class);
			// c.add(Restrictions.eq("name", user));
			// // get the root.
			st = con.createStatement();
			// System.out.println (" rootname :  " + root_name );
			// String sql = "SELECT NODE_ID FROM AB_PATH WHERE PATH_NAME = '" +
			// root_name + "'";
			// ResultSet rs = st.executeQuery(sql);
			rs = st.executeQuery("select node_id from ab_path where path_name = \'/"
                    + root_name + "\'");
			TNode root = null;
			if (rs != null && rs.next()) {
				long node_id = rs.getLong(1);
				root = getNode(node_id, con);
			} else {
				root = NodeFactory.createRoot(_user, root_name, _session);
			}
			TNode back = mktags(_user, root, path, 1, con, _leaf, _session);
			return back;

		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
		}
		return null;
	}

	private TNode mktags(String _user, TNode parentNode, String[] path,
			int _index, Connection con, SourceType _leaf, Session session) {
		if (_index >= path.length)
			return parentNode;
		String ps = buildPath(path, _index);

		TPath _parent = getPath(ps, session);

		if (_parent == null) {
			System.out.println("We do not have a path object for this path : "
					+ ps);
		}
		List<Integer> refNodes = parentNode.getReference();
		Map<Long, String> names = getReferenceNames(refNodes, con);
		String _field = path[_index];
		boolean isThere = false;
		TNode current = null;
		Set<Long> keys = names.keySet();
		for (long key : keys) {
			String node_name = names.get(key);
			if (node_name != null && node_name.equalsIgnoreCase(_field)) {
				isThere = true;
				current = load(key, con);
			}
		}
		// {{ IF WE DO NOT HVAE THE NODE THEN WE NEED TO CREATE IT }}
		if (!isThere) {
			SourceType type = SourceType.NODE;
			if (_index == path.length - 1)
				type = _leaf;
			TNode ch = NodeFactory.createNode(ps, _user, _field, type, session);
			current = ch;
		} else {// we have found the node in the tnode path so now we need to
				// make sure we
				// have a corresponding ab_path object.
				// this has not been tested. we need to test this.
			NodeFactory.createOrUpdatePathObject(buildPath(path, _index),
					_user, _parent.getPath_id(), _parent.getNode_id(), session);
		}
		return mktags(_user, current, path, ++_index, con, _leaf, session);
	}

	// this will load the following table
	// +------------------+--------------+------+-----+---------+----------------+
	// | Field | Type | Null | Key | Default | Extra |
	// +------------------+--------------+------+-----+---------+----------------+
	// | node_id | bigint(20) | NO | PRI | NULL | auto_increment |
	// | createdDate | datetime | YES | | NULL | |
	// | created_by | varchar(255) | YES | | NULL | |
	// | node_description | longtext | YES | | NULL | |
	// | lastEditedDate | datetime | YES | | NULL | |
	// | node_link | longtext | YES | | NULL | |
	// | node_name | longtext | YES | | NULL | |
	// | node_type | longtext | YES | | NULL | |
	// | node_value | float | NO | | NULL | |
	// | node_owner | varchar(255) | YES | | NULL | |
	// | qualifier | longtext | YES | | NULL | |
	// | node_status | varchar(255) | YES | | NULL | |
	// | synonyms | longtext | YES | | NULL | |
	// +------------------+--------------+------+-----+---------+----------------+
	private TNode load(long key, Connection con) {

		try {
			TNode t = NodeFactory.createJDBCNode(key, con);
			return t;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	private Map<Long, String> getReferenceNames(List<Integer> refNodes,
			Connection con) {

        Statement st = null;
        ResultSet rs = null;
		LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>();
		// return an empty map if there are no nodes to load.
		if (refNodes == null || refNodes.size() <= 0)
			return map;
		try {

			String list = "";
			for (Integer i : refNodes) {
				list += i + ",";
			}
			list = list.substring(0, list.length() - 1);
			st = con.createStatement();
			String sql = "select node_id, node_name from ab_node where node_id in ( "
					+ list + ")";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				long id = rs.getLong(1);
				String name = rs.getString(2);
				map.put(id, name);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
        }
        return null;
	}

	private TPath getPath(String _path, Session session) {
		try {
			Criteria c = session.createCriteria(TPath.class).add(
					Restrictions.eq("name", _path));
			List l = c.list();
			if (l != null && l.size() > 0) {
				TPath p = (TPath) l.get(0);
				return p;
			}
		} catch (Exception _e) {
			_e.printStackTrace();

		}
		return null;

	}

	private String buildPath(String[] path, int _index) {
		String p = "";
		for (int i = 0; i < _index; i++)
			p += "/" + path[i];
		return p;
	}

	private List<TNode> getReferenceNodes(TNode node) {
		List<Integer> refs = node.getReference();
		Map<Integer, TNode> ref_nodes = load(refs);
		ArrayList<TNode> list = new ArrayList<TNode>();
		Collection<TNode> col = ref_nodes.values();
		for (TNode t : col) {
			list.add(t);
		}
		return list;
	}

	private TNode mktags(String _userid, TNode _r, String[] path, int _index,
			Session _session, SourceType _leaf) {
		if (_index >= path.length)
			return _r;
		TPath _parent = getPath(_r, _session);
		List<TNode> chs = getReferenceNodes(_r, _session);
		// List<TNode> chs = getReferenceNodes(_r);
		String _field = path[_index];
		boolean isThere = false;
		TNode current = null;
		for (TNode cht : chs) {
			if (cht.getName().equalsIgnoreCase(_field)) {
				isThere = true;
				current = cht;
			}
		}
		if (!isThere) {
			// create the node
			SourceType type = SourceType.NODE;
			if (_index == path.length - 1)
				type = _leaf;

			TNode ch = NodeFactory.createNode(_parent.getName(), _userid,
					_field, type, _session);
			_r.addCRef(ch);
			// chs.add(ch);
			// _r.setChildren(chs);
			_session.save(_r);

			TPath tpath = new TPath();
			tpath.setTMParent(_parent.getPath_id());
			tpath.setDescription("");
			tpath.setGroup_name(_parent.getGroup_name());
			tpath.setNode_id(ch.getNode_id());
			tpath.setName(_parent.getName() + "/" + ch.getName());
			save(tpath, _session);
			current = ch;
		}
		return mktags(_userid, current, path, ++_index, _session, _leaf);
	}

	/**
	 * Get the Path object for a given node. Session remains open.
	 * 
	 * @param _r
	 * @return
	 */
	private TPath getPath(TNode _r, Session _session) {
		try {
			// long node_id = _r.getNode_id();
			// System.out.println(" noe id : " + node_id);
			Criteria c = _session.createCriteria(TPath.class);
			c.add(Restrictions.eq("node_id", _r.getNode_id()));
			List l = c.list();
			if (l.size() > 0) {
				TPath p = (TPath) l.get(0);
				return p;
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	/**
	 * @deprecated
	 * @param _r
	 * @return
	 */
	private TPath getPath(TNode _r) {
        Session s = null;
		try {
			s = dbcm.getSession();
			s.beginTransaction();
			Criteria c = s.createCriteria(TPath.class);
			c.add(Restrictions.eq("node_id", _r.getNode_id()));
			List l = c.list();
			if (l.size() > 0) {
				TPath p = (TPath) l.get(0);
				return p;
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(s);
		}

		return null;
	}

	private TNode findRefNode(TNode value, String[] _path, int _index,
			Session _session) {
		if (_index >= _path.length)
			return value;

		List<Integer> l = value.getReference();
		if (l == null || l.size() <= 0)
			return null;

		Map<Integer, TNode> refs = load(l, _session);
		Collection<TNode> values = refs.values();
		for (TNode ll : values) {
			String name = ll.getName();
			if (name == null) {
				// TODO: add a fix for null nodes.
				// we need to fix this node.
			} else {
				if (ll.getName().equalsIgnoreCase(_path[_index]))
					return findRefNode(ll, _path, ++_index, _session);
			}
		}
		return null;
	}

	/**
	 * Add links to nodes
	 * 
	 * @param t
	 * @param links
	 * @return
	 */
	public String add(TNode t, List<TMNodeLink> links) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			long node_id = t.getNode_id();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.idEq(node_id));
			TNode cnode = (TNode) c.uniqueResult();
			for (TMNodeLink _l : links) {
				TNode ccnode = new TNode();
				ccnode.setCreated_by("");
				ccnode.setCreatedDate(new Date());
				ccnode.setNodeType(_l.getNodeLinkType());
				ccnode.setLastEditedDate(new Date());
				ccnode.setName(_l.getTitle());
				ccnode.setLink(_l.getCore_uri());
				session.save(ccnode);
				cnode.addCRef(ccnode);
			}
			session.getTransaction().commit();
			// cnode.setChildren(copy ( cnode.getChildren(), t.getChildren()));
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public String removeFrom(ArrayList<TNode> _tmnode_list, String _path) {
		TNode t = getNode(_path);
		List<TNode> ch = getReferenceNodes(t);
		for (TNode c : ch) {
			for (TNode tt : _tmnode_list) {
				if (tt.getNode_id() == c.getNode_id())
					ch.remove(c);
			}
		}
		return "Removed";
	}

	/**
	 * Copy the current node to the path
	 */
	public String copyTo(ArrayList<TNode> node_list, String _path) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			if (_path.startsWith("/"))
				_path = _path.substring(1);
			if (_path.endsWith("/"))
				_path = _path.substring(0, _path.length());
			String[] path = _path.split("/");
			log.debug("Accessing TMNode path : " + "");
			int index = 0;
			for (String s : path) {
				path[index++] = s.trim();
			}
			String root = path[0];
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("name", root));
			List<TNode> lvalue = (List<TNode>) c.list();
			if (lvalue == null || lvalue.size() <= 0)
				return null;
			TNode value = lvalue.get(0);
			if (value == null)
				return null;

			TNode cvalue = findRefNode(value, path, 1, session);
			if (cvalue == null)
				return null;

			for (TNode tt : node_list) {
				// TMNode t_copy = (TMNode) deepCopy(tt);
				// Criteria c2 =
				// session.createCriteria(TMNode.class).add(Restrictions.eq (
				// "node_id", tt.getNode_id()));
				// TMNode t_node = (TMNode)c2.uniqueResult();
				// cvalue.addChild(tt);
				cvalue.addCRef(tt);
			}
			// session.saveOrUpdate(cvalue);
			session.getTransaction().commit();

			log.debug("\n\n\n\n");
			return "";
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return "Saved";
	}

	private void testTMNodeReference() {

		TNode root = new TNode("root");
		TNode c1 = new TNode("c1");

		TNode c2 = new TNode("c2");
		try {
			save(c1);
			save(c2);
			root.addCRef(c1);
			root.addCRef(c2);
			save(root);
			List<Integer> ids = root.getReference();
			Map<Integer, TNode> children = load(ids);

			Set<Integer> keys = children.keySet();
			for (Integer ii : keys) {
				log.debug(" i:  " + ii + " === " + children.get(ii));
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	//
	// public void save(TMNode... _node) throws Exception {
	// try {
	// Session session = dbcm.getSession(HBType.TMNode);
	// session.beginTransaction();
	// session.save(_node);
	// session.getTransaction().commit();
	// } catch (Exception _e) {
	// _e.printStackTrace();
	// throw new Exception("Failed to save");
	// }finally {
	// dbcm.close(HBType.TMNode);
	// }
	//
	// }

	public TNode save(TNode _node) {
		return saveNode(_node);
	}

	/**
	 * Save a node object.. this does not query first... so it may not overwrite
	 * what is already there if that is the intended pupose.
	 * 
	 * @param _node
	 * @return
	 */
	private TNode saveNode(TNode _node) {
        Session session = null;
		try {
			session = dbcm.getSession();
			synchronized (session) {
				session.beginTransaction();
				Criteria c = session.createCriteria(TNode.class);
				c.add(Restrictions.eq("node_id", _node.getNode_id()));
				List l = c.list();
				if (l != null && l.size() > 0) {
					TNode savedNode = (TNode) l.get(0);
					savedNode.setCreated_by(_node.getCreated_by());
					savedNode.setCreatedDate(_node.getCreatedDate());
					savedNode.setDescription(_node.getDescription());
					savedNode.setLastEditedDate(_node.getLastEditedDate());
					savedNode.setLink(_node.getLink());
					savedNode.setName(_node.getName());
					savedNode.setNode_value(_node.getNode_value());
					savedNode.setNodeType(_node.getNodeType());
					savedNode.setOwner(_node.getOwner());
					savedNode.setQualifier(_node.getQualifier());
					savedNode.setReference(_node.getReference());
					savedNode.setStatus(_node.getStatus());
					savedNode.setSynonyms(_node.getSynonyms());
					savedNode.setUser(_node.getUser());

				} else {
					session.save(_node);
				}
				session.flush();
				session.getTransaction().commit();
				_node = HibernateToCoreJava.convert(_node);
			}
			return _node;
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
            HBConnect.close(session);
		}
		return null;
	}

	/**
	 * Save a node object.. this does not query first... so it may not overwrite
	 * what is already there if that is the intended pupose.
	 * 
	 * @param _node
	 * @return
	 */
	public TNode save(TNode _node, Session session) {
		try {

			session.saveOrUpdate(_node);
			session.flush();
			// _node = HibernateToCoreJava.convert(_node);
			// session.getTransaction().commit();
			return _node;
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
		}
		return null;
	}

	/**
	 * Save a node object.. this does not query first... so it may not overwrite
	 * what is already there if that is the intended pupose.
	 * 
	 * @param _node
	 * @return
	 */
	public TNode merge(TNode _node, Session session) {
		try {
			session.merge(_node);
			session.flush();
			return _node;
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
		}
		return null;
	}

	public void merge(TPath _node, Session session) {
		try {
			session.merge(_node);
			session.flush();
		} catch (Exception _e) {
			_e.printStackTrace();

		} finally {
		}
	}

	public static Object deepCopy(Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			Object object_ = new ObjectInputStream(bais).readObject();
			return object_;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Map<Integer, TNode> load(List<Integer> _node_ids) {
		Connection connection = null;
		try {
			connection = dbcm.createConnection();
			Map<Integer, TNode> values = load(_node_ids, connection);
			return values;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeConnection(connection);
		}
		return null;

	}

	public Map<Integer, TNode> load(List<Integer> _node_ids, Session session) {
		HashMap<Integer, TNode> default_map = new HashMap<Integer, TNode>();
		if (_node_ids == null || _node_ids.size() <= 0)
			return default_map;
		try {
			// String hql = "from TMNode node where ";
			// for (int i = 0; i < _node_ids.size(); i++) {
			// hql += " node.node_id=" + _node_ids.get(i);
			// if (i + 1 < _node_ids.size())
			// hql += " OR ";
			// }
			int index = 0;
			Long[] ll = new Long[_node_ids.size()];
			for (Integer i : _node_ids) {
				ll[index++] = new Long(i);
			}
			Criteria cc = session.createCriteria(TNode.class);
			cc.add(Restrictions.in("node_id", ll));
			List<TNode> lvalue = cc.list();
			// Query q = session.createQuery(hql);
			// List<TMNode> lvalue = q.list();
			// log.debug("Dalist " + lvalue);
			HashMap<Integer, TNode> vl = new HashMap<Integer, TNode>();
			for (int j = 0; j < lvalue.size(); j++) {
				TNode current = lvalue.get(j);
				current.getName();
				vl.put((int) current.getNode_id(), current);
				// HibernateToGWT.convert(current));
			}
			return vl;

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public Map<Integer, TNode> load(List<Integer> _node_ids,
			Connection _connection) {
		if (_node_ids == null || _node_ids.size() <= 0)
			return new HashMap<Integer, TNode>();
		try {
			LinkedHashMap<Integer, TNode> vl = new LinkedHashMap<Integer, TNode>();
			Statement st = _connection.createStatement();
			int index = 0;
			for (Integer i : _node_ids) {
				String sql = getTMNodeQuery() + " where n.node_id=" + i;
				ResultSet rs = st.executeQuery(sql);
				TNode n = getNode(rs);
				if (n != null)
					vl.put(i, n);
			}
			return vl;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	private TNode getNode(ResultSet rs) throws SQLException {

		// construct the node object.
		if (!rs.next())
			return null;
		TNode node = new TNode();
		long nodeid = rs.getLong(1);

		Date created_date = rs.getDate(2);
		String created_by = rs.getString(3);
		String desc = rs.getString(4);
		Date last_edited = rs.getDate(5);
		String node_link = rs.getString(6);
		String node_name = rs.getString(7);
		String node_type = rs.getString(8);
		String node_owner = rs.getString(10);
		node.setNode_id(nodeid);
		node.setCreated_by(created_by);
		node.setCreatedDate(created_date);
		node.setDescription(desc);
		node.setLastEditedDate(last_edited);
		node.setLink(node_link);
		node.setName(node_name);
		node.setNodeType(node_type);

		// node.setNode_value ( node_value );
		node.setOwner(node_owner);
		List<Integer> reference = new ArrayList<Integer>();
		int reference_l = rs.getInt(11);
		reference.add(reference_l);
		while (rs.next()) {
			int ref = rs.getInt(11);
			reference.add(ref);
		}
		node.setReference(reference);
		return node;
	}

	private String getTMNodeQuery() {
		String q = "select n.node_id, createdDate, created_by, node_description, lastEditedDate, "
				+ "node_link, node_name, node_type, node_value, "
				+ "node_owner, "
				+ "synonyms, ref.reference from ab_node n "
				+ "left join ab_node_ref ref on ref.n_to_r=n.node_id ";
		return q;
	}

	private String buildNodeIds(List<Integer> _node_ids) {

		String g = "";
		for (int i : _node_ids) {
			g += i + " OR ";
		}
		int index = g.lastIndexOf("OR");
		g = g.substring(0, index);
		return g;
	}

	/**
	 */
	public static NodeProperty createNodeGeneratorProperty(TNode node,
			String abq, String link, Map<String, String> config) {
		NodeProperty nod = new NodeProperty();
		config.put(ABQFile.LAC, link);
		GBNodes nodes = GB.getNodes();
		TPath node_path = nodes.getPath(node);



		config.put(ABQFile.NODE_PATH, node_path.getName());
		Gson gson = new Gson();
		// everything should be accounted for... now.
		String json = gson.toJson(config);
		nod.setName(NodeProperty.NODE_GENERATOR);
		nod.setNode_id(node.getNode_id());
		nod.setProperty(json);
		nod.setType(NodeProperty.NODE_GENERATOR);
		return nod;
	}

	public static void setProperty(TNode node, String _property_type,
			String _name, String _value) {

		long node_id = node.getNode_id();
		NodeProperty property = NodeManager.getNodeProperty(node.getNode_id(),
				_property_type);
		if (property == null) {
			property = new NodeProperty();
			property.setType(_property_type);
			property.setNode_id(node.getNode_id());
			property.setName(_property_type);
		}

		String json = property.getProperty();
		if (json == null || json.length() <= 0) {
			LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
			m.put(_name, _value);
			Gson gg = new Gson();
			String __json = gg.toJson(m);
			property.setProperty(__json);

		} else {
			Gson g = new Gson();
			Map installer = g.fromJson(json, Map.class);
			installer.put(_name, _value);
			json = g.toJson(installer);
			property.setProperty(json);
		}
		GBNodes.saveNodeProperty(property);
	}

	/**
	 * Redundant method. will deprecsate GBNodes.saveNodeProperty
	 * 
	 * @param _p
	 */
	public static void saveNodeProperty(NodeProperty _p) {
        Session session = HBConnect.getSession();
		try {
			long node_id = _p.getNode_id();
			String key = _p.getName();
			session.beginTransaction();
			Criteria c = session.createCriteria(NodeProperty.class);
			c.add(Restrictions.and(Restrictions.eq("node_id", node_id),
					Restrictions.eq("name", key)));
			List list = c.list();
			if (list == null || list.size() <= 0) {
				session.save(_p);
			} else {
				NodeProperty nodep = (NodeProperty) list.get(0);
				nodep.setProperty(_p.getProperty());
				nodep.setFile(_p.getFile());
				nodep.setType(_p.getType());
				nodep.setFile(_p.getFile());
				session.update(nodep);
			}
			session.flush();
			session.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
	}

	/**
	 * Redundant method. will deprecsate GBNodes.saveNodeProperty
	 * 
	 * @param _p
	 */
	public void saveNodeProperties(Map<String, String> _p, String _path) {
		Map<String, String> pn = GB.getNodeProps(_path);
		if ( pn == null )
			pn = new LinkedHashMap<String, String> ();
		Set<String> newks = _p.keySet();
		for (String key : newks) {
			pn.put(key, _p.get(key));
		}

		TNode node = getNode(_path);
		Set<String> merged = pn.keySet();
		for (String mk : merged) {
			String prop = pn.get(mk);
			NodeProperty nod = new NodeProperty();
			nod.setName(mk);
			nod.setNode_id(node.getNode_id());
			nod.setProperty(prop);
			nod.setType("generic");
			saveNodeProperty(nod);
		}
	}

	public static NodeProperty getNodeProperty(long node_id,
			String _property_type) {
		Session session = HBConnect.getSession();
		try {
			session.beginTransaction();
			Criteria c = session.createCriteria(NodeProperty.class);
			c.add(Restrictions.eq("node_id", node_id)).add(
					Restrictions.eq("type", _property_type));
			List list = c.list();
			if (list == null || list.size() <= 0) {
				return null;
			} else {
				LinkedHashMap<String, String> mapli = new LinkedHashMap<String, String>();
				for (Object l : list) {
					NodeProperty p = (NodeProperty) l;
					return p;
				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;

	}

	public static void deleteNode(String path) {

		String rmn = path.trim();

		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(rmn);
		TPath node_path = nodes.getPath(node);
		if (node_path == null)
			node_path = nodes.getPath(rmn);

		if (node == null)
			GB.print("Failed to find the Node object: " + rmn);
		if (node_path == null)
			GB.print(" Failed to find the Path object for: " + rmn);

		// remove the references
		String parent = GBPathUtils.getParent(rmn);
		GB.print("Removing references from parent " + parent);

		TNode parent_node = nodes.getNode(parent);
		if (parent_node != null) {
			ArrayList<Integer> remove = new ArrayList<Integer>();
			List<Integer> refs = parent_node.getReference();
			for (int i = 0; i < refs.size(); i++) {
				int l = refs.get(i);
				TNode n = nodes.getNode(l);
				if (n == null)
					remove.add(i);
			}
			for (Integer index : remove) {
				refs.remove(index);
			}
			parent_node.setReference(refs);
			Session sess = HBConnect.getSession();
			try {
				sess.beginTransaction();
				Criteria c = sess.createCriteria(TNode.class).add(
						Restrictions.eq("node_id", parent_node.getNode_id()));
				List l = c.list();
				if (l != null || l.size() > 0) {
					TNode pnode = (TNode) l.get(0);
					pnode.setReference(refs);
					Criteria cc = sess.createCriteria(TNode.class).add(
							Restrictions.eq("node_id", node.getNode_id()));
					List ll = cc.list();
					if (ll != null) {
						TNode remove_me = (TNode) ll.get(0);
						sess.delete(remove_me);
					}
					sess.update(pnode);
					sess.flush();
				}
				sess.getTransaction().commit();
			} finally {
				HBConnect.close(sess);
			}
			GBPathUtils.remove(node_path);
		}

	}

	/**
	 * New version of the Map<INteger, TMNode> load ( list<integer>, session)
	 * 
	 * @param _node_ids
	 * @param session
	 * @return
	 */
	private Map<Integer, TNode> load_nodes(List<Integer> _node_ids,
			Session session) {
		HashMap<Integer, TNode> default_map = new HashMap<Integer, TNode>();
		if (_node_ids == null || _node_ids.size() <= 0)
			return default_map;
		try {

			String hql = "from TMNode node where ";
			for (int i = 0; i < _node_ids.size(); i++) {
				hql += " node.node_id=" + _node_ids.get(i);
				if (i + 1 < _node_ids.size())
					hql += " OR ";
			}
			log.debug("\n\n\n\n\n\n");
			log.debug("----------\n");
			log.debug(hql);
			log.debug("----------\n");

			Query q = session.createQuery(hql);
			List<TNode> lvalue = q.list();
			log.debug("We have the list " + lvalue);
			HashMap<Integer, TNode> vl = new HashMap<Integer, TNode>();
			for (int j = 0; j < lvalue.size(); j++) {

				TNode current = lvalue.get(j);
				vl.put((int) current.getNode_id(), current);
				// HibernateToGWT.convert(current));
			}
			return vl;

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public TNode getNode(String _param, long nodeId, Session session) {
		try {
			session.beginTransaction();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq(_param, nodeId));
			List<TNode> lvalue = (List<TNode>) c.list();
			if (lvalue == null || lvalue.size() <= 0)
				return null;
			TNode value = lvalue.get(0);
			if (value == null)
				return null;
			else
				return HibernateToCoreJava.convert(value);

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
		}
		return null;
	}

	/**
	 * Remoce a node from the database with the given primary id.
	 * 
	 * @param nodeID
	 * @return
	 */
	public String removeNode(long nodeID) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("", nodeID));
			List<TNode> node_list = c.list();
			if (node_list != null && node_list.size() > 0) {
				TNode node = node_list.get(0);
				session.delete(node);
			}
			session.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return "Node removed";
	}

	public String removeNodeAnnotation(String _path, String _table_name,
			String data) {

		HashMap<String, String> annotation = AnnotationLACAction
				.getAnnotationMap(_path, "milton");
		try {
			String solr = ABProperties.get(ABProperties.SOLRSITE, null);
			TMSolrServer solr_server = new TMSolrServer(solr);
			Set<String> fields = annotation.keySet();
			ArrayList<String> remove_fields = new ArrayList<String>();
			for (String f : fields) {
				remove_fields.add(f);
			}
			List<String> field_names = new ArrayList<String>();
			Set<String> f = annotation.keySet();
			for (String ff : f) {
				field_names.add(ff);
			}
			solr_server.dynamicRemoveAnnotation(_table_name, field_names);
		} catch (LoaderException e) {
			e.printStackTrace();
		}
		return "Annotation complete.";
	}

	/**
	 * remove the node id from the foldr
	 */
	public String remove(String _path, long _nodeID) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			TNode tm = getNode(_path, session);
			tm.removeCRef(_nodeID);

			session.update(tm);
			session.flush();
			session.getTransaction().commit();

			return "Node removed from : " + _path;
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to remove node : " + _nodeID + " from path= "
					+ _path + "  msg: " + _e.getLocalizedMessage();
		} finally {
			HBConnect.close(session);
		}
	}

	/**
	 * TODO: use jdbc here.
	 * 
	 * @param _node_id
	 * @return
	 */
	public TNode load(long _node_id) {
		try {
			Session session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("node_id", _node_id));
			List<TNode> node_list = c.list();
			if (node_list != null && node_list.size() > 0) {
				TNode node = node_list.get(0);
				node = HibernateToCoreJava.convert(node);
				return node;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			dbcm.close();
		}
		return null;
	}

	public TNode getNode(String[] path) {
		String pp = "";
		for (String p : path) {
			pp += "/" + p;
		}
		return getNode(pp);
	}

	/**
	 * Apply a heirarchy (path) to a link (lac) object.
	 */
	public String exec(String _lac) {

		String[] lac = LAC.parse(_lac);
		String _target = lac[0];
		String _action = lac[1];
		String _data = lac[2];
		LACAction lac_action = LACActionFactory.create(_target, _action, _data);
		try {
			lac_action.exec();
		} catch (LACExecException _exec) {
			_exec.printStackTrace();
		}
		return null;
	}

	public TNode createAlias(String user, String path, SourceType ref,
			String lac) {

		TNode root_node = createPath(path, user, SourceType.NODE);
		TNode node = getNode(path);

		node.setUser(user);
		node.setCreatedDate(new Date());
		node.setLink(lac);
		node.setNodeType(ref.getName());
		try {
			save(node);
		} catch (Exception e) {
			log.debug("Failed to save the node while creating the alias");
			e.printStackTrace();
		}
		return node;
	}

	public Map<Long, String> getQuickNodeRef(TNode currentNode) {
        Session sess = null;
		HashMap<Long, String> map = new HashMap<Long, String>();
		try {
			List<Integer> refNodes = currentNode.getReference();
			sess = dbcm.getSession();
			sess.beginTransaction();

			String or_clause = "";
			int index = 0;
			for (Integer i : refNodes) {
				if (index > 0)
					or_clause += " or ";
				or_clause += "node.node_id=" + i;
				index++;
			}

			SQLQuery query = sess
					.createSQLQuery("select name, node_id from ab_node node where "
                            + or_clause);
			if (or_clause == null || or_clause.length() <= 0)
				return map;

			List<Object[]> ls = query.list();
			for (Object[] ob : ls) {
				// java.math.BigDecimal l1 = (java.math.BigDecimal) ob[1];
				String s1 = (String) ob[0];
				Number n = (Number) ob[1];

				// if ( ob[1] instanceof java.math.BigDecimal )
				// {
				//
				// }else if ( ob[1] instanceof java.math.BigInteger )
				// {
				//
				// }
				map.put(n.longValue(), s1);
				log.info("ob:  " + n + " s : " + s1);
			}
			return map;

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(sess);
		}
		return map;
	}

	public static void main(String[] _args) {
		NodeManager t = new NodeManager();
		t.getQuickNodeRef(null);
	}

	public String setNodeType(String node_id, SourceType _type) {

		Long _node_id = Long.parseLong(node_id);
		TNode no = load(_node_id);
		no.setNodeType(_type.getName());
		try {
			save(no);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Type updated to " + _type.getName();
	}

	public String setNodeName(String node_id, String _name) {

		Long _node_id = Long.parseLong(node_id);
		TNode no = load(_node_id);
		no.setName(_name);
		try {
			save(no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Name changed to : " + _name;
	}

	/**
	 * Remove the annotation applied to a particular lac.
	 */
	public String removeAnnotation(String _user, String _path, String _lac) {
		NodeManager node_service = new NodeManager();
		TNode alias = node_service.createAlias(_user, _path, SourceType.DB,
				_lac);
		HashMap<String, String> annotation = AnnotationLACAction
				.getAnnotationMap(_path, _user);
		String solr_table = alias.getLink();
		String[] lac_ = LAC.parse(solr_table);
		// e.g.
		// [milton_Repository_SH_Treatment, search, g*]
		try {
			// get a handle on the solr server
			String solr = ABProperties.get(ABProperties.SOLRSITE, null);
			TMSolrServer solr_server = new TMSolrServer(solr);
			Set<String> fields = annotation.keySet();
			ArrayList<String> remove_fields = new ArrayList<String>();
			for (String f : fields) {
				remove_fields.add(f);
			}
			// solr_server.dynamicAppendSchema(table_name, data, annotation);
			solr_server.dynamicRemoveAnnotation(lac_[0], remove_fields);
		} catch (LoaderException e) {
			e.printStackTrace();
		}
		return "Annotation removed.";
	}

	/**
	 * Remove the annotation applied to a particular lac.
	 */
	public String removeNodeChildren(String _user, String _path) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			TNode tm = getNode(_path, session);
			if (tm != null)
				tm.setReference(new ArrayList<Integer>());
			session.flush();
			return "Removed";
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return "Remove failed";
	}

	public String saveNodeProperties(long node_id, Map<String, String> props) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("node_id", node_id));
			List<TNode> node_list = c.list();
			if (node_list != null && node_list.size() > 0) {
				TNode node = node_list.get(0);
				String user = props.get("user");
				if (user != null) {
					node.setUser(user);
				}
				String link = props.get("link");
				if (link != null)
					node.setLink(link);
				String name = props.get("name");
				if (name != null)
					node.setName(name);

			}
			session.flush();
			session.getTransaction().commit();
			return "Node updated.";
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to update the node: " + _e.getLocalizedMessage();
		} finally {
			HBConnect.close(session);
		}
	}

	public String setNodeDescription(long _node_id, String _description) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("node_id", _node_id));
			List<TNode> node_list = c.list();
			if (node_list != null && node_list.size() > 0) {
				TNode node = node_list.get(0);
				node.setDescription(_description);
			}
			session.flush();
			session.getTransaction().commit();
			return "Description saved (ab_node=" + _node_id + ")";
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to save description for node: " + _node_id;
		} finally {
			HBConnect.close(session);
		}
	}

	public String getLink(long _node_id) {
        Session session = null;
		try {
			String link = null;
			session = dbcm.getSession();
			session.beginTransaction();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.eq("node_id", _node_id));
			List<TNode> node_list = c.list();
			if (node_list != null && node_list.size() > 0) {
				TNode node = node_list.get(0);
				link = node.getLink();
			}
			session.getTransaction().commit();
			return link;
		} catch (Exception _e) {
			_e.printStackTrace();
			return null;
		} finally {
            HBConnect.close(session);
		}
	}

	public String buildNodeIndex(String _path, String _user) {
		try {
			GBFileManager gbm = new GBFileManager(dbcm);
			return gbm.buildIndicies(_path, _user);
		} catch (Exception _e) {
			_e.printStackTrace();
			return "" + _path + " error in path " + _e.getLocalizedMessage();
		}
	}

	public TMNodeSet load(String _path, int _index) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			TNode tm = getNode(_path, session);
			tm = HibernateToCoreJava.convert(tm);
			TMNodeSet set = new TMNodeSet(tm);
			set.setParent(tm);
			set = loadNodeSet(set, _index, session);
			set = HibernateToCoreJava.convert(set);
			session.close();
			return set;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	/**
	 * This is a recursive method for loading a tree structure.
	 * 
	 * @param _parent
	 * @param _index
	 * @param session
	 * @return
	 */
	private TMNodeSet loadNodeSet(TMNodeSet _parent, int _index, Session session) {
		int index = _index - 1;
		if (index < 0)
			return _parent;
		TNode tm = _parent.getParent();

		if (tm.getReference().size() == 0)
			return _parent;

		Map<Integer, TNode> values = load(tm.getReference(), session);
		Collection<TNode> cv = values.values();
		List<TMNodeSet> list = new ArrayList<TMNodeSet>();
		for (TNode t : cv) {
			TMNodeSet ch = new TMNodeSet(t);
			ch = loadNodeSet(ch, index, session);
			list.add(ch);
		}
		_parent.setSub(list);

		return _parent;
	}

	/**
	 * Load the node set for the particular ID object.
	 */
	public TMNodeSet loadNodeSet(long node_id) {
        Session session = null;
		try {
			TNode node = load(node_id);
			TMNodeSet set = new TMNodeSet(node);
			if (node.getReference() != null && node.getReference().size() > 0) {
				session = dbcm.getSession();
				session.beginTransaction();
				set = loadNodeSet(set, 1, session);
			}
			return HibernateToCoreJava.convert(set);
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
		return null;
	}

	public void add(String _user, TNode node, ArrayList<TMNodeLink> links) {
        Session session = null;
		try {
			session = dbcm.getSession();
			session.beginTransaction();
			long node_id = node.getNode_id();
			Criteria c = session.createCriteria(TNode.class).add(
					Restrictions.idEq(node_id));
			TNode cnode = (TNode) c.uniqueResult();
			for (TMNodeLink _l : links) {
				TNode ccnode = new TNode();
				ccnode.setCreated_by(_user);
				ccnode.setCreatedDate(new Date());
				ccnode.setNodeType(_l.getNodeLinkType());
				ccnode.setLastEditedDate(new Date());
				ccnode.setName(_l.getTitle());
				ccnode.setLink(_l.getCore_uri());
				session.save(ccnode);
				cnode.addCRef(ccnode);
			}
			session.getTransaction().commit();
			// cnode.setChildren(copy ( cnode.getChildren(), t.getChildren()));
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
		}
	}

	public String[] list_dep(TNode node) {
		List<Integer> list = node.getReference();
		Map<Integer, TNode> n = load(list);

		Collection<TNode> col = n.values();
		String[] listnodes = new String[col.size()];
		int index = 0;
		// System.out.printf("-[%1$s]D: %2$s\t", n.getNode_id(),
		// n.getName());

		for (TNode t : col) {
			if (t.getNodeType() != null) {
				if (t.getNodeType().equalsIgnoreCase(SourceType.DB.getName())) {
					listnodes[index++] = String
							.format("\t- %1$s, type{table}:%2$s:%3$s",
                                    t.getName(),
                                    GBUtil.trunc(t.getCreated_by()),
                                    t.getCreatedDate());
				} else if (t.getNodeType().equalsIgnoreCase(
						SourceType.RAW_FILE.getName())
						|| t.getNodeType().equalsIgnoreCase(
								SourceType.GBFILE.getName())) {
					listnodes[index++] = String
							.format("\t- %1$s, type{file}:%2$s:%3$s",
                                    t.getName(),
                                    GBUtil.trunc(t.getCreated_by()),
                                    t.getCreatedDate());
				} else if (t.getNodeType().equalsIgnoreCase(
						SourceType.NODE.getName())) {
					listnodes[index++] = String
							.format("\t- %1$s, type{" + t.getNodeType()
									+ "}:%2$s:%3$s", t.getName(),
									GBUtil.trunc(t.getCreated_by()),
									t.getCreatedDate());
				} else {
					listnodes[index++] = String
							.format("\t- %1$s, type{" + t.getNodeType()
									+ "}:%2$s:%3$s", t.getName(),
									GBUtil.trunc(t.getCreated_by()),
									t.getCreatedDate());
				}
			} else {
				listnodes[index++] = String.format(
						"\t- %1$s, type{" + t.getNodeType() + "}:%2$s:%3$s",
						"", t.getName(), GBUtil.trunc(t.getCreated_by()),
						t.getCreatedDate());
			}
			// "\t-[" + t.getNode_id() + "]-\t" + t.getName()
			// + "    " + t.getCreated_by() + "    " + t.getCreatedDate();
		}
		return listnodes;
	}

	public String[] list(TNode node) {
		List<Integer> list = node.getReference();
		Map<Integer, TNode> n = load(list);

		Collection<TNode> col = n.values();
		String[] listnodes = new String[col.size()];
		int index = 0;
		// System.out.printf("-[%1$s]D: %2$s\t", n.getNode_id(),
		// n.getName());

		for (TNode t : col) {
			if (t.getNodeType() != null) {
				if (t.getNodeType().equalsIgnoreCase(SourceType.DB.getName())) {
					listnodes[index++] = String.format(
							"\t\t- %1$s - %2$s  %3$s/",
							t.getName(), GBUtil.trunc(t.getCreated_by()),
							t.getCreatedDate());
				} else if (t.getNodeType().equalsIgnoreCase(
						SourceType.RAW_FILE.getName())
						|| t.getNodeType().equalsIgnoreCase(
								SourceType.GBFILE.getName())) {

					listnodes[index++] = String.format(
							"\t\t- %1$s - %2$s  %3$s (File)",
							t.getName(), GBUtil.trunc(t.getCreated_by()),
							t.getCreatedDate());

				} else if (t.getNodeType().equalsIgnoreCase(
						SourceType.NODE.getName())) {
					listnodes[index++] = String
							.format("\t\t- %1$s    %2$s  %3$s"
									+ geTypeSummary(t.getNodeType()) + "", t.getName(),
									GBUtil.trunc(t.getCreated_by()),
									t.getCreatedDate());
				} else {
					listnodes[index++] = String
							.format("\t\t- %1$s   %2$s  %3$s{"
									+ geTypeSummary(t.getNodeType()) + "}", t.getName(),
									GBUtil.trunc(t.getCreated_by()),
									t.getCreatedDate());
				}
			} else {
				listnodes[index++] = String.format(
						"\t\t- %1$s   %2$s   %3$s{" + geTypeSummary(t.getNodeType())
								+ "}", "", t.getName(),
						GBUtil.trunc(t.getCreated_by()), t.getCreatedDate());
			}
			// "\t-[" + t.getNode_id() + "]-\t" + t.getName()
			// + "    " + t.getCreated_by() + "    " + t.getCreatedDate();
		}
		return listnodes;
	}

	private static String geTypeSummary(String nodeType) {
		String c = nodeType.substring(0, 1);
		if ( c.equalsIgnoreCase ("D") || c.equalsIgnoreCase("N") ){
			return "/";
		}else if ( c.equalsIgnoreCase("T")){
			return "(table)";
		}
		return c;
	}

	public String[] listChildNames(TNode node) {
		List<Integer> list = node.getReference();
		Map<Integer, TNode> n = load(list);

		Collection<TNode> col = n.values();
		String[] listnodes = new String[col.size()];
		int index = 0;
		for (TNode t : col) {
			if (t.getNodeType() != null) {
				listnodes[index++] = t.getName();
			}
		}
		return listnodes;
	}
	

	public void tag(TTable table, TNode node) {
		TNode t = new TNode();
		t.setLink(table.getLink());
		t.setName(table.getTitle());

		try {
			save(t);
			node.addCRef(t);
			save(node);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LinkedHashMap<String, Integer> search(
			LinkedHashMap<String, Integer> _results, String _path,
			String _searchString) {
		TNode path = getNode(_path);
		// System.out.println(path.getNodeType());
		if (_path.endsWith("/"))
			_path = _path.substring(0, _path.length() - 1);

		List<Integer> list = path.getReference();
		Map<Integer, TNode> nodes = load(list);
		Collection<TNode> values = nodes.values();
		for (TNode n : values) {
			return search(_results, _path + "/" + n.getName(), _searchString);
		}
		return _results;
	}

	/**
	 * list the path: What is returned is the following: filename \t created_by
	 * \t date_created
	 * 
	 * @param _path
	 * @return
	 */
	public String[] ls(String _path) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
		try {

			// private long path_id = -1l;
			// private String group_name = null;
			// private long node_id = -1l;
			// private String name = "";
			// private String description = "";
			// private Long parent = -1l;

			String sql = "select path_name from ab_path where path_name like \""
					+ _path + "%\"";
			con = dbcm.getJDBCConnection();
			st = con.createStatement();
			System.out.println(" sq;l " + sql);
			rs = st.executeQuery(sql);

			ArrayList<String> list = new ArrayList<String>();
			while (rs.next()) {
				String pathid = rs.getString(1);
				list.add(pathid);
			}
			String[] values = list.toArray(new String[list.size()]);
			st.close();
			return values;

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(st);
            JDBC.closeConnection(con);
		}
		return null;
	}

	public TPath getPathForNode(long node_id, Session _session) {
		try {
			Criteria c = _session.createCriteria(TPath.class);
			c.add(Restrictions.eq("node_id", node_id));
			List l = c.list();
			if (l.size() > 0) {
				return (TPath) l.get(0);
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a path with a link on the leaf node.
	 * 
	 * @param path
	 * @param userid
	 * @param nodetype
	 * @param link
	 * @return
	 */
	public TNode createPath(String path, String userid, SourceType nodetype,
			String link) {
        Connection con = null;
        Session session = null;
		try {
			con = dbcm.getJDBCConnection();
			session = dbcm.getSession();
			session.beginTransaction();
			TNode no = mknode(userid, path, con, session, nodetype);
			if (link != null)
				no.setLink(link);
			session.getTransaction().commit();
			return no;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(session);
            JDBC.closeConnection(con);
		}
		return null;
	}

	/**
	 * In order to fully remove the path we need to remove it from the ab_path
	 * and the ab_node as well as any children references: i.e. ab_node_refs
	 * 
	 * @param _path
	 */
	public String removePath(String _path) {
		TNode node = getNode(_path);
		long nodeid = node.getNode_id();
        Connection con = null;
        Statement st = null;
		try {
			con = dbcm.getJDBCConnection();
			st = con.createStatement();

			GB.print(" delete from ab_path : " + _path);
			String qy = "delete from ab_path where path_name = '" + _path + "'";
			st.execute(qy);
			GB.print(" delete from ab_node : " + nodeid);
			String qt = "delete from ab_node where node_id=" + nodeid;
			st.execute(qt);
			st = con.createStatement();
			GB.print(" delete from ab_node_ref : " + nodeid);
			String qq = "delete from  ab_node_ref where n_to_r=" + nodeid;
			st.execute(qq);
			return "Removed.";
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeStatement(st);
            JDBC.closeConnection(con);
        }
        return "Failed to remove the path : " + _path;
	}

	public String renameLeaf(String _path, String newName) {
		String root = _path;
		_path = _path.trim();
		int leafi = _path.lastIndexOf('/');
		if (leafi > 0) {
			root = _path.substring(0, leafi);
		}

		TPath root_path = getPath(_path);
		TNode node = getNode(_path);
		TPath path = getPath(_path);
		String orig = node.getName();

		if (node == null || path == null)
			return "Path " + _path + " not found";

        Connection con = null;
        Statement st = null;
		long nodeid = node.getNode_id();
		try {
			con = dbcm.getJDBCConnection();
			st = con.createStatement();

			String qy = "update ab_path set path_name='" + root + "/" + newName
					+ "' where path_name = '" + _path + "'";
			st.execute(qy);

			String qt = "update ab_node set node_name='" + newName
					+ "' where node_id=" + node.getNode_id();
			st.execute(qt);

			// we need to think about how we are going to remove the raw files.
			// not sure what I'm going to do here.
			// raw_files do not referencer nodes.
			return "Rename complete -[" + node.getNode_id() + "]- " + orig
					+ " --> " + newName;

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeStatement(st);
            JDBC.closeConnection(con);
        }
        return "Failed to remove the path : " + _path;
	}

	/**
	 * 
	 * @param user_name
	 * @param node_idl
	 * @param path
	 * @return
	 */
	public String linkNode(String user_name, long node_idl, String path) {
        Session session = null;
		try {
			// {{ FIRST VERIFY THE LINK IS NOT ALREADY THERE }}
			TPath patho = getPath(path);
			String parent_path = GBPathUtils.getParent(path);
			System.out.println(" parent path : " + parent_path);
			TPath parent = getPath(parent_path);
			if (patho != null) {
				return "Cannot create a link since the path already exists.";
			}
			if (parent == null) {
				GBNodes.mkdir(user_name, parent_path);
				parent = getPath(parent_path);
			}
			TPath the_path = NodeFactory.createPathForNode(user_name, path,
					parent, node_idl);
			session = dbcm.getSession();
			session.beginTransaction();
			session.save(the_path);
			session.getTransaction().commit();

		} catch (Exception _e) {
			_e.printStackTrace();
			return "Path link failed to process.  " + _e.getLocalizedMessage();
		} finally {
            HBConnect.close(session);
        }
        return "Path link Created" + path + " --> -[" + node_idl + "]-";
	}

	/**
	 * TODO: Need to finish this... not done.
	 * 
	 * @param fac
	 * @return
	 */
	public static TNode createNodeFromReferenceScript(String fac) {

		if (fac.equalsIgnoreCase(ReferenceScript.NODE.name)) {
			// String[] st = fac.par
		}
		return null;
	}

	public List<String> getAllPaths() {
		Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
		try {
			String st = "select path_name from ab_path";
			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(st);
			ArrayList<String> paths = new ArrayList<String>();
			while (rs.next()) {
				String p = rs.getString(1);
				paths.add(p);
			}
			return paths;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeConnection(connection);
		}
		return null;
	}

	public void updatePath(String from, String to) {
		Connection connection = null;
        Statement statement = null;
        PreparedStatement updates = null;
        ResultSet rs = null;
		try {
			String st = "select path_id, path_name from ab_path where path_name like '"
					+ from + "/%'";
			connection = dbcm.getJDBCConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(st);
			LinkedHashMap<Integer, String> paths = new LinkedHashMap<Integer, String>();
			while (rs.next()) {
				Integer p = rs.getInt(1);
				String stf = rs.getString(2);
				paths.put(p, stf);
			}

			String up = "update ab_path set path_name = ? where path_id = ?";
			connection.setAutoCommit(false);
			updates = connection.prepareStatement(up);
			Set<Integer> ids = paths.keySet();
			for (Integer p : ids) {
				String lpath = paths.get(p);
				lpath = lpath.replace(from, to);
				updates.setString(1, lpath);
				updates.setInt(2, p);
				updates.executeUpdate();
			}
			connection.commit();

		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            JDBC.closeResultSet(rs);
            JDBC.closeStatement(statement);
            JDBC.closeStatement(updates);
            JDBC.closeConnection(connection);
		}
	}

	public void close() {
		dbcm.close();

	}

	// public TNode getNode(long _nodeid, DBConnectionManager dbcm) {
	// Connection con = dbcm.getJDBCConnection();
	// return getNode(_nodeid, con);
	// }
}
