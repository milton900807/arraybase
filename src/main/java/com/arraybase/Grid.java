package com.arraybase;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.lac.LAC;
import com.arraybase.lac.LACAction;
import com.arraybase.lac.LACActionFactory;
import com.arraybase.lac.LACActionProcess;
import com.arraybase.lac.LACExecException;
import com.arraybase.lac.ListFieldLACActionProcess;
import com.arraybase.lac.SolrServerUtil;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.tm.GResults;
import com.arraybase.tm.TableManager;

public class Grid {

	private DBConnectionManager dbcm = null;
	private String target = null;
	private String action = null;
	private String data = null;
	ArrayList<GColumn> fields = null;

	public Grid(String _lac, DBConnectionManager _db) {
		setLac(_lac);
		dbcm = _db;
	}

	private void setLac(String _lac) {
		String[] l = LAC.parse(_lac);
		target = l[0];
		action = l[1];
		data = l[2];
	}

	public GColumn[] getFields() {
		try {
			fields = SolrServerUtil.getFields(target);
			GColumn[] p = new GColumn[fields.size()];
			return fields.toArray(p);
		} catch (ConnectException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(GRow row) {
		SolrServerUtil.save(target, row);
	}

	public GRow getRow(int index) {
		LACAction lac = LACActionFactory.create(target, LACAction.SEARCH_CORE,
				data);
		try {
			LACSearchProcess search_process = (LACSearchProcess) lac.exec();
			ArrayList<GRow> rows = search_process.getValues();
			if (index >= rows.size())
				return null;
			GRow y_row = rows.get(index);
			return y_row;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public ArrayList<GRow> getRows() {
		LACAction lac = LACActionFactory.create(target, LACAction.SEARCH_CORE,
				data);
		try {
			LACSearchProcess search_process = (LACSearchProcess) lac.exec();
			ArrayList<GRow> rows = search_process.getValues();
			return rows;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the object from the given x and y value
	 * 
	 * @param _x
	 * @param _y
	 * @return
	 */
	public Object getObject(int _x, int _y) {
		if (fields == null)
			getFields();
		GColumn xp = fields.get(_x);
		LACAction lac = LACActionFactory.create(target, LACAction.SEARCH_CORE,
				data);
		try {
			LACSearchProcess search_process = (LACSearchProcess) lac.exec();
			ArrayList<GRow> rows = search_process.getValues();
			if (_y >= rows.size())
				return null;
			GRow y_row = rows.get(_y);

			HashMap objects = y_row.getData();
			Object value = objects.get(xp.getName());
			return value;
		} catch (LACExecException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getColumnCount() {
		if (fields != null && fields.size() > 0)
			return fields.size();
		else {
			getFields();
			return fields.size();
		}
	}

	public int getRowCount() {
		return getRows().size();
	}

	public String getColumn(int i) {
		if (fields != null && i < fields.size())
			return fields.get(i).getName();
		else {
			getFields();
			if (fields != null && i < fields.size())
				return fields.get(i).getName();
		}
		return null;
	}

	public ArrayList<GRow> getRows(String field, String value) {

		TableManager t = new TableManager(dbcm);
		GResults r = t.search(target, field + ":" + value, 0, 1000);
		if (r == null)
			return null;
		ArrayList<GRow> v = r.getValues();
		return v;
	}

}
