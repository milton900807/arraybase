package com.arraybase.qmath;

import java.util.ArrayList;

public class SearchOperation extends ABOperation {

	
	public SearchOperation(ABOperation input, String params, ArrayList<String> fields) {
		super(input, params, fields, null);
		// the default for the search is just to reference the original op
		setOtarget(input.getOtarget());
	}

}
