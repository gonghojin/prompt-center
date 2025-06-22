package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.application.port.in.command.view.RecordViewCommand;

/**
 * 조회수 기록 관련 명령 작업을 처리하는 유스케이스 인터페이스입니다.
 * 이 인터페이스는 헥사고널 아키텍처의 인바운드 포트로,
 * 조회수 기록 생성 기능을 정의합니다.
 */
public interface ViewCommandUseCase {

    /**
     * 프롬프트 조회를 기록합니다.
     * Redis를 통한 실시간 중복 체크와 데이터베이스 영구 저장을 모두 수행합니다.
     *
     * @param command 조회 기록 명령 객체
     * @return 업데이트된 총 조회수
     */
    long recordView(RecordViewCommand command);
}
