package com.arraybase.modules;

import com.arraybase.GB;
import com.arraybase.GBModule;
import com.arraybase.GBV;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.flare.DBProcessFailedException;
import com.arraybase.flare.GBJobListener;
import com.arraybase.flare.SQLToFile;
import com.arraybase.io.ABQFile;
import com.arraybase.lac.LAC;
import com.arraybase.shell.cmds.NodePropertyType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildCSVFromABQFile implements GBModule {

	public void exec(List<String> l) throws UsageException {
		throw new UsageException(
				" This method is currently not implemented... please use exec(Map<String, Object> ");
	}

	/**
	 * This will create a table from a set of parameters defined in the abq
	 * file.
	 */
	public void exec(final Map<String, Object> l) throws UsageException {
		final Map<String, String> config = new HashMap<String, String>();
		for (String keys : l.keySet()) {
			String value = (String) l.get(keys);
			config.put(keys, value);
		}
		String job_id = (String)l.get("job_id");
		final String user = (String) l.get(ABQFile.USER);
		final String query = (String) l.get(ABQFile.QUERY);
		final String export = (String) l.get(ABQFile.EXPORT);
		if( job_id == null )
		{
			job_id = query.toString()+new Date().toString();
		}

		// create the new core object
		final String path = config.get(ABQFile.NODE_PATH);

		final String core = NameUtiles.convertToValidCharName(path);
		final String link = LAC.getLink(core, "search", "*:*");
		// set this object as the loader in the config.
		config.put(NodePropertyType.MODULE.name(), this.getModName());
		if ( query.startsWith("select ")) {
			try {
				new SQLToFile()
						.run(user, path, "" + query, config, null, query, job_id, new GBJobListener() {
							public void jobComplete(String msg) {
								GB.print(" Job complete");
								String final_operation = (String) l.get("final-operation");
								if ( final_operation != null ) {
									String[] pg = final_operation.split(":");
									if ( pg.length > 1 )
									{
										GB.setVariable(pg[0], new GBV(true));
									}
								}
							}
						});

			} catch (DBProcessFailedException e) {
				e.printStackTrace();
			}
		}else{

			GB.printUsage ( " This feature is currently not implemented.  Please make sure the abq file contains a select statement " );
//			try {
//				if ( query == null || query.length() <= 0 ){
//					throw new UsageException( " Please provide an export in the abq file " );
//				}
//				new DocumentStoreToSolr ( ).run ( user, path, core, export, config, null, query, job_id, new GBJobListener() {
//                    public void jobComplete(String msg) {
//                    }
//                });
//			} catch (DBProcessFailedException e) {
//				e.printStackTrace();
//			}
		}
	}
	public String getModName() {
		return this.getClass().getCanonicalName();
	}
}
