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

	@Autowired
	Geocode coder;
	
	static int rateCounter = 0;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String table = parms.get(0);
			// String column = parms.get(1);
			String status = updateNextDistanceMatrix(table);

			// log.warn(status);
			return status;
		}
		return "Data";
	}

	private String updateNextDistanceMatrix(String table) {
		
		List<List<String>> names = database.new ReaderBuilder().table(table)
				.column("key").go();

		List<List<String>> distancematrix = database.new ReaderBuilder()
				.table(table).column("distancematrix").go();

		List<String> namesList = new ArrayList<String>();
		for (List<String> i : names) {
			namesList.add(i.get(1).toString());
		}

		List<String> distancematrixList = new ArrayList<String>();
		for (List<String> i : distancematrix) {
			String name = i.get(1).toString();
			distancematrixList.add(name);
			if (namesList.contains(name)) {
				namesList.remove(name);
			}
		}

		for (String currentName : namesList) {
			// if (!distancematrixList.contains(currentName)) {
			// log.warn("Need to Distance Matrix:" + currentName);

			List<List<String>> distancematrixelements = database.new ReaderBuilder()
					.table(table).row(currentName)
					.column("distancematrixelement").go();

			// List<List<String>> distancematrixelements =
			// database.actionAllKeysByTableByRowByColumn(table, currentName,
			// "distancematrixelement");

			// log.warn("1:distancematrixList:" + distancematrixList.size()
			// + ", distancematrixelements:"
			// + distancematrixelements.size());

			// List<String> visitedList = new ArrayList<String>();
			for (List<String> distancematrixelement : distancematrixelements) {
				// fixme;

				String valuesName = distancematrixelement.get(4);
				// visitedList.add(valuesName);
				if (distancematrixList.contains(valuesName)) {
					distancematrixList.remove(valuesName);
					// log.warn("All ready visited:" + valuesName);
				} else {
					// log.warn("valuesName:" + valuesName
					// + ", not in distancematrixList");
				}
			}

			// log.warn("2:distancematrixList:" + distancematrixList.size()
			// + ", distancematrixelements:"
			// + distancematrixelements.size());

			// boolean allVisited = true;
			// for (String others : distancematrixList) {
			// log.warn("Others:" + others);
			// allVisited=false;
			// }

			if (distancematrixList.size() > 0) {
				// visit first 99

				// if (distancematrixList.size() > 97) {
				// log.warn("97 case");
				// }

				int endOfList = (distancematrixList.size() > 99) ? 98
						: distancematrixList.size();

				List<String> next99 = distancematrixList.subList(0, endOfList);

				// log.warn("Adding:{}", next99.size());
				rateCounter += next99.size();
				for (String others : next99) {
					int drivingDistance = findDrivingDistance(table,
							currentName, others);

					database.new WriterBuilder().table(table).row(currentName)
							.column("distancematrixelement").value(others)
							.data(Integer.toString(drivingDistance)).go();
				}
				if (endOfList < 98) {
					log.warn("{} marking all visited", currentName);
					database.new WriterBuilder().table(table).row(currentName)
							.column("distancematrix").value("").go();
					
					if ((rateCounter +next99.size()+2) > 99 ) {

					try {
						Thread.sleep(60000);
						// Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					rateCounter=0;
					}
				}

			} else {
				log.warn("{} is all visited", currentName);
				database.new WriterBuilder().table(table).row(currentName)
						.column("distancematrix").value("").go();
			}

			updateNextDistanceMatrix(table);
			return currentName;
			// }
		}
		return "All distancematrix up to data";
	}

	private int findDrivingDistance(String table, String start, String end) {
		String startAddress = (database.new ReaderBuilder().table(table)
				.row(start).column("Family Address").go()).get(0).get(4);
		String endAddress = (database.new ReaderBuilder().table(table).row(end)
				.column("Family Address").go()).get(0).get(4);

		log.warn("Drive from {} to {}", startAddress, endAddress);

		return coder.addressMatix(startAddress, endAddress);
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
