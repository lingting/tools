package live.lingting.tools.core.lambda.meta;

import live.lingting.tools.core.lambda.LambdaException;
import live.lingting.tools.core.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * Created by hcl at 2021/5/14
 */
@Slf4j
@SuppressWarnings("java:S1181")
public class ReflectLambdaMeta implements LambdaMeta {

	private static final Field FIELD_CAPTURING_CLASS;

	static {
		Field fieldCapturingClass;
		try {
			Class<SerializedLambda> aClass = SerializedLambda.class;
			fieldCapturingClass = ClassUtils.setAccessible(aClass.getDeclaredField("capturingClass"));
		}
		catch (Throwable e) {
			// 解决高版本 jdk 的问题 gitee: https://gitee.com/baomidou/mybatis-plus/issues/I4A7I5
			fieldCapturingClass = null;
		}
		FIELD_CAPTURING_CLASS = fieldCapturingClass;
	}

	private final SerializedLambda lambda;

	public ReflectLambdaMeta(SerializedLambda lambda) {
		this.lambda = lambda;
	}

	@Override
	public String getImplMethodName() {
		return lambda.getImplMethodName();
	}

	@Override
	public Class<?> getInstantiatedClass() {
		String instantiatedMethodType = lambda.getInstantiatedMethodType();
		String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(';')).replace('/',
				'.');
		try {
			return ClassUtils.loadClass(instantiatedType, getCapturingClassClassLoader());
		}
		catch (ClassNotFoundException e) {
			throw new LambdaException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法!", e);
		}
	}

	private ClassLoader getCapturingClassClassLoader() {
		// 如果反射失败，使用默认的 classloader
		if (FIELD_CAPTURING_CLASS == null) {
			return null;
		}
		try {
			return ((Class<?>) FIELD_CAPTURING_CLASS.get(lambda)).getClassLoader();
		}
		catch (IllegalAccessException e) {
			throw new LambdaException("获取 class loader 时异常!", e);
		}
	}

}
