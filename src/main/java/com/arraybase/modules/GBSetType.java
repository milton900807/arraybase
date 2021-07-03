package com.arraybase.modules;

import java.util.List;
import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBModule;
import com.arraybase.GBNodes;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class GBSetType implements GBModule {

	private static GBLogger log = GBLogger.getLogger(GBSetType.class);
	static {
		log.setLevel(GBLogger.DEBUG);
	}

	public String getModName() {
		return "GB Set Type";
	}
	public void exec(Map<String, Object> l) throws UsageException
	{
		throw new UsageException ( "This is not implemented. ");
	}

	public void exec(List<String> l) throws UsageException {
		// example:
		// java -jar gb.jar set type /gne/research/test/jeff1.MWD int
		String path = l.get(0);
		String new_type = l.get(1);
		log.debug("\t\t Type Node: " + path);
		log.debug("\t\t Converting to : " + new_type);
		GBNodes nodes = GB.getNodes();
		TNode node = nodes.getNode(path);
		nodes.changeType(node, new_type);
		// if ( node == null )
		// log.debug ( " Nod is null ");
		// else
		// log.debug ( " Node type found is " + node.getNodeType());
		// throw new UsageException(" Input is not formatted correctly");
	}

}
