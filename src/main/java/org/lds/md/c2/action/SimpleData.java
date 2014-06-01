package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SimpleData")
public class SimpleData implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory.getLogger(Data.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 2) {
			String table = parms.get(0);
			String column = parms.get(1);

			List<List<String>> data = database.new ReaderBuilder().table(table)
					.column(column).go();
			StringBuilder sb = new StringBuilder();
			for (List<String> i : data) {
				sb.append(i.get(4).toString() + "<br>");
			}
			return sb;
		}
		return "Data";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
