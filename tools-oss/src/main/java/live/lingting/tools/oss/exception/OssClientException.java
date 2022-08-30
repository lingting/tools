package live.lingting.tools.oss.exception;

/**
 * @author lingting 2022/8/20 16:40
 */
public class OssClientException extends RuntimeException {

	public OssClientException(String message) {
		super(message);
	}

	public OssClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public OssClientException(Throwable cause) {
		super(cause);
	}

	protected OssClientException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
