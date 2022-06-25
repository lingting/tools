package live.lingting.tools.core.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author lingting 2021/2/25 21:17
 */
public class ClassUtils {

	private static final Map<String, Boolean> CACHE = new ConcurrentHashMap<>(8);

	/**
	 * 确定class是否可以被加载
	 * @param className 完整类名
	 * @param classLoader 类加载
	 * @author lingting 2021-02-25 21:17
	 */
	public static boolean isPresent(String className, ClassLoader classLoader) {
		if (CACHE.containsKey(className)) {
			return CACHE.get(className);
		}
		try {
			Class.forName(className, true, classLoader);
			CACHE.put(className, true);
			return true;
		}
		catch (Exception ex) {
			CACHE.put(className, false);
			return false;
		}
	}

	public static <T> Set<Class<T>> scan(String basePack, Class<?> cls) throws IOException {
		return scan(basePack, tClass -> cls == null || cls.isAssignableFrom(tClass), (s, e) -> {
		});
	}

	/**
	 * 扫描指定包下, 所有继承指定类的class
	 * @param basePack 指定包 eg: live.lingting.wirelesstools.item
	 * @param filter 过滤指定类
	 * @param error 获取类时发生异常处理
	 * @return java.util.Set<java.lang.Class < T>>
	 */
	public static <T> Set<Class<T>> scan(String basePack, Function<Class<T>, Boolean> filter,
			BiConsumer<String, Exception> error) throws IOException {
		List<String> classNames = new ArrayList<>();
		String clsPath = basePack.replace(".", "/");
		URL url = Thread.currentThread().getContextClassLoader().getResource(clsPath);
		if (url == null) {
			return new HashSet<>();
		}
		if ("file".equals(url.getProtocol())) {
			String path = url.getFile();
			for (String file : FileUtils.scanFile(path, true)) {
				if (file.endsWith(".class")) {
					String className = basePack + "."
							+ file.substring(path.length(), file.length() - 6).replace(File.separator, ".");

					classNames.add(className);
				}
			}
		}
		else {
			URLConnection connection = url.openConnection();
			if (connection instanceof JarURLConnection) {
				JarURLConnection jarURLConnection = (JarURLConnection) connection;
				JarFile jarFile = jarURLConnection.getJarFile();

				Enumeration<JarEntry> entries = jarFile.entries();

				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();

					if (entryName.endsWith(".class") && entryName.startsWith(clsPath)) {
						classNames.add(entryName.substring(0, entryName.length() - 6).replace("/", "."));
					}
				}

			}
		}

		Set<Class<T>> classes = new HashSet<>();
		for (String className : classNames) {

			try {
				Class<T> aClass = (Class<T>) Class.forName(className);

				if (filter.apply(aClass)) {
					classes.add(aClass);
				}
			}
			catch (Exception e) {
				error.accept(className, e);
			}
		}
		return classes;
	}

}
