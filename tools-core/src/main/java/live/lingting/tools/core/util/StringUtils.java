package live.lingting.tools.core.util;

import live.lingting.tools.core.constant.StringConstants;

import java.io.CharArrayWriter;
import java.util.Iterator;

/**
 * @author lingting
 */
public class StringUtils {

	/**
	 * 指定字符串是否存在可见字符
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean hasText(String str) {
		if (str == null || str.length() < 1) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			// 如果是非空白字符
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 指定字符串是否为 json 字符串
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean isJson(String str) {
		if (hasText(str)) {
			if (str.startsWith(StringConstants.CURLY_BRACES_LEFT)) {
				return str.endsWith(StringConstants.CURLY_BRACES_RIGHT);
			}
			else if (str.startsWith(StringConstants.BRACKETS_LEFT)) {
				return str.endsWith(StringConstants.BRACKETS_RIGHT);
			}
		}

		return false;
	}

	/**
	 * 指定字符串是否为 xml 字符串
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean isXml(String str) {
		if (hasText(str)) {
			if (str.startsWith(StringConstants.ANGLE_BRACKETS_LEFT)) {
				return str.endsWith(StringConstants.ANGLE_BRACKETS_RIGHT);
			}
		}

		return false;
	}

	public static String join(Iterable<?> iterable, String delimiter) {
		if (iterable == null) {
			return null;
		}

		return join(iterable.iterator(), delimiter);
	}

	public static String join(Iterator<?> iterator, String delimiter) {
		if (iterator == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			final Object next = iterator.next();
			if (next == null) {
				continue;
			}
			builder.append(next);

			if (iterator.hasNext()) {
				builder.append(delimiter);
			}
		}

		return builder.toString();

	}

	public static String firstLower(String str) {
		if (!hasText(str)) {
			return str;
		}

		final char c = str.charAt(0);
		if (CharUtils.isUpperLetter(c)) {
			return Character.toLowerCase(c) + str.substring(1);
		}
		return str;
	}

	public static String firstUpper(String str) {
		if (!hasText(str)) {
			return str;
		}

		final char c = str.charAt(0);
		if (CharUtils.isLowerLetter(c)) {
			return Character.toUpperCase(c) + str.substring(1);
		}
		return str;
	}

	/**
	 * 驼峰字符串转下划线字符串
	 * <p>
	 * eg: HumpToUnderscore -> hump_to_underscore
	 * </p>
	 */
	public static String humpToUnderscore(String str) {
		CharArrayWriter writer = new CharArrayWriter();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 大写字母处理
			if (CharUtils.isUpperLetter(c)) {
				// 如果不是第一个大写字母, 插入下划线 _
				if (writer.size() > 0) {
					writer.append('_');
				}
				// 转小写
				writer.append(Character.toLowerCase(c));
			}
			// 不是则直接写入
			else {
				writer.append(c);
			}
		}

		return writer.toString();
	}

}
