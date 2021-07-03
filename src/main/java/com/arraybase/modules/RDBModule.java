package com.arraybase.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.arraybase.GBModule;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.db.GBRelationalDB;
import com.arraybase.flare.DBProcessFailedException;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.tm.WhereClause;
import com.arraybase.util.GBLogger;

/**
 * Module to load a databasem
 * 
 * @author milton
 * 
 */
public class RDBModule implements GBModule {

	private GBLogger log = GBLogger.getLogger(RDBModule.class);
	private GBRelationalDB db = null;

	public RDBModule(GBRelationalDB gb) {
		db = gb;
	}

	// param_map.put(GBModule.KEYS, exported_values);
	// param_map.put(GBModule.WHERE_CLAUSE, wcl);
	// param_map.put(GBModule.PATH, path);
	public void exec(List<String> l) throws UsageException {
		throw new UsageException(
				" This method is not implemented.  Please use exec (map)");

	}
	// param_map.put(GBModule.KEYS, exported_values);
	// param_map.put(GBModule.WHERE_CLAUSE, wcl);
	// param_map.put(GBModule.PATH, path);
	public void exec(Map<String, Object> l) throws UsageException {
		String[] exported = (String[]) l.get(GBModule.KEYS);
		ArrayList<WhereClause> list = (ArrayList<WhereClause>) l
				.get(GBModule.WHERE_CLAUSE);
		String path = (String) l.get(GBModule.PATH);
		String query = (String) l.get(GBModule.QUERY);
		try {
			db.insertDBQueryIntoGBNode(query, exported, path, list);
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		} catch (NodeWrongTypeException e) {
			e.printStackTrace();
		} catch (DBProcessFailedException e) {
			e.printStackTrace();
		}
	}

	public String getModName() {
		return "RDBModule";
	}
}
