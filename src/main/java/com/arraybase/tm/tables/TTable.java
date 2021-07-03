package com.arraybase.tm.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;

@Entity
@Table(name = "ab_table")
public class TTable implements Serializable {

	public final static String TABLE = "table";
	public final static String IMAP = "imap";

	protected Integer itemID = -1;
	protected String title;
	protected String description;
	protected String user;
	protected String securityStatus = "1";
	protected Date lastEdited;
	protected String sourceType = SourceType.DEFAULT.name;
	protected TMTableSettings settings = null;
	protected List<TTable> subitems = new ArrayList<TTable>();
	private Map<String, String> server = new HashMap<String, String>();
	private String link = "";
	private String index_id = null;

	public TTable() {
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String _type) {
		sourceType = _type;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_table_seq_gen")
	@SequenceGenerator(name = "ab_table_seq_gen", sequenceName = "ab_table_seq", initialValue = 1, allocationSize = 1)
	public Integer getItemID() {
		return itemID;
	}

	public void setItemID(Integer itemID) {
		this.itemID = itemID;
	}

	@Column(length = 1000)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(length = 4000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "tm_lib_user")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSecurityStatus() {
		return securityStatus;
	}

	public void setSecurityStatus(String securityStatus) {
		this.securityStatus = securityStatus;
	}

	@Column(name = "date_last_edited")
	public Date getLastEdited() {
		return lastEdited;
	}

	public void setLastEdited(Date lastEdited) {
		this.lastEdited = lastEdited;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "ab_table_si")
	public List<TTable> getSubitems() {
		return subitems;
	}

	public void setSubitems(List<TTable> subitems) {
		this.subitems = subitems;
	}

	@OneToOne(targetEntity = TMTableSettings.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "settings_tbl_prop_id")
	public TMTableSettings getSettings() {
		return settings;
	}

	public void setSettings(TMTableSettings settings) {
		this.settings = settings;
	}

	@ElementCollection
	@CollectionTable(name = "ab_servers", joinColumns = @JoinColumn(name = "s_to_l"))
	public Map<String, String> getServer() {
		return server;
	}

	public void setServer(Map<String, String> server) {
		this.server = server;
	}

	public void setServer(String _server) {
		server.put("server", _server);
	}

	public void setPath(String _path) {
		server.put("server", _path);
	}

	public String getLink() {
		return "com.tissuematch.tm3.mylib.TMLibraryItem.load(" + getItemID()
				+ ")";
	}

	public void setLink(String _link) {

	}

	/**
	 * This is the actual index identifier
	 * 
	 * @return
	 */
	@Column(name = "index_id", length = 400)
	public String getIndex_id() {

		// milton_Repository_HTL_age_set
		if (index_id == null) {
			index_id = NameUtiles.prepend(user, title);
		}
		return index_id;
	}

	public void setIndex_id(String index_id) {
		this.index_id = index_id;
	}

}