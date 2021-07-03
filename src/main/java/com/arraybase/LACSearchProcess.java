package com.arraybase;

import java.util.ArrayList;

import com.arraybase.lac.LACActionProcess;
import com.arraybase.lac.SolrServerUtil;
import com.arraybase.tm.GRow;

public class LACSearchProcess extends LACActionProcess<ArrayList<GRow>> {

	private ArrayList<GRow> rows = null;

	public LACSearchProcess(String _message) {
		super(_message);
	}

	public ArrayList<GRow> getValues() {
		return rows;
	}

	public void setResults(ArrayList<GRow> _rows) {
		rows = _rows;
	}

}
