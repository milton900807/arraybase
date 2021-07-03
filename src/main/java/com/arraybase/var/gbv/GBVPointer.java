package com.arraybase.var.gbv;

import com.arraybase.GBV;
import com.arraybase.search.SearchPointer;

public class GBVPointer extends GBV {

	public GBVPointer(SearchPointer searchptr) {
		super(searchptr);
	}

	public String toString() {
		SearchPointer sh = (SearchPointer) get();
		return " " + sh.getPath() + "" + sh.getFieldsAsArray() + ".search("
				+ sh.getSearchString() + ")[" + sh.getStart() + ", "
				+ sh.getMax() + "]";

	}

}
