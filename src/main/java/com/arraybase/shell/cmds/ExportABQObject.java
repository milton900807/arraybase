package com.arraybase.shell.cmds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.google.gson.Gson;

public class ExportABQObject implements GBPlugin {

	
	public String exec(String command, String variable_key)
			throws UsageException {
		
		String filename = null;
		String[] args = GBIO.parseParams(command);
		if ( args == null )
		{
			GB.print ( " Please provide a file name as a parameter . ");
			return "";
		}
		if ( args.length == 1 )
			filename = args[0];
		if ( filename == null )
			filename = "export.abq";
		
		String target = GBIO.parsePath(command);
		// this will update an index. n
		// node.refresh(where MTID > node.max(MTID))
		if (target == null) {
			GB.print("Failed to find the target : " + target);
			return null;
		}
		TNode node = GB.getNodes().getNode(target);
		long node_id = node.getNode_id();
		// {{ 2. PULL THE PROPERTIES FROM THIS NODE }}
		NodeProperty nps = NodeManager.getNodeProperty(node_id, NodeProperty.NODE_GENERATOR);
		String json = nps.getProperty();
		Gson g = new Gson();
		Map installer = g.fromJson(json, Map.class);
		if (installer == null) {
			GB.print("Configuration error.... it looks like there isn't a configuration available to permit you to reload this node.  ");
			return "";
		}
		
		
		String local_path = GB.lpwd();
		File f = new File ( local_path, filename );
		try {
			PrintStream pr = new PrintStream ( f );
			Set<String> keys = installer.keySet();
			for ( String key : keys ){
				GB.print( key + "=" + installer.get(key) );
				pr.println ( key +"=" + installer.get(key));
			}
			pr.flush();
			pr.close();
			GB.print ( "Export to " + f.getAbsolutePath() + "\n \tcomplete.");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		return "Export complete";
		
	}

	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
