package com.arraybase.solr.plugin.jaxb;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "field")
@XmlType(propOrder = {"multiValued","required", "stored","indexed", "type","name"})
public class Field {

	@XmlAttribute
	private String name;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private String indexed;
	@XmlAttribute
	private String stored;
	@XmlAttribute
	private String required;
	@XmlAttribute
	private String multiValued;
	
	public Field() {
		super();
	}
	
	public Field(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getIndexed() {
		return indexed;
	}
	
	public void setIndexed(String indexed) {
		this.indexed = indexed;
	}
	public String getStored() {
		return stored;
	}
	
	public void setStored(String stored) {
		this.stored = stored;
	}
	public String getRequired() {
		return required;
	}
	
	public void setRequired(String required) {
		this.required = required;
	}
	public String getMultiValued() {
		return multiValued;
	}
	
	public void setMultiValued(String multiValued) {
		this.multiValued = multiValued;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Field:" + "\t");
		result.append("name: " + name + "\t");
		result.append("type: " + type + "\t");
		result.append("indexed: " + indexed + "\t");
		result.append("stored: " + stored + "\t");
		result.append("required: " + required + "\t");
		result.append("multiValued: " + multiValued + "\n");
		return result.toString();
	}

}