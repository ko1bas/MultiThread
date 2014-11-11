package org.kolbas.modules.fitst;

import java.util.ArrayList;

import org.kolbas.common.classes.StringSplitter;
import org.kolbas.common.interfaces.StringConvertable;

public class FirstModule implements StringConvertable {

	private ArrayList<String> list;

	public FirstModule() {
		list = new ArrayList<String>();
	}

	@Override
	public String convert(String input) {

		list.clear();
		StringSplitter splitter = new StringSplitter(input);
		StringBuffer buf = new StringBuffer(input.length());
		boolean flag = true;
		while (splitter.hasMoreStrings()) {
			String str = splitter.getNextString();
			if (splitter.isDelemiters(str)) {
				buf.append(str);
			} else {
				if (flag) {
					buf.append(str);
				} else {
					list.add(str);
				}
				flag = !flag;
			}
		}
		return buf.toString();
	}

	@Override
	public ArrayList<String> getDeletedStrings(String input) {
		convert(input);
		return list;
	}
}
