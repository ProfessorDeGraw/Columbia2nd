package org.lds.md.c2.action;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
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
import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component("Geocode")
public class Geocode implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(Geocode.class);

	KeyValueDatabase database = null;
	
	private Map<String, Area> placemarksList = null;
	private String cacheTagLoaded = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String address = parms.get(0);

			StringBuilder sb = new StringBuilder();

			String geoArea = findGeocode(address);

			sb.append("Found Geoarea:" + geoArea + "<br>");

			return sb.toString();
		}
		return "Data";
	}

	public String findGeocode(String geocode) {
		//Point2D p = addressToGeocode(address);
		String[] parts = geocode.split(",");
		
		Point2D p;
		try {
			p = new Point2D.Double(Double.parseDouble(parts[1]), Double.parseDouble(parts[0]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return "Not Found";
		}
		
		InputStream kmlStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("/TrashDays4.xml");
		Map<String, Area> placemarksList = kmlToArea(kmlStream, "TrashDays4");
		String geoArea = containsGeoarea(p, placemarksList);
		return geoArea;
	}

	private String containsGeoarea(Point2D p, Map<String, Area> placemarksList) {
		String geoArea = "Not Found";

		for (String place : placemarksList.keySet()) {
			Area t = placemarksList.get(place);
			if (t.contains(p)) {
				geoArea = place;
			}
		}
		return geoArea;
	}

	private Map<String, Area> kmlToArea(InputStream kmlFile, String cacheTag) {

		if (placemarksList == null || cacheTagLoaded == null
				|| !cacheTagLoaded.equalsIgnoreCase(cacheTag) ) {
			cacheTagLoaded = cacheTag;
			placemarksList = new HashMap<String, Area>();

			try {
				DocumentBuilderFactory docBuilderFactory2 = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder2 = docBuilderFactory2
						.newDocumentBuilder();
				Document doc2 = docBuilder2.parse(kmlFile);

				// normalize text representation
				doc2.getDocumentElement().normalize();
				log.trace("Root element of the doc is {}", doc2
						.getDocumentElement().getNodeName());

				NodeList listOfPersons2 = doc2
						.getElementsByTagName("Placemark");
				int totalPersons2 = listOfPersons2.getLength();
				log.trace("Total no of Placemark : {}", totalPersons2);

				for (int s = 0; s < listOfPersons2.getLength(); s++) {

					Node firstPersonNode = listOfPersons2.item(s);
					if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

						Element firstPersonElement = (Element) firstPersonNode;

						// -------
						NodeList nameList = firstPersonElement
								.getElementsByTagName("name");
						Element nameElement = (Element) nameList.item(0);

						NodeList textNameList = nameElement.getChildNodes();
						log.trace("name:{}", ((Node) textNameList.item(0))
								.getNodeValue().trim());

						String coordinateName = ((Node) textNameList.item(0))
								.getNodeValue();

						// -------
						NodeList coordinatesList = firstPersonElement
								.getElementsByTagName("coordinates");
						Element coordinatesElement = (Element) coordinatesList
								.item(0);

						NodeList textCoordinatesList = coordinatesElement
								.getChildNodes();
						log.trace("coordinates:{}", ((Node) textCoordinatesList
								.item(0)).getNodeValue().trim());

						String allPoints = ((Node) textCoordinatesList.item(0))
								.getNodeValue();

						Pattern pattern = Pattern
								.compile("([-]?[+]?[\\d]+.[\\d]+,[-]?[+]?[\\d]+.[\\d]+)");
						Matcher matcher = pattern.matcher(allPoints);

						boolean isFirstGeopoint = true;
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
										isFirstGeopoint = false;
									} else {
										boundary.lineTo(doublePoint.getX(),
												doublePoint.getY());
									}
								}

								log.trace("start:{}", match);
							}
						}

						boundary.closePath();

						Area area = new Area(boundary);

						if (area.isEmpty()) {
							log.warn("empty area");
						}
						placemarksList.put(coordinateName, area);
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return placemarksList;
	}

	public Point2D addressToGeocode(String address) {
		InputStream a;
		a = getInfoBody(address);
		Point2D p = new Point2D.Double();

		int numberOfAttempts = 0;
		if (address.length()<4) {
			numberOfAttempts=3;
			log.warn("Geo skiped for:{}, address to short", address);
		}
		while (numberOfAttempts++<3 && ( p.getX() == 0 || p.getX() == 0) ) {
		try {
			if (numberOfAttempts>1) {
				try {
					log.warn("Geo lookup failed for:{}, trying the address", address);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(a);

			// normalize text representation
			doc.getDocumentElement().normalize();
			log.trace("Root element of the doc is {}", doc.getDocumentElement()
					.getNodeName());

			NodeList listOfPersons = doc.getElementsByTagName("location");
			int totalPersons = listOfPersons.getLength();
			log.trace("Total no of people : {}", totalPersons);

			for (int s = 0; s < listOfPersons.getLength(); s++) {

				Node firstPersonNode = listOfPersons.item(s);
				if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstPersonElement = (Element) firstPersonNode;

					// -------
					NodeList lastNameList = firstPersonElement
							.getElementsByTagName("lng");
					Element lastNameElement = (Element) lastNameList.item(0);

					NodeList textLNList = lastNameElement.getChildNodes();
					log.trace("lng: {}", ((Node) textLNList.item(0))
							.getNodeValue().trim());

					Double x = Double.valueOf(((Node) textLNList.item(0))
							.getNodeValue());

					// -------
					NodeList firstNameList = firstPersonElement
							.getElementsByTagName("lat");
					Element firstNameElement = (Element) firstNameList.item(0);

					NodeList textFNList = firstNameElement.getChildNodes();
					log.trace("lat:{}", ((Node) textFNList.item(0))
							.getNodeValue().trim());

					Double y = Double.valueOf(((Node) textFNList.item(0))
							.getNodeValue());

					p.setLocation(x, y);

				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if (numberOfAttempts>3) {
			log.warn("Geo lookup failed for:{}", address);
		}
		return p;
	}

	private static InputStream getInfoBody(String streeetAddress) {

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("address", streeetAddress));
		params.add(new BasicNameValuePair("sensor", "false"));

		String paramsUTF = URLEncodedUtils.format(params, "utf-8");

		String uri = "http://maps.googleapis.com/maps/api/geocode/xml?"
				+ paramsUTF;

		log.trace("calling: {}", uri);

		HttpGet method = new HttpGet(uri);

		DefaultHttpClient client = new DefaultHttpClient();

		HttpHost targetHost = new HttpHost("maps.googleapis.com");

		InputStream body = null;
		try {
			HttpResponse httpResponse = client.execute(targetHost, method);
			body = httpResponse.getEntity().getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return body;
	}

	@Override
	public void destroy() throws Exception {
		if (database != null) {
			database.closeDatabase();
		}
	}

}
