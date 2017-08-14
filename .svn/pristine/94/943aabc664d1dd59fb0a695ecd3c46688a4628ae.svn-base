/**
 * 
 */
package com.wy.mq.dto;

import java.util.Date;

import com.wy.mq.LtnMqMessage;

/**
 * <pre>
 * producer方发送消息后，对消息记录时对应的数据库行数据
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月27日
 */
public class MessageDBRecord extends LtnMqMessage implements java.io.Serializable {
	public MessageDBRecord() {
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7640934599424729134L;

	public MessageDBRecord(LtnMqMessage ltnMqMessage) {
		this.setTopic(ltnMqMessage.getTopic());
		this.setTag(ltnMqMessage.getTag());
		this.setBody(ltnMqMessage.getBody());
		this.setKey(ltnMqMessage.getKey());
	}

	private Long	id;

	private String	msgId;

	/**
	 * 当前消息被消费次数
	 */
	private Integer	consumeCount	= 0;

	/**
	 * <pre>
	 * 1：未发送 
	 * 2：已经发送成功
	 * 3：已消费
	 * </pre>
	 */
	private Integer	status;

	/**
	 * <pre>
	 * 消息补偿发送重试的次数,重试次数到达最大值时还没有发送成功，则报警<br>
	 * 补偿动作是定时任务执行的
	 * </pre>
	 */
	private Integer	retrySendCount;

	private Date	createTime;

	private Date	updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Integer getConsumeCount() {
		return consumeCount;
	}

	public void setConsumeCount(Integer consumeCount) {
		this.consumeCount = consumeCount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getRetrySendCount() {
		return retrySendCount;
	}

	public void setRetrySendCount(Integer retrySendCount) {
		this.retrySendCount = retrySendCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
