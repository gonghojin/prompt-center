package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.application.port.in.query.LoadPromptDetailQuery;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptDetail;
import com.gongdel.promptserver.domain.model.PromptTemplate;

import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 템플릿 단건 조회 포트
 */
public interface LoadPromptPort {

    /**
     * 프롬프트 템플릿을 ID로 조회합니다.
     *
     * @param id 프롬프트 템플릿의 PK
     * @return 조회된 프롬프트 템플릿 (Optional)
     * @throws PromptOperationException 조회 실패 시 예외 발생
     */
    Optional<PromptTemplate> loadPromptById(Long id);

    /**
     * 프롬프트 템플릿을 UUID로 조회합니다.
     *
     * @param uuid 프롬프트 템플릿의 UUID
     * @return 조회된 프롬프트 템플릿 (없는 경우 빈 Optional 반환)
     */
    Optional<PromptTemplate> loadPromptByUuid(UUID uuid);

    /**
     * 프롬프트 상세 정보를 UUID로 조회합니다.
     *
     * @param command 프롬프트 상세 정보를 조회하기 위한 명령
     * @return 조회된 프롬프트 상세 정보 (없는 경우 빈 Optional 반환)
     */
    Optional<PromptDetail> loadPromptDetailBy(LoadPromptDetailQuery command);

}
