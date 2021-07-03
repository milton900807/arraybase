package com.arraybase.shell.cmds;

import com.arraybase.*;
import com.arraybase.lang.ItrVar;
import com.arraybase.modules.UsageException;
import com.arraybase.util.IOUTILs;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class MapIt implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {
		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) throws UsageException {
		System.out.println("\t : " + cmd);
		Map<String, Object> dictionary = parseDictionary(cmd);
		Set<String> key = dictionary.keySet();
		for (String k : key) {
			Object v = dictionary.get(k);
			System.out.println(k + " = " + v);
		}
		// fpkm_tracking.search(kras)[file]|map(isoforms.fpkm_tracking=hello)>fpkm_tracking.file
		ABTable table = null;
		String newfield = null;

		// {{ IS THERE A WRITE-TO FEATURE }}
		if (cmd.contains(">")) {
			int bindex = cmd.indexOf('>');
			String postcmd = cmd.substring(bindex + 1);

			// we expect a field as the target... and the syntax as follows:
			// path/etc/tablename.field_name

			int lastIndex = postcmd.lastIndexOf('.');
			if (lastIndex < 0) {
				GB.print("It appears you are attempting to write to something that is not a field reference in a table. ");
				GB.print(" Argument should contains something like pathtotable.field");
				return null;
			}
			String table_path = postcmd.substring(0, lastIndex);
			if (!table_path.startsWith("/"))
				table_path = GB.pwd() + "/" + table_path;
			table = new ABTable(table_path);
			try {
				// first we need to check to see if the table exists.
				if (table.exists()) {
					newfield = postcmd.substring(lastIndex + 1);
					// make sure this table has the field...
					if (!table.hasField(newfield)) {
						GB.print("Appears the field " + newfield
								+ "Is not in this table " + table_path);
						return null;
						// fpkm_tracking.search(kras)[directory]|map(rat.b.liver.accepted_hits=b.liver)>fpkm_tracking.file
						// fpkm_tracking.search(kras)[directory]|map(map.properties)>fpkm_tracking.file
					}
				}
			} catch (NodeWrongTypeException e) {
				e.printStackTrace();
			}
		}

		if (input instanceof ItrVar) {
			ItrVar var = (ItrVar) input;
			
			
			Iterator itt = var.get();
			if ( itt instanceof GBSearchIterator ){
				GBSearchIterator gb_search = (GBSearchIterator) itt;
				String search_string = gb_search.getSearchString();
				if ( search_string != null ){
					String[] fields = gb_search.getFields();
					Set<String> dict = dictionary.keySet();
					for ( String df : dict ){
						String dictionary_value = dictionary.get ( df ).toString();
						for ( String field : fields ){
							if ( !field.equalsIgnoreCase("TMID") && (!field.equals("_version_") && (!field.equals("TMID_lastUpdated")))){
							String update_search_ = search_string + " " + field + ":" +  df + " ";
							search_string = search_string.trim ();
							HashMap<String, Object> newmap = new HashMap<String, Object>();
							newmap.put(newfield, dictionary_value);
							table.update(update_search_, newmap);
							}
						}
					}
				}
			}
		}

		return null;
	}

	public GBV execGBVIn_first_pass_very_slow(String cmd, GBV input)
			throws UsageException {
		System.out.println("\t : " + cmd);
		Map<String, Object> dictionary = parseDictionary(cmd);
		Set<String> key = dictionary.keySet();
		for (String k : key) {
			Object v = dictionary.get(k);
			System.out.println(k + " = " + v);
		}
		// fpkm_tracking.search(kras)[file]|map(isoforms.fpkm_tracking=hello)>fpkm_tracking.file
		ABTable table = null;
		String newfield = null;

		// {{ IS THERE A WRITE-TO FEATURE }}
		if (cmd.contains(">")) {
			int bindex = cmd.indexOf('>');
			String postcmd = cmd.substring(bindex + 1);

			// we expect a field as the target... and the syntax as follows:
			// path/etc/tablename.field_name

			int lastIndex = postcmd.lastIndexOf('.');
			if (lastIndex < 0) {
				GB.print("It appears you are attempting to write to something that is not a field reference in a table. ");
				GB.print(" Argument should contains something like pathtotable.field");
				return null;
			}
			String table_path = postcmd.substring(0, lastIndex);
			if (!table_path.startsWith("/"))
				table_path = GB.pwd() + "/" + table_path;
			table = new ABTable(table_path);
			try {
				// first we need to check to see if the table exists.
				if (table.exists()) {
					newfield = postcmd.substring(lastIndex + 1);
					// make sure this table has the field...
					if (!table.hasField(newfield)) {
						GB.print("Appears the field " + newfield
								+ "Is not in this table " + table_path);
						return null;
						// fpkm_tracking.search(kras)[directory]|map(rat.b.liver.accepted_hits=b.liver)>fpkm_tracking.file
						// fpkm_tracking.search(kras)[directory]|map(map.properties)>fpkm_tracking.file
					}
				}
			} catch (NodeWrongTypeException e) {
				e.printStackTrace();
			}
		}

		if (input instanceof ItrVar) {
			ItrVar var = (ItrVar) input;
			Iterator<ArrayList<LinkedHashMap<String, Object>>> it = var.get();
			ArrayList<LinkedHashMap<String, Object>> ita = it.next();
			while (ita != null && ita.size() > 0) {
				for (LinkedHashMap<String, Object> itb : ita) {
					String mappedValues = "";
					HashMap<String, Object> newmap = new HashMap<String, Object>();

					Set<String> keys = dictionary.keySet();
					String tmid = null;
					for (String s : keys) {
						Object dv = dictionary.get(s);
						if (dv != null) {
							Set<String> rowkeys = itb.keySet();
							for (String field : rowkeys) {

								if (field.equals("TMID")) {
									Object dob = itb.get(field);
									tmid = dob.toString();
								} else {
									Object dob = itb.get(field);
									if (dob != null
											&& dob.toString().equalsIgnoreCase(
													s)) {

										newmap.put(newfield, dv);
										mappedValues += dob.toString() + "-->"
												+ dv.toString() + "\t";
									}
								}
							}
						}
					}
					if (mappedValues.length() > 0)
						GB.print("" + mappedValues);
					if (newmap != null && newmap.size() > 0)
						table.update("TMID:" + tmid, newmap);
				}

				ita = it.next();
			}
		}

		return null;
	}

	private Map<String, Object> parseDictionary(String cmd)
			throws UsageException {
		// map(isoforms.fpkm_tracking=test);
		int index = cmd.indexOf('(');
		if (index > 0) {
			String args = GBUtil.parsePArgs(cmd);
			if (args.contains("=")) {
				int argi = args.indexOf('=');
				String start = args.substring(0, argi);
				start = start.trim();
				String end = args.substring(argi + 1);
				end = end.trim();
				LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
				values.put(start, end);
				return values;

			} else {
				// assume this is a properties file.
				File f = new File(args.trim());
				if (!f.exists()) {
					throw new UsageException("Failed to find the file : "
							+ f.getAbsolutePath());
				} else {
					FileReader file_reader = null;
					try {
						file_reader = new FileReader(f);
						Properties p = new Properties();
                        p.load(file_reader);
                        HashMap<String, Object> mapob = new HashMap<String, Object>();
                        Set<String> keys = (Set<String>) p.keys();
                        for (String k : keys) {
                            mapob.put(k, p.getProperty(k));
                        }
                        return mapob;
					} catch (Exception e1) {
						e1.printStackTrace();
						throw new UsageException("Failed to load the file : "
								+ f.getAbsolutePath());
					} finally {
                        IOUTILs.closeResource(file_reader);
                    }
                }
			}
		}

		return null;
	}

}
