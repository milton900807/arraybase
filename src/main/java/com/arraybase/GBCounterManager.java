package com.arraybase;



import java.util.LinkedHashMap;

import com.arraybase.db.GBCountFactory;
import com.arraybase.db.counters.FailedToDetermineCountException;

public class GBCounterManager {

	public static void count(String[] _args) {
		if (_args.length <= 2) {
			GB.printUsage("Please provide a counter type.");
			GB.printUsage("Example:  gb count $columns $localfile");
			GB.printUsage("Example:  gb count columns row=10 delim=, $gbpath_to_db_or_table");
			GB.printUsage("Example:  gb count $fields $gbpath_to_db_or_table\n  Where "
					+ "the default delimiter is '\\t+'");
			return;
		}
		String type = _args[1];
		LinkedHashMap<String, String> values = GB.parseVals(_args);
		String file = _args[_args.length - 1];
		String delim_ = values.get("delim");
		String row = values.get("row");
		com.arraybase.db.counters.GBCounter counter = GBCountFactory
				.getCounter(type);
		try {
			GB.print("" + counter.getCounterType() + " "
					+ counter.count(file, delim_, row));
		} catch (FailedToDetermineCountException e) {
			e.printStackTrace();
		}

	}

}
