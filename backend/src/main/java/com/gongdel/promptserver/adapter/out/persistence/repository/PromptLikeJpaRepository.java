package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 프롬프트 좋아요 JPA 리포지토리입니다.
 */
public interface PromptLikeJpaRepository extends JpaRepository<PromptLikeEntity, Long> {
    /**
     * userId로 좋아요 목록을 조회합니다.
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 좋아요 엔티티 목록(페이징)
     */
    @Query("SELECT pl FROM PromptLikeEntity pl JOIN FETCH pl.promptTemplate pt WHERE pl.user.id = :userId")
    Page<PromptLikeEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * userId, promptTemplateId로 좋아요 존재 여부를 확인합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 존재 여부
     */
    boolean existsByUserIdAndPromptTemplateId(Long userId, Long promptTemplateId);

    /**
     * userId, promptTemplateId로 좋아요를 삭제합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    void deleteByUserIdAndPromptTemplateId(Long userId, Long promptTemplateId);

    /**
     * promptTemplateId로 좋아요 수를 집계합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 좋아요 수
     */
    long countByPromptTemplateId(Long promptTemplateId);

    /**
     * 여러 프롬프트 ID에 대해 사용자가 좋아요한 프롬프트 ID 목록을 조회합니다.
     *
     * @param userId            사용자 ID
     * @param promptTemplateIds 프롬프트 ID 리스트
     * @return 사용자가 좋아요한 프롬프트 ID 리스트
     */
    @Query("""
            SELECT pl.promptTemplate.id
            FROM PromptLikeEntity pl
            WHERE pl.user.id = :userId
              AND pl.promptTemplate.id IN :promptTemplateIds
        """)
    List<Long> findPromptTemplateIdsLikedByUser(@Param("userId") Long userId,
                                                @Param("promptTemplateIds") List<Long> promptTemplateIds);
}
