package com.arraybase.var.app;

import com.arraybase.*;
import com.arraybase.search.ABaseResults;
import com.arraybase.search.SearchPointer;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.var.gbv.GBVPointer;
import com.arraybase.var.gbv.GBVTable;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This packs only two output variables
 * 
 * @author milton
 * 
 */
public class GMapCommand implements GBPlugin {

	private ArrayList<GBV> results = new ArrayList<GBV>();

	public static void main(String[] args) {

	}

	public String exec(String command, String variable_key) {

		int index = command.indexOf('(');
		int lindex = command.indexOf(')');
		String variable = command.substring(index + 1, lindex);
		variable = variable.trim();

		GBSearch search = GB.getSearch();
		String[] cols = { "SEQUENCE" };
		String previous = "----";
		try {
			Iterator<ArrayList<LinkedHashMap<String, Object>>> sl = GBSearch
					.searchAndDeploy("/isis/oligos/OligoList", "*:*",
							"SEQUENCE DESC", cols, new SearchConfig(SearchConfig.RAW_SEARCH));

			while (sl.hasNext()) {

				ArrayList<LinkedHashMap<String, Object>> ls = sl.next();
				for (LinkedHashMap<String, Object> ob : ls) {

					Object sequence = ob.get("SEQUENCE");
					String ts = sequence.toString();
					System.out.println("" + sequence);
					if (!ts.equals(previous)) {
						variable = sequence.toString();
						System.out.println("\t\t: " + variable);
						File ff = new File("temp.fa");
						PrintStream pr;
						pr = new PrintStream(ff);
						pr.println("> test");
						pr.println(variable);
						pr.flush();
						String cc = "var"
								+ " -D /Users/milton/db/var -d hg19 -A "
								+ ff.getAbsolutePath();
						previous = ts;
						Process process = Runtime.getRuntime().exec(cc);
						InputStream st = process.getInputStream();
						LogStreamReader reader = new LogStreamReader(st,
								variable, variable_key);
						Thread t = new Thread(reader);
						t.start();
						while (t.isAlive()) {
							try {
								Thread.sleep(400);
							} catch (InterruptedException ec) {
								ec.printStackTrace();
							}
						}
					}
				}
			}

		} catch (NotASearchableTableException e1) {
			e1.printStackTrace();
		} catch (NodeNotFoundException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (variable.startsWith("$")) {
			System.out.println(" we have the variable : " + variable);
		}
		//
		// GBVariables.setVariable(variable_key, gbv);

		return "complete";
	}

	class LogStreamReader implements Runnable {

		private BufferedReader reader;
		private String key = null;
		private String sequence = null;
		private Oligo oligo = null;

		public LogStreamReader(InputStream is, final String var,
				final String variable_key) {
			key = variable_key;
			sequence = var;
			oligo = new Oligo(sequence);
			this.reader = new BufferedReader(new InputStreamReader(is));
		}

		public void run() {
			try {
				String line = reader.readLine();
				// GB.print("[sequence]\t[chromosome]\t[position]");
				results.clear();
				GBVTable table = new GBVTable();
				while (line != null) {
					if (line != null)
						line = line.trim();
					// GB.print(line);
					// line = line.trim();
					if (line.startsWith("Alignment for path")) {
						line = reader.readLine();
						line = reader.readLine();
						System.out.println("" + line);
						addAlignment(line);
					}
					line = reader.readLine();
				}
				GB.setVariable(key, table);
				reader.close();
				
				saveOligo ( oligo );
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void addAlignment(String line) {

			GenomicPosition genomic_position = new GenomicPosition(line);
			oligo.addTarget(genomic_position);
		}

	}

	/**
	 * Update the This will store the executed value in a temporary variable
	 */
	public GBV execGBVIn(String cmd, GBV input) {

		if (input != null)
			if (input instanceof GBVPointer) {
				GBVPointer pointer = (GBVPointer) input;
				SearchPointer spointer = (SearchPointer) pointer.get();
				GBSearch search = GB.getSearch();
				String path = spointer.getPath();
				String[] st = spointer.getFields();
				String search_string = spointer.getSearchString();
				int start = spointer.getStart();
				int max = spointer.getMax();
				try {
					ABaseResults rb = GBSearch.select(path, st, search_string,
							start, max, null);
					ArrayList<GRow> list = rb.getValues();
					ArrayList<GColumn> clist = rb.getColumns();
					for (int i = 0; i < st.length; i++) {
						for (GColumn gc : clist) {
							if (st[i].equalsIgnoreCase(gc.getName())) {
								st[i] = gc.getName();
							}
						}
					}
					ArrayList<String> vlist = new ArrayList<String>();
					for (GRow r : list) {
						Map data = r.getData();
						String sequence = (String) data.get(st[0]);
						exec("var(" + sequence + ")", null);
					}
				} catch (NodeNotFoundException e) {
					e.printStackTrace();
				}

			}
		return GB.getVariable(getClass().getCanonicalName());
	}

	public static void saveOligo(Oligo oligo) {
		
		
		
		
	}

}
