package org.lds.md.c2;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component("Load")
public class Load implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(Load.class);

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);
		
		if (parms.size() >= 2) {
			String table = parms.get(0);
			String file = parms.get(1);
			
			KeyValueDatabase.loadMemberDataFile(database, table, "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/" +
					file);
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
