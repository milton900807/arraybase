package com.arraybase.flare;

public class SuccessReport implements ProcessReport {

	String corename = null;

	public SuccessReport(String _corename) {
		corename = _corename;
	}

	public String getCore() {
		return corename;
	}

}
