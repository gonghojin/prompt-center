package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;

/**
 * 프롬프트 템플릿 등록을 위한 유스케이스 인터페이스입니다.
 * 이 인터페이스는 헥사고널 아키텍처의 인바운드 포트로서,
 * 외부 어댑터가 애플리케이션 코어와 상호작용하기 위해 사용됩니다.
 */
public interface PromptCommandUseCase {

    /**
     * 새로운 프롬프트 템플릿을 등록합니다.
     *
     * @param command 프롬프트 등록에 필요한 정보를 담은 커맨드 객체
     * @return 등록된 프롬프트 정보 응답 DTO
     */
    RegisterPromptResponse registerPrompt(RegisterPromptCommand command);
}
