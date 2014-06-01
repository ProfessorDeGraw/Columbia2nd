package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("HelloDatabase")
public class HelloDatabase implements BeanRequest, DisposableBean {
	// @Value("#{mail.database}")
	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		StringBuilder dbMessage = new StringBuilder();
		
		List<List<String>> keys = database.new ReaderBuilder().go();
		//List<List<String>> keys = database.actionReader();
		
		for (List<String> key : keys) {
			for (String field : key ) {
				dbMessage.append( field + ":" );
			}
			dbMessage.append("<br>\n");
		}
		
		return dbMessage.toString();
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}
}
