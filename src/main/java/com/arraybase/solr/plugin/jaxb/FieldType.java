package com.arraybase.solr.plugin.jaxb;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "fieldType")
@XmlType(propOrder = {"precisionStep","positionIncrementGap", "omitNorms",
		 "sortMissingLast","clazz","name"})
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class FieldType {

	@XmlAttribute
	private String name;
	@XmlAttribute(name = "class")
	private String clazz;
	@XmlAttribute
	private String sortMissingLast;
	@XmlAttribute
	private String omitNorms;
	@XmlAttribute
	private String positionIncrementGap;
	@XmlAttribute
	private String precisionStep;
	

	public FieldType() {
		super();
	}

	public FieldType(String name, String clazz) {
		super();
		this.name = name;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getSortMissingLast() {
		return sortMissingLast;
	}

	
	public void setSortMissingLast(String sortMissingLast) {
		this.sortMissingLast = sortMissingLast;
	}

	public String getOmitNorms() {
		return omitNorms;
	}

	
	public void setOmitNorms(String omitNorms) {
		this.omitNorms = omitNorms;
	}

	public String getPositionIncrementGap() {
		return positionIncrementGap;
	}
	
	public void setPositionIncrementGap(String positionIncrementGap) {
		this.positionIncrementGap = positionIncrementGap;
	}

	public String getPrecisionStep() {
		return precisionStep;
	}
	
	public void setPrecisionStep(String precisionStep) {
		this.precisionStep = precisionStep;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Field:" + "\t");
		result.append("name: " + name + "\t");
		result.append("class: " + clazz + "\t");
		result.append("sortMissingLast: " + sortMissingLast + "\t");
		result.append("omitNorms: " + omitNorms + "\t");
		return result.toString();
	}

}