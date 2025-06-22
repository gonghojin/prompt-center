package com.gongdel.promptserver.application.port.out.command;

/**
 * 태그 도메인에 대한 명령(Command) 작업을 정의하는 포트입니다.
 * 태그의 생성, 수정, 삭제와 같은 데이터 변경 작업을 담당합니다.
 */
public interface TagCommandPort {
    /**
     * ID로 태그를 삭제합니다.
     *
     * @param id 삭제할 태그 ID
     * @throws IllegalArgumentException id가 null인 경우
     */
    void deleteById(Long id);
    // 필요시 create, update 등 추가 가능
}
