package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.favorite.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 즐겨찾기(Favorite) 엔티티의 JPA 레포지토리입니다.
 * 사용자별, 프롬프트별 즐겨찾기 조회 및 중복 체크, 삭제 기능을 제공합니다.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {

    /**
     * 특정 사용자와 프롬프트의 즐겨찾기를 삭제합니다.
     *
     * @param user           사용자 엔티티
     * @param promptTemplate 프롬프트 엔티티
     * @return 삭제된 개수
     */
    long deleteByUserAndPromptTemplate(UserEntity user, PromptTemplateEntity promptTemplate);

    /**
     * 특정 프롬프트의 즐겨찾기 개수를 반환합니다.
     *
     * @param promptTemplate 프롬프트 엔티티
     * @return 즐겨찾기 개수
     */
    long countByPromptTemplate(PromptTemplateEntity promptTemplate);

    /**
     * 특정 사용자의 즐겨찾기 개수를 반환합니다.
     *
     * @param user 사용자 엔티티
     * @return 즐겨찾기 개수
     */
    long countByUser(UserEntity user);

    /**
     * 특정 기간에 생성된 즐겨찾기 개수를 반환합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 해당 기간의 즐겨찾기 개수
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 사용자가 즐겨찾기한 프롬프트 ID 목록을 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 즐겨찾기한 프롬프트 ID 목록
     */
    @Query("select f.promptTemplate.id from FavoriteEntity f where f.user.id = :userId")
    java.util.List<Long> findPromptTemplateIdsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자가 즐겨찾기한 프롬프트 ID 목록을 반환합니다.
     * 주어진 프롬프트 ID 목록 중에서만 조회합니다.
     *
     * @param userId            사용자 ID
     * @param promptTemplateIds 조회할 프롬프트 ID 목록
     * @return 즐겨찾기한 프롬프트 ID 목록
     */
    @Query("select f.promptTemplate.id from FavoriteEntity f where f.user.id = :userId and f.promptTemplate.id in :promptTemplateIds")
    java.util.List<Long> findPromptTemplateIdsByUserIdAndPromptTemplateIdsIn(
        @Param("userId") Long userId,
        @Param("promptTemplateIds") java.util.List<Long> promptTemplateIds);

    /**
     * 특정 사용자와 프롬프트의 즐겨찾기 존재 여부를 확인합니다.
     *
     * @param user           사용자 엔티티
     * @param promptTemplate 프롬프트 엔티티
     * @return 즐겨찾기 존재 여부
     */
    boolean existsByUserAndPromptTemplate(UserEntity user, PromptTemplateEntity promptTemplate);
}
