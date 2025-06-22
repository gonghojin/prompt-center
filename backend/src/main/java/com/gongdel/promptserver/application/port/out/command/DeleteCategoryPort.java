package com.gongdel.promptserver.application.port.out.command;

/**
 * 카테고리 삭제를 위한 포트입니다.
 */
public interface DeleteCategoryPort {

    /**
     * 카테고리를 삭제합니다.
     *
     * @param id 삭제할 카테고리 ID
     */
    void deleteCategory(Long id);
}
