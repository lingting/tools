package live.lingting.tools.http.constant;

import lombok.experimental.UtilityClass;

/**
 * @author lingting
 */
@UtilityClass
public class HttpConstants {

	public static final String HTTP = "http";

	public static final String HTTPS = "https";

	public static final String URL_DELIMITER = "://";

	public static final String QUERY_DELIMITER = "?";

	public static final String QUERY_DELIMITER_SPLIT = "\\?";

	public static final String QUERY_PARAMS_DELIMITER = "&";

	public static final String DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"%s\"\r\n\r\n";

	public static final String DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n";

	public static final String CONTENT_TYPE_TEMPLATE = "Content-Type: %s\r\n\r\n";

	public static final String MULTIPART_DEFAULT_CONTENT_TYPE = "application/octet-stream";

	public static final int SUCCESS_CODE = 200;

	public static final String GZIP = "gzip";

	public static final String DEFLATE = "deflate";

	public static final String UA = "lingting";

	public static final String HEADER_CONTENT_TYPE_CHARSET = "%s; charset=%s";

	public static final String HEADER_CONTENT_TYPE_BOUNDARY = "%s; charset=%s; boundary=%s";

}
