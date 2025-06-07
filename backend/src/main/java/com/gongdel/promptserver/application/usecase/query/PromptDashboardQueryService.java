package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.DashboardOperationFailedException;
import com.gongdel.promptserver.application.port.in.PromptDashboardQueryUseCase;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptSortType;
import com.gongdel.promptserver.domain.model.PromptStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 대시보드 최근 프롬프트 조회 유스케이스 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptDashboardQueryService implements PromptDashboardQueryUseCase {
    private final PromptsQueryUseCase promptsQueryUseCase;

    /**
     * 대시보드에 최근 프롬프트 목록을 조회합니다.
     *
     * @param limit 최대 조회 개수(1 이상)
     * @return 최근 프롬프트 목록
     * @throws IllegalArgumentException          limit이 1 미만인 경우
     * @throws DashboardOperationFailedException 프롬프트 조회 중 오류가 발생한 경우
     */
    @Override
    public List<PromptSearchResult> getRecentPrompts(int limit) {
        Assert.isTrue(limit > 0, "limit must be greater than zero");
        try {
            log.debug("Fetching recent prompts for dashboard, limit={}", limit);
            PromptSearchCondition condition = createRecentPromptCondition(limit);
            Page<PromptSearchResult> page = promptsQueryUseCase.searchPrompts(condition);
            log.debug("Recent prompts fetched: {} items", page.getContent().size());
            return page.getContent();
        } catch (Exception e) {
            log.error("Unexpected error during recent prompts query", e);
            throw new DashboardOperationFailedException("Failed to query recent prompts", e);
        }
    }

    /**
     * 최근 프롬프트 조회 조건을 생성합니다.
     *
     * @param limit 최대 조회 개수
     * @return 프롬프트 조회 조건
     */
    private PromptSearchCondition createRecentPromptCondition(int limit) {
        return PromptSearchCondition.builder()
            .status(PromptStatus.PUBLISHED)
            .sortType(PromptSortType.LATEST_MODIFIED)
            .pageable(PageRequest.of(0, limit))
            .build();
    }
}
