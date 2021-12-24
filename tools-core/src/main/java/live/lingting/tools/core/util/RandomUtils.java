package live.lingting.tools.core.util;

import java.util.concurrent.ThreadLocalRandom;
import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class RandomUtils {

	/**
	 * 用于随机选的数字
	 */
	public static final String NUMBER = "0123456789";

	/**
	 * 用于随机选的字母
	 */
	public static final String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 用于随机选的字符
	 */
	public static final String STRING = NUMBER + LETTER;

	public static ThreadLocalRandom getRandom() {
		return ThreadLocalRandom.current();
	}

	/**
	 * 随机数
	 * @param max 最大值 - 不包括该值
	 * @return int
	 */
	public static int nextInt(int max) {
		return nextInt(0, max);
	}

	/**
	 * 随机数
	 * @param min 最小值 - 包括该值
	 * @param max 最大值 - 不包括该值
	 * @return int
	 */
	public static int nextInt(int min, int max) {
		return getRandom().nextInt(min, max);
	}

	public static String nextStr(int len) {
		StringBuilder builder = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			builder.append(STRING.charAt(nextInt(STRING.length())));
		}

		return builder.toString();
	}

}
