/**
 * 
 */
package com.wy.mq.task.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.mq.LtnMqMessage;
import com.wy.mq.consumer.MqConsumerFactory;
import com.wy.mq.dto.ConsumeDetail;
import com.wy.mq.service.IMqDbConsumerService;

@Service
public class ReConsumerService implements IReConsumerService {
	private static Logger			logger	= LoggerFactory.getLogger(ReConsumerService.class);

	@Autowired
	private IMqDbConsumerService	mqDbConsumerService;

	@Autowired
	private MqConsumerFactory		mqConsumerFactory;

	public void execute() {
		logger.info("ReConsumerService 被调度");
		try {
			List<ConsumeDetail> failureList = mqDbConsumerService.loadFailureConsumerList();
			if (failureList == null || failureList.size() == 0)
				return;

			for (ConsumeDetail consumeDetail : failureList) {
				if (consumeDetail.getMessageDBRecord() == null) {
					logger.error("{}未找到对应的之前发送的消息", consumeDetail);
					continue;
				}
				LtnMqMessage ltnMqMessage = new LtnMqMessage();
				ltnMqMessage.setTopic(consumeDetail.getMessageDBRecord().getTopic());
				ltnMqMessage.setTag(consumeDetail.getMessageDBRecord().getTag());
				ltnMqMessage.setKey(consumeDetail.getMessageDBRecord().getKey());
				ltnMqMessage.setBody(consumeDetail.getMessageDBRecord().getBody());
				mqConsumerFactory.callRemoteService(consumeDetail.getMessageDBRecord(), consumeDetail);
			}
		} finally {
		}
	}
}
