package org.lds.md.c2;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;

public class HelloDatabase implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory
	// .getLogger(HelloDatabase.class);

	// @Value("#{mail.database}")
	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public Object get(List<String> parms) {
		// database.initDB();
		// database.actionWriterInitDB();
		database.actionReader();
		return database.getDbMessage();
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}
}
