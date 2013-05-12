package org.lds.md.c2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;

class KeyValueDatabaseReader implements TransactionWorker {

	KeyValueDatabase db;

	KeyValueDatabaseReader(KeyValueDatabase myDb) {
		db = myDb;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		db.readAllKeys();
	}

}

class KeyValueDatabaseAllKeysReader implements TransactionWorker {

	KeyValueDatabase db;
	List<List<String>> keys;
	String table, column;

	KeyValueDatabaseAllKeysReader(KeyValueDatabase myDb, String myTable,
			String myColumn) {
		db = myDb;
		table = myTable;
		column = myColumn;
	}

	public List<List<String>> getAllKeys() {
		return keys;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		keys = db.allKeys(table, column);
	}
}

class KeyValueDatabaseWriter implements TransactionWorker {

	KeyValueDatabase myDb;
	private String myDatabaseLoadFile;

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

class KeyValueDatabaseWrite implements TransactionWorker {

	KeyValueDatabase myDb;

	String myTable, myRow, myColumn, myValue;

	KeyValueDatabaseWrite(KeyValueDatabase db, String table, String row,
			String column, String value) {
		myDb = db;
		myTable = table;
		myRow = row;
		myColumn = column;
		myValue = value;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		myDb.writeKey(myTable, myRow, myColumn, myValue);
	}

}

class KeyValueDatabaseRemove implements TransactionWorker {

	KeyValueDatabase myDb;

	String myTable, myRow, myColumn;

	KeyValueDatabaseRemove(KeyValueDatabase db, String table, String row,
			String column) {
		myDb = db;
		myTable = table;
		myRow = row;
		myColumn = column;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		myDb.removeKey(myTable, myRow, myColumn);
	}

}

class KeyValueDatabaseRemoveAll implements TransactionWorker {

	KeyValueDatabase myDb;

	String myTable, myRow, myColumn;

	KeyValueDatabaseRemoveAll(KeyValueDatabase db) {
		myDb = db;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		myDb.removeAllKeys();
	}

}

public class KeyValueDatabase {

	private static final Logger log = LoggerFactory
			.getLogger(KeyValueDatabase.class);

	private static boolean create = true;

	private boolean databaseOpen = false;

	private Environment env;
	private ClassCatalog catalog;
	private Database db;
	private SortedMap<SimpleKey, String> map;

	private StringBuilder dbMessage = new StringBuilder(
			"Database is not ready.");

	private String databaseLoadFile = "/tmp/occurrence.txt";
	private String databaseLocation = "/tmp/berkeleydb";

	public void setDatabaseLoadFile(String databaseLoadFile) {
		this.databaseLoadFile = databaseLoadFile;
	}

	public void removeAllKeys() {
		if (databaseOpen == false) {
			openDatabase();
		}

		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			String value = map.remove(entry.getKey());
			log.trace("removed:" + entry.getKey().toString() + ":" + value);
		}
		iter = null;
	}

	public void removeKey(String myTable, String myRow, String myColumn) {
		if (databaseOpen == false) {
			openDatabase();
		}

		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			if (entry.getKey().getTable().equals(myTable)
					&& entry.getKey().getRow().equals(myRow)
					&& entry.getKey().getColumn().equals(myColumn)) {
				String value = map.remove(entry.getKey());
				log.trace("removed:" + entry.getKey().toString() + ":" + value);
			}
		}
		iter = null;
	}

	public void writeKey(String myTable, String myRow, String myColumn,
			String myValue) {
		map.put(new SimpleKey(myTable, myRow, myColumn), myValue);
	}

	public void setDatabaseLocation(String databaseLocation) {
		this.databaseLocation = databaseLocation;
	}

	public String getDbMessage() {
		return dbMessage.toString();
	}

	public void databaseCycle() {
		openDatabase();

		actionWriterInitDB();
		actionReader();

		closeDatabase();
	}

	private void openDatabase() {
		log.trace("Staring to setup database");

		// environment is transactional
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		if (create) {
			envConfig.setAllowCreate(true);
		}
		Environment env;
		try {
			env = new Environment(new File(databaseLocation), envConfig);

			// create the application and run a transaction
			// HelloDatabaseWorld worker = new KeyValueDatabase(env);
			try {
				this.setupDatabase(env);

				databaseOpen = true;

			} catch (Exception e) {
				log.error("Setup Database failed!", e);
			}
		} catch (EnvironmentLockedException e) {
			log.error("Setup Database Environment Lock failed!", e);
		} catch (DatabaseException e) {
			log.error("Setup Database Environment failed!", e);
		}
	}

	public void initDB() {
		actionWriterInitDB();
	}

	public List<List<String>> actionAllKeys() {
		return actionAllKeysByTable(null);
	}

	public List<List<String>> actionAllKeysByTable(String table) {
		return actionAllKeysByTableByColumn(table, null);
	}

	public List<List<String>> actionAllKeysByTableByColumn(String table,
			String column) {

		List<List<String>> retKeys = null;

		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			// KeyValueDatabaseWriter writer = new KeyValueDatabaseWriter(this);
			KeyValueDatabaseAllKeysReader reader = new KeyValueDatabaseAllKeysReader(
					this, table, column);
			try {
				// runner.run(writer);
				runner.run(reader);
				retKeys = reader.getAllKeys();
			} catch (DatabaseException e) {
				log.error("Database read failed!", e);
			} catch (Exception e) {
				log.error("Database read failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO
				// this.close();
			} catch (Exception e) {
				log.error("Database read failed!", e);
			}
		}

		return retKeys;
	}

	public void actionWriterInitDB() {
		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			KeyValueDatabaseWriter writer = new KeyValueDatabaseWriter(this,
					databaseLoadFile);
			// KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				runner.run(writer);
				// runner.run(reader);
			} catch (DatabaseException e) {
				log.error("Database run failed!", e);
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO
				// this.close();
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		}
	}

	public void actionReader() {
		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			// KeyValueDatabaseWriter writer = new KeyValueDatabaseWriter(this);
			KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				// runner.run(writer);
				runner.run(reader);
			} catch (DatabaseException e) {
				log.error("Database read failed!", e);
			} catch (Exception e) {
				log.error("Database read failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO
				// this.close();
			} catch (Exception e) {
				log.error("Database read failed!", e);
			}
		}
	}

	/** Creates the database for this application */
	private void setupDatabase(Environment env) throws Exception {
		this.env = env;
		open();
	}

	private static class SimpleKeyBinding extends TupleBinding<SimpleKey> {

		@Override
		public SimpleKey entryToObject(TupleInput input) {
			// TODO Auto-generated method stub
			String value = input.readString();

			return new SimpleKey(value);
		}

		@Override
		public void objectToEntry(SimpleKey object, TupleOutput output) {
			// TODO Auto-generated method stub
			SimpleKey key = (SimpleKey) object;
			output.writeString(key.toSerial());
		}
	}

	/** Opens the database and creates the Map. */
	private void open() throws Exception {

		// use a generic database configuration
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(false);
		if (create) {
			dbConfig.setAllowCreate(true);
		}

		// catalog is needed for serial bindings (java serialization)
		Database catalogDb = env.openDatabase(null, "catalog", dbConfig);
		catalog = new StoredClassCatalog(catalogDb);

		// use Integer tuple binding for key entries
		TupleBinding<SimpleKey> keyBinding = new SimpleKeyBinding();
		// TupleBinding.getPrimitiveBinding(SimpleKey.class);

		// use String serial binding for data entries
		SerialBinding<String> dataBinding = new SerialBinding<String>(catalog,
				String.class);

		this.db = env.openDatabase(null, "Keyvaluedatabase", dbConfig);

		// create a map view of the database
		this.map = new StoredSortedMap<SimpleKey, String>(db, keyBinding,
				dataBinding, true);
	}

	public void closeDatabase() {
		try {
			this.close();
		} catch (Exception e) {
			log.error("Database close failed!", e);
		}
	}

	/** Closes the database. */
	private void close() throws Exception {

		if (catalog != null) {
			catalog.close();
			catalog = null;
		}
		if (db != null) {
			db.close();
			db = null;
		}
		if (env != null) {
			env.close();
			env = null;
		}
	}

	/** Performs work within a transaction. */
	public void doWork() {
		writeManyKeysFromFile("/tmp/occurrence.txt");
		readAllKeys();
	}

	/** Writes and reads the database via the Map. */
	public void writeManyKeysFromFile(String file) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Pattern tab = Pattern.compile("\t");
			String line = reader.readLine();
			long count = 1;
			long time = System.currentTimeMillis();

			long kvp = 1;

			while (line != null) {
				String parts[] = tab.split(line);
				try {
					// map.put(parts[0], line);
					// int i = 0;
					// for (String s : parts) {
					map.put(new SimpleKey(parts[0], parts[1], parts[2]),
							parts[3]);
					// i++;
					// }
					// kvp += i;

				} catch (NumberFormatException e) {
					log.error("Key parse number format failed!", e);
				}

				if (count % 10000 == 0) {
					log.trace("Added ["
							+ count
							+ "].  Average["
							+ (1000 * kvp / (System.currentTimeMillis() - time))
							+ " kvp/sec] ["
							+ (1000 * count / (System.currentTimeMillis() - time))
							+ " records/sec]");
					log.trace("last key: " + parts[0]);
					// if (count%1000000==0) break;
				}

				count++;
				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			log.error("Read File failed!", e);
		}
	}

	public void readAllKeys() {
		// log.trace("Reading data");
		// Iterator<Map.Entry<String, String>> iter = map.tailMap("47874585:")
		// .entrySet().iterator();
		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		dbMessage = new StringBuilder();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			// if (!entry.getKey().startsWith("47874585:")) {
			// break;
			// }
			dbMessage.append(entry.getKey().toString() + entry.getValue()
					+ "<br>" + "\n");
			// log.trace(entry.getKey().toString() + entry.getValue());
		}
		iter = null;
	}

	public List<List<String>> allKeys(String table, String column) {
		if (databaseOpen == false) {
			openDatabase();
		}

		List<List<String>> keys = new ArrayList<List<String>>();

		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			if ((table == null || entry.getKey().getTable().equals(table))
					&& (column == null || entry.getKey().getColumn()
							.equals(column))) {
				List<String> key = new ArrayList<String>();
				key.add(entry.getKey().getTable());
				key.add(entry.getKey().getRow());
				key.add(entry.getKey().getColumn());
				key.add(entry.getKey().getTime());
				key.add(entry.getValue());
				keys.add(key);
			}
		}
		iter = null;

		return keys;
	}

	public void actionAddKey(String table, String row, String column,
			String value) {
		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			KeyValueDatabaseWrite writer = new KeyValueDatabaseWrite(this,
					table, row, column, value);
			// KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				runner.run(writer);
				// runner.run(reader);
			} catch (DatabaseException e) {
				log.error("Database run failed!", e);
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO
				// this.close();
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		}
	}

	public void actionRemoveKey(String table, String row, String column) {
		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			KeyValueDatabaseRemove writer = new KeyValueDatabaseRemove(this,
					table, row, column);
			// KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				runner.run(writer);
				// runner.run(reader);
			} catch (DatabaseException e) {
				log.error("Database run failed!", e);
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO
				// this.close();
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		}

	}

	public void actionRemoveAllKeys() {
		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			KeyValueDatabaseRemoveAll writer = new KeyValueDatabaseRemoveAll(
					this);
			// KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				runner.run(writer);
				// runner.run(reader);
			} catch (DatabaseException e) {
				log.error("Database run failed!", e);
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO
				// this.close();
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		}
	}

	public static void loadMemberDataFile(KeyValueDatabase database,
			String tableName, String fileName) {
		try {
			loadMemberData(database, tableName, new BufferedReader(
					new FileReader(fileName)));
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", e);
		}
	}

	public static void loadMemberData(KeyValueDatabase database,
			String tableName, BufferedReader data) {

		BufferedReader wardDir = null;

		try {
			String thisLine;

			boolean firstLine = true;
			List<String> headerNames = new ArrayList<String>();

			while ((thisLine = data.readLine()) != null) { // while loop
															// begins here
				List<String> parsedLine = new ArrayList<String>();
				String parseLine = thisLine;
				while (parseLine.length() > 0) {
					if (parseLine.length() == 1) {
						parseLine = parseLine.substring(1);
					} else if (parseLine.charAt(0) == '\"') {
						int nextQuote = parseLine.substring(1).indexOf("\"") + 1;
						String left = parseLine.substring(1, nextQuote);
						String right = parseLine.substring(nextQuote + 1);
						parsedLine.add(left);
						parseLine = right;
					} else if (parseLine.charAt(0) == ','
							&& parseLine.charAt(1) == ',') {
						parsedLine.add("");
						String right = parseLine.substring(1);
						parseLine = right;
					} else if (parseLine.charAt(0) == ','
							&& parseLine.charAt(1) == '\"') {
						int nextQuote = parseLine.substring(2).indexOf("\"") + 2;
						String left = parseLine.substring(2, nextQuote);
						String right = parseLine.substring(nextQuote + 1);
						parsedLine.add(left);
						parseLine = right;
					} else {
						log.warn("Unknown parse:" + parseLine);
					}
				}

				String[] mg = parsedLine.toArray(new String[0]);
				;

				// String[] mg = thisLine.split("\",\"");

				if (mg.length > 0 && mg[0].length() > 0
						&& mg[0].charAt(0) == '\"') {
					mg[0] = mg[0].substring(1);
				}

				if (mg.length > 0) {
					if (mg[mg.length - 1].length() > 0) {
						if (mg[mg.length - 1]
								.charAt(mg[mg.length - 1].length() - 1) == '\"') {
							mg[mg.length - 1] = mg[mg.length - 1].substring(0,
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
					if (mg.length > 2) {
						database.actionAddKey(tableName, mg[1], "key", "1");
						int i = 0;
						for (String m : mg) {
							database.actionAddKey(tableName, mg[1],
									headerNames.get(i), m);
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

				// break;

			} // end while
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (wardDir != null) {
				try {
					wardDir.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static List<String> detectChanges(KeyValueDatabase database,
			String leftTable, String rightTable) {
		List<String> lines = new ArrayList<String>();

		List<List<String>> left = database.actionAllKeysByTableByColumn(
				leftTable, "key");
		List<List<String>> right = database.actionAllKeysByTableByColumn(
				rightTable, "key");

		List<String> leftKeyTypeOnly = new ArrayList<String>();
		for (List<String> leftRow : left) {
			leftKeyTypeOnly.add(leftRow.get(1));
		}

		List<String> rightKeyTypeOnly = new ArrayList<String>();
		for (List<String> rightRow : right) {
			rightKeyTypeOnly.add(rightRow.get(1));
		}

		for (String name : leftKeyTypeOnly) {
			int found = rightKeyTypeOnly.lastIndexOf(name);
			if (found == -1) {
				lines.add(name + " --Removed");
				log.debug(name + " --Removed");
			}
		}

		for (String name : rightKeyTypeOnly) {
			int found = leftKeyTypeOnly.lastIndexOf(name);
			if (found == -1) {
				lines.add(name + " --Added");
				log.debug(name + " --Added");
			}
		}

		// for ( int i=0; i<13; i++) {
		// log.trace(leftKeyTypeOnly.get(i));
		// }
		return lines;
	}

	public static BufferedReader GetLDSPage(String USR, String PWD)
			throws IOException, HttpException, UnsupportedEncodingException {
		InputStream body = getInfoBody(USR, PWD);

		// now convert to characters. utf-8 is specified due to the content type
		// header's specified charset, should actually parse and used that but
		// I'll take a short cut for now
		InputStreamReader reader = new InputStreamReader(body, "utf-8");
		// char[] chars = new char[1024];
		// int charsRead = reader.read(chars);
		// StringWriter sw = new StringWriter();

		return new BufferedReader(reader);

		// while (charsRead != -1) {
		// sw.write(chars, 0, charsRead);
		// charsRead = reader.read(chars);
		// }

		// log.trace(sw.toString());
	}

	private static InputStream getInfoBody(String USR, String PWD) throws IOException,
			HttpException {
		/*
		 * WARNING: https requires that the SSL certificates served by the
		 * server be located in the local trust store otherwise request will
		 * fail with a certificate exception.
		 */

		log.trace("Stating Auth");
		
		String authUrl = "https://lds.org/login.html";
		HttpClient client = new HttpClient();
		
		log.trace("Set policy");
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		PostMethod auth = new PostMethod(authUrl);
		auth.addParameter("username", USR);
		auth.addParameter("password", PWD);
		
		log.trace("Set user");

		auth.setFollowRedirects(false);
		int respCode = client.executeMethod(auth);
		// Header location = auth.getResponseHeader("location");
		Header cookieHrd = auth.getResponseHeader("set-cookie");
		
		log.trace("set-cookie");

		log.trace("called: " + authUrl);
		log.trace("response: " + respCode);
		// log.trace("location: " + location.toExternalForm());
		
		log.trace("call-response");

		HeaderElement[] elements = cookieHrd.getElements();
		for (int i = 0; elements != null && i < elements.length; i++) {
			log.trace("Received set-cookie: " + elements[i].getName()
					+ "=" + elements[i].getValue());
		}
		String value = cookieHrd.getValue();
		String[] parts = value.split(";");
		String ssoToken = parts[0].split("=")[1];
		
		log.trace("call-ssoToken");

		log.trace("ssoToken: " + ssoToken) ;

		if (respCode != 200) {
			log.error("--Auth failed--");
			throw new HttpException();
		}

		// ------ now access restricted resource -------

		// long curr = System.currentTimeMillis();
		// GregorianCalendar cal = new GregorianCalendar();
		// cal.set(cal.get(GregorianCalendar.YEAR),
		// cal.get(GregorianCalendar.MONTH),
		// cal.get(GregorianCalendar.DATE), 0, // hours
		// 0, // minutes
		// 0); // seconds
		// long startOfToday = cal.getTimeInMillis();
		// long totalDaysDesired = 30;
		// long daysOut = startOfToday + totalDaysDesired * 24L * 60L * 60L
		// * 1000L;

		// String uri =
		// "https://lds.org/church-calendar/services/lucrs/evt/locations/0/"
		// + startOfToday + "-" + daysOut + "/L/";
		String uri = "https://www.lds.org/directory/services/ludrs/unit/member-list/200239/csv";

		log.trace("calling: " + uri);

		HttpMethod method = new GetMethod(uri);
		method.addRequestHeader("cookie", "ObSSOCookie=" + ssoToken);
		method.setFollowRedirects(false);
		int status = client.executeMethod(method);
		InputStream body = method.getResponseBodyAsStream();
		Header ctype = method.getResponseHeader("content-type");
		String val = ctype.getValue();
		log.trace("Content-Type: " + val);
		return body;
	}
}
