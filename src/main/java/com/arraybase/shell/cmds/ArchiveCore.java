package com.arraybase.shell.cmds;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.flare.SolrCallException;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.lac.LAC;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.ABProperties;

public class ArchiveCore implements GBPlugin {

	static final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

	public String exec(String command, String variable_key) {
		String target = GBIO.parsePath(command);
		String[] sp = command.split("\\s+");
		if (sp.length != 2) {
			GB.print(" Please enter the table name you want to archive.");
			return "archive command";
		}
		String table = GB.absolute(sp[1].trim());
		GB.print("\tArchiving \t\t " + table);
		NodeManager manager = new NodeManager();
		GBNodes nodes = GB.getNodes();
		TNode toNode = manager.getNode(table);
		String lac = toNode.getLink();
		String corename = LAC.getTarget(lac);

		String url = ABProperties.getSolrURL();
		LinkedHashMap<String, String> lm = new LinkedHashMap<String, String>();
		lm.put("core", corename);
		try {
			TMSolrServer.callSolrAction(url, "archive", lm);
		} catch (SolrCallException e) {
			e.printStackTrace();
		}
		return "Archive complete.";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
