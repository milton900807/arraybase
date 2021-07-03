package com.arraybase.qmath;

public class ABTarget extends ABOperation{
	private String target = null;
	public ABTarget(String sub) {
		this.target = sub;
		setOtarget(target);
	}
	public String getPointer ()
	{
		return target;
	}
	
	public String toString ()
	{
		return target;
	}
}
