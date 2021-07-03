package com.arraybase.db;

import com.arraybase.db.counters.GBColCounter;
import com.arraybase.db.counters.GBCounter;

public class GBCountFactory {
	public static GBCounter getCounter(String param) {
		if (param.equalsIgnoreCase(GBCounter.COLUMNS)) {
			GBCounter cb = new GBColCounter();
			return cb;
		}
		return null;
	}

}
