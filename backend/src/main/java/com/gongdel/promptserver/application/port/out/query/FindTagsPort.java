package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.Tag;

import java.util.List;

/**
 * 태그 도메인의 목록 조회를 담당하는 포트입니다.
 * 여러 태그를 조회하거나 필터링된 태그 목록을 제공합니다.
 */
public interface FindTagsPort {
    /**
     * 모든 태그를 조회합니다.
     *
     * @return 태그 도메인 리스트
     */
    List<Tag> findAllTags();
}
