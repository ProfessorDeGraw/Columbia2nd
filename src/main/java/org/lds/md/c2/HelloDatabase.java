package org.lds.md.c2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

		loadMemberDataFile("old_members", "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/200239_old.csv");
		loadMemberDataFile("new_members", "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/200239.csv");
		
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

	private void loadMemberDataFile(String tableName, String fileName) {
		
		BufferedReader wardDir = null;
		
		try {
			wardDir = new BufferedReader(
					new FileReader(fileName));

			String thisLine;

			boolean firstLine = true;
			List<String> headerNames = new ArrayList<String>();

			while ((thisLine = wardDir.readLine()) != null) { // while loop
																// begins here
				List<String> parsedLine = new ArrayList<String>();
				String parseLine = thisLine;
				while (parseLine.length()>0) {
					if (parseLine.length()==1) {
						parseLine = parseLine.substring(1);
					} else if (parseLine.charAt(0) == '\"') {
						int nextQuote = parseLine.substring(1).indexOf("\"") + 1;
						String left = parseLine.substring(1, nextQuote);
						String right = parseLine.substring(nextQuote+1);
						parsedLine.add(left);
						parseLine = right;
					} else if (parseLine.charAt(0) == ',' && parseLine.charAt(1) == ',') {
						parsedLine.add("");
						String right = parseLine.substring(1);
						parseLine = right;
					} else if (parseLine.charAt(0) == ',' && parseLine.charAt(1) == '\"') {
						int nextQuote = parseLine.substring(2).indexOf("\"") + 2;
						String left = parseLine.substring(2, nextQuote);
						String right = parseLine.substring(nextQuote+1);
						parsedLine.add(left);
						parseLine = right;
					} else {
						log.warn("Unknown parse:" + parseLine);
					}
				}
				
				String[] mg = parsedLine.toArray(new String[0]);;
				
				//String[] mg = thisLine.split("\",\"");

				if (mg.length > 0 && mg[0].length() > 0
						&& mg[0].charAt(0) == '\"') {
					mg[0] = mg[0].substring(1);
				}

				if (mg.length > 0) {
					if (mg[mg.length - 1].length() > 0) {
						if (mg[mg.length - 1].charAt(mg[mg.length - 1]
								.length() - 1) == '\"') {
							mg[mg.length - 1] = mg[mg.length - 1]
									.substring(0,
											mg[mg.length - 1].length() - 1);
						}
					}
				}
				
				if (firstLine) {
					for (String m : mg) {
						log.trace("Header:" + m);
						headerNames.add(m);
					}
					firstLine = false;
				} else {
					log.trace("Line:" + thisLine);
					if (mg.length>2) {
					database.actionAddKey(tableName,  mg[1], "key", "1");
					int i=0;
					for (String m : mg) {
						database.actionAddKey(tableName, mg[1], headerNames.get(i), m);
						i++;
					}
					}
				}

				// Pattern pattern = Pattern.compile("(\"[^\"]*?\",?)*");
				// Matcher matcher = pattern.matcher(thisLine);

				// if (matcher.matches()) {
				// for (int i = 1; i <= matcher.groupCount(); i++) {
				// String match = matcher.group(i);
				// log.trace(match);
				// }
				// }
				// while (matcher.find()) {
				// for (int i = 1; i <= matcher.groupCount(); i++) {
				// String match = matcher.group(i);
				// log.trace(match);
				// }
				// }

				//break;

			} // end while
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (wardDir!=null) {
			try {
				wardDir.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	}

}
