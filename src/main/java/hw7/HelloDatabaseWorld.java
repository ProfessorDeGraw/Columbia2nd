package hw7;

/**
 * @author Mark Hayes
 */
public class HelloDatabaseWorld  {

	public static String main() {
		
		KeyValueDatabase db = new KeyValueDatabase();
		db.databaseCycle();

		return db.getDbMessage();
	}

}