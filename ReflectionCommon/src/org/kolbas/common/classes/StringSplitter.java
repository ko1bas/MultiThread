package org.kolbas.common.classes;

import java.util.StringTokenizer;

public class StringSplitter {

	private final String szDelemiters = " :;\"(),+-=<>?!@#$%^&*~'./\\";

	private StringTokenizer st;

	public StringSplitter(String val) {
		st = new StringTokenizer(val, szDelemiters, true);
	}

	public boolean hasMoreStrings() {
		return st.hasMoreTokens();
	}

	public String getNextString() {
		return st.nextToken();
	}

	public boolean isDelemiters(String val) {
		return szDelemiters.contains(val);
	}

}
