package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.util.IOUTILs;

import java.io.*;

public class loadCSV implements GBPlugin {

	public String exec(String command, String variable_key) {

		String[] l = LAC.parse(command);
		String target = l[0];
		String data = l[2];

		File f = new File(data);
		if (!f.exists()) {
			GB.printUsage("CSV file not found : " + f.getAbsolutePath());
			return "file not found";
		}
		BufferedReader bread = null;
		try {
			FileReader reader = new FileReader(f);
			bread = new BufferedReader(reader);
			String title = bread.readLine();
			String[] fields = title.split(",");

			for (String ff : fields) {

			}

			String line = bread.readLine();
			while (line != null) {

				line = bread.readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(bread);
		}

		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

}
