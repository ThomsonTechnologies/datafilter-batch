package com.thomsoncodes.batch.datafilter.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;


public class UserPurgeChunkListener implements ChunkListener {
	
	public static final Logger LOG = LogManager.getLogger(UserPurgeChunkListener.class);

	@Override
	public void beforeChunk(ChunkContext context) {
		LOG.info("|||Starting Chunk process..");
		
	}

	@Override
	public void afterChunk(ChunkContext context) {
		LOG.error(">>>EXCEPTION processing the Chunk<<<");		
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		
		int count = context.getStepContext().getStepExecution().getReadCount();
        System.out.println("ItemCount: " + count);
        
		LOG.info("...End of Chunk process|||..");		
	}

}
