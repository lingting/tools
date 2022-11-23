package live.lingting.tools.core.thread;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2022/11/17 20:15
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadPool {

	private static final ThreadPool THREAD_POOL;

	private static final Integer QUEUE_MAX = 100;

	@Getter
	private final ThreadPoolExecutor pool;

	static {
		THREAD_POOL = new ThreadPool(new ThreadPoolExecutor(
				// 核心线程数大小. 不论是否空闲都存在的线程
				300,
				// 最大线程数 - 10万个
				100000,
				// 存活时间. 非核心线程数如果空闲指定时间. 就回收
				// 存活时间不宜过长. 避免任务量遇到尖峰情况时. 大量空闲线程占用资源
				10,
				// 存活时间的单位
				TimeUnit.SECONDS,
				// 等待任务存放队列 - 队列最大值
				// 这样配置. 当积压任务数量为 队列最大值 时. 会创建新线程来执行任务. 直到线程总数达到 最大线程数
				new LinkedBlockingQueue<>(QUEUE_MAX),
				// 新线程创建工厂 - LinkedBlockingQueue 不支持线程优先级. 所以直接新增线程就可以了
				runnable -> new Thread(null, runnable),
				// 拒绝策略 - 在主线程继续执行.
				new ThreadPoolExecutor.CallerRunsPolicy()));
	}

	public static ThreadPool instance() {
		return THREAD_POOL;
	}

	/**
	 * 线程池是否正在执行任务
	 */
	public boolean isRunning() {
		return getPool().getTaskCount() > 0;
	}

	public void execute(Runnable runnable) {
		getPool().execute(new ExceptionRunnable(runnable));
	}

	@RequiredArgsConstructor
	public static class ExceptionRunnable implements Runnable {

		private final Runnable runnable;

		@Override
		public void run() {
			try {
				runnable.run();
			}
			catch (Throwable e) {
				log.debug("线程发生异常!", e);
				if (!(e instanceof Exception)) {
					throw e;
				}
			}
		}

	}

}
