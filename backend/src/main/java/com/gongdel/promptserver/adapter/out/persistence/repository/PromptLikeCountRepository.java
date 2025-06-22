package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 프롬프트별 좋아요 수 집계 Projection JPA 리포지토리입니다.
 */
public interface PromptLikeCountRepository extends JpaRepository<PromptLikeCountEntity, Long> {
    /**
     * promptTemplateId로 Projection을 조회합니다.
     *
     * @param promptTemplateId 프롬프트 ID
     * @return Projection 엔티티(Optional)
     */
    Optional<PromptLikeCountEntity> findByPromptTemplateId(Long promptTemplateId);

    /**
     * 여러 프롬프트 ID에 대해 좋아요 수 Projection을 조회합니다.
     *
     * @param promptTemplateIds 프롬프트 ID 리스트
     * @return Projection 리스트
     */
    @Query("""
            SELECT new com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeCountProjection(
                plc.promptTemplateId, plc.likeCount
            )
            FROM com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeCountEntity plc
            WHERE plc.promptTemplateId IN :promptTemplateIds
        """)
    List<PromptLikeCountProjection> findLikeCountsByPromptTemplateIds(
        @Param("promptTemplateIds") List<Long> promptTemplateIds);

    /**
     * 내가 생성한 프롬프트의 총 좋아요 수를 집계합니다.
     *
     * @param userId 사용자 ID
     * @return 총 좋아요 수
     */
    @Query("""
            SELECT SUM(plc.likeCount)
            FROM com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeCountEntity plc
            JOIN com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity pt ON plc.promptTemplateId = pt.id
            WHERE pt.createdBy.id = :userId
        """)
    Long sumLikeCountByUserId(@Param("userId") Long userId);
}
