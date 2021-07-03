package com.arraybase.tm;

import java.util.ArrayList;

import com.arraybase.tm.tables.TTable;

public class TableDescriptor {
	private int count = 0;
	ArrayList<GColumn> props = null;
	private TTable tmd = new TTable();
	private String coreName = null;
	private String msg = "";
	private boolean schema_found = false;
	private String link_reference = null;

	public TableDescriptor() {

	}
	
	public String getLinkReference ()
	{
		return link_reference;		
	}
	public void setLinkReference ( String _link_reference ){
		link_reference = _link_reference;
	}
	
	
	public void setCoreName ( String _core ){
		coreName = _core;
	}
	public String getCoreName ()
	{
		return coreName;
	}

	public void setCount(int size) {
		count = size;
	}

	public int getCount() {
		return count;
	}

	public void setColumns(ArrayList<GColumn> _props) {
		props = _props;
	}

	public ArrayList<GColumn> getColumns() {
		return props;
	}

	public TTable getLibraryDescriptor() {
		return tmd;
	}

	public void setLibraryDescriptor(TTable _item) {
		tmd = _item;
	}

	public String getSchema() {
		return tmd.getTitle();
	}
	public String getUser ()
	{
		return tmd.getUser();
	}

	public void setMsg(String _msg) {
		msg = _msg;
	}

	public boolean isSchema_found() {
		return schema_found;
	}

	public void setSchema_found(boolean schema_found) {
		this.schema_found = schema_found;
	}

	public String getMsg() {
		return msg;
	}
	

}
