package live.lingting.tools.json;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import live.lingting.tools.core.util.ClassUtils;
import live.lingting.tools.json.jackson.JavaTimeModule;
import live.lingting.tools.json.jackson.NullSerializerModifier;

/**
 * @author lingting 2021/6/9 14:28
 */
@UtilityClass
public class JacksonUtils {

	@Getter
	static ObjectMapper mapper = new ObjectMapper();

	static final String JSON_READ_FEATURE_CLASS = "com.fasterxml.jackson.core.json.JsonReadFeature";

	private static final String JACKSON_CLASS = "com.fasterxml.jackson.databind.ObjectMapper";

	private static final String GSON_CLASS = "com.google.gson.Gson";

	private static final String HUTOOL_JSON_CLASS = "cn.hutool.json.JSONConfig";

	private static final String HUTOOL_JSON_TYPE_REFERENCE_CLASS = "cn.hutool.core.lang.TypeReference";

	private static final String FAST_JSON_CLASS = "com.alibaba.fastjson.JSON";

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
		// 防止误传入其他类型的 typeReference 走这个方法然后转换出错
		if (classIsPresent(HUTOOL_JSON_TYPE_REFERENCE_CLASS) && t instanceof cn.hutool.core.lang.TypeReference) {
			return toObj(json, new TypeReference<T>() {
				@Override
				public Type getType() {
					return ((cn.hutool.core.lang.TypeReference<?>) t).getType();
				}
			});
		}
		else if (classIsPresent(FAST_JSON_CLASS) && t instanceof com.alibaba.fastjson.TypeReference) {
			return toObj(json, new TypeReference<T>() {
				@Override
				public Type getType() {
					return ((com.alibaba.fastjson.TypeReference<?>) t).getType();
				}
			});
		}
		else if (classIsPresent(JACKSON_CLASS) && t instanceof com.fasterxml.jackson.core.type.TypeReference) {
			return toObj(json, new TypeReference<T>() {
				@Override
				public Type getType() {
					return ((com.fasterxml.jackson.core.type.TypeReference<?>) t).getType();
				}
			});
		}

		return mapper.readValue(json, mapper.constructType(t));
	}

	@SneakyThrows
	public static <T> T toObj(String json, TypeReference<T> t) {
		return mapper.readValue(json, t);
	}

	private static boolean classIsPresent(String className) {
		return ClassUtils.isPresent(className, JacksonUtils.class.getClassLoader());
	}

}
