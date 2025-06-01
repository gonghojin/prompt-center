package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.PromptDetail;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 목록 조회를 위한 유스케이스 인터페이스
 */
public interface PromptsQueryUseCase {

    /**
     * 프롬프트 상세 정보를 UUID로 조회합니다.
     *
     * @param uuid 프롬프트 템플릿의 UUID
     * @return 조회된 프롬프트 상세 정보 (없는 경우 빈 Optional 반환)
     */
    Optional<PromptDetail> loadPromptDetailByUuid(UUID uuid);

    /**
     * 복합 검색 조건(제목, 설명, 태그, 카테고리, 정렬 등)으로 프롬프트를 조회합니다.
     *
     * @param condition 검색 조건 DTO
     * @return 프롬프트 검색 결과 페이지
     */
    Page<PromptSearchResult> searchPrompts(PromptSearchCondition condition);
}
