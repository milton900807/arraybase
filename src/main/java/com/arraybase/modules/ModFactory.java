package com.arraybase.modules;

import com.arraybase.GBModule;

public class ModFactory {

	public final static String TYPE = "type";

	public static GBModule create(String type) {
		if (type.equalsIgnoreCase(TYPE)) {
			return new GBSetType();
		}
		return null;
	}

}
