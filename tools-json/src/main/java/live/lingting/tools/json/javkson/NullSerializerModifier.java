package live.lingting.tools.json.javkson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lingting
 */
public class NullSerializerModifier extends BeanSerializerModifier {

	private final JsonSerializer<Object> nullArrayJsonSerializer = new NullArrayJsonSerializer();

	private final JsonSerializer<Object> nullMapJsonSerializer = new NullMapJsonSerializer();

	private final JsonSerializer<Object> nullStringJsonSerializer = new NullStringJsonSerializer();

	@Override
	public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties) {
		// 循环所有的beanPropertyWriter
		for (BeanPropertyWriter writer : beanProperties) {
			if (isStringType(writer)) {
				// null 字符串转空
				writer.assignNullSerializer(this.nullStringJsonSerializer);
			}
			else if (isArrayType(writer)) {
				// null array 或 list，set则注册nullSerializer
				writer.assignNullSerializer(this.nullArrayJsonSerializer);
			}
			else if (isMapType(writer)) {
				// null Map 转 '{}'
				writer.assignNullSerializer(this.nullMapJsonSerializer);
			}
		}
		return beanProperties;
	}

	private boolean isStringType(BeanPropertyWriter writer) {
		Class<?> clazz = writer.getType().getRawClass();
		return String.class.isAssignableFrom(clazz);
	}

	private boolean isMapType(BeanPropertyWriter writer) {
		Class<?> clazz = writer.getType().getRawClass();
		return Map.class.isAssignableFrom(clazz);
	}

	private boolean isArrayType(BeanPropertyWriter writer) {
		Class<?> clazz = writer.getType().getRawClass();
		return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
	}

}
