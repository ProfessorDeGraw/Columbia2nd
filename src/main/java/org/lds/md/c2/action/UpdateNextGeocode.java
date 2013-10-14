package org.lds.md.c2.action;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("UpdateNextGeocode")
public class UpdateNextGeocode implements BeanRequest, DisposableBean {
	private static final Logger log = LoggerFactory
			.getLogger(UpdateNextGeocode.class);

	@Autowired
	private KeyValueDatabase database;
	
	@Autowired
	Geocode coder;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String table = parms.get(0);
			//String column = parms.get(1);
			List<List<String>> names = database.new ReaderBuilder().table(table).column("key").go();
			
			List<List<String>> geocodes = database.new ReaderBuilder().table(table).column("geocode").go();
			
			List<String> namesList = new ArrayList<String>();
			for (List<String> i : names) {
				namesList.add(i.get(1).toString());
			}
			
			List<String> geocodeList = new ArrayList<String>();
			for (List<String> i : geocodes) {
				geocodeList.add(i.get(1).toString());
			}
			
			for (String name: namesList) {
				if (!geocodeList.contains(name)) {
					log.warn("Need to geocode:" + name);
					
					List<List<String>> addresses = database.new ReaderBuilder().table(table).row(name).column("Family Address").go();
					
					boolean addressFound = false;
					for (List<String> k : addresses) {
						if ( k.get(1).toString().equals(name) ) {
							addressFound=true;
							String address = k.get(4).toString();
							log.warn("Coding Address:" + address);
							Point2D point = coder.addressToGeocode(address);
							if (point.getX() == 0 || point.getY() == 0) {
								database.new WriterBuilder().table(table).row(name).column("geocode").value("no,address").go();
							} else {
								database.new WriterBuilder().table(table).row(name).column("geocode").value(point.getY() + "," + point.getX()).go();
							}
						}
					}
					if (!addressFound) {
						log.warn("No address to geocode");
						database.new WriterBuilder().table(table).row(name).column("geocode").value("no,address").go();
					}
					
					return ("Geocoded address:" + name);
				}
			}

			log.warn("All Geocodes up to data");
			return "All Geocodes up to data";
		}
		return "Data";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
