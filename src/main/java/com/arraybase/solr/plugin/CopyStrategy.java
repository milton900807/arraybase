package com.arraybase.solr.plugin;

import java.util.LinkedHashMap;

public class CopyStrategy {

	private static final String TO_NUMBER = "TO_NUMBER";
	private static final String TO_INTEGER = "TO_INTEGER";
	private LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

	public Number toNumber(String fs) {
		if (fs == null)
			return Double.MAX_VALUE * -1;
		else if (fs.equalsIgnoreCase("NS")) {// not significant--- i.e. if
			// then this should be a very large
			// number
			return Double.MAX_VALUE;
		} else if (fs.equalsIgnoreCase("null")) {
			return Integer.MAX_VALUE * -1;
		}
		try {
			Double d = Double.parseDouble(fs.trim());
			return d.doubleValue();
		} catch (NumberFormatException _ns) {
			_ns.printStackTrace();
		}
		return Double.MAX_VALUE * -1;
	}

	public boolean hasStrategy(String f_name) {
		String value = map.get(f_name);
		return value != null;
	}

	public Object executed(String f_name, String _value) {
		String strat = map.get(f_name);
		if (strat != null) {
			if (strat.equals(TO_NUMBER))
				return toNumber(_value);
			else if (strat.equals(TO_INTEGER)) {
				return toInteger(_value);
			}
		}
		return _value;
	}

	private Object toInteger(String _value) {
		try {
			Double dd = Double.parseDouble(_value);
			Integer it = dd.intValue();
			return it;
		} catch (NumberFormatException ec) {
			ec.printStackTrace();
		}
		return null;
	}

	public void set(String field_name, String to_type) {
		if (to_type.equalsIgnoreCase("float")
				|| to_type.equalsIgnoreCase("double")
				|| to_type.equalsIgnoreCase("sfloat")
				|| to_type.equalsIgnoreCase("sdouble")
				|| to_type.equalsIgnoreCase("number"))
			map.put(field_name, TO_NUMBER);
		else if (to_type.equalsIgnoreCase("int")
				|| to_type.equalsIgnoreCase("sint")) {
			map.put(field_name, TO_INTEGER);
		}
	}
}
