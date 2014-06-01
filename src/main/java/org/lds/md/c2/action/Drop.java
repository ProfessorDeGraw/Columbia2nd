package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Drop")
public class Drop implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory.getLogger(Drop.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 3) {
			String table = parms.get(0);
			String key = parms.get(1);
			String value = parms.get(2);
			
			database.new RemoverBuilder().table(table).row(key).value(value).go();
		}
		if (parms.size() >= 1) {
			String table = parms.get(0);

			database.new RemoverBuilder().table(table).go();
		} else {
			database.new RemoverBuilder().go();
		}
		return "Data";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
