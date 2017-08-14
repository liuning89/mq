package com.wy.mq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.common.api.IdWorker;

@Service("sequenceService")
public class SequenceService {

	@Autowired
	IdWorker worker;

	public Long getNextSequence(String tabelName) {
		return worker.getNextSequence(tabelName);
	}
}
