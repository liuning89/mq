/**
 * 
 */
package com.wy.mq.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.mq.LtnMqMessage;

/**
 * <pre>
 * 发送消息远程服务
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月31日
 */
@Service
public class MqProducerService implements IMqProducerService {

	@Autowired
	private LtnMqProducer ltnMqProducer;
	
	@Override
	public void send(LtnMqMessage ltnMqMessage) {
		ltnMqProducer.send(ltnMqMessage);
	}

	@Override
	public void asyncSend(LtnMqMessage ltnMqMessage) {
		ltnMqProducer.asyncSend(ltnMqMessage);
	}

}
