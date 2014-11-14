/**
 * 
 */
package org.kolbas.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.kolbas.files.FileLoader;

public class FileReaderThread extends Thread  implements Callable<Boolean>{

	private FileLoader loader;
	private BlockingQueue<String> queue;
	private boolean isClose;
	

	public FileReaderThread(FileLoader loader, BlockingQueue<String> queue ){

		this.loader = loader;
		this.queue = queue;
		this.isClose =false;
	}
	
	public FileReaderThread(String fileName, BlockingQueue<String> queue) throws FileNotFoundException, IOException {

		this.loader = new FileLoader(fileName);
		this.queue = queue;
		this.isClose =false;
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
			isClose =true;
			try {
				loader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Boolean call() throws Exception {
		
		return isClose;
	}

}
