/**
 * 
 */
package com.wy.mq.consumer;

import com.wy.mq.LtnMqMessage;

/**
 * <pre>
 * 消息消费
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月28日
 */
public interface IMqConsumerService {

	public Boolean doHandler(LtnMqMessage message);
}
