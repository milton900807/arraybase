package com.arraybase.tm.builder;

import java.util.Date;

public interface TMScheduleIterator extends TimeIterator {

	String DAILY = "DAILY";
	String WEEKLY = "WEEKLY";
	String EVERY_OTHER_MINUTE = "EVERY_OTHER_MINUTE";
	String SECONDS = "SECONDS";

	Date next();

}
