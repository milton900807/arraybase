package com.arraybase.io;

import com.arraybase.util.IOUTILs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class GBOutputFileReader implements Runnable {
	private Thread t = null;
	private File file = null;
	private ArrayList<LineListener> listeners = new ArrayList<LineListener> ();
	public GBOutputFileReader(File _file) {
		this.file = _file;
	}
	public void start() {
		t = new Thread(this);
		t.start();
	}
	
	public void addLineListener ( LineListener list ){
		listeners.add(list);
	}
	public void removeLineListener ( LineListener list )
	{
		listeners.remove(list);
	}
	
	public void run() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String re = reader.readLine();
			LinkedHashMap<LineListener, String> starts = new LinkedHashMap<LineListener, String> ();
			for ( LineListener l : listeners ){
				if ( l.getStartsWithToken() != null )
					starts.put ( l, l.getStartsWithToken() );
			}
			while (re != null) {
				System.out.println ( " re : "+ re );
				// find the value of the strings 
				Set<LineListener> li = starts.keySet();
				for ( LineListener st : li){
					String value = starts.get(st);
					if (re.startsWith(value.trim())){
						st.lineFound(re);
					}
				}
				re = reader.readLine();
			}
		} catch (IOException _e) {
			_e.printStackTrace();
		} finally {
            IOUTILs.closeResource(reader);
        }
    }

}
