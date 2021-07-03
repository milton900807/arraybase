package com.arraybase.tm.builder;


import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author donaldm
 * 
 */
public class TMScheduler {
	class TMScTT extends TimerTask {
		private TMSchedulerTask st;
		private TMScheduleIterator si;
		public TMScTT(TMSchedulerTask tmSchedulerTask,
				TMScheduleIterator iterator) {
			st = tmSchedulerTask;
			si = iterator;
		}
		public void run() {

			st.run();
			reschedule ( st, si);
			
		}
	}

	private final Timer timer = new Timer();

	public TMScheduler() {
		
	}
	public void cancel (){
		timer.cancel();
	}

	public void schedule(TMSchedulerTask tmSchedulerTask,
			TMScheduleIterator iterator) {
		Date time = iterator.next();
		if (time == null) {
			tmSchedulerTask.cancel();
		} else {
			synchronized (tmSchedulerTask.lock_object) {

				if (tmSchedulerTask.state != TMSchedulerTask.INIT) {
					throw new IllegalStateException(
							"This task is already scheduled ");
				}
				tmSchedulerTask.state = TMSchedulerTask.SCHEDULED;
				tmSchedulerTask.setTimerTask(new TMScTT(tmSchedulerTask,
						iterator));
				timer.schedule(tmSchedulerTask.getTimerTask(), time);
			}
		}
	}

	/**
	 * @param _scheduleTask
	 * @param _iterator
	 */
	public void reschedule(TMSchedulerTask _scheduleTask,
			TMScheduleIterator _iterator) {

		Date d = _iterator.next();
		if (d == null) {
			_scheduleTask.cancel();
		} else {
			synchronized (_scheduleTask.lock_object) {
				if (_scheduleTask.state != TMSchedulerTask.CANCELLED) {
					_scheduleTask.setTimerTask(new TMScTT(_scheduleTask,
							_iterator));
					timer.schedule(_scheduleTask.getTimerTask(), d);
				}
			}
		}

	}

}
