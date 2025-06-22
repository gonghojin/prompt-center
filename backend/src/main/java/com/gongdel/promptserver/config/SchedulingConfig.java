package com.gongdel.promptserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 스케줄링 설정 클래스입니다.
 * <p>
 * 스케줄러의 스레드 풀을 구성하고 스케줄링 기능을 활성화합니다.
 * 기본적으로 Spring Boot는 단일 스레드로 스케줄링 작업을 수행하므로,
 * 여러 스케줄링 작업이 동시에 실행될 수 있도록 스레드 풀을 설정합니다.
 */
@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5); // 스케줄링 작업용 스레드 풀 크기
        taskScheduler.setThreadNamePrefix("scheduling-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(60);
        taskScheduler.initialize();

        taskRegistrar.setTaskScheduler(taskScheduler);

        log.info("Scheduling configured with thread pool size: {}", taskScheduler.getPoolSize());
    }
}
