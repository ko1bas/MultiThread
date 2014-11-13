package org.kolbas.files;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Загружает файлы из заданного jar-архива. Классы должны относится к заданному
 * пакету.
 */

public class JarLoader extends ClassLoader {

	private static String WARNING = "Warning : No jar file found. Packet unmarshalling won't be possible. Please verify your classpath";
	
	private HashMap<String, Class<?>> cache;
	private String jarFileName;
	private String packageName;
	private boolean seeHints;
	

	/**
	 * 
	 * Извлекает все классы из jar и кэширует в map
	 * 
	 * @param jarFileName
	 *            путь к файлу jar-архива.
	 * 
	 * @param packageName
	 *            пакет из которого будут извлекаться классы (если нужны все классы - то "").
	 *            
	 * @param seeHints
	 *            показывать отладочную информацию.
	 *
	 *
	 */
	public JarLoader(String jarFileName, String packageName, boolean seeHints) {

		this.cache = new HashMap<String, Class<?>>();
		this.jarFileName = jarFileName;
		this.packageName = packageName;
		this.seeHints = seeHints;
		this.cacheClasses();
	}
	
	/**
	 * 
	 * Извлекает все классы из jar и кэширует в map.
	 * 
	 * @param jarFileName
	 *            путь к файлу jar-архива.
	 *        
	 * @param seeHints
	 *            показывать отладочную информацию.
	 *
	 *
	 */
	public JarLoader(String jarFileName, boolean seeHints) {

		this.cache = new HashMap<String, Class<?>>();
		this.jarFileName = jarFileName;
		this.packageName = "";
		this.seeHints = seeHints;
		this.cacheClasses();
	}
	

	/**
	 * 
	 * Извлекает все классы из jar и кэширует в map.
	 *
	 */
	private void cacheClasses() {
		try {
			JarFile jarFile = new JarFile(jarFileName);
			Enumeration entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) entries.nextElement();

				if (match(normalize(jarEntry.getName()), packageName)) {
					byte[] classData = loadClassData(jarFile, jarEntry);
					if (classData != null) {
						Class<?> clazz = defineClass(
								stripClassName(normalize(jarEntry.getName())),
								classData, 0, classData.length);
						cache.put(clazz.getName(), clazz);
						if (seeHints)
							System.out.println("== class " + clazz.getName()
									+ " loaded in cache");
					}
				}
			}
		}
		catch (IOException IOE) {
			System.out.println(WARNING);
		} 
	}

	
	/**
	 * 
	 * Получает набор имен загруженных классов.
	 * 
	 */
	public Set<String> getCachedClasses() {
		return cache.keySet();
	}
	
	
	/**
	 * 
	 * Получает ArrayList классов, реализующих данный интерфейс.
	 * 
	 * @param interfaceName
	 *
	 * @return пустой List, если таких классов нет
	 * 
	 */
	public List<Class<?>> getClassesImplementsInterface (String interfaceName) throws ClassNotFoundException {
		List<Class<?>> result = new ArrayList<Class<?>>();
		
		if ("".equals(interfaceName)||interfaceName.equals(null))
			return result;
		
		Class<?> currClass =null;
		for (String className : cache.keySet()) {
			currClass = this.loadClass(className);
			Class[] interfaces = currClass.getInterfaces();
			for (Class cInterface : interfaces) {
				if (interfaceName.equals(cInterface.getName())) {
					result.add(currClass);
					break;
				}
			} // for
		} // for
		return result;
	}
	
	

	/**
	 * 
	 * Реализует загрузку класса.
	 * 
	 * @param name
	 *
	 */
	public synchronized Class<?> loadClass(String name)
			throws ClassNotFoundException {
		Class<?> result = cache.get(name);
		// Возможно класс вызывается не по полному имени - добавим имя пакета
		if (result == null)
			result = cache.get(packageName + "." + name);

		// Если класса нет в кэше то возможно он системный
		if (result == null)
			result = super.findSystemClass(name);
		if (seeHints)
			System.out.println("== loadClass(" + name + ")");
		return result;
	}

	/**
	 * 
	 * Получает каноническое имя класса.
	 * 
	 * @param className
	 * 
	 * @return
	 */
	private String stripClassName(String className) {
		return className.substring(0, className.length() - 6);

	}

	/**
	 * 
	 * Преобразует имя в файловой системе в имя класса (заменяет слэши на точки).
	 * 
	 * @param className
	 * 
	 * @return
	 */

	private String normalize(String className) {
		return className.replace('/', '.');
	}

	/**
	 * 
	 * Проверят принадлежит ли класс заданному пакету и имеет
	 * ли он расширение .class.
	 * 
	 * @param className
	 * 
	 * @param packageName
	 * 
	 * @return
	 */

	private boolean match(String className, String packageName) {
		return className.startsWith(packageName)
				&& className.endsWith(".class");
	}

	/**
	 * 
	 * Извлекает файл из заданного JarEntry.
	 * 
	 *
	 * 
	 * @param jarFile
	 *            файл jar-архива из которого извлекается нужный файл
	 * 
	 * @param jarEntry
	 *            jar-сущность которая извлекается
	 * 
	 * @return null если невозможно прочесть файл
	 */

	private byte[] loadClassData(JarFile jarFile, JarEntry jarEntry)
			throws IOException {
		long size = jarEntry.getSize();
		if (size == -1 || size == 0)
			return null;

		byte[] data = new byte[(int) size];
		InputStream in = jarFile.getInputStream(jarEntry);
		in.read(data);
		return data;
	}

}