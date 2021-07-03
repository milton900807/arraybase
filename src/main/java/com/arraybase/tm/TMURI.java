package com.arraybase.tm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author D. Jeff Milton
 */

// <id name="itemID">
// <generator class="increment" />
// </id>
// <property name="uri" />
// <property name="owner" />
// <property name="title" />
// <property name="sourceType" />
// <property name="connectionType" />
// </class>
// </hibernate-mapping>

@Entity
@Table(name = "tm_uri")
public class TMURI implements java.io.Serializable {

	/**
	 * CONNECTION TYPE
	 */
	public final static String PHYSICAL_LOCATION = "PHYSICAL_LOCATION";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "item_id_seq_gen")
	@SequenceGenerator(name = "item_id_seq_gen", sequenceName = "sr_action_seq", initialValue = 1, allocationSize = 1)
	private long itemID = 12l;
	private static final long serialVersionUID = -1798450533477854907L;
	private String uri = "";
	private String title = "";
	private String owner = "";
	private String sourceType = "";
	private String connectionType = "";

	public TMURI() {

	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String _connectionType) {
		connectionType = _connectionType;
	}

	public long getItemID() {
		return itemID;
	}

	public void setItemID(long itemID) {
		this.itemID = itemID;
	}

	@Column(name = "uri")
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Column(name = "title", length = 1000)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "owner", length = 1000)
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Column(name = "source_type")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String _sourceType) {
		sourceType = _sourceType;
	}

	public String getProtocol() {
		if (uri != null) {
			String[] l = uri.split(":");
			if (l.length <= 0)
				return "undefined";
			return l[0];
		} else
			return "undefined";
	}

	public TMURI copy() {

		TMURI tm = new TMURI();
		// private long itemID = 12l;
		// private static final long serialVersionUID = -1798450533477854907L;
		// private String uri = "";
		// private String title = "";
		// private String owner = "";
		// private String sourceType = "";
		// private String connectionType = "";
		tm.setItemID(itemID);
		tm.setUri(uri);
		tm.setTitle(title);
		tm.setOwner(owner);
		tm.setSourceType(sourceType);
		tm.setConnectionType(connectionType);
		return tm;
	}

	public String getTarget() {
		if (uri != null) {
			String[] l = uri.split(":");
			if (l.length <= 0)
				return "undefined";
			return l[l.length - 1];
		} else
			return "undefined";
	}

	public String getUser() {
		if (uri != null) {
			String[] l = uri.split(":");
			if (l.length <= 0)
				return "undefined";
			return l[1];
		} else
			return "undefined";
	}

}
