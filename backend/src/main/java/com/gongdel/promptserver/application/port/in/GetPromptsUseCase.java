package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 목록 조회를 위한 유스케이스 인터페이스
 */
public interface GetPromptsUseCase {

    /**
     * 모든 프롬프트 목록을 조회합니다.
     *
     * @return 모든 프롬프트 목록
     */
    List<PromptTemplate> getAllPrompts();

    /**
     * 공개된 프롬프트 목록만 조회합니다.
     *
     * @return 공개 프롬프트 목록
     */
    List<PromptTemplate> getPublicPrompts();

    /**
     * 특정 사용자가 작성한 프롬프트 목록을 조회합니다.
     *
     * @param authorId 작성자 ID
     * @return 해당 작성자의 프롬프트 목록
     */
    List<PromptTemplate> getPromptsByAuthor(UUID authorId);

    /**
     * 키워드로 프롬프트를 검색합니다. 제목, 설명, 내용에서 검색합니다.
     *
     * @param keyword 검색어
     * @return 검색 결과 프롬프트 목록
     */
    List<PromptTemplate> searchPrompts(String keyword);

    /**
     * ID로 특정 프롬프트를 조회합니다.
     *
     * @param id 프롬프트 ID
     * @return 해당 ID의 프롬프트 (존재하지 않을 경우 빈 Optional)
     */
    Optional<PromptTemplate> getPromptById(UUID id);
}
