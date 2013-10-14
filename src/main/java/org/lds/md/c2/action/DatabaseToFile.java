package org.lds.md.c2.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.lds.md.c2.db.KeyValueDatabase;
import org.lds.md.c2.web.BeanRequest;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component("DatabaseToFile")
public class DatabaseToFile implements BeanRequest, DisposableBean {

	// private static final Logger log = LoggerFactory
	// .getLogger(DatabaseToFile.class);

	KeyValueDatabase database = null;
	String defaultSaveFile = "database_backup.html";
	String defaultSaveFileRoot = "/var/lib/openshift/513d28c14382ec80940000ac/app-root/data/";

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	public void setDefaultSaveFile(String fileName) {
		this.defaultSaveFile = fileName;
	}

	@Override
	public Object get(List<String> parms) {

		try {
			File file;
			if (parms.size() > 0) {
				file = new File(defaultSaveFileRoot + parms.get(0));
			} else {
				file = new File(defaultSaveFileRoot + defaultSaveFile);
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			StringBuilder dbMessage = new StringBuilder();
			
			List<List<String>> keys = database.new ReaderBuilder().go();
			
			for (List<String> key : keys) {
				for (String field : key ) {
					dbMessage.append( field + ":" );
				}
				dbMessage.append("<br>\n");
			}
			
			//database.actionReader();
			bw.write(dbMessage.toString());
			bw.close();

			return "Database saved to file";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Database NOT saved";
	}

	@Override
	public void destroy() throws Exception {
		database.closeDatabase();
	}
}
