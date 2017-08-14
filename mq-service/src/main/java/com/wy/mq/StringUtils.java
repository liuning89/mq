/**
 * 
 */
package com.wy.mq;

import com.wy.mq.dto.MqConfig;

/**
 *
 * @author 张克行
 * @since 2016年11月2日
 */
public class StringUtils {
	public static String concat(String topic, String tag) {
		return topic + "_" + tag;
	}

	public static String getConsumerKey(MqConfig config) {
		return concat(config.getTopic(), config.getTag());
	}

	public static String getConsumerKey(LtnMqMessage msg) {
		return concat(msg.getTopic(), msg.getTag());
	}

	public static String getServiceKey(MqConfig config) {
		return concat(config.getRegistryGroup(), concat(config.getGroup(), config.getVersion()));
	}
}
