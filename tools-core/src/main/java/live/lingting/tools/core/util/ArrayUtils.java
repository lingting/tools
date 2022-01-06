package live.lingting.tools.core.util;

import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class ArrayUtils {

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

}
