package com.thomsoncodes.batch.datafilter.config;


import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.thomsoncodes.batch.datafilter.dto.UserInfo;
import com.thomsoncodes.batch.datafilter.listener.JobLoggerListener;
import com.thomsoncodes.batch.datafilter.listener.StepStartStopListener;
import com.thomsoncodes.batch.datafilter.listener.StopListener;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	public static final Logger LOG = LogManager.getLogger(BatchConfiguration.class);

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
		
	@Autowired
    public DataSource dataSource;
	
	//------------------------------------
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
	
//	@Autowired
//	private JobRegistry jobRegistry;
	
	long jobExecutionId;
	
	boolean isRunning = false;
	boolean isNotRunning = false;
	private String batchName;
	private String filePath;
	private long currentMillis = System.currentTimeMillis();
	private JobParameters jobParameter;
	
//	@Bean
//	   public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
//	       JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
//	       postProcessor.setJobRegistry(jobRegistry);
//	       return postProcessor;
//	   }
	
	
    @Bean
    public Job dataFilterJob() {
        return jobBuilderFactory.get("dataFilterJob")
        		.incrementer(new RunIdIncrementer())
        		.start(dataFilterStep())
        		.listener(new JobLoggerListener())
//        		.listener(new StopListener())
                .build();
    }
    
    @Bean
    public Step dataFilterStep() {
        return stepBuilderFactory.get("dataFilterStep")
                .<UserInfo, UserInfo> chunk(10)
                .reader(dataFilterItemReader())
                .processor(dataFilterItemProcessor())
                .writer(dataFilterWriter())
               // .listener(new StopListener())
                .listener(new StepStartStopListener())                
                .build();
    }
    
    @Bean
    public FlatFileItemReader<UserInfo> dataFilterItemReader() {
    	
    	return new FlatFileItemReaderBuilder<UserInfo>()
    			.name("reader")
    			.delimited()
    			.names(new String[] { 
	    					"userId"
	    					 })
    			.targetType(UserInfo.class)
    			.resource(new ClassPathResource("activeusers.csv"))
    			.build();
    }
    
    
    @Bean
    public UserInfoItemProcessor dataFilterItemProcessor() {
        return new UserInfoItemProcessor();
    }
    
    
    @Bean
    public JdbcBatchItemWriter<UserInfo> dataFilterWriter() {
        JdbcBatchItemWriter<UserInfo> writer = new JdbcBatchItemWriter<UserInfo>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("UPDATE db3.userinfo SET user_type = 'user' WHERE user_id = :userId");
        writer.setDataSource(dataSource);

        return writer;
    }
            
}
