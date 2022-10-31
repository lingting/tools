package live.lingting.tools.security.core;

import live.lingting.tools.core.util.ArrayUtils;
import live.lingting.tools.security.annotation.AllowAnonymity;
import live.lingting.tools.security.annotation.AllowPermission;
import live.lingting.tools.security.annotation.AllowRole;
import live.lingting.tools.security.constant.SecurityConstants;
import live.lingting.tools.security.exception.SecurityLoginException;
import live.lingting.tools.security.exception.SecurityPermissionException;
import live.lingting.tools.security.exception.SecurityRoleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lingting 2022/10/28 13:53
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Order(SecurityConstants.ORDER_ASPECT_PERMISSION)
public class SecurityPermissionAspect {

	protected static final List<Class<? extends Annotation>> ANNOTATION_CLS_LIST;

	static {
		ANNOTATION_CLS_LIST = new ArrayList<>(8);
		ANNOTATION_CLS_LIST.add(AllowPermission.class);
		ANNOTATION_CLS_LIST.add(AllowRole.class);
		ANNOTATION_CLS_LIST.add(AllowAnonymity.class);
	}

	private final SecurityEvaluator evaluator;

	@Pointcut("execution(@(@live.lingting.tools.security.annotation.MetaCacheAnnotation *) * *(..))")
	public void pointcut() {
		//
	}

	public <T extends Annotation> T getAnnotation(ProceedingJoinPoint point, Class<T> cls) {
		T t = null;

		Signature signature = point.getSignature();
		if (signature instanceof MethodSignature ms) {
			t = ms.getMethod().getAnnotation(cls);
		}

		if (t == null) {
			t = point.getTarget().getClass().getAnnotation(cls);
		}

		return t;
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		AllowPermission allowPermission = getAnnotation(point, AllowPermission.class);

		if (!valid(evaluator::hasPermission, allowPermission.serialCode(), allowPermission.value(),
				allowPermission.parallelCode())) {
			throw new SecurityPermissionException();
		}

		AllowRole allowRole = getAnnotation(point, AllowRole.class);

		if (!valid(evaluator::hasRole, allowRole.serialCode(), allowRole.value(), allowRole.parallelCode())) {
			throw new SecurityRoleException();
		}

		AllowAnonymity allowAnonymity = getAnnotation(point, AllowAnonymity.class);

		// 允许匿名登录 或者 用户正常 则放行
		if (allowAnonymity != null || evaluator.isNormal()) {
			return point.proceed();
		}

		// 默认允许访问
		throw new SecurityLoginException();
	}

	/**
	 * 校验是否放行
	 * @param has 判断是否拥有code
	 * @param serial 串行
	 * @param parallelArray 并行
	 * @return boolean true 表示方法
	 */
	protected boolean valid(Predicate<String> has, String[] serial, String[]... parallelArray) {
		// 串行权限已设置
		if (!ArrayUtils.isEmpty(serial)) {
			for (String code : serial) {
				// 串行权限中存在任一权限不满足, 则未通过校验
				if (!has.test(code)) {
					return false;
				}
			}
		}

		// 对并行权限进行判断
		for (String[] parallel : parallelArray) {
			// 未设置 跳过
			if (ArrayUtils.isEmpty(parallel)) {
				continue;
			}

			for (String code : parallel) {
				// 拥有任意一个权限则通过校验
				if (has.test(code)) {
					return true;
				}
			}
		}

		return true;
	}

}
