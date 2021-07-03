package com.arraybase.solr.plugin.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "schema")
@XmlType(propOrder = { 
		"name", "types", "fields", "uniqueKey",
		"defaultSearchField", "solrQueryParser", "similarity"})
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlSeeAlso({ Field.class })
public class Schema {
	
//	private static final String[][] = new String[][] {
//		{
//	}

	private String name;

	private List<FieldType> types;
	private List<Field> fields;
	private String uniqueKey;
	private String defaultSearchField;
	private SolrQueryParser solrQueryParser;
	private String similarity;

	public Schema() {
		super();
	}

	public Schema(String name) {
		super();
		this.name = name;
	}

	public List<Field> getFields() {
		if (fields == null) {
			fields = new ArrayList<Field>();
		}
		return fields;
	}

	public Field addField(String name, String type) {
		Field field = new Field(name, type);
		getFields().add(field);
		return field;
	}

	@XmlElementWrapper(name = "fields")
	@XmlElement(name = "field")
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<FieldType> getTypes() {
		if (types == null) {
			types = new ArrayList<FieldType>();
		}
		return types;
	}

	public FieldType addFieldType(String name, String clazz) {
		FieldType fieldType = new FieldType(name, clazz);
		getTypes().add(fieldType);
		return fieldType;
	}

	@XmlElementWrapper(name = "types")
	@XmlElement(name = "fieldType")
	public void setTypes(List<FieldType> types) {
		this.types = types;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	@XmlElement
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String getDefaultSearchField() {
		return defaultSearchField;
	}

	@XmlElement
	public void setDefaultSearchField(String defaultSearchField) {
		this.defaultSearchField = defaultSearchField;
	}

	public SolrQueryParser getSolrQueryParser() {
		return solrQueryParser;
	}

	@XmlElement
	public void setSolrQueryParser(SolrQueryParser solrQueryParser) {
		this.solrQueryParser = solrQueryParser;
	}

	public String getSimilarity() {
		return similarity;
	}

	@XmlElement
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}

	public String getName() {
		return name;
	}

	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Schema: " + name + "\n");
		result.append("\tTypes: " + types);
		result.append("\tFields: \n");
		for (Field f : fields) {
			result.append("\t\t" + f.toString());
		}
		result.append("\tuniqueKey: " + uniqueKey + "\n");
		result.append("\tdefaultSearchField: " + defaultSearchField + "\n");
		result.append("\tsolrQueryParser: " + solrQueryParser + "\n");
		result.append("\tsimilarity: " + similarity + "\n");
		return result.toString();
	}

	public ArrayList<String> getFieldNamesArr() {
		ArrayList<String> result = new ArrayList<String>();
		for (Field field : fields) {
			result.add(field.getName());
		}
		return result;
	}

	public String[] getFieldNames() {
		String[] result = new String[fields.size()];
		return getFieldNamesArr().toArray(result);
	}

}