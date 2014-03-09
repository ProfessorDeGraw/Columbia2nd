package org.lds.md.c2.db;

import java.util.List;

import com.sleepycat.collections.TransactionWorker;

public class KeyValueReader implements TransactionWorker {
	//private static final Logger log = LoggerFactory
	//		.getLogger(KeyValueReader.class);

	KeyValueDatabase database;
	List<List<String>> keys;
	String table, row, column, time, value, data;

	public static class Builder {
		final KeyValueDatabase database;

		String table = null, row = null, column = null, value = null,
				time = null, data = null;

		public Builder(KeyValueDatabase database) {
			this.database = database;
		}

		public Builder table(String value) {
			table = value;
			return this;
		}

		public Builder row(String value) {
			row = value;
			return this;
		}

		public Builder column(String value) {
			column = value;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder time(String value) {
			time = value;
			return this;
		}

		public Builder data(String value) {
			data = value;
			return this;
		}

		public KeyValueReader build() {
			return new KeyValueReader(this);
		}
	}

	private KeyValueReader(Builder builder) {
		database = builder.database;
		table = builder.table;
		row = builder.row;
		column = builder.column;
		time = builder.time;
		value = builder.value;
		data = builder.data;
	}

	public List<List<String>> getAllKeys() {
		return keys;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		keys = database.getKeys(table, row, column, time, value, data);
	}
}
