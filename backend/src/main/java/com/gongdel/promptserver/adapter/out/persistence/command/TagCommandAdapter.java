package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.application.port.out.command.TagCommandPort;
import com.gongdel.promptserver.domain.exception.TagErrorType;
import com.gongdel.promptserver.domain.exception.TagOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 태그 도메인의 명령(Command) 작업을 구현하는 어댑터입니다.
 * 태그의 삭제와 같은 데이터 변경 작업을 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TagCommandAdapter implements TagCommandPort {
    private final TagRepository tagRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        Assert.notNull(id, "Tag ID must not be null");
        log.debug("Deleting tag with id: {}", id);
        try {
            tagRepository.deleteById(id);
            log.debug("Successfully deleted tag with id: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete tag with id: {}", id, e);
            throw new TagOperationException(TagErrorType.OPERATION_ERROR, "Failed to delete tag", e);
        }
    }
}
