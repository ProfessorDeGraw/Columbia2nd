package org.lds.md.c2.db;

import com.sleepycat.collections.TransactionWorker;

public class KeyValueRemover implements TransactionWorker {

	KeyValueDatabase database;

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

		public KeyValueRemover build() {
			return new KeyValueRemover(this);
		}
	}

	private KeyValueRemover(Builder builder) {
		database = builder.database;
		table = builder.table;
		row = builder.row;
		column = builder.column;
		value = builder.value;
		time = builder.time;
		data = builder.data;
	}

	@Override
	public void doWork() throws Exception {
		// TODO Auto-generated method stub
		if (table == null && row == null && column == null && value == null
				&& time == null && data == null) {
			database.removeAllKeys();
		} else if (row == null && column == null) {
			database.removeKey(table);
		} else {
			database.removeKey(table, row, column);
		}
	}

}
