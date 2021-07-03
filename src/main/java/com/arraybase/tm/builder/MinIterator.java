package com.arraybase.tm.builder;

import java.util.Date;
import java.util.Calendar;
import java.util.Date;

public class MinIterator implements TMScheduleIterator {
	int min = 1;
	private final Calendar cal = Calendar.getInstance();

	public MinIterator(int i) {
		min = i;
		cal.setTime(new Date());
	}

	public Date next() {
		cal.add(Calendar.MINUTE, min);
		return cal.getTime();
	}
}
