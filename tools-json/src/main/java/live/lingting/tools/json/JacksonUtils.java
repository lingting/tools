package live.lingting.tools.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import live.lingting.tools.core.util.ClassUtils;
import live.lingting.tools.json.javkson.JavaTimeModule;
import live.lingting.tools.json.javkson.NullSerializerModifier;

/**
 * @author lingting 2021/6/9 14:28
 */
@UtilityClass
public class JacksonUtils {

	static ObjectMapper mapper = new ObjectMapper();

	static final String JSON_READ_FEATURE_CLASS = "com.fasterxml.jackson.core.json.JsonReadFeature";

	static {
		// 序列化时忽略未知属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 单值元素可以被设置成 array, 防止处理 ["a"] 为 List<String> 时报错
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializerFactory(mapper.getSerializerFactory().withSerializerModifier(new NullSerializerModifier()));
		mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
		// 时间解析器
		mapper.registerModule(new JavaTimeModule());
		if (ClassUtils.isPresent(JSON_READ_FEATURE_CLASS, JacksonUtils.class.getClassLoader())) {
			// 有特殊需要转义字符, 不报错
			mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
		}
	}

	public static void config(ObjectMapper mapper) {
		JacksonUtils.mapper = mapper;
	}

	public static void config(Consumer<ObjectMapper> consumer) {
		consumer.accept(mapper);
	}

	@SneakyThrows
	public static String toJson(Object obj) {
		return mapper.writeValueAsString(obj);
	}

	@SneakyThrows
	public static <T> T toObj(String json, Class<T> r) {
		return mapper.readValue(json, r);
	}

	@SneakyThrows
	public static <T> T toObj(String json, Type t) {
		return mapper.readValue(json, mapper.constructType(t));
	}

	@SneakyThrows
	public static <T> T toObj(String json, TypeReference<T> t) {
		return mapper.readValue(json, t);
	}

}
