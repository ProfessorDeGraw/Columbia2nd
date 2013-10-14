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

@Component("NextDistanceMatrixNeeded")
public class NextDistanceMatrixNeeded implements BeanRequest, DisposableBean {
	private static final Logger log = LoggerFactory
			.getLogger(NextDistanceMatrixNeeded.class);

	@Autowired
	private KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String table = parms.get(0);
			//String column = parms.get(1);
			String status = updateNextDistanceMatrix(table);

			log.warn(status);
			return status;
		}
		return "Data";
	}

	private String updateNextDistanceMatrix(String table) {
		List<List<String>> names = database.new ReaderBuilder().table(table).column("key").go();
		
		List<List<String>> distancematrix = database.new ReaderBuilder().table(table).column("distancematrix").go();
		
		List<String> namesList = new ArrayList<String>();
		for (List<String> i : names) {
			namesList.add(i.get(1).toString());
		}
		
		List<String> distancematrixList = new ArrayList<String>();
		for (List<String> i : distancematrix) {
			String name = i.get(1).toString();
			distancematrixList.add(name);
			if ( namesList.contains(name) ) {
				namesList.remove(name);
			}
		}
		
		for (String currentName: namesList) {
			//if (!distancematrixList.contains(currentName)) {
				log.warn("Need to Distance Matrix:" + currentName);
				
				List<List<String>> distancematrixelements = database.new ReaderBuilder().table(table).row(currentName).column("distancematrixelement").go();
				
				//List<List<String>> distancematrixelements = database.actionAllKeysByTableByRowByColumn(table, currentName, "distancematrixelement");
				
				log.warn("1:distancematrixList" + distancematrixList.size() + ", distancematrixelements:" + distancematrixelements.size());
				
				//List<String> visitedList = new ArrayList<String>();
				for (List<String> distancematrixelement : distancematrixelements) {
					String distancematrixelementValue = distancematrixelement.get(4);
					String[] values = distancematrixelementValue.split(":");
					String valuesName = values[0];
					//visitedList.add(valuesName);
					if (distancematrixList.contains(valuesName)) {
						distancematrixList.remove(valuesName);
						//log.warn("All ready visited:" + valuesName);
					} else {
						log.warn("valuesName:" + valuesName + ", not in distancematrixList");
					}
				}
				
				log.warn("2:distancematrixList" + distancematrixList.size() + ", distancematrixelements:" + distancematrixelements.size());
						
				//boolean allVisited = true;
				//for (String others : distancematrixList) {
					//log.warn("Others:" + others);
				//	allVisited=false;
				//}
				
				if (distancematrixList.size()>1) {
					//visit first 99
					
					if ( distancematrixList.size() > 97 ) {
						log.warn("97 case");
					}
					
					int endOfList = (distancematrixList.size() > 99 ) ? 98 : distancematrixList.size()-1;
					
					List<String> next99 = distancematrixList.subList(0, endOfList);
					
					log.warn("Adding:" + next99.size());
					for (String others : next99) {
						//log.warn("Others to visit:" + others);
						
						database.new WriterBuilder().table(table).row(currentName).column("distancematrixelement").value(others +":1").go();
					}
					if (endOfList<98) {
						log.warn(currentName + " marking all visited");
						database.new WriterBuilder().table(table).row(currentName).column("distancematrix").value("").go();
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					log.warn(currentName + " is all visited");
					database.new WriterBuilder().table(table).row(currentName).column("distancematrix").value("").go();
				}
				
				//updateNextDistanceMatrix(table);
				return currentName;
			//}
		}
		return "All distancematrix up to data";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
