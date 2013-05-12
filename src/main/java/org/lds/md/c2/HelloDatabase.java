package org.lds.md.c2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
		String PWD = "password";
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
			BufferedReader page = GetLDSPage(USR, PWD);
			KeyValueDatabase.loadMemberData(database, "new_members",
					page);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//String request = "https://signin.lds.org/login.html?username=nathandegraw&password=Chur7351";

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

	private BufferedReader GetLDSPage(String USR, String PWD) throws IOException,
			HttpException, UnsupportedEncodingException {
		InputStream body = getInfoBody(USR, PWD);

		// now convert to characters. utf-8 is specified due to the content type
		// header's specified charset, should actually parse and used that but
		// I'll take a short cut for now
		InputStreamReader reader = new InputStreamReader(body, "utf-8");
		//char[] chars = new char[1024];
		//int charsRead = reader.read(chars);
		//StringWriter sw = new StringWriter();
		
		return new BufferedReader(reader);

		//while (charsRead != -1) {
		//	sw.write(chars, 0, charsRead);
		//	charsRead = reader.read(chars);
		//}

		//System.out.println(sw.toString());
	}

	private InputStream getInfoBody(String USR, String PWD) throws IOException,
			HttpException {
		/*
		 * WARNING: https requires that the SSL certificates served by the
		 * server be located in the local trust store otherwise request will
		 * fail with a certificate exception.
		 */

		String authUrl = "https://lds.org/login.html";
		HttpClient client = new HttpClient();
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		PostMethod auth = new PostMethod(authUrl);
		auth.addParameter("username", USR);
		auth.addParameter("password", PWD);

		auth.setFollowRedirects(false);
		int respCode = client.executeMethod(auth);
		// Header location = auth.getResponseHeader("location");
		Header cookieHrd = auth.getResponseHeader("set-cookie");

		System.out.println("called: " + authUrl);
		System.out.println("response: " + respCode);
		// System.out.println("location: " + location.toExternalForm());

		HeaderElement[] elements = cookieHrd.getElements();
		for (int i = 0; elements != null && i < elements.length; i++) {
			System.out.println("Received set-cookie: " + elements[i].getName()
					+ "=" + elements[i].getValue());
		}
		String value = cookieHrd.getValue();
		String[] parts = value.split(";");
		String ssoToken = parts[0].split("=")[1];

		System.out.println("ssoToken: " + ssoToken);
		System.out.println();

		if (respCode != 200) {
			log.error("--Auth failed--");
			throw new HttpException();
		}

		// ------ now access restricted resource -------

		//long curr = System.currentTimeMillis();
		//GregorianCalendar cal = new GregorianCalendar();
		//cal.set(cal.get(GregorianCalendar.YEAR),
		//		cal.get(GregorianCalendar.MONTH),
		//		cal.get(GregorianCalendar.DATE), 0, // hours
		//		0, // minutes
		//		0); // seconds
		//long startOfToday = cal.getTimeInMillis();
		//long totalDaysDesired = 30;
		//long daysOut = startOfToday + totalDaysDesired * 24L * 60L * 60L
		//		* 1000L;

		//String uri = "https://lds.org/church-calendar/services/lucrs/evt/locations/0/"
		//		+ startOfToday + "-" + daysOut + "/L/";
		String uri="https://www.lds.org/directory/services/ludrs/unit/member-list/200239/csv";
		
		System.out.println("calling: " + uri);

		HttpMethod method = new GetMethod(uri);
		method.addRequestHeader("cookie", "ObSSOCookie=" + ssoToken);
		method.setFollowRedirects(false);
		int status = client.executeMethod(method);
		InputStream body = method.getResponseBodyAsStream();
		Header ctype = method.getResponseHeader("content-type");
		String val = ctype.getValue();
		System.out.println("Content-Type: " + val);
		return body;
	}
}
