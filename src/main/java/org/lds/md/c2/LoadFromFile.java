package org.lds.md.c2;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component("LoadFromFile")
public class LoadFromFile implements BeanRequest, DisposableBean {

	private static final Logger log = LoggerFactory
			.getLogger(LoadFromFile.class);

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

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
	public void doWork() {
		log.trace("do work");
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}

}
