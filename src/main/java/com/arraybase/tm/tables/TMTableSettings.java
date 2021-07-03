package com.arraybase.tm.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ab_table_props")
public class TMTableSettings implements java.io.Serializable {
	private Long tbl_prop_id = 0L;
	private int height = 500;
	private int width = 300;
	private Map<String, Integer> col_order = new LinkedHashMap<String, Integer>();
	private String state = null;
	private int rows_per_page = 100;
	private boolean default_width = true;
	private Map<String, String> properties = new HashMap<String, String>();

	public TMTableSettings() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_table_props_gen")
	@SequenceGenerator(name = "ab_table_props_gen", sequenceName = "ab_table_props_seq", initialValue = 1, allocationSize = 1)
	public Long getTbl_prop_id() {
		return tbl_prop_id;
	}

	public void setTbl_prop_id(Long tbl_prop_id) {
		this.tbl_prop_id = tbl_prop_id;
	}

	@ElementCollection(targetClass = java.lang.String.class)
	@CollectionTable(name = "ab_table_props_map", joinColumns = @JoinColumn(name = "t_to_tp"))
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> _properties) {
		this.properties = _properties;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	// @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
	// mappedBy="columnName")
	// @JoinTable(name="tm_col_settings_si")
	// public List<TMColumn> getColumns() {
	// return columns;
	// }
	//
	// public void setColumns(List<TMColumn> _columns) {
	// this.columns = (List) _columns;
	// }

	/**
	 * This is overridden by the state order. Please remember that!!!!
	 * 
	 * @return
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ab_tbl_col_order", joinColumns = @JoinColumn(name = "columns"))
	public Map<String, Integer> getCol_order() {
		return col_order;
	}

	public void setCol_order(Map<String, Integer> col_order) {
		this.col_order = col_order;
	}

	@Column(name = "column_state", length = 6000)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getRows_per_page() {
		return rows_per_page;
	}

	public void setRows_per_page(int rows_per_page) {
		this.rows_per_page = rows_per_page;
	}

	public boolean isDefault_width() {
		return default_width;
	}

	public void setDefault_width(boolean default_width) {
		this.default_width = default_width;
	}

	public boolean getDefault_width() {
		return default_width;
	}

}
