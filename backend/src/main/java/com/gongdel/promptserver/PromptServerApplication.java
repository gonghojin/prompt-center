package com.gongdel.promptserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories
public class PromptServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptServerApplication.class, args);
    }
}
