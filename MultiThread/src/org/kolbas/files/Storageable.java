/**
 * 
 */
package org.kolbas.files;

import java.io.IOException;

/**
 * @author Колбсов П.А.
 *
 */
public interface  Storageable {
	
	public boolean put(String value);
	
	public boolean contains(String value);
	
	public void saveToFile(String fileName) throws IOException;

}
