package com.arraybase.db.counters;

import com.arraybase.util.IOUTILs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GBColCounter implements GBCounter {

	public int count(String... params) throws FailedToDetermineCountException {
		BufferedReader re = null;
		try {
			String _param = params[0];
			String _delim = params[1];
			String _row = params[2];
			Integer row = Integer.parseInt(_row);

			File f = new File(_param);
			re = new BufferedReader(new FileReader(f));
			String l = re.readLine();
			if (l == null)
				throw new FailedToDetermineCountException(
						"It appears we cannot read this file. "
								+ f.getAbsolutePath());
			int lindex = 0;
			while (l != null) {
				if (lindex == row) {
					String[] sp = l.split(_delim);
					int co = sp.length;
					return co;
				}
				l = re.readLine();
				lindex++;
			}

		} catch (FileNotFoundException _e) {
			_e.printStackTrace();
			throw new FailedToDetermineCountException("File was not found ");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUTILs.closeResource(re);
		}
		throw new FailedToDetermineCountException(
				"Failed to count the lines in the file : " + params);
	}

	public String getCounterType() {
		return "Column counter: ";
	}

}
