package com.arraybase.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUnzipper {

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[2048];
		int len;
		int bytes = 0;
		while ((len = in.read(buffer)) != -1){
			out.write(buffer, 0, len);
			bytes += len;
		}
		out.flush();
		out.close();
		in.close();
	}

	// If no output folder is passed in, unzip it into the dir the file is in
	public static String unzip(String fn) {
		int loc = fn.lastIndexOf(File.separator);
		String retVal = "";
		if (loc == -1)
			unzip(fn, retVal);
		else {
			retVal = fn.substring(0, loc + 1);
			unzip(fn, retVal);
		}

		return retVal;
	}

	public static void unzip(String fn, String outputFolder) {
		Enumeration entries;
		ZipFile zipFile = null;

		if (!(outputFolder.endsWith("/") || outputFolder.endsWith("\\"))) {
			outputFolder += File.separator;
		}
		System.err.println("unzip " + fn + " to " + outputFolder);

		if (fn == null || fn.length() == 0) {
			System.err.println("Must pass in a filename to unzip");
			return;
		}
		if (fn.endsWith(".gz")) {
			gunzip(fn, outputFolder, null);
			return;
		}
		try {
			zipFile = new ZipFile(fn);
			entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				if (entry.isDirectory()) {
					System.out.println("Extracting directory: "
							+ entry.getName());
					(new File(outputFolder + entry.getName())).mkdir();
					continue;
				} else {
					System.out.println("Extracting file: " + entry.getName()
							+ " to " + outputFolder);
					
					entry.getSize();
					copyInputStream(zipFile.getInputStream(entry),
							new BufferedOutputStream(new FileOutputStream(
									outputFolder + entry.getName())));
				}
			}
		} catch (java.util.zip.ZipException ze) {
			gunzip(fn, outputFolder, null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			IOUTILs.closeResource(zipFile);
		}
	}

	public static String gunzip(String filename, String outputFolder,
			String outputFileName) {
		try {
			int loc = filename.lastIndexOf(File.separator);
			if (outputFolder == null)
				outputFolder = filename.substring(0, loc + 1);

			int lext = filename.lastIndexOf(".");
			String ext = filename.substring(lext + 1);

			FileInputStream fin = new FileInputStream(filename);
			if (outputFileName == null)
				outputFileName = filename.substring(loc + 1,
						filename.indexOf("." + ext));
			GZIPInputStream gzis = new GZIPInputStream(fin);
			new File(outputFolder).mkdir();

			FileOutputStream fos = new FileOutputStream(outputFolder
					+ outputFileName);
			copyInputStream(gzis, new BufferedOutputStream(fos));
			fin.close();
			gzis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputFolder + outputFileName;
	}

	public static void main(String[] args) {
		unzip("/temp/GSE1009_series_matrix.txt.gz");

	}
}
