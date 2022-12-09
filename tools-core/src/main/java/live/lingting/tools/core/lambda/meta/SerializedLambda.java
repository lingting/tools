package live.lingting.tools.core.lambda.meta;

import live.lingting.tools.core.lambda.LambdaException;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serial;
import java.io.Serializable;

/**
 * 当前类是 {@link java.lang.invoke.SerializedLambda } 的一个镜像
 * <p>
 * Create by hcl at 2020/7/17
 */
@SuppressWarnings("ALL")
@Data
public class SerializedLambda implements Serializable {

	@Serial
	private static final long serialVersionUID = 8025925345765570181L;

	private Class<?> capturingClass;

	private String functionalInterfaceClass;

	private String functionalInterfaceMethodName;

	private String functionalInterfaceMethodSignature;

	private String implClass;

	private String implMethodName;

	private String implMethodSignature;

	private int implMethodKind;

	private String instantiatedMethodType;

	private transient Object[] capturedArgs;

	public static SerializedLambda extract(Serializable serializable) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(serializable);
			oos.flush();
			try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
				@Override
				protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
					Class<?> clazz = super.resolveClass(desc);
					return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
				}

			}) {
				return (SerializedLambda) ois.readObject();
			}
		}
		catch (IOException | ClassNotFoundException e) {
			throw new LambdaException("获取序列化信息时异常!", e);
		}
	}

	public String getInstantiatedMethodType() {
		return instantiatedMethodType;
	}

	public Class<?> getCapturingClass() {
		return capturingClass;
	}

	public String getImplMethodName() {
		return implMethodName;
	}

}
