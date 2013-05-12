package org.lds.md.c2;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component("Data")
public class Data implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(Data.class);

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);
		
		if (parms.size() >= 2) {
			String table = parms.get(0);
			String column = parms.get(1);
			List<List<String>> data = database.actionAllKeysByTableByColumn(
					table, column);
			StringBuilder sb = new StringBuilder();
			for (List<String> i : data) {
				if (column.equals("key")) {
					sb.append(i.get(1).toString() + "<br>");
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
	public void doWork() {
		log.trace("do work");
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
