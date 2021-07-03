package com.arraybase.modules;

import java.util.ArrayList;

public class JoinModule {

	public void exec(ArrayList<String> a) {
		
		// first let's count the on statements
		int count = count ( a, "on");
//		 java -jar gb.jar join /gne/totable/t5 as t5 /gne/totable/t6 as t6 on t5.wdir=t6.wdir 
		for ( int i =0; i < count; i++){
			
			
			
		}
	}

	private int count(ArrayList<String> _list, String _key) {
		int count = 0;
		for ( String l : _list ){
			if ( l.equalsIgnoreCase(_key)){
				count++;
			}
		}
		return count;
	}
}
