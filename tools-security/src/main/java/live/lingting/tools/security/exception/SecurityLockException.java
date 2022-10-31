package live.lingting.tools.security.exception;

/**
 * 权限异常
 *
 * @author lingting 2022/10/28 15:51
 */
public class SecurityLockException extends SecurityException {

	public SecurityLockException() {
		super("用户已被锁定!");
	}

}
