package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.flare.CurrentTimeForSolr;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;
import org.apache.solr.schema.TrieDateField;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

public class SetSchemaCommand implements GBPlugin {

	public String exec(String command, String variable_key) {
		int index = command.indexOf('(');
		int index2 = command.indexOf(')');
		String temp = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		temp2 = temp2.trim();

		String[] v = temp.split(",");
		if (v == null || v.length <= 0) {
			v = new String[1];
			v[0] = temp;
		}
		ArrayList<GColumn> gclist = new ArrayList<GColumn>();
		for (String s : v) {
			String t = s.trim();
			String[] tt = t.split("\\s+");
			GColumn column = new GColumn(tt[1], tt[0]);
			gclist.add(column);
		}
		String[] table_t = temp2.split("\\s+");
		String table_name = table_t[2];
		// Set<String> keys = map.keySet();
		// for (String key : keys) {
		// GB.print(key + " \t " + map.get(key));
		// }
		String path = GB.pwd() + "/" + table_name;
		// first lets get the node.
		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.print("Node with name " + path + " is not available");
			return null;
		}
		String user = GB.getDefaultUser();
		String schema = NameUtiles.convertToValidCharName(path);

		// NameUtiles.prepend(user, path);
		DBConnectionManager dbcm = GB.getConnectionManager();
		NodeManager tmnode = new NodeManager(dbcm);
		LinkedHashMap<String, Map<String, String>> _params = createParameters(gclist);

		for (GColumn g : gclist) {
			System.out.println(" 1.column " + g.getName());
		}
		Set<String> list2 = _params.keySet();
		for (String l : list2) {
			System.out.println(" 2.column " + l);
		}
		TableManager tmd = new TableManager(dbcm);
		tmd.build(user, TableManager.TMSOLR, schema, "__", "1", _params, null);
		// TableManager table_manager = tables.get
		// GB.print("\n\n\n created schema = \t " + table_name);
		String link = "" + schema + ".search(*:*)";
		node.setLink(link);
		node.setLastEditedDate(new Date());
		tmnode.save(node);

		// String _lib_name = NameUtiles.strip(_userName, _name);
		TTable litem = new TTable();
		Session hibernateSession = null;
		try {
			hibernateSession = dbcm.getSession();
			// lock it
			synchronized (hibernateSession) {
				hibernateSession.beginTransaction();
				Criteria c = hibernateSession.createCriteria(TTable.class);
				c.add(Restrictions.eq("title", schema));
				List values = c.list();
				if (values != null && values.size() > 0) {
					litem = (TTable) values.get(0);
				}
				litem.setDescription("");
				litem.setLastEdited(new Date());
				// litem.setSecurityStatus(_security + ".png");
				litem.setUser(user);
				litem.setSourceType(node.getNodeType());

				TMTableSettings tmset = litem.getSettings();
				if (tmset == null)
					tmset = new TMTableSettings();
				Set<String> keys = _params.keySet();
				LinkedHashMap<String, Integer> order = new LinkedHashMap<String, Integer>();
				int index1 = 0;
				for (String key : keys) {
					order.put(key, index1++);
				}
				tmset.setCol_order(order);
				litem.setSettings(tmset);
				litem.setTitle(schema);
				if (litem.getItemID() < 0)
					hibernateSession.save(litem);
				else {
					hibernateSession.update(litem);
					hibernateSession.flush();
				}
				hibernateSession.getTransaction().commit();
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}

		return "Schema created";
	}

	public static LinkedHashMap<String, Map<String, String>> createParameters(
			ArrayList<GColumn> ps) {
		LinkedHashMap<String, Map<String, String>> params = new LinkedHashMap<String, Map<String, String>>();
		UUID idOne = UUID.randomUUID();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", idOne.toString());
		uuidp.put("dataType", "text");
		uuidp.put("requiredField", "true");
		params.put("TMID", uuidp);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");
		last_updated.put("defaultString", CurrentTimeForSolr.timeStr());
		last_updated.put("dataType", "date");
		last_updated.put("requiredField", "true");
		params.put("TMID_lastUpdated", last_updated);
		log.debug("Generating the solr schema... ");
		for (GColumn k : ps) {
			uuidp = new HashMap<String, String>();
			uuidp.put("fieldName", k.getName());
			uuidp.put("sortable", "true");
			uuidp.put("indexed", "true");
			uuidp.put("defaultString", "");
			uuidp.put("dataType", k.getType());
			uuidp.put("requiredField", "false");
			params.put(k.getName(), uuidp);
		}
		return params;
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

	static GBLogger log = GBLogger.getLogger(SetSchemaCommand.class);
}
