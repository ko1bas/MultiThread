/**
 * 
 */
package org.kolbas.threads;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.kolbas.common.interfaces.StringConvertable;


public class QueueReaderThread extends Thread {

	private BlockingQueue<String> queue;

	private StringConvertable plugin;

	private ConcurrentMap<String, Boolean> map;


	public QueueReaderThread(BlockingQueue<String> queue, Class<?> plugin,
			ConcurrentMap<String, Boolean> map) throws InstantiationException,
			IllegalAccessException {

		this.queue = queue;
		this.plugin = (StringConvertable) plugin.newInstance();
		this.map = map;
	}

	public void run() {
		
		while (true) {
			ArrayList<String> array;
			try {
				String res = queue.remove();
				array = plugin.getDeletedStrings(res);
				for (String string : array) {
					map.putIfAbsent(string, true);
				}
			} catch (NoSuchElementException IOE) {
				break;
			}
		}
	}

}
