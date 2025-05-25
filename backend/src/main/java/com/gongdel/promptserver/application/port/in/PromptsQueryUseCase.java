package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.Category;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.Visibility;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 프롬프트 목록 조회를 위한 유스케이스 인터페이스
 */
public interface PromptsQueryUseCase {

    /**
     * 모든 프롬프트 템플릿을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @return 모든 프롬프트 템플릿 페이지
     */
    Page<PromptTemplate> findAllPrompts(Pageable pageable);

    /**
     * 작성자와 상태로 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param user     작성자
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplate> findPromptsByCreatedByAndStatus(User user, PromptStatus status, Pageable pageable);

    /**
     * 가시성과 상태로 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param visibility 가시성
     * @param status     상태
     * @param pageable   페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplate> findPromptsByVisibilityAndStatus(Visibility visibility, PromptStatus status,
            Pageable pageable);

    /**
     * 카테고리와 상태로 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param category 카테고리
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplate> findPromptsByCategoryAndStatus(Category category, PromptStatus status, Pageable pageable);

    /**
     * 프롬프트 템플릿을 UUID로 조회합니다.
     *
     * @param uuid 프롬프트 템플릿의 UUID
     * @return 조회된 프롬프트 템플릿 (없는 경우 빈 Optional 반환)
     */
    Optional<PromptTemplate> loadPromptByUuid(UUID uuid);

    /**
     * 키워드와 상태로 프롬프트 템플릿을 페이지네이션하여 검색합니다.
     *
     * @param keyword  검색어
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplate> searchPromptsByKeyword(
            String keyword, PromptStatus status,
            Pageable pageable);
}
