package live.lingting.tools.security.exception;

/**
 * 权限异常
 *
 * @author lingting 2022/10/28 15:51
 */
public class SecurityPermissionException extends SecurityException {

	public SecurityPermissionException() {
		super("权限校验异常!");
	}

}
