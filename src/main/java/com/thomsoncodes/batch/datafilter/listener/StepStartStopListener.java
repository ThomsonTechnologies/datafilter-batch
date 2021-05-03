package com.thomsoncodes.batch.datafilter.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class StepStartStopListener {
	static final Logger LOG = LogManager.getLogger(StepStartStopListener.class);
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		LOG.info(stepExecution.getStepName() + " has begun!");		
	}
	
	@AfterStep
	public ExitStatus afterStep(StepExecution stepExecution) {
		LOG.info(stepExecution.getStepName() + " has ended!");
		
		return stepExecution.getExitStatus();
	}

}
