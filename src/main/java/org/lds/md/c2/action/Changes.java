package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Changes")
public class Changes implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory.getLogger(Changes.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		StringBuilder sb = new StringBuilder();

		if (parms.size() >= 2) {
			String table1 = parms.get(0);
			String table2 = parms.get(1);

			List<String> changes = KeyValueDatabase.detectChanges(database,
					table1, table2);

			for (String i : changes) {
				sb.append(i + "<br>");
			}
		}
		return sb;
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
