package live.lingting.tools.http.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lingting 2021/7/12 16:39
 */
@Getter
@AllArgsConstructor
public enum HttpHeader {

	/**
	 * Accept
	 */
	ACCEPT("Accept"),
	/**
	 * User_Agent
	 */
	USER_AGENT("User-Agent"),
	/**
	 * Content_Type
	 */
	CONTENT_TYPE("Content-Type"),
	/**
	 * Authorization
	 */
	AUTHORIZATION("Authorization"),
	/**
	 * Content-Encoding
	 */
	CONTENT_ENCODING("Content-Encoding"),
	/**
	 * Connection
	 */
	CONNECTION("Connection"),

	;

	private final String val;

}
