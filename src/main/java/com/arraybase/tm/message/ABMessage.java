package com.arraybase.tm.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * This is a server side a
 * @author donaldm
 *
 */
@Entity
@Table(name = "tt_message")
public class ABMessage implements java.io.Serializable{
	
	private Long msg_id;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tt_message_seq_gen")
	@SequenceGenerator(name = "tt_message_seq_gen", sequenceName = "tt_message_seq", initialValue = 1, allocationSize = 1)
	public Long getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(Long msg_id) {
		this.msg_id = msg_id;
	}
	private String title = null;
	private String message = "";
	private Date created_date = new Date ();
	private String created_by = null;
	private String uri = null;
	private ArrayList<String> reference = new ArrayList<String> ();
	private HashMap<String, String> user_radio_label = new HashMap<String, String> ();
	
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column ( length=6000) 
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public ArrayList<String> getReference() {
		return reference;
	}
	public void setReference(ArrayList<String> reference) {
		this.reference = reference;
	}
	public HashMap<String, String> getUser_radio_label() {
		return user_radio_label;
	}
	public void setUser_radio_label(HashMap<String, String> user_radio_label) {
		this.user_radio_label = user_radio_label;
	}
	
}
