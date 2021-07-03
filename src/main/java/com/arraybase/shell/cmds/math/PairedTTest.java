package com.arraybase.shell.cmds.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBUtil;
import com.arraybase.GBV;
import com.arraybase.qmath.stats.TTest;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.cmds.search2;
import com.arraybase.tab.ABFieldType;

public class PairedTTest implements GBPlugin {
	public String exec(String command, String variable_key)
			throws UsageException {

		// command :
		// pairedt(/isis/something.search(kras)[rpkm]{0-10},/isis/anotherthing.search(opten
		// AND feature:exon)[rpkm]{0-10}) key : null

		int st = command.indexOf('(');
		int et = command.lastIndexOf(')');

		String sub = command.substring(st + 1, et);
		int end_first_q = sub.indexOf(']');

		int inxco = findparams(sub, 0);

		String first_q = sub.substring(0, inxco);
		String secondq = sub.substring(inxco + 1);

		TTest t = new TTest();
		search2 s1 = new search2();
		search2 s2 = new search2();
		GBV<Iterator> v1 = s1.execGBVIn(first_q, null);
		GBV<Iterator> v2 = s2.execGBVIn(secondq, null);

		Iterator i1 = v1.get();
		Iterator i2 = v2.get();

		ArrayList<Double> first_list = buildArrayList(i1);
		ArrayList<Double> second_list = buildArrayList(i2);

		double[] sample1 = getDoubleArray(first_list);
		double[] sample2 = getDoubleArray(second_list);

		Double p_v = null;
		try {
			
			org.apache.commons.math3.stat.inference.TTest tt = new org.apache.commons.math3.stat.inference.TTest();
			double p_value = tt.pairedTTest(sample1, sample2);
			GB.print ( " P-Value : " + p_value);
			p_v=new Double ( p_value );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
		// System.out.println(" command : " + command + " key : " +
		// variable_key);

		return "P-value="+p_v;
	}

	private double[] getDoubleArray(ArrayList<Double> first_list) {
		int index = 0;
		double[] d = new double[first_list.size()];
		for (Double dd : first_list) {
			d[index++] = dd;
		}
		return d;
	}

	private ArrayList<Double> buildArrayList(Iterator i1) {

		ArrayList<Double> list_1 = new ArrayList<Double>();
		while (i1.hasNext()) {
			Object ob = i1.next();
			ArrayList list1 = (ArrayList) ob;
			for (int index = 0; index < list1.size(); index++) {
				LinkedHashMap<String, Object> lob = (LinkedHashMap<String, Object>) list1
						.get(index);
				Set<String> keys = lob.keySet();
				for (String k : keys) {
					if (!ABFieldType.isReserved(k)) {
						Object llob = lob.get(k);
						System.out.println(" k : " + k + " v : "
								+ llob.toString());
						if (llob != null && llob.toString().length() > 0) {
							Double dd = GBUtil.toDouble(llob);
							if (dd != null)
								list_1.add(dd);
						}
					}
				}
			}
		}
		return list_1;
	}

	// something.search(adsfasdf), something.search(adfasdf) --true
	// something.search(asdfsad,adfadsf), sometlhing.search(asdfasdf) false,
	// true will recurse once.
	private int findparams(String sub, int start_index) {
		int s = sub.indexOf(',', start_index);

		int startq = sub.indexOf('(');
		int endq = sub.indexOf(')');

		// if the comma is between parens then it's not the correct one.
		if (s > startq && s < endq) {
			return findparams(sub, endq);
		} else
			return s;
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		return null;
	}

}
