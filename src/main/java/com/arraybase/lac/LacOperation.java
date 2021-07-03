package com.arraybase.lac;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LacOperation {
	
	private ArrayList<String> lacs = new ArrayList<String> ();
	private HashMap<String, String> params = new HashMap<String, String> ();
	private String operation = "join";// we'll want to enumerate this at some point
	private String userId = null;
	private HashMap<String, Map<String, String>> alias = new HashMap<String, Map<String, String>> ();
	private String new_table_name = "untitled";
	private String lfield = null;
	private String rfield = null;
	private String lLac = null;
	private String rLac = null;
	
	public LacOperation ()
	{
		
	}
	
	public String getNewTableName ()
	{
		return new_table_name;
	}
	public void setNewTableName ( String _table_name ){
		new_table_name = _table_name;
	}
	
	
	public LacOperation (String _type, String _user)
	{
		operation = _type;
		userId = _user;
	}
	
	public String getTarget (int _index)
	{
		String[] lac = LAC.parse(lacs.get ( _index));
		return lac[0];
	}
	public String getAction (int _index)
	{
		String[] lac = LAC.parse(lacs.get ( _index));
		return lac[1];
	}
	
	public String getData (int _index)
	{
		String[] lac = LAC.parse(lacs.get ( _index));
		return lac[2];
	}
		
	public HashMap<String, String> getParams() {
		return params;
	}
	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
	public void addParam(String key, String _value) {
		params.put(key, _value);
	}
	public ArrayList<String> getLacs ()
	{
		return lacs;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

//	public void setLacs(ArrayList<String> lacs) {
//		this.lacs = lacs;
//	}

	public String getLac(int _index) {
		
		return lacs.get(_index);
	}


	public String getUserID() {
		return userId;
	}
	public void setFieldAliasMap(String _key, Map<String, String> aliasMap) {
		alias.put(_key, aliasMap);
	}
	public Map<String, String> getFieldAliasMap ( String _key ){
		return alias.get ( _key );
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public HashMap<String, Map<String, String>> getAlias() {
		return alias;
	}

	public void setAlias(HashMap<String, Map<String, String>> alias) {
		this.alias = alias;
	}

	public String getLfield() {
		return lfield;
	}

	public void setLfield(String lfield) {
		this.lfield = lfield;
	}

	public String getRfield() {
		return rfield;
	}

	public void setRfield(String rfield) {
		this.rfield = rfield;
	}

	public String getlLac() {
		return lLac;
	}

	public void setlLac(String lLac) {
		this.lLac = lLac;
	}

	public String getrLac() {
		return rLac;
	}

	public void setrLac(String rLac) {
		this.rLac = rLac;
	}

	public void setLacs(ArrayList<String> _lacs) {
		lacs = _lacs;
	}

	
	
}
