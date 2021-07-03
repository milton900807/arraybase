package com.arraybase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.schema.TrieDateField;

import com.arraybase.lac.LAC;
import com.arraybase.tm.GBPathUtils;
import com.arraybase.tm.tables.RowData;
import com.google.gson.Gson;

public class GBUtil {


	/**
	 * does not use beans
	 * 
	 * @param _row
	 * @return
	 */
	public static ArrayList<SolrInputDocument> getDocs(ArrayList<RowData> _row) {
		ArrayList<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int i = 0; i < _row.size(); i++) {
			SolrInputDocument sid = new SolrInputDocument();
			RowData r = _row.get(i);
			HashMap mas = r.getData();
			Set keys = mas.keySet();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if ((!key.equals("delete")) && (!key.equals("save"))
						&& (!key.equals("_checkboxField"))) {
					sid.addField(key, mas.get(key));
				}
			}
			if (sid.getField("TMID_lastUpdated") != null) {
				Date udate = new Date();
				sid.setField("TMID_lastUpdated", udate);
			}
			if (sid.getField("TMID") == null) {
				UUID idOne = UUID.randomUUID();
				sid.addField("TMID", idOne);
			}
			if (sid.getField("TMID_lastUpdated") == null) {
				Date udate = new Date();
				sid.addField("TMID_lastUpdated", udate);
			}

			docs.add(sid);
		}
		return docs;
	}

	public static HashMap<String, String> mapFields(String target,
			HashMap<String, String> fieldMap) {
		HashMap<String, String> nfields = new HashMap<String, String>();
		Set<String> f = fieldMap.keySet();
		for (String field : f) {
			nfields.put(field, target + "_" + field);
		}
		return nfields;
	}

	public static String toGSON(Map<String, Map<String, String>> _map) {
		Gson gson = new Gson();
		String value = gson.toJson(_map);
		return value;
	}

	public static HashMap<String, Map<String, String>> fromGSON(String _value) {
		Gson gson = new Gson();
		HashMap<String, Map<String, String>> value = gson.fromJson(_value,
				HashMap.class);
		return value;
	}

	public static HashMap<String, String> mapTypes(String target,
			HashMap<String, String> _types, HashMap<String, String> _field_map) {
		HashMap<String, String> newFieldMap = new HashMap<String, String>();
		Set<String> fields = _types.keySet();
		for (String f : fields) {
			newFieldMap.put(_field_map.get(f), _types.get(f));
		}
		return newFieldMap;
	}

	/**
	 * Get the schema name from the sorl url
	 * 
	 * @param solrUrl
	 * @return
	 */
	public static String getSchema(String solrUrl) {

		if (solrUrl.endsWith("/")) {
			solrUrl = solrUrl.substring(0, solrUrl.length() - 1);

		}
		int index = solrUrl.lastIndexOf('/');
		String schema = solrUrl.substring(index + 1);
		if (schema != null)
			schema = schema.trim();
		return schema;
	}

	public static void write(String resourceAsString, File file)
			throws IOException {
		FileWriter fl = new FileWriter(file);
		try {
			fl.write(resourceAsString);
			fl.flush();
		} finally {
			fl.close();
		}
	}

	public static String[] parse(String type_command) {

		int index = type_command.indexOf("->");
		String t = type_command.substring(0, index);
		String tt = type_command.substring(index + 2);
		String[] l = new String[2];
		l[0] = t;
		l[1] = tt;
		return l;
	}

	public static String catPath(String _path, String _name) {
		_path = GBPathUtils.adjustPathChars(_path);
		String name = _name;
		if (_name.startsWith("/"))
			name = name.substring(1);
		if (!_path.endsWith("/"))
			return _path + "/" + name;
		else
			return _path + name;
	}

	public static String getSolrRootURL(String site) {
		site = site.trim();
		if (site.endsWith("/")) {
			site = site.substring(0, site.length() - 1);
		}
		int li = site.lastIndexOf('/');
		String solr = site.substring(0, li);
		return solr;
	}

	/**
	 * Strip the character from the string
	 * 
	 * @param s
	 * @param del
	 * @return
	 */
	private static String strip(String s, String del) {
		String t = s;
		t = t.replace(del, "\r");
		return t.trim();
	}

	public static String[] parse(String key, String arg) {
		int index = arg.indexOf('=') + 1;
		String line = arg.substring(index);
		String[] types = line.split(",");

		return types;
	}

	/**
	 * This will strip the options param from the _args list
	 * 
	 * @param _args
	 * @return
	 */
	public static String[] parseOptions(LinkedHashMap<String, String> values,
			String[] _args) {
		// LinkedHashMap<String, String> values = new LinkedHashMap<String,
		// String>();
		ArrayList<String> com = new ArrayList<String>();
		for (String _s : _args) {
			if (_s.startsWith("-")) {
				if (_s.indexOf('=') > 0) {
					String[] k = _s.split("=");
					values.put(k[0], k[1]);
				} else
					values.put(_s, _s);
			} else
				com.add(_s);
		}
		String[] st = new String[com.size()];
		st = com.toArray(st);
		return st;
	}

	/**
	 * Get the string version of the array command
	 * 
	 * @param _args
	 * @return
	 */
	public static String getStringFromCommand(String[] _args) {
		String t = "";
		for (String tt : _args) {
			t += tt + "\r";
		}
		return t.trim();
	}

	public static String trunc(String name) {
		if (name != null) {
			if (name.length() > 4) {
				return name.substring(0, 4) + "..";
			} else
				return name + "..";
		}
		return "?";
	}

	/**
	 * Remove the item from the array
	 * 
	 * @param i
	 * @param _args
	 * @return
	 */
	public static String[] remove(int index, String[] _args) {
		String[] t = new String[_args.length - 1];
		for (int i = 0; i < _args.length; i++) {
			if (i == index) {
				continue;
			} else {
				t[i++] = _args[i];
			}
		}
		return t;
	}

	/**
	 * parse the string between parens
	 * 
	 * @param val
	 * @return
	 */
	public static String parsePArgs(String val) {
		int fi = val.indexOf('(');
		int li = val.lastIndexOf(')');
		String te = val.substring(fi + 1, li);
		te = te.trim();
		return te;
	}

	/**
	 * Get the value string from the name=value pair
	 * 
	 * @param nvp
	 * @return
	 */
	public static String parseValueFromNVP(String nvp) {
		if (nvp.contains("=")) {
			String[] sp = nvp.split("=");
			return sp[0];
		}
		return null;
	}

	public static String parseTarget(String command) {
		return LAC.getTarget(command);
	}

	public static String parsePath(String command) {
		String target = parseTarget(command);
		if (target.startsWith("/"))
			return target;
		String currentpath = GB.pwd();
		return currentpath + "/" + target;
	}

	/**
	 * a METHOD THAT prepares a search string for restful queries
	 * 
	 * @param _searchString
	 * @return
	 */
	public static String prepareSearchString(String _s) {
		// if ( _s.indexOf('"')>=0)
		// {
		// int st = _s.indexOf('"');
		// int ed = _s.indexOf('"', st+1);
		// String sub = _s.substring(st+1, ed);
		// String sub2 = escapeIt ( sub );
		// _s = _s.replace(sub, sub2);
		// }
		return _s;
	}

	public static Double toDouble(Object ob) {
		if (ob != null) {
			if (ob instanceof Number) {
				Double dd = ((Number) ob).doubleValue();
				return dd;
			} else {
				try {
					String g = ob.toString();
					Double d = Double.parseDouble(g);
					return d;
				} catch (NumberFormatException df) {
					df.printStackTrace();
				}
			}
		}
		return null;
	}

}
