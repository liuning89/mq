/**
 * 
 */
package com.wy.mq.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.mq.dao.MqConfigDao;
import com.wy.mq.dto.MqConfig;

/**
 * <pre>
 * 这里描述类的主要功能和业务上的注意点
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月31日
 */
@Service
public class MqConfigService implements IMqConfigService {
	private static final String		TT_MQ_CONFIG	= "TT_MQ_CONFIG";

	@Autowired
	private MqConfigDao				mqConfigDao;

	@Resource(name = "sequenceService")
	private SequenceService			sequenceService;

	@Autowired
	private IMqDbConsumerService	mqDbConsumerService;

	@Override
	public List<MqConfig> queryConfig() {
		return mqConfigDao.queryConfig();
	}

	@Override
	public List<MqConfig> selectTags() {
		return mqConfigDao.selectTags();
	}

	@Override
	public MqConfig save(MqConfig config) {
		config.setId(sequenceService.getNextSequence(TT_MQ_CONFIG));
		mqConfigDao.save(config);
		return config;
	}

	@Override
	public void remove(MqConfig config) {
		mqConfigDao.remove(config);
	}

}
