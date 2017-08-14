package com.wy.mq.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.wy.mq.LtnMqMessage;
import com.wy.mq.MqThreadPoolExecutor;
import com.wy.mq.StringUtils;
import com.wy.mq.consumer.AbstractMqConsumer;
import com.wy.mq.consumer.IMqConsumerService;
import com.wy.mq.discover.ConsumerContext;
import com.wy.mq.dto.ConsumeDetail;
import com.wy.mq.dto.MqConfig;

/**
 * <pre>
 * 消息消费
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月28日
 */
public class MqConsumerFactoryTest {
	private static Logger			logger	= LoggerFactory.getLogger(MqConsumerFactoryTest.class);

	private static ConsumerContext	context	= ConsumerContext.getInstance();

	// RocketMQ Name Server Addr
	private String					namesrvAddr="192.168.18.194:9876";

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	/**
	 * 系统启动时进行初始化
	 * 
	 * @author 张克行
	 * @since 2016年11月1日
	 * @throws MQClientException
	 * 
	 * id	clazz	application	registry	registryGroup	topic	tag	group	version	createTime	updateTime	remark
6	com.wy.mq.consumer.IMqConsumerService	market-pc-provider	zookeeper://localhost:2181	pctemp	ORDER	ORDER_ORDER_SUCCESS	pctemp	oldwithnew	2016-10-31 18:20:49	2016-10-31 18:20:52	下单时默认配置
	 */
	private void init() throws MQClientException {
		// 加载全部mq config信息，进行JVM进程缓存
		List<MqConfig> list = new ArrayList<MqConfig>();
		MqConfig config = new MqConfig();
		config.setClazz("com.wy.mq.consumer.IMqConsumerService");
		config.setApplication("market-pc-provider");
		config.setRegistry("zookeeper://192.168.18.194:2181");
		config.setRegistryGroup("pctemp");
		config.setTopic("ORDER");
		config.setTag("ORDER_ORDER_SUCCESS");
		config.setGroup(null);
		config.setVersion("oldwithnew");
		list.add(config);
		ConsumerContext.getInstance().init(list);

		// 加载所有TOPIC TAG信息，RocketMQ Consumer实例数 == TAG数量
		List<MqConfig> configList = new ArrayList<MqConfig>();
		configList.add(config);
		for (MqConfig mqConfig : configList) {
			String consumerKey = StringUtils.getConsumerKey(mqConfig);
			ConsumerContext.getConsumers().putIfAbsent(consumerKey, newConsumer(mqConfig));
		}

		start();
	}

	public AbstractMqConsumer newConsumer(String topic, String tag) {

		return new AbstractMqConsumer(topic, tag) {


			@Override
			public void doBusiness(LtnMqMessage t, ConsumeDetail detail) {
				try {
					callRemoteService(t, detail);
				} finally {

				}
			}
		};

	}

	public AbstractMqConsumer newConsumer(MqConfig mqConfig) {
		return newConsumer(mqConfig.getTopic(), mqConfig.getTag());
	}

	/**
	 * 调用远程服务消费当前消息
	 * 
	 * @author 张克行
	 * @since 2016年11月1日
	 * @param t
	 * @param detail
	 */
	public void callRemoteService(final LtnMqMessage t, final ConsumeDetail detailParam) {
		// 获取订阅当前消息对应的远程服务列表，然后遍历调用
		Map<String, IMqConsumerService> services = context.getRemoteServices(t);
		if (services == null) {
			logger.warn("消费消息时{}，无远程服务订阅", t);
			return;
		}
		for (Entry<String, IMqConsumerService> service : services.entrySet()) {
			// registryKey_serviceGroup_serviceVersion
			// 请特别注意serviceKey.split("_")
			final String serviceKey = service.getKey();
			final IMqConsumerService remoteService = service.getValue();
			String[] serviceGroupVersion = serviceKey.split("_");
			String group = serviceGroupVersion[1];
			String version = serviceGroupVersion[2];
			
			final ConsumeDetail detail = new ConsumeDetail();
			detail.setMessageId(detailParam.getMessageId());
			detail.setGroup(group);
			detail.setVersion(version);
			
			Runnable task = new Runnable() {
				@Override
				public void run() {
					Boolean isOk = false;
					boolean consumed = true;
					try {
						if (!consumed) {
							return;
						}
						isOk = remoteService.doHandler(t);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					} finally {
						if (consumed) {
						}
					}
				}
			};
			MqThreadPoolExecutor.getDefaultThreadPoolExecutor().execute(task);

		}
	}

	public void start() throws MQClientException {
		for (Entry<String, AbstractMqConsumer> entry : ConsumerContext.getConsumers().entrySet()) {
			AbstractMqConsumer abstractMqConsumer = entry.getValue();
			start(abstractMqConsumer);
		}
	}

	public void start(AbstractMqConsumer abstractMqConsumer) throws MQClientException {
		DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
		defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
		abstractMqConsumer.setDefaultMQPushConsumer(defaultMQPushConsumer);
		abstractMqConsumer.start();
	}

	public static void main(String[] args) throws MQClientException {
		MqConsumerFactoryTest test = new MqConsumerFactoryTest();
		test.init();
	}
}
