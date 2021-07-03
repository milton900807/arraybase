package com.arraybase.tm.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ab_nodelink")
public class TMNodeLink implements Serializable {

	private long tmlink_id = -1L;
	private String core_uri = null;

	private List<Integer> fields = new ArrayList<Integer>();
	private String query = null;
	private String nodeLinkType = null;
	private String title = "";

	public TMNodeLink() {

	}

	public String getQuery() {
		return query;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tr_nodelink_gen")
	@SequenceGenerator(name = "tr_nodelink_gen", sequenceName = "tr_nodelink_gen_seq", initialValue = 1, allocationSize = 1)
	public long getTmlink_id() {
		return tmlink_id;
	}

	public void setTmlink_id(long tmlink_id) {
		this.tmlink_id = tmlink_id;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getCore_uri() {
		return core_uri;
	}

	public void setCore_uri(String core_uri) {
		this.core_uri = core_uri;
	}

	// @OneToMany
	// @JoinTable(name = "ab_node_link_prop_map", joinColumns = @JoinColumn(name
	// = "tmlink_id"), inverseJoinColumns = @JoinColumn(name = "prop_id"))
	@ElementCollection
	@CollectionTable(name = "ab_nodelink_prop", joinColumns = @JoinColumn(name = "n_to_r"))
	public List<Integer> getFields() {
		return fields;
	}

	public void setFields(List<Integer> fields) {
		this.fields = fields;
	}

	public String getNodeLinkType() {
		return nodeLinkType;
	}

	public void setNodeLinkType(String _node_link) {
		nodeLinkType = _node_link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String _title) {
		title = _title;
	}
}
