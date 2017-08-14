package com.wy.mq;

public class LtnMqMessage implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8518330901080045241L;

	private String				topic;										// 主题

	private String				tag;										// 标签

	private String				key;										// key，业务主键，心填

	private String				body;										// 消息内容,选填

	/**
	 * @param topic
	 * @param tag
	 * @param key
	 * @param body
	 */
	public LtnMqMessage(String topic, String tag, String key, String body) {
		super();
		this.topic = topic;
		this.tag = tag;
		this.key = key;
		this.body = body;
	}

	public LtnMqMessage() {
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LtnMqMessage [topic=");
		builder.append(topic);
		builder.append(", tag=");
		builder.append(tag);
		builder.append(", key=");
		builder.append(key);
		builder.append(", body=");
		builder.append(body);
		builder.append("]");
		return builder.toString();
	}

}
