package com.thomsoncodes.batch.datafilter.listener;

import java.time.Duration;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;

public class StopListener extends StepListenerSupport{
	public static final Logger LOG = LoggerFactory.getLogger(StopListener.class);
	private static final int TIMEOUT = 1; // in minutes (can be made configurable through constructor)

    private StepExecution stepExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void afterChunk(ChunkContext context) { // or afterRead, or afterWrite, etc.
        if (timeout(context)) {
            this.stepExecution.setTerminateOnly();
        }
    }

    private boolean timeout(ChunkContext chunkContext) {
    	LOG.info("----- TIMEOUT-----");
        Date startTime = chunkContext.getStepContext().getStepExecution().getJobExecution().getStartTime();
        Date now = new Date();
        return Duration.between(startTime.toInstant(), now.toInstant()).toMinutes() > TIMEOUT;
    }

}
