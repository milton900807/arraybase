package com.arraybase.schedule;

import java.util.List;
import java.util.Properties;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import com.arraybase.GB;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;

public class GBScheduler {
	private Properties properties = ScheduleProperties.create();
	private Scheduler scheduler = null;

	public static GBScheduler createScheduler() {
		return new GBScheduler();
	}

	GBScheduler() {
		StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
		try {
			stdSchedulerFactory.initialize(properties);
			scheduler = stdSchedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			GB.print("Shutting down scheduler...");
			scheduler.shutdown();
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			if ( scheduler == null || scheduler.isStarted()) {
				System.out.println ( " \t\t\t\t\tscheduler is already started. ");
				return;
			}
			GB.print("Scheduler starting...");
			scheduler.start();
			GB.print("Scheduler started.");

		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}


	public void printScheduler ( )
	{
		try {
			List<String> groupnmes = scheduler.getJobGroupNames();
			for ( String t : groupnmes )
			{
				System.out.println ( " name : " + t );
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}




	public void add(String command, String group, String jobname,
			String cron_expression) throws GBSchedulerNotReady {
		if (cron_expression.equalsIgnoreCase("nightly")) {
			cron_expression = "0 45 22 * * ?";// nightly schedule
		}

		if ( scheduler == null )
			throw new GBSchedulerNotReady ();


		JobDetail jbDetail = newJob(RunCommandJob.class).withIdentity(jobname,
				group).build();
		jbDetail.getJobDataMap().put(RunCommandJob.COMMAND, command);
		try {
			cron_expression = cron_expression.trim();
			GB.print ( "CRExpr:" + cron_expression);
			GB.print ( "Command:" + command);

			CronTrigger trigger = newTrigger().withIdentity(jobname, group)
					.withSchedule(cronSchedule(cron_expression)).build();
			scheduler.scheduleJob(jbDetail, trigger);
			GB.print ( " Job has been scheduled " );

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}