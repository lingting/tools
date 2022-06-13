package live.lingting.tools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	@SafeVarargs
	public static <T> Set<T> toSet(T... ts) {
		HashSet<T> set = new HashSet<>();
		Collections.addAll(set, ts);
		return set;
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map != null && map.isEmpty();
	}

	/**
	 * 是否是否可以存放多个数据
	 */
	public static boolean isMulti(Object obj) {
		return obj instanceof Iterable || obj instanceof Iterator || obj.getClass().isArray();
	}

	public static ArrayList<Object> multiToList(Object obj) {
		ArrayList<Object> list = new ArrayList<>();

		if (obj.getClass().isArray()) {
			Collections.addAll(list, (Object[]) obj);
		}
		else if (obj instanceof Iterator) {
			((Iterator<?>) obj).forEachRemaining(list::add);
		}
		else if (obj instanceof Iterable) {
			((Iterable<?>) obj).forEach(list::add);
		}

		return list;
	}

}
