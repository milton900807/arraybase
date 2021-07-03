package com.arraybase.shell.environment;

import com.arraybase.db.util.SourceType;
import com.arraybase.tm.tree.TNode;

public class PromptEnvFactory {

	public static PromptEnv create(TNode node) {
		String nodeType = node.getNodeType();
		if ( nodeType == null )
			return new NullNodeEnv ( );
		if ( nodeType.equals ( SourceType.TABLE.name ))
		{
			return new TableEnv ( node );
		}
		return createNullNodeEnvironment();
	}

	public static PromptEnv createNullNodeEnvironment() {
		return new NullNodeEnv ( );
	}

}
