package com.thomsoncodes.batch.datafilter.listener;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class JobLoggerListener {
	
	public static final Logger LOG = LogManager.getLogger(JobLoggerListener.class);
	
	private static String START_MESSAGE = ">>> %s is beggining execution";
	private static String END_MESSAGE = ">>> %s has completed with status: %s";
	
	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		LOG.info(String.format(START_MESSAGE, jobExecution.getJobInstance().getJobName()));
	}
	
	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		LOG.info(String.format(END_MESSAGE, 
				jobExecution.getJobInstance().getJobName(),
				jobExecution.getStatus()));
	}


}
