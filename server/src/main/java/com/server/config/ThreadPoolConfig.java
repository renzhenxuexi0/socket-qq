package com.server.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Value("${threadPool.corePoolSize}")
    private Integer corePoolSize;
    @Value("${threadPool.maximumPoolSize}")
    private Integer maximumPoolSize;
    @Value("${threadPool.workQueueSize}")
    private Integer workQueueSize;


    @Bean
    ThreadPoolExecutor poolExecutor() {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                2, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(workQueueSize), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
