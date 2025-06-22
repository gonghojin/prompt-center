package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.RecordViewCommand;

/**
 * 조회 기록 저장을 위한 포트 인터페이스
 */
public interface RecordViewPort {

    /**
     * 조회 기록을 저장합니다.
     * 중복 체크를 수행하고 새로운 조회인 경우에만 기록합니다.
     *
     * @param command 조회 기록 명령
     * @return 실제로 조회수가 증가했는지 여부 (중복이 아닌 경우 true)
     */
    boolean recordView(RecordViewCommand command);
}
