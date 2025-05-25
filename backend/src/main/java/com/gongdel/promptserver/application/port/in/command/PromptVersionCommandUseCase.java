package com.gongdel.promptserver.application.port.in.command;

/**
 * 프롬프트 버전 관련 명령(생성, 수정, 삭제)을 처리하는 유스케이스 인터페이스입니다. 애플리케이션 계층에서 프롬프트 버전의 상태를 변경하는 작업을 정의합니다.
 */
public interface PromptVersionCommandUseCase {

    /**
     * 프롬프트 버전을 생성합니다.
     *
     * @param command 프롬프트 버전 생성에 필요한 정보가 담긴 커맨드 객체
     * @return 생성된 프롬프트 버전의 식별자(ID)
     */
    Long createPromptVersion(CreatePromptVersionCommand command);

    /**
     * 프롬프트 버전을 수정합니다.
     *
     * @param command 프롬프트 버전 수정에 필요한 정보가 담긴 커맨드 객체
     * @return 수정된 프롬프트 버전의 식별자(ID)
     */
    Long updatePromptVersion(UpdatePromptVersionCommand command);

    /**
     * 프롬프트 버전을 삭제합니다.
     *
     * @param id 삭제할 프롬프트 버전의 식별자(ID)
     */
    void deletePromptVersion(Long id);
}
