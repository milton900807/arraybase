package com.arraybase.tm.builder.jobs;

import com.arraybase.db.util.NameUtiles;
import com.arraybase.tm.tables.TMTableSettings;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "ab_jobs")
public class Job implements Serializable {

    public static final String COMPLETE_STATUS = "Complete";
    public static final String ACTIVE_STATUS = "Active";
    public static final String PAUSED_STATUS = "Paused";

    Integer itemID = -1;
	String user;
	String job_id = null;
	String status = null;
	Date started = null;
	Date completed = null;


	public Job() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_jobs_seq_gen")
	@SequenceGenerator(name = "ab_jobs_seq_gen", sequenceName = "ab_jobs_seq", initialValue = 1, allocationSize = 1)
	public Integer getItemID() {
		return itemID;
	}

	public void setItemID(Integer itemID) {
		this.itemID = itemID;
	}

	@Column(name = "job_user")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

    @Column(length = 2000)
	public String getJob_id() {
		return job_id;
	}

	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}
	@Column(length = 1000)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getCompleted() {
		return completed;
	}

	public void setCompleted(Date completed) {
		this.completed = completed;
	}
}