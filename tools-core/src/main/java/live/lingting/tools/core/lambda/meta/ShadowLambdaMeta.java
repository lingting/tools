package live.lingting.tools.core.lambda.meta;

import live.lingting.tools.core.lambda.LambdaException;
import live.lingting.tools.core.util.ClassUtils;

/**
 * 基于 {@link SerializedLambda} 创建的元信息
 * <p>
 * Create by hcl at 2021/7/7
 */
public class ShadowLambdaMeta implements LambdaMeta {

	private final SerializedLambda lambda;

	public ShadowLambdaMeta(SerializedLambda lambda) {
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
			return ClassUtils.loadClass(instantiatedType, lambda.getCapturingClass().getClassLoader());
		}
		catch (ClassNotFoundException e) {
			throw new LambdaException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法!", e);
		}
	}

}
