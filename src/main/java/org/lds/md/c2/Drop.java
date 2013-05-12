package org.lds.md.c2;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component("Drop")
public class Drop implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(Drop.class);

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);
		
		if (parms.size() >= 3) {
			String table = parms.get(0);
			String key = parms.get(1);
			String value = parms.get(2);
			
			database.actionRemoveKey(table, key, value);
		} else {
			database.actionRemoveAllKeys();
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