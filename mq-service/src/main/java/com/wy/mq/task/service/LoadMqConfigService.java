/**
 * 
 */
package com.wy.mq.task.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.mq.consumer.MqConsumerFactory;
import com.wy.mq.discover.ConsumerContext;
import com.wy.mq.dto.MqConfig;
import com.wy.mq.service.IMqConfigService;

@Service
public class LoadMqConfigService implements ILoadMqConfigService {

	private static Logger		logger	= LoggerFactory.getLogger(LoadMqConfigService.class);

	@Autowired
	private IMqConfigService	mqConfigService;

	@Autowired
	private MqConsumerFactory	mqConsumerFactory;

	public void execute() {
		logger.info("LoadMqConfigService 被调度");
		try {
			List<MqConfig> serviceStubs = mqConfigService.queryConfig();
			ConsumerContext.getInstance().init(serviceStubs, mqConsumerFactory);
		} finally {
		}
	}
}
