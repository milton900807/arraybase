package com.arraybase.flare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

import com.arraybase.util.FileUtilities;

public class InMemoryJobManager {

	public final static String RUNNING = "Running";
	public final static String COMPLETE = "Complete";

	private static HashMap<Integer, String> in_mem_jobs = new HashMap<Integer, String>();
	// private static HashMap<Integer, File> logs = new HashMap<Integer,
	// File>();
	private static HashMap<Integer, PrintWriter> currently_printing = new HashMap<Integer, PrintWriter>();
	private static HashMap<Integer, Date> last_written = new HashMap<Integer, Date>();
	private static HashMap<Integer, BufferedReader> reader = new HashMap<Integer, BufferedReader>();
	public static boolean alive = true;

	static class InMemoryJobGarbageCollector extends Thread {

		public void run() {

			while (alive) {
				Set<Integer> jobs = in_mem_jobs.keySet();
				Date current = new Date();
				ArrayList<Integer> close_these = new ArrayList<Integer>();
				for (Integer job : jobs) {
					Date date = last_written.get(job);
					if (date != null) {
						int min_diff = (int) (((current.getTime() - date
								.getTime()) / 1000.0 / 60.0));

						if (min_diff > 45) {
							close_these.add(job);
						}
					}
				}

				for (Integer j : close_these) {
					close(j);
				}

				try {
					sleep(6000);
				} catch (Exception _e) {
					_e.printStackTrace();
				}

			}

		}

	}

	static {
		new InMemoryJobGarbageCollector().start();
	}

	public static void kill() {
		alive = false;
	}

	public static int start() {
		try {
			int _job_id = createJobID();
			in_mem_jobs.put(_job_id, RUNNING);
			File f = FileUtilities.createTempFile(_job_id);
			PrintWriter pr = new PrintWriter(f);
			currently_printing.put(_job_id, pr);

			FileReader r = new FileReader(f);
			BufferedReader b = new BufferedReader(r);
			reader.put(_job_id, b);

			return _job_id;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void setStatus(int _job, String _status) {
		String status = in_mem_jobs.get(_job);
		if (status != null && _status.equalsIgnoreCase(COMPLETE)) {
			close(_job);
		}
		in_mem_jobs.put(_job, _status);
		log(_job, _status);
	}

	public static void close(int _job) {

		PrintWriter pr = currently_printing.get(_job);
		if (pr != null) {
			pr.close();
			currently_printing.remove(_job);
		}
		BufferedReader f = reader.get(_job);
		if (f != null) {
			try {
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader.remove(_job);
		}
		in_mem_jobs.remove(_job);
		last_written.remove(_job);
	}

	private static int createJobID() throws Exception {
		synchronized (in_mem_jobs) {
			if (in_mem_jobs.size() >= 1000)
				throw new Exception("No more job space available ");
			int value = (int) (Math.random() * 100000000);
			if (in_mem_jobs.get(value) != null)
				return createJobID();
			else
				return value;
		}
	}

	public static String readLine(int _job) throws IOException {
		BufferedReader r = reader.get(_job);

		if (r == null)
			new IOException("No log defined for this job=" + _job);
		return r.readLine();
	}

	public static void log(int _job, String _line) {
		PrintWriter pr = currently_printing.get(_job);
		if (pr != null) {
			synchronized (pr) {
				pr.println(_line);
				pr.flush();
				last_written.put(_job, new Date());
			}
		}
	}
	public static void log(String _job, String _line) {
		PrintWriter pr = currently_printing.get(_job);
		if (pr != null) {
			synchronized (pr) {
				pr.println(_line);
				pr.flush();
			}
		}
	}

}
