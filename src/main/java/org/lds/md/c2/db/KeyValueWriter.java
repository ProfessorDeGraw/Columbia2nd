package org.lds.md.c2.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.collections.TransactionWorker;

public class KeyValueWriter implements TransactionWorker {
	private static final Logger log = LoggerFactory
			.getLogger(KeyValueWriter.class);

	KeyValueDatabase database;

	String table, row, column, value, time, data;

	public static class Builder {
		final KeyValueDatabase database;

		String table = null, row = null, column = null, value = null,
				time = "", data = "";

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

		public KeyValueWriter build() {
			return new KeyValueWriter(this);
		}
	}

	private KeyValueWriter(Builder builder) {
		database = builder.database;
		table = builder.table;
		row = builder.row;
		column = builder.column;
		value = builder.value;
		time = builder.time;
		data = builder.data;
	}

	@Override
	public void doWork() {
		// TODO Auto-generated method stub
		try {
			database.writeKey(table, row, column, time, value, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("Database write failed");
			e.printStackTrace();
		}
	}

}
