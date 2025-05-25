package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.port.in.PromptTagUseCase;
import com.gongdel.promptserver.application.port.out.PromptTagPort;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 프롬프트 템플릿과 태그 연결을 관리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromptTagService implements PromptTagUseCase {

    private final PromptTagPort promptTagPort;

    /**
     * 프롬프트 템플릿에 태그를 연결합니다.
     *
     * @param promptTemplate 태그를 연결할 프롬프트 템플릿
     * @param tags           연결할 태그 목록
     * @return 태그가 연결된 프롬프트 템플릿
     */
    @Override
    public PromptTemplate connectTags(PromptTemplate promptTemplate, Set<Tag> tags) {
        log.debug("태그 연결 서비스 시작. 프롬프트: {}, 태그 수: {}", promptTemplate.getTitle(), tags.size());

        if (tags == null || tags.isEmpty()) {
            return promptTemplate;
        }

        // 태그 ID 세트 추출
        Set<Long> tagIds = tags.stream()
            .filter(tag -> tag.getId() != null)
            .map(Tag::getId)
            .collect(Collectors.toSet());

        // 태그 이름 세트 추출 (ID가 없는 새 태그)
        Set<String> tagNames = tags.stream()
            .filter(tag -> tag.getId() == null)
            .map(Tag::getName)
            .collect(Collectors.toSet());

        // 기존 태그 ID가 있으면 연결
        if (!tagIds.isEmpty()) {
            promptTemplate = promptTagPort.connectTags(promptTemplate.getId(), tagIds);
        }

        // 새 태그 이름이 있으면 생성 및 연결
        if (!tagNames.isEmpty()) {
            promptTemplate = promptTagPort.connectTagsByName(promptTemplate.getId(), tagNames);
        }

        log.debug("태그 연결 서비스 완료. 프롬프트 ID: {}", promptTemplate.getId());
        return promptTemplate;
    }

    /**
     * 프롬프트 템플릿에서 특정 태그를 제거합니다.
     *
     * @param promptTemplate 태그를 제거할 프롬프트 템플릿
     * @param tag            제거할 태그
     * @return 태그가 제거된 프롬프트 템플릿
     */
    @Override
    public PromptTemplate removeTag(PromptTemplate promptTemplate, Tag tag) {
        log.debug("태그 제거 서비스. 프롬프트: {}, 태그: {}", promptTemplate.getTitle(), tag.getName());

        if (tag.getId() == null) {
            log.warn("제거할 태그의 ID가 없습니다. 태그: {}", tag.getName());
            return promptTemplate;
        }

        return promptTagPort.removeTag(promptTemplate.getId(), tag.getId());
    }

    /**
     * 프롬프트 템플릿에서 모든 태그를 제거합니다.
     *
     * @param promptTemplate 태그를 모두 제거할 프롬프트 템플릿
     * @return 태그가 모두 제거된 프롬프트 템플릿
     */
    @Override
    public PromptTemplate clearTags(PromptTemplate promptTemplate) {
        log.debug("모든 태그 제거 서비스. 프롬프트: {}", promptTemplate.getTitle());
        return promptTagPort.clearTags(promptTemplate.getId());
    }
}
