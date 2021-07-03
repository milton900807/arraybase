package com.arraybase.flare;

import java.util.LinkedHashMap;

public class ErrorLog {

	private LinkedHashMap<String, String> log = new LinkedHashMap<String, String>();
	private String msg = "";

	public ErrorLog() {

	}
	public ErrorLog(String stat_msg) {
		msg = stat_msg;
	}

	public LinkedHashMap<String, String> getLog() {
		return log;
	}

	public void setLog(LinkedHashMap<String, String> log) {
		this.log = log;
	}

	public int count() {
		return log.size();
	}

	public void add(int r, int columnIndex, String _error, String _log) {
		
		msg += r + "," + columnIndex + ":" + _error +";\n ";
		log.put(r + "," + columnIndex + ":" + _error, _log);
	}

	public void setMsg(String _msg) {
		msg = _msg;
	}

	public String getMsg() {
		return msg;
	}
	
	public String toString ()
	{
		return msg;
	}
	

}
