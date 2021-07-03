package com.arraybase.flare;

public enum XLSTypes {
	STRING("string"), SINT("sint"), INTEGER("Integer"), INT("int"), DOUBLE(
			"double"), SDOUBLE("sdouble"), TEXT("text"), DATE("Date"), SFLOAT(
			"sfloat"), FLOAT("float"), BOOLEAN("boolean");

	private String name = "string";

	XLSTypes(String _type) {
		name = _type;
	}

	public boolean isType(String _value) {
		return _value.equalsIgnoreCase(name);
	}

	public static boolean isAType(String _type) {
		for (XLSTypes t : XLSTypes.values()) {
			if (t.isType(_type))
				return true;

		}
		return false;
	}

}
