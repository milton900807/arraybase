package com.arraybase.lac;

import java.util.ArrayList;

import com.arraybase.LACSearchProcess;
import com.arraybase.db.DBConnectionManager;
import com.arraybase.tm.GRow;
import com.arraybase.tm.GResults;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tables.TMTableSettings;

public class SearchCoreLACAction implements LACAction {

	String core = null;
	String action = null;
	String data = null;
	int _start = 0;
	int _rows = 1000;

	/**
	 * given the core url and the action... Data format can be variable but the
	 * default expected value is: searchString... e.g. *:* for searching
	 * everything. if a comma exists then we can expect a range result.
	 * 
	 * @param _target
	 * @param _action
	 * @param _data
	 *            (examples 1: *:*, 0, 1000 --> is search all and return the
	 *            first 1000 row results.)
	 */
	public SearchCoreLACAction(String _target, String _action, String _data) {
		core = _target;
		action = _action;
		setData(_data);
	}

	public void setData(String _data) {
		if (_data.contains(",")) {
			String[] sp = _data.split(",");
			data = sp[0].trim();
			try {
				Integer min = Integer.parseInt(sp[1].trim());
				Integer max = Integer.parseInt(sp[2].trim());
				_start = min;
				_rows = max;
			} catch (Exception _E) {
				_E.printStackTrace();
			}
		} else
			data = _data;
	}

	public LACActionProcess exec() throws LACExecException {
		DBConnectionManager db = new DBConnectionManager();
		TableManager service = new TableManager(db);
		GResults r = service.search(core, data, _start, _rows, null);
		ArrayList<GRow> rows = r.getValues();
		LACSearchProcess search = new LACSearchProcess(null);
		search.setResults(rows);
		return search;
	}
	public String getLAC ()
	{
		return core + "." + action + "(" + data + ")";
	}

}
