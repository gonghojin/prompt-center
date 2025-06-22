package com.gongdel.promptserver.adapter.out.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.gongdel.promptserver.adapter.out.persistence.repository")
public class JpaConfig {
}
