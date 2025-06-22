package com.gongdel.promptserver.domain.view;

/**
 * 조회 기록 키 생성 전략을 정의하는 도메인 포트입니다.
 * <p>
 * 헥사고날 아키텍처의 포트 역할을 하며,
 * 도메인이 인프라스트럭처에 의존하지 않도록 합니다.
 */
public interface ViewKeyStrategy {

    /**
     * 중복 체크를 위한 키를 생성합니다.
     *
     * @param identifier 조회 식별자
     * @return 중복 체크용 키
     */
    String createDuplicationCheckKey(ViewIdentifier identifier);

    /**
     * 조회수 캐시를 위한 키를 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 조회수 캐시 키
     */
    String createViewCountKey(Long promptTemplateId);

    /**
     * 중복 체크 TTL을 반환합니다 (시간 단위).
     *
     * @return 중복 체크 TTL
     */
    long getDuplicationCheckTtl();

    /**
     * 조회수 캐시 TTL을 반환합니다 (시간 단위).
     *
     * @return 조회수 캐시 TTL
     */
    long getCountCacheTtl();

    /**
     * 조회수 키 패턴을 반환합니다.
     *
     * @return 조회수 키 패턴
     */
    String getViewCountKeyPattern();

    /**
     * 조회수 키에서 프롬프트 ID를 추출합니다.
     *
     * @param key 조회수 키
     * @return 프롬프트 ID
     */
    Long extractPromptIdFromViewCountKey(String key);
}
