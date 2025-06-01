package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.model.PromptDetail;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 목록 및 상세 조회, 검색 등 다양한 조건으로 프롬프트를 조회하는 유스케이스 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptsQueryService implements PromptsQueryUseCase {

    private final LoadPromptPort loadPromptPort;
    private final SearchPromptsPort searchPromptsPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PromptDetail> loadPromptDetailByUuid(UUID uuid) {
        Assert.notNull(uuid, "UUID must not be null");
        log.debug("Loading prompt detail by UUID: {}", uuid);
        Optional<PromptDetail> detail = loadPromptPort.loadPromptDetailByUuid(uuid);
        if (detail.isPresent()) {
            log.debug("Successfully loaded prompt detail for UUID: {}", uuid);
        } else {
            log.debug("No prompt detail found for UUID: {}", uuid);
        }
        return detail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptSearchResult> searchPrompts(PromptSearchCondition condition) {
        Assert.notNull(condition, "PromptSearchCondition must not be null");
        log.debug("Searching prompts with condition: {}", condition);
        try {
            return searchPromptsPort.searchPrompts(condition);
        } catch (Exception e) {
            log.error("Unexpected error while searching prompts with condition: {}", condition, e);
            throw new PromptVersionOperationFailedException(
                "Error occurred during prompt search operation: " + e.getMessage(),
                e);
        }
    }
}
