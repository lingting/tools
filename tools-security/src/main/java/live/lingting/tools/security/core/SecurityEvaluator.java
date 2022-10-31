package live.lingting.tools.security.core;

import live.lingting.tools.security.SecurityUser;
import live.lingting.tools.security.exception.SecurityExpireException;
import live.lingting.tools.security.exception.SecurityLockException;
import live.lingting.tools.security.exception.SecurityLoginException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 权限校验
 *
 * @author lingting 2022/10/28 14:51
 */
public interface SecurityEvaluator {

	/**
	 * 获取用户信息
	 * @return live.lingting.tools.security.SecurityUser
	 */
	SecurityUser getUser();

	/**
	 * 判断当前用户是否正常
	 * @return boolean true 表示用户正常, 能够访问要求登录才允许访问的方法
	 */
	default boolean isNormal() {
		// 用户状态判断
		SecurityUser user = getUser();
		if (user == null) {
			throw new SecurityLoginException();
		}
		else if (user.isLock()) {
			throw new SecurityLockException();
		}
		else if (user.isExpired()) {
			throw new SecurityExpireException();
		}
		return true;
	}

	/**
	 * 是否拥有指定权限
	 * @param code 权限码
	 * @return boolean true 表示拥有指定权限
	 */
	default boolean hasPermission(String code) {
		if (!StringUtils.hasText(code)) {
			return false;
		}
		SecurityUser user = getUser();
		if (user == null) {
			return false;
		}
		Collection<String> permission = user.getPermission();
		if (CollectionUtils.isEmpty(permission)) {
			return false;
		}
		return permission.contains(code);
	}

	/**
	 * 是否拥有指定角色
	 * @param code 角色码
	 * @return boolean true 表示拥有指定角色
	 */
	default boolean hasRole(String code) {
		if (!StringUtils.hasText(code)) {
			return false;
		}
		SecurityUser user = getUser();
		if (user == null) {
			return false;
		}
		Collection<String> roles = user.getRoles();
		if (CollectionUtils.isEmpty(roles)) {
			return false;
		}
		return roles.contains(code);
	}

}
