package com.arraybase.util;

public final class PathManager {

	/**
	 * Given a string representation of a path then we want to get the parent
	 * path as a string
	 * 
	 * @param _path
	 * @return
	 */
	public static String getParent(String _path) {
		int lindex = _path.lastIndexOf('/');
		if (lindex <= 0)
			return _path;
		else {
			String temp = _path.substring(0, lindex);
			return temp;
		}

	}

	// TODO: implement this shite
	public static String getPopPath(String _dir) {
		return null;
	}

}
