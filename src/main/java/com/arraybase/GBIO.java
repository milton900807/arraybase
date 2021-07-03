package com.arraybase;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.util.GBLogger;
import com.arraybase.util.GBRGX;
import com.arraybase.util.IOUTILs;

public class GBIO {

	static GBLogger log = GBLogger.getLogger(GBIO.class);
	static File logs = new File("logs");

	static {
		logs.mkdir();
	}

	/**
	 * @param results
	 * @param out
	 * @param _cols
	 */
	public static void printResults(ABaseResults results, PrintStream out,
			String[] _cols) {
		ArrayList<GRow> rows = results.getValues();
		ArrayList<GColumn> cols = results.getColumns();
		SortedMap<String, String> map = new TreeMap<String, String>();
		if (_cols != null && _cols.length > 0) {
			String top = "";
			for (String c : _cols) {
				top += c + "\t\t";
			}
			if (out != null)
				out.println(top.trim());

			for (GRow r : rows) {
				Map values = r.getData();
				String line = "";

				for (String columnstr : _cols) {

					for (GColumn c : cols) {
						if (columnstr.equalsIgnoreCase(c.getName())) {
							Object ov = values.get(c.getName());
							if (ov != null) {
								String v = ov.toString();
								line += v + "\t\t";
							} else
								line += "----\t\t";
						}
					}
				}
				if (out != null)
					out.println(line);
			}
		} else {
			for (GRow r : rows) {
				Map values = r.getData();
				String line = "";
				for (GColumn c : cols) {
					Object ov = values.get(c.getName());
					if (ov != null) {
						String v = ov.toString();
						line += v + "\t\t";
					} else
						line += "----\t\t";
				}
				if (out != null)
					out.println(line);
			}
		}
		Set<String> ss = map.keySet();
		for (String s : ss) {
			log.debug("\t\t" + s);
		}
		if (out != null)
			out.flush();
	}

	public static void printResults(ABaseResults results, PrintStream out) {
		ArrayList<GRow> rows = results.getValues();
		ArrayList<GColumn> cols = results.getColumns();

		if (rows.size() <= 0) {
			out.println("No results found.");
			return;
		}
		String titleLine = "";
		for (int i = 0; i < 1; i++) {
			GRow r = rows.get(i);
			Map values = r.getData();
			String line = "";
			for (GColumn c : cols) {
				titleLine += c.getName() + "\t\t";
			}
		}
		out.println(titleLine);
		for (GRow r : rows) {
			Map values = r.getData();
			String line = "";
			for (GColumn c : cols) {
				Object ov = values.get(c.getName());
				if (ov != null) {
					String v = ov.toString();
					line += v + "\t\t";
				} else
					line += "----\t\t";
			}
			if (out != null)
				out.println(line);
		}
	}

	private static boolean in(String name, String[] _cols) {
		if (_cols == null)
			return true;
		for (String s : _cols) {
			if (name.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	public static BufferedReader getInputStream(String file_name)
			throws FileNotFoundException {
		FileReader reader = new FileReader(file_name);
		BufferedReader br = new BufferedReader(reader);
		return br;
	}

	public static File createLogFile(String string) {

		File f = new File(logs, string + ".log");
		if (f.exists()) {
			return createLogFile(string, 1);
		}
		return f;
	}

	public static File createLogFile(String string, int _index) {
		File f = new File(logs, string + _index + ".log");
		if (f.exists()) {
			return createLogFile(string, ++_index);
		}
		return f;
	}

	public static PrintStream createLogStream(String string) {
		File f = createLogFile(string);
		try {
			PrintStream pr = new PrintStream(f);
			return pr;
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public static void setFieldNames(File _f, int _index) throws IOException {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(_f));
			String line = r.readLine();
			int index = 0;
			if (line != null) {
				if (_index == index) {
					String[] firstLine = line.split("\\t+");
					LinkedHashMap<String, String> newFields = getFields(firstLine);
					printFields(_f, newFields, "\\t+", index);
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(r);
		}
	}

	/**
	 * 
	 * @param newFields
	 * @param string
	 * @param index
	 * @throws IOException
	 */
	private static void printFields(File _f,
			LinkedHashMap<String, String> field_map, String delim, int index)
			throws IOException {
        BufferedReader r = null;
        PrintStream pr = null;
        File ff = null;
        try {
            ff = new File(_f.getParentFile(), "." + _f + ".lock");
            r = new BufferedReader(new FileReader(_f));
            pr = new PrintStream(ff);
            String line = r.readLine();
            int lindex = 0;

            Set<String> original_fields = field_map.keySet();
            String[] newFields = new String[original_fields.size()];

            while (line != null) {
                if (lindex == index) {
                    String t = "";
                    for (String s : newFields) {
                        t += s + delim;
                    }
                    pr.println(t);
                } else
                    pr.println(line);
                lindex++;
            }
        } finally {
            IOUTILs.closeResource(pr);
            IOUTILs.closeResource(r);
        }
		File final_file = new File(_f.getParentFile(), _f.getName());
		ff.renameTo(final_file);
	}

	/**
	 * get the new fields from the user
	 * 
	 * @param firstLine
	 * @return
	 */
	private static LinkedHashMap<String, String> getFields(String[] fieldLine) {
		LinkedHashMap<String, String> new_fields = new LinkedHashMap<String, String>();
		Console co = System.console();
		String input = co
				.readLine("Type a new name and hit [enter] to rename field. "
						+ "\n Simply hit [Enter] to skip. "
						+ "\n [r] to start over, [c] to exit");
		if (input.equalsIgnoreCase("c")) {
			for (String field : fieldLine) {
				String field_name = co.readLine(field + "==> New name: ");
				if (field_name.equalsIgnoreCase("r")) {
					return getFields(fieldLine);
				}
				if (field_name == null || field_name.length() <= 0) {
					new_fields.put(field, field);
				} else
					new_fields.put(field, field_name);
				co.printf("field: " + field_name);
			}
		}
		return new_fields;
	}

	/**
	 * Provide a way to insert a type into a file.
	 * 
	 * @param _args
	 * @return
	 * @throws IOException
	 */
	public static void insertTypeValues(String[] _args) throws IOException {
		String file_name = _args[2];
		File f = new File(file_name);
		if (!f.exists()) {
			throw new FileNotFoundException("Failed to find the file : "
					+ f.getAbsolutePath());
		}
		FileReader freader = new FileReader(f);
		BufferedReader bf = new BufferedReader(freader);
        try {
            String l = bf.readLine();
            String delim = "\\t+";
            String[] firstLine = l.split("\\t+");
            Console console = System.console();
            String input = console
                    .readLine("This utility application will allow you to define field names and field types."
                            + "\n  [enter] to start [c] to exit\n\n");
            if (input.equalsIgnoreCase("c"))
                GB.exit(1);
            print("The fields for this file:");
            for (String s : firstLine) {
                print(s);
            }
            bf.mark(0);

            print("Is this correct?");
            input = console.readLine("[enter] to continue or [c] to exit.\n" + "");
            if (input.equalsIgnoreCase("c"))
                GB.exit(1);

            LinkedHashMap<String, String> new_fields = getFields(firstLine);
            print("\n\nNext add types for each field (s=string, f=float, b=boolean, i=integer, d=date)");
            LinkedHashMap<String, String> field_types = new LinkedHashMap<String, String>();
            for (int i = 0; i < firstLine.length; i++) {
                String s = firstLine[i];
                print(bf, i, s);
                input = console.readLine("Type for field \": " + s + "\"--> ");
                if (input == null) {
                    GB.print(" " + s + " is set to type string");
                    input = "string";
                }
                GB.print(" input : " + input);
                field_types.put(s, input.trim());
            }
            prompForFileModification(f, new_fields, delim, field_types);
        } finally {
            IOUTILs.closeResource(bf);
            IOUTILs.closeResource(freader);
        }
	}

	/**
	 * Prompt the user for entering new fields and types
	 * 
	 * @param f
	 */
	public static void promptUserForFieldsAndTypes(File f) {
		try {
			LinkedHashMap<String, String> values = assignFieldsToFile(f, 2);
			insertFieldsAndTypes(f, values);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * inser the field name and the type into the first two lines of a file.
	 * 
	 * @param file
	 * @param types
	 * @throws IOException
	 */
	public static void insertFieldsAndTypes(File file,
			LinkedHashMap<String, String> types) throws IOException {

        BufferedReader br = null;
        PrintStream pr = null;

        try {
            br = new BufferedReader(new FileReader(file));
            File new_file = new File(file.getParentFile(), file.getName() + ".lock");
            pr = new PrintStream(new_file);
            // print the first two lines
            Set<String> keys = types.keySet();
            String title_line = "";
            String type_line = "";
            for (String k : keys) {
                title_line += k + "\t";
                type_line += types.get(k);
            }
            pr.println(title_line);
            pr.println(type_line);
            String line = br.readLine();
            // print the rest of the file
            while (line != null) {
                pr.println(line);
            }
            pr.flush();
        } finally {
            IOUTILs.closeResource(br);
            IOUTILs.closeResource(pr);
        }
	}

	/**
	 * Assign fields to the file using the row number at index
	 * 
	 * @throws IOException
	 */
	public static LinkedHashMap<String, String> assignFieldsToFile(File file,
			int index) throws IOException {
        BufferedReader re = null;
        LinkedHashMap<Integer, String[]> map = null;
        LinkedHashMap<String, String> fieldnames = null;
        try {
            re = new BufferedReader(new FileReader(file));
            String l = re.readLine();
            int lindex = 1;
            fieldnames = new LinkedHashMap<String, String>();
            map = new LinkedHashMap<Integer, String[]>();
            boolean rangeFound = false;
            while (l != null) {
                if (lindex <= (index + 2) && lindex >= (index - 2)) {
                    String[] split = l.split("\\t+");
                    for (int i = 0; i < split.length; i++) {
                        String s = split[i];
                        print(s);
                    }
                    map.put(lindex, split);
                    rangeFound = true;
                }
                if (rangeFound)
                    break;
                lindex++;
            }
        } finally {
            IOUTILs.closeResource(re);
        }

		Console c = System.console();
		Set<Integer> keys = map.keySet();
		for (Integer k : keys) {
			String[] values = map.get(k);
			for (String s : values) {
				print(s);
			}
			String input = null;
			while (input == null) {
				input = c.readLine("Name this field: ");
			}
			String type_input = null;
			while (type_input == null) {
				type_input = c
						.readLine("Enter a type (s=string, i=integer, f=float, d=date): ");
			}
			fieldnames.put(input, type_input);
		}
		return fieldnames;
	}

	/**
	 * 
	 * @param _f
	 * @param _l
	 */
	public static void insertTypeRow(File _f, String[] _fields, String delim,
			LinkedHashMap<String, String> _l) {
        BufferedReader reader = null;
        PrintStream st = null;
        File output = null;
		try {
			Console c = System.console();
			output = new File(_f.getParent(), "." + _f.getName() + ".lock");
            reader = new BufferedReader(new FileReader(_f));
            st = new PrintStream(output);

            String line = reader.readLine();

			st.println(line);
			Set<String> keys = _l.keySet();
			String type_line = "";
			for (String s : keys) {
				String type = _l.get(s);
				type_line += type += "\t";
			}
			st.println(type_line);
			line = reader.readLine();
            PrintWriter con = c.writer();
			con.println("writing...");
			while (line != null) {
				st.println(line);
				line = reader.readLine();
            }
			con.println("Complete");
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
            IOUTILs.closeResource(reader);
            IOUTILs.closeResource(st);
            output.renameTo(_f);
        }
    }

	private static String buildLine(String[] _fields, String delim) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void prompForFileModification(final File f,
			LinkedHashMap<String, String> field_map, final String delim,
			final LinkedHashMap<String, String> field_types) {
		print(" The following fields are assigned with types: ");
		Runnable r = new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception _e) {
					_e.printStackTrace();
				}
				Set<String> keys = field_types.keySet();
				String[] new_fields = new String[keys.size()];
				int index = 0;
				for (String s : keys) {
					String value = field_types.get(s);
					GB.print(s + " " + value);
					new_fields[index++] = s;
					try {
						Thread.sleep(50);
					} catch (Exception _e) {
						_e.printStackTrace();
					}
				}
				Console console = System.console();
				String input = console
						.readLine("Modify the file with these types and field names"
								+ "? Yes [y] or [enter], No [n]");
				if (input == null || input.equalsIgnoreCase("y")) {
					insertTypeRow(f, new_fields, delim, field_types);
				}

			}
		};
		Thread t = new Thread(r);
		t.start();
	}

	public static void clip(File _f, int _lineNumber)
			throws FileNotFoundException {

		BufferedReader re = new BufferedReader(new FileReader(_f));
		File director = _f.getParentFile();
		File _temp = new File(director, "." + _f.getName() + ".writing");
		PrintStream pr = new PrintStream(_temp);

		try {
			String line = re.readLine();
			int index = 0;
			while (line != null) {
				if (index != _lineNumber)
					pr.println(line);
				line = re.readLine();
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            IOUTILs.closeResource(re);
            IOUTILs.closeResource(pr);
            _temp.renameTo(_f);
        }
    }

	private static void print(BufferedReader bf, int index, String _field) {
		try {
			print("------");
			print("Field:" + _field);
			print("-----");
			bf.reset();
			int line_index = 0;
			String line = bf.readLine();
			while (line != null) {
				String[] tt = line.split("\\t+");
				if (tt != null && tt.length > index)
					print(tt[index]);
				if (line_index > 9)
					break;
				line_index++;
				line = bf.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void print(String s) {
		GB.print(s);

	}

	public static void printOut(String[] command) {
		for (String c : command)
			System.out.println(c);

	}

	public static String parsePath(String command) {

		int ind = command.indexOf('[');
		if (ind <= 0)
			ind = command.length();
		String lac = command.substring(0, ind);
		int first_paran = command.indexOf('(');
		if (first_paran <= 0)
			first_paran = command.length();
		int action = command.indexOf('.');
		// if there is no action the we only have a path!
		if (action <= 0) {
			return command.trim();
		}
		if (lac.startsWith("/")) {
			String path = command.substring(0, action);
			return path;
		} else {

			// no support for relative paths at the moment.

			String target = command.substring(0, action);
			String p = GB.pwd() + "/" + target;
			return p;
		}
	}

	public static String[] parseParams(String val) {
		if ( val != null )
			val = val.trim();
		int fi = val.indexOf('(');
		int li = val.lastIndexOf(')');

		if (fi < 0 || li <= 0)
			return null;

		String te = val.substring(fi + 1, li);
		te = te.trim();
		if (te.indexOf(',') > 0) {
			String[] sp = te.split(",");
			for (int i = 0; i < sp.length; i++) {
				sp[i] = sp[i].trim();
			}
			return sp;
		} else {
			String[] on = { te };
			return on;
		}
	}

	public static String[] parseParams(String val, char del_open, char del_close) {
		int fi = val.indexOf(del_open);
		int li = val.lastIndexOf(del_close);
		if (fi <= 0 || li <= 0)
			return null;
		String te = val.substring(fi + 1, li);
		te = te.trim();
		if (te.indexOf(',') > 0) {
			String[] sp = te.split(",");
			for (int i = 0; i < sp.length; i++) {
				sp[i] = sp[i].trim();
			}
			return sp;
		} else {
			String[] on = { te };
			return on;
		}
	}

	public static String parseFieldNames(String param, ArrayList<String> parms) {
		int st = param.indexOf('[');
		int en = param.indexOf(']');
		if (st >= 0 && en >= 0) {
			String p = param.substring(st + 1, en);
			parms.add(p);
			String l = param.substring(en + 1);
			return parseFieldNames(l, parms);
		}

		return null;
	}

	public static ArrayList<String> parseFieldNames(String param) {
		ArrayList<String> f = new ArrayList<String>();
		parseFieldNames(param, f);
		return f;
	}

	public static String[] parseParams(String val, String delim) {
		int fi = val.indexOf('(');
		int li = val.lastIndexOf(')');
		if (fi <= 0 || li <= 0)
			return null;
		String te = val.substring(fi + 1, li);
		te = te.trim();
		if (te.indexOf(delim) > 0) {
			String[] sp = te.split(delim);
			for (int i = 0; i < sp.length; i++) {
				sp[i] = sp[i].trim();
			}
			return sp;
		} else {
			String[] on = { te };
			return on;
		}
	}

	public static void printFacets(ABaseResults results, PrintStream out,
			String delim) {
		ArrayList<GRow> rows = results.getValues();
		LinkedHashMap<String, LinkedHashMap<String, Integer>> facets = results
				.getFacet();

		Set<String> keys = facets.keySet();
		String line = "";

		for (String key : keys) {
			LinkedHashMap<String, Integer> st = facets.get(key);
			line += "\n" + key;
			line += "\n";
			Set<String> fields = st.keySet();
			for (String field : fields) {
				Integer it = st.get(field);
				line += field + delim + it;
				line += "\n";
			}
			if (out != null)
				out.println(line);
		}
	}

	public static int[] parseRange(String c) {
			int start_count = 0;
			int end_count = Integer.MAX_VALUE;

			int bindex = c.lastIndexOf('{');
			if (bindex > 0) {
				int lindex = c.indexOf('}', bindex);
				String sub = c.substring(bindex, lindex+1);
				if (sub.matches(GBRGX.COUNT_RANGE + "$")) {
					// we have a range.
					int ob = sub.lastIndexOf('{');
					int cb = sub.lastIndexOf('}');
					String rng = sub.substring(ob + 1, cb);
					rng = rng.trim();
					int m = rng.indexOf('-');
					if (m <= 0) {
						GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
					}
					String bg = rng.substring(0, m);
					if (bg == null) {
						GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
					}
					bg = bg.trim();
					String eg = rng.substring(m + 1);
					if (eg == null) {
						GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
					}
					eg = eg.trim();

					try {
						int start = Integer.parseInt(bg);
						int end = Integer.parseInt(eg);

						start_count = start;
						end_count = end;
					} catch (NumberFormatException nf) {
						nf.printStackTrace();
						GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");

					}
				}
			}
			int[] range = new int[2];
			range[0]=start_count;
			range[1]=end_count;
			return range;
					
			
		}
}
