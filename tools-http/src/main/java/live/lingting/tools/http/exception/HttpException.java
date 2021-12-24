package live.lingting.tools.http.exception;

/**
 * @author lingting
 */
public class HttpException extends Exception {

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}

}
