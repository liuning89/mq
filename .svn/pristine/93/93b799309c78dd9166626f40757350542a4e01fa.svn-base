/**
 * 
 */
package com.wy.mq.producer;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.wy.mq.dto.MqConfig;
import com.wy.mq.service.IMqConfigService;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月29日
 */
public class MqSubUnsubExample {

	@Autowired
	private IMqConfigService					mqConfigService;

	private static ScheduledThreadPoolExecutor	scheduler	= new ScheduledThreadPoolExecutor(10);

	public void start() throws Exception {
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					MqConfig config = new MqConfig();
					config.setGroup("user");
					config.setVersion("login");
					config.setTopic("USER");
					config.setTag("LOGIN");
					config.setRegistryGroup("mq-service");
					mqConfigService.remove(config);
					Thread.sleep(2000l);
					
					 config = new MqConfig();
					config.setRegistry("zookeeper://192.168.18.130:2181");
					config.setRegistryGroup("mq-service");
					config.setApplication("mq-example");

					config.setClazz(null);
					config.setGroup("user");
					config.setVersion("login");

					config.setRemark("测试");
					config.setRetries(2);

					config.setTopic("USER");
					config.setTag("LOGIN");
					mqConfigService.save(config);
					
					
					
					Thread.sleep(2000l);

				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}, 5000, 2000, TimeUnit.MILLISECONDS);
	}
}
