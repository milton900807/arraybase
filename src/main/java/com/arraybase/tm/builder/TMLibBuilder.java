package com.arraybase.tm.builder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tm_lib_builder")
public class TMLibBuilder {

	private long builder_id = -1l;
	private String datasource = null;
	private String table_name = null;
	private String builder_type = "DEFAULT";
	private String query = null;
	private String user = null;
	private Map<String, String> prps = new HashMap<String, String> ();
	private long schedule = -1L; 
	private String schedule_status = "ON";
	private String schedule_name = "NONE";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tm_builder_gen")
	@SequenceGenerator(name = "tm_builder_gen", sequenceName = "tm_builder_seq", initialValue = 1, allocationSize = 1)
	public long getBuilder_id() {
		return builder_id;
	}
	public void setBuilder_id(long builder_id) {
		this.builder_id = builder_id;
	}
	public String getBuilder_type() {
		return builder_type;
	}
	public void setBuilder_type(String builder_type) {
		this.builder_type = builder_type;
	}
	@Column(name="builder_user")
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	@ElementCollection
	@CollectionTable(name = "tm_build_prps", joinColumns = @JoinColumn(name = "b_to_p"))
	public Map<String, String> getPrps() {
		return prps;
	}
	public void setPrps(Map<String, String> prps) {
		this.prps = prps;
	}
	@Column(name="builder_string")
	public long getSchedule() {
		return schedule;
	}
	public void setSchedule(long schedule) {
		this.schedule = schedule;
	}
	@Column(length=2000, name="query_string")
	public String getQuery() {
		return query;
	}
	@Column(length=1000, name="data_source")
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getSchedule_status() {
		return schedule_status;
	}
	public void setSchedule_status(String schedule_status) {
		this.schedule_status = schedule_status;
	}
	public String getSchedule_name() {
		return schedule_name;
	}
	public void setSchedule_name(String schedule_name) {
		this.schedule_name = schedule_name;
	}
	
	
}
