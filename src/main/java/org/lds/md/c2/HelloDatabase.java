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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
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
		
		String USR2 = "degraw";
		String PWD2 = "PASSWORD";
		// database.openDatabase();

		// database.actionWriterInitDB();
		// database.actionReader();

		// log.trace(database.actionAllKeys().toString());

		log.trace("Self Test");
		
		try {
			getElggPage(USR2, PWD2);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// database.actionAddKey("members", "family", "De Graw", "1");
		// database.actionAddKey("members", "family", "De Graw", "2");
		// database.actionAddKey("members", "family", "Smith", "1");
		// database.actionAddKey("members", "family", "Smith", "2");

		// log.trace(database.actionAllKeys().toString());

		// database.actionRemoveKey("members", "family", "De Graw");

		//database.actionRemoveAllKeys();
		
		//try {
		//	BufferedReader page = KeyValueDatabase.GetLDSPage(USR, PWD);
		//	KeyValueDatabase.loadMemberData(database, "new_members",
		//			page);
		//} catch (UnsupportedEncodingException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//} catch (IOException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}

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

	private void getElggPage(String username, String password) throws IOException, ClientProtocolException {
		String authUrl = "http://elgg-columbia2nd.rhcloud.com/services/api/rest/xml/?method=auth.gettoken";
		DefaultHttpClient authClient = new DefaultHttpClient();
		
		authClient.getParams().setParameter(
				"http.protocol.single-cookie-header", true);
		authClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		
		HttpPost auth = new HttpPost(authUrl);
		auth.setHeader("username", username);
		auth.setHeader("password", password);
		
		HttpHost AuthTargetHost = new HttpHost("elgg-columbia2nd.rhcloud.com");
		HttpResponse AuthHttpResponse = authClient.execute(AuthTargetHost, auth);
		
		List<Cookie> cookies = authClient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			log.trace("None Cookies");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				log.trace("- " + cookies.get(i).toString());
			}
		}
		
		log.trace("called: " + authUrl);
		log.trace("response: " + AuthHttpResponse.toString());
		
		String uri = "http://elgg-columbia2nd.rhcloud.com/services/api/rest/xml/?method=user.get_profile_fields";

		System.out.println("calling: " + uri);

		HttpGet method = new HttpGet(uri);
		// method.addHeader("cookie", "ObSSOCookie=" + ssoToken);

		// method.setFollowRedirects(false);

		DefaultHttpClient client = new DefaultHttpClient();

		client.setCookieStore(authClient.getCookieStore());

		HttpHost targetHost = new HttpHost("elgg-columbia2nd.rhcloud.com");
		HttpResponse httpResponse = client.execute(targetHost, method);

		// int status = client.executeMethod(method);
		// InputStream body = method.getResponseBodyAsStream();
		InputStream body = httpResponse.getEntity().getContent();
		Header[] ctype = method.getAllHeaders();
		// String val = ctype.
		// System.out.println("Content-Type: " + val);
		// return null;

		// now convert to characters. utf-8 is specified due to the content type
		// header's specified charset, should actually parse and used that but
		// I'll take a short cut for now
		InputStreamReader reader = new InputStreamReader(body, "utf-8");
		// char[] chars = new char[1024];
		// int charsRead = reader.read(chars);
		// StringWriter sw = new StringWriter();

		BufferedReader br = new BufferedReader(reader);
		
		String thisLine;
		while ((thisLine = br.readLine()) != null) {
			log.trace(thisLine);
		}
	}
}
