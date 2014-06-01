package org.lds.md.c2.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
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
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;

public class KeyValueDatabase {

	private static final Logger log = LoggerFactory
			.getLogger(KeyValueDatabase.class);

	private static boolean create = true;

	private boolean databaseOpen = false;

	private Environment env;
	private ClassCatalog catalog;
	private Database db;
	private SortedMap<SimpleKey, String> map;

	private String databaseLoadFile = "/tmp/occurrence.txt";
	private String databaseLocation = "/tmp/berkeleydb";

	public void setDatabaseLoadFile(String databaseLoadFile) {
		this.databaseLoadFile = databaseLoadFile;
	}
	
	public class ReaderBuilder {
		String table = null, row = null, column = null, value = null,
				time = null, data = null;
		
		public ReaderBuilder table(String value) {
			table = value;
			return this;
		}

		public ReaderBuilder row(String value) {
			row = value;
			return this;
		}

		public ReaderBuilder column(String value) {
			column = value;
			return this;
		}

		public ReaderBuilder value(String value) {
			this.value = value;
			return this;
		}

		public ReaderBuilder time(String value) {
			time = value;
			return this;
		}

		public ReaderBuilder data(String value) {
			data = value;
			return this;
		}

		public List<List<String>> go() {
			List<List<String>> retKeys = null;

			if (KeyValueDatabase.this.databaseOpen == false) {
				openDatabase();
			}

			TransactionRunner runner = new TransactionRunner(env);
			try {
				// open and access the database within a transaction
				KeyValueReader reader = new KeyValueReader.Builder(KeyValueDatabase.this).table(table).row(row).column(column).value(value).time(time).data(data).build();
				try {
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
					// TODO Auto-generated catch block
					// this.close();
				} catch (Exception e) {
					log.error("Database read failed!", e);
				}
			}

			return retKeys;
		}
	}
	
	public class RemoverBuilder {
		String table = null, row = null, column = null, value = null,
				time = null, data = null;
		
		public RemoverBuilder table(String value) {
			table = value;
			return this;
		}

		public RemoverBuilder row(String value) {
			row = value;
			return this;
		}

		public RemoverBuilder column(String value) {
			column = value;
			return this;
		}

		public RemoverBuilder value(String value) {
			this.value = value;
			return this;
		}

		public RemoverBuilder time(String value) {
			time = value;
			return this;
		}

		public RemoverBuilder data(String value) {
			data = value;
			return this;
		}

		public void go() {
			if (databaseOpen == false) {
				openDatabase();
			}

			TransactionRunner runner = new TransactionRunner(env);
			try {
				// open and access the database within a transaction
				KeyValueRemover writer = new KeyValueRemover.Builder(KeyValueDatabase.this).table(table).row(row).column(column).value(value).time(time).data(data).build();
				try {
					runner.run(writer);
				} catch (DatabaseException e) {
					log.error("Database run failed!", e);
				} catch (Exception e) {
					log.error("Database run failed!", e);
				}
			} finally {
				// close the database outside the transaction
				try {
					// TODO Auto-generated catch block
					// this.close();
				} catch (Exception e) {
					log.error("Database run failed!", e);
				}
			}
		}
	}
	
	public class WriterBuilder {
		String table = "", row = "", column = "", value = "",
				time = "", data = "";
		
		public WriterBuilder table(String value) {
			table = value;
			return this;
		}

		public WriterBuilder row(String value) {
			row = value;
			return this;
		}

		public WriterBuilder column(String value) {
			column = value;
			return this;
		}

		public WriterBuilder value(String value) {
			this.value = value;
			return this;
		}

		public WriterBuilder time(String value) {
			time = value;
			return this;
		}

		public WriterBuilder data(String value) {
			data = value;
			return this;
		}

		public void go() {
			if (databaseOpen == false) {
				openDatabase();
			}

			TransactionRunner runner = new TransactionRunner(env);
			try {
				// open and access the database within a transaction
				KeyValueWriter writer = new KeyValueWriter.Builder(KeyValueDatabase.this).table(table).row(row).column(column).time(time).value(value).data(data).build();
				try {
					runner.run(writer);
				} catch (DatabaseException e) {
					log.error("Database run failed!", e);
				} catch (Exception e) {
					log.error("Database run failed!", e);
				}
			} finally {
				// close the database outside the transaction
				try {
					// TODO Auto-generated catch block
					// this.close();
				} catch (Exception e) {
					log.error("Database run failed!", e);
				}
			}
		}
	}

	public void removeAllKeys() {
		if (databaseOpen == false) {
			openDatabase();
		}

		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			String value = map.remove(entry.getKey());
			log.warn("removed:{}:{}",entry.getKey().toString(),value);
		}
		iter = null;
		
		log.warn("Data drop done");
	}

	public void removeKey(String myTable) {
		if (databaseOpen == false) {
			openDatabase();
		}
		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			if (entry.getKey().getTable().equals(myTable)) {
				String value = map.remove(entry.getKey());
				log.trace("removed:" + entry.getKey().toString() + ":" + value);
			}
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
			String myTime, String myValue, String myData) {
		log.trace("{} adding {} in {} as {} with {}", new Object[] {myTable, myRow, myColumn, myValue, myData});
		try {
			map.put( new SimpleKey.Builder().table(myTable).row(myRow).column(myColumn).time(myTime).value(myValue).build() , myData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("Database add failed", e);
		}
	}

	public void setDatabaseLocation(String databaseLocation) {
		this.databaseLocation = databaseLocation;
	}

	public void databaseCycle() {
		openDatabase();

		actionWriterInitDB();
		this.new ReaderBuilder().go();

		closeDatabase();
	}

	private void openDatabase() {
		log.warn("Staring to setup database");

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

	public void actionWriterInitDB() {
		if (databaseOpen == false) {
			openDatabase();
		}

		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			KeyValueDatabaseWriter writer = new KeyValueDatabaseWriter(this,
					databaseLoadFile);
			try {
				runner.run(writer);
			} catch (DatabaseException e) {
				log.error("Database run failed!", e);
			} catch (Exception e) {
				log.error("Database run failed!", e);
			}
		} finally {
			// close the database outside the transaction
			try {
				// TODO Auto-generated catch block
				// this.close();
			} catch (Exception e) {
				log.error("Database run failed!", e);
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

			return new SimpleKey.Builder().serial(value).build();
		}

		@Override
		public void objectToEntry(SimpleKey object, TupleOutput output) {
			// TODO Auto-generated method stub
			SimpleKey key = object;
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
		getKeys(null, null, null, null, null, null);
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
					map.put(new SimpleKey.Builder().table(parts[0]).row(parts[1]).column(parts[2]).build(),parts[3]);
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

	/*
	public List<List<String>> readAllKeys() {
		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		
		List<List<String>> keys = new ArrayList<List<String>>();
		
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			
			List<String> key = new ArrayList<String>();
			
			key.add(entry.getKey().getTable());
			key.add(entry.getKey().getRow());
			key.add(entry.getKey().getColumn());
			key.add(entry.getKey().getValue());
			key.add(entry.getKey().getTime());
			key.add(entry.getValue());
			keys.add(key);
		}
		iter = null;
		
		return keys;
	}
	*/

	/*
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
	*/
	
	/*
	public List<List<String>> allKeys(String table, String row, String column) {
		if (databaseOpen == false) {
			openDatabase();
		}

		List<List<String>> keys = new ArrayList<List<String>>();

		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			if ((table == null || entry.getKey().getTable().equals(table)) && ( row == null || entry.getKey().getRow().trim()
					.equals(row.trim()) )
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
	*/
	
	public List<List<String>> getKeys(String table, String row, String column, String time,
			String value, String data) {
		if (databaseOpen == false) {
			openDatabase();
		}

		List<List<String>> keys = new ArrayList<List<String>>();

		Iterator<Map.Entry<SimpleKey, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<SimpleKey, String> entry = iter.next();
			if ( (table == null || entry.getKey().getTable().equals(table)  )
					&& (row == null || entry.getKey().getRow().equals(row)) 
					&& (column == null || entry.getKey().getColumn().equals(column))
					&& (time == null || entry.getKey().getTime().equals(time))
					&& (value == null || entry.getKey().getValue().equals(value))
							&& ( data == null || entry.getValue().equals(data)) ) {
				List<String> key = new ArrayList<String>();
				key.add(entry.getKey().getTable());
				key.add(entry.getKey().getRow());
				key.add(entry.getKey().getColumn());
				key.add(entry.getKey().getTime());
				key.add(entry.getKey().getValue());
				key.add(entry.getValue());
				keys.add(key);
			}
		}
		iter = null;

		return keys;
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

					if (parseLine.length() > 4
							&& parseLine.substring(0, 4).contains("\"De ")) {
						log.trace("De ");
					}

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
						parseLine = "";
						// throw new IOException("Unknown parse:"+ parseLine);
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
						database.new WriterBuilder().table(tableName).row(mg[1]).column("key").value("").go();
						int i = 0;
						for (String m : mg) {
							database.new WriterBuilder().table(tableName).row(mg[1]).column(headerNames.get(i)).value(m).go();
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

		List<List<String>> left = database.new ReaderBuilder().table(leftTable).column("key").go();
		
		List<List<String>> right = database.new ReaderBuilder().table(rightTable).column("key").go();

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
		
		return lines;
	}

	public static BufferedReader GetLDSPage(String USR, String PWD)
			throws ClientProtocolException, IOException {
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

	private static InputStream getInfoBody(String USR, String PWD)
			throws ClientProtocolException, IOException {
		/* WARNING: https requires that the SSL certificates served by the server
	       * be located in the local trust store otherwise request will fail with 
	       * a certificate exception.
	       */
	       String authUrl = "https://lds.org/login.html"; 
	       HttpClient client = new HttpClient();
	       client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	 
	       PostMethod auth = new PostMethod(authUrl);
	       auth.addParameter("username", USR);
	       auth.addParameter("password", PWD);
	 
	       auth.setFollowRedirects(false);
	       int respCode = client.executeMethod(auth);
	       //Header location = auth.getResponseHeader("location");
	       Header cookieHrd = auth.getResponseHeader("set-cookie");
	 
	       System.out.println("called: " + authUrl);
	       System.out.println("response: " + respCode);
	       //System.out.println("location: " + location.toExternalForm());
	 
	 
	       HeaderElement[] elements = cookieHrd.getElements();
	       for (int i=0; elements != null && i<elements.length; i++) {
	           System.out.println("Received set-cookie: " + elements[i].getName() + "=" 
	                   + elements[i].getValue());
	       }
	       String value = cookieHrd.getValue();
	       String[] parts = value.split(";");
	       String ssoToken = parts[0].split("=")[1];
	 
	       System.out.println("ssoToken: " + ssoToken);
	       System.out.println();
	 
	       if (respCode != 200) {
	           System.out.println("--Auth failed--");
	           return null;
	       }
	 
	       // ------ now access restricted resource -------
	 
	       long curr = System.currentTimeMillis();
	       GregorianCalendar cal = new GregorianCalendar();
	       cal.set(cal.get(GregorianCalendar.YEAR),
	           cal.get(GregorianCalendar.MONTH),
	           cal.get(GregorianCalendar.DATE),
	           0,  // hours
	           0,  // minutes
	           0); // seconds
	       long startOfToday = cal.getTimeInMillis();
	       long totalDaysDesired = 30;
	       long daysOut = startOfToday + totalDaysDesired * 24L * 60L * 60L * 1000L;
	 
	       String uri = "https://www.lds.org/directory/services/ludrs/unit/member-list/200239/csv";
	       System.out.println("calling: " + uri);
	 
	       HttpMethod method = new GetMethod(uri);
	       method.addRequestHeader("cookie", "ObSSOCookie=" + ssoToken);
	       method.setFollowRedirects(false);
	       int status = client.executeMethod(method);
	       InputStream body = method.getResponseBodyAsStream();
	       Header ctype = method.getResponseHeader("content-type");
	       String val = ctype.getValue();
	       System.out.println("Content-Type: " + val);
	 
	       // now convert to characters. utf-8 is specified due to the content type 
	       // header's specified charset, should actually parse and used that but
	       // I'll take a short cut for now
	       //InputStreamReader reader = new InputStreamReader(body, "utf-8");
	       //char[] chars = new char[1024];
	       //int charsRead = reader.read(chars);
	       //StringWriter sw = new StringWriter();
	 
	       //while (charsRead != -1) {
	       //    sw.write(chars, 0, charsRead);
	       //    charsRead = reader.read(chars);
	       //}
	 
	       //System.out.println(sw.toString());
		
		return body;
		
		
		/*
		 * WARNING: https requires that the SSL certificates served by the
		 * server be located in the local trust store otherwise request will
		 * fail with a certificate exception.
		 */
		/*

		String authUrl = "https://www.lds.org/login.html";
		DefaultHttpClient authClient = new DefaultHttpClient();

		authClient.getParams().setParameter(
				"http.protocol.single-cookie-header", true);
		authClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);

		HttpPost auth = new HttpPost(authUrl);
		auth.setHeader("username", USR);
		auth.setHeader("password", PWD);

		// CookieStore cookieStore = new CookieStore();

		// authClient.setCookieStore(cookieStore);

		// auth.setFollowRedirects(false);

		HttpHost AuthTargetHost = new HttpHost("lds.org");
		HttpResponse AuthHttpResponse = authClient
				.execute(AuthTargetHost, auth);
		

		// int respCode = client.executeMethod(auth);
		// Header location = auth.getResponseHeader("location");
		// DefaultHttpClient httpclient = new DefaultHttpClient();

		List<Cookie> cookies = authClient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			log.trace("None Cookies");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				log.trace("- " + cookies.get(i).toString());
			}
		}

		// Header cookieHrd = auth.getFirstHeader("set-cookie");

		log.trace("called: " + authUrl);
		log.trace("response: " + AuthHttpResponse.toString());

		// if (cookieHrd==null) {
		// return null;
		// }
		// System.out.println("location: " + location.toExternalForm());

		// HeaderElement[] elements = cookieHrd.getElements();
		// for (int i = 0; elements != null && i < elements.length; i++) {
		// System.out.println("Received set-cookie: " + elements[i].getName()
		// + "=" + elements[i].getValue());
		// }
		// String value = cookieHrd.getValue();
		// String[] parts = value.split(";");
		// String ssoToken = parts[0].split("=")[1];

		// System.out.println("ssoToken: " + ssoToken);
		// System.out.println();

		// if (respCode != 200) {
		// log.error("--Auth failed--");
		// throw new HttpException();
		// }

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

		System.out.println("calling: " + uri);

		HttpMethod method = new GetMethod(uri);
		method.addRequestHeader("cookie", "ObSSOCookie=" + ssoToken);
		method.setFollowRedirects(false);
		int status = authClient.executeMethod(method);
		InputStream body = method.getResponseBodyAsStream();
		Header ctype = method.getResponseHeader("content-type");
		String val = ctype.getValue();
		System.out.println("Content-Type: " + val);
	       
	       /*
		
		HttpGet method = new HttpGet(uri);
		// method.addHeader("cookie", "ObSSOCookie=" + ssoToken);

		// method.setFollowRedirects(false);

		DefaultHttpClient client = new DefaultHttpClient();

		client.setCookieStore(authClient.getCookieStore());

		HttpHost targetHost = new HttpHost("www.lds.org");
		HttpResponse httpResponse = client.execute(targetHost, method); 
		
		// int status = client.executeMethod(method);
		// InputStream body = method.getResponseBodyAsStream();
		InputStream body = httpResponse.getEntity().getContent();
		// Header[] ctype = method.getAllHeaders();
		// String val = ctype.
		// System.out.println("Content-Type: " + val);
		// return null;
		 
		 

		return body;
		
		*/
	}

	public static void loadMemberDataFileSimple(KeyValueDatabase database,
			String fileName) {
		try {
			loadMemberDataFileSimple(database, new BufferedReader(
					new FileReader(fileName)));
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", e);
		}
	}

	private static void loadMemberDataFileSimple(KeyValueDatabase database,
			BufferedReader data) {
		BufferedReader wardDir = null;

		try {
			String thisLine;

			while ((thisLine = data.readLine()) != null) {
				log.warn(thisLine);
				String[] fields = thisLine.split(":");
				String[] lastfield = { "" };
				if (fields[5].length() != 5) {
					lastfield = fields[5].split("<br>");
				}
				
				database.new WriterBuilder().table(fields[0]).row(fields[1]).column(fields[2]).time(fields[3]).value(fields[4]).data(lastfield[0]).go();
			}
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
}
