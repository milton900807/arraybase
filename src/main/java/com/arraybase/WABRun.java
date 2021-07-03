package com.arraybase;

import com.arraybase.modules.UsageException;
import com.arraybase.util.GBLogger;
import com.arraybase.util.IOUTILs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * WAB files are workflow arraybase files
 * 
 * @author jmilton
 * 
 */
public class WABRun {

	private GBLogger log = GBLogger.getLogger(WABRun.class);
	private String file = null;
	private LinkedHashMap<String, String> vars = new LinkedHashMap<String, String>();
	private ArrayList<String> commands = new ArrayList<String>();

	public WABRun(String _filename) throws IOException {
		file = _filename;
		build();
	}

	private void build() throws IOException {
		File f = new File(file);
		FileReader reader = new FileReader(f);
		BufferedReader br = new BufferedReader(reader);
		try {
			String line = br.readLine();
			while (line != null) {
				String s = line.trim();
				if (s.startsWith("#") || s.startsWith("//") || s.startsWith("/*")) {
					// skip comment lines
				} else if (s.startsWith("$")) {
					String[] assignment = s.split("=\\s*");
					vars.put(assignment[0].trim(), assignment[1].trim());

				} else
					commands.add(s);
				line = br.readLine();
			}
		} finally {
			IOUTILs.closeResource(br);
			IOUTILs.closeResource(reader);
		}
	}

	public void run() {
		for (String com : commands) {
			log.info(com);
			GB.print(com);
			long startTime = System.nanoTime();
			String[] command = com.split("\\s+");
			String[] mcommand = new String[command.length];
			// RUN THE VARIABLES
			int index = 0;
			for (String c : command) {
				Set<String> vark = vars.keySet();
				for (String s : vark) {
					if (c.contains(s)) {
						c = c.replace(s, vars.get(s));
					}
				}
				if (c != null && c.length() > 0)
					mcommand[index++] = c;
			}
			try {
				if (mcommand != null && mcommand.length > 1)
					GB.gogb(mcommand);
			} catch (UsageException e) {
				e.printStackTrace();
			}
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			log.info("Execution Time: " + duration / 1000000000.0f + " secs.");
		}
	}

}
