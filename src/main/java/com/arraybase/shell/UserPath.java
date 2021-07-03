package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.shell.environment.PromptEnv;
import com.arraybase.shell.environment.PromptEnvFactory;
import com.arraybase.tm.tree.TNode;

public class UserPath {
	
	public final String TABLE = "TABLE";
	public final String TABLE_FIELD = "TABLE_FIELD";
	public final String TABLE_CELL = "TABLE_CELL";
	
	private String path = "";
	private String type = null;
	private PromptEnv userEnvironment = null;
	
	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		verifyEnvironment();
	}
	

	private void verifyEnvironment() {
		GBNodes nodes = GB.getNodes();
		String[] re = GBNodes.listPath(path);
		TNode node = nodes.getNode(path);
		if ( re == null )
			return;
		for ( String r : re ){
			if ( r.equalsIgnoreCase(".ab.config")){
				// load the configuration
				

			}
		}
		if ( node != null )
			userEnvironment = PromptEnvFactory.create ( node );
		else
			userEnvironment = PromptEnvFactory.createNullNodeEnvironment ();

		GB.print("Environment scope changed: " + userEnvironment.toString());
		userEnvironment.updateCommandCenter();
	}

}
