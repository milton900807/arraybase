package com.arraybase.flare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.tm.TableManager;
import com.arraybase.util.ABProperties;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;

public class XLSIntegration {
	GBLogger log = GBLogger.getLogger(XLSIntegration.class);
	private DBConnectionManager dbcm = null;

	public XLSIntegration(DBConnectionManager db) {
		dbcm = db;
	}

	private void updateLib(File fi, Map newValues) {
		// System.out.println(" we are importing a csv file into the library ");
		String userName = (String) newValues.get("user");
		String name = (String) newValues.get("schema");
		String security = (String) newValues.get("security");
		String delim = (String) newValues.get("delimiter");
		// dataType=Text
		// {afdad={dataType=Text, dictionary=No_Dictionary, fieldName=afdad}}
		HashMap<String, Map<String, String>> _params = new HashMap<String, Map<String, String>>();
		BufferedReader pst = null;
		BufferedReader rd = null;
        OutputStreamWriter wr = null;
        HttpURLConnection conn = null;
		try {
			String _schema = NameUtiles.prepend(userName, name);
			String value = fi.getName();
			InputStream str = new FileInputStream(fi);
			pst = new BufferedReader(new InputStreamReader(str));
			String line = pst.readLine();
			String da = "";

			String[] head = line.split(delim);
			if (!line.contains(delim)) {
				head = new String[1];
				head[0] = line;
			}
			String header_list = buildHeader(head, delim);
			for (int i = 0; i < head.length; i++) {
				HashMap<String, String> values = new HashMap<String, String>();
				values.put("dictionary", "NO_Dictionary");
				values.put("fieldName", "" + head[i]);
				values.put("dataType", "Text");
				values.put("multiValued", "false");
				_params.put("" + head[i], values);
			}

			System.out.println(header_list);

			// build the schema.
			TableManager tmd = new TableManager(dbcm);
			tmd.build(userName, TableManager.TMSOLR,
					NameUtiles.prepend(userName, name), "", "1", _params, null);
			log.debug(" header list " + header_list);

			line = pst.readLine();
			int index = 0;
			while (line != null) {
				if (line != null)
					da += buildLine(line, delim);
				line = pst.readLine();
				System.out.println(" line " + line);
				index++;
			}
			log.debug(" total lines read to be loaded... " + index);
			String solr_url = ABProperties.get(ABProperties.SOLRSITE);
			if (!solr_url.endsWith("/")) {
				solr_url += "/";
			}
			URL url = new URL(solr_url + _schema + "/update/csv");
			String data = URLEncoder.encode("stream.body", "UTF-8") + "="
					+ URLEncoder.encode(da, "UTF-8");

			data += "&" + URLEncoder.encode("header", "UTF-8") + "="
					+ URLEncoder.encode("true", "UTF-8");

			data += "&" + URLEncoder.encode("fieldnames", "UTF-8") + "="
					+ URLEncoder.encode("" + header_list, "UTF-8");
			// f.tags.split=true&f.tags.separator=%20&f.tags.encapsulator='

			data += "&" + URLEncoder.encode("separator", "UTF-8") + "="
					+ URLEncoder.encode("" + delim, "UTF-8");

			data += "&" + URLEncoder.encode("escape", "UTF-8") + "="
					+ URLEncoder.encode("\\", "UTF-8");

			data += "&" + URLEncoder.encode("f.tags.split", "UTF-8") + "="
					+ URLEncoder.encode("false", "UTF-8");
			data += "&" + URLEncoder.encode("encapsulator", "UTF-8") + "="
					+ URLEncoder.encode("\"", "UTF-8");
			data += "&" + URLEncoder.encode("commit", "UTF-8") + "="
					+ URLEncoder.encode("true", "UTF-8");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			String bytes = Integer.toString(data.getBytes().length);
			log.debug(" data : " + bytes);
			conn.setRequestProperty("Content-Length",
                    "" + Integer.toString(data.getBytes().length));
			conn.setRequestProperty("Content-Language", "en-US");
			conn.setDoOutput(true);
			log.debug(" url\n" + url.toExternalForm());
			wr = new OutputStreamWriter(
					conn.getOutputStream());
			data = data.trim();
			wr.write(data);
			wr.flush();
			// Get the response
			rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			wr.close();
			rd.close();
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            IOUTILs.closeResource(pst);
            IOUTILs.closeResource(wr);
            IOUTILs.closeResource(rd);
            conn.disconnect();
        }
    }

	private String buildHeader(String[] line, String _del) {
		String header = "";
		for (int i = 0; i < line.length; i++) {
			header += line[i];
			header += _del;
		}
		return header + "TMID" + _del + "TMID_lastUpdated";
	}

	private static String buildLine(String _line,
			ArrayList<String> schemaColumns, LinkedHashMap valuesMap) {
		if (_line == null)
			return null;
		System.out.println(" \n:::" + _line);
		String[] data = _line.split(",");
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i].replace('"', '\r');
			if (data[i] != null)
				data[i] = data[i].trim();
		}

		String line = "";
		for (int i = 0; i < schemaColumns.size(); i++) {
			String title = schemaColumns.get(i);
			Object ob = valuesMap.get(title);
			if (ob != null) {
				Integer obi = Integer.parseInt((String) ob);
				if (obi < data.length) {
					line += "\"" + data[obi.intValue()] + "\"";
					if ((i + 1) < schemaColumns.size())
						line += ",";
				}
			}
			// line += title + ",";
		}
		line = line.trim();
		if (line.endsWith(","))
			line = line.substring(0, line.length() - 1);

		Date udate = new Date();
		String random = TMID.create(); // --this line was this --> Math.random()
										// + "__" + new Date().toGMTString();
										// and that was wrong
		line += ",\"" + random + "\",\"" + udate.toGMTString() + "\"\n";
		return line;
	}

	private String buildLine(String _line, String _delim) {
		if (_line == null)
			return null;
		String line = "";
		line = _line.trim();
		Date udate = new Date();
		String random = TMID.create(); // --this line was this --> Math.random()
										// + "__" + new Date().toGMTString();
										// and that was wrong
		line += _delim + random + _delim + udate.toGMTString() + "\n";
		return line;
	}

	private String buildHeaderLine(ArrayList<String> schemaColumns,
			LinkedHashMap valuesMap) {
		String line = "";
		for (int i = 0; i < schemaColumns.size(); i++) {

			String title = schemaColumns.get(i);
			if (title.equals("TMID") || title.equals("TMID_lastUpdated")) {

			} else {
				line += title;
				if ((i + 1) < schemaColumns.size())
					line += ",";
			}
		}
		if (line.endsWith(",")) {
			line = line.substring(0, line.length() - 1);
		}
		line = line.trim();
		return line + ",TMID," + "TMID_lastUpdated";
	}

	/**
	 * ' Create a table from the xls file 'fi'
	 * 
	 * @param name
	 * @param userName
	 * @param _params 
	 * @param fi
	 */
	public ProcessReport buildTableFromXLSFile(String _tableName,
			String userName, String description, File _localfile, Map<String, String> _params) {
		try {
			// String _schema = NameUtiles.prepend(userName, name);
			if (description == null)
				description = "";
			InputStream str = new FileInputStream(_localfile);
			ProcessReport p = new ErrorReport();

			try {
				LoadTableToSolr sl = new LoadTableToSolr();
				// FileInputStream st = new FileInputStream(f);
				XLSObject xls_ob = sl.createGMObject(str,
						NameUtiles.prepend(userName, _tableName));
				xls_ob = rename_fields_for_solr(xls_ob);
				
				
				// {{ CONSTRUCT THE SOLR FIELDS FROM THE XLS OBJECT
				HashMap<String, Map<String, String>> params = buildSolrFields(xls_ob);
				// this is where we create the solr schema...
				params = TMSolrServer.appendTMFields(params);
				// {{ SAVE THE LIBRARY IN THE DATABASE }}
				TableManager tmd = new TableManager(dbcm);
				tmd.build(userName, TableManager.TMSOLR,
						NameUtiles.prepend(userName, _tableName), description,
						"1", params, null);
				String[] fields = xls_ob.getFields();
				// for (String field : fields) {
				// log.debug("fields : " + field);
				// }
				log.debug(" XLS ANNOTATION FILE COMPLETE ");
				InputStream st2 = new FileInputStream(_localfile);
				LoadTableToSolr.load(st2, xls_ob);

				// we have a successful report
				p = new SuccessReport(xls_ob.getName());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (LoaderException e) {
				e.printStackTrace();
				ErrorReport er = new ErrorReport();
				er.addError("Loader Error", e.getMessage());
				return er;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: add information that may be recorded (I.E. BENCHMARK TIME
			// ETC )2
			return p;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		// TODO: add information to help qualify the failure.
		return new FailureReport();
	}

	public static HashMap<String, Map<String, String>> buildSolrFields(
			XLSObject _ob) {
		String[] fields = _ob.getFields();
		String[] type = _ob.getTypes();
		if (type == null) {

			type = new String[fields.length];
			for (int i = 0; i < fields.length; i++) {
				type[i] = "String";
			}
		}
		LinkedHashMap<String, Map<String, String>> params = new LinkedHashMap<String, Map<String, String>>();
		for (int i = 0; i < fields.length; i++) {
			HashMap<String, String> field = new HashMap<String, String>();
			field.put("fieldName", fields[i]);
			field.put("sortable", "true");
			field.put("indexed", "true");
			field.put("defaultString", "");
			field.put("dataType", type[i]);
			field.put("requiredField", "false");
			params.put("" + fields[i], field);
		}
		return params;
	}

	public static XLSObject rename_fields_for_solr(XLSObject xls_ob) {
		String[] fields = xls_ob.getFields();
		int index = 0;
		for (int l = 0; l < fields.length; l++) {
			index = 0;
			String t = fields[l].trim();
			t = t.replace(':', '_');
			t = t.replace(' ', '_');
			t = t.replace("#", "_Num_");
			t = t.replace('/', '_');
			t = t.replace('\\', '_');
			t = t.replace('$', '_');
			t = t.replace(',', '_');
			t = t.replace("(", "__");
			t = t.replace(")", "__");
			t = t.replace(',', '_');
			t = t.replace("%", "_percent_");
			fields[l] = t;
		}
		fields = mkdistinct(fields, index);
		xls_ob.setFields(fields);
		return xls_ob;
	}

	public static String[] mkdistinct(String[] fields, int index) {
		for (int l = 0; l < fields.length; l++) {
			index = 0;
			String t = fields[l].trim();
			if (!isDistinct(fields, t))
				t = increment(fields, t, index++);
			fields[l] = t.trim();
		}
		return fields;
	}

	public static String increment(String[] fields, String t, int i) {
		String tt = t + i;
		for (int j = 0; j < fields.length; j++) {
			// String d = fields[j].trim();
			if (tt.equalsIgnoreCase(fields[j].trim())) {
				tt = increment(fields, tt + i, i++);
			}
		}
		return tt;
	}

	public static boolean isDistinct(String[] fields, String t) {
		int index = 0;
		for (int j = 0; j < fields.length; j++) {
			if (t.equalsIgnoreCase(fields[j].trim())) {
				if (index > 0)
					return false;
				index++;
			}
		}
		return true;
	}

}