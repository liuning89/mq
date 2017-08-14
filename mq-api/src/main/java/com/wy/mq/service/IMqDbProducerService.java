package com.wy.mq.service;

import java.util.List;

import com.wy.mq.LtnMqMessage;
import com.wy.mq.dto.MessageDBRecord;

public interface IMqDbProducerService {
	/**
	 * 消息发送前（producer.send()），记录消息到数据库，消息状态为“”
	 * 
	 * @author 张克行
	 * @since 2016年10月27日
	 * @param record
	 */
	public MessageDBRecord saveMsg(LtnMqMessage record);

	/**
	 * 消息发送成功后，更新消息状态
	 * 
	 * @author 张克行
	 * @since 2016年10月27日
	 * @param record
	 */
	public void sendOk(MessageDBRecord record, String messageId);

	/**
	 * 查询未发送成功的消息，为了防止消息补偿重发，所以只查询超过1分钟（当前时间-消息创建时间>=1秒）未发送成功的消息
	 * 
	 * @author 张克行
	 * @since 2016年10月27日
	 * @return
	 */
	public List<MessageDBRecord> queryNewMsgList();

	/**消息补偿的回调
	 * @author 张克行
	 * @since 2016年10月31日
	 * @param record
	 */
	void sendOk(MessageDBRecord record);
}
