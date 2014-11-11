package org.kolbas.modules.second;

import java.util.ArrayList;
import java.util.Collections;

import org.kolbas.common.classes.StringSplitter;
import org.kolbas.common.interfaces.StringConvertable;


public class SecondModule implements StringConvertable {

	@Override
	public String convert(String input) {
		StringSplitter splitter = new StringSplitter(input);
		StringBuffer buf = new StringBuffer(input.length());
		boolean flag = false;
		while (splitter.hasMoreStrings()) {
			String str = splitter.getNextString();
			if (splitter.isDelemiters(str)) {
				if (" ".equals(str)) {
					if (!flag)
						buf.append(str);
					flag = true;
				} else {
					buf.append(str);
					flag = false;
				}
			} else {
				buf.append(str);
				flag = false;
			}
		}
		return buf.toString();
	}


	@Override
	public ArrayList<String> getDeletedStrings(String input) {
		return (ArrayList<String>) Collections.EMPTY_LIST;
	}

}
