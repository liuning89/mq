/**
 * 
 */
package com.wy.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wy.mq.LtnMqMessage;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年11月1日
 */
@Service
public class UserNew implements IMqConsumerService {
	private static Logger	logger	= LoggerFactory.getLogger(UserNew.class);

	@Override
	public Boolean doHandler(LtnMqMessage message) {
		logger.info(message.toString());
		return true;
	}

}
