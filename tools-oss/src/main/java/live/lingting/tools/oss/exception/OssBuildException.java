package live.lingting.tools.oss.exception;

/**
 * @author lingting 2022/8/20 15:59
 */
public class OssBuildException extends Exception {

	public OssBuildException(String message) {
		super(message);
	}

	public OssBuildException(String message, Throwable cause) {
		super(message, cause);
	}

	public OssBuildException(Throwable cause) {
		super(cause);
	}

	protected OssBuildException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
