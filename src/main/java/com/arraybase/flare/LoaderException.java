package com.arraybase.flare;


public class LoaderException extends Exception {

	private ErrorLog log = null;

	public LoaderException() {

	}

	public LoaderException(ErrorLog el) {
		log = el;
	}

	public LoaderException(String stat_msg, String string) {
		log = new ErrorLog ( stat_msg + ":" + string );
	}

	public LoaderException(String string) {
		log = new ErrorLog ( string );
	}

	public ErrorLog getLog() {
		return log;
	}

	public void setLog(ErrorLog log) {
		this.log = log;
	}

	public ErrorLog getErrorLog() {
		return log;
	}

}
