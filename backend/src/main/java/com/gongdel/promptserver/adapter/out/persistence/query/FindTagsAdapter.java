package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.TagMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.application.port.out.query.FindTagsPort;
import com.gongdel.promptserver.domain.model.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 태그 도메인의 목록 조회를 구현하는 어댑터입니다.
 * 여러 태그를 조회하거나 필터링된 태그 목록을 제공합니다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FindTagsAdapter implements FindTagsPort {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> findAllTags() {
        log.debug("Finding all tags");
        List<Tag> tags = tagRepository.findAll().stream()
            .map(tagMapper::toDomain)
            .collect(Collectors.toList());
        log.debug("Found {} tags", tags.size());
        return tags;
    }
}
