/**
 * 
 */
package com.wy.mq.consumer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.wy.mq.LtnMqMessage;
import com.wy.mq.dto.ConsumeDetail;

/**
 * <pre>
 * 消息消费
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月28日
 */
public abstract class AbstractMqConsumer {

	private static Logger			log	= LoggerFactory.getLogger(AbstractMqConsumer.class);

	private DefaultMQPushConsumer	defaultMQPushConsumer;

	private String					topic;

	private String					tags;

	public AbstractMqConsumer() {
	}

	public AbstractMqConsumer(String topic, String tags) {
		this.topic = topic;
		this.tags = tags;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public DefaultMQPushConsumer getDefaultMQPushConsumer() {
		return defaultMQPushConsumer;
	}

	public void setDefaultMQPushConsumer(DefaultMQPushConsumer defaultMQPushConsumer) throws MQClientException {
		this.defaultMQPushConsumer = defaultMQPushConsumer;
		this.defaultMQPushConsumer.setConsumerGroup(getConsumerGroup());
		this.defaultMQPushConsumer.setInstanceName(getTags() + "_" + System.currentTimeMillis());
		this.defaultMQPushConsumer.subscribe(this.topic, this.tags);
	}

	private String getConsumerGroup() {
		return topic + getTags() + "consumerGroup";
	}

	private String getTags() {
		return tags.replaceAll(" ", "").replaceAll("\\|", "").replaceAll("\\*", "");
	}

	public void start() throws MQClientException {
		this.defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {

			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				if (msgs.size() > 0) {
					// Long offset = msgs.get(0).getQueueOffset();
					// String maxOffset=
					// msgs.get(0).getProperty(MessageConst.PROPERTY_MAX_OFFSET);
					// long diff = Long.parseLong(maxOffset) - offset;
					// if (diff > 10000l) {
					// return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					// }
					for (MessageExt messageExt : msgs) {

						ConsumeDetail detail = new ConsumeDetail();
						detail.setMessageId(messageExt.getMsgId());

						try {
							Message msg = (Message) messageExt;

							LtnMqMessage ltnMsg = new LtnMqMessage();
							ltnMsg.setTopic(msg.getTopic());
							ltnMsg.setTag(msg.getTags());
							ltnMsg.setKey(msg.getKeys());
							ltnMsg.setBody(new String(msg.getBody()));

							log.info("开始消费消息{}", ltnMsg);
							// 应用方做业务逻辑
							work(ltnMsg, detail);
						} catch (Exception e) {
							e.printStackTrace();
							log.error(e.getMessage(), e);
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
					}
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

		});
		defaultMQPushConsumer.start();
	}

	private void work(LtnMqMessage t, ConsumeDetail detail) {
		// 消费时是多进程多线程的，防止并发消费同一业务消息，需要锁定业务标识
		doBusiness(t, detail);
	}

	/**
	 * 
	 * @author 张克行
	 * @since 2016年10月28日
	 * @param t
	 * @return true 业务成功结束
	 */
	public abstract void doBusiness(LtnMqMessage t, ConsumeDetail detail);

	public void shutdown() {
		this.defaultMQPushConsumer.shutdown();
	}
}
