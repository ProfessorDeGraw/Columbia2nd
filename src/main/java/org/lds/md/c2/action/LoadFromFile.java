package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("LoadFromFile")
public class LoadFromFile implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory
			.getLogger(LoadFromFile.class);

	@Autowired
	KeyValueDatabase database;

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() > 0) {
			String fileName = parms.get(0);

			log.trace("Getting data from " + fileName);
			KeyValueDatabase.loadMemberDataFileSimple(database,
					"/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/"
							+ fileName);
			log.trace("Finished with data from " + fileName);

		}

		return "LoadedFileData";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
