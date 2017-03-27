package com.csv.scrubber.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration //Tells Spring this is a configuration class
@EnableAsync //Tells Spring that we plan on using asynchronous methods
public class SpringAsyncConfiguration extends AsyncConfigurerSupport {
	
	@Override
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); //so this is a Spring class that creates, runs, and destroys threads
        							  //The values you supply to it are meant for your particular machine
        executor.setCorePoolSize(1); //The number of processor cores we normally use
        executor.setMaxPoolSize(2); //The maximum number of processor cores we can use
        executor.setQueueCapacity(50); //How many threads to make
        executor.setThreadNamePrefix("Scrubber-"); //The name of each thread (prefix only)
        executor.initialize(); //Initialize the executor
        return executor;
    }

}
