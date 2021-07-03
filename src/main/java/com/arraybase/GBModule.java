package com.arraybase;

import java.util.List;
import java.util.Map;

import com.arraybase.modules.UsageException;

public interface GBModule {

	String KEYS = "KEYS";
	String PATH = "PATH";
	String MAP = "MAP";
	String WHERE_CLAUSE = "WHERE_CLAUSER";
	String QUERY = "QUERY";
	String EXPORT = "EXPORT";
	String USER = "USER";
	String URL = "URL";
	String ABQ = "ABQ";
	String ABQ_UPDATE = "ABQ_UPDATE";
	String WEBJAR = null;
	String ABQ_FOR_DOCUMENT_STORE = "ABQ_FOR_DOCUMENT_STORE";
    String ABQ_FOR_CSV = "ABQ_FOR_CSV";

    void exec(List<String> l) throws UsageException;

	void exec(Map<String, Object> l) throws UsageException;

	String getModName();

}
