package com.arraybase.qmath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBUtil;

public class DiffOperation extends ABOperation {

	
	public DiffOperation(ABOperation input, String params, ArrayList<String> fields, int[] index) {
		super(input, params, fields, index);
	}

	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		// default operation does not take in input
		// default operation does not push to output
		// search2 search = new search2 ();
		// search.exec(target.toString()+".search("+ param + ")", null);
		ABOperation in = getItarget();
		String out = in.getOtarget();
		String param = getParam();
		String searchString = param;
		
		Iterator<ArrayList<LinkedHashMap<String, Object>>> values = in.exec();
		ArrayList<String> fields = GBIO.parseFieldNames(param);
		double sum = 0d;
		double index = 0;
		int count = 0;
		while (values.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> v = values.next();
			for (Map<String, Object> vmap : v) {
				for (String f : fields) {
					Object ob = vmap.get(f);
					Double d = GBUtil.toDouble(ob);
					if (d != null) {
						sum += d;
						index += 1;
					}else
					{
						GB.print ( " WARNING: " + f + " is not a valid number at index : " + count);
					}
				}
				count++;
			}
		}
		Double mean = sum / index;
		
		
		return null;
	}

}
