package org.lds.md.c2;

import java.util.List;

public class HelloDatabase implements BeanRequest {
	
	KeyValueDatabase database = null;
	
	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		database.actionWriter();
		database.actionReader();

		return database.getDbMessage();
	}

}
