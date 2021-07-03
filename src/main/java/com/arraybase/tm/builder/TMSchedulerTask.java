package com.arraybase.tm.builder;

import java.util.TimerTask;

public abstract class TMSchedulerTask implements Runnable {

	final Object lock_object = new Object();
	int state = INIT;
	public static final int SCHEDULED = 1;
	public static final int INIT = 0;
	public static final int CANCELLED = 2;

	private TimerTask timerTask;

	public TMSchedulerTask() {

	}

	public abstract void run();

	/**
	 * Cancel the task
	 * 
	 * @return
	 */
	public boolean cancel() {
		synchronized (lock_object) {
			if (timerTask != null) {
				timerTask.cancel();
			}
			boolean result = (state == SCHEDULED);
			state = CANCELLED;
			return result;
		}
	}

	public long scheduledExecutionTime() {
		synchronized (lock_object) {
			return timerTask == null ? 0 : timerTask.scheduledExecutionTime();
		}
	}

	public void setTimerTask(TimerTask _timerTask) {
		timerTask = _timerTask;
	}

	public TimerTask getTimerTask() {
		return timerTask;
	}
}
