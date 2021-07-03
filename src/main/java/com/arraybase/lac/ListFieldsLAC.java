package com.arraybase.lac;

import java.net.ConnectException;
import java.util.ArrayList;

import com.arraybase.tm.GColumn;

public class ListFieldsLAC implements LACAction {

	private String target = null;
	private ArrayList<GColumn> fields = null;

	/**
	 * The target should be a core location
	 * 
	 * @param _target
	 */
	public ListFieldsLAC(String _solrCore_target) {
		target = _solrCore_target;
	}

	public LACActionProcess<ArrayList<GColumn>> exec() throws LACExecException {
		// determine the type of target.
		// in this case we can expect a solr core target object:
		// we can verify this with this:
		// it is possible to have false negative here. ..but no
		// but unlikely
		if (target.startsWith("http")) {
			try {
				fields = SolrServerUtil.getFields(target);
				LACActionProcess<ArrayList<GColumn>> pr = new ListFieldLACActionProcess<ArrayList<GColumn>>(
						fields);
				return pr;
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		LACActionProcess<ArrayList<GColumn>> pr = new ListFieldLACActionProcess<ArrayList<GColumn>>(
				"Connection failed");
		return pr;
	}

	public String getLAC() {
		return null;
	}

}
