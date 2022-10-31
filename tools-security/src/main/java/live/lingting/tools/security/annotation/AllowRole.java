package live.lingting.tools.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 前置角色校验
 * @author lingting 2022/10/28 13:55
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@MetaCacheAnnotation
public @interface AllowRole {

	/**
	 * 并行角色code, 表示用户拥有任意一个角色就允许访问, 与 parallelCode 等价
	 * <p>
	 * 假设设置 value 为 ["user:child:read", "user:read"]
	 * </p>
	 * <p>
	 * 假设设置 parallelCode 为 ["user:all"]
	 * </p>
	 * <p>
	 * 此时用户拥有 ["user:child:read", "user:read", "user:all"] 中任一角色皆可访问接口
	 * </p>
	 */
	String[] value() default {};

	/**
	 * 串行角色code, 表示用户必须同时拥有这些角色code才允许访问
	 * <p>
	 * 当同时设置并行角色时, 则表示必须要同时满足串行角色和并行角色才允许访问
	 * </p>
	 *
	 * <p>
	 * 假设设置 并行角色 为 ["user:child:read", "user:read"]
	 * </p>
	 * <p>
	 * 假设设置 串行角色 为 ["user:all"]
	 * </p>
	 * <p>
	 * 此时用户拥有 ["user:child:read", "user:read"] 中任一角色 且 同时拥有 ["user:all"]角色 才可访问接口
	 * </p>
	 * @return java.lang.String[]
	 */
	String[] serialCode() default {};

	/**
	 * 并行角色code, 表示用户拥有任意一个角色就允许访问
	 */
	String[] parallelCode() default {};

}
