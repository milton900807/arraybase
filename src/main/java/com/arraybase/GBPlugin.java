package com.arraybase;

import com.arraybase.modules.UsageException;

public interface GBPlugin {
	String exec(String command, String variable_key) throws UsageException;
	GBV execGBVIn(String cmd, GBV input) throws UsageException;

}
