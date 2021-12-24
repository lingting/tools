package live.lingting.tools.core.util;

import java.util.Iterator;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class HttpUtils {

	public static String urlParamBuild(Map<String, Object> map) {
		StringBuilder builder = new StringBuilder();

		final Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			final Map.Entry<String, Object> entry = iterator.next();

			builder.append(entry.getKey()).append("=");

			if (entry.getValue() instanceof Iterable) {
				builder.append(StringUtils.join((Iterable<?>) entry.getValue(), ","));
			}
			else if (entry.getValue() instanceof Iterator) {
				builder.append(StringUtils.join((Iterator<?>) entry.getValue(), ","));
			}
			else {
				builder.append(entry.getValue());
			}

			if (iterator.hasNext()) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

}
