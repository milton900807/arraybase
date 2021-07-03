package com.arraybase.io;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * This does not need to be included in the SR application.
 */
@Entity
@Table(name = "ab_raw_files")
public class GBBlobFile implements Serializable {
	//
	private Long file_id = 0l;
	private static final long serialVersionUID = 1L;
	private Date last_updated_date;
	private String last_saved_by_usr_id;
	private String attachment_desc;
	private String attachment_name;
	private byte[] attachment1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "raw_file_gen")
	@SequenceGenerator(name = "raw_file_gen", sequenceName = "raw_file_gen_seq", initialValue = 1001, allocationSize = 1)
	public Long getFile_id() {
		return file_id;
	}

	public void setFile_id(Long file_id) {
		this.file_id = file_id;
	}

	public Date getLast_updated_date() {
		return last_updated_date;
	}

	public void setLast_updated_date(Date lastUpdatedDate) {
		last_updated_date = lastUpdatedDate;
	}

	public String getLast_saved_by_usr_id() {
		return last_saved_by_usr_id;
	}

	public void setLast_saved_by_usr_id(String lastSavedByUsrId) {
		last_saved_by_usr_id = lastSavedByUsrId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAttachment_desc() {
		return attachment_desc;
	}

	public void setAttachment_desc(String attachmentDesc) {
		attachment_desc = attachmentDesc;
	}

	public String getAttachment_name() {
		return attachment_name;
	}

	public void setAttachment_name(String attachmentName) {
		attachment_name = attachmentName;
	}

	@Lob
	public byte[] getAttachment1() {
		return attachment1;
	}

	public void setAttachment1(byte[] attachment1) {
		this.attachment1 = attachment1;
	}
}