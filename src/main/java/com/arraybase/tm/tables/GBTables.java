package com.arraybase.tm.tables;

import com.arraybase.*;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.HBConnect;
import com.arraybase.db.JDBC;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.flare.CurrentTimeForSolr;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMID;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.flare.solr.GBSolr;
import com.arraybase.lac.LAC;
import com.arraybase.modules.GBTypes;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import org.apache.solr.schema.TrieDateField;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Get information about the tables for a particular server.
 * 
 * @author donaldm
 * 
 */
public class GBTables {

	private final static String TEMPLATES = "/.templates";
//Primer_Probes.search(14768)[SET_NAME]
	private String server = ABProperties.getSolrURL();
	DBConnectionManager dbcm = new DBConnectionManager();

	public static ArrayList<GColumn> describeCore(String core)
			throws ConnectException {
		Map<String, String> props = TableManager.getTableProperties(core);
		if ( props == null )
			return describeCore(core, null);
		Set<String> nps = props.keySet();
		for (String p : nps) {
			if (p.equalsIgnoreCase("state")) {
				return describeCore(core, null);
			}
		}
		return describeCore(core, null);
	}

	/**
	 * Does not have a field order associated
	 * 
	 * @param core
	 * @return
	 * @throws ConnectException
	 */
	public static ArrayList<GColumn> describeCore(String core,
			ArrayList<String> _order) throws ConnectException {
		ArrayList<GColumn> cols = TMSolrServer.describeCore(core);
		if (cols == null || cols.size() <= 0)
			return new ArrayList<GColumn>();
		cols = GBSearch.removeTrackingColumns(cols);

		if (_order != null) {
			ArrayList<GColumn> ncols = new ArrayList<GColumn>();
			int index = 0;
			for (String order : _order) {
				GColumn sorder = getCol(order, cols);
				if (sorder != null)
					ncols.add(sorder);
				index++;
			}
			// add the rest
			if (cols.size() > _order.size()) {
				for (int i = index; i < cols.size(); i++) {
					GColumn rcol = cols.get(i);
					ncols.add(rcol);
				}
			}
			return ncols;

		}
		return cols;
	}

	public static GColumn getCol(String order, ArrayList<GColumn> cols) {
		for (GColumn g : cols) {
			if (g.getName().equalsIgnoreCase(order))
				return g;
		}
		return null;
	}

	public static ArrayList<GColumn> describeTable(String path)
			throws ConnectException {
		TNode node = GB.getNodes().getNode(path);
		return describeTable(node);

	}

	public static ArrayList<GColumn> describeTable(TNode node)
			throws ConnectException {
		String lac = node.getLink();
		String core = GBSolr.getCoreFromLAC(lac);
		return describeCore(core);
	}

	public static void main(String[] args) {
		String test = "[{name:\"SET_ID\",width:null},{name:\"MOL_TARGETID\",width:null},{name:\"PROBE_LEN\",width:null},{name:\"GENE\",width:null},{name:\"REVERSE_LEN\",width:null},{name:\"REVERSE_ISISNO\",width:null},{name:\"SPECIES\",width:null},{name:\"FORWARD_LEN\",width:null},{name:\"FORWARD_ISISNO\",width:null},{name:\"PROBE_ISISNO\",width:null},{name:\"SET_NAME\",width:null}]";
		ArrayList<String> t = parseOrder(test);
		System.out.println(" t : " + t.size());
	}

	// [{name:"SET_ID",width:null},{name:"MOL_TARGETID",width:null},{name:"PROBE_LEN",width:null},{name:"GENE",width:null},{name:"REVERSE_LEN",width:null},{name:"REVERSE_ISISNO",width:null},{name:"SPECIES",width:null},{name:"FORWARD_LEN",width:null},{name:"FORWARD_ISISNO",width:null},{name:"PROBE_ISISNO",width:null},{name:"SET_NAME",width:null}]
	public static ArrayList<String> parseOrder(String property) {
		ArrayList<String> sort = new ArrayList<String>();
		try {
			JSONArray js = new JSONArray(property);
			int length = js.length();
			for (int i = 0; i < length; i++) {
				JSONObject obj = js.getJSONObject(i);
				String name = obj.getString("name");
				sort.add(name);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sort;
	}

	public TTable getTable(int pk) {
		TTable t = TableManager.getTable(pk, dbcm);
		return t;
	}

	/**
	 * Return the index that this table is pointing to. (given the table id is
	 * the PK )
	 * 
	 * @param pk
	 * @return
	 */
	public String getTableIndexId(int pk) {
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "select index_id from ab_table where itemID=" + pk;
			connection = dbcm.getJDBCConnection();
			st = connection.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				String index_id = rs.getString(1);
				return index_id;
			} else {
				return null;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			JDBC.closeResultSet(rs);
			JDBC.closeStatement(st);
			JDBC.closeConnection(connection);
		}
		return null;
	}

	private static GBLogger log = GBLogger.getLogger(GBTables.class);
	static {
		log.setLevel(GBLogger.DEBUG);
	}

	public GColumn getField(String target, String field_name)
			throws ConnectException {
		log.debug(" we have the target : " + target);
		ArrayList<GColumn> cols = describeCore(target, null);
		log.debug(" Checking the cols :" + cols.size());
		for (GColumn gc : cols) {
			String gcname = gc.getName();
			if (gcname != null && gcname.equalsIgnoreCase(field_name))
				return gc;
		}
		return null;
	}

	public static TTable getTable(String core_name) {
		DBConnectionManager dbcm = GB.getConnectionManager();
		Session hibernateSession = dbcm.getSession();
		try {
			hibernateSession.beginTransaction();
			Criteria c = hibernateSession.createCriteria(TTable.class);
			c.add(Restrictions.eq("title", core_name));
			List l = c.list();
			TTable t = (TTable) l.get(0);
			return t;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(hibernateSession);
		}
		return null;
	}

	/**
	 * Set the field type.
	 */
	public static void changeField(String server, String core, String field,
			String type) {
		// String table = _params.get("table");
		// String field_name = _params.get("field_name");
		// String to_type = _params.get("type");
		//
		// admin/cores?action=create_field_facet&schema=milton_Repository_HTL&table=$original_fiel
		String url = "" + ABProperties.getSolrURL()
				+ "admin/cores?action=set_field_type&table=" + core
				+ "&field_name=" + field + "&to_type=" + type;
		try {

			TMSolrServer.callSolr(url);
		} catch (SolrCallException e) {
			e.printStackTrace();
		}
	}

	public static void changeFieldType(String[] _args) {
		if (_args.length != 3) {
			GB.printUsage("gb type /gne/research/trials/date_check.xls STUDY_DATE->float");
			return;
		}

		String path = _args[1];
		String type_command = _args[2];
		String[] cmd = GBUtil.parse(type_command);
		System.out.println("Convert the path : " + path + " and column "
				+ type_command + "" + cmd[0]);
		String core = TMSolrServer.getCore(path);
		String url = ABProperties.getSolrURL();
		changeField(url, core, cmd[0], cmd[1]);
	}

	public static void listFields(String[] _args) {
		if (_args.length != 2 && _args.length != 3) {
			System.out
					.println("List the fields available in a table or node.  "
							+ "If the node contains subnodes then list the fields recursively");
			GB.printUsage("gb listfields $path");
			return;
		}
		String path = _args[1];
		if (_args.length == 3) {
			String fields = GB.getColumnProperties(path);
			GB.print(fields);
		} else {
			String server = ABProperties.getSolrURL();
			// {{ GET THE NODE OBJECT AT THIS PATH }}
			GBNodes nodes = GB.getNodes();
			TNode node = nodes.getNode(path);
			// TODO: make this a more useful feedback mechanism
			if (node == null) {
				System.err
						.println("Search failed: the path does not seem to be correct.");
				System.err.println("Search path : " + path);

				return;
			}
			String lac = node.getLink();
			if (lac == null) {
				System.err
						.println("Please provide a path to a linking node:  For example a table node");
				return;

			}
			DBConnectionManager dbcm = new DBConnectionManager();
			String _schema = LAC.getTarget(lac);
			GBSolr.getCoreFromLAC(lac, dbcm);
			try {
				ArrayList<GColumn> columns = TMSolrServer.describeCore(server,
						_schema);
				columns = GBSearch.removeTrackingColumns(columns);

				for (GColumn col : columns) {
					System.out.println("\t\t" + col.getName().trim());
				}

			} catch (ConnectException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Make a table and save it to the template node.
	 * 
	 * @param _args
	 */
	public static String createTableType(String[] _args, String current_path) {
		if (current_path == null)
			current_path = TEMPLATES;
		String a = _args[0];
		if (a.equalsIgnoreCase("gb"))
			_args = GBUtil.remove(0, _args);
		String user = GB.getDefaultUser();
		String path = current_path.trim();
		String new_table_name = _args[2];
		// String core = NameUtiles.convertToValidCharName(path + "/"
		// + new_table_name);
		String command__ = GBUtil.getStringFromCommand(_args);
		try {
			return buildTable(path, new_table_name, user, _args, command__);
		} catch (UsageException e) {
			e.printStackTrace();
			return "Failed to create the template.  Check the arguments";
		}
	}

	// gb > create table_type stocks (String symbol, String eps, String comment,
	// boolean buy, int something.abq)
	private static String buildTable(String path, String table_name,
			String user, String[] _args, String _command) throws UsageException {

		if (_args == null || _args.length <= 0)
			new UsageException("Arguments are incorrect for creating a table. ");

		if (path == null)
			path = TEMPLATES;

		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		String[] command = _args;
		// NEED TO ADJUST THE COMMANDS (maybe)
		for (int i = 0; i < command.length; i++) {
			command[i] = fix(command[i]);
		}

		// {{ parse the command and the fields }}
		String command_str = _command;
		int index = command_str.indexOf('(');
		int linddx = command_str.indexOf(')');
		String vals = command_str.substring(index + 1, linddx);
		String table_name_vals = command_str.substring(0, index);
		table_name_vals = table_name_vals.trim();

		String[] sp = vals.split(",");
		// we should now have the types and names of each field.
		Map<String, Map<String, String>> schema = buildSchema(sp);
		String solrSite = ABProperties.get("solrSite");
		String _path = path + "/" + table_name;
		String obfuscated_name = NameUtiles.convertToValidCharName(_path);
		TMSolrServer
				.createSchema(user, solrSite, obfuscated_name, schema, true);
		TNode nlinked_node_path = GBLinkManager.createSolrLink(TEMPLATES,
				obfuscated_name);
		String linked_node_path = "";
		if (nlinked_node_path == null) {
			GB.print(" Failed to create the solr link");

		} else
			linked_node_path = nlinked_node_path.getLink();
		DBConnectionManager dbcm = GB.getConnectionManager();
		// now we save the table object
		// verify we are saving the correct link
		// verify we are saving the correct table name
		// verify the schema is correct
		TableTemplate tt = new TableTemplate();
		tt.setName(table_name);
		tt.setSchema(schema);
		tt.setTemplate_link(linked_node_path);
		Session session = dbcm.getSession();
		try {
			session.beginTransaction();
			session.save(tt);
			session.getTransaction().commit();
		} catch (Exception _e) {
			_e.printStackTrace();
			return "Failed to save the TableTemplate... : "
					+ _e.getLocalizedMessage();
		} finally {
			HBConnect.close(session);
		}
		return "Template saved.";
	}

	/**
	 * this will create a new table or a new table tempate TODO: create table is
	 * not complete.
	 * 
	 * @param _args
	 * @param _path
	 */
	public static void create(String[] _args, String _path) {

		// todo: this is where we are going to look for the table type
		String a = _args[0];
		if (a.equalsIgnoreCase("gb"))
			_args = GBUtil.remove(0, _args);

		// check the table type
		String type = _args[1];
		if (type.equalsIgnoreCase("table_type")) {
			createTableType(_args, _path);
		} else {
			// TODO: we have not created this feature yet.
		}
	}

	/**
	 * Build the Schema given the types as strings.
	 * 
	 * @param sp
	 * @return
	 */
	private static Map<String, Map<String, String>> buildSchema(String[] sp) {

		HashMap<String, Map<String, String>> _params = new HashMap<String, Map<String, String>>();
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

		Map<String, String> types = GBTypes.getSolrTypes(sp);
		Set<String> names = types.keySet();
		for (String name : names) {

			String type = types.get(name);
			HashMap<String, String> field = new HashMap<String, String>();
			field.put("fieldName", "" + name);
			field.put("sortable", "true");
			field.put("indexed", "true");
			field.put("defaultString", "");
			field.put("dataType", "" + type);
			field.put("requiredField", "true");
			_params.put(name, field);
		}
		return _params;
	}

	private static String fix(String string) {
		if (string.contains("(")) {
			int in = string.indexOf('(');
			String sub = string.substring(0, in);
			return sub;
		}
		return string;
	}

	public int count(String _schema) {
		if (GBLinkManager.isFullyQualifiedURL(_schema)) {
			server = GBLinkManager.getSolrRoot(_schema);
			_schema = GBLinkManager.getCoreLK(_schema);
		}
		return TMSolrServer.countCore(server, _schema);
	}

	public void setTypeField(String target, String field, String type) {
		String core = GB.getNodes().getCore(target);
		changeField(server, core, field, type);
	}

	public static LinkedHashMap<String, Map<String, String>> createParameters(
			ArrayList<GColumn> ps) {
		LinkedHashMap<String, Map<String, String>> params = new LinkedHashMap<String, Map<String, String>>();
		// UUID idOne = UUID.randomUUID();
		HashMap<String, String> uuidp = new HashMap<String, String>();
		uuidp.put("fieldName", "TMID");
		uuidp.put("sortable", "true");
		uuidp.put("indexed", "true");
		uuidp.put("defaultString", TMID.create());
		uuidp.put("dataType", "string");
		uuidp.put("requiredField", "true");
		params.put("TMID", uuidp);
		Date dd = new Date();
		HashMap<String, String> last_updated = new HashMap<String, String>();
		last_updated.put("fieldName", "TMID_lastUpdated");
		last_updated.put("sortable", "true");
		last_updated.put("indexed", "true");
		last_updated.put("defaultString",CurrentTimeForSolr.timeStr());
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

	public static void createDefaultCacheTable(String path) {
		GB.getNodes().mkNode(GB.DEFAULT_UER, path, "?", SourceType.TABLE);
		GB.print("Table : " + path + " has been created");
		String schema = NameUtiles.convertToValidCharName(path) + "_cache";
		TNode node = GB.getNodes().getNode(path);
		ArrayList<GColumn> gclist = new ArrayList<GColumn>();
		GColumn index = new GColumn("index", "string");
		gclist.add(index);
		GColumn docu = new GColumn("cache", "text");
		gclist.add(docu);
		LinkedHashMap<String, Map<String, String>> _params = createParameters(gclist);
		DBConnectionManager dbcm = GB.getConnectionManager();
		TableManager tmd = new TableManager(dbcm);
		NodeManager tmnode = new NodeManager(dbcm);
		tmd.build("cache", TableManager.TMSOLR, schema, "__", "1", _params,
				null);
		String link = "" + schema + ".search(*:*)";
		node.setLink(link);
		node.setLastEditedDate(new Date());
		tmnode.save(node);
		GB.print("\n\n\n Cache... " + schema
				+ " has been created and applied to : " + link + " in path : "
				+ path);
	}

	public static boolean isTable(TNode node) {

		if (node == null)
			return false;

		if (node.getNodeType() == null)
			return false;

		return node.getNodeType().equalsIgnoreCase(SourceType.TABLE.name())
				|| node.getNodeType().equalsIgnoreCase(SourceType.DB.name());
	}

}
