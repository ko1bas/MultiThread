/**
 * 
 */
package org.kolbas.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.files.Storageable;

import sun.nio.cs.ext.ISCII91;

/**
 * @author Колбсов П.А.
 *
 */
public class QueueReaderThreadPool extends Thread {
	private int countThread;
	private ExecutorService executor;
	private BlockingQueue<String> queue;
	private boolean isClose;
	private FileReaderThreadPool pool;
	private ConcurrentMap<String, Boolean> storage;
	private Class<?> plugin;

	public QueueReaderThreadPool(int countThread, BlockingQueue<String> queue,
			ConcurrentMap<String, Boolean> map, FileReaderThreadPool pool,
			Class<?> plugin) {
		this.queue = queue;
		this.countThread = countThread;
		this.executor = Executors.newFixedThreadPool(this.countThread);
		this.isClose = false;
		this.pool = pool;
		this.storage = map;
		this.plugin = plugin;
	}

	@Override
	public void run() {
		isClose = false;
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (int i = 0; i < countThread; i++) {
			try {
				tasks.add(Executors.callable(new QueueReaderThread(queue,
						plugin, storage, pool)));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
			isClose = true;
		}
	}
	
	public boolean isTerminated()
	{
		return isClose;
	}
}
