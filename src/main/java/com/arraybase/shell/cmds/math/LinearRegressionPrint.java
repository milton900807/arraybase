package com.arraybase.shell.cmds.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.qmath.NumberVar;
import com.arraybase.tab.ABFieldType;

/**
 * @deprecated use LinearRegressionOperation
 * @author jmilton
 *
 */
public class LinearRegressionPrint implements GBPlugin {
	public String exec(String command, String variable_key)
			throws UsageException {
		return null;
	}
	

	public GBV execGBVIn(String cmd, GBV input) {
		// apob.search(percent_control:[30 TO 35])[mass][percent_control]|linear_regression
		// passes in an iterator of arraylist<linkedhashmaps> etc...
		// i.e. a se
		Object object = input.get();
		if (!(object instanceof Iterator)) {
			GB.print("MeanValue Cannot handle this type of input object.");
		}
		Iterator it = (Iterator) object;
		Double sum = 0d;
		double index = 0;
		while (it.hasNext()) {
			Object ob = it.next();
			if (ob instanceof ArrayList) {
				ArrayList list = (ArrayList) ob;
				for (Object obb : list) {
					if (obb instanceof LinkedHashMap) {
						LinkedHashMap<String, Object> obbb = (LinkedHashMap<String, Object>) obb;
						Set<String> stss = obbb.keySet();
						for (String key : stss) {
							if (!ABFieldType.isReserved(key)) {
								Object value = obbb.get(key);
								// System.out.println(" k: " + key + " v : "
								// + value.toString());
								if (value instanceof Number) {
									try {
										Number d = (Number) value;
										Double db = d.doubleValue();
										if (db.isNaN() || db.isInfinite()) {
											GB.print("Warning: "
													+ db.toString()
													+ " found at n="
													+ index
													+ "  ...skipping this (n) as part of the mean.");

										} else {
											sum += d.doubleValue();
											index++;
										}
									} catch (ClassCastException _ex) {
										_ex.printStackTrace();
									}
								} else {
									if (value != null) {
										String va = value.toString();
										if (va != null && va.length() > 0) {
											try {
												Double dab = Double
														.parseDouble(va.trim());
												sum += dab.doubleValue();
												index++;
											} catch (NumberFormatException _nn) {
												GB.print("Warning... Not a number : " + va);

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		double mean = sum / index;
		GB.print("Mean : " + mean);
		GB.printSub("Sum : " + sum);
		GB.printSub("N : " + index);
		NumberVar numb = new NumberVar(mean);
		return numb;
	}}
