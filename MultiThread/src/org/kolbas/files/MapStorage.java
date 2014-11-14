/**
 * 
 */
package org.kolbas.files;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Колбсов П.А.
 *
 */
public class MapStorage implements Storageable {

	private ConcurrentMap<String, Boolean> map;

	public MapStorage() {
		map = new ConcurrentHashMap<String, Boolean>();
	}

	@Override
	public synchronized boolean  put(String value) {
		{
			return map.putIfAbsent(value, true);
		}
	}

	@Override
	public synchronized boolean contains(String value) {
		return map.containsKey(value);

	}

	@Override
	public synchronized void saveToFile(String fileName) throws IOException {
		{
			FileSaver saver = new FileSaver(fileName);
			for (String str : map.keySet()) {
				saver.write(str);
			}
			saver.close();
		}
	}
}
