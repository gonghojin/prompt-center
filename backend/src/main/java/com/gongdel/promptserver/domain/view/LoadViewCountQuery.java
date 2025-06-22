package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * 프롬프트 조회수 조회 쿼리 객체입니다.
 */
@Getter
@ToString
public class LoadViewCountQuery {

    /**
     * 프롬프트 템플릿 ID
     */
    private final Long promptTemplateId;

    /**
     * LoadViewCountQuery 객체를 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID (필수)
     */
    @Builder
    private LoadViewCountQuery(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        this.promptTemplateId = promptTemplateId;
    }

    /**
     * Long ID로 조회수 조회 쿼리 생성 팩토리 메서드
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return LoadViewCountQuery 객체
     */
    public static LoadViewCountQuery of(Long promptTemplateId) {
        return LoadViewCountQuery.builder()
            .promptTemplateId(promptTemplateId)
            .build();
    }
}
