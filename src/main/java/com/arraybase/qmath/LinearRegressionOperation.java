package com.arraybase.qmath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.arraybase.GBIO;
import com.arraybase.GBSearchIterator;
import com.arraybase.qmath.stats.SimpleLinearRegression;

public class LinearRegressionOperation extends ABOperation {


	public LinearRegressionOperation(ABOperation target, String params,
			ArrayList<String> fields) {
		super ( target, params, fields, null );
	}

	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		
		SimpleLinearRegression regression = new SimpleLinearRegression();
		ABOperation in = getItarget();
		String otarget = in.getOtarget();
		String param = getParam();
		Iterator<ArrayList<LinkedHashMap<String, Object>>> values = in.exec();
		ArrayList<String> field_names = GBIO.parseFieldNames(param);
		String xfield = field_names.get(0);
		String yfield = field_names.get(1);
		if (values instanceof GBSearchIterator ){
			GBSearchIterator it = (GBSearchIterator)values;
			regression.calculate(it, xfield, yfield);
			
		}
		return null;
	}
}