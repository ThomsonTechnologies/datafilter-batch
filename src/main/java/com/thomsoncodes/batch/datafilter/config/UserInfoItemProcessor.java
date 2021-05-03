package com.thomsoncodes.batch.datafilter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.thomsoncodes.batch.datafilter.dto.UserInfo;

public class UserInfoItemProcessor implements ItemProcessor<UserInfo, UserInfo>{
	public static final Logger LOG = LoggerFactory.getLogger(UserInfoItemProcessor.class);

	@Override
	public UserInfo process(UserInfo userInfo) throws Exception {
		
//		String useridTrim = userInfo.getUserId().trim();
//		LOG.info(">>> TRIMMED MATCH: " + useridTrim.equals(userInfo.getUserId()));		
//		useridTrim.stripLeading();
//		LOG.info(">>> TRIMMED MATCH--2: " + useridTrim.equals(userInfo.getUserId()));
		
//		LOG.info(">>> user_id: " + userInfo.getUserId());
		
		return userInfo;
	}

}
