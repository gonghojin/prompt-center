package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 템플릿 엔티티에 대한 JPA 리포지토리입니다.
 */
@Repository
public interface PromptTemplateJpaRepository extends JpaRepository<PromptTemplateEntity, Long> {

    /**
     * UUID로 프롬프트 템플릿을 조회합니다.
     *
     * @param uuid 프롬프트 템플릿 UUID
     * @return 프롬프트 템플릿 Optional
     */
    Optional<PromptTemplateEntity> findByUuid(UUID uuid);

    /**
     * 작성자와 상태로 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param createdBy 작성자
     * @param status    상태
     * @param pageable  페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplateEntity> findByCreatedByAndStatus(
        com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity createdBy,
        PromptStatus status,
        Pageable pageable);

    /**
     * 가시성과 상태로 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param visibility 가시성
     * @param status     상태
     * @param pageable   페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplateEntity> findByVisibilityAndStatus(Visibility visibility, PromptStatus status,
                                                         Pageable pageable);

    /**
     * 카테고리와 상태로 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param category 카테고리
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    Page<PromptTemplateEntity> findByCategoryAndStatus(CategoryEntity category, PromptStatus status,
                                                       Pageable pageable);

    /**
     * 태그를 포함하는 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param tag      태그
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    @Query("SELECT pt FROM PromptTemplateEntity pt JOIN pt.tagRelations tr JOIN tr.tag t WHERE t.name = :tag AND pt.status = :status")
    Page<PromptTemplateEntity> findByTagsContainingAndStatus(@Param("tag") String tag,
                                                             @Param("status") PromptStatus status, Pageable pageable);

    /**
     * 제목이나 설명에 검색어를 포함하는 프롬프트 템플릿 목록을 페이지네이션하여 조회합니다.
     *
     * @param keyword  검색어
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지
     */
    @Query("SELECT pt FROM PromptTemplateEntity pt WHERE (pt.title LIKE %:keyword% OR pt.description LIKE %:keyword%) AND pt.status = :status")
    Page<PromptTemplateEntity> findByTitleOrDescriptionContainingAndStatus(@Param("keyword") String keyword,
                                                                           @Param("status") PromptStatus status, Pageable pageable);

    /**
     * fetch join을 사용하여 id로 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 모두 조회합니다.
     *
     * @param id 프롬프트 템플릿 ID
     * @return 프롬프트 템플릿 Optional (연관 엔티티 포함)
     */
    @Query("SELECT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE pt.id = :id")
    Optional<PromptTemplateEntity> findByIdWithRelations(@Param("id") Long id);

    /**
     * fetch join을 사용하여 uuid로 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 모두 조회합니다.
     *
     * @param uuid 프롬프트 템플릿 UUID
     * @return 프롬프트 템플릿 Optional (연관 엔티티 포함)
     */
    @Query("SELECT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE pt.uuid = :uuid")
    Optional<PromptTemplateEntity> findByUuidWithRelations(@Param("uuid") UUID uuid);

    /**
     * fetch join을 사용하여 작성자와 상태로 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 페이지네이션하여 모두 조회합니다.
     *
     * @param createdBy 작성자
     * @param status    상태
     * @param pageable  페이지네이션 정보
     * @return 프롬프트 템플릿 페이지 (연관 엔티티 포함)
     */
    @Query(value = "SELECT DISTINCT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE pt.createdBy = :createdBy AND pt.status = :status", countQuery = "SELECT COUNT(pt) FROM PromptTemplateEntity pt WHERE pt.createdBy = :createdBy AND pt.status = :status")
    Page<PromptTemplateEntity> findByCreatedByAndStatusWithRelations(
        @Param("createdBy") com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity createdBy,
        @Param("status") PromptStatus status,
        Pageable pageable);

    /**
     * fetch join을 사용하여 가시성과 상태로 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 페이지네이션하여 모두 조회합니다.
     *
     * @param visibility 가시성
     * @param status     상태
     * @param pageable   페이지네이션 정보
     * @return 프롬프트 템플릿 페이지 (연관 엔티티 포함)
     */
    @Query(value = "SELECT DISTINCT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE pt.visibility = :visibility AND pt.status = :status", countQuery = "SELECT COUNT(pt) FROM PromptTemplateEntity pt WHERE pt.visibility = :visibility AND pt.status = :status")
    Page<PromptTemplateEntity> findByVisibilityAndStatusWithRelations(@Param("visibility") Visibility visibility,
                                                                      @Param("status") PromptStatus status,
                                                                      Pageable pageable);

    /**
     * fetch join을 사용하여 카테고리와 상태로 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 페이지네이션하여 모두
     * 조회합니다.
     *
     * @param category 카테고리
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지 (연관 엔티티 포함)
     */
    @Query(value = "SELECT DISTINCT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE pt.category = :category AND pt.status = :status", countQuery = "SELECT COUNT(pt) FROM PromptTemplateEntity pt WHERE pt.category = :category AND pt.status = :status")
    Page<PromptTemplateEntity> findByCategoryAndStatusWithRelations(@Param("category") CategoryEntity category,
                                                                    @Param("status") PromptStatus status,
                                                                    Pageable pageable);

    /**
     * fetch join을 사용하여 태그를 포함하고 상태가 일치하는 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 페이지네이션하여
     * 모두 조회합니다.
     *
     * @param tag      태그
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지 (연관 엔티티 포함)
     */
    @Query(value = "SELECT DISTINCT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE EXISTS (SELECT 1 FROM pt.tagRelations tr2 WHERE tr2.tag.name LIKE %:tag%) AND pt.status = :status", countQuery = "SELECT COUNT(pt) FROM PromptTemplateEntity pt WHERE EXISTS (SELECT 1 FROM pt.tagRelations tr2 WHERE tr2.tag.name LIKE %:tag%) AND pt.status = :status")
    Page<PromptTemplateEntity> findByTagsContainingAndStatusWithRelations(@Param("tag") String tag,
                                                                          @Param("status") PromptStatus status,
                                                                          Pageable pageable);

    /**
     * fetch join을 사용하여 제목 또는 설명에 검색어를 포함하고 상태가 일치하는 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리,
     * 태그)를 페이지네이션하여 모두 조회합니다.
     *
     * @param keyword  검색어
     * @param status   상태
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 템플릿 페이지 (연관 엔티티 포함)
     */
    @Query(value = "SELECT DISTINCT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag " +
        "WHERE (pt.title LIKE %:keyword% OR pt.description LIKE %:keyword%) AND pt.status = :status", countQuery = "SELECT COUNT(pt) FROM PromptTemplateEntity pt WHERE (pt.title LIKE %:keyword% OR pt.description LIKE %:keyword%) AND pt.status = :status")
    Page<PromptTemplateEntity> findByTitleOrDescriptionContainingAndStatusWithRelations(
        @Param("keyword") String keyword,
        @Param("status") PromptStatus status,
        Pageable pageable);

    /**
     * fetch join을 사용하여 모든 프롬프트 템플릿과 연관 엔티티(작성자, 카테고리, 태그)를 조회합니다.
     *
     * @return 모든 프롬프트 템플릿 엔티티 (연관 엔티티 포함)
     */
    @Query("SELECT DISTINCT pt FROM PromptTemplateEntity pt " +
        "LEFT JOIN FETCH pt.createdBy " +
        "LEFT JOIN FETCH pt.category " +
        "LEFT JOIN FETCH pt.tagRelations tr " +
        "LEFT JOIN FETCH tr.tag")
    List<PromptTemplateEntity> findAllWithRelations();

    /**
     * 주어진 기간(createdAt) 내 프롬프트 개수를 반환합니다.
     *
     * @param start 시작일 (포함)
     * @param end   종료일 (포함)
     * @return 해당 기간 내 프롬프트 개수
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
