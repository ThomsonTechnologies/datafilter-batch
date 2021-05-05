package com.thomsoncodes.batch.datafilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.thomsoncodes.batch.datafilter.utils.ApplicationConstants;

@SpringBootApplication
@EnableScheduling
public class DatafilterBatchApplication implements CommandLineRunner{
	public static final Logger LOG = LogManager.getLogger(DatafilterBatchApplication.class);
	
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private JobRepository jobRepository;
 
	@Autowired
	private JobRegistry jobRegistry;
	
	@Autowired
	private JobOperator jobOperator;
 
	@Autowired
	private JobExplorer jobExplorer;
	
	private String jobName;
	private JobParameters jobParameters;
	private String completionStatus;
	boolean isRunning = false;
	private String filePath;	
	
	public static void main(String[] args) {
		SpringApplication.run(DatafilterBatchApplication.class, args);
	}
	
	@Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }


	@Override
	public void run(String... args) throws Exception {
		LOG.info("---Beginning of run()---");
		
		try {
			if(args.length == 2) {
				jobName = args[0];
				filePath = args[1];				
				
				ExitStatus exitStatus = jobController(jobName, filePath);
				LOG.info("Job completed with status-" + exitStatus);
			}else {
				LOG.info("Invalid Job Parameters!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.info("---End of run()---");
	}
	
	
	public ExitStatus jobController(String jobName, String fileName) {
		LOG.info("---Beginning of jobController()---");
		
		Job job = this.context.getBean(jobName, Job.class);
		ExitStatus exitStatus = ExitStatus.UNKNOWN;	
		
		jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString("inputFile", fileName)
				.toJobParameters();
		
		try {
			isRunning = true;						
			exitStatus = jobLauncher.run(job, jobParameters).getExitStatus();
			
			if(exitStatus.getExitCode().equals(ApplicationConstants.JOB_EXITSTATUS_STOPPED)) {				
				isRunning = false;
			}
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			
			LOG.info("Error in launching job!!");
			isRunning = false;
			e.printStackTrace();
		}
		
		LOG.info("---End of jobController()---");
		return exitStatus;
	}
	

	@Scheduled(cron = "*/10 * * * * *")
	public void batchScheduler() {
		LOG.info("---Beginning of batchScheduler()---");
		
		if(isRunning) {
			
			try {
				LOG.info("....stopping the job!");
								
				this.isRunning = false;
				jobOperator.stop(jobRepository.getLastJobExecution(jobName, this.jobParameters).getId());
				
			} catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
				
				this.isRunning = true;
				LOG.info("Error in Stopping job!!");
				e.printStackTrace();
			}
		}else {
			
			try {
				LOG.info("Restarting the job....");
								
				this.isRunning = true;
				jobOperator.restart(jobRepository.getLastJobExecution(jobName, this.jobParameters).getId());
				
			} catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException
					| JobRestartException | JobParametersInvalidException e) {
				
				this.isRunning = false;
				LOG.info("Error in Restarting the job!!");
				e.printStackTrace();
			}
		}		
		
		LOG.info("---End of batchScheduler()---");
	}

	@Scheduled(cron = "*/15 * * * * *")
	public void batchScheduler2() {
		LOG.info("---=========test scheduler=========---");
	}

}
