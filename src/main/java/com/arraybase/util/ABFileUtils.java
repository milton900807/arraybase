package com.arraybase.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.security.MessageDigest;
import java.util.SortedSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ABFileUtils {
	private static GBLogger log = GBLogger.getLogger(ABFileUtils.class);
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
					log.error(e1);
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
					log.error(e1);
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
					log.error(e1);
				}
			}
			e.printStackTrace();
		}
	}

	public static void appendLineToFile(File _file, String _value) {
		PrintStream st = null;
		try {
			st = new PrintStream(_file);
			st.append("\n" + _value + "\n");
		} catch (Exception _e) {
			_e.printStackTrace();
		} finally {
			IOUTILs.closeResource(st);
		}
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

	// public static void readTar(InputStream in, String untarDir) throws
	// IOException{
	// System.out.println("Reading TarInputStream...");
	// TarInputStream tin = new TarInputStream(in);
	// TarEntry tarEntry = tin.getNextEntry();
	// if(new File(untarDir).exists()){
	// while (tarEntry != null){
	// File destPath = new File(untarDir + File.separatorChar +
	// tarEntry.getName());
	// System.out.println("Processing " + destPath.getAbsoluteFile());
	// if(!tarEntry.isDirectory()){
	// FileOutputStream fout = new FileOutputStream(destPath);
	// tin.copyEntryContents(fout);
	// fout.close();
	// }else{
	// destPath.mkdir();
	// }
	// tarEntry = tin.getNextEntry();
	// }
	// tin.close();
	// }else{
	// System.out.println("That destination directory doesn't exist! " +
	// untarDir);
	// }
	// }

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

	public static void ZipFile(String fileToZip, String outputFile) {
		String[] foo = new String[1];
		foo[0] = fileToZip;
		System.out.println(" archive file  " + fileToZip + "" + " to "
				+ outputFile);
		ZipFile(foo, outputFile);
	}

	public static void gzipIt(File _source, File _dest) {

		if (_source.isDirectory()) {
			String[] ls = _source.list();
			ZipFile(ls, _dest.getPath());
		} else
			ZipFile(_source.getAbsolutePath(), _dest.getPath());

	}

	public static void ZipFile(SortedSet<String> filesToZip, String outputFile) {
		String[] array = filesToZip.toArray(new String[filesToZip
				.size()]);
		ZipFile(array, outputFile);
	}
	
	public static void tar (File _dir, String _outfile) throws IOException{
		TarArchiveOutputStream out = null;
		try {
		     out = new TarArchiveOutputStream(
		          new GZIPOutputStream(
		               new BufferedOutputStream(new FileOutputStream(_outfile + ".tar.gz"))));
		     
		     
		     if ( _dir.isDirectory() ){
		    	 
		    	 String[] l = _dir.list();
		    	 for ( String file : l ){
		    		 addFileToTarGz(out, _dir.getAbsolutePath()+"/"+file, "");
		    	 }
		    	 
		     }
		     out.flush();
		     
		}catch ( Exception _e ){
			_e.printStackTrace();
		}
		finally {
		     if(out != null) out.close();
		}
	}
	

    private static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) throws IOException {
        File f = new File(path);
        System.out.println("Adding file " + f.getAbsolutePath());
        System.out.println("\t\texist?" + f.exists());
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (f.isFile()) {
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] children = f.listFiles();
            if (children != null){
                for (File child : children) {
                    System.out.println(child.getName());
                    addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }
	

	public static void ZipFile(String[] filesToZip, String outputFile) {
        ZipOutputStream out = null;
        FileInputStream in = null;
		byte[] buf = new byte[20000];
		try {
			// Create the ZIP file
			if (outputFile.indexOf("\\/") > 0) {
				int loc = outputFile.indexOf("\\/");
				outputFile = outputFile.replaceAll("/", "\\\\");
				outputFile = outputFile.replaceAll("\\\\\\\\", "\\\\");
			}
			System.err.println("Zipping file to " + outputFile);
			File output_file = new File(outputFile);
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream ( output_file ) ) );

			// Compress the files
			for (int i = 0; i < filesToZip.length; i++) {

				String ftz = filesToZip[i];
				System.err.println("ADD TO ZIP : " + ftz);
				if (ftz.indexOf("\\/") > 0) {
					ftz = ftz.replaceAll("/", "\\\\");
					ftz = ftz.replaceAll("\\\\\\\\", "\\\\");
				}
				String dir = ftz;
				if (ftz.lastIndexOf(File.separator) > 0)
					dir = ftz.substring(0, ftz.lastIndexOf(File.separator));

				File ff = new File(dir);
				if (!ff.exists()) {
					ff.mkdirs();
				}

				int loc = ftz.lastIndexOf(File.separator);
				String justFileName = ftz.substring(loc + 1);
				// justFileName = justFileName.replaceAll(File.separator, "");
				in = new FileInputStream(ftz);

				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(justFileName));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
			}

		} catch (IOException e) {
            e.printStackTrace();
		} finally {
            IOUTILs.closeResource(out);
            IOUTILs.closeResource(in);
        }
	}

	public static String saveStreamToFile(InputStream is, String fileName) {
        OutputStream out = null;
		try {
			File f = new File(fileName);
			out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				out.write(buf, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
            IOUTILs.closeResource(out);
            IOUTILs.closeResource(is);
        }
        return fileName;
	}
}
