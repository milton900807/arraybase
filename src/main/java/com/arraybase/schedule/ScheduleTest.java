package com.arraybase.schedule;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleTest {

	public static void main(String[] args) {

		try {
			Properties properties = ScheduleProperties.create();
			StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
			stdSchedulerFactory.initialize(properties);
			Scheduler scheduler = stdSchedulerFactory.getScheduler();
			// and start it off
			scheduler.start();
			scheduler.shutdown();
		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}
}