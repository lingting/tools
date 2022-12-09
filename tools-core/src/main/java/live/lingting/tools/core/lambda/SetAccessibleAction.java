package live.lingting.tools.core.lambda;

import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;

/**
 * Create by hcl at 2021/5/14
 */
@SuppressWarnings("java:S3011")
public class SetAccessibleAction<T extends AccessibleObject> implements PrivilegedAction<T> {

	private final T obj;

	public SetAccessibleAction(T obj) {
		this.obj = obj;
	}

	@Override
	public T run() {
		obj.setAccessible(true);
		return obj;
	}

}
