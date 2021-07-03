package com.arraybase.lac;

// please do not use external logger libs.
import com.arraybase.util.GBLogger;

/**
 * Manage LAC with file objects in them.
 * 
 * @author donaldm
 * 
 */
public class FileLac {

	static GBLogger log = GBLogger.getLogger(FileLac.class);

	/**
	 * Given a LAC this will return a file id if one is found No id found is
	 * thrown if not found.
	 * 
	 * @param link
	 * @return
	 */
	public static long getFileID(String link)
			throws No_LAC_ElementFoundException {
		// file.report (localdb/GBBlobFile/62)

		log.debug(link);
		if (link == null)
			throw new No_LAC_ElementFoundException();
		String[] lac = LAC.parse(link);
		if (lac[0].equalsIgnoreCase("file")) {
			String t = lac[2];
			// we can parse the id from the data field
			if (t.endsWith("/"))
				t.substring(0, t.length() - 1);
			int index = t.lastIndexOf('/');
			String nv = t.substring(index+1);
			try {
				long value = Long.parseLong(nv);
				return value;
			} catch (NumberFormatException _exception) {
				_exception.printStackTrace();
				throw new No_LAC_ElementFoundException();
			}
		}
		throw new No_LAC_ElementFoundException();
	}

}
