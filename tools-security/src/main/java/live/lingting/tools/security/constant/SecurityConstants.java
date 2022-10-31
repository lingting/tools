package live.lingting.tools.security.constant;

import lombok.experimental.UtilityClass;

/**
 * @author lingting 2022/10/28 12:08
 */
@UtilityClass
public class SecurityConstants {

	/**
	 * token 解析过滤器优先级, 不设置成太高
	 */
	public static final int ORDER_FILTER_TOKEN = Integer.MIN_VALUE + 10000;

	/**
	 * uri 是否需要登录过滤器优先级, 再 token 解析后面
	 */
	public static final int ORDER_FILTER_URI = ORDER_FILTER_TOKEN + 100;

	/**
	 * 接口权限校验过滤器优先级, 在是否需要登录过滤器后面
	 */
	public static final int ORDER_FILTER_PERMISSION = ORDER_FILTER_URI + 100;

	/**
	 * 接口校验 aop 优先级
	 */
	public static final int ORDER_ASPECT_PERMISSION = Integer.MIN_VALUE;

	/**
	 * 匿名登录注解优先级
	 */
	public static final int ORDER_ANNOTATION_ANONYMITY = Integer.MIN_VALUE;

	/**
	 * 权限校验优先级
	 */
	public static final int ORDER_ANNOTATION_PERMISSION = ORDER_ANNOTATION_ANONYMITY - 100;

	/**
	 * 角色校验
	 */
	public static final int ORDER_ANNOTATION_ROLE = ORDER_ANNOTATION_PERMISSION - 100;

}
