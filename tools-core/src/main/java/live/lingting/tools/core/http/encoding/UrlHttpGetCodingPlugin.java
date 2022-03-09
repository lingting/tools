package live.lingting.tools.core.http.encoding;

import java.util.HashMap;
import java.util.Map;

/**
 * get 请求地址编码
 *
 * @author lingting
 */
public class UrlHttpGetCodingPlugin implements HttpGetCodingPlugin {

	private static final Map<Character, String> DICT = new HashMap<>();

	static {
		DICT.put(' ', "%20");
		DICT.put('"', "%22");
		DICT.put('#', "%23");
		DICT.put('$', "%24");
		DICT.put('%', "%25");
		DICT.put('&', "%26");
		DICT.put('\'', "%27");
		// DICT.put('(', "%28");
		// DICT.put(')', "%29");
		DICT.put('+', "%2B");
		DICT.put(',', "%2C");
		// DICT.put('/', "%2F");
		// DICT.put(':', "%3A");
		DICT.put(';', "%3B");
		DICT.put('<', "%3C");
		DICT.put('=', "%3D");
		DICT.put('>', "%3E");
		DICT.put('?', "%3F");
		DICT.put('@', "%40");
		DICT.put('[', "%5B");
		DICT.put('\\', "%5C");
		DICT.put(']', "%5D");
		DICT.put('^', "%5E");
		DICT.put('{', "%7B");
		DICT.put('|', "%7C");
		DICT.put('}', "%7D");
	}

	@Override
	public String encoding(char c) {
		if (DICT.containsKey(c)) {
			return DICT.get(c);
		}
		return String.valueOf(c);
	}

	public static void put(char source, String target) {
		DICT.put(source, target);
	}

}
