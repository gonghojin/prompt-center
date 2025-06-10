package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.LoadPromptDetailQuery;
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
     * 주어진 UUID로 프롬프트 상세 정보를 조회합니다.
     *
     * @param query 프롬프트 상세 조회 쿼리 객체
     * @return 프롬프트 상세 정보(Optional), 존재하지 않을 경우 Optional.empty()
     * @throws IllegalArgumentException 쿼리 객체가 null인 경우 발생
     */
    @Override
    public Optional<PromptDetail> loadPromptDetailByUuid(LoadPromptDetailQuery query) {
        Assert.notNull(query, "LoadPromptDetailQuery must not be null");
        log.debug("Loading prompt detail by UUID: {}", query.getPromptUuid());

        Optional<PromptDetail> detail = loadPromptPort.loadPromptDetailBy(query);
        if (detail.isPresent()) {
            log.debug("Successfully loaded prompt detail for UUID: {}", query.getPromptUuid());
            return detail;
        }

        log.info("No prompt detail found for UUID: {}", query.getPromptUuid());
        return Optional.empty();
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
