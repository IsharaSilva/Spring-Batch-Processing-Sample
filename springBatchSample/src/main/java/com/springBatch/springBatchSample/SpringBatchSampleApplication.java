package com.springBatch.springBatchSample;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class SpringBatchSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSampleApplication.class, args);
	}
	

	@Bean
	Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder("job", jobRepository).start(step).build();
	}
	
	@Bean
	ApplicationRunner runner(JobLauncher jobLauncher, Job job) {
		return args-> {
			
			var jobParameters=new JobParametersBuilder()
				.addString("uuid", UUID.randomUUID().toString())
				.toJobParameters();
			var run=jobLauncher.run(job,jobParameters );
			var instanceId=run.getJobInstance().getInstanceId();
			System.out.println("instanceId:"+ instanceId);
		
		};
	}
	
	@Bean
	@StepScope
    public Tasklet tasklet(@Value("#{jobParameters['uuid']}")String uuid) {
        return (contribution, context) -> {
            System.out.println("hello world! the uuid is "+ uuid);
            return RepeatStatus.FINISHED;
        };
    }

	@Bean
	Step step1(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager transactionManager) {
		return new StepBuilder("step1", jobRepository).tasklet(tasklet, transactionManager).build();
	}
}