package com.arraybase.util;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IOContext.Context;

import java.io.*;
import java.security.MessageDigest;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class FileUtilities {
	private static final char[] hexadecimal = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Encodes the 128 bit (16 bytes) MD5 into a 32 character String.
	 * 
	 * @param binaryData
	 *            Array containing the digest
	 * @return Encoded MD5, or null if encoding failed
	 */
	public static String md5encode(byte[] binaryData) {

		if (binaryData.length != 16)
			return null;

		char[] buffer = new char[32];

		for (int i = 0; i < 16; i++) {
			int low = binaryData[i] & 0x0f;
			int high = (binaryData[i] & 0xf0) >> 4;
			buffer[i * 2] = hexadecimal[high];
			buffer[i * 2 + 1] = hexadecimal[low];
		}
		return new String(buffer);
	}

	public static String getMd5(String filename) {
		String theMd5 = null;
		byte[] buf = new byte[1000];
		int totlen = 0;
		int len = 0;
		MessageDigest algorithm = null;
		InputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(filename));
			algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			while ((len = fis.read(buf)) >= 0) {
				totlen += len;
				algorithm.update(buf);
			}
			fis.close();
			fis = null;
			theMd5 = md5encode(algorithm.digest());
		} catch (Exception e) {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e1) {
					// Log.ignoringError(e1);
				}
			}
		}
		return theMd5;
	}

	public static String readMd5(String filename) {
		String ans = null;
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(filename + ".md5"));
			ans = r.readLine();
			if (ans != null) {
				ans = ans.trim();
			}
			r.close();
			r = null;
		} catch (Exception e) {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e1) {
					// Log.ignoringError(e1);
				}
			}
			e.printStackTrace();
		}
		return ans;
	}

	public static void writeMd5(String filename) {
		String theMd5 = getMd5(filename);
		Writer w = null;
		try {
			if (theMd5 != null) {
				w = new FileWriter(filename + ".md5");
				w.write(theMd5);
				w.flush();
				w.close();
				w = null;
			}
		} catch (IOException e) {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e1) {
					// Log.ignoringError(e1);
				}
			}
			e.printStackTrace();
		}
	}

	public static void fileCopier(String fn, Directory indir, Directory outdir) {
		// System.err.println("in fileCopier with " + fn);
		org.apache.lucene.store.IndexInput in = null;
		org.apache.lucene.store.IndexOutput outs = null;
		byte[] buf = new byte[1024];
		long bytesleft = 0;
		int thistimebytes = buf.length;
		try {
			IOContext iocontext = new IOContext (Context.FLUSH);
			outs = outdir.createOutput(fn, iocontext);
			in = indir.openInput(fn, iocontext);
			bytesleft = in.length();
			while (bytesleft > 0) {
				if (bytesleft < buf.length) {
					thistimebytes = (int) bytesleft;
				}
				in.readBytes(buf, 0, thistimebytes);
				bytesleft -= thistimebytes;
				outs.writeBytes(buf, thistimebytes);
			}
		} catch (Exception anye) {
			System.out.println("fileCopier problem: " + anye.getMessage());
			anye.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outs.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		// System.err.println("out fileCopier with " + fn);
	}

	public static InputStream getTarInputStream(String tarFileName)
			throws Exception {
		if (tarFileName.substring(tarFileName.lastIndexOf(".") + 1,
				tarFileName.lastIndexOf(".") + 3).equalsIgnoreCase("gz")) {
			System.out.println("Creating an GZIPInputStream for the file");
			return new GZIPInputStream(new FileInputStream(
					new File(tarFileName)));
		} else {
			System.out.println("Creating an InputStream for the file");
			return new FileInputStream(new File(tarFileName));
		}
	}

	public static InputStream getaStream(File f) {
		InputStream is = null;
		try {
			if (f.getName().toLowerCase().endsWith(".gz")) {
				is = new BufferedInputStream(new GZIPInputStream(
						new FileInputStream(f)));
			} else {
				is = new BufferedInputStream(new FileInputStream(f));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return is;
	}

	/**
	 * @param searchSum
	 * @return
	 */
	public static String readFile(File searchSum) {
		BufferedReader re = null;
		try {
			FileReader reader = new FileReader(searchSum);

			re = new BufferedReader(reader);
			String fileString = "";
			String line = re.readLine();

			while (line != null) {
				fileString += line + "\n";
				line = re.readLine();
			}
			return fileString;
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(re);
		}
		return null;
	}

	public static File createTempFile(int _job_id) throws IOException {

		File dir = new File("./TMLoad_logs");
		if (!dir.exists()) {
			dir.mkdir();
		}
		Date d = new Date();
		String udi = _job_id + "900807" + d.getTime();
		File temp = File.createTempFile(udi, "tmp");

		return temp;
	}
}
