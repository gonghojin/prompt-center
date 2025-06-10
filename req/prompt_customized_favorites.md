# 프롬프트 즐겨찾기 기능 세부 작업계획서

## 1. 데이터 모델 상세

### 1.1 Favorite 테이블

| 필드               | 타입       | 설명          | 제약조건                        |
|------------------|----------|-------------|-----------------------------|
| id               | BigInt   | 즐겨찾기 고유 식별자 | Primary Key, Auto Increment |
| userId           | BigInt   | 사용자 ID      | Foreign Key, Not Null       |
| promptTemplateId | BigInt   | 템플릿 ID      | Foreign Key, Not Null       |
| createdAt        | DateTime | 생성 일시       | Not Null                    |

> **유니크 제약:** (userId, promptTemplateId)

### 1.2 인덱스 설계

- PRIMARY KEY (id)
- UNIQUE KEY (userId, promptTemplateId)
- INDEX (userId) - 사용자별 즐겨찾기 조회 최적화
- INDEX (promptTemplateId) - 프롬프트별 즐겨찾기 수 조회 최적화

## 2. API 상세 설계

### 2.1 즐겨찾기 추가

```
POST /api/prompts/{id}/favorite
Request:
- Authorization: Bearer {token}
- Content-Type: application/json

Response: 201 Created
{
    "id": "favorite_id",
    "promptTemplateId": "prompt_id",
    "createdAt": "2024-03-21T10:00:00Z"
}

Error:
- 400: 이미 즐겨찾기한 프롬프트
- 401: 인증되지 않은 요청
- 404: 존재하지 않는 프롬프트
```

### 2.2 즐겨찾기 삭제

```
DELETE /api/prompts/{id}/favorite
Request:
- Authorization: Bearer {token}

Response: 204 No Content

Error:
- 401: 인증되지 않은 요청
- 404: 존재하지 않는 즐겨찾기
```

### 2.3 즐겨찾기 목록 조회

```
GET /api/v1/prompts/my/favorites
Request:
- Authorization: Bearer {token}
- Query Parameters:
  - page: 페이지 번호 (default: 1)
  - size: 페이지 크기 (default: 20)
  - sort: 정렬 기준 (createdAt, title)
  - order: 정렬 순서 (asc, desc)

Response: 200 OK
{
    "items": [
        {
            "id": "favorite_id",
            "promptTemplate": {
                "id": "prompt_id",
                "title": "프롬프트 제목",
                "description": "프롬프트 설명",
                "category": "카테고리",
                "tags": ["태그1", "태그2"],
                "createdBy": {
                    "id": "user_id",
                    "name": "작성자"
                },
                "createdAt": "2024-03-21T10:00:00Z"
            },
            "createdAt": "2024-03-21T10:00:00Z"
        }
    ],
    "total": 100,
    "page": 1,
    "size": 20
}
```

## 3. 캐싱 전략

### 3.1 Redis 캐시 키 설계

- 프롬프트별 즐겨찾기 수: `prompt:{id}:favorites_count`
- 사용자별 즐겨찾기 목록: `user:{id}:favorites`
- 프롬프트 즐겨찾기 상태: `user:{id}:prompt:{id}:favorite`

### 3.2 캐시 갱신 전략

1. 즐겨찾기 추가/삭제 시
    - 해당 프롬프트의 즐겨찾기 수 증가/감소
    - 사용자의 즐겨찾기 목록 캐시 무효화
    - 해당 프롬프트의 즐겨찾기 상태 캐시 갱신

2. 주기적 동기화
    - 5분마다 Redis의 즐겨찾기 수를 DB와 동기화
    - 불일치 발생 시 DB 값으로 캐시 갱신

## 4. 구현 작업 계획

### 4.1 백엔드 구현 (3일)

1. 데이터 모델 및 마이그레이션 (1일)
    - Favorite 테이블 생성
    - 인덱스 설정
    - 마이그레이션 스크립트 작성

2. API 구현 (1일)
    - 즐겨찾기 추가/삭제/조회 API
    - 예외 처리 및 유효성 검사
    - 단위 테스트 작성

3. 캐싱 구현 (1일)
    - Redis 연동
    - 캐시 갱신 로직
    - 동기화 배치 작업

### 4.2 프론트엔드 구현 (2일)

1. UI 컴포넌트 (1일)
    - 즐겨찾기 버튼 컴포넌트
    - 즐겨찾기 목록 컴포넌트
    - 토스트 메시지

2. 상태 관리 및 API 연동 (1일)
    - API 호출 함수
    - 상태 관리 (Redux/Recoil)
    - 낙관적 업데이트

### 4.3 테스트 및 배포 (1일)

1. 통합 테스트
    - API 엔드포인트 테스트
    - 캐시 동작 테스트
    - UI 인터랙션 테스트

2. 배포
    - 마이그레이션 실행
    - 서버 배포
    - 모니터링 설정

## 5. 모니터링 지표

- 즐겨찾기 API 응답 시간
- 캐시 히트율
- 에러율
- 동시 접속자 수
- DB 부하
