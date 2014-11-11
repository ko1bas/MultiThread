/**
 * 
 */
package org.kolbas.threads;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.files.FileLoader;

/**
 * @author Колбсов П.А.
 *
 */
public class FileReaderThread extends Thread {

	private FileLoader loader;
	private BlockingQueue<String> query;

	public FileReaderThread(FileLoader loader, BlockingQueue<String> query) {

		this.loader = loader;
		this.query = query;
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
					query.put(buf);
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
