package com.arraybase.io;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.arraybase.util.GBLogger;

public class GFile {

	static String serverRunDir = ".";
	static GBLogger log = GBLogger.getLogger(GFile.class);

	public static File getFile(String _path) throws FileNotFoundException {
		try {
			String dir = serverRunDir;
			log.info("Run directory : " + dir);
			File f = new File(dir, _path);
			if (f.exists()) {
				log.info("Found the file : " + f.getAbsolutePath());
				return f;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
			log.error("Failed to find the path : " + _path);
		}
		File f2 = new File("src", _path);
		if (f2.exists())
			return f2;

		return null;

	}

	public static File getFile(String dir, String file)
			throws FileNotFoundException {
		return GFile.getFile(dir + "/" + file);
	}

	public static InputStream getInputStream(String lucenePath,
			String pATHLIMESFIELDSPROP) {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = cl.getClass().getClassLoader()
				.getResourceAsStream(lucenePath + "/" + pATHLIMESFIELDSPROP);

		if (inputStream == null) {
			File f = new File(lucenePath, pATHLIMESFIELDSPROP);
			if (f.exists()) {
				FileInputStream ff = null;
				try {
					ff = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return ff;
			}

		}

		return inputStream;
	}

	public static File createFile(String path) {
		File dir = new File(path);
		return dir;
	}

	public static void setRunDir(String _serverRunDir) {
		serverRunDir = _serverRunDir;
	}

	public static File createTempFile(String _path) {
		File dir = new File(serverRunDir, "tmp");
		if (dir == null || (!dir.exists())) {
			dir.mkdirs();
		}

		File f = new File(serverRunDir, "tmp/" + _path);
		return f;
	}
}
