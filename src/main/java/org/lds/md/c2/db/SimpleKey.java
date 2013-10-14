package org.lds.md.c2.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKey {
	private static final Logger log = LoggerFactory.getLogger(SimpleKey.class);

	List<String> values = new ArrayList<String>();

	public static class Builder {
		// optional
		private String table = "", row = "", column = "", value = "",
				time = "";

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

		public Builder serial(String value) {
			List<String> matches = new ArrayList<String>();

			Pattern pattern = Pattern
					.compile("([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):");
			Matcher matcher = pattern.matcher(value);

			if (matcher.matches()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String match = matcher.group(i);
					matches.add(match);
				}
			}
			table = matches.get(0);
			row = matches.get(1);
			column = matches.get(2);
			this.value = matches.get(3);
			time = matches.get(4);
			return this;
		}

		public SimpleKey build() {
			if (time.equals("")) {
				time = String.valueOf(System.currentTimeMillis());
			}
			return new SimpleKey(this);
		}
	}

	private SimpleKey(Builder builder) {
		values.add(builder.table);
		values.add(builder.row);
		values.add(builder.column);
		values.add(builder.value);
		values.add(builder.time);
	}

	/*
	 * public SimpleKey(String serial) {
	 * 
	 * Pattern pattern =
	 * Pattern.compile("([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):");
	 * Matcher matcher = pattern.matcher(serial);
	 * 
	 * if (matcher.matches()) { for (int i = 1; i <= matcher.groupCount(); i++)
	 * { String match = matcher.group(i); values.add(match); } }
	 * 
	 * }
	 * 
	 * public SimpleKey(String table, String row, String column) {
	 * values.add(table); values.add(row); values.add(column);
	 * values.add(String.valueOf(System.currentTimeMillis())); }
	 * 
	 * public SimpleKey(String table, String row, String column, String time) {
	 * values.add(table); values.add(row); values.add(column); values.add(time);
	 * }
	 */

	public String getTable() {
		return values.get(0);
	}

	public String getRow() {
		return values.get(1);
	}

	public String getColumn() {
		return values.get(2);
	}

	public String getValue() {
		return values.get(3);
	}

	public String getTime() {
		return values.get(4);
	}

	public String toSerial() {
		StringBuilder out = new StringBuilder();

		if (values.size() != 5) {
			log.warn("Key Serial was not the expected size");
		}

		for (String i : values) {
			out.append(i + ":");
		}
		return out.toString();
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		for (String i : values) {
			out.append(i + ":");
		}
		return out.toString();
	}
}
