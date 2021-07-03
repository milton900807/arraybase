package com.arraybase.io;

import com.arraybase.GBModule;
import com.arraybase.modules.InsertFactory;
import com.arraybase.modules.UsageException;
import com.arraybase.util.IOUTILs;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class GBFileParser {

	public static ABQFile parseABQ(String file_name, String path) throws UsageException, IOException {
		FileReader file_reader = null;
		try {
			file_reader = new FileReader(file_name);
			Properties p = new Properties();
			p.load(file_reader);
			String export = p.getProperty("export");
			String[] exported_values = export.split(",\\s*");
			// {{ DETERMINE THE TYPE OF INSERT }}
			String url = p.getProperty("url");
			String query = p.getProperty("query");
			if (url == null) {
				System.err
						.println("url parameter is not defined in the abq file. \n\n");
				throw new UsageException(
						"URL was not provided in the file... please specify this parameter");
			} else {
				// get the insert module type based on the url in the file
				String insert_module_type = getInsertModuleType(url);
				if (insert_module_type == null) {
					String usage = ("Please provide a valid connect url in the abq file.  It appears the url "
							+ url + " is not correct, as I can't seem to find a module that will be able to load the data");
					throw new UsageException(usage);
				}
				// QUERY IS NOT BEING SET AT THE MOMENT NEED TO DO THIS.
				HashMap<String, Object> param_map = new HashMap<String, Object>();
				param_map.put(GBModule.EXPORT, exported_values);
				param_map.put(GBModule.PATH, path);
				param_map.put(GBModule.QUERY, query);
				ABQFile ab = new ABQFile(p, param_map, insert_module_type);
				return ab;
			}
		} finally {
			IOUTILs.closeResource(file_reader);
		}

	}

	private static String getInsertModuleType(String url) {
		String u = url.toLowerCase();
		if (u.startsWith("jdbc")) {
			return InsertFactory.RELATIONAL;
		} else if (u.startsWith("file"))
			return InsertFactory.FILE;
		else if (u.startsWith("ab:")) {
			return InsertFactory.INDEXED;
		}
		return null;
	}

}
