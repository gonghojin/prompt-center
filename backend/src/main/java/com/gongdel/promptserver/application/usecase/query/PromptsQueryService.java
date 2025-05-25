package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionExceptionConverter;
import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.FindPromptsPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.exception.PromptVersionDomainException;
import com.gongdel.promptserver.domain.model.Category;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.Visibility;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프롬프트 목록 조회 유스케이스 구현 서비스 다양한 조건으로 프롬프트를 조회하는 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptsQueryService implements PromptsQueryUseCase {

    private final LoadPromptPort loadPromptPort;
    private final FindPromptsPort findPromptsPort;
    private final SearchPromptsPort searchPromptsPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findAllPrompts(Pageable pageable) {
        log.debug("Querying all prompts with pagination: pageable={}", pageable);
        try {
            Page<PromptTemplate> prompts = findPromptsPort.findAllPrompts(pageable);
            log.info("Retrieved all prompts: total count={}", prompts.getTotalElements());
            return prompts;
        } catch (PromptVersionDomainException e) {
            throw PromptVersionExceptionConverter.convertToApplicationException(e, "all prompts");
        } catch (Exception e) {
            log.error("Unexpected error while querying all prompts", e);
            throw new PromptVersionOperationFailedException(
                    "Error occurred during prompt operation: " + e.getMessage(),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findPromptsByCreatedByAndStatus(User user, PromptStatus status,
            Pageable pageable) {
        log.debug("Querying prompts by creator and status: user={}, status={}, pageable={}", user, status, pageable);
        try {
            Page<PromptTemplate> prompts = findPromptsPort.findPromptsByCreatedByAndStatus(user, status, pageable);
            log.info("Retrieved prompts by creator and status: total count={}", prompts.getTotalElements());
            return prompts;
        } catch (PromptVersionDomainException e) {
            throw PromptVersionExceptionConverter.convertToApplicationException(e, user);
        } catch (Exception e) {
            log.error("Unexpected error while querying prompts by creator and status", e);
            throw new PromptVersionOperationFailedException(
                    "Error occurred during prompt operation: " + e.getMessage(),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findPromptsByVisibilityAndStatus(Visibility visibility,
            PromptStatus status, Pageable pageable) {
        log.debug("Querying prompts by visibility and status: visibility={}, status={}, pageable={}", visibility,
                status, pageable);
        try {
            Page<PromptTemplate> prompts = findPromptsPort.findPromptsByVisibilityAndStatus(visibility, status,
                    pageable);
            log.info("Retrieved prompts by visibility and status: total count={}", prompts.getTotalElements());
            return prompts;
        } catch (PromptVersionDomainException e) {
            throw PromptVersionExceptionConverter.convertToApplicationException(e, visibility);
        } catch (Exception e) {
            log.error("Unexpected error while querying prompts by visibility and status", e);
            throw new PromptVersionOperationFailedException(
                    "Error occurred during prompt operation: " + e.getMessage(),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findPromptsByCategoryAndStatus(Category category, PromptStatus status,
            Pageable pageable) {
        log.debug("Querying prompts by category and status: category={}, status={}, pageable={}", category, status,
                pageable);
        try {
            Page<PromptTemplate> prompts = findPromptsPort.findPromptsByCategoryAndStatus(category, status, pageable);
            log.info("Retrieved prompts by category and status: total count={}", prompts.getTotalElements());
            return prompts;
        } catch (PromptVersionDomainException e) {
            throw PromptVersionExceptionConverter.convertToApplicationException(e, category);
        } catch (Exception e) {
            log.error("Unexpected error while querying prompts by category and status", e);
            throw new PromptVersionOperationFailedException(
                    "Error occurred during prompt operation: " + e.getMessage(),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PromptTemplate> loadPromptByUuid(UUID uuid) {
        log.info("Retrieving prompt by ID: {}", uuid);
        Optional<PromptTemplate> prompt = loadPromptPort.loadPromptByUuid(uuid);
        if (prompt.isPresent()) {
            log.debug("Successfully retrieved prompt with ID: {}, title: {}", uuid, prompt.get().getTitle());
        } else {
            log.debug("No prompt found with ID: {}", uuid);
        }
        return prompt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> searchPromptsByKeyword(String keyword, PromptStatus status, Pageable pageable) {
        log.debug("Searching prompts by keyword: keyword={}, status={}, pageable={}", keyword, status, pageable);
        try {
            Page<PromptTemplate> prompts = searchPromptsPort.searchPromptsByKeywordAndStatus(keyword, status, pageable);
            log.info("Searched prompts by keyword: total count={}", prompts.getTotalElements());
            return prompts;
        } catch (PromptVersionDomainException e) {
            throw PromptVersionExceptionConverter.convertToApplicationException(e, keyword);
        } catch (Exception e) {
            log.error("Unexpected error while searching prompts by keyword", e);
            throw new PromptVersionOperationFailedException(
                    "Error occurred during prompt search operation: " + e.getMessage(),
                    e);
        }
    }
}
