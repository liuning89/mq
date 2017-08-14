package com.wy.mq.dao;

import java.util.List;

import com.wy.mq.dto.ConsumeDetail;
import com.wy.mq.dto.MessageDBRecord;

public interface MessageDBRecordDao {

	/**
	 * 存储msg纪录
	 * 
	 * @param dbRecord
	 */
	public void saveMsgRecord(MessageDBRecord dbRecord);

	/**
	 * 更新msg纪录
	 * 
	 * @param record
	 */
	public void updateMsgRecord(MessageDBRecord record);

	/**
	 * @author 张克行
	 * @since 2016年10月28日
	 * @param detail
	 * @return
	 */
	public int updateCompleteMsg(ConsumeDetail detail);

	/**
	 * @author 张克行
	 * @since 2016年10月28日
	 * @param detail
	 */
	public void doneConsumed(ConsumeDetail detail);

	/**
	 * 查询未发送成功的消息
	 * 
	 * @author 张克行
	 * @since 2016年10月31日
	 * @return
	 */
	public List<MessageDBRecord> queryNewMsgList();

	/**
	 * 提 借给消息补偿回调使用
	 * 
	 * @author 张克行
	 * @since 2016年10月31日
	 * @param id
	 * @param msgId
	 */
	public void sendOk(MessageDBRecord record);

	/**
	 * @author 张克行
	 * @since 2016年11月1日
	 * @param detail
	 */
	public void saveConsumeDetail(ConsumeDetail detail);

	/**
	 * @author 张克行
	 * @since 2016年11月1日
	 * @return
	 */
	public List<ConsumeDetail> loadFailureConsumerList();

	/**
	 * @author 张克行
	 * @since 2016年11月1日
	 * @param consumeDetail
	 * @return
	 */
	public MessageDBRecord loadMessageDBRecord(ConsumeDetail consumeDetail);

}
