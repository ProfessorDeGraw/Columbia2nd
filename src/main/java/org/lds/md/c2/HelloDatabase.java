package org.lds.md.c2;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class HelloDatabase implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory
			.getLogger(HelloDatabase.class);

	// @Value("#{mail.database}")
	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		//database.initDB();
		// database.actionWriterInitDB();
		database.actionReader();
		return database.getDbMessage();
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

	@Override
	public void doWork() {
		// database.openDatabase();

		// database.actionWriterInitDB();
		// database.actionReader();

		//log.trace(database.actionAllKeys().toString());

		database.actionAddKey("members", "family", "De Graw", "1");
		database.actionAddKey("members", "family", "De Graw", "2");
		database.actionAddKey("members", "family", "Smith", "1");
		database.actionAddKey("members", "family", "Smith", "2");

		//log.trace(database.actionAllKeys().toString());

		database.actionRemoveKey("members", "family", "De Graw");

		//log.trace(database.actionAllKeys().toString());

		database.actionRemoveAllKeys();

		//log.trace(database.actionAllKeys().toString());

		KeyValueDatabase.loadMemberDataFile(database, "old_members", "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/200239_old.csv");
		KeyValueDatabase.loadMemberDataFile(database, "new_members", "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/200239.csv");
		
		detectChanges("old_members", "new_members");

		 //log.trace("========================");
		 
		 //for ( int i=0; i<13; i++) {
		//	 log.trace(database.actionAllKeys().get(i));
		 //}

		// database.actionReader();
		// log.trace(database.actionAllKeys().toString());
	}

	private void detectChanges(String leftTable, String rightTable) {
		List<List<String>> left = database.actionAllKeysByTableByColumn(leftTable, "key");
		List<List<String>> right = database.actionAllKeysByTableByColumn(rightTable, "key");
		
		List<String> leftKeyTypeOnly = new ArrayList<String>();
		for (List<String> leftRow : left ) {
			leftKeyTypeOnly.add(leftRow.get(1));
		}
		
		List<String> rightKeyTypeOnly = new ArrayList<String>();
		for (List<String> rightRow : right ) {
			rightKeyTypeOnly.add(rightRow.get(1));
		}
		
		for (String name: leftKeyTypeOnly) {
			int found=rightKeyTypeOnly.lastIndexOf(name);
			if ( found == -1) {
				log.debug(name+" --Removed");
			}
		}
		
		for (String name: rightKeyTypeOnly) {
			int found=leftKeyTypeOnly.lastIndexOf(name);
			if ( found == -1) {
				log.debug(name+" --Added");
			}
		}
		
		 //for ( int i=0; i<13; i++) {
		//	 log.trace(leftKeyTypeOnly.get(i));
		 //}
	}
}
