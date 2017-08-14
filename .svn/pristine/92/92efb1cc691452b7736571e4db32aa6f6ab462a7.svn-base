/**
 * 
 */
package com.wy.mq.producer;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.wy.mq.task.service.ILoadMqConfigService;
import com.wy.mq.task.service.IReConsumerService;
import com.wy.mq.task.service.IRetrySendService;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月29日
 */
public class TaskExample {

	@Autowired
	private ILoadMqConfigService				loadMqConfigService;

	@Autowired
	private IReConsumerService					reConsumerService;

	@Autowired
	private IRetrySendService					retrySendService;

	private static ScheduledThreadPoolExecutor	scheduler	= new ScheduledThreadPoolExecutor(10);

	public void start() throws Exception {
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					retrySendService.execute();
					loadMqConfigService.execute();
					reConsumerService.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 5000, 2000, TimeUnit.MILLISECONDS);
	}
}
