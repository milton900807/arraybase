package com.arraybase.qmath;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBUtil;

public class MeanOperation extends ABOperation {
    private final static DecimalFormat formatter = new DecimalFormat("0.###");

	public MeanOperation(ABOperation input, String params, ArrayList<String> fields) {
		super(input, params, fields, null);
	}
	// target.search(gene:kras).mean([rpkm])
	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		ABOperation in = getItarget();
		String otarget = in.getOtarget();
		String param = getParam();
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
		GB.print("Mean=" + formatter.format(mean));
		String oTarget = getOtarget();
		return null;
	}
}
