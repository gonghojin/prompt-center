package com.gongdel.promptserver.adapter.out.persistence.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정 클래스입니다.
 * JPAQueryFactory를 빈으로 등록하여 Repository에서 주입받을 수 있도록 합니다.
 */
@Configuration
public class QuerydslConfig {

    /**
     * JPAQueryFactory 빈을 생성합니다.
     *
     * @param entityManager JPA EntityManager
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
