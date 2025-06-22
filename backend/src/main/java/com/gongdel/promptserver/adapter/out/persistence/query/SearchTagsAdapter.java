package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.application.port.out.query.SearchTagsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 태그 도메인의 검색 및 존재 여부 확인을 구현하는 어댑터입니다.
 * 태그의 존재 여부나 검색 조건에 따른 조회 기능을 제공합니다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SearchTagsAdapter implements SearchTagsPort {
    private final TagRepository tagRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByName(String name) {
        Assert.hasText(name, "Tag name must not be empty");
        log.debug("Checking existence of tag with name: {}", name);
        boolean exists = tagRepository.existsByName(name);
        log.debug("Tag with name {} exists: {}", name, exists);
        return exists;
    }
}
