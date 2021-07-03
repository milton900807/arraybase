package com.arraybase.qmath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.arraybase.GB;
import com.arraybase.GBSearch;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;

public class IQROperation extends ABOperation {

	public IQROperation(ABOperation input, String params,
			ArrayList<String> fields) {
		super(input, params, fields, null);
	}

	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		// Iterator<ArrayList<LinkedHashMap<String, Object>>> exec (){
		// get the output of the input operation
		String path = getItarget().getOtarget();
		List<String> fields = getFields();
		double q25 = getQuartile(path,
				fields.toArray(new String[fields.size()]), 0.25);
		double q75 = getQuartile(path,
				fields.toArray(new String[fields.size()]), 0.75);
		String s = "[q25] [q75]";
		s += "\n" + "[" + q25 + "] [" + q75 + "]";
		GB.print(s);
		return null;
	}

	public double getQuartile(String path, String[] ff, double k) {
		double qValue = 0;
		try {
			ABaseResults res = GBSearch.select(path, ff, "*:*", 0, 1, ""
					+ ff[0] + " desc");
			int n = res.getTotalHits() + 1;
			double first_index = (k * n);
			GB.print(" K  " + k + " n=" + n + " first_index: " + first_index);
			double remainder = first_index % 1;
			GB.print(" Remainder: " + remainder);
			int findex = (int) Math.floor(first_index) - 1;
			GB.print(" Floored index: " + findex);
			if (remainder == 0) {
				ABaseResults firstq = GBSearch.select(path, ff, "*:*", findex,
						1, "" + ff[0] + " asc");
				ArrayList<GRow> row = firstq.getValues();
				GRow r = row.get(0);
				HashMap data = r.getData();
				Number d = (Number) data.get(ff[0]);
				qValue = d.doubleValue();
			} else {
				ABaseResults firstq = GBSearch.select(path, ff, "*:*", findex,
						2, "" + ff[0] + " asc");
				ArrayList<GRow> row = firstq.getValues();
				GRow r = row.get(0);
				GRow r1 = row.get(1);
				HashMap data = r.getData();
				HashMap data1 = r1.getData();
				Number d = (Number) data.get(ff[0]);
				Number d1 = (Number) data.get(ff[0]);
				qValue = d.doubleValue()
						+ ((d1.doubleValue() - d.doubleValue()) * remainder);
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return qValue;
	}

}
