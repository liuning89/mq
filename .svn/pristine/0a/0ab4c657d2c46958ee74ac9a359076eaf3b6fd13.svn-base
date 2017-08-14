/**
 * 
 */
package com.wy.mq;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年11月1日
 */
public class MqThreadPoolExecutor {
	private static final int			defaultOffset		= 100;

	private static final int			maxOffset			= 1000 * 60;

	private static AtomicInteger		offset				= new AtomicInteger(defaultOffset);

	/**
	 * <pre>
	 * 核心线程5个
	 * 最大线程数500个	最大线程数不可设置过大，会导致stackoverflow
	 * 线程空闲1分钟进行回收
	 * 排队任务数最大5000
	 * 
	 * </pre>
	 */
	private static ThreadPoolExecutor	threadPoolExecutor	= new ThreadPoolExecutor(5, 500, 1, TimeUnit.MINUTES,//
																	new ArrayBlockingQueue<Runnable>(5000),//
																	new RejectedExecutionHandler() {//

																		@Override
																		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
																			synchronized (r) {
																				try {
																					// 防止消息洪峰
																					int offsetValue = offset.intValue();
																					if (offsetValue >= maxOffset)
																						offset.set(defaultOffset);

																					r.wait(offsetValue);
																				} catch (InterruptedException e) {
																					e.printStackTrace();
																				}
																			}
																			offset.addAndGet(50);
																			executor.submit(r);
																		}
																	});

	public static ThreadPoolExecutor getDefaultThreadPoolExecutor() {
		return threadPoolExecutor;
	}
}
