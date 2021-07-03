package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBTableLoader;
import com.arraybase.GBV;
import com.arraybase.db.jdbc.TypeMappingException;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;

public class LOADLacCommand implements GBPlugin {

	public String exec(String command, String variable_key) {

		String[] l = LAC.parse(command);
		String target = l[0];
		String data = l[2];

		String local_file = GB.getLocalPath().getPath() + "/" + data.trim();
		String gb_file = GB.pwd() + "/" + target.trim();
		try {
			GBTableLoader.appendABQ(GB.getDefaultUser(), local_file, gb_file);
		} catch (TypeMappingException e) {
			// e.printStackTrace();
			GB.print("Import didn't work:  The abq file does not map directly to the existing schema.   Please verify the file or change the schema.");
			GB.print(" " + e.getLocalizedMessage());
			return "Failed.";
		} catch (UsageException e) {
			e.printStackTrace();
		}
		return "Loaded";
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}
}
