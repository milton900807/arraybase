package com.arraybase.lang;

public class VarRef {

	private String referenceName = null;
	private String connectMethod = null;

	public VarRef() {
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getConnectMethod() {
		return connectMethod;
	}

	public void setConnectMethod(String connectMethod) {
		this.connectMethod = connectMethod;
	}
}
