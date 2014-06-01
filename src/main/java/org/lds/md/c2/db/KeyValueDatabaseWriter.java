package org.lds.md.c2.db;

import com.sleepycat.collections.TransactionWorker;

public class KeyValueDatabaseWriter implements TransactionWorker {

	KeyValueDatabase myDb;
	private final String myDatabaseLoadFile;

	KeyValueDatabaseWriter(KeyValueDatabase db, String databaseLoadFile) {
		myDb = db;
		myDatabaseLoadFile = databaseLoadFile;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		myDb.writeManyKeysFromFile(myDatabaseLoadFile);
	}

}
