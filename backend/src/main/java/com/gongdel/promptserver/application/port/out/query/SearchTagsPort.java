package com.gongdel.promptserver.application.port.out.query;

/**
 * 태그 도메인의 검색 및 존재 여부 확인을 담당하는 포트입니다.
 * 태그의 존재 여부나 검색 조건에 따른 조회 기능을 제공합니다.
 */
public interface SearchTagsPort {
    /**
     * 태그 이름으로 존재 여부를 확인합니다.
     *
     * @param name 확인할 태그 이름
     * @return 존재 여부
     * @throws IllegalArgumentException name이 null이거나 빈 문자열인 경우
     */
    boolean existsByName(String name);
}
