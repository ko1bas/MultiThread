package org.kolbas.common.interfaces;

import java.util.ArrayList;

public interface StringConvertable {

	public String convert(String input);
	
	public ArrayList<String> getDeletedStrings(String input);
 
}
