/**
 * 
 */
package com.wy.mq.discover;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.wy.mq.LtnMqMessage;
import com.wy.mq.StringUtils;
import com.wy.mq.consumer.AbstractMqConsumer;
import com.wy.mq.consumer.IMqConsumerService;
import com.wy.mq.consumer.MqConsumerFactory;
import com.wy.mq.dto.MqConfig;

/**
 *
 * @author 张克行
 * @since 2016年10月31日
 */
public class ConsumerContext {
	private static Logger														logger			= LoggerFactory.getLogger(ConsumerContext.class);

	/**
	 * 缓存所有的RocketMQ consumer实例
	 */
	private static ConcurrentHashMap<String, AbstractMqConsumer>				consumers		= new ConcurrentHashMap<String, AbstractMqConsumer>();

	/**
	 * <pre>
	 * TOPIC+TAG
	 * 		GROUP+VERSION
	 * 			IMqConsumerService
	 * 
	 * 消息存在一对多关系，单一消息，多个业务方感兴趣
	 * 
	 * TOPIC + TAG 
	 * 		|registryGroup+GROUP+VERSION
	 * 		|————|IMqConsumerService
	 * 		|————|...
	 * 		|
	 * 		|registryGroup+GROUP+VERSION
	 *  	|————|IMqConsumerService
	 *  	|————|...
	 * 		| 
	 * 		|registryGroup+GROUP+VERSION
	 *  	|————|IMqConsumerService
	 *  	|————|...
	 *  	|
	 * 		|registryGroup+GROUP+VERSION	
	 *  	|————|IMqConsumerService
	 *  	|————|...
	 * </pre>
	 */
	private static ConcurrentHashMap<String, Map<String, IMqConsumerService>>	servicesCache	= new ConcurrentHashMap<String, Map<String, IMqConsumerService>>();

	/**
	 * dubbo 注册中心
	 */
	private static ConcurrentHashMap<String, RegistryConfig>					registries		= new ConcurrentHashMap<String, RegistryConfig>();

	/**
	 * dubbo 应用
	 */
	private static Map<String, ApplicationConfig>								applications	= new ConcurrentHashMap<String, ApplicationConfig>();

	private static ConsumerContext												context;

	private ConsumerContext() {
	}

	static {
		context = new ConsumerContext();
	}

	public static ConcurrentHashMap<String, AbstractMqConsumer> getConsumers() {
		return consumers;
	}

	public static ConsumerContext getInstance() {
		return context;
	}

	public synchronized void init(List<MqConfig> configList) {
		for (MqConfig config : configList) {
			init(config);
		}
	}

	/**
	 * 初始化远程服务与消息的订阅关系，如果远程服务未订阅过，立即订阅
	 * 
	 * @author 张克行
	 * @since 2016年11月2日
	 * @param serviceStubs
	 * @param mqConsumerFactory
	 */
	public synchronized void init(List<MqConfig> serviceStubs, MqConsumerFactory mqConsumerFactory) {
		for (MqConfig config : serviceStubs) {
			if (init(config)) {
				String topic = config.getTopic(), tag = config.getTag();
				mqConsumerFactory.subscribe(topic, tag);
			}
		}

		// 取消订阅处理
		for (Entry<String, Map<String, IMqConsumerService>> consumerEntry : servicesCache.entrySet()) {
			// 取得topic+tag下的所有之前订阅者
			Map<String, IMqConsumerService> services = consumerEntry.getValue();

			for (Entry<String, IMqConsumerService> serviceEntry : services.entrySet()) {

				String consumerKey = consumerEntry.getKey();
				String serviceKey = serviceEntry.getKey();

				boolean canUnsubscribe = true;
				// 遍历本次加载后的所有配置信息
				for (MqConfig config : serviceStubs) {
					String thisServicekey = StringUtils.getServiceKey(config);
					String thisConsumerKey = StringUtils.getConsumerKey(config);
					if (consumerKey.equals(thisConsumerKey) && serviceKey.equals(thisServicekey)) {
						canUnsubscribe = false;
						break;
					}
				}

				if (canUnsubscribe) {
					unSubscribe(consumerKey,serviceKey);
				}
			}
		}

	}

	/**
	 * 初始化远程服务与消息的订阅绑定
	 * 
	 * @author 张克行
	 * @since 2016年11月2日
	 * @param config
	 * @return false 表明远程服务已经订阅过消息同样的topic+tag消息
	 */
	public synchronized boolean init(MqConfig config) {
		String group = config.getGroup();// 服务分组
		String version = config.getVersion();// 服务版本
		String servicekey = StringUtils.getServiceKey(config);
// TODO: 2017/4/7  
		// 检查服务是否已经存在
		String consumerKey = StringUtils.getConsumerKey(config);
		Map<String, IMqConsumerService> services = servicesCache.get(consumerKey);
		if (services != null && services.keySet().contains(servicekey)) {
			return false;
		}

		// 检查注册中心是否已经加载
		RegistryConfig registryConfig = registries.get(config.getRegistry()+"#"+config.getRegistryGroup());
		if (registryConfig == null) {
			registryConfig = new RegistryConfig(config.getRegistry());
			registryConfig.setGroup(config.getRegistryGroup());
			registries.put(config.getRegistry(), registryConfig);
		}

		if (services == null) {
			services = new ConcurrentHashMap<String, IMqConsumerService>();
		}

		// 检查dubbo 应用是否已经加载
		ApplicationConfig applicationConfig = applications.get(config.getApplication());
		if (applicationConfig == null) {
			applicationConfig = new ApplicationConfig(config.getApplication());
			applications.put(config.getApplication(), applicationConfig);
		}

		// 实例化远程服务
		ReferenceConfig<IMqConsumerService> referenceConfig = new ReferenceConfig<IMqConsumerService>();
		referenceConfig.setRegistry(registryConfig);
		referenceConfig.setTimeout(60000);
		referenceConfig.setProtocol("dubbo");
		referenceConfig.setInterface(IMqConsumerService.class);
		referenceConfig.setGroup(group);
		referenceConfig.setApplication(applicationConfig);
		referenceConfig.setVersion(version);
		referenceConfig.setRetries(config.getRetries());
		referenceConfig.setCheck(false);
		IMqConsumerService service = referenceConfig.get();

		// 缓存远程服务
		services.put(servicekey, service);
		servicesCache.put(consumerKey, services);

		return true;
	}

	/**
	 * 从缓存获取需要消费当前消息的远程服务
	 * 
	 * @author 张克行
	 * @since 2016年10月31日
	 * @param config
	 * @return
	 */
	public Map<String, IMqConsumerService> getRemoteServices(LtnMqMessage msg) {
		Map<String, IMqConsumerService> consumers = servicesCache.get(StringUtils.getConsumerKey(msg));
		return consumers;
	}

	/**
	 * <pre>
	 * 数据库配置删除时，需要调用这个方法，解除消息订阅
	 * @author 张克行
	 * @since 2016年11月1日
	 * @param topic	RocketMQ topic
	 * @param tag	RocketMQ tag	
	 * @param group	远程服务分组
	 * @param version	远程服务版本
	 * </pre>
	 */
	public void unSubscribe(String consumerKey, String servicekey) {

		Map<String, IMqConsumerService> services = servicesCache.get(consumerKey);
		if (services == null || services.size() == 0) {
			logger.warn("订阅{}的远程服务为空,即将调用consumer.shutdown()", consumerKey);
			AbstractMqConsumer consumer = consumers.get(consumerKey);
			if (consumer != null) {
				consumer.shutdown();
				consumers.remove(consumerKey);
			}
			return;
		}

		logger.warn("删除消息订阅,consumerKey={},servicekey={}", consumerKey, servicekey);
		services.remove(servicekey);

		if (services == null || services.size() == 0) {
			logger.warn("订阅{}的远程服务为空,即将调用consumer.shutdown()", consumerKey);
			AbstractMqConsumer consumer = consumers.get(consumerKey);
			if (consumer != null) {
				consumer.shutdown();
				consumers.remove(consumerKey);
			}
			return;
		}

	}

	public void unSubscribe(String topic, String tag, String registryGroup, String group, String version) {
		String consumerKey = StringUtils.concat(topic, tag);
		String servicekey = StringUtils.concat(registryGroup, StringUtils.concat(group, version));
		unSubscribe(consumerKey, servicekey);
	}

}
