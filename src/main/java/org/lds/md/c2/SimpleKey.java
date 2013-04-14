package org.lds.md.c2;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleKey {
	Vector<String> values = new Vector<String>();

	public SimpleKey(String serial) {

		Pattern pattern = Pattern.compile("(.*):*");
		Matcher matcher = pattern.matcher(serial);

		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				values.add(matcher.group(i));
			}
		}

	}

	public SimpleKey(String table, String row, String column) {
		values.add(table);
		values.add(row);
		values.add(column);
		values.add(String.valueOf(System.currentTimeMillis()));
	}

	public SimpleKey(String table, String row, String column, String time) {
		values.add(table);
		values.add(row);
		values.add(column);
		values.add(time);
	}
	
	public String getTable() {
		return values.get(0);
	}

	public String getRow() {
		return values.get(1);
	}

	public String getColumn() {
		return values.get(2);
	}

	public String getMyTime() {
		return values.get(3);
	}
	
	public String toSerial() {
		StringBuilder out = new StringBuilder();
		for ( String i : values) {
			out.append(i + ":");
		}
		return out.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		for ( String i : values) {
			out.append(i + ":");
		}
		return out.toString();
	}
}
