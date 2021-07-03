package com.arraybase.tm.tree;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
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
@Table(name = "ab_path")
public class TPath implements Serializable {

	private long path_id = -1l;
	private String group_name = null;
	private long node_id = -1l;
	private String name = "";
	private String description = "";
	private Long parent = -1l;

	public TPath() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_path_gen")
	@SequenceGenerator(name = "ab_path_gen", sequenceName = "ab_path_seq", initialValue = 1, allocationSize = 1)
	public long getPath_id() {
		return path_id;
	}

	public void setPath_id(long _path_id) {
		path_id = _path_id;
	}

	@Column(length = 2000, name = "path_name")
	public String getName() {
		if (name == null)
			return "Undefined";
		return name;
	}

	public void setName(String name) {
		if (name == null)
			name = "Undefined";
		this.name = name;
	}

	@Column(length = 6000, name = "path_description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "parent")
	public Long getTMParent() {
		return parent;
	}

	public void setTMParent(Long _parent) {
		parent = _parent;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public long getNode_id() {
		return node_id;
	}

	public void setNode_id(long _node_id) {
		this.node_id = _node_id;
	}

	// @ElementCollection
	// @CollectionTable(name = "ab_path_nodes", joinColumns = @JoinColumn(name =
	// "path_j"))
	// public List<Long> getNodes() {
	// return nodes;
	// }
	//
	// public void setNodes(List<Long> reference) {
	// nodes = new ArrayList<Long>();
	// for (Long ll : reference) {
	// nodes.add(ll);
	// }
	// }

}
