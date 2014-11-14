/**
 * 
 */
package org.kolbas.threads;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.files.MapStorage;
import org.kolbas.files.Storageable;

public class QueueReaderThread extends Thread implements Callable<Boolean>{

	private BlockingQueue<String> queue;

	private StringConvertable plugin;

	private ConcurrentMap<String, Boolean> storage;

	private FileReaderThreadPool pool;

	private final int SLEEP_TIMES = 50;
	
	private boolean isClose;

	public QueueReaderThread(BlockingQueue<String> queue, Class<?> plugin,
			ConcurrentMap<String, Boolean> map, FileReaderThreadPool pool)
			throws InstantiationException, IllegalAccessException {

		this.queue = queue;
		this.plugin = (StringConvertable) plugin.newInstance();
		this.storage = map;
		this.pool = pool;
		this.isClose = false;
	}

	public void run() {
		isClose = false;
		while (true) {
			ArrayList<String> array;
			try {
				String res = queue.remove();
				array = plugin.getDeletedStrings(res);
				for (String str : array) {
						storage.putIfAbsent(str, true);
				}
			} catch (NoSuchElementException IOE) {
				if (!pool.isTerminated())
					try {
						this.sleep(SLEEP_TIMES);
						System.out.println("sleep");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					break;
			}
		}
		this.isClose = true;
	}


	@Override
	public Boolean call() throws Exception {
		return isClose;
	}
}
