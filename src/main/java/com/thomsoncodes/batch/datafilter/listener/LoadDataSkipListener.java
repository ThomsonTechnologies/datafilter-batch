package com.thomsoncodes.batch.datafilter.listener;

import org.springframework.batch.core.SkipListener;

import com.thomsoncodes.batch.datafilter.dto.UserInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;

public class LoadDataSkipListener implements SkipListener<UserInfo, UserInfo>{
	
	public static final Logger LOG = LogManager.getLogger(LoadDataSkipListener.class);

	@Override
	public void onSkipInRead(Throwable t) {
//		LOG.info("ItemWriter:  ");
		
	}

	@Override
	public void onSkipInWrite(UserInfo item, Throwable t) {
//		LOG.info(">>> onSkipInWrite <<<");
		MDC.put("item_log", String.valueOf(item.getUserId()));
		
	}

	@Override
	public void onSkipInProcess(UserInfo item, Throwable t) {
//		LOG.info(">>> onSkipInProcess<<< ");
		
	}

	

}
