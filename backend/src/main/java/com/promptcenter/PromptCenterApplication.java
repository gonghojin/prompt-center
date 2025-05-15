package com.promptcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableElasticsearchRepositories
public class PromptCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptCenterApplication.class, args);
    }
} 