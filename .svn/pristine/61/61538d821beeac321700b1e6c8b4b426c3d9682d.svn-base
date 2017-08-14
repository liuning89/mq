/**
 * 
 */
package com.wy.mq.consumer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.wy.constants.SendBusinessEnum;
import com.wy.email.api.EmailSendService;
import com.wy.mq.LtnMqMessage;
import com.wy.mq.MqThreadPoolExecutor;
import com.wy.mq.StringUtils;
import com.wy.mq.discover.ConsumerContext;
import com.wy.mq.dto.ConsumeDetail;
import com.wy.mq.dto.MqConfig;
import com.wy.mq.service.IMqConfigService;
import com.wy.mq.service.IMqDbConsumerService;

/**
 * <pre>
 * 消息消费
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月28日
 */
public class MqConsumerFactory {
	private static Logger			logger	= LoggerFactory.getLogger(MqConsumerFactory.class);

	private static ConsumerContext	context	= ConsumerContext.getInstance();

	@Autowired
	private IMqDbConsumerService	mqDbConsumerService;

	@Autowired
	private IMqConfigService		mqConfigService;

	@Autowired
	private EmailSendService		emailSendService;

	// RocketMQ Name Server Addr
	private String					namesrvAddr;

	private JedisPool				jedisPool;

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	/**
	 * 系统启动时进行初始化
	 * 
	 * @author 张克行
	 * @since 2016年11月1日
	 * @throws MQClientException
	 */
	@SuppressWarnings("unused")
	private void init() throws MQClientException {
		// 加载全部mq config信息，进行JVM进程缓存
		List<MqConfig> list = mqConfigService.queryConfig();
		ConsumerContext.getInstance().init(list);

		// 加载所有TOPIC TAG信息，RocketMQ Consumer实例数 == TAG数量
		List<MqConfig> configList = mqConfigService.selectTags();
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
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					pw.close();
					String content = detail.toString() + "<br><br>" + sw.toString();
					emailSendService.systemEmailSend("MQ消息消费时callRemoteService异常", content, SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
				}
			}
		};

	}

	public AbstractMqConsumer newConsumer(MqConfig mqConfig) {
		return newConsumer(mqConfig.getTopic(), mqConfig.getTag());
	}

	private String getRedisKey(ConsumeDetail detail) {
		return String.format("MQ_%s_%s_%s", detail.getMessageId(), detail.getGroup(), detail.getVersion());
	}

	private boolean lock(ConsumeDetail detail) {
		String key = getRedisKey(detail);
		Jedis jedis = jedisPool.getResource();
		if (jedis != null) {
			try {
				if (jedis != null) {
					Long saved = jedis.setnx(key, "1");
					if (saved == 1) {
						return jedis.expire(key, 30) == 1;
					}
				}
				return false;
			} catch (Exception e) {
			} finally {
				jedis.close();
			}
		}
		return false;
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
			String content = String.format("消费消息时%s，无远程服务订阅", t);
			logger.warn(content);
			//// TODO: 2017/4/7  邮件发送次数限制
			emailSendService.systemEmailSend("MQ消息消费时异常", content, SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
			return;
		}
		for (final Entry<String, IMqConsumerService> service : services.entrySet()) {

			String serviceKey = service.getKey();
			String[] serviceGroupVersion = serviceKey.split("_");
			String group = serviceGroupVersion[1];
			String version = serviceGroupVersion[2];
			if(org.apache.commons.lang3.StringUtils.isNotBlank(detailParam.getGroup())
					&&org.apache.commons.lang3.StringUtils.isNotBlank(detailParam.getVersion())){
				// 1.detailParam的group和version不为空时为任务调度
				if(!org.apache.commons.lang3.StringUtils.equals(detailParam.getGroup(),group)
						||!org.apache.commons.lang3.StringUtils.equals(detailParam.getVersion(),version)){
					//2.service对应的group和version与消费者的group和version不一致时，不需要调用
					continue;
				}
			}

			Runnable task = new Runnable() {
				@Override
				public void run() {
					// registryKey_serviceGroup_serviceVersion
					// 请特别注意serviceKey.split("_")
					String serviceKey = service.getKey();
					IMqConsumerService remoteService = service.getValue();
					String[] serviceGroupVersion = serviceKey.split("_");
					String group = serviceGroupVersion[1];
					String version = serviceGroupVersion[2];
					
					ConsumeDetail detail = new ConsumeDetail();
					detail.setMessageId(detailParam.getMessageId());
					detail.setGroup(group);
					detail.setVersion(version);

					Boolean isOk = false;
					boolean consumed = true;
					long start = System.currentTimeMillis();
					try {
						// 消息的单个远程消费者防并发处理
						consumed = lock(detail);
						if (!consumed) {
							// TODO: 2017/1/18  
							//emailSendService.systemEmailSend("MQ发生疑似重复消费，已经被拦截", detail.toString(), SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
							return;
						}
						isOk = remoteService.doHandler(t);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);

						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						pw.close();
						String content = detail.toString() + "<br><br>" + sw.toString();
						// TODO: 2017/4/7  邮件发送次数限制
						emailSendService.systemEmailSend("MQ消息消费时异常", content, SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
					} finally {
						if (consumed) {
							// 单一应用方消息消费成功 or 失败
							detail.setStatus((isOk != null && isOk) ? 2 : 3);
							mqDbConsumerService.saveConsumeDetail(detail);
						
							long end = System.currentTimeMillis();
							if (!isOk) {
								logger.error(String.format("MQ消息消费失败,消息：%s,耗时：%s,远程服务%s", detail.toString(),end-start,remoteService.toString()));
							}else{
								String remoteServiceUrl="";
								try{
									Field handler = remoteService.getClass().getDeclaredField("handler");
									handler.setAccessible(true);
							        Object o = handler.get(remoteService);
							        
							        Field invoker = o.getClass().getDeclaredField("invoker");
							        invoker.setAccessible(true);
							        Object url = invoker.get(o);
							        
							        Field directory = url.getClass().getDeclaredField("directory");
							        directory.setAccessible(true);
							        Object registryDirectory = directory.get(url);
							        
							        Field urlInvokerMap =registryDirectory.getClass().getDeclaredField("urlInvokerMap");
							        urlInvokerMap.setAccessible(true);
							        Object urls = urlInvokerMap.get(registryDirectory);
							        
							        remoteServiceUrl = urls.toString();
								}catch(Exception e){}
								
								if(remoteServiceUrl == null || remoteServiceUrl.equals("")){
									remoteServiceUrl=remoteService.toString();
								}
								logger.info(String.format("MQ消息消费成功,消息：%s,耗时：%s,远程服务%s", detail.toString(),end-start,remoteServiceUrl));
							}
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

	/**
	 * 增量启用消息订阅
	 * 
	 * @author 张克行
	 * @since 2016年11月2日
	 * @param topic
	 * @param tag
	 */
	public void subscribe(String topic, String tag) {
		String consumerKey = StringUtils.concat(topic, tag);

		if (!ConsumerContext.getConsumers().containsKey(consumerKey)) {
			AbstractMqConsumer consumer = newConsumer(topic, tag);
			ConsumerContext.getConsumers().//
					putIfAbsent(consumerKey, //
							consumer);
			try {
				start(consumer);
				logger.warn("{}消息消费者启动成功", consumerKey);
			} catch (MQClientException e) {
				String content = String.format("订阅消息失败：topic=%s,tag=%s", topic, tag);
				emailSendService.systemEmailSend("MQ消息订阅异常（MqConsumerFactory.subscribe(String, String)）", content,
						SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
				logger.error(content);
			}
		}
	}
}
