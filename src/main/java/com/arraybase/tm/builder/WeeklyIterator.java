package com.arraybase.tm.builder;

import java.util.Date;
import java.util.Calendar;
import java.util.Date;

public class WeeklyIterator implements TMScheduleIterator {

	private int h;
	private int m;
	private int s;
	private final Calendar cal = Calendar.getInstance();

	public WeeklyIterator(int hourOfDay, int minute, int second) {
		this(hourOfDay, minute, second, new Date());
	}

	public WeeklyIterator(int hourOfDay, int minute, int second, Date date) {
		h = hourOfDay;
		m = minute;
		s = second;
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		cal.set(Calendar.SECOND, s);
		cal.set(Calendar.MILLISECOND, 0);
		if (!cal.getTime().before(date)) {
			cal.add(Calendar.DATE, -1);
		}
	}

	public Date next() {
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		return cal.getTime();
	}
}
