package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.ViewCount;

import java.util.Optional;

/**
 * 조회수 업데이트를 위한 포트 인터페이스
 */
public interface UpdateViewCountPort {

    /**
     * 조회수를 1 증가시킵니다.
     * 해당 프롬프트의 조회수 정보가 없는 경우 새로 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 업데이트된 조회수 정보
     */
    ViewCount incrementViewCount(Long promptTemplateId);

    /**
     * 조회수를 지정된 값만큼 증가시킵니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param count            증가시킬 조회수
     * @return 업데이트된 조회수 정보
     */
    ViewCount incrementViewCount(Long promptTemplateId, long count);

    /**
     * 조회수 정보를 저장하거나 업데이트합니다.
     *
     * @param viewCount 저장할 조회수 정보
     * @return 저장된 조회수 정보
     */
    ViewCount saveOrUpdate(ViewCount viewCount);

    /**
     * 프롬프트의 조회수 정보를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 조회수 정보 (없는 경우 Optional.empty())
     */
    Optional<ViewCount> findByPromptTemplateId(Long promptTemplateId);
}
