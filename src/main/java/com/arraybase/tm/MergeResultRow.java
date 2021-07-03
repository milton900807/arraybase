package com.arraybase.tm;

import java.util.Date;
import java.util.HashMap;

public class MergeResultRow extends GRow {

	public MergeResultRow() {

	}

	public void add(String key, Object _value) {
		HashMap data = getData();
		Object value = data.get(key);
		if (key.equalsIgnoreCase("TMID_lastUpdated")) {
			data.put(key, new Date());
		} else {
			if (value != null)
				append(key, _value);
			else
				super.add(key, _value);
		}
	}

	private void append(String key, Object _value) {

		HashMap hvalue = getData();
		Object value = hvalue.get(key);
		HashMap<String, String> data_types = getDataTypes();
		String type = data_types.get(key);
		if (type.equalsIgnoreCase("date")) {
			hvalue.put(key, new Date());
		} else if (type.equalsIgnoreCase("text")
				|| type.equalsIgnoreCase("string")
				|| type.equalsIgnoreCase("char")) {
			hvalue.put(key, value + "||" + _value);
		} else if (type.equalsIgnoreCase("int")
				|| type.equalsIgnoreCase("double")
				|| type.equalsIgnoreCase("float")
				|| type.equalsIgnoreCase("sint")
				|| type.equalsIgnoreCase("sdouble")
				|| type.equalsIgnoreCase("sfloat")
				|| type.equalsIgnoreCase("long")
				|| type.equalsIgnoreCase("slong")) {
			hvalue.put(key, value);// new Long(-99999));
		}
		setData(hvalue);
	}

}
