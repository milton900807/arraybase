
package com.arraybase.flare;

public class DBProcessFailedException extends Exception {

	public DBProcessFailedException(String stat_msg, String string) {
		super ( stat_msg + string );
	}

}
