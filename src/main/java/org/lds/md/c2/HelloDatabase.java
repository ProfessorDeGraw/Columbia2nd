package org.lds.md.c2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
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
		// database.initDB();
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

		String USR = "nathandegraw";
		String PWD = "PASSWORD";
		// database.openDatabase();

		// database.actionWriterInitDB();
		// database.actionReader();

		// log.trace(database.actionAllKeys().toString());

		log.trace("Self Test");

		// database.actionAddKey("members", "family", "De Graw", "1");
		// database.actionAddKey("members", "family", "De Graw", "2");
		// database.actionAddKey("members", "family", "Smith", "1");
		// database.actionAddKey("members", "family", "Smith", "2");

		// log.trace(database.actionAllKeys().toString());

		// database.actionRemoveKey("members", "family", "De Graw");

		database.actionRemoveAllKeys();
		
		try {
			BufferedReader page = KeyValueDatabase.GetLDSPage(USR, PWD);
			KeyValueDatabase.loadMemberData(database, "new_members",
					page);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//String request = "https://signin.lds.org/login.html?username=nathandegraw&password=PASSWORD";

		//String ldsDirURL = "https://www.lds.org/directory/services/ludrs/unit/member-list/200239/csv";

		// log.trace(database.actionAllKeys().toString());

		// database.actionRemoveAllKeys();

		// log.trace(database.actionAllKeys().toString());

		// KeyValueDatabase.loadMemberDataFile(database, "old_members",
		// "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/200239_old.csv");
		// KeyValueDatabase.loadMemberDataFile(database, "new_members",
		// "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/200239.csv");

		// KeyValueDatabase.detectChanges(database, "old_members",
		// "new_members");

		// log.trace("========================");

		// for ( int i=0; i<13; i++) {
		// log.trace(database.actionAllKeys().get(i));
		// }

		// database.actionReader();
		// log.trace(database.actionAllKeys().toString());
	}
}
