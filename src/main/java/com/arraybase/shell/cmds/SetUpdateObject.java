package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.IOUTILs;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SetUpdateObject implements GBPlugin {

	
	
	// mytable.setupdateobject(mymainclass,myjarfile)
	public String exec(String command, String variable_key)
			throws UsageException {

		// {{ 1. GET THE TARGET OBJECT }}
		String[] args = GBIO.parseParams(command);
		String target = GBIO.parsePath(command);
		// this will update an index. n
		// node.refresh(where MTID > node.max(MTID))
		if (target == null) {
			GB.print("Failed to find the target : " + target);
			return null;
		}
		TNode node = GB.getNodes().getNode(target);
		if ( node == null )
		{
			GB.print("Node not found for : " + target);
			return null;
		}
		long node_id = node.getNode_id();
		
		// if we do not have a start class then we will 
		// assume one is defined in the meta-inf of the jar file.
		// so we will set the main class as undefined here.
		if (args!=null && args.length==1){
			String object = args[0];
			args = new String[2];
			args[0]="undefined";
			args[1]=object;
		}
		// now find the jar object
		String jarobject = args[1];
		String classobject = args[0];
		if (jarobject != null && classobject != null) {
			jarobject = jarobject.trim();
			classobject = classobject.trim();
		} else {
			GB.print(" Incorrect arguments for this method.  Please provide:  (fully_qualified_class, local_jarfile)");
			return "Failed to load the update object from a jar file";
		}
		try {
			// read the jar file into a byte array
			File file = new File(jarobject);
			byte[] fileData = new byte[(int) file.length()];

			if ( !file.exists() ){
				GB.print ( "Failed to find the file : "+ file.getAbsolutePath() + " on the file system... you can change directories by using lcd.");
				return "Failed to find the file : "+ file.getAbsolutePath();
			}
			
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			try {
				dis.readFully(fileData);
			} finally {
				IOUTILs.closeResource(dis);
			}
		
			NodeProperty updateObject = NodeManager.getNodeProperty(node.getNode_id(), NodePropertyType.UPDATE.name);
			if ( updateObject != null )
			{
				updateObject.setFile(fileData);
			}else
			{
				updateObject = new NodeProperty();
				updateObject.setNode_id(node_id);
				updateObject.setName("UpdateByObject");
				updateObject.setProperty(classobject);
				updateObject.setFile(fileData);
				updateObject.setType(NodePropertyType.UPDATE.name);
			}
			
			NodeManager.saveNodeProperty(updateObject);
		
		} catch (IOException _e) {
			_e.printStackTrace();
		}
		return "Node Update complete.";
	}

	
	public GBV execGBVIn(String cmd, GBV input) {
		// TODO Auto-generated method stub
		return null;
	}

}
