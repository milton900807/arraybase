package com.arraybase.db.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NameUtiles {
	
	private static SimpleDateFormat sm = new SimpleDateFormat("yyMMddHHmmss");
	
	
	public static String strip(String _user, String v) {
		if (v.startsWith(_user + "_Repository")) {
			int length = (_user + "_Repository_").length();
			String value = v.substring(length);
			return value;
		}
		return v;
	}

	/**
	 * Returns the schema name given the fully qualified schema URI
	 * 
	 * @param v
	 * @return
	 */
	public static String strip(String v) {
		int index = v.indexOf("_Repository_");
		if (index <= 0)
			return v;
		String user = v.substring(0, index);
		return strip(user, v);
	}

	/**
	 * Returns the user id given the fully qualified schmea URI
	 * 
	 * @param v
	 * @return
	 */
	public static String stripUser(String v) {
		int index = v.indexOf("_Repository_");
		String user = v.substring(0, index);
		return user;
	}

	public static String prepend(String _user, String _schema) {
		return _user + "_Repository_" + _schema;
	}

	private static NumberFormat formatter = new DecimalFormat(
			"#####################");

	/**
	 * Should we use the hash here?
	 * 
	 * @param _path
	 * @return
	 */
	public static String convertToValidCharName(String _path) {
		String path = _path;
		path = path.replace(' ', '_');
		path = path.replace('/', '_');
		path = path.replace(':', '_');
		path = path.replace('~', '_');
		path = path.replace("&nbsp;", "");
		path = path.replace("#", "");
		path = path.replace("$", "");
		return path;
	}
	
	public static String replaceCharsWithValid(String _path) {
		String path = _path;
		path = path.replace(' ', '_');
		path = path.replace('/', '_');
		path = path.replace(':', '_');
		path = path.replace('~', '_');
		path = path.replace("&nbsp;", "");
		path = path.replace("#", "");
		return path;
	}

	/**
	 * @deprecated
	 * @param _v
	 * @return
	 */
	public static String getTitle_dep(String _v) {
		int index = _v.indexOf("_Repository_");
		if (index > 0) {
			String value = _v.substring(index + 12);
			return value;
		} else
			return _v;
	}

	public static String getTarget(String _value) {
		int v = _value.lastIndexOf('.');
		if (v > 0) {
			return _value.substring(v + 1);
		} else
			return _value;
	}

	public static String getTitle(String v) {
		int index = v.indexOf("_Repository_");

		if (index > 0) {
			String user = v.substring(0, index);

			String qualified_name = strip(user, v);

			int tindex = qualified_name.lastIndexOf('.');
			if (tindex >= 0) {
				String tt = qualified_name.substring(tindex + 1);
				tt = tt.replace("_", " ");
				return tt.trim();
			}
			qualified_name = qualified_name.replace("_", " ");
			return qualified_name.trim();
		} else {
			v = v.replace('_', ' ');
			v = v.replace('.', '_');
			return v;
		}
	}

	public static String stripItem(String value, String _item) {
		if (value.contains(_item)) {
			int index = value.indexOf(_item);
			int endex = _item.length();
			String st1 = value.substring(0, index);
			String st2 = value.substring((index + endex));
			String st = st1 + st2;
			return st.trim();
		}
		return value;
	}

	public static String[] convertToValidNames(String[] titles) {
		int index = 0;
		String[] new_names = new String[titles.length];
		for (String name : titles) {
			new_names[index++] = convertToValidCharName(name);
		}
		return new_names;
	}

}
