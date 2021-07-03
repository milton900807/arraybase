package com.arraybase;

public class FieldTypes {

	public static String[] map(String[] types) {
		String[] req = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			String t = types[i];
			if (t.equalsIgnoreCase("i"))
				req[i] = "sint";
			else if (t.equalsIgnoreCase("d")) {
				req[i] = "sfloat";
			} else if (t.equalsIgnoreCase("f")) {
				req[i] = "sfloat";
			} else if (t.equalsIgnoreCase("b")) {
				req[i] = "boolean";
			} else if (t.equalsIgnoreCase("date")) {
				req[i] = "date";
			} else if (t.equalsIgnoreCase("s")) {
				req[i] = "string";
			} else if (t.equalsIgnoreCase("t")) {
				req[i] = "text";
			} else if (t.equalsIgnoreCase("bool")) {
				req[i] = "boolean";
			} else
				req[i] = t;
		}
		return req;
	}

}
