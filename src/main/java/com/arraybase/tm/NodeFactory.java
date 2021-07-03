package com.arraybase.tm;

import com.arraybase.GBUtil;
import com.arraybase.db.JDBC;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.GBLogger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

public class NodeFactory {

	private static GBLogger lg = GBLogger.getLogger(NodeFactory.class);

	static {
		lg.setLevel(GBLogger.DEBUG);
	}

	public static TNode createRoot(String _user, String root_name,
			Session _session) {

		lg.debug("Creating the root  " + root_name);

		TNode node = new TNode();
		node.setUser(_user);
		node.setCreated_by(_user);
		node.setCreatedDate(new Date());
		node.setLastEditedDate(new Date());
		node.setName(root_name);
		// Session session = HBConnect.getSession();
		// session.beginTransaction();
		_session.save(node);

		TPath path = new TPath();
		path.setGroup_name(root_name);

		System.out.println(" creating the root :  " + root_name);

		path.setName("/" + root_name);
		path.setTMParent((-1l));
		path.setNode_id(node.getNode_id());
		path.setDescription("root");
		_session.save(path);
		// session.getTransaction().commit();

		return node;
	}

	public static TNode createNode(String _path, String _user, String _name,
			SourceType _type, Session _session) {
		_path = GBPathUtils.adjustPathChars(_path);

		Criteria c = _session.createCriteria(TPath.class);
		c.add(Restrictions.eq("name", _path));
		List l = c.list();
		if (l == null || l.size() <= 0) {
			// parent path not found...
			// TODO: implement a plan for this.
			return null;
		}

		TPath parent_path = (TPath) l.get(0);
		long node_id = parent_path.getNode_id();
		c = _session.createCriteria(TNode.class);
		c.add(Restrictions.eq("node_id", node_id));
		l = c.list();
		if (l == null || l.size() <= 0) {
			// parent node not found...
			// TODO: implement a plan for this.
			return null;
		}
		TNode parent_node = (TNode) l.get(0);
		TNode node = new TNode();
		node.setUser(_user);
		node.setCreated_by(_user);
		node.setNodeType(_type.getName());
		node.setCreatedDate(new Date());
		node.setLastEditedDate(new Date());
		node.setName(_name);
		_session.save(node);

		parent_node.addCRef(node);
		_session.update(parent_node);

		TPath path = new TPath();
		path.setTMParent(parent_path.getPath_id());
		path.setGroup_name(parent_path.getGroup_name());

		String name = GBUtil.catPath(_path, _name);
		path.setName(name);
		path.setNode_id(node.getNode_id());
		path.setDescription("node created");
		System.out.println(" saving the path object " + _path);
		_session.save(path);
		_session.flush();

		return node;
	}

	public static TPath createOrUpdatePathObject(String _path_name,
			String _path_group, long _parentPath, long _nodeid, Session _session) {

		Criteria c = _session.createCriteria(TPath.class);
		c.add(Restrictions.eq("name", _path_name));
		List l = c.list();
		if (l != null && l.size() > 0) {
			TPath p = (TPath) l.get(0);
			p.setNode_id(_nodeid);
			p.setTMParent(_parentPath);
			_session.update(p);
			_session.flush();
			return p;
		} else {
			TPath path = new TPath();
			path.setTMParent(_parentPath);
			path.setGroup_name(_path_group);
			path.setName(_path_name);
			path.setNode_id(_nodeid);
			path.setDescription(" ref for " + _nodeid);
			_session.save(path);
			_session.flush();

			return path;
		}
	}

	private static TNode createNode(ResultSet rs) {
		try {
			while (rs.next()) {
				long node_id = rs.getLong(1);
				Date created = rs.getDate(2);
				String by = rs.getString(3);
				String desc = rs.getString(4);
				Date last = rs.getDate(5);
				String link = rs.getString(6);
				String name = rs.getString(7);
				String type = rs.getString(8);
				float value = rs.getFloat(9);
				String owner = rs.getString(10);
				String status = rs.getString(11);
				String synonyms = rs.getString(12);
				int ref = rs.getInt(13);
				TNode n = new TNode();
				n.setName(name);
				n.setNode_id(node_id);
				n.setCreated_by(by);
				n.setDescription(desc);
				n.setLastEditedDate(last);
				n.setOwner(owner);
				n.setLink(link);
				n.setNodeType(type);
				n.setCreatedDate(created);
				n.setNode_value(value);
				n.setStatus(status);
				n.setSynonyms(synonyms);
				n.addCRef(ref);
				while (rs.next()) {
					int more_refs = rs.getInt(13);
					n.addCRef(more_refs);
				}
				return n;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
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
	public static TNode createJDBCNode(long key, Connection con) {
		Statement tst = null;
		ResultSet rs = null;
		try {
			tst = con.createStatement();
			String sql = "select node_id, createdDate, created_by, node_description, lastEditedDate, node_link, "
					+ "node_name, node_type, node_value, node_owner, node_status, synonyms, reference from ab_node "
					+ "left join ab_node_ref on ab_node.node_id=ab_node_ref.n_to_r where node_id="
					+ key;
			// System.out.println ( sql );
			rs = tst.executeQuery(sql);
			return createNode(rs);
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(tst);
		}
		return null;
	}

	/**
	 * This is an advanced feature and should only be used by an administrator.
	 * 
	 * @param userid
	 * @param fullPath
	 * @param _parent
	 * @param _node_id
	 * @return
	 */
	public static TPath createPathForNode(String userid, String fullPath,
			TPath _parent, long _node_id) {
		try {

			TPath path = new TPath();
			path.setTMParent(_parent.getPath_id());
			String root = GBPathUtils.getRoot(fullPath);
			path.setGroup_name(root);
			path.setName(fullPath);
			path.setNode_id(_node_id);
			path.setDescription(" Item created for node id " + _node_id);
			return path;
		} catch (Exception _e) {
			_e.printStackTrace();
			return null;
		}

	}

}
