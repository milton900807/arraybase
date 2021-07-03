package com.arraybase.var.gbv;

import java.util.ArrayList;
import java.util.HashMap;

import com.arraybase.GBV;
import com.arraybase.tm.GRow;

public class GBVTable extends GBV {

	public GBVTable() {
		super(new ArrayList<GRow>());
	}

	public void add(GRow row) {
		ArrayList t = (ArrayList<GRow>) get();
		t.add(row);
	}

	public ArrayList<GRow> getRows() {
		return (ArrayList<GRow>) get();
	}

	public String toString() {
		String t = "\n\n";
		ArrayList<GRow> rows = (ArrayList<GRow>) get();
		for (GRow g : rows) {
			String rs = "";
			HashMap objects = g.getData();
			ArrayList<String> keys = g.getOrder();
			for (String o : keys) {
				rs += objects.get(o) + "\t";
			}
			t += rs + "\n";
		}
		return t;
	}

}
