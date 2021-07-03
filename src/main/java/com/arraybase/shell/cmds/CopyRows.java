package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.TMID;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;

public class CopyRows implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		
		
		int index = command.indexOf('(');
		int index2 = command.indexOf(')');
		String temp = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		// String tt = GB.pwd() + "/" + temp2.trim();
		String search_string = temp;

		int doti = command.indexOf('.');
		String path = command.substring(0, doti);
		path = GB.pwd() + "/" + path.trim();
		temp2 = temp2.trim();
		String[] v = temp.split(",");
		if (v == null || v.length <= 0) {
			v = new String[1];
			v[0] = temp;
		}
		
		GB.print ( " Warning... currently this method will only duplicate at most the first 100 rows of the query... ");
		ABTable table = new ABTable ( path );
		ABaseResults res = table.search ( search_string, null, 0, 100);
		
		ArrayList<GRow> rows = res.getValues();		
		ArrayList<GRow> newrows = new ArrayList<GRow> ();		
		for ( GRow ro : rows ){
			
			HashMap data = ro.getData();
			LinkedHashMap ndata = new LinkedHashMap();
			Set keys = data.keySet();
			for ( Object key :  keys){
				Object value = data.get ( key );
				ndata.put ( key, value);
			}
			String t = TMID.create();
			Date lastUpdate = new Date ();
			ndata.remove("TMID_lastUpdated");
			ndata.remove( "TMID");
			table.append(ndata);
		}
		
		return "Complete.";
	}
	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
