package com.arraybase.solr.plugin.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "solrQueryParser")
public class SolrQueryParser {
	
	private String defaultOperator;

	public String getDefaultOperator() {
		return defaultOperator;
	}

	@XmlAttribute
	public void setDefaultOperator(String defaultOperator) {
		this.defaultOperator = defaultOperator;
	}

}
