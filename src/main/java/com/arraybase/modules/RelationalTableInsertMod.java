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

public class RelationalTableInsertMod implements GBModule {

	private static GBLogger log = GBLogger
			.getLogger(RelationalTableInsertMod.class);
	private GBRelationalDB rdb = null;

	public RelationalTableInsertMod(GBRelationalDB _rdb) {
		rdb = _rdb;
	}

	/**
	 *  most often this is used for passing in KEYS, PATH AND MAP
	 */
	public void exec(Map<String, Object> l) throws UsageException{
		String[] exported_values = (String[]) l.get(EXPORT);
		String path = (String) l.get(PATH);
		ArrayList<WhereClause> wherehm = (ArrayList<WhereClause>) l.get(WHERE_CLAUSE);
		String query = (String)l.get(QUERY);
		
		try {
			rdb.insertDBQueryIntoGBNode(query, exported_values, path, wherehm);
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
			log.error(" Path :  " + path + " was not found ");
		} catch (NodeWrongTypeException e) {
			e.printStackTrace();
		} catch (DBProcessFailedException e) {
			e.printStackTrace();
		}
	}

	public String getModName() {
		return "Relational Mapping Insert Module";
	}

	
	public void exec(List<String> l) throws UsageException {
		throw new UsageException("This module is configured incorrectly. ");
	}

}
