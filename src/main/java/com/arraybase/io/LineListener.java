package com.arraybase.io;

public interface LineListener {
	String getStartsWithToken();
	String getEndsWithToken();
	void lineFound(String _line);
	
}
