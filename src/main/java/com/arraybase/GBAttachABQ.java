package com.arraybase;

import com.arraybase.db.JDBC;
import com.arraybase.db.SQLManager;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.io.ABQFile;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.TableManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class GBAttachABQ {

	private String path = null;
	private ArrayList<GColumn> leftColumns = new ArrayList<GColumn>();
	private ArrayList<GColumn> rightColumns = new ArrayList<GColumn>();
	String left = null;
	String right = null;
	private Map<String, String> abq = null;
	private String whereClause = null;

	public GBAttachABQ(String _path, String where_clause) {
		path = _path;
		whereClause = where_clause;
	}

	public Map<String, String> getAbq() {
		return abq;
	}

	public void setAbq(Map<String, String> abq) {
		this.abq = abq;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public ArrayList<GColumn> getLeftColumns() {
		return leftColumns;
	}

	public void setLeftColumns(ArrayList<GColumn> leftColumns) {
		this.leftColumns = leftColumns;
	}

	public ArrayList<GColumn> getRightColumns() {
		return rightColumns;
	}

	public void setRightColumns(ArrayList<GColumn> rightColumns) {
		this.rightColumns = rightColumns;
	}

	public void executeAttachProcess() {

		// 1. loop over a set of objects
		String increment_string = "*:*";
		TableManager manager = new TableManager(GB.getConnectionManager());
		String core = TMSolrServer.getCore(path);

		// 2. update the schema for the new fields.
		// the left cols should only contain the exported values
		for (GColumn c : leftColumns) {
			String type = c.getType();
			String field = c.getName();
			GB.print("Adding the column :" + field + " type " + type);
			manager.addColumn(core, field, type);
		}
		try {

			// 3. Create the statement for query
			String[] fields = new String[rightColumns.size() + 1];
			int index = 0;
			for (GColumn r : rightColumns) {
				fields[index++] = r.getName();
			}
			fields[rightColumns.size()] = "TMID";
			Connection conn = SQLManager.createConnection(abq, 900809+"");
			Statement st = null;
			ResultSet rs = null;
			try {
				st = conn.createStatement();
				// http://www.ncbi.nlm.nih.gov/gene/406959
				// potential target for pa micro-rna targeting smad4
				// http://www.ncbi.nlm.nih.gov/pubmed/17854080 diagnostic already
				// made.

				String query = abq.get(ABQFile.QUERY);

				// 4. Iterate over all fields
				GBSearch search = GB.getSearch();
				Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
						.searchAndDeploy(path, "*:*", null, fields,
								new SearchConfig(SearchConfig.RAW_SEARCH));
				int findex = 0;
				while (it.hasNext()) {
					ArrayList<LinkedHashMap<String, Object>> d = it.next();

					for (LinkedHashMap<String, Object> ob : d) {
						// gb attach local_file gb_file where
						// local_file.field=gb_file.field
						String tmid = (String) ob.get("TMID");
						Object b = ob.get(left);
						if (b == null)
							b = ob.get(left.toLowerCase());
						if (b == null)
							b = ob.get(left.toUpperCase());

						if (b != null) {

							String bvalue = parse(b);
							String q = query + " where " + left.trim() + "="
									+ bvalue;
							GB.print(q);
							rs = st.executeQuery(q);
							LinkedHashMap<String, Object> u_row = new LinkedHashMap<String, Object>();
							while (rs.next()) {
								for (GColumn c : leftColumns) {
									String t = c.getType().toLowerCase();
									String n = c.getName();
									Object o = rs.getObject(n);
									System.out.println("-->" + n + " type  " + t);

									if (t.startsWith("varchar")) {

									}


									update(n, o, tmid);


									if (o != null)
										System.out.println("-->" + o.toString());
								}
							}
						}
						GB.print(d.size() + " index " + findex++);

					}
				}
			} finally {
				JDBC.closeResultSet(rs);
				JDBC.closeStatement(st);
				JDBC.closeConnection(conn);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	private void update(String n, Object o, String tmid) {
		// TODO Auto-generated method stub
		
	}

	private String parse(Object b) {
		String t = b.toString();
		if (t.endsWith(".0"))
			return t.substring(0, t.length() - 2);
		return t;
	}
}
