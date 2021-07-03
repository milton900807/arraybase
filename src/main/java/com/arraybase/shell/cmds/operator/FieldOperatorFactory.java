package com.arraybase.shell.cmds.operator;

public class FieldOperatorFactory {

	public static FieldOperator create(String function) {
		
		if ( function.equalsIgnoreCase("replace") || function.equalsIgnoreCase("replacewith"))
		{
			return new ReplaceFunction ();
		}
		return null;
	}

}
