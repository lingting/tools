package live.lingting.tools.spring.post;

import live.lingting.tools.core.ContextComponent;
import live.lingting.tools.spring.core.ToolsBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author lingting 2022/10/22 15:10
 */
@Component
public class ContextComposeBeanPostProcessor implements ToolsBeanPostProcessor {

	@Override
	public boolean isProcess(Object bean, String beanName, boolean isBefore) {
		return bean != null && ContextComponent.class.isAssignableFrom(bean.getClass());
	}

	@Override
	public Object postProcessAfter(Object bean, String beanName) {
		((ContextComponent) bean).onApplicationStart();
		return bean;
	}

}
