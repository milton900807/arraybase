package com.arraybase.util;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.arraybase.GB;

/**
 * DO NOT INCLUDE LOGGER FROM LOG4J!!!! USE THIS CLASS INSTEAD. THERE ARE TOO
 * MANY INCOMPATABILITIES WITH LOG4J VERSION ON SOLR SERVERS. (I.E. PLUGINS WILL
 * NOT RUN)
 * 
 * @author donaldm
 * 
 */
public class GBLogger  {
	public static final String DEBUG = "DEBUG";
	private Class cl = null;
	private boolean debug = true;
	private boolean info = false;
	private boolean config = true;
	private static boolean status = true;

	public GBLogger(Class<?> class1) {
		cl = class1;
	}

	public static GBLogger getLogger(Class<?> class1) {
		return new GBLogger(class1);
	}

	public void debug(String string) {
		if (debug) {
			System.out.println("DEBUG: \t\t" + string);
		}
	}

	public void info(String string) {
		if (info) {
			System.out.println(string);
		}
	}

	public void error(String string) {
		System.err.println(string);
	}

	public void setLevel(String level) {
		if (level.equals(DEBUG))
			debug = true;

	}

	public void fatal(String string) {
		System.err.print(string);
	}

	public void error(IOException e1) {
		e1.printStackTrace();
		System.err.println("Exception " + e1.getLocalizedMessage());

	}

	public void config(String string) {
		if (config)
			System.out.println(string);

	}

	public void prinln() {
		System.out.println("");
	}

	public void println(String content) {
		System.out.println(content);

	}

	public void install(String string) {
		System.out.println("\tInstallation : " + string);
	}

	public void init(String string) {
		System.out.println(string);
	}

	public void debug(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	public void debug(String arg0, Object[] arg1) {
		// TODO Auto-generated method stub

	}

	public void debug(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	public void debug(Marker arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void debug(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	public void debug(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	public void debug(Marker arg0, String arg1, Object[] arg2) {
		// TODO Auto-generated method stub

	}

	public void debug(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub

	}

	
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	
	public void error(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	
	public void error(String arg0, Object[] arg1) {
		// TODO Auto-generated method stub

	}

	
	public void error(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	
	public void error(Marker arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	
	public void error(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void error(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void error(Marker arg0, String arg1, Object[] arg2) {
		// TODO Auto-generated method stub

	}

	
	public void error(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub

	}

	
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void info(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	
	public void info(String arg0, Object[] arg1) {
		// TODO Auto-generated method stub

	}

	
	public void info(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	
	public void info(Marker arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	
	public void info(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void info(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void info(Marker arg0, String arg1, Object[] arg2) {
		// TODO Auto-generated method stub

	}

	
	public void info(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub

	}

	
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isDebugEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isErrorEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isInfoEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isTraceEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isWarnEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void trace(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public void trace(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	
	public void trace(String arg0, Object[] arg1) {
		// TODO Auto-generated method stub

	}

	
	public void trace(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	
	public void trace(Marker arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	
	public void trace(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void trace(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void trace(Marker arg0, String arg1, Object[] arg2) {
		// TODO Auto-generated method stub

	}

	
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub

	}

	
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	
	public void warn(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public void warn(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	
	public void warn(String arg0, Object[] arg1) {
		// TODO Auto-generated method stub

	}

	
	public void warn(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	
	public void warn(Marker arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	
	public void warn(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void warn(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	
	public void warn(Marker arg0, String arg1, Object[] arg2) {
		// TODO Auto-generated method stub

	}

	
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub

	}

	
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	public void debug(Map<String, String> vals) {
		debug("\n\nMap: ");
		Set<String> keys = vals.keySet();
		for (String k : keys) {
			debug("\t\t Key : " + k + " val = " + vals.get(k));
		}
	}

	public void debug(String[] _args) {
		if (debug) {
			for (String s : _args)
				debug(s);
		}
	}
	public static void status ( String _stat ){
		if ( status )
		{
			GB.print ( _stat );
		}
	}
	
	
}
