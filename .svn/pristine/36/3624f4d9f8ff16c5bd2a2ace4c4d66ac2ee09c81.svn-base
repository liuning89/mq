/**
 * 
 */
package com.wy.mq.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.mq.LtnMqMessage;
import com.wy.mq.dao.MessageDBRecordDao;
import com.wy.mq.dto.MessageDBRecord;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月27日
 */
@Service
public class MqDbProducerService implements IMqDbProducerService {

	private static final String	MSG_RECORD_SEQUENCE	= "TT_MQ_MESSAGEDB_RECORD";

	@Resource(name = "sequenceService")
	private SequenceService		sequenceService;

	@Autowired
	private MessageDBRecordDao	messageDBRecordDao;

	@Override
	public MessageDBRecord saveMsg(LtnMqMessage record) {
		MessageDBRecord dbRecord = new MessageDBRecord(record);
		dbRecord.setStatus(1);
		dbRecord.setId(sequenceService.getNextSequence(MSG_RECORD_SEQUENCE));
		messageDBRecordDao.saveMsgRecord(dbRecord);
		return dbRecord;
	}

	@Override
	public void sendOk(MessageDBRecord record, String messageId) {
		record.setMsgId(messageId);
		record.setStatus(2);
		record.setConsumeCount(0);// 消费次数初始为0

		messageDBRecordDao.updateMsgRecord(record);
	}

	@Override
	public List<MessageDBRecord> queryNewMsgList() {
		return messageDBRecordDao.queryNewMsgList();
	}

	@Override
	public void sendOk(MessageDBRecord record) {
		messageDBRecordDao.sendOk(record);
	}

}
