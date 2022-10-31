package live.lingting.tools.security.exception;

/**
 * 权限异常
 *
 * @author lingting 2022/10/28 15:51
 */
public class SecurityRoleException extends SecurityException {

	public SecurityRoleException() {
		super("角色校验异常!");
	}

}
