package live.lingting.tools.core.util;

import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class CharUtils {

	/**
	 * 是否为可见字符
	 * @param c c
	 * @return boolean
	 */
	public static boolean isVisible(char c) {
		return c >= 32 && c < 127;
	}

	public static boolean isLowerLetter(char c) {
		return c >= 'a' && c <= 'z';
	}

	public static boolean isUpperLetter(char c) {
		return c >= 'A' && c <= 'Z';
	}

	public static boolean isLetter(char c) {
		return isLowerLetter(c) || isUpperLetter(c);
	}

}
