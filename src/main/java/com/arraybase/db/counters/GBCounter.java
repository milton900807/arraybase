package com.arraybase.db.counters;

public interface GBCounter {
	String COLUMNS = "COLUMNS";
	String GB_COLUMSN = "GB_COLUMNS";

	int count(String... _param) throws FailedToDetermineCountException;

	String getCounterType();

}
