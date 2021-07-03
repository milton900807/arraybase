package com.arraybase.io.parse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.arraybase.GBV;
import com.arraybase.search.ABaseResults;

public class ABResultsVar extends GBV {

	public ABResultsVar(ABaseResults t) {
		super(t);
	}

	public ABaseResults getResults() {
		return (ABaseResults) get();
	}

	public String toString() {
		ABaseResults abmap = (ABaseResults) get();
		LinkedHashMap<String, LinkedHashMap<String, Integer>> map = abmap.getFacet();
		Set<String> sets = map.keySet();
		String value = null;
		for (String o : sets) {
			
			value += o + "\t" + map.get(o) + "\n";
		}
		return value;
	}

}
