package com.wy.mq.service;

import java.util.List;

import com.wy.mq.dto.ConsumeDetail;

public interface IMqDbConsumerService {

	
	/**
	 * @author 张克行
	 * @since 2016年11月1日
	 * @param detail
	 */
	public void saveConsumeDetail(ConsumeDetail detail);

	/**
	 * @author 张克行
	 * @since 2016年11月1日
	 * @return
	 */
	public List<ConsumeDetail> loadFailureConsumerList();

}
