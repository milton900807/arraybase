package com.arraybase.lac;

public class LACExecException extends Exception {

	public LACExecException(String msg) {
		super(msg);
	}

	public LACExecException() {
		super("Failed to execute LAC");
	}

}
