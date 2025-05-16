package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.port.in.GetPromptsUseCase;
import com.gongdel.promptserver.application.port.out.LoadPromptPort;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프롬프트 목록 조회 유스케이스 구현 서비스
 * 다양한 조건으로 프롬프트를 조회하는 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPromptsService implements GetPromptsUseCase {

    private final LoadPromptPort loadPromptPort;

    /**
     * 모든 프롬프트 목록을 조회합니다.
     * 시스템에 등록된 모든 프롬프트를 반환합니다.
     *
     * @return 모든 프롬프트 목록
     */
    @Override
    public List<PromptTemplate> getAllPrompts() {
        log.info("Retrieving all prompts");
        List<PromptTemplate> allPrompts = loadPromptPort.loadAllPrompts();
        log.debug("Retrieved {} prompts", allPrompts.size());
        return allPrompts;
    }

    /**
     * 공개 상태인 프롬프트 목록만 조회합니다.
     * 비공개 프롬프트는 제외됩니다.
     *
     * @return 공개 상태인 프롬프트 목록
     */
    @Override
    public List<PromptTemplate> getPublicPrompts() {
        log.info("Retrieving public prompts");
        List<PromptTemplate> publicPrompts = loadPromptPort.loadPublicPrompts();
        log.debug("Retrieved {} public prompts", publicPrompts.size());
        return publicPrompts;
    }

    /**
     * 특정 작성자가 작성한 프롬프트 목록을 조회합니다.
     *
     * @param authorId 작성자 ID
     * @return 해당 작성자가 작성한 프롬프트 목록
     */
    @Override
    public List<PromptTemplate> getPromptsByAuthor(UUID authorId) {
        log.info("Retrieving prompts by author ID: {}", authorId);
        List<PromptTemplate> authorPrompts = loadPromptPort.loadPromptsByAuthor(authorId);
        log.debug("Retrieved {} prompts for author ID: {}", authorPrompts.size(), authorId);
        return authorPrompts;
    }

    /**
     * 키워드를 포함하는 프롬프트를 검색합니다.
     * 제목, 설명, 내용 등에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 검색 결과 프롬프트 목록
     */
    @Override
    public List<PromptTemplate> searchPrompts(String keyword) {
        log.info("Searching prompts with keyword: '{}'", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("Empty keyword provided for search, returning empty results");
            return List.of();
        }
        List<PromptTemplate> searchResults = loadPromptPort.searchPrompts(keyword);
        log.debug("Found {} prompts matching keyword: '{}'", searchResults.size(), keyword);
        return searchResults;
    }

    /**
     * ID로 특정 프롬프트를 조회합니다.
     *
     * @param id 프롬프트 ID
     * @return 해당 ID의 프롬프트 (존재하지 않을 경우 빈 Optional)
     */
    @Override
    public Optional<PromptTemplate> getPromptById(UUID id) {
        log.info("Retrieving prompt by ID: {}", id);
        Optional<PromptTemplate> prompt = loadPromptPort.loadPrompt(id);
        if (prompt.isPresent()) {
            log.debug("Successfully retrieved prompt with ID: {}, title: {}", id, prompt.get().getTitle());
        } else {
            log.debug("No prompt found with ID: {}", id);
        }
        return prompt;
    }
}
