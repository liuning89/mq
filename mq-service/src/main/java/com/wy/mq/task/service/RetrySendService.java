/**
 * 
 */
package com.wy.mq.task.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.wy.constants.SendBusinessEnum;
import com.wy.email.api.EmailSendService;
import com.wy.mq.dto.MessageDBRecord;
import com.wy.mq.service.IMqDbProducerService;

/**
 * <pre>
 * 消息发送失败补偿
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月31日
 */
@Service
public class RetrySendService implements IRetrySendService {
	private static Logger			logger				= LoggerFactory.getLogger(RetrySendService.class);

	@Autowired
	private IMqDbProducerService	mqDbProducerService;

	@Autowired
	private DefaultMQProducer		mqProducer;

	@Autowired
	private EmailSendService		emailSendService;

	@Autowired
	private JedisPool				jedisPool;

	private static final String		MQ_RETRY_SEND_KEY	= "MQ_Retry_Send_Key";

	public void execute() {
		Jedis jedis = jedisPool.getResource();
		Boolean isSaved = Boolean.FALSE;
		if (jedis != null) {
			Long saved = jedis.setnx(MQ_RETRY_SEND_KEY, "1");
			if (saved == 1) {
				isSaved = jedis.expire(MQ_RETRY_SEND_KEY, 5) == 1;
			}
		}
		if (!isSaved) {
			return; // 严禁并发重复发送
		}
		try {
			logger.info("RetrySendService 被调度");
			List<MessageDBRecord> newMsgList = mqDbProducerService.queryNewMsgList();
			if (newMsgList == null || newMsgList.size() == 0)
				return;

			for (MessageDBRecord messageDBRecord : newMsgList) {
				Message msg = new Message(messageDBRecord.getTopic(), //
						messageDBRecord.getTag(), //
						messageDBRecord.getKey(),//
						messageDBRecord.getBody().getBytes());
				SendResult sendResult = mqProducer.send(msg);
				if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
					messageDBRecord.setMsgId(sendResult.getMsgId());
					mqDbProducerService.sendOk(messageDBRecord);
				}
			}
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			emailSendService.systemEmailSend("MQ消息发送失败补偿异常", e1.getMessage(), SendBusinessEnum.SEND_BUSINESS_MANAGE_INFO);
		} finally {
			jedis.del(MQ_RETRY_SEND_KEY);
			jedis.close();
		}
	}
}
