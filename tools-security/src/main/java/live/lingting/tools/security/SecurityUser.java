package live.lingting.tools.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * 用户
 *
 * @author lingting 2022/10/28 11:51
 */
public interface SecurityUser extends Serializable {

	/**
	 * 获取当前用户名
	 * @return java.lang.String
	 */
	String getUsername();

	/**
	 * 获取当前用户的角色
	 * @return java.util.Set<live.lingting.tools.security.SecurityRole>
	 */
	Collection<String> getRoles();

	/**
	 * 获取用户拥有的权限
	 * @return java.util.Set<java.lang.String>
	 */
	Collection<String> getPermission();

	/**
	 * 返回当前用户是否为管理员
	 * @return boolean true 表示为管理员
	 */
	boolean isAdmin();

	/**
	 * 返回当前用户是否为超管
	 * @return boolean true 表示为超级管理员
	 */
	boolean isSupperAdmin();

	/**
	 * 该用户是否已锁定
	 * @return boolean true 表示已锁定
	 */
	boolean isLock();

	/**
	 * 该用户是否已过期
	 * @return boolean true 表示已过期
	 */
	boolean isExpired();

}
