package com.gongdel.promptserver.adapter.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 공통 페이징 응답 DTO
 *
 * @param <T> 콘텐츠 타입
 */
@Getter
public class PageResponse<T> {
    @Schema(description = "데이터 목록")
    private final List<T> content;
    @Schema(description = "현재 페이지 번호(0부터 시작)")
    private final int page;
    @Schema(description = "페이지 크기")
    private final int size;
    @Schema(description = "전체 데이터 개수")
    private final long totalElements;
    @Schema(description = "전체 페이지 수")
    private final int totalPages;
    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;
    @Schema(description = "이전 페이지 존재 여부")
    private final boolean hasPrevious;

    @Builder
    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean hasNext,
            boolean hasPrevious) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    /**
     * Spring Data Page 객체를 PageResponse로 변환합니다.
     *
     * @param page 변환할 Page 객체
     * @param <T>  데이터 타입
     * @return PageResponse 객체
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
