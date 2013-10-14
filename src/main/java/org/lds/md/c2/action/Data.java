package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Data")
public class Data implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory.getLogger(Data.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 2) {
			String table = parms.get(0);
			String column = parms.get(1);
			
			List<List<String>> data = database.new ReaderBuilder().table(table).column(column).go();
			StringBuilder sb = new StringBuilder();
			for (List<String> i : data) {
				if (column.equals("key")) {
					sb.append(i.get(1).toString() + "<br>");
				} else if (column.equals("Family Address")) {
					List<List<String>> geocode = database.new ReaderBuilder().table(table).row(i.get(1)).column("geocode").go();
					Geocode code = new Geocode();
					
					sb.append(i.get(1).toString() + ":"
							+ i.get(4).toString() + ":" + 
							code.findGeocode(geocode.get(0).get(4)) + ":" +
							geocode.get(0).get(4).toString() + "<br>");
				} else {
					if (!i.get(4).toString().trim().equals("")) {
						if (column.contains("Email")) {
							sb.append(i.get(4).toString() + "<br>");
						} else {
							sb.append(i.get(1).toString() + ":"
									+ i.get(4).toString() + "<br>");
						}
					}
				}
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
