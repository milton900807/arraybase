package com.arraybase.schedule;

import java.util.Properties;

public class ScheduleProperties {

	public static Properties create() {
		Properties properties = new Properties();
		properties.put("org.quartz.scheduler.instanceName", "GBSchedule");
		properties.put("org.quartz.threadPool.class",
				"org.quartz.simpl.SimpleThreadPool");
		properties.put("org.quartz.threadPool.threadCount", String.valueOf(3));
		properties.put("org.quartz.scheduler.skipUpdateCheck", "true");
		return properties;
	}

}
