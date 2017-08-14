/**
 * 
 */
package com.wy.mq.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 消费被消费的具体情况
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月27日
 */
public class ConsumeDetail implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2621268745005055442L;

	private Long				id;

	private String				messageId;

	private Date				createTime;

	private Date				updateTime;

	/**
	 * <pre>
	 * 1:已经接收到消息，开始消费
	 * 2：消费成功
	 * 3：消费失败
	 * </pre>
	 */
	private Integer				status;

	/**
	 * 远程服务版本
	 */
	private String				version;

	/**
	 * 远程服务分组
	 */
	private String				group;

	private MessageDBRecord		messageDBRecord;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public boolean emptyMessageId() {
		return this.messageId == null || this.messageId.trim().length() < 1;
	}

	public boolean consumeSucceed() {
		return this.status.intValue() == 2;
	}

	public boolean consumeFailure() {
		return this.status.intValue() == 3;
	}

	public MessageDBRecord getMessageDBRecord() {
		return messageDBRecord;
	}

	public void setMessageDBRecord(MessageDBRecord messageDBRecord) {
		this.messageDBRecord = messageDBRecord;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConsumeDetail [id=");
		builder.append(id);
		builder.append(", messageId=");
		builder.append(messageId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", version=");
		builder.append(version);
		builder.append(", group=");
		builder.append(group);
		builder.append("]");
		return builder.toString();
	}

}
