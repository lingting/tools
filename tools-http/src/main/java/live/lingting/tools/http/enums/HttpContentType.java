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
	/**
	 * application/pdf
	 */
	APPLICATION_PDF("application/pdf"),
	/**
	 * application/msword
	 */
	APPLICATION_MSWORD("application/msword"),
	/**
	 * application/octet-stream 常用于文件下载
	 */
	APPLICATION_OCTET_STREAM("application/octet-stream"),
	/**
	 * text/html
	 */
	TEXT_HTML("text/html"),
	/**
	 * text/plain
	 */
	TEXT_PLAIN("text/plain"),
	/**
	 * text/xml
	 */
	TEXT_XML("text/xml"),
	/**
	 * image/gif
	 */
	IMAGE_GIF("image/gif"),
	/**
	 * image/jpeg
	 */
	IMAGE_JPEG("image/jpeg"),
	/**
	 * image/png
	 */
	IMAGE_PNG("image/png"),

	;

	private final String value;

	public String value() {
		return value;
	}

}
