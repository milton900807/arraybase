package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.schedule.GBScheduler;
import com.arraybase.util.IOUTILs;

import java.io.*;

public class LoadCron implements GBPlugin {




	public String exec(String command, String variable_key) {
		String[] sp = command.split("\\s");
		if (sp != null && sp.length > 0) {
			String filename = sp[1];
			try {

				 if ( !filename.startsWith("/"))
				 {
					 filename = GB.lpwd() + "/" + filename;
				 }
				loadCronFile(filename);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "Cron loaded";
	}

	// keep it simple
	private void loadCronFile(String filename) throws IOException {
		BufferedReader re = null;
		try {
			File f = new File(filename);
			if (!f.exists())
				throw new FileNotFoundException(f.getAbsolutePath());
			re = new BufferedReader(new FileReader(f));
			String line = re.readLine();
			while (line != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					// skip
				} else {
					System.out.println(" line " + line);
					int pipe = line.indexOf('|');
					if (pipe > 0) {
						String command = line.substring(0, pipe);
						String time = line.substring(pipe + 1);
						if (command != null)
							command = command.trim();
						if (time != null)
							time = time.trim();
						GB.addToScheduler ( command, "arraybase", command, time);
					}
				}
				line = re.readLine();
			}
		} finally {
			IOUTILs.closeResource(re);
		}
		GB.start();
		GB.print("Scheduler has started.");
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}




}
