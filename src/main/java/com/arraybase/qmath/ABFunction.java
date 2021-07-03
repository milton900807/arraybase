package com.arraybase.qmath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;


import com.arraybase.GBV;

public class ABFunction {

	public static final String MEAN = "mean";
	private GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> results = null;
	private String name = null;
	
	public ABFunction ( String _name ){
		this.name = _name;
	}
	
	public void setSearch(GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> results) {
		this.results = results;
	}
	public GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> getSearch ()
	{
		return results;
	}
	
	public float calculateValue () throws CalculationNotAvailableException, CalculationFailedException {
	
		throw new CalculationNotAvailableException ("Calculate to a single floating point value is not supported with this function object: " + name);
	}
	

}
