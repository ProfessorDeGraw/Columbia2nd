package org.lds.md.c2;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component("Geocode")
public class Geocode implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(Geocode.class);

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String address = parms.get(0);

			InputStream a;
			try {

				StringBuilder sb = new StringBuilder();

				a = getInfoBody(address);

				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory
						.newDocumentBuilder();
				Document doc = docBuilder.parse(a);

				// normalize text representation
				doc.getDocumentElement().normalize();
				log.trace("Root element of the doc is {}", 
						doc.getDocumentElement().getNodeName());

				NodeList listOfPersons = doc.getElementsByTagName("location");
				int totalPersons = listOfPersons.getLength();
				log.trace("Total no of people : {}", totalPersons);

				Point2D p = new Point2D.Double();

				for (int s = 0; s < listOfPersons.getLength(); s++) {

					Node firstPersonNode = listOfPersons.item(s);
					if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

						Element firstPersonElement = (Element) firstPersonNode;

						// -------
						NodeList lastNameList = firstPersonElement
								.getElementsByTagName("lng");
						Element lastNameElement = (Element) lastNameList
								.item(0);

						NodeList textLNList = lastNameElement.getChildNodes();
						log.trace("lng: {}"
								, ((Node) textLNList.item(0)).getNodeValue()
										.trim());

						Double x = Double.valueOf(((Node) textLNList.item(0))
								.getNodeValue());

						// -------
						NodeList firstNameList = firstPersonElement
								.getElementsByTagName("lat");
						Element firstNameElement = (Element) firstNameList
								.item(0);

						NodeList textFNList = firstNameElement.getChildNodes();
						log.trace("lat:{}"
								, ((Node) textFNList.item(0)).getNodeValue()
										.trim());

						Double y = Double.valueOf(((Node) textFNList.item(0))
								.getNodeValue());

						p.setLocation(x, y);

					}
				}

				DocumentBuilderFactory docBuilderFactory2 = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder2 = docBuilderFactory2
						.newDocumentBuilder();
				Document doc2 = docBuilder2.parse(Thread.currentThread()
						.getContextClassLoader()
						.getResourceAsStream("/TrashDays4.xml"));

				// normalize text representation
				doc2.getDocumentElement().normalize();
				log.trace("Root element of the doc is {}"
						, doc2.getDocumentElement().getNodeName());

				NodeList listOfPersons2 = doc2
						.getElementsByTagName("Placemark");
				int totalPersons2 = listOfPersons2.getLength();
				log.trace("Total no of Placemark : {}", totalPersons2);

				Map<String, Area> placemarksList = new HashMap<String, Area>();

				for (int s = 0; s < listOfPersons2.getLength(); s++) {

					Node firstPersonNode = listOfPersons2.item(s);
					if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

						Element firstPersonElement = (Element) firstPersonNode;

						// -------
						NodeList nameList = firstPersonElement
								.getElementsByTagName("name");
						Element nameElement = (Element) nameList.item(0);

						NodeList textNameList = nameElement.getChildNodes();
						log.trace("name:{}"
								, ((Node) textNameList.item(0)).getNodeValue()
										.trim());

						String coordinateName = ((Node) textNameList.item(0))
								.getNodeValue();

						// -------
						NodeList coordinatesList = firstPersonElement
								.getElementsByTagName("coordinates");
						Element coordinatesElement = (Element) coordinatesList
								.item(0);

						NodeList textCoordinatesList = coordinatesElement
								.getChildNodes();
						log.trace("coordinates:{}"
								, ((Node) textCoordinatesList.item(0))
										.getNodeValue().trim());

						String allPoints = ((Node) textCoordinatesList.item(0))
								.getNodeValue();

						Pattern pattern = Pattern
								.compile("([-]?[+]?[\\d]+.[\\d]+,[-]?[+]?[\\d]+.[\\d]+)");
						Matcher matcher = pattern.matcher(allPoints);


						boolean isFirstGeopoint = true;
						//Point2D lastGeopoint = null;
						//Point2D firstGeopoint = null;
						Path2D boundary = new Path2D.Double();

						while (matcher.find()) {
							for (int i = 1; i <= matcher.groupCount(); i++) {
								String match = matcher.group(i);

								String[] lngLat = match.split(",");

								if (lngLat.length > 1) {
									Double x = Double.valueOf(lngLat[0]);
									Double y = Double.valueOf(lngLat[1]);
									Point2D doublePoint = new Point2D.Double(x,
											y);

									if (isFirstGeopoint) {
										boundary.moveTo(doublePoint.getX(),
												doublePoint.getY());
										isFirstGeopoint=false;
								} else {
										//boundary.moveTo(lastGeopoint.getX(),
										//		lastGeopoint.getY());
										boundary.lineTo(doublePoint.getX(),
												doublePoint.getY());
									}
									//lastGeopoint = doublePoint;
								}

								log.warn("start:{}", match);
							}
						}

						//boundary.moveTo(lastGeopoint.getX(),
						//		lastGeopoint.getY());
						//boundary.lineTo(firstGeopoint.getX(),
						//		firstGeopoint.getY());
						
						boundary.closePath();

						Area area = new Area(boundary);
						
						if ( area.isEmpty() ) {
							log.warn("empty area");
						}
						
						//PathIterator pi = boundary.getPathIterator(null);
						
						//Path2D ssb = new Path2D.Double();
						//ssb.moveTo(1, 1);
						//ssb.lineTo(1, 3);
						
						//ssb.moveTo(1, 3);
						//ssb.lineTo(3, 3);
						
						//ssb.moveTo(3, 3);
						//ssb.lineTo(3, 1);
						
						//ssb.moveTo(3, 1);
						//ssb.lineTo(1, 1);
						
						//ssb.closePath();
						
						//Area ssa = new Area(ssb);
						
						//if ( ssa.isEmpty() ) {
						//	log.warn("empty area");
						//}
						
						//if (ssa.contains(2, 2)) {
						//	log.warn("It is in it 22");
						//}
						
						//while (pi.isDone()) {
						//	double[] type = new double[6];
						//	int leg = pi.currentSegment(type);
						//	log.warn("{}", leg);
						//}
						
						if ( boundary.contains(p) ) {
							log.warn("Found it there");
						}
						
						if ( boundary.contains(p.getX(), p.getY()) ) {
							log.warn("Found it there 2");
						}
						
						if ( area.contains(p) ) {
							log.warn("Found it here");
						}
						
						if ( area.contains(p.getX(), p.getY()) ) {
							log.warn("Found it here q");
						}


						placemarksList.put(coordinateName, area);
					}
				}

				String geoArea = "Not Found";

				for (String place : placemarksList.keySet()) {
					Area t = placemarksList.get(place);
					if (t.contains(p)) {
						geoArea = place;
					}
				}

				sb.append("Found Geoarea:" + geoArea + "<br>");

				// String it;
				// while ( (it=a.readLine()) != null) {
				// sb.append(it + "<br>");
				// }
				return sb.toString();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "Data";
	}

	private static InputStream getInfoBody(String streeetAddress)
			throws ClientProtocolException, IOException {
		/*
		 * WARNING: https requires that the SSL certificates served by the
		 * server be located in the local trust store otherwise request will
		 * fail with a certificate exception.
		 */

		// String authUrl = "https://www.lds.org/login.html";
		// DefaultHttpClient authClient = new DefaultHttpClient();

		// authClient.getParams().setParameter(
		// "http.protocol.single-cookie-header", true);
		// authClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
		// CookiePolicy.BROWSER_COMPATIBILITY);

		// HttpPost auth = new HttpPost(authUrl);
		// auth.setHeader("username", USR);
		// auth.setHeader("password", PWD);

		// CookieStore cookieStore = new CookieStore();

		// authClient.setCookieStore(cookieStore);

		// auth.setFollowRedirects(false);

		// HttpHost AuthTargetHost = new HttpHost("lds.org");
		// HttpResponse AuthHttpResponse = authClient
		// .execute(AuthTargetHost, auth);

		// int respCode = client.executeMethod(auth);
		// Header location = auth.getResponseHeader("location");
		// DefaultHttpClient httpclient = new DefaultHttpClient();

		// List<Cookie> cookies = authClient.getCookieStore().getCookies();
		// if (cookies.isEmpty()) {
		// log.trace("None Cookies");
		// } else {
		// for (int i = 0; i < cookies.size(); i++) {
		// log.trace("- " + cookies.get(i).toString());
		// }
		// }

		// Header cookieHrd = auth.getFirstHeader("set-cookie");

		// log.trace("called: " + authUrl);
		// log.trace("response: " + AuthHttpResponse.toString());

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

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("address", streeetAddress));
		params.add(new BasicNameValuePair("sensor", "false"));

		String paramsUTF = URLEncodedUtils.format(params, "utf-8");

		String uri = "http://maps.googleapis.com/maps/api/geocode/xml?"
				+ paramsUTF;

		log.trace("calling: {}", uri);

		HttpGet method = new HttpGet(uri);
		// method.addHeader("cookie", "ObSSOCookie=" + ssoToken);

		// method.setFollowRedirects(false);

		DefaultHttpClient client = new DefaultHttpClient();

		// client.setCookieStore(authClient.getCookieStore());

		HttpHost targetHost = new HttpHost("maps.googleapis.com");
		HttpResponse httpResponse = client.execute(targetHost, method);

		// int status = client.executeMethod(method);
		// InputStream body = method.getResponseBodyAsStream();
		InputStream body = httpResponse.getEntity().getContent();
		// Header[] ctype = method.getAllHeaders();
		// String val = ctype.
		// System.out.println("Content-Type: " + val);
		// return null;

		return body;
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
