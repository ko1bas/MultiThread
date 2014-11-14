package org.kolbas.multithread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.kolbas.common.interfaces.StringConvertable;
import org.kolbas.files.FileLoader;
import org.kolbas.files.JarLoader;
import org.kolbas.files.FileSaver;
import org.kolbas.files.MapStorage;
import org.kolbas.files.Storageable;
import org.kolbas.threads.FileReaderThread;
import org.kolbas.threads.FileReaderThreadPool;
import org.kolbas.threads.QueueReaderThread;
import org.kolbas.threads.QueueReaderThreadPool;

public class Main {

	private static ResourceBundle resource;

	private static final void p(String s) {
		System.out.println(s);
	}

	private static void printHelpStr() {

		p(resource.getString("HelpStr"));
		p(resource.getString("HelpStrDescription"));
	}

	private static final boolean isArgsValid(String[] args) {

		boolean res = false;
		switch (args.length) {
		case 0:
			printHelpStr();
			break;
		case 1:
			if (args[0].equalsIgnoreCase("-help")
					|| args[0].equalsIgnoreCase("/?"))
				printHelpStr();
			else {
				p(resource.getString("ErrCommandFailure"));
				printHelpStr();
			}
			break;
		default:
			// MultiThread <jar-file> <txt-file1> [<txt-file2> ...]
			res = true;
			break;
		}
		return res;
	}

	public static void OneThread(Class<?> plugin, String outFileName,
			String[] args) throws FileNotFoundException, IOException,
			InstantiationException, IllegalAccessException {

		long time = System.currentTimeMillis();
		int start = 1;
		FileLoader loader = new FileLoader(args, start);

		String buf = "";
		StringConvertable module = (StringConvertable) plugin.newInstance();

		int setCapacity = 10000;
		Set<String> set = new HashSet<String>(setCapacity);

		while (true) {
			buf = loader.next();
			if (buf == null)
				break;
			for (String str : module.getDeletedStrings(buf)) {
				set.add(str);
			}
		}
		loader.close();

		FileSaver saver = new FileSaver(outFileName);
		for (String key : set) {
			saver.write(key);
		}
		saver.close();

		System.out.println("One Thread.  Time: "
				+ (System.currentTimeMillis() - time));
	}


	public static void MultiThread2(Class<?> plugin, String outFileName,
			String[] args) throws FileNotFoundException, IOException,
			InstantiationException, IllegalAccessException,
			InterruptedException {

		long time = System.currentTimeMillis();

		int queueCapasity = 100000;
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		
		// MapStorage storage = new MapStorage();
		int mapCapsity=10000;
		ConcurrentMap<String, Boolean> map = new ConcurrentHashMap<String, Boolean>(mapCapsity);

		int startArgs = 1;
		int countThread = 4;
		FileReaderThreadPool poolFileReaders = new FileReaderThreadPool(args,
				startArgs, countThread, queue);
		poolFileReaders.start();

		int processorCount = Runtime.getRuntime().availableProcessors();
		QueueReaderThreadPool poolQueueReaders = new QueueReaderThreadPool(
				processorCount, queue, map, poolFileReaders, plugin);
		poolQueueReaders.start();

		
		poolFileReaders.join();
		poolQueueReaders.join();

		FileSaver saver = new FileSaver(outFileName);
		for (String key : map.keySet()) {
			saver.write(key);
		}
		saver.close();
		// storage.saveToFile(outFileName);

		System.out.println("Available processors: " + processorCount);
		System.out.println("MultiThread. Time: "
				+ (System.currentTimeMillis() - time));
	}

	public static void main(String[] args) throws ClassNotFoundException,
			FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException, InterruptedException {

		resource = ResourceBundle.getBundle("data_en_EN");
		final String MODULE_INTERFACE = "org.kolbas.common.interfaces.StringConvertable";

		if (!isArgsValid(args))
			return;

		boolean seeHints = false;
		JarLoader jarClassLoader = new JarLoader(args[0], seeHints);

		List<Class<?>> classes = jarClassLoader
				.getClassesImplementsInterface(MODULE_INTERFACE);
		if (classes.isEmpty()) {
			p(resource.getString("FileNotContainInterface")
					.replace("<file>", args[0])
					.replace("<interface>", MODULE_INTERFACE));
			return;
		}
		Class<?> plugin = classes.get(0);

		OneThread(plugin, "D:\\output1.txt", args);

		MultiThread2(plugin, "D:\\output2.txt", args);

	} // main

}
