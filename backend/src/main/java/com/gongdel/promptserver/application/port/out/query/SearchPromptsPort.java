package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import org.springframework.data.domain.Page;

/**
 * 프롬프트 템플릿 검색을 위한 포트입니다.
 */
public interface SearchPromptsPort {

    /**
     * 복합 검색 조건(제목, 설명, 태그, 카테고리) 및 정렬 기준으로 프롬프트를 조회합니다.
     *
     * @param condition 프롬프트 검색 조건
     * @return 프롬프트 페이지
     * @throws PromptOperationException 프롬프트 검색 중 예외 발생 시
     */
    Page<PromptSearchResult> searchPrompts(PromptSearchCondition condition);
}
