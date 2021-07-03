package com.arraybase.tm.tables;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Entity
@Table(name = "ab_table_template")
public class TableTemplate {
	private String name = "Unknown";
	/**
	 * in order to presist this data struture we are going to json it in the
	 * fields_persist below.
	 */
	@Transient
	private transient Map<String, Map<String, String>> fields = null;
	private String fields_persist = null;
	private String template_link = null;

	private String path = null;
	private String creator = null;
	protected Integer pkid = -1;

	public TableTemplate() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_ttemplate_seq_gen")
	@SequenceGenerator(name = "ab_ttemplate_seq_gen", sequenceName = "ab_ttemplate_seq", initialValue = 1, allocationSize = 1)
	public Integer getPkid() {
		return pkid;
	}

	public void setPkid(Integer pkid) {
		this.pkid = pkid;
	}

	public void setSchema(Map<String, Map<String, String>> schema) {
		fields = schema;
	}

	@Transient
	public Map<String, Map<String, String>> getSchema() {
		return fields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public Map<String, Map<String, String>> getFields() {
		return fields;
	}

	public void setFields(Map<String, Map<String, String>> fields) {
		this.fields = fields;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	

	public String getTemplate_link() {
		return template_link;
	}

	public void setTemplate_link(String template_link) {
		this.template_link = template_link;
	}

	/**
	 * This is the persisted field set...
	 * 
	 * @return
	 */
	@Column(length = 4000)
	public String getFields_persist() {
		fields_persist = buildPersitedValues();
		return fields_persist;
	}

	private String buildPersitedValues() {
		Gson g = new Gson();
		String values = g.toJson(fields);
		return values;
	}

	public void setFields_persist(String fields_persist) {
		this.fields_persist = fields_persist;
		buildTransientValues();
	}

	private void buildTransientValues() {
		Gson g = new Gson();
		fields = g.fromJson(fields_persist,
				new TypeToken<Map<String, Map<String, String>>>() {
				}.getType());

	}
}
