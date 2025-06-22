package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.Category;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 필터링된 프롬프트 템플릿 목록 조회를 위한 포트입니다.
 */
public interface FindPromptsPort {

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
}
