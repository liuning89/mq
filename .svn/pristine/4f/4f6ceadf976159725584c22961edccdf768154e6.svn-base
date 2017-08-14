package com.wy.mq.service;

import java.util.List;

import com.wy.mq.dto.MqConfig;

public interface IMqConfigService {
	/**
	 * 从数据库加载消息配置
	 * 
	 * @author 张克行
	 * @since 2016年11月1日
	 * @return
	 */
	public List<MqConfig> queryConfig();

	public List<MqConfig> selectTags();

	/**
	 * 新增订阅
	 * 
	 * @author 张克行
	 * @since 2016年11月2日
	 * @param config
	 * @return
	 */
	public MqConfig save(MqConfig config);

	/**
	 * 取消订阅消息
	 * @author 张克行
	 * @since 2016年11月2日
	 * @param config
	 */
	void remove(MqConfig config);
}
