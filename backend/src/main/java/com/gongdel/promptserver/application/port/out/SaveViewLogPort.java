package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.ViewRecord;

/**
 * 조회 로그 저장을 위한 포트 인터페이스
 */
public interface SaveViewLogPort {

    /**
     * 조회 기록을 저장합니다.
     *
     * @param viewRecord 저장할 조회 기록
     * @return 저장된 조회 기록
     */
    ViewRecord save(ViewRecord viewRecord);
}
