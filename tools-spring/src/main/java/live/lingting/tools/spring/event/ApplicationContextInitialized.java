package live.lingting.tools.spring.event;

import live.lingting.tools.spring.util.SpringUtils;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 监听spring上下文初始化完成事件
 *
 * @author lingting 2022/10/15 15:27
 */
public class ApplicationContextInitialized implements ApplicationListener<ApplicationContextInitializedEvent> {

	@Override
	public void onApplicationEvent(ApplicationContextInitializedEvent event) {
		// 给 spring utils 注入 spring 上下文
		SpringUtils.setContext(event.getApplicationContext());
	}

}
