package com.arraybase.tm;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


/**
 *  Node facets are labeled json objects
 * @author milton
 *
 */
public class NodeFacet {
	
	private String name = null;
	private List<String> values = new ArrayList<String> ();
	
	public NodeFacet()
	{
		
	}
	public NodeFacet ( String _json ){
		Gson g = new Gson ( );
		NodeFacet f = g.fromJson(_json, NodeFacet.class);
		name = f.getName();
		values = f.getValues();
	}
	public String toJSON ( )
	{
		Gson g = new Gson ( );
		String json = g.toJson(this);
		return json;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	

}
