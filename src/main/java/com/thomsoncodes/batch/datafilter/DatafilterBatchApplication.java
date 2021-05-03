package com.thomsoncodes.batch.datafilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
//@EnableScheduling
public class DatafilterBatchApplication implements CommandLineRunner{
	public static final Logger LOG = LogManager.getLogger(DatafilterBatchApplication.class);
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobOperator jobOperator;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	@Autowired
	private JobRegistry jobRegistry;
	
	long jobExecutionId;
	
	boolean isRunning = false;
	boolean isNotRunning = false;
	private String batchName;
	private String filePath;
	private long currentMillis = System.currentTimeMillis();
	private JobParameters jobParameter;
	
	
	public static void main(String[] args) {
		SpringApplication.run(DatafilterBatchApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		LOG.info("---Beginning of run()---");		
		
		try {
			if(args.length == 2) {
				batchName = args[0];
				filePath = args[1];
				
				jobParameter = new JobParametersBuilder()
						.addLong("time", currentMillis)
						.addString("inputFile", filePath)
						.toJobParameters();
				
				ExitStatus exitStatus = jobController(jobParameter);
				LOG.info("Job completed with status-" + exitStatus);
			}else {
				LOG.info("Invalid Job Parameters!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.info("---End of run()---");
	}
	
	
	public ExitStatus jobController(JobParameters jobParameters) {
		LOG.info("---Beginning of jobController()---");
		
		Job job = this.context.getBean("dataFilterJob", Job.class);
		ExitStatus exitStatus = ExitStatus.UNKNOWN;	
		
		try {
			isRunning = true;						
			exitStatus = jobLauncher.run(job, jobParameters).getExitStatus();			
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			
			LOG.info("Error in launching job!!");
			e.printStackTrace();
		}
		
		LOG.info("---End of jobController()---");
		return exitStatus;
	}
	

//	@Scheduled(cron = "*/10 * * * * *")
	public void batchScheduler() {
		LOG.info("---Beginning of batchScheduler()---");
				
		if(isRunning) {
			try {
				LOG.info("....stopping the job!");				
				
				LOG.info("-----------> 2");				
				jobExecutionId = jobRepository.getLastJobExecution("dataFilterJob", jobParameter).getId();
				LOG.info("##### ExecutionID-1: " + jobExecutionId);
				
				this.isRunning = false;
				this.jobOperator.stop(jobExecutionId);
				
			} catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
				
				this.isRunning = true;
				LOG.info("Error in Stopping job!!");
				e.printStackTrace();
			}
		}
		else {
			try {
				LOG.info("Restarting the job....");
				
				LOG.info("-----------> 3");	
				LOG.info("##### ExecutionID-2: " + jobExecutionId);
				
				this.isRunning = true;
				this.jobOperator.restart(jobExecutionId);
				
			} catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException
					| JobRestartException | JobParametersInvalidException e) {
				
				this.isRunning = false;
				LOG.info("Error in Restarting the job!!");
				e.printStackTrace();
			}
		}
		
		LOG.info("---End of batchScheduler()---");
	}


}
