package com.arraybase;

import com.arraybase.modules.*;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class GBModuleBuildFactory {

	private static GBLogger log = GBLogger
			.getLogger(GBModuleBuildFactory.class);
	private static final String SEARCH_INDEX = "searchindex";
	public static final String JDBC = "jdbc";

	public static GBModule create(String type, TNode node) {
		if (type == null)
			return null;
		if (type.equalsIgnoreCase(SEARCH_INDEX)) {
			return new SearchIndexBuilder();
		} else if (type.equalsIgnoreCase(GBModule.ABQ)) {
			return new BuildTableFromABQFile();
		}else if (type.equalsIgnoreCase(GBModule.ABQ_UPDATE)) {
			return new UpdateTableFromABQFile(node);
		}else if (type.equalsIgnoreCase(GBModule.WEBJAR)) {
			return new JUp();
		} else if ( type.equalsIgnoreCase("com.arraybase.mods.BuildTableFromABQFile")){
			return new com.arraybase.modules.BuildTableFromABQFile();
		}
	 	else if (type.equalsIgnoreCase(GBModule.ABQ_FOR_DOCUMENT_STORE)) {

			return new BuildTableFromABQFile();


		}else if ( type.equalsIgnoreCase(GBModule.ABQ_FOR_CSV)){
			return new BuildCSVFromABQFile();
		}
		try {
			String t = type.trim();
			Object ob = Class.forName(t).newInstance();
			if (ob instanceof GBModule) {
				return ((GBModule) ob);
			}
		} catch (Exception _e) {
		}
		return null;
	}
}
