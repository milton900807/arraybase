package com.arraybase.lac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.arraybase.tm.GRow;
import com.arraybase.tm.tables.TMTableSettings;

/**
 * A project lac action that will add a project row to the db table.
 * 
 * @author donaldm
 * 
 */
public class ProjectRowLACAction extends TableLACAction {

	private String target = null;
	private String data = null;
	public final static String STUDY_INDEX = "study_index";
	private String sortField = STUDY_INDEX;
	private String sortDirection = "desc";
	public final static String PROJECT_KEY = "project";

	public ProjectRowLACAction(String _target, String _data) {
		super(_target, _data);
		target = _target;
		data = _data;
	}

	public GRow createProject() {
		Map<String, String> _properties = new HashMap<String, String>();
		_properties.put("sort_field", sortField);
		_properties.put("sort_direction", sortDirection);

		TMTableSettings s = new TMTableSettings();
		s.setProperties(_properties);

		ArrayList<GRow> results = search("*:*", s);
		GRow last_row = results.get(0);
		HashMap ___data = last_row.getData();

		String prefix = getSearchPrefix(data);

		ArrayList<GRow> project_results = search("project:" + prefix, s);
		String new_value = increment(project_results);
		Object vl = ___data.get(STUDY_INDEX);
		int study_i = Integer.parseInt(vl.toString());
		study_i++;
		if (last_row != null) {
			GRow new_row = new GRow();
			new_row.set(PROJECT_KEY, new_value);
			new_row.set(STUDY_INDEX, study_i);
			return new_row;
		}
		return null;
	}

	// project_types.add("Collaboration (C-*)");
	// project_types.add("Vendor (V-*)");
	// project_types.add("Internal (X-*)");
	private String getSearchPrefix(String _type) {
		if (_type.equals(ProjectTypes.COLLABORATOR.getName())) {
			return ProjectTypes.COLLABORATOR.getSearchString();
		} else if (_type.equals(ProjectTypes.VENDOR.getName())) {
			return ProjectTypes.VENDOR.getSearchString();
		} else if (_type.equals(ProjectTypes.INTERNAL.getName())) {
			return ProjectTypes.INTERNAL.getSearchString();
		}
		return "";
	}

	/**
	 * Increment the key value
	 * 
	 * @param project_value
	 * @param _project_results
	 * @return
	 */
	private String increment(ArrayList<GRow> _project_results) {
		if ( _project_results.size()<=0){
			return null;
		}
		GRow last_row = _project_results.get(0);
		
		HashMap data = last_row.getData();
		String project_value = (String) data.get(PROJECT_KEY);
		int in = project_value.indexOf('-');
		String prefix = project_value.substring(0, in);
		String s = project_value.substring(in + 1);
		Integer ig = Integer.parseInt(s);
		int increment = ig + 1;
		return prefix + "-" + increment;
	}

	public LACActionProcess exec() throws LACExecException {
		// VERYIFY THE TARGET HAS THE APPROPRIATE FIELDS
		if (SolrServerUtil.verifyFields("study_index")) {

		}
		return null;
	}
}
