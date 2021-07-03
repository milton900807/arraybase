package com.arraybase.lac;

import java.io.Serializable;

import com.arraybase.GBLinkManager;

public class LAC implements Serializable {
	public static final String CREATE = "create";

	public static String getLink(String _target, String _action, String _data) {
		// if (_node_type.equalsIgnoreCase(TMNodeType.DEFAULT.getName())) {
		String uri = "" + _target + "." + _action + "(" + _data + ")";
		return uri;
	}

	public static String[] parse(String value) {

		int st2 = value.indexOf('(');
		if (st2 <= 0)
			return null;
		String t = value.substring(0, st2);
		int st = t.lastIndexOf('.');
		String target = value.substring(0, st);
		target = target.trim();
		int st3 = value.lastIndexOf(')');

		String action = t.substring(st + 1);
		if (action != null)
			action = action.trim();
		String data = value.substring(st2 + 1, st3);
		if (data != null)
			data = data.trim();
		String[] url = new String[3];
		url[0] = target;
		url[1] = action;
		url[2] = data;
		return url;
	}

	public static String getTarget(String lac) {
		if (GBLinkManager.isFullyQualifiedURL(lac)) {
			return lac;
		}
		String[] l = parse(lac);
		if ( l == null )
			return null;
		return l[0];
	}

	public static String getData(String lac) {
		String[] l = parse(lac);
		if ( l == null )
			return null;
		return l[2];
	}

	public static String parseLastTarget(String _schema) {
		if (_schema != null && _schema.contains(".")) {
			int last_item_index = _schema.lastIndexOf(".");
			if (last_item_index > 0) {
				String item = _schema.substring(last_item_index + 1);
				return item;
			}
		}
		return _schema;
	}

	public static String construct(String _target, String _action, String _data) {
		return _target + "." + _action + "(" + _data + ")";
	}

}
