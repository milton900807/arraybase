package com.arraybase.tm.builder;

public class TimeIteratorFactory {

	public static TMScheduleIterator getIterator(String type, int hourOfDay,
			int minute, int second) {
		if (type.equalsIgnoreCase(TMScheduleIterator.DAILY)) {
			return new DailyIterator(hourOfDay, minute, second);
		} else if (type.equalsIgnoreCase(TMScheduleIterator.WEEKLY)) {
			return new WeeklyIterator(hourOfDay, minute, second);
		} else if (type.equalsIgnoreCase(TMScheduleIterator.EVERY_OTHER_MINUTE)) {
			return new MinIterator(2);
		}else if (type.equalsIgnoreCase(TMScheduleIterator.SECONDS)) {
			return new SecondIterator(second);
		}
		return null;
	}
}
