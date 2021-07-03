package com.arraybase.io.parse;

import java.util.ArrayList;
import com.arraybase.GBV;

public class ArrayListVar extends GBV {

	public ArrayListVar(ArrayList<String> t) {
		super(t);
	}

	public String toString() {
		ArrayList<String> to = (ArrayList<String>) get();
		String value = null;
		for (String o : to) {
			value += o.toString() + "\n";
		}
		return value;
	}

}
