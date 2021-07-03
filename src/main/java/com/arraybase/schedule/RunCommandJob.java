package com.arraybase.schedule;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.arraybase.GB;
import com.arraybase.modules.UsageException;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RunCommandJob implements Job {

	public static final String COMMAND = "command";

	public RunCommandJob() {
	}

	/**
	 * The COMMAND VALUE MUST BE SET IN THE JOB MAP BEFORE YOU MAY RUN THIS
	 * METHOD
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			JobDataMap map = context.getJobDetail().getJobDataMap();
			String command = null;
			if (map.containsKey(COMMAND)) {
				command = map.getString(COMMAND);
			}
			if ( command == null )
			{
				throw new JobExecutionException("Failed to find the command to execute from the JobMap... : "+ command);
			}
			GB.execute(command);
		} catch (UsageException e) {
			e.printStackTrace();
		}
	}

}
