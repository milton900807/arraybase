package com.arraybase.qmath;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.inference.TTest;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBUtil;

public class TTestOperation extends ABOperation {
    private final static DecimalFormat formatter = new DecimalFormat("0.###");

	public TTestOperation(ABOperation input, String params, ArrayList<String> fields) {
		super(input, params, fields, null);
	}
	// target.search(gene:kras).ttest([rpkm][)
	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		ABOperation in = getItarget();
		String otarget = in.getOtarget();
		String param = getParam();
		Iterator<ArrayList<LinkedHashMap<String, Object>>> values = in.exec();
		ArrayList<String> fields = GBIO.parseFieldNames(param);
		double sum = 0d;
		double index = 0;
		int count = 0;
		
		if ( fields.size() != 2 ){
			GB.print ( "The input must have two fields of floating point numbers.");
			return null;
		}
		String f1 = fields.get(0);
		String f2 = fields.get(1);

		ArrayList<Double> a = new ArrayList<Double> ();
		ArrayList<Double> b = new ArrayList<Double> ();
		while (values.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> v = values.next();
			for (Map<String, Object> vmap : v) {
				
				Object oba = vmap.get(f1);
				Double da = GBUtil.toDouble(oba);
				Object obb = vmap.get(f2);
				Double db = GBUtil.toDouble(obb);
				if ( db!=null && da !=null ){
				a.add(da);
				b.add (db);
				}
			}
		}
		
		
		double[] sample1 = new double[a.size()];
		double[] sample2 = new double[b.size()];
		for ( int i =0; i < a.size(); i++)
			sample1[i]=a.get(i);

		for ( int i =0; i < b.size(); i++)
			sample2[i]=b.get(i);

		TTest t = new TTest ();
		try {
			double pvalue = t.pairedTTest(sample1, sample2);
			GB.print ( "P-value=" + formatter.format(pvalue));
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		Double mean = sum / index;
//		GB.print("Mean=" + formatter.format(mean));
		String oTarget = getOtarget();
		return null;
	}
}
