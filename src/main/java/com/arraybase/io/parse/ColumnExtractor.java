package com.arraybase.io.parse;

import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.HostFileSystem;
import com.arraybase.shell.UserPath;
import com.arraybase.util.IOUTILs;

import java.io.*;
import java.util.ArrayList;

/**
 * This is an object that will pull a column of data out of a file
 * 
 * @author milton
 * 
 */
public class ColumnExtractor {

	public final static String HEAD = "head";

	public final static String delim_ = "--delim";
	public final static String start_ = "--start";
	public final static String end_ = "--end";
	public final static String col_ = "--col";

	private static final String TOP = "TOP";

	private ArrayList<String> values = new ArrayList<String>();
	private int start_row = 0;
	private String delim = "\t";
	private int col = 0;
	private int stop_row = Integer.MAX_VALUE;

	public static void main(String[] args) {

		String file = args[args.length - 1];
		ColumnExtractor ext = new ColumnExtractor();
		ext.setPropeties(args);

		try {
			ext.extract(file);
		} catch (DelimNotFoundException e) {
			e.printStackTrace();
		} catch (ColumnNotFoundException e) {
			e.printStackTrace();
		}

	}

	public GBV extract(String file) throws DelimNotFoundException,
			ColumnNotFoundException {
		BufferedReader breader = null;
		try {
			HostFileSystem upath = GB.getLocalPath();

			File dir = new File(upath.getPath());
			if (dir.isDirectory())
				System.out.println(" dir " + dir.getAbsolutePath());
			File f = new File(dir, file);
			System.out.println(file + " f " + f.getAbsolutePath());
			FileReader reader = new FileReader(f);
			breader = new BufferedReader(reader);
			String st = breader.readLine();
			int row = 0;
			while (st != null) {
				if (row >= start_row && row < stop_row) {
					int index = st.indexOf(delim);
					if (index <= 0) {
						throw new DelimNotFoundException(delim);
					}
					String[] split = st.split(delim);
					if (col >= split.length)
						throw new ColumnNotFoundException(col, st);
					String val = split[col];
					values.add(val);
				}
				row++;
				st = breader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(breader);
		}
		return new ArrayListVar(values);
	}

	public void print() {
		for (String val : values)
			System.out.println(val);
	}

	public void setPropeties(String[] _args) {
		for (String _s : _args) {

			if (_s.startsWith(start_)) {
				setStart(_s);
			} else if (_s.startsWith(end_)) {
				setStop(_s);
			} else if (_s.startsWith(delim_))
				setDelim(_s);
			else if (_s.startsWith(col_))
				setCol(col_);
		}
	}

	public void setCol(String col2) {
		try {
			if (col2 != null) {
				col2 = col2.trim();
				Integer it = Integer.parseInt(col2);
				col = it;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}

	}

	public void setDelim(String _s) {
		delim = _s;
	}

	public void setStop(String _s) {
		Integer i = Integer.parseInt(_s);
		stop_row = i;
	}

	public void setStart(String _start) {
		start_row = Integer.parseInt(_start);
	}

	public void configure(String string) {
		string = string.toUpperCase();
		string = string.trim();
		if (string.equalsIgnoreCase(HEAD)) {
			start_row = 0;
			stop_row = 1;
		} else if (string.startsWith(TOP)) {
			start_row = 0;
			if (string.length() > 3) {
				String num = string.substring(3);
				try {
					Integer it = Integer.parseInt(num);
					stop_row = it.intValue();
				} catch (Exception _e) {
					_e.printStackTrace();
				}
			}
		} else {
			setCol(string);
		}
	}
}
