package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("FindSameGeoarea")
public class FindSameGeoarea implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory.getLogger(Data.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 2) {
			String table = parms.get(0);
			String name = parms.get(1);
			
			List<List<String>> geocode = database.new ReaderBuilder().table(table).row(name).column("geoarea").go();
			String areaName = geocode.get(0).get(4);
			
			List<List<String>> inArea = database.new ReaderBuilder().table(table).column("geoarea").value(areaName).go();
	
			StringBuilder sb = new StringBuilder();
			
			List<List<String>> thisFamily = database.new ReaderBuilder().table(table).row(name).column("Family Address").go();
			String thisFamilyAddress = thisFamily.get(0).get(4);
			
			sb.append("https://maps.google.com/maps?");
			sb.append("saddr=" + thisFamilyAddress);
			sb.append("&waypoints=optimize:true");
			sb.append("&daddr=" + thisFamilyAddress);
			
			for (List<String> i : inArea) {
				String familyName = i.get(1);
				
				List<List<String>> family = database.new ReaderBuilder().table(table).row(familyName).column("Family Address").go();
				String familyAddress = family.get(0).get(4);
				 
				sb.append("++to:"+ familyAddress.trim());
			}
			sb.append("&sensor=false");
			return sb;
		}
		return "Data";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
