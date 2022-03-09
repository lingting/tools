package live.lingting.tools.core.http.encoding;

import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class HttpGetCoding {

	public static final HttpGetCodingPlugin URL_PLUGIN = new UrlHttpGetCodingPlugin();

	public static final HttpGetCodingPlugin PARAM_PLUGIN = new ParamHttpGetCodingPlugin();

	/**
	 * 编码http请求地址
	 * @param str 请求地址
	 * @return java.lang.String
	 */
	public static String encodingUrl(String str) {
		return encoding(str, URL_PLUGIN);
	}

	/**
	 * 编码http单个请求参数
	 * @param str 请求参数
	 * @return java.lang.String
	 */
	public static String encodingParam(String str) {
		return encoding(str, PARAM_PLUGIN);
	}

	public static String encoding(String str, HttpGetCodingPlugin plugin) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			builder.append(plugin.encoding(str.charAt(i)));
		}

		return builder.toString();
	}

}
