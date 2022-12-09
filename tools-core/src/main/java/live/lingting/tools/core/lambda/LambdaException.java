package live.lingting.tools.core.lambda;

/**
 * @author lingting 2022/12/6 17:07
 */
public class LambdaException extends RuntimeException {

	public LambdaException(String message) {
		super(message);
	}

	public LambdaException(String message, Throwable cause) {
		super(message, cause);
	}

}
