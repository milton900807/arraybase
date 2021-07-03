package com.arraybase;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Stack;

public class GBHistory {
	private Stack<String> history = new Stack<String>();
	
	
	public void push ( String cmd ){
		history.push(cmd);
	}
	public String pop ( ){
		return history.pop();
	}
	public void print ()
	{
		
		
	}

}
