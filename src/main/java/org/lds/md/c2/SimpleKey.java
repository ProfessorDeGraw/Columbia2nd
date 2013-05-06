package org.lds.md.c2;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleKey {
	Vector<String> values = new Vector<String>();

	public SimpleKey(String serial) {

		Pattern pattern = Pattern.compile("([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):([^:]{0,}?):");
		Matcher matcher = pattern.matcher(serial);
		
		if (matcher.matches()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				String match = matcher.group(i);
				values.add(match);
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

	public String getTime() {
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
