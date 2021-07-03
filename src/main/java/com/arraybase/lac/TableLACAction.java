package com.arraybase.lac;

import java.util.ArrayList;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.tm.GRow;
import com.arraybase.tm.GResults;
import com.arraybase.tm.tables.TMTableSettings;
import com.arraybase.tm.tables.TTable;
import com.arraybase.tm.TableManager;
import com.arraybase.util.Level;
import com.arraybase.util.GBLogger;

public class TableLACAction implements LACAction {

	private static GBLogger log = GBLogger.getLogger(LACAction.class);
	static {
		log.setLevel(Level.DEBUG);
	}

	private String target = null;
	private String action = null;
	private String data = null;

	public TableLACAction(String _target, String _data) {
		target = _target;
		data = _data;
		log.debug("target : " + _target + " _data : " + _data);
	}

	public LACActionProcess exec() throws LACExecException {
		log.debug("target : " + target + " _data : " + data);
		throw new LACExecException();
	}

	public String getTarget() {
		return target;
	}

	public String getData() {
		return data;
	}
	public String getLAC ()
	{
		return target + "." + action + "(" + data + ")";
	}


	// public static String getCore(String _lac) {
	//
	// // if the link is the old way... do this.
	// String[] lac_ = LAC.parse(_lac);
	// if (lac_[0].startsWith("com.tissuematch.tm3.mylib.TMLibraryItem")) {
	// String lib_data = lac_[2].trim();
	// Integer it = Integer.parseInt(lib_data);
	// TTable item = getLibraries(it);
	//
	// String name = item.getTitle();
	// String user = item.getUser();
	// String core = NameUtiles.prepend(user, name);
	// return core;
	// } else// this is the new way.
	// {
	// return lac_[0];
	// }
	// }

	private static TTable deprecated_getLibraries(Integer itemID) {
		// try {
		// Session hibernateSession = HBConnect.getSession();
		// hibernateSession.beginTransaction();
		// Criteria criteria = hibernateSession.createCriteria(TTable.class);
		// criteria.add(Restrictions.eq("itemID", itemID));
		// List list = criteria.list();
		// if (list.size() > 0) {
		// TTable tl = (TTable) list.get(0);
		// tl = HibernateToCoreJava.convert(tl);
		// hibernateSession.close();
		// return tl;
		// }
		// return null;
		// } catch (Exception _e) {
		// HBConnect.close();
		// _e.printStackTrace();
		// } finally {
		// HBConnect.close();
		// }
		return null;
	}

	/**
	 * The default table action is to do a search
	 * 
	 * @return
	 */
	public ArrayList<GRow> search(String _searchString, TMTableSettings settings) {
		DBConnectionManager db = new DBConnectionManager();
		TableManager service = new TableManager(db);
		String table_url = target;
		String _schema = table_url;
		int _start = 0;
		int _rows = 1000;
		GResults r = service.search(_schema, _searchString, _start, _rows,
				settings);
		return r.getValues();
	}

}
