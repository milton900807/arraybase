package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.HostFileSystem;
import com.arraybase.io.parse.ArrayListVar;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.UserPath;
import com.arraybase.util.IOUTILs;

import java.io.*;
import java.util.ArrayList;

/**
 * This is similar to R read into dataframe e.g. variable<-read(myfile.csv)
 */
public class FileToArrayListVar implements GBPlugin {
	public String exec(String command, String variable_key)
			throws UsageException {
		int arrowind = command.indexOf("<-");
		if (arrowind < 0) {
			throw new UsageException(
					"Please provide the following syntax for loading a file\t variable_name<-read(filename)");
		}
		int fstart = command.indexOf('(', arrowind);
		int fend = command.indexOf(')', fstart);
		String filename = command.substring(fstart + 1, fend);
		if ( !filename.startsWith("/"))
		{
			// derive the absolute path 
			HostFileSystem local_user_path = GB.getLocalPath();
			filename = local_user_path.getPath() + "/" + filename;
			
		}
		File f = new File(filename);
		if (!f.exists()) {
			throw new UsageException("Failed to find the file "
					+ f.getAbsolutePath());
		}

		// get the variable name
		String variable_name = command.substring(0, arrowind);
		if (variable_name == null) {
			throw new UsageException(
					"Please provide the following syntax for loading a file\t variable_name<-read(filename)");
		} else {
			variable_name = variable_name.trim();
		}

		ArrayList<String> list = readFile(f);
		ArrayListVar alist = new ArrayListVar(list);
		GB.setVariable(variable_name, alist);
		return "Variable : " + variable_name + " is set. ";

	}

	private ArrayList<String> readFile(File f) {
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = reader.readLine();
			while (line != null) {
				if (line != null) {
					line = line.trim();

				}
				if (line.length() > 0)
					list.add(line);

				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(reader);
		}
		return list;
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
