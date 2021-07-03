package com.arraybase.lac;

import com.arraybase.db.DBConnectionManager;
import com.arraybase.io.GBBlobFile;
import com.arraybase.io.GBFileManager;
import com.arraybase.tm.GBPathUtils;

public class FileLacReference implements LACReference {

	private GBBlobFile file = null;

	public void save(DBConnectionManager _manager)
			throws LacReferenceSaveException {
		GBFileManager dl = new GBFileManager(_manager);
		dl.save(file);
	}

	public void load(String _lac, DBConnectionManager _manager)
			throws LoadFailedException {
		String[] lac = LAC.parse(_lac);
		String leaf = GBPathUtils.getLeaf(lac[2]);
		try {
			long id = Long.parseLong(leaf.trim());
			GBFileManager dl = new GBFileManager(_manager);
			GBBlobFile f = dl.getFile(id);
			// dl.getFile(file_id);
			file = f;

		} catch (Exception _e) {
			throw new LoadFailedException(_e);
		}
	}

	/**
	 * The string representation of this reference object.
	 */
	public String getReference() {
		return "fileid:" + file.getFile_id() + "";
	}

}
