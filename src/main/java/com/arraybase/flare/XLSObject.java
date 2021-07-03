package com.arraybase.flare;

import java.util.ArrayList;

public class XLSObject {

	public static String DEFAULT_TYPE = "string";

	private String name = "";
	private String[] fields = null;
	private String[] annotations = null;
	private String[] coms = null;
	private String[] types = null;
	private int data_start_row = 4;
	private int[] ignore_rows = null;
	private String delim = "\\s+";

	public XLSObject(String _name, String _delim) {
		name = _name;
		delim = _delim;
	}

	public String getDelim() {
		return delim;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
		// setTypes(DEFAULT_TYPE);
	}

	public int getStartRow() {
		return data_start_row;
	}

	public void setStartRow(int _start_row) {
		data_start_row = _start_row;
	}

	public String[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(String[] annotations) {
		this.annotations = annotations;
	}

	public String[] getComs() {
		return coms;
	}

	public void setComs(String[] coms) {
		this.coms = coms;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImports() {
		String im = "";
		for (int i = 0; i < fields.length; i++) {
			if (types[i].equalsIgnoreCase("Date"))
				im += "import java.util.Date;\n";
		}
		return im;
	}

	public String getTableName() {
		return getName();
	}

	/**
	 * Set all the type for all the fields to a single value. This is most often
	 * used for setting a default type.
	 * 
	 * @param _default_type
	 */
	public void setTypes(String _default_type) {
		types = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			types[i] = _default_type;
		}
	}

	public void setIgnoreRows(int[] _ignore_rowi) {
		ignore_rows = _ignore_rowi;
	}

	public int[] getIgnoreRows() {
		return ignore_rows;
	}

	public void removeIgnoreRow(int types_rowi) {
		ArrayList<Integer> igt = new ArrayList<Integer>();
		for (int i : ignore_rows) {
			if (i == types_rowi) {

			} else
				igt.add(i);
		}
		ignore_rows = new int[igt.size()];
		int index = 0;
		for (Integer ig : igt) {
			ignore_rows[index++] = ig;
		}
	}

	public void addIgnoreRows(ArrayList<Integer> ignore) {
		ArrayList<Integer> igt = new ArrayList<Integer>();
		if (ignore_rows == null) {
			int index = 0;
			ignore_rows = new int[ignore.size()];
			for (int ii : ignore) {
				ignore_rows[index++] = ii;
			}
		} else {
			for (int i : ignore_rows) {
				igt.add(i);
			}
			igt.addAll(ignore);
			ignore_rows = new int[igt.size()];
			int index = 0;
			for (Integer ig : igt) {
				ignore_rows[index++] = ig;
			}
		}
	}
}
