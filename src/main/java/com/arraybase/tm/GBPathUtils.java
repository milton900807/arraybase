package com.arraybase.tm;


import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.arraybase.db.HBConnect;
import com.arraybase.tm.tree.TPath;

public class GBPathUtils {

	/**
	 * Return the trimmed version of the path object
	 * 
	 * @param path
	 * @return
	 */
	public static String trim(String path) {
		if (path.endsWith("/")) {
			int i = path.lastIndexOf('/');
			return path.substring(0, i);
		}
		return path;
	}

	/**
	 * this will return null if there is no parent node.
	 * 
	 * @param path
	 * @return
	 */
	public static String getParent(String path) {
		if (!path.contains("/")) {
			return null;
		}
		if (path.endsWith("/")) {
			path = GBPathUtils.trim(path);
		}
		int leafindex = path.lastIndexOf('/');
		if (leafindex < 0)
			return null;
		String parent_path = path.substring(0, leafindex);
		return parent_path;
	}

	public static String getLeaf(String path) {
		return FilenameUtils.getBaseName(path);
	}

	/**
	 * Create a file name that is unique and indexed to uniqueness if not
	 * 
	 * @param ls
	 * @param local_file_name
	 * @param _index
	 * @return
	 */
	public static String createNewSequenceFileName(String[] ls, String lf,
			int _index) {
		if (_index < 0) {
			return lf;
		}
		for (String l : ls) {
			if (lf.equalsIgnoreCase(l)) {
				// we need to remove the file name extension
				String extension = extractExtension(l);
				String pure_filename = removeExtension(l);
				int file_count = getFileCount(pure_filename);
				if (file_count < 0) {
					return pure_filename += "_" + _index + "." + extension;
				} else
					return createNewSequenceFileName(ls, lf, _index + 1);// try
																			// a
																			// new
																			// increment
			}
		}
		return lf;
	}

	private static String extractExtension(String l) {

		int i = l.lastIndexOf('.');
		if (i < 0)
			return null;
		return l.substring(i + 1);
	}

	/**
	 * This will return -1 if there is no index-count at the end of the filename
	 * 
	 * @param pure_filename
	 * @return
	 */
	private static int getFileCount(String pure_filename) {

		if (pure_filename.contains("_")) {
			int index = pure_filename.lastIndexOf('_');
			if (index <= 0) {
				return -1;
			}
			String lastindex = pure_filename.substring(index + 1);
			try {
				Integer value = Integer.parseInt(lastindex);
				return value;
			} catch (NumberFormatException _e) {
				return -1;
			}
		}
		return -1;
	}

	
	public static void remove( TPath path ){
		Session ses = HBConnect.getSession();
		try {
			ses.beginTransaction();
			Criteria c = ses.createCriteria(TPath.class).add(Restrictions.eq("path_id", path.getPath_id()));
			List l = c.list();
			if (l != null || l.size() > 0) {
				TPath p = (TPath) l.get(0);
				ses.delete(p);
				ses.flush();
				ses.getTransaction().commit();
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			HBConnect.close(ses);
		}
	}
	
	
	private static String removeExtension(String l) {
		int i = l.lastIndexOf('/');
		if (i <= 0)
			return l;
		else {
			return l.substring(0, i);
		}
	}

	public static String[] split(String _path) {
		return _path.split("/");
	}

	/**
	 * Adjust the path characters.
	 * 
	 * @param absolutePath
	 * @return
	 */
	public static String adjustPathChars(String absolutePath) {
		String t = absolutePath.replace("/./", "/");
		t = t.replace("//", "/");
		t = t.replace("///", "/");
		if (t.endsWith("/"))
			t = t.substring(0, t.length() - 1);
		if (t.endsWith("/."))
			t = t.substring(0, t.length() - 2);
		return t;
	}

	public static String getRoot(String fullPath) {
		fullPath = adjustPathChars(fullPath);
		if (!fullPath.startsWith("/")) {
			fullPath = "/" + fullPath;
		}
		int second_index = fullPath.indexOf("/", 2);
		if (second_index >= 0) {
			return fullPath.substring(0, second_index);
		} else
			return fullPath;
	}
}
