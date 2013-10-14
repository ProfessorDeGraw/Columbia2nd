package org.lds.md.c2.db;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.collections.TransactionWorker;

public class KeyValueReader implements TransactionWorker {
	private static final Logger log = LoggerFactory
			.getLogger(KeyValueReader.class);

	KeyValueDatabase database;
	List<List<String>> keys;
	String table, row, column, value, time, data;

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
		value = builder.value;
		time = builder.time;
		data = builder.data;
	}

	public List<List<String>> getAllKeys() {
		return keys;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		if (table == null && row == null && column == null && value == null
				&& time == null && data == null) {
			keys = database.readAllKeys();
		} else if (row == null && value == null) {
			keys = database.allKeys(table, column);
		} else if (value == null) {
			keys = database.allKeys(table, row, column);
		} else if (row == null) {
			keys = database.allKeysByValue(table, column, value);
		} else {
			log.warn("KeyValueDatabaseAllKeysReader for all values not implemented");
		}
	}
}
