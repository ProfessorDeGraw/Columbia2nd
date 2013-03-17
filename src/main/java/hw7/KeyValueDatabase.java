package hw7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
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

class KeyValueDatabaseWriter implements TransactionWorker {

	KeyValueDatabase db;
	
	KeyValueDatabaseWriter(KeyValueDatabase myDb) {
		db = myDb;
	}
	
	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		db.writeManyKeysFromFile("/tmp/occurrence.txt");
	}
	
}

public class KeyValueDatabase {
	
	private static boolean create = true;
	
	private Environment env;
	private ClassCatalog catalog;
	private Database db;
	private SortedMap<String, String> map;

	private StringBuilder dbMessage = new StringBuilder("Database is not ready.");
	
	public String getDbMessage() {
		return dbMessage.toString();
	}
	
	public void databaseCycle() {
		String dir = "/tmp/berkeleydb";

		// environment is transactional
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		if (create) {
			envConfig.setAllowCreate(true);
		}
		Environment env;
		try {
			env = new Environment(new File(dir), envConfig);
			
			// create the application and run a transaction
			//HelloDatabaseWorld worker = new KeyValueDatabase(env);
			try {
				this.setupDatabase(env);
				
				actionWriter();
				actionReader();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			closeDatabase();
			
		} catch (EnvironmentLockedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionWriter() {
		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			KeyValueDatabaseWriter writer = new KeyValueDatabaseWriter(this);
			//KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				runner.run(writer);
				//runner.run(reader);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			// close the database outside the transaction
			try {
				//this.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void actionReader() {
		TransactionRunner runner = new TransactionRunner(env);
		try {
			// open and access the database within a transaction
			//KeyValueDatabaseWriter writer = new KeyValueDatabaseWriter(this);
			KeyValueDatabaseReader reader = new KeyValueDatabaseReader(this);
			try {
				//runner.run(writer);
				runner.run(reader);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			// close the database outside the transaction
			try {
				//this.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** Creates the database for this application */
	private void setupDatabase(Environment env) throws Exception {
		this.env = env;
		open();
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
		TupleBinding<String> keyBinding = TupleBinding
				.getPrimitiveBinding(String.class);

		// use String serial binding for data entries
		SerialBinding<String> dataBinding = new SerialBinding<String>(catalog,
				String.class);

		this.db = env.openDatabase(null, "Keyvaluedatabase", dbConfig);

		// create a map view of the database
		this.map = new StoredSortedMap<String, String>(db, keyBinding,
				dataBinding, true);
	}
	
	public void closeDatabase() {
		try {
			this.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			BufferedReader reader = new BufferedReader(new FileReader(
					file));
			Pattern tab = Pattern.compile("\t");
			String line = reader.readLine();
			long count = 1;
			long time = System.currentTimeMillis();

			long kvp = 1;

			while (line != null) {
				String parts[] = tab.split(line);
				try {
					// map.put(parts[0], line);
					int i = 0;
					for (String s : parts) {
						map.put(parts[0] + ":" + i, s);
						i++;
					}
					kvp += i;

				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				if (count % 10000 == 0) {
					System.out
							.println("Added ["
									+ count
									+ "].  Average["
									+ (1000 * kvp / (System.currentTimeMillis() - time))
									+ " kvp/sec] ["
									+ (1000 * count / (System
											.currentTimeMillis() - time))
									+ " records/sec]");
					System.out.println("last key: " + parts[0]);
					// if (count%1000000==0) break;
				}

				count++;
				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readAllKeys() {
		System.out.println("Reading data");
		// Iterator<Map.Entry<String, String>> iter = map.tailMap("47874585:")
		// .entrySet().iterator();
		Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
		dbMessage = new StringBuilder();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = iter.next();
			// if (!entry.getKey().startsWith("47874585:")) {
			// break;
			// }
			dbMessage
					.append(entry.getKey().toString() + ' ' + entry.getValue());
			System.out.println(entry.getKey().toString() + ' '
					+ entry.getValue());
		}
		iter = null;
	}
}