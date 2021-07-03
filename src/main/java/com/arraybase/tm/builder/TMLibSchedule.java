package com.arraybase.tm.builder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tm_lb_bldr_schedule")
public class TMLibSchedule {

	private long schedule_id = -1;
	private String name = null;
	private String type = "DAILY";
	private String schedule_str = null;
	private String created_by = null;
	private Date date_created = null;
	private int hourOfDay = 0;
	private int minute = 0;
	private int second = 0;
	private transient TMScheduler scheduler = new TMScheduler();
	private transient String instance_key = "";

	public TMLibSchedule() {

	}

	public TMLibSchedule(String _type, int _hr, int _min, int _sec) {
		type = _type;
		hourOfDay = _hr;
		minute = _min;
		second = _sec;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tm_bschedule_gen")
	@SequenceGenerator(name = "tm_bschedule_gen", sequenceName = "tm_bschedule_seq", initialValue = 1, allocationSize = 1)
	public long getSchedule_id() {
		return schedule_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSchedule_id(long schedule_id) {
		this.schedule_id = schedule_id;
	}

	/**
	 * @param hour
	 * @param _min
	 * @param _second
	 */
	public TMLibSchedule(int hour, int _min, int _second) {
		hourOfDay = hour;
		minute = _min;
		second = _second;
	}

	public void start() {
		TMScheduleIterator time_iterator = TimeIteratorFactory.getIterator(
				type, hourOfDay, minute, second);
		scheduler.schedule(new TMSchedulerTask() {
			public void run() {
				
				
				
				
				System.out.println(instance_key + " GO !!!!!!!! ");
			}
		}, time_iterator);
	}

	public String getSchedule_str() {
		return schedule_str;
	}

	public void setSchedule_str(String schedule_str) {
		this.schedule_str = schedule_str;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public Date getDate_created() {
		return date_created;
	}

	public void setDate_created(Date date_created) {
		this.date_created = date_created;
	}

	@Transient
	public TMScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(TMScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Column(name = "hour_of_day")
	public int getHourOfDay() {
		return hourOfDay;
	}

	public void setHourOfDay(int hourOfDay) {
		this.hourOfDay = hourOfDay;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Transient
	public void setInstanceKey(String _instance_key) {
		instance_key = _instance_key;

	}

	@Column(name = "builder_type")
	public String getType() {
		return type;
	}

	public void setType(String _type) {
		type = _type;
	}

}
