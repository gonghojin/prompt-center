package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptTemplateMapper;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptVersionMapper;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.*;
import com.gongdel.promptserver.application.port.in.query.LoadPromptDetailQuery;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptDetail;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptVersion;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 템플릿 단건 조회(Query) 어댑터 구현체입니다. LoadPromptPort를 구현하며, fetch join 최적화된 JPA
 * 메서드를 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptLoadAdapter implements LoadPromptPort, LoadPromptTemplateIdPort {

    private final PromptTemplateJpaRepository promptTemplateJpaRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateMapper promptTemplateMapper;
    private final PromptVersionMapper promptVersionMapper;
    private final UserMapper userMapper;
    private final FavoriteRepository favoriteRepository;
    private final PromptLikeCountRepository promptLikeCountRepository;
    private final PromptLikeJpaRepository promptLikeJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PromptTemplate> loadPromptById(Long id) {
        try {
            log.debug("Loading prompt by id: {}", id);
            return promptTemplateJpaRepository.findById(id)
                .map(promptTemplateMapper::toDomain);
        } catch (Exception e) {
            log.error("Failed to load prompt by id: {}. Error: {}", id, e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 단건(ID) 조회 실패", e);
        }
    }

    /**
     * 프롬프트 템플릿을 UUID로 조회합니다.
     *
     * @param uuid 프롬프트 템플릿의 UUID
     * @return 조회된 프롬프트 템플릿 (Optional)
     * @throws PromptOperationException 조회 실패 시 예외 발생
     */
    @Override
    public Optional<PromptTemplate> loadPromptByUuid(UUID uuid) {
        try {
            log.debug("Loading prompt by uuid: {}", uuid);
            return promptTemplateJpaRepository.findByUuid(uuid)
                .map(promptTemplateMapper::toDomain);
        } catch (Exception e) {
            log.error("Failed to load prompt by uuid: {}. Error: {}", uuid, e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 단건(UUID) 조회 실패", e);
        }
    }

    /**
     * 주어진 UUID와 사용자 ID로 프롬프트 상세 정보를 조회합니다.
     *
     * @param query 프롬프트 상세 조회 쿼리 객체
     * @return 조회된 프롬프트 상세 정보 (Optional), 없으면 Optional.empty()
     * @throws PromptOperationException 조회 실패 시 예외 발생
     */
    @Override
    public Optional<PromptDetail> loadPromptDetailBy(LoadPromptDetailQuery query) {
        try {
            Assert.notNull(query, "LoadPromptDetailQuery must not be null");
            log.debug("Loading prompt detail by uuid: {}, userId: {}", query.getPromptUuid(), query.getUserId());
            return promptTemplateJpaRepository.findByUuidWithRelations(query.getPromptUuid())
                .map(entity -> toPromptDetail(entity, query.getUserId()));
        } catch (Exception e) {
            log.error("Failed to load prompt detail by uuid: {}, userId: {}. Error: {}", query.getPromptUuid(),
                query.getUserId(), e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 상세 조회 실패", e);
        }
    }

    /**
     * 프롬프트 엔티티를 상세 도메인 객체로 변환합니다.
     *
     * @param entity        프롬프트 엔티티
     * @param currentUserId 현재 사용자 ID
     * @return 상세 도메인 객체
     */
    private PromptDetail toPromptDetail(PromptTemplateEntity entity, Long currentUserId) {
        PromptTemplate template = promptTemplateMapper.toDomain(entity);
        PromptVersion version = getCurrentVersion(template.getCurrentVersionId());
        User author = userMapper.toDomain(entity.getCreatedBy());
        Set<String> tags = extractTags(entity);
        int viewCount = generateRandomStat();
        int favoriteCount = promptLikeCountRepository
            .findByPromptTemplateId(entity.getId())
            .map(c -> c.getLikeCount().intValue())
            .orElse(0);
        boolean isLiked = (currentUserId != null)
            && promptLikeJpaRepository.existsByUserIdAndPromptTemplateId(currentUserId, entity.getId());
        boolean isFavorite = favoriteRepository.existsByUserAndPromptTemplate(new UserEntity(currentUserId), entity);

        return PromptDetail.builder()
            .id(template.getUuid())
            .title(template.getTitle())
            .description(template.getDescription())
            .content(version != null ? version.getContent() : null)
            .author(author)
            .tags(tags)
            .isPublic(template.getVisibility() == Visibility.PUBLIC)
            .createdAt(template.getCreatedAt())
            .updatedAt(template.getUpdatedAt())
            .viewCount(viewCount)
            .favoriteCount(favoriteCount)
            .categoryId(template.getCategoryId())
            .visibility(template.getVisibility() != null ? template.getVisibility().name() : null)
            .status(template.getStatus() != null ? template.getStatus().name() : null)
            .isFavorite(isFavorite)
            .isLiked(isLiked)
            .inputVariables(version != null ? version.getInputVariables() : java.util.Collections.emptyList())
            .build();
    }

    /**
     * 현재 버전 ID로 PromptVersion을 조회합니다.
     *
     * @param currentVersionId 현재 버전 ID
     * @return PromptVersion 또는 null
     */
    private PromptVersion getCurrentVersion(Long currentVersionId) {
        if (currentVersionId == null)
            return null;
        return promptVersionRepository.findById(currentVersionId)
            .map(promptVersionMapper::toDomain)
            .orElse(null);
    }

    /**
     * 프롬프트 엔티티에서 태그 이름 Set을 추출합니다.
     *
     * @param entity 프롬프트 엔티티
     * @return 태그 이름 Set
     */
    private Set<String> extractTags(PromptTemplateEntity entity) {
        if (entity.getTagRelations() == null)
            return java.util.Collections.emptySet();
        return entity.getTagRelations().stream()
            .map(rel -> rel.getTag().getName())
            .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 임시 통계(랜덤값) 생성
     *
     * @return 0~999 사이의 랜덤값
     */
    private int generateRandomStat() {
        return new java.util.Random().nextInt(1000);
    }

    /**
     * 프롬프트 템플릿 UUID로 PK(Long id)를 조회합니다.
     *
     * @param uuid 프롬프트 템플릿 UUID
     * @return PK(Long id) Optional
     * @throws PromptOperationException 조회 실패 시 예외 발생
     */
    @Override
    public Optional<Long> findIdByUuid(UUID uuid) {
        try {
            log.debug("Loading prompt id by uuid: {}", uuid);
            return promptTemplateJpaRepository.findByUuid(uuid)
                .map(PromptTemplateEntity::getId);
        } catch (Exception e) {
            log.error("Failed to load prompt id by uuid: {}. Error: {}", uuid, e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 PK(UUID) 조회 실패", e);
        }
    }
}
