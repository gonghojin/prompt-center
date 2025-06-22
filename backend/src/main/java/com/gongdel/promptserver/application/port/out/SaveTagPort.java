package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.model.Tag;

/**
 * 태그 저장을 위한 포트 인터페이스
 */
public interface SaveTagPort {

    /**
     * 태그를 저장합니다.
     *
     * @param tag 저장할 태그
     * @return 저장된 태그
     */
    Tag saveTag(Tag tag);
}
