package com.arraybase.qmath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.arraybase.GBV;
import com.arraybase.shell.cmds.MEANValue;
import com.arraybase.shell.cmds.SumValue;

public class sumFunction extends ABFunction {

	public sumFunction() {
		super("sum function");
	}

	public float calculateValue() throws CalculationNotAvailableException,
			CalculationFailedException {
		GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> res = getSearch();
		if (res == null)
			throw new CalculationNotAvailableException(
					"Calculation failed: search component is not available.");
		SumValue meanvalue = new SumValue();
		GBV value = meanvalue.execGBVIn(null, res);
		
		if (value != null && value instanceof NumberVar) {
			NumberVar num = (NumberVar) value;
			Double d = num.get();
			if (d != null)
				return d.floatValue();
		}
		throw new CalculationFailedException(
				"Mean not found. Search component is available but still.. failed");
	}

}
