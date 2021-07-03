package com.arraybase.lac;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.util.GBLogger;

public class LacFactory {

	static GBLogger log = GBLogger.getLogger(LacFactory.class);

	public static String create(String _type, String _data) {
		if (_type.equals("raw_file")) {
			return "file.report (" + _data + ")";
		}
		return null;
	}

	public static LACReference getLACReference(String link,
			DBConnectionManager db) {

		if (link == null)
			return null;
		String[] lac = LAC.parse(link);
		log.install("Loading the reference for : " + link);
		// if we don't have target action data... then it's not a valid link
		if (lac == null || lac.length != 3)
			return null;

		if (link.startsWith(" com.tissuematch.tm3.mylib.TMLibrary")) { // this
																		// is a
																		// legacy
																		// table
																		// lac
			TableLacReference tn = new TableLacReference();
			try {
				tn.load(link, db);
			} catch (LoadFailedException e) {
				e.printStackTrace();
			}
			return tn;
		} else if (lac[0].equalsIgnoreCase("file")) { // this is a file lac
			FileLacReference filelac = new FileLacReference();
			try {
				filelac.load(link, db);
			} catch (LoadFailedException e) {
				e.printStackTrace();
			}
			return filelac;
		} else if (lac[1].equalsIgnoreCase("load")) // this is a solr lac
		{
			TableLacReference tn = new TableLacReference();
			try {
				tn.load(link, db);
			} catch (LoadFailedException e) {
				e.printStackTrace();
			}
			return tn;
		}
		return null;
	}

}
