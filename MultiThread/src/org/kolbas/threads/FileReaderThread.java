/**
 * 
 */
package org.kolbas.threads;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.kolbas.files.FileLoader;

public class FileReaderThread extends Thread {

	private FileLoader loader;
	private BlockingQueue<String> queue;

	public FileReaderThread(FileLoader loader, BlockingQueue<String> queue) {

		this.loader = loader;
		this.queue = queue;
	}

	@Override
	public void run() {
		String buf = "";
		try {
			while (true) {
				buf = loader.next();
				if (buf == null)
					break;
				try {
					queue.put(buf);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} finally {
			try {
				loader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
