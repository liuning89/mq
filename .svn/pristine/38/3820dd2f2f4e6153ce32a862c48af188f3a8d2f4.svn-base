/**
 * 
 */
package com.wy.mq.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.mq.consumer.MqConsumerFactory;
import com.wy.mq.dao.MessageDBRecordDao;
import com.wy.mq.dto.ConsumeDetail;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月27日
 */
@Service
public class MqDbConsumerService implements IMqDbConsumerService {

	private static final String	TT_MQ_CONSUME_DETAIL	= "TT_MQ_CONSUME_DETAIL";

	@Resource(name = "sequenceService")
	private SequenceService		sequenceService;

	@Autowired
	private MessageDBRecordDao	messageDBRecordDao;

	@Autowired
	private MqConsumerFactory	mqConsumerFactory;

	@Override
	public void saveConsumeDetail(ConsumeDetail detail) {
		detail.setId(sequenceService.getNextSequence(TT_MQ_CONSUME_DETAIL));
		messageDBRecordDao.saveConsumeDetail(detail);
		if (detail.consumeSucceed()) {
			messageDBRecordDao.doneConsumed(detail);
		}
	}

	@Override
	public List<ConsumeDetail> loadFailureConsumerList() {
		List<ConsumeDetail> failures = messageDBRecordDao.loadFailureConsumerList();
		if (failures != null) {
			for (ConsumeDetail consumeDetail : failures) {
				consumeDetail.setMessageDBRecord(messageDBRecordDao.loadMessageDBRecord(consumeDetail));
			}
		}
		return failures;
	}

}
