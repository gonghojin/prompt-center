package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.view.PromptViewCountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 프롬프트 조회수 집계 JPA 리포지토리
 */
public interface PromptViewCountJpaRepository extends JpaRepository<PromptViewCountEntity, Long> {

    /**
     * 프롬프트 템플릿 ID로 조회수 정보를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 조회수 정보
     */
    Optional<PromptViewCountEntity> findByPromptTemplateId(Long promptTemplateId);

    /**
     * 조회수를 원자적으로 증가시킵니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param count            증가시킬 조회수
     * @return 영향받은 행 수
     */
    @Modifying
    @Query("""
        UPDATE PromptViewCountEntity pvc
        SET pvc.totalViewCount = pvc.totalViewCount + :count,
            pvc.updatedAt = CURRENT_TIMESTAMP
        WHERE pvc.promptTemplateId = :promptTemplateId
        """)
    int incrementViewCount(@Param("promptTemplateId") Long promptTemplateId, @Param("count") long count);

    /**
     * 조회수 기준 상위 프롬프트 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 조회수 기준 상위 프롬프트 목록
     */
    @Query("""
        SELECT pvc FROM PromptViewCountEntity pvc
        ORDER BY pvc.totalViewCount DESC, pvc.updatedAt DESC
        """)
    Page<PromptViewCountEntity> findTopViewedPrompts(Pageable pageable);

    /**
     * 최소 조회수 이상인 프롬프트 목록을 조회합니다.
     *
     * @param minViewCount 최소 조회수
     * @param pageable     페이징 정보
     * @return 조건에 맞는 프롬프트 목록
     */
    Page<PromptViewCountEntity> findByTotalViewCountGreaterThanEqual(Long minViewCount, Pageable pageable);

    /**
     * 특정 기간 내 업데이트된 조회수 정보를 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @param pageable  페이징 정보
     * @return 기간 내 업데이트된 조회수 정보
     */
    Page<PromptViewCountEntity> findByUpdatedAtBetween(
        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 여러 프롬프트 ID에 대한 조회수 정보를 일괄 조회합니다.
     *
     * @param promptTemplateIds 프롬프트 템플릿 ID 목록
     * @return 조회수 정보 목록
     */
    List<PromptViewCountEntity> findByPromptTemplateIdIn(List<Long> promptTemplateIds);

    /**
     * 전체 조회수 합계를 조회합니다.
     *
     * @return 전체 조회수 합계
     */
    @Query("SELECT COALESCE(SUM(pvc.totalViewCount), 0) FROM PromptViewCountEntity pvc")
    long getTotalViewCount();

    /**
     * 특정 기간 내 업데이트된 프롬프트의 조회수 합계를 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 기간 내 조회수 합계
     */
    @Query("""
        SELECT COALESCE(SUM(pvc.totalViewCount), 0)
        FROM PromptViewCountEntity pvc
        WHERE pvc.updatedAt BETWEEN :startDate AND :endDate
        """)
    long getTotalViewCountByPeriod(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 조회수가 있는 프롬프트 개수를 조회합니다.
     *
     * @return 조회수가 있는 프롬프트 개수
     */
    @Query("SELECT COUNT(pvc) FROM PromptViewCountEntity pvc WHERE pvc.totalViewCount > 0")
    long countPromptsWithViews();

    /**
     * 평균 조회수를 조회합니다.
     *
     * @return 평균 조회수
     */
    @Query("SELECT COALESCE(AVG(pvc.totalViewCount), 0.0) FROM PromptViewCountEntity pvc")
    double getAverageViewCount();

    /**
     * 내가 생성한 프롬프트의 총 조회수를 집계합니다.
     *
     * @param userId 사용자 ID
     * @return 총 조회수
     */
    @Query("""
        SELECT COALESCE(SUM(pvc.totalViewCount), 0)
        FROM PromptViewCountEntity pvc
        JOIN PromptTemplateEntity pt ON pvc.promptTemplateId = pt.id
        WHERE pt.createdBy.id = :userId
        """)
    Long sumViewCountByUserId(@Param("userId") Long userId);

}
