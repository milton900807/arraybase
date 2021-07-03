package com.arraybase.modules;

import com.arraybase.GBModule;

public class InstallFactory {
	
	public final static String SEARCHINDEX = "SEARCHINDEX";
	public final static String GB = "GB";

	public static GBModule create(String type) {
		
		if ( type.equalsIgnoreCase(SEARCHINDEX))
		{
			return new SearchIndexInstaller ( );
		} else if ( type.equalsIgnoreCase(GB))
		{
			return new GBInstaller ( );
		}
		return null;
	}

}
