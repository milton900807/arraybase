package com.arraybase.lac;

import com.arraybase.db.DBConnectionManager;

public interface LACReference {

	void save(DBConnectionManager dbConnectionManager) throws LacReferenceSaveException;

	void load(String _lac, DBConnectionManager _manager) throws LoadFailedException;

	String getReference();
	
}
