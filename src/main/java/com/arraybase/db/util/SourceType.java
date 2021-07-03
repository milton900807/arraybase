package com.arraybase.db.util;

public enum SourceType {
	FILES(1, "files", ""), DEFAULT(0, "default", ""), EXCEL(2, "excel", ""), VERIFICATION_SHEET(
			3, "verification_sheet", ""), TISSUE(4, "tissues",
			"These are tissues that are located in the HTL"), HUMAN_FLUID(
			5,
			"fluid",
			"These "
					+ "are samples contained in the Human Fluid Sample Repository "), PATIENTS(
			6, "patients", "List of patients."), VERIFICATION(7,
			"verification", "Checklist for verifying a tissue sample."), TABLE(
			8, "table", "General table for data"), DB(9, "db",
			"General table for data"), REQUESTS(9, "requests",
			"This represents a list of requests"), CELLLINES(10, "celllines",
			"Cell Lines"), STUDIES(11, "studies", "Studies that "
			+ "link to procured human samples"), SAMPLE_HUB(12, "sample_hub",
			"Sample Hub"), HEADER(13, "header_file", "Header file"), NODE(14,
			"node", "A node in an annotation tree"), LINK(15, "link",
			"A link to content"), SIMPLE_TABLE(16, "simple_table",
			"Simple Table"), PATHLIMS(17, "pathlims", "Pathlims"), CLINICAL(18,
			"clinical", "Clinical Trial"), RAW_FILE(19, "raw_file", "File"), DICTIONARY(
			20, "dictionary",
			"A dictionary of terms that can be used to control the fields of other tables."), GBFILE(
			21, "GBFile", "This is the structure of the GRIDBase file"), DIRECTORY(
			22, "Directory",
			" This is a directory that can contains a search index "), DB_TEMPLATE(
			23, "TemplateObject", " "), COLUMN_METATABLE(
			24,
			"columns",
			"This is a table that represents the fields of another table. This table MUST have a fields column."), ROW_METATABLE(
			25, "rows",
			"This is a table that represents the row metadata of another table."), VALUE_TABLE(
			26, "values",
			"This is a table that represents the row metadata of another table."), TABLE_TRIGGER(
			27, "trigger",
			"A action trigger on a particular table.");

	private int id = 0;
	public String name = "";
	private String description = "";

	SourceType(int _id, String _ids, String _desc) {
		id = _id;
		name = _ids;
		description = _desc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param valueAsString
	 * @return
	 */
	public static SourceType getType(String v) {
		if (v.equalsIgnoreCase(SourceType.DEFAULT.name))
			return SourceType.DEFAULT;
		else if (v.equalsIgnoreCase(SourceType.CLINICAL.name))
			return SourceType.CLINICAL;
		else if (v.equalsIgnoreCase(SourceType.HUMAN_FLUID.name))
			return SourceType.HUMAN_FLUID;
		else if (v.equalsIgnoreCase(SourceType.TISSUE.name))
			return SourceType.TISSUE;
		else if (v.equalsIgnoreCase(SourceType.VERIFICATION.name))
			return SourceType.VERIFICATION;
		else if (v.equalsIgnoreCase(SourceType.PATIENTS.name))
			return SourceType.PATIENTS;
		else if (v.equalsIgnoreCase(SourceType.DB.name))
			return SourceType.DB;
		else if (v.equalsIgnoreCase(SourceType.REQUESTS.name))
			return SourceType.REQUESTS;
		else if (v.equalsIgnoreCase(SourceType.CELLLINES.name))
			return SourceType.CELLLINES;
		else if (v.equalsIgnoreCase(SourceType.SAMPLE_HUB.name))
			return SourceType.SAMPLE_HUB;
		else if (v.equalsIgnoreCase(SourceType.HEADER.name))
			return SourceType.HEADER;
		else if (v.equalsIgnoreCase(SourceType.SIMPLE_TABLE.name))
			return SourceType.SIMPLE_TABLE;
		else if (v.equalsIgnoreCase(SourceType.RAW_FILE.name))
			return SourceType.RAW_FILE;
		else if (v.equalsIgnoreCase(SourceType.TABLE.name))
			return SourceType.TABLE;
		else
			return SourceType.DEFAULT;
	}

	public static String getIcon(String n) {
		if (n.equalsIgnoreCase(DEFAULT.getName()))
			return DEFAULT.getIcon();
		if (n.equalsIgnoreCase(LINK.getName()))
			return LINK.getIcon();
		return null;
	}

	private String getIcon() {
		return name + ".png";
	}

	public static Boolean isFolder(String _type) {
		return _type.equalsIgnoreCase(DEFAULT.name)
				|| _type.equalsIgnoreCase(NODE.name);
	}

	public static boolean isIndex(String nodeType) {
		return !isFolder(nodeType);
	}

	public String getDescreiption() {
		return description;
	}

	public static boolean isTable(String nodeType) {

		return nodeType.equalsIgnoreCase(SourceType.DB.name())
				|| nodeType.equalsIgnoreCase(SourceType.TABLE.name);

	}

}
