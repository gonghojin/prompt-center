# 🗂️ 프롬프트 논리 삭제(Soft Delete) 정책 적용 작업 계획

---

## 1. 정책 개요

- 프롬프트 삭제 시 DB에서 실제로 삭제하지 않고, status 필드를 DELETED로 변경(논리 삭제)
- 일반 조회/검색에서는 DELETED 상태를 제외
- 관리자/생성자 등은 DELETED 상태도 조회/복구 가능(옵션)

---

## 2. 전체 작업 단계

### [1] 삭제 API 추가

- PromptCommandController: DELETE /api/v1/prompts/{uuid} 엔드포인트 추가
- PromptCommandUseCase: deletePrompt(UUID uuid, User currentUser) 메서드 추가
- PromptCommandService: 논리 삭제 비즈니스 로직 구현(권한 체크, status 변경)
- PromptPersistenceAdapter: status만 DELETED로 변경하여 저장하는 로직 구현
- PromptTemplateJpaRepository: uuid로 조회/저장 메서드 활용

### [2] 조회/검색 API 수정

- status != DELETED 조건이 모든 조회/검색에 반영되어야 함(이미 대부분 status 조건 있음, 추가 확인 필요)

### [3] 복구/관리자 기능(선택)

- 필요 시, DELETED → 기존 상태로 복구하는 API/로직 추가

---

## 3. 클래스별 상세 작업 계획

### 1) PromptCommandController.java

- [추가] @DeleteMapping("/api/v1/prompts/{uuid}")
- deletePrompt(UUID uuid) 메서드 추가
- 삭제 성공/실패 응답 처리

### 2) PromptCommandUseCase.java

- [추가] void deletePrompt(UUID uuid, User currentUser);

### 3) PromptCommandService.java

- [구현] deletePrompt(UUID uuid, User currentUser) 메서드 구현
- uuid로 프롬프트 조회 → 권한 체크 → status = DELETED로 변경 → 저장
- 삭제 이력/로그 기록(옵션)

### 4) PromptPersistenceAdapter.java

- [구현] 프롬프트 status만 DELETED로 변경하여 저장하는 메서드 구현
- 예외/유효성 처리

### 5) PromptTemplateJpaRepository.java

- [활용] findByUuid(UUID uuid) 등 기존 메서드 활용
- 필요시, status 업데이트용 커스텀 쿼리 추가 가능

---

## 4. 기타 고려사항

- 삭제 권한(생성자/관리자 등) 반드시 체크
- 복구/관리자 기능은 선택적으로 구현 가능
- 삭제 요청 시 실제로는 status만 변경(논리 삭제)
- 조회/검색 시 status != DELETED 조건 필수

---
