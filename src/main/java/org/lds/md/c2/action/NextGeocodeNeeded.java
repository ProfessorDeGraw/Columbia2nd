package org.lds.md.c2.action;

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

@Component("NextGeocodeNeeded")
public class NextGeocodeNeeded implements BeanRequest, DisposableBean {
	private static final Logger log = LoggerFactory
			.getLogger(NextGeocodeNeeded.class);

	@Autowired
	private KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String table = parms.get(0);
			//String column = parms.get(1);
			List<List<String>> names = database.new ReaderBuilder().table(table).column("key").go();
			
			List<List<String>> geocode = database.new ReaderBuilder().table(table).column("Geocode").go();
			
			List<String> namesList = new ArrayList<String>();
			for (List<String> i : names) {
				namesList.add(i.get(1).toString());
			}
			
			List<String> geocodeList = new ArrayList<String>();
			for (List<String> i : geocode) {
				geocodeList.add(i.get(1).toString());
			}
			
			for (String i: namesList) {
				if (!geocodeList.contains(i)) {
					log.warn("Need to geocode:" + i);
					return i;
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
