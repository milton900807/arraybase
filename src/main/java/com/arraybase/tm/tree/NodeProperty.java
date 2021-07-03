package com.arraybase.tm.tree;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;



// some examples of what linkouts look like
//linkout[Screening Results]	http://birdbeta.isisph.com/birdapps/query/ListTargetScreens.do?mtid=[TARGETID]#screeningexperiment_results
//-	-	linkout[NCBI Gene]	http://www.ncbi.nlm.nih.gov/gene/[GENEID]
//-	-	link	http://birdbeta.isisph.com/birdapps/query/TargetResults.do?mtid=[TARGETID]
//-	-	render[SCREENING_COMPLETE]	genomic
//-	-	render.SCREENING_COMPLETE	binary
//-	-	render[SCREENING_COMPLETED]	binary
//




@Entity
@Table(name = "ab_node_props")
public class NodeProperty {
	// types of properties 
	public final static String NODE_GENERATOR = "GEN";
	public final static String NODE_UPDATER = "UPDATE";
	public static final String NODE_UPDATE_QUERY = "update_query";
	public static final String NODE_UPDATE_CONDITION = "update";
	public static final String NODE_TABLE_STATE = "table_state";
	
	// the node link can be the following :  name.link
	public static final String FIELD_LINK = "$field.link";
	
	
	private String name = "";
	private String type = "";
	private String property = "";
	private long prop_id = 0l;
	private long node_id = -1l;
	private byte[] node_property_file = null;

	@Column(name = "n_prop", length = 4000)
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ab_node_prop_gen")
	@SequenceGenerator(name = "ab_node_prop_gen", sequenceName = "ab_node_prop_gen_seq", initialValue = 1, allocationSize = 1)
	public long getProp_id() {
		return prop_id;
	}

	public void setProp_id(long prop_id) {
		this.prop_id = prop_id;
	}

	@Lob
	@Column(name = "property_file")
	public byte[] getFile() {
		return node_property_file;
	}

	public void setFile(byte[] file) {
		this.node_property_file = file;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNode_id() {
		return node_id;
	}

	public void setNode_id(long node_id) {
		this.node_id = node_id;
	}

	public static String getFieldLink(String field) {
		return FIELD_LINK.replace("$field", field);
	}
	

}
