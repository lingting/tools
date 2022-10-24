package live.lingting.tools.spring.configuration;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import live.lingting.tools.core.util.ClassUtils;
import live.lingting.tools.json.JacksonUtils;
import live.lingting.tools.json.jackson.JavaTimeModule;
import live.lingting.tools.json.jackson.NullSerializerProvider;
import live.lingting.tools.spring.constant.SpringConstants;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author lingting 2022/10/15 15:20
 */
@ComponentScan("live.lingting.tools.spring")
@AutoConfiguration(before = JacksonAutoConfiguration.class)
public class ToolsSpringAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	@ConditionalOnClass({ Jackson2ObjectMapperBuilder.class })
	public ObjectMapper toolsObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper mapper = builder.createXmlMapper(false).build();

		// 对于空对象的序列化不抛异常
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 序列化时忽略未知属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 单值元素可以被设置成 array, 防止处理 ["a"] 为 List<String> 时报错
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// 有特殊需要转义字符, 不报错
		mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());

		// 引入了 tools-json
		if (ClassUtils.isPresent(SpringConstants.CLASS_TOOLS_JACKSON, getClass().getClassLoader())) {
			// 空值处理 避免未引入 tools-json 时报错
			mapper.setSerializerProvider(NullSerializerProvider.INSTANCE.copy());
			// 替换原有配置
			JacksonUtils.config(mapper);
		}

		return mapper;
	}

	/**
	 * @author lingting 2022/10/15 16:11
	 */
	@ConditionalOnClass(JacksonUtils.class)
	@Configuration(proxyBeanMethods = false)
	public static class ToolsJsonAutoConfiguration {

		@Bean
		@ConditionalOnMissingBean(JavaTimeModule.class)
		public JavaTimeModule toolsJavaTimeModule() {
			return new JavaTimeModule();
		}

	}

}
