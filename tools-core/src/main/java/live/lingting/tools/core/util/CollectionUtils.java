package live.lingting.tools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lingting
 */
public class CollectionUtils {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	@SafeVarargs
	public static <T> List<T> toList(T... ts) {
		final ArrayList<T> list = new ArrayList<>();
		Collections.addAll(list, ts);
		return list;
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map != null && map.isEmpty();
	}

}
