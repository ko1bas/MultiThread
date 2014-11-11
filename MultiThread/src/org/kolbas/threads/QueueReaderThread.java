/**
 * 
 */
package org.kolbas.threads;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import javax.swing.plaf.SliderUI;

import org.kolbas.common.interfaces.StringConvertable;

/**
 * @author Колбсов П.А.
 *
 */
public class QueueReaderThread implements Runnable {

	private BlockingQueue<String> query;

	private StringConvertable currClass;

	private ConcurrentMap<String, Boolean> map;

	public QueueReaderThread(BlockingQueue<String> query, Class<?> module,
			ConcurrentMap<String, Boolean> map) throws InstantiationException,
			IllegalAccessException {

		this.query = query;
		this.currClass = (StringConvertable) module.newInstance();
		this.map = map;
	}

	@Override
	public void run() {
		ArrayList<String> array = currClass.getDeletedStrings(query.poll());
		for (String string : array) {
			map.putIfAbsent(string, true);
		}
	}

}
