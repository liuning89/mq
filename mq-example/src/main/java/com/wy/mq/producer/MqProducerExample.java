/**
 * 
 */
package com.wy.mq.producer;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.wy.mq.LtnMqMessage;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月29日
 */
public class MqProducerExample {

	@Autowired
	private IMqProducerService					mqProducerService;

	private static ScheduledThreadPoolExecutor	scheduler	= new ScheduledThreadPoolExecutor(10);

	public void start() throws Exception {
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					LtnMqMessage ltnMqMessage = new LtnMqMessage("USER", "NEW", "userid : " + System.currentTimeMillis(), "同步");
					// 同步发送
					mqProducerService.send(ltnMqMessage);
					Thread.sleep(1000);

					ltnMqMessage = new LtnMqMessage("USER", "NEW", "userid : " + System.currentTimeMillis(), "异步");
					// 同步发送
					mqProducerService.asyncSend(ltnMqMessage);
					Thread.sleep(1000);
					ltnMqMessage = new LtnMqMessage("USER", "LOGIN", "userid : " + System.currentTimeMillis(), "同步");
					// 异步发送
					mqProducerService.send(ltnMqMessage);
					Thread.sleep(1000);
					ltnMqMessage = new LtnMqMessage("USER", "LOGIN", "userid : " + System.currentTimeMillis(), "异步");
					// 异步发送
					mqProducerService.asyncSend(ltnMqMessage);

					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}, 1000, 2000, TimeUnit.MILLISECONDS);
	}
}
