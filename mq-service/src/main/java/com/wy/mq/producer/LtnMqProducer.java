/**
 * 
 */
package com.wy.mq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.wy.constants.SendBusinessEnum;
import com.wy.email.api.EmailSendService;
import com.wy.mq.LtnMqMessage;
import com.wy.mq.MqThreadPoolExecutor;
import com.wy.mq.dto.MessageDBRecord;
import com.wy.mq.service.IMqDbProducerService;

/**
 * <pre>
 * 消息生产
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月27日
 */
public class LtnMqProducer {
	private static Logger			log	= LoggerFactory.getLogger(LtnMqProducer.class);

	@Autowired
	private IMqDbProducerService	mqDbProducerService;

	@Autowired
	private DefaultMQProducer		defaultMQProducer;

	@Autowired
	private EmailSendService		emailSendService;

	private String instanceName;
	
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public IMqDbProducerService getMqDbProducerService() {
		return mqDbProducerService;
	}

	public void setMqDbProducerService(IMqDbProducerService mqDbProducerService) {
		this.mqDbProducerService = mqDbProducerService;
	}

	public DefaultMQProducer getDefaultMQProducer() {
		return defaultMQProducer;
	}

	public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) throws MQClientException {
		this.defaultMQProducer = defaultMQProducer;
		this.defaultMQProducer.setInstanceName(this.instanceName+System.currentTimeMillis());
		this.defaultMQProducer.start();
	}

	/**
	 * 异步发送消息，发送失败自动重试，不重试其它broker，需要依赖 消息补偿机进行补偿
	 * 
	 * @author 张克行
	 * @since 2016年10月27日
	 * @param ltnMqMessage
	 */
	public void asyncSend(final LtnMqMessage ltnMqMessage) {
		MqThreadPoolExecutor.getDefaultThreadPoolExecutor().submit(new Runnable() {

			@Override
			public void run() {
				Message msg = copy(ltnMqMessage);
				// 新消息入库备档
				final MessageDBRecord dbRecord = mqDbProducerService.saveMsg(ltnMqMessage);

				try {
					log.info("开始发送消息{}", ltnMqMessage);
					defaultMQProducer.send(msg, new SendCallback() {

						@Override
						public void onSuccess(SendResult sendResult) {
							mqDbProducerService.sendOk(dbRecord, sendResult.getMsgId());
						}

						@Override
						public void onException(Throwable e) {
							log.error(e.getMessage(), e);
						}
					});

				} catch (Exception e) {
					log.error("{}消息发送失败\n{}", ltnMqMessage, e);
					emailSendService.systemEmailSend("发送MQ消息异常", ltnMqMessage.toString(), SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
				}
			}
		});
	};

	/**
	 * <pre>
	 * 同步发送，发送失败自动重试,
	 * 当broker出现问题时，并开启重试其它broker选项时，会自动重试其它broker，仍失败时会收到异常，则需要报警
	 * </pre>
	 * 
	 * @author 张克行
	 * @since 2016年10月27日
	 * @param ltnMqMessage
	 */
	public void send(final LtnMqMessage ltnMqMessage) {

		MqThreadPoolExecutor.getDefaultThreadPoolExecutor().submit(new Runnable() {

			@Override
			public void run() {
				Message msg = copy(ltnMqMessage);

				SendResult sendResult = null;
				String messageId = null;
				// 新消息入库备档
				MessageDBRecord dbRecord = mqDbProducerService.saveMsg(ltnMqMessage);
				try {
					log.info("开始发送消息{}", ltnMqMessage);
					sendResult = defaultMQProducer.send(msg);
				} catch (Exception e) {
					log.error("{}消息发送失败\n{}", ltnMqMessage, e);
					emailSendService.systemEmailSend("发送MQ消息异常", ltnMqMessage.toString(), SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
				} finally {
					if (sendResult != null) {
						messageId = sendResult.getMsgId();
						// 消息是否发送成功
						if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
							mqDbProducerService.sendOk(dbRecord, messageId);
						}
					}
				}
			}
		});
	};

	private Message copy(LtnMqMessage ltnMqMessage) {
		return new Message(ltnMqMessage.getTopic(), //
				ltnMqMessage.getTag(), //
				ltnMqMessage.getKey(), //
				ltnMqMessage.getBody().getBytes());
	}
}
