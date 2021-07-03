package com.arraybase.tm;

public class TMDBConnection {

	private String url = null;
	private String usr = null;
	private String pss = null;
	private String dvr = null;
	private String sql = null;

	public TMDBConnection(String _url, String _usr, String _pss, String _dvr) {
		url = _url;
		usr = _usr;
		pss = _pss;
		dvr = _dvr;
	}

	public TMDBConnection() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsr() {
		return usr;
	}

	public void setUsr(String usr) {
		this.usr = usr;
	}

	public String getPss() {
		return pss;
	}

	public void setPss(String pss) {
		this.pss = pss;
	}

	public String getDvr() {
		return dvr;
	}

	public void setDvr(String dvr) {
		this.dvr = dvr;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void configure() {

	}
}
