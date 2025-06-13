# 프롬프트 수정(소프트 업데이트) 기능 작업 계획

## 1. 요구사항 및 모델 분석 요약

- 프롬프트(PromptTemplate)는 수정 시 PromptVersion에 EDIT 이력이 남아야 함
- 작성자/권한자만 수정 가능, 수정 시 새 버전 생성
- 상태/가시성/태그/변수 등 주요 필드 수정 가능
- API: PUT /api/v1/prompts/{uuid} (예상)
- 예외: 권한 없음(403), 미존재(404), 유효성 오류(400)

## 1.1. 버전 상태 관리 상세

- **PromptTemplate.status**: 프롬프트의 현재 상태 관리
    - DRAFT: 초안 상태
    - PUBLISHED: 발행된 상태
    - ARCHIVED: 보관된 상태
    - DELETED: 삭제된 상태

- **PromptVersion.actionType**: 버전 생성 이유/목적 관리
    - CREATE: 최초 생성 (versionNumber=1)
    - EDIT: 내용 수정 (versionNumber 증가)
    - PUBLISH: 발행/공개
    - ARCHIVE: 보관 처리

- **버전 상태 흐름**
  ```mermaid
  stateDiagram-v2
      [*] --> DRAFT: CREATE
      DRAFT --> DRAFT: EDIT
      DRAFT --> PUBLISHED: PUBLISH
      PUBLISHED --> DRAFT: EDIT
      PUBLISHED --> ARCHIVED: ARCHIVE
      ARCHIVED --> DELETED: DELETE
  ```

- **버전 관리 규칙**
    1. 수정 시 새로운 버전 생성 (EDIT actionType)
    2. 이전 버전은 히스토리로 보관
    3. versionNumber는 순차적으로 증가
    4. 변경 사항은 changes 필드에 기록

## 1.2. 상태별 시나리오 정리

### 1) 최초 생성 (CREATE)

- PromptTemplate.status: DRAFT
- PromptVersion.actionType: CREATE
- 설명: 최초 프롬프트 생성 시, 템플릿은 DRAFT 상태로 시작하며, versionNumber=1의 CREATE 버전이 생성됨.

### 2) 수정 (EDIT)

- PromptTemplate.status: DRAFT 또는 PUBLISHED
- PromptVersion.actionType: EDIT
- 설명: 프롬프트를 수정하면 새로운 EDIT 버전이 생성되고, 이전 버전은 히스토리로 남음. 템플릿 상태는 DRAFT(미발행) 또는 PUBLISHED(발행 중)일 수 있음.

### 3) 발행 (PUBLISH)

- PromptTemplate.status: PUBLISHED
- PromptVersion.actionType: PUBLISH
- 설명: DRAFT 상태의 템플릿을 발행하면, 새로운 PUBLISH 버전이 생성되고 템플릿 상태가 PUBLISHED로 변경됨.

### 4) 보관 (ARCHIVE)

- PromptTemplate.status: ARCHIVED
- PromptVersion.actionType: ARCHIVE
- 설명: 더 이상 사용하지 않는 템플릿을 보관 처리하면, ARCHIVE 버전이 생성되고 템플릿 상태가 ARCHIVED로 변경됨.

### 5) 삭제 (DELETE)

- PromptTemplate.status: DELETED
- PromptVersion.actionType: (없음, 관리 목적상 상태만 변경)
- 설명: 템플릿을 삭제하면 status만 DELETED로 변경되고, 별도의 버전 이력은 남기지 않음.

## 1.3. 상태 변경만 있을 때의 버전 관리

- 템플릿의 내용(content)은 변경하지 않고 PromptTemplate.status만 변경하는 경우(예: DRAFT → PUBLISHED, PUBLISHED → ARCHIVED 등)에도, 상태 변경 자체가 프롬프트의 중요한 라이프사이클 이벤트이므로 PromptVersion에 해당 actionType(PUBLISH, ARCHIVE 등) 이력을 반드시 남긴다.
- 이때 versionNumber는 증가하며, content는 이전 버전과 동일하게 복사된다.
- changes 필드에는 "상태 변경: DRAFT → PUBLISHED" 등 상태 변경 내역을 명확히 기록한다.

### 예시

```json
// 기존 버전 (DRAFT)
{
  "versionNumber": 2,
  "actionType": "EDIT",
  "content": "최신 프롬프트 내용",
  "changes": "내용 일부 수정",
  "createdAt": "2024-06-10T10:00:00Z"
}

// 상태만 변경 (DRAFT → PUBLISHED)
{
  "versionNumber": 3,
  "actionType": "PUBLISH",
  "content": "최신 프롬프트 내용", // 내용 동일
  "changes": "상태 변경: DRAFT → PUBLISHED",
  "createdAt": "2024-06-10T11:00:00Z"
}
```

- 이렇게 하면 모든 상태 변경 이력이 명확하게 남고, 프롬프트의 라이프사이클을 완벽하게 추적할 수 있다.

## 1.4. 화면 버튼(임시저장/저장하고 게시) 동작별 상태 관리

### 1) 임시저장(초안 저장) 버튼 클릭 시

- **PromptTemplate.status**: DRAFT
- **PromptVersion.actionType**: CREATE
- **설명**: 프롬프트를 처음 작성하고 임시저장(초안 저장)하면, 템플릿은 DRAFT 상태로 남아있고 최초 버전의 actionType은 CREATE로 기록된다. 이때 외부에는 노출되지 않으며, 작성자만 접근 가능하다.

### 2) 저장하고 게시(발행) 버튼 클릭 시

- **PromptTemplate.status**: PUBLISHED
- **PromptVersion.actionType**: PUBLISH
- **설명**: 프롬프트를 바로 게시(발행)하면, 템플릿의 status는 PUBLISHED로 변경되고 새로운 버전이 생성되며 actionType은 PUBLISH로 기록된다. 이때 게시 범위(visibility)에 따라 팀/전체에 노출된다.

### 3) 임시저장 후 나중에 게시한 경우

- 최초 임시저장: status=DRAFT, actionType=CREATE, versionNumber=1
- 게시: status=PUBLISHED, actionType=PUBLISH, versionNumber=2

#### 시나리오 표

| 동작        | PromptTemplate.status | PromptVersion.actionType | 비고                |
|-----------|-----------------------|--------------------------|-------------------|
| 최초 임시저장   | DRAFT                 | CREATE                   | versionNumber=1   |
| 최초 게시     | PUBLISHED             | PUBLISH                  | versionNumber=1   |
| 임시저장 후 게시 | DRAFT → PUBLISHED     | CREATE → PUBLISH         | versionNumber=1,2 |

- 임시저장(초안 저장): status는 DRAFT, actionType은 CREATE(최초), EDIT(수정 시)
- 저장하고 게시(발행): status는 PUBLISHED, actionType은 PUBLISH(항상 새로운 버전 생성)

이렇게 관리하면 프롬프트의 라이프사이클과 이력이 명확하게 남고, 화면의 버튼 동작과 데이터 모델이 일관성 있게 연결된다.

## 2. 설계 및 구현 단계

### 2.1. Request/Response/Command 클래스 정의

- UpdatePromptRequest: 수정 요청 DTO (title, content, description, categoryId, tags, inputVariables, visibility, status 등)
- UpdatePromptResponse: 응답 DTO (수정된 프롬프트 정보)
- UpdatePromptCommand: 유스케이스용 커맨드

### 2.2. Controller에 수정 API 추가

- @PutMapping("/{id}")
- 입력값 검증, 현재 사용자 정보 조회, 커맨드 생성, 유스케이스 호출, 결과 반환

### 2.3. Application Layer(UseCase) 수정 메서드 구현

- updatePrompt(UpdatePromptCommand) 메서드 작성
- 프롬프트 존재/권한 확인, 변경 내용 적용, PromptVersion에 EDIT 이력 추가, PromptTemplateEntity 업데이트

### 2.4. Persistence Layer(Repository/Adapter) 수정 로직 구현

- PromptTemplateEntity: 필드 업데이트
- PromptVersionEntity: 새 버전 추가 (EDIT)
- 트랜잭션 처리

### 2.5. 권한/유효성/예외 처리

- 작성자/권한자만 수정 가능
- 필수값 검증, 중복, 상태 등
- 예외: 400/403/404/409 등

### 2.6. 테스트 코드 작성

- 단위/통합 테스트

### 2.7. 문서화 및 기타

- Swagger/OpenAPI 문서화
- 로깅/트랜잭션/예외 처리 일관성 유지

## 3. 추가 고려사항

- 버전 관리: 수정 시 PromptVersionEntity에 EDIT 이력 반드시 남길 것
- 상태/가시성/태그/변수 등 모든 주요 필드 수정 가능
- 로깅/트랜잭션/예외 처리 일관성 유지
