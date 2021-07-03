package com.arraybase.lac;

import com.arraybase.util.Level;
import com.arraybase.util.GBLogger;


public class SearchLACAction extends TableLACAction {

	private static GBLogger log = GBLogger.getLogger(LACAction.class);
	static {
		log.setLevel ( Level.DEBUG );
	}
	
	
	public SearchLACAction(String _target, String _data) {
		super ( _target, _data);
		log.debug( "target : "+ _target + " _data : " + _data );
	}

	public LACActionProcess exec() throws LACExecException {
		log.debug( "getTarget : "+ getTarget() + " getData (): " + getData ());
		throw new LACExecException ( );
	}

}
