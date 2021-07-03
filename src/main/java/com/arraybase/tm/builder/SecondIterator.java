package com.arraybase.tm.builder;

import java.util.Date;
import java.util.Calendar;
import java.util.Date;

public class SecondIterator implements TMScheduleIterator {

	final Calendar cal = Calendar.getInstance();
	private int sec = 1;

	public SecondIterator() {

	}

	public SecondIterator(int _sec) {
		cal.setTime(new Date());
		sec = _sec;
	}

	public Date next() {
		cal.add(Calendar.SECOND, sec);
		return cal.getTime();
	}

}
