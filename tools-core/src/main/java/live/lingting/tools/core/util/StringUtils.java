package live.lingting.tools.core.util;

import java.util.Iterator;
import live.lingting.tools.core.constant.StringConstants;

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
		return str != null && str.length() > 0 && str.replace("\\s", "").length() > 0;
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
			builder.append(next.toString());

			if (iterator.hasNext()) {
				builder.append(delimiter);
			}
		}

		return builder.toString();
	}

}