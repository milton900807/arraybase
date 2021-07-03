package com.arraybase.var;

import com.arraybase.GBV;
import com.arraybase.tm.GRow;

/**
 * This is an object that encapsulates the row 
 * 
 * @author milton
 * 
 */
public class RowGBV extends GBV {
	private String[] types = null;

	public RowGBV(String... types) {
		super(new GRow());
		this.types = types;
	}

}
