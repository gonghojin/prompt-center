package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.view.PromptViewLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 프롬프트 조회 로그 JPA 리포지토리
 */
public interface PromptViewLogJpaRepository extends JpaRepository<PromptViewLogEntity, String> {

    /**
     * 로그인 사용자의 중복 조회 여부를 확인합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param since            확인할 시간 기준 (이 시간 이후의 조회만 확인)
     * @return 중복 조회 여부
     */
    boolean existsByUserIdAndPromptTemplateIdAndViewedAtAfter(
        Long userId, Long promptTemplateId, LocalDateTime since);

    /**
     * 비로그인 사용자(IP 기반)의 중복 조회 여부를 확인합니다.
     *
     * @param ipAddress        IP 주소
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param since            확인할 시간 기준
     * @return 중복 조회 여부
     */
    boolean existsByIpAddressAndPromptTemplateIdAndViewedAtAfter(
        String ipAddress, Long promptTemplateId, LocalDateTime since);

    /**
     * 익명 사용자(익명 ID 기반)의 중복 조회 여부를 확인합니다.
     *
     * @param anonymousId      익명 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param since            확인할 시간 기준
     * @return 중복 조회 여부
     */
    boolean existsByAnonymousIdAndPromptTemplateIdAndViewedAtAfter(
        String anonymousId, Long promptTemplateId, LocalDateTime since);

    /**
     * 특정 프롬프트의 조회수를 집계합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 총 조회수
     */
    long countByPromptTemplateId(Long promptTemplateId);

    /**
     * 특정 기간 동안의 프롬프트 조회수를 집계합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param startDate        시작 날짜
     * @param endDate          종료 날짜
     * @return 기간 내 조회수
     */
    long countByPromptTemplateIdAndViewedAtBetween(
        Long promptTemplateId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간 이후의 조회 로그를 삭제합니다. (데이터 정리용)
     *
     * @param before 이 시간 이전의 로그 삭제
     * @return 삭제된 로그 수
     */
    long deleteByViewedAtBefore(LocalDateTime before);

    /**
     * 인기 프롬프트 목록을 조회합니다. (특정 기간 내)
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @param limit     조회할 개수
     * @return 인기 프롬프트 ID와 조회수 목록
     */
    @Query("""
        SELECT pvl.promptTemplateId as promptTemplateId, COUNT(pvl) as viewCount
        FROM PromptViewLogEntity pvl
        WHERE pvl.viewedAt BETWEEN :startDate AND :endDate
        GROUP BY pvl.promptTemplateId
        ORDER BY COUNT(pvl) DESC
        LIMIT :limit
        """)
    List<PromptViewCountProjection> findTopViewedPrompts(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("limit") int limit);

    /**
     * 일별 조회수 통계를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param startDate        시작 날짜
     * @param endDate          종료 날짜
     * @return 일별 조회수 통계
     */
    @Query("""
        SELECT DATE(pvl.viewedAt) as viewDate, COUNT(pvl) as viewCount
        FROM PromptViewLogEntity pvl
        WHERE pvl.promptTemplateId = :promptTemplateId
          AND pvl.viewedAt BETWEEN :startDate AND :endDate
        GROUP BY DATE(pvl.viewedAt)
        ORDER BY DATE(pvl.viewedAt)
        """)
    List<DailyViewStatisticsProjection> findDailyViewStatistics(
        @Param("promptTemplateId") Long promptTemplateId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * 프롬프트 조회수 집계 결과를 위한 프로젝션 인터페이스
     */
    interface PromptViewCountProjection {
        Long getPromptTemplateId();

        Long getViewCount();
    }

    /**
     * 일별 조회수 통계를 위한 프로젝션 인터페이스
     */
    interface DailyViewStatisticsProjection {
        String getViewDate();

        Long getViewCount();
    }
}
