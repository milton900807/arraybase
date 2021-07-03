package com.arraybase;

import com.arraybase.modules.UsageException;

public class LoadUsageException extends UsageException {
	
	public static final String HELP = "Available load types: \n\t Binary\n\tTable\n\tFloatData\n\tSQL"; 
	

	public LoadUsageException(String _correctUsage) {
		super(_correctUsage);
	}
	public LoadUsageException() {
		super(HELP);
	}

}
