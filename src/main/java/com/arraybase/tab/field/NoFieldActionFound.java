package com.arraybase.tab.field;

public class NoFieldActionFound extends Exception {

	private String action = null;
	
	public NoFieldActionFound ( String _action ){
		action = _action;
	}
	public String toString ( ){
		return " Failed to find the action : "+ action;
	}
	
	
}
