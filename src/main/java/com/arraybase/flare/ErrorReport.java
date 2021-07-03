package com.arraybase.flare;

import java.util.Properties;

public class ErrorReport implements ProcessReport{

	private String message = null;
	private Properties messages = new Properties ();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void addError(String _key, String _message) {
		messages.put(_key, _message);
	}

	public String getCore() {
		return null;
	}
	

}
