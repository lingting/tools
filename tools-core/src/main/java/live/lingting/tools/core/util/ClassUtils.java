package live.lingting.tools.core.util;

import live.lingting.tools.core.constant.StringConstants;
import live.lingting.tools.core.domain.ClassField;
import live.lingting.tools.core.lambda.LambdaException;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author lingting 2021/2/25 21:17
 */
@UtilityClass
@SuppressWarnings("java:S3011")
public class ClassUtils {

	private static final Map<String, Boolean> CACHE = new ConcurrentHashMap<>(8);

	private static final Map<Class<?>, Field[]> CACHE_FIELDS = new ConcurrentHashMap<>(16);

	private static final Map<Class<?>, ClassField[]> CACHE_CLASS_FIELDS = new ConcurrentHashMap<>(16);

	/**
	 * 确定class是否可以被加载
	 * @param className 完整类名
	 * @param classLoader 类加载
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
	@SuppressWarnings("java:S3776")
	public static <T> Set<Class<T>> scan(String basePack, Predicate<Class<T>> filter,
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
			if (connection instanceof JarURLConnection jarURLConnection) {
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

				if (filter.test(aClass)) {
					classes.add(aClass);
				}
			}
			catch (Exception e) {
				error.accept(className, e);
			}
		}
		return classes;
	}

	/**
	 * 把指定对象的所有字段和对应的值组成Map
	 * @param o 需要转化的对象
	 * @return java.util.Map<java.lang.String,java.lang.Object>
	 */
	public static Map<String, Object> toMap(Object o) {
		return toMap(o, field -> true, Field::getName, (field, v) -> v);
	}

	/**
	 * 把指定对象的所有字段和对应的值组成Map
	 * @param o 需要转化的对象
	 * @param filter 过滤不存入Map的字段, 返回false表示不存入Map
	 * @param toKey 设置存入Map的key
	 * @param toVal 自定义指定字段值的存入Map的数据
	 * @return java.util.Map<java.lang.String,java.lang.Object>
	 */
	public static <T> Map<String, T> toMap(Object o, Predicate<Field> filter, Function<Field, String> toKey,
			BiFunction<Field, Object, T> toVal) {
		if (o == null) {
			return Collections.emptyMap();
		}
		HashMap<String, T> map = new HashMap<>();
		for (Field field : fields(o.getClass())) {
			if (filter.test(field)) {
				Object val = null;

				try {
					val = field.get(o);
				}
				catch (IllegalAccessException e) {
					//
				}

				map.put(toKey.apply(field), toVal.apply(field, val));
			}
		}
		return map;
	}

	/**
	 * 获取所有字段, 并且设置为可读
	 * <p>
	 * 由于内部调用的了 {@link Field#setAccessible(boolean)}, 高版本使用会有问题
	 * </p>
	 * @param cls 指定类
	 * @return java.lang.reflect.Field[]
	 */
	public static Field[] fields(Class<?> cls) {
		return CACHE_FIELDS.computeIfAbsent(cls, k -> {
			List<Field> fields = new ArrayList<>();
			while (k != null && !k.isAssignableFrom(Object.class)) {
				for (Field field : k.getDeclaredFields()) {
					field.setAccessible(true);
					fields.add(field);
				}
				k = k.getSuperclass();
			}
			return fields.toArray(new Field[0]);
		});
	}

	/**
	 * 扫描所有字段以及对应字段的值
	 * <p>
	 * 优先取指定字段的 get 方法, 仅会获取其中的无参方法
	 * </p>
	 * <p>
	 * 如果是 boolean 类型, 尝试取 is 方法
	 * </p>
	 * <p>
	 * 否则直接取字段 - 不会尝试修改可读性, 如果可读性有问题, 请主动get 然后修改
	 * </p>
	 * @return live.lingting.tools.core.domain.ClassField 可用于获取字段值的数组
	 */
	public static ClassField[] classFields(Class<?> cls) {
		return CACHE_CLASS_FIELDS.computeIfAbsent(cls, k -> {
			Method[] methods = cls.getDeclaredMethods();

			List<ClassField> fields = new ArrayList<>();
			while (k != null && !k.isAssignableFrom(Object.class)) {
				for (Field field : k.getDeclaredFields()) {
					String fieldName = StringUtils.firstUpper(field.getName());
					// 尝试获取get方法
					String getMethodName = StringConstants.GET + fieldName;

					Optional<Method> optional = Arrays.stream(methods)
							.filter(method -> method.getName().equals(getMethodName) && method.getParameterCount() == 0)
							.findFirst();

					// get 不存在则尝试获取 is 方法
					if (optional.isEmpty()) {
						String isMethodName = StringConstants.IS + fieldName;
						optional = Arrays.stream(methods).filter(
								method -> method.getName().equals(isMethodName) && method.getParameterCount() == 0)
								.findFirst();
					}

					fields.add(new ClassField(field, optional.orElse(null)));
				}
				k = k.getSuperclass();
			}
			return fields.toArray(new ClassField[0]);
		});
	}

	/**
	 * 获取指定类中的指定字段名的字段
	 * @param fieldName 字段名
	 * @param cls 指定类
	 * @return live.lingting.tools.core.domain.ClassField 字段
	 */
	public static ClassField classField(String fieldName, Class<?> cls) {
		for (ClassField field : classFields(cls)) {
			if (field.getFiledName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, ClassUtils.class.getClassLoader());
	}

	public static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		return loadClass(className, classLoader, ClassLoader.getSystemClassLoader(), ClassUtils.class.getClassLoader(),
				Thread.currentThread().getContextClassLoader());
	}

	public static Class<?> loadClass(String className, ClassLoader... classLoaders) throws ClassNotFoundException {
		for (ClassLoader loader : classLoaders) {
			if (loader != null) {
				try {
					return loader.loadClass(className);
				}
				catch (ClassNotFoundException e) {
					//
				}
			}
		}
		throw new ClassNotFoundException(className + " not found");
	}

	/**
	 * 设置可访问对象的可访问权限为 true
	 * @param object 可访问的对象
	 * @param <T> 类型
	 * @return 返回设置后的对象
	 */
	public static <T extends AccessibleObject> T setAccessible(T object) {
		object.setAccessible(true);
		return object;
	}

	/**
	 * 方法名转字段名
	 * @param methodName 方法名
	 * @return java.lang.String 字段名
	 */
	public static String toFiledName(String methodName) {
		if (methodName.startsWith(StringConstants.IS)) {
			methodName = methodName.substring(2);
		}
		else if (methodName.startsWith(StringConstants.GET) || methodName.startsWith(StringConstants.SET)) {
			methodName = methodName.substring(3);
		}
		else {
			throw new LambdaException(
					"Error parsing property name '" + methodName + "'.  Didn't start with 'is', 'get' or 'set'.");
		}

		if (methodName.length() == 1 || (methodName.length() > 1 && !Character.isUpperCase(methodName.charAt(1)))) {
			methodName = methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
		}

		return methodName;
	}

}
