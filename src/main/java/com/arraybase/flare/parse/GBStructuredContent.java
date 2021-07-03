package com.arraybase.flare.parse;

import java.util.Date;

/**
 * This is the object that represents the contract between GB and this package.
 * we expect this to be available in the GBFile format.
 * 
 * @author donaldm
 * 
 */
public class GBStructuredContent {

	public static final String SEMI_STRUCTURED = "SEMI_STRUCTURED";
	public static final String STRUCTURED = "STRUCTURED";
	private String level = SEMI_STRUCTURED;
	private String type = null;
	private StringBuffer buffer = null;
	private String header = null;
	private String authors = null;
	private String producer = null;
	private Date lastModified = null;
	private Date created_date = null;
	private String creator = null;
	private String subject = null;
	private String table_of_contents = null;
	private String index = null;
	private String title = null;
	private int wordCount = -1;
	private String path = null;

	public GBStructuredContent(String _type) {
		type = _type;
	}

	public GBStructuredContent() {
		// TODO Auto-generated constructor stub
	}

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getFileHeaderInformation() {
		// TODO Auto-generated method stub
		return header;
	}

	public String getContentAsString() {
		if (buffer == null)
			return "";
		return buffer.toString();
	}

	public String getAuthors() {
		return authors;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTable_of_contents() {
		return table_of_contents;
	}

	public void setTable_of_contents(String table_of_contents) {
		this.table_of_contents = table_of_contents;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
