package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.application.port.in.command.CreateCategoryCommand;
import com.gongdel.promptserver.application.port.in.command.UpdateCategoryCommand;
import com.gongdel.promptserver.domain.model.Category;

/**
 * 카테고리 명령(Command) 작업을 처리하는 유스케이스 인터페이스입니다.
 * 이 인터페이스는 헥사고널 아키텍처의 인바운드 포트로,
 * 카테고리 생성, 수정, 삭제 등의 명령 기능을 정의합니다.
 */
public interface CategoryCommandUseCase {

    /**
     * 새 카테고리를 생성합니다.
     *
     * @param command 카테고리 생성 명령 객체
     * @return 생성된 카테고리
     */
    Category createCategory(CreateCategoryCommand command);

    /**
     * 기존 카테고리를 업데이트합니다.
     *
     * @param command 카테고리 업데이트 명령 객체
     * @return 업데이트된 카테고리
     */
    Category updateCategory(UpdateCategoryCommand command);

    /**
     * 카테고리를 삭제합니다.
     *
     * @param id 삭제할 카테고리 ID
     */
    void deleteCategory(Long id);
}
