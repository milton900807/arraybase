package com.arraybase.modules;

import java.util.Properties;

import com.arraybase.GBModule;
import com.arraybase.db.GBRelationalDB;


/**
 *  For some reason this is not added to the hub build.
 * @author jmilton
 *
 */
public class InsertFactory {

	public final static String RELATIONAL = "relational";
	public final static String INDEXED = "indexed";
	public final static String FILE = "FILE";

	/**
	 * This will create a factory of a particular type
	 * 
	 * @param type
	 * @param p
	 * @return
	 */
	public static GBModule create(String type, Properties p) {
		if (type.equalsIgnoreCase(RELATIONAL)) {
			GBRelationalDB rdb = new GBRelationalDB(p);
			return new RelationalTableInsertMod(rdb);
		} else if (type.equalsIgnoreCase(INDEXED)) {
			GBSolrDB indexedDB = new GBSolrDB (p);
			return indexedDB;
		} else
			return null;
	}

}
