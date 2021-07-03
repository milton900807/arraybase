package com.arraybase.shell.cmds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.lac.LAC;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.tm.tree.TPath;
import com.arraybase.util.ABProperties;

public class cp implements GBPlugin {
	
	
	static final SimpleDateFormat sdf = new SimpleDateFormat ( "yyMMddHHmmss");
	
	
	
	

	public String exec(String command, String variable_key) {
		String target = GBIO.parsePath(command);
		String[] sp = command.split("\\s+");
		if (sp.length != 3) {
			GB.print(" Please provide two arguments... if you really want to move something from one location (1) to another (2) ");
			return "cp command";
		}
		String from = GB.absolute(sp[1].trim());
		String to = GB.absolute(sp[2].trim());
		GB.copy ( from, to );

		return "cp complete";
	}


	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
