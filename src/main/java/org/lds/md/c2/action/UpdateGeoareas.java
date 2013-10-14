package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("UpdateGeoareas")
public class UpdateGeoareas implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory.getLogger(Data.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String table = parms.get(0);
			
			List<List<String>> data = database.new ReaderBuilder().table(table).column("Family Address").go();
			StringBuilder sb = new StringBuilder();
			for (List<String> i : data) {

				List<List<String>> geocode = database.new ReaderBuilder().table(table).row(i.get(1)).column("geocode").go();
				Geocode code = new Geocode();
				String name = i.get(1).toString();
				String geoname = code.findGeocode(geocode.get(0).get(4));
				sb.append(name + ":"
						+ geoname + "<br>");
				database.new WriterBuilder().table(table).row(name).column("geoarea").value(geoname).go();
			}
			return sb;
		}
		return "Data";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
