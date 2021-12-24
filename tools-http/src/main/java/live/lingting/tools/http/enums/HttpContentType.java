package live.lingting.tools.http.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lingting 2021/9/10 16:09
 */
@Getter
@AllArgsConstructor
public enum HttpContentType {

	/**
	 * json
	 */
	APPLICATION_JSON("application/json"),
	/**
	 * form
	 */
	APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),

	MULTIPART_FORM_DATA("multipart/form-data"),
	/**
	 * xml
	 */
	APPLICATION_XML("application/xml"),

	;

	private final String value;

}
