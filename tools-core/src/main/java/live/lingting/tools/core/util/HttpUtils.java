package live.lingting.tools.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class HttpUtils {

	public static String urlParamBuild(Map<String, Object> map) throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();

		final Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			final Map.Entry<String, Object> entry = iterator.next();

			builder.append(entry.getKey()).append("=");

			if (entry.getValue() instanceof Iterable) {
				builder.append(iteratorParamsBuild(((Iterable<?>) entry.getValue()).iterator()));
			}
			else if (entry.getValue() instanceof Iterator) {
				builder.append(iteratorParamsBuild((Iterator<?>) entry.getValue()));
			}
			else {
				builder.append(urlParamEncoder(entry.getValue().toString()));
			}

			if (iterator.hasNext()) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

	public static String iteratorParamsBuild(Iterator<?> iterator) throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();

		while (iterator.hasNext()) {
			final Object next = iterator.next();
			if (next == null) {
				continue;
			}

			builder.append(urlParamEncoder(next.toString()));

			if (iterator.hasNext()) {
				builder.append(",");
			}
		}

		return builder.toString();
	}

	public static String urlParamEncoder(String parma) throws UnsupportedEncodingException {
		return URLEncoder.encode(parma, StandardCharsets.UTF_8.name());
	}

}
