package com.arraybase.shell.cmds.operator;

public class ReplaceFunction implements FieldOperator {

	
	// target.search(field:something).set(name,[name].replace(wggg,))
	public String exec(String _v, String params) {
		return _v.replace(_v, params);
	}

}
