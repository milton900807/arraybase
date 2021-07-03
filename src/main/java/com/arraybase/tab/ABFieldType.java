package com.arraybase.tab;

public enum ABFieldType {

	SINT("sint"), STRING("string"), STRING_CI("string_ci"), DOUBLE("sdouble");

	String type = null;

	ABFieldType(String _type) {
		type = _type;
	}

	public String getName() {
		return type;
	}

	public String getType() {
		return type;
	}

	public static boolean isReserved(String field) {
		return !(!field.equalsIgnoreCase("TMID")
				&& (!field.equals("_version_")
				&& (!field.equals("TMID_lastUpdated")) && (!field
				.endsWith("__900807"))));
	}

	public static ABFieldType getType ( String type ){
		if ( type.equalsIgnoreCase("string")){
			return STRING;
		}else if ( type.equalsIgnoreCase("string_ci")){
			return STRING_CI;
		}else if ( type.equalsIgnoreCase( "sdouble")){
			return DOUBLE;
		}else if ( type.equalsIgnoreCase( "sint")){
			return SINT;
		}else
			return STRING_CI;
	}



	public static String simple(String _t) {
		_t = _t.toLowerCase();
		if (_t.equals(SINT.name())) {
			return "i";
		} else	if (_t.equals(STRING_CI.name())) {
			return "sc";
		}else
			return _t.substring(0, 1);
	}

}
