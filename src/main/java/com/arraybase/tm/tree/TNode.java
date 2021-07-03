package com.arraybase.tm.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;

import com.arraybase.tm.TMSourceType;

@Entity
@Table(name = "ab_node")
public class TNode implements Serializable {
	private long node_id = -1l;
	private float node_value = 0f;
	private String name = "";
	private String description = "";
	private String qualifier = "";
	private String status = "Undefined";
	private List<Integer> reference = new ArrayList<Integer>();
	private String owner = null;
	private String created_by = "unknown";
	private Date createdDate = new Date();
	private Date lastEditedDate = new Date();
	private String synonyms = "";
	private String nodeType = TMSourceType.NODE.name();
	private String link = null;

	public TNode() {

	}

	public TNode(String _field) {
		name = _field;
	}

	public void setUser(String _user) {

	}

	// public String getSecurityStatus ()
	// {
	// return "Unrestricted";
	// }
	// public void setSecurityStatus ( String _seq ){
	//
	// }
	// public String getSourceType ()
	// {
	// return "Node";
	// }
	// public void setSourceType ( String _t ){
	//
	// }
	// public String getItemID ()
	// {
	// return "hello world";
	// }
	// public void setItemID ( String _item )
	// {
	//
	// }

	/**
	 * Get the owner of this object.
	 * 
	 * @return
	 */
	@Transient
	public String getUser() {
		return owner;
	}

	// public void addChild(TMNode _node) {
	// children.add(_node);
	// }

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_node_gen")
	@SequenceGenerator(name = "ab_node_gen", sequenceName = "ab_node_seq", initialValue = 1, allocationSize = 1)
	public long getNode_id() {
		return node_id;
	}

	public void setNode_id(long node_id) {
		this.node_id = node_id;
	}

	@Column(length = 2000, name = "node_name")
	public String getName() {
		if (name == null)
			return "null";
		return name;
	}

	public void setName(String _name) {
		name = _name;
	}

	@Column(length = 6000, name = "node_description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length = 2000, name = "qualifier")
	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	@Column(name = "node_status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "node_owner")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastEditedDate() {
		return lastEditedDate;
	}

	public void setLastEditedDate(Date lastEditedDate) {
		this.lastEditedDate = lastEditedDate;
	}

	@Column(length = 4000, nullable = true, insertable = true)
	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}

	@Column(name = "node_type", length = 1000)
	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	@Column(name = "node_link", length = 2000)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Get the node float value
	 * 
	 * @return
	 */
	public float getNode_value() {
		return node_value;
	}

	public void setNode_value(float node_value) {
		this.node_value = node_value;
	}

	@ElementCollection
	@CollectionTable(name = "ab_node_ref", joinColumns = @JoinColumn(name = "n_to_r"))
	public List<Integer> getReference() {
		return reference;
	}

	public void setReference(List<Integer> reference) {
		this.reference = normalize(reference);
	}

	private List<Integer> normalize(List<Integer> ref) {

		HashSet<Integer> set = new HashSet<Integer>();
		for (Integer i : ref) {
			set.add(i);
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer iset : set) {
			list.add(iset);
		}
		return list;

	}

	public void addCRef(TNode c2) {
		reference.add((int) c2.getNode_id());
	}

	public void addCRef(int _id) {
		reference.add(_id);
	}

	public void removeCRef(long _nodeID) {
		Long l = new Long(_nodeID);
		reference.remove(new Integer(l.intValue()));
	}

}
