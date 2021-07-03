package com.arraybase.shell.cmds;

import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

//public static final String PK_ID = "^setpkid\\s*\\(\\s*[A-Za-z0-9_]\\s*\\)\\s*";
/**
 * Once you set the pkid field then you are free to do incremental updates by
 * indicating a pkid value > than the largest in the index.
 */
public class PKID implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		String target = GBIO.parsePath(command);
		String[] args = GBIO.parseParams(command);
		if (args == null || args.length != 1) {
			throw new UsageException(
					"Please provide only one parameter; the Primary Key field that already exists in this table.");
		}
		
		String cmds = target + ".setabqprop(PK,"+args[0] + ")";
		SetABQValue abq = new SetABQValue();
		return abq.exec ( cmds, null );
//		String param = args[0];
//		TNode node = GB.getNodes().getNode(target);
//		NodeManager.setProperty(node, NodePropertyType.PK.name,
//				NodePropertyType.PK.name, param);
//		return "PK set";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
