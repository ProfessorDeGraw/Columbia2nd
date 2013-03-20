package hw7;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Mark Hayes
 */
public class HelloDatabaseWorld  {

	public static String main() {
		
		KeyValueDatabase db = new KeyValueDatabase();
		//db.databaseCycle();
		db.actionWriter();
		db.actionReader();

		return db.getDbMessage();
	}

}