package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 모든 도메인 모델의 기본 클래스
 * 생성 시간과 수정 시간을 포함합니다.
 */
@Getter
public abstract class BaseTimeEntity {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected BaseTimeEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected BaseTimeEntity(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    /**
     * 업데이트 시간을 현재 시간으로 갱신합니다.
     * 이 메서드는 도메인 모델 내부에서만 사용해야 합니다.
     */
    protected void updateModifiedTime() {
        this.updatedAt = LocalDateTime.now();
    }
}
