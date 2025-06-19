# API 명세 (인증, 카테고리 & 프롬프트)

---

## 인증(Auth) API

| 기능    | HTTP Method | 엔드포인트               | 설명                       | 주요 파라미터/Body                  | 응답 코드         | 예외/특이사항                   |
|-------|-------------|---------------------|--------------------------|-------------------------------|---------------|---------------------------|
| 회원가입  | POST        | `/api/auth/signup`  | 새로운 사용자 회원가입             | body: email, password, name   | 201, 400, 409 | 이메일 중복 시 409(CONFLICT)    |
| 로그인   | POST        | `/api/auth/login`   | 이메일/비밀번호로 로그인, JWT 토큰 발급 | body: email, password         | 200, 400, 401 | 인증 실패 시 401(UNAUTHORIZED) |
| 토큰 갱신 | POST        | `/api/auth/refresh` | 리프레시 토큰으로 액세스 토큰 재발급     | body: refreshToken            | 200, 400, 401 | 리프레시 토큰 오류 시 401          |
| 로그아웃  | POST        | `/api/auth/logout`  | 액세스 토큰 무효화(로그아웃)         | header: Authorization(Bearer) | 200, 401      | 인증되지 않은 요청 시 401          |

### 요청/응답 예시

#### 로그인 요청

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### 로그인 응답

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

#### 회원가입 요청

```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}
```

#### 토큰 갱신 요청

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 카테고리(Category) API

| 기능      | HTTP Method | 엔드포인트                                         | 설명                  | 주요 파라미터                                                      | 응답 코드         | 예외/특이사항               |
|---------|-------------|-----------------------------------------------|---------------------|--------------------------------------------------------------|---------------|-----------------------|
| 카테고리 생성 | POST        | `/api/v1/categories`                          | 새로운 카테고리 생성         | body: name, displayName, ... , header: Authorization(Bearer) | 201, 409, 400 | 이름 중복 시 409(CONFLICT) |
| 카테고리 수정 | PUT         | `/api/v1/categories/{id}`                     | 기존 카테고리 정보 수정       | path: id, body: ... , header: Authorization(Bearer)          | 200, 404, 400 | 미존재 시 404             |
| 카테고리 삭제 | DELETE      | `/api/v1/categories/{id}`                     | 카테고리 삭제             | path: id, header: Authorization(Bearer)                      | 204, 404      | 미존재 시 404             |
| 단건 조회   | GET         | `/api/v1/categories/{id}`                     | ID로 카테고리 조회         | path: id, header: Authorization(Bearer)                      | 200, 404      | 미존재 시 404             |
| 목록 조회   | GET         | `/api/v1/categories`                          | 조건(이름/시스템여부)별 목록 조회 | query: name, isSystem, header: Authorization(Bearer)         | 200           | 결과 없으면 빈 배열 반환        |
| 루트 목록   | GET         | `/api/v1/categories/roots`                    | 최상위(루트) 카테고리 목록 조회  | header: Authorization(Bearer)                                | 200           |                       |
| 하위 목록   | GET         | `/api/v1/categories/{parentId}/subcategories` | 특정 카테고리의 하위 목록 조회   | path: parentId, header: Authorization(Bearer)                | 200, 404      | 상위 미존재 시 404          |

### CategoryResponse 예시

```json
{
  "id": 1,
  "name": "category-key",
  "displayName": "AI",
  "description": "AI 관련 프롬프트 카테고리",
  "parentCategoryId": 10,
  "parentCategoryName": "상위카테고리",
  "isSystem": false,
  "createdAt": "2024-05-01T12:00:00",
  "updatedAt": "2024-05-02T12:00:00"
}
```

| 필드명                | 타입               | 설명          |
|--------------------|------------------|-------------|
| id                 | Long             | 카테고리 ID     |
| name               | String           | 카테고리 고유 이름  |
| displayName        | String           | 화면에 표시될 이름  |
| description        | String           | 카테고리 설명     |
| parentCategoryId   | Long             | 상위 카테고리 ID  |
| parentCategoryName | String           | 상위 카테고리 이름  |
| isSystem           | Boolean          | 시스템 카테고리 여부 |
| createdAt          | String(DateTime) | 생성 일시       |
| updatedAt          | String(DateTime) | 수정 일시       |

---

## 프롬프트(Prompt) API

| 기능      | HTTP Method | 엔드포인트                             | 설명                      | 주요 파라미터/Body                                                                                                                           | 응답 코드                   | 예외/특이사항                        |
|---------|-------------|-----------------------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------|-------------------------|--------------------------------|
| 프롬프트 생성 | POST        | `/api/v1/prompts`                 | 새로운 프롬프트 생성             | body: title, content, ... , header: Authorization(Bearer)                                                                              | 201, 400                | 필수 필드 누락 시 400                 |
| 단건 조회   | GET         | `/api/v1/prompts/{id}`            | UUID로 프롬프트 상세 정보 조회     | path: id, header: Authorization(Bearer)                                                                                                | 200, 404                | 미존재 시 404                      |
| 복합 검색   | GET         | `/api/v1/prompts/advanced-search` | 다양한 조건으로 프롬프트 검색        | query: title, description, tag, categoryId, status, sortType, 페이징, header: Authorization(Bearer)                                       | 200, 400, 500           | 페이징 처리 포함, 기본 status=PUBLISHED |
| 프롬프트 삭제 | DELETE      | `/api/v1/prompts/{id}`            | 프롬프트를 논리적으로 삭제          | path: uuid(UUID), header: Authorization(Bearer)                                                                                        | 200, 400, 401, 403, 404 | 삭제 권한 없음, 미존재 시 404            |
| 프롬프트 수정 | PUT         | `/api/v1/prompts/{id}`            | 기존 프롬프트 정보 수정(소프트 업데이트) | path: id(UUID), body: title, content, description, categoryId, inputVariables, tags, visibility, status, header: Authorization(Bearer) | 200, 400, 401, 403, 404 | 권한 없음 403, 미존재 404, 유효성 오류 400 |

### 프롬프트 생성 요청 필드

- `title` (필수): 프롬프트 제목
- `content` (필수): 프롬프트 내용
- `description` (필수): 프롬프트 설명
- `categoryId` (필수): 카테고리 ID
- `inputVariables`: 입력 변수 목록
- `tags`: 태그 목록
- `visibility`: 가시성 설정
- `status`: 프롬프트 상태
- `createdBy`: 작성자 정보

### 프롬프트 복합 검색 파라미터

- `title` (선택): 프롬프트 제목
- `description` (선택): 프롬프트 설명
- `tag` (선택): 태그
- `categoryId` (선택): 카테고리 ID
- `status` (선택, 기본값: PUBLISHED): 프롬프트 상태
- `sortType` (선택, 기본값: LATEST_MODIFIED): 정렬 기준 (LATEST_MODIFIED, TITLE)
- 페이징 파라미터 (Pageable): page, size 등

### 프롬프트 삭제 요청 예시

```
DELETE /api/v1/prompts/123e4567-e89b-12d3-a456-426614174000
Authorization: Bearer {accessToken}
```

#### 응답 예시

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "deleted": true
}
```

| 응답 필드명  | 타입   | 설명            |
|---------|------|---------------|
| id      | UUID | 삭제된 프롬프트 UUID |
| deleted | Bool | 논리 삭제 성공 여부   |

### 프롬프트 수정 요청 필드

- `title` (필수): 프롬프트 제목
- `content` (필수): 프롬프트 내용
- `description` (필수): 프롬프트 설명
- `categoryId` (필수): 카테고리 ID
- `inputVariables`: 입력 변수 목록
- `tags`: 태그 목록
- `visibility`: 가시성 설정
- `status`: 프롬프트 상태

#### 요청 예시

```json
{
  "title": "수정된 프롬프트 제목",
  "content": "수정된 프롬프트 내용",
  "description": "수정된 설명",
  "categoryId": 2,
  "inputVariables": [
    {
      "name": "userName",
      "type": "string",
      "description": "사용자 이름"
    }
  ],
  "tags": [
    "ai",
    "gpt"
  ],
  "visibility": "PUBLIC",
  "status": "DRAFT"
}
```

#### 응답 예시

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "수정된 프롬프트 제목",
  "content": "수정된 프롬프트 내용",
  "description": "수정된 설명",
  "categoryId": 2,
  "inputVariables": [
    {
      "name": "userName",
      "type": "string",
      "description": "사용자 이름"
    }
  ],
  "tags": [
    "ai",
    "gpt"
  ],
  "visibility": "PUBLIC",
  "status": "DRAFT",
  "updatedAt": "2024-06-10T12:00:00"
}
```

#### 예외 및 오류 코드

- 400: 필수값 누락, 유효성 오류
- 401: 인증 실패
- 403: 수정 권한 없음(작성자/권한자만 가능)
- 404: 프롬프트 미존재

---

## 내 프롬프트(My Prompt) API

| 기능           | HTTP Method | 엔드포인트                           | 설명                    | 주요 파라미터/Body                                                                                                | 응답 코드         | 예외/특이사항        |
|--------------|-------------|---------------------------------|-----------------------|-------------------------------------------------------------------------------------------------------------|---------------|----------------|
| 내 프롬프트 목록 조회 | GET         | `/api/v1/prompts/my`            | 내가 생성한 프롬프트 목록 조회     | query: statusFilters, visibilityFilters, searchKeyword, sortType, page, size, header: Authorization(Bearer) | 200, 400, 500 | 페이징 처리, 필터링 가능 |
| 내 프롬프트 통계 조회 | GET         | `/api/v1/prompts/my/statistics` | 내가 생성한 프롬프트 상태별 통계 조회 | header: Authorization(Bearer)                                                                               | 200, 400, 500 |                |

### 내 프롬프트 목록 조회

#### 요청 예시

```
GET /api/v1/prompts/my?statusFilters=PUBLISHED&visibilityFilters=PUBLIC&page=0&size=20
Authorization: Bearer {accessToken}
```

#### 주요 쿼리 파라미터

| 파라미터명             | 타입     | 필수 | 설명                       |
|-------------------|--------|----|--------------------------|
| statusFilters     | Set    | N  | 프롬프트 상태(PUBLISHED 등) 필터  |
| visibilityFilters | Set    | N  | 공개 범위(PUBLIC 등) 필터       |
| searchKeyword     | String | N  | 검색어(제목/내용 등)             |
| sortType          | String | N  | 정렬 기준(LATEST_MODIFIED 등) |
| page              | int    | N  | 페이지 번호(0부터 시작, 기본 0)     |
| size              | int    | N  | 페이지 크기(기본 20)            |

#### 응답 예시

```json
{
  "content": [
    {
      "id": 1,
      "title": "예시 프롬프트",
      "description": "설명",
      "status": "PUBLISHED",
      "visibility": "PUBLIC",
      "createdAt": "2024-06-01T12:00:00",
      "updatedAt": "2024-06-02T12:00:00",
      "isFavorite": true,
      "isLiked": true,
      "favoriteCount": 10,
      "viewCount": 123
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

---

### 내 프롬프트 통계 조회

#### 요청 예시

```
GET /api/v1/prompts/my/statistics
Authorization: Bearer {accessToken}
```

#### 응답 예시

```json
{
  "totalCount": 10,
  "statusCounts": {
    "PUBLISHED": 7,
    "DRAFT": 3
  }
}
```

---

### 내 프롬프트 총 좋아요 수 통계 조회

#### 요청 예시

```
GET /api/v1/prompts/my/like-statistics
Authorization: Bearer {accessToken}
```

#### 응답 예시

```json
{
  "totalLikeCount": 57
}
```

| 기능                | HTTP Method | 엔드포인트                                | 설명                      | 주요 파라미터/Body                  | 응답 코드 | 예외/특이사항            |
|-------------------|-------------|--------------------------------------|-------------------------|-------------------------------|-------|--------------------|
| 내 프롬프트 총 좋아요 수 조회 | GET         | `/api/v1/prompts/my/like-statistics` | 내가 생성한 프롬프트의 총 좋아요 수 조회 | header: Authorization(Bearer) | 200   | 인증 필요, 서버 오류 시 500 |

| 응답 필드명         | 타입   | 설명                   |
|----------------|------|----------------------|
| totalLikeCount | Long | 내가 생성한 프롬프트의 총 좋아요 수 |

---

## 프롬프트 통계(Prompt Statistics) API

| 기능    | HTTP Method | 엔드포인트                                 | 설명                  | 주요 파라미터                                                                 | 응답 코드         | 예외/특이사항                              |
|-------|-------------|---------------------------------------|---------------------|-------------------------------------------------------------------------|---------------|--------------------------------------|
| 통계 조회 | GET         | `/api/v1/dashboard/prompt-statistics` | 대시보드용 프롬프트 통계 정보 조회 | query: startDate, endDate (필수, ISO-8601), header: Authorization(Bearer) | 200, 400, 500 | endDate < startDate 또는 파라미터 누락 시 400 |

### 요청 예시

```
GET /api/v1/dashboard/prompt-statistics?startDate=2024-06-01T00:00:00&endDate=2024-06-02T00:00:00
```

### 응답 예시

```json
{
  "totalCount": 1500,
  "currentCount": 100,
  "previousCount": 80,
  "percentageChange": 25.0
}
```

| 필드명              | 타입     | 설명            |
|------------------|--------|---------------|
| totalCount       | Long   | 전체 프롬프트 개수    |
| currentCount     | Long   | 현재 기간 프롬프트 개수 |
| previousCount    | Long   | 이전 기간 프롬프트 개수 |
| percentageChange | Double | 변동률(%)        |

---

## 대시보드(Dashboard) API

| 기능                  | HTTP Method | 엔드포인트                                                       | 설명                         | 주요 파라미터/Body          | 응답 코드 | 예외/특이사항              |
|---------------------|-------------|-------------------------------------------------------------|----------------------------|-----------------------|-------|----------------------|
| 최근 프롬프트 조회          | GET         | `/api/v1/dashboard/prompts/recent`                          | 대시보드에서 최근 N개의 프롬프트 조회      | query: pageSize(기본 4) | 200   | pageSize ≤ 0 시 4로 대체 |
| 루트 카테고리별 프롬프트 통계 조회 | GET         | `/api/v1/dashboard/categories/root/statistics`              | 루트 카테고리별 프롬프트 개수 조회        | 없음                    | 200   |                      |
| 하위 카테고리별 프롬프트 통계 조회 | GET         | `/api/v1/dashboard/categories/{rootId}/children/statistics` | 특정 루트의 하위 카테고리별 프롬프트 개수 조회 | path: rootId          | 200   |                      |

### 루트 카테고리별 프롬프트 통계 조회

#### 요청 예시

```
GET /api/v1/dashboard/categories/root/statistics
```

#### 응답 예시

```json
{
  "categories": [
    {
      "categoryId": 1,
      "categoryName": "AI",
      "promptCount": 10
    },
    {
      "categoryId": 2,
      "categoryName": "마케팅",
      "promptCount": 5
    }
  ]
}
```

---

### 하위 카테고리별 프롬프트 통계 조회

#### 요청 예시

```
GET /api/v1/dashboard/categories/1/children/statistics
```

#### path 파라미터

| 파라미터명  | 타입   | 필수 | 설명         |
|--------|------|----|------------|
| rootId | Long | Y  | 루트 카테고리 ID |

#### 응답 예시

```json
{
  "categories": [
    {
      "categoryId": 10,
      "categoryName": "챗봇",
      "promptCount": 3
    },
    {
      "categoryId": 11,
      "categoryName": "이미지 생성",
      "promptCount": 7
    }
  ]
}
```

- 응답은 `CategoryStatisticsResponse` 객체입니다.
- 각 카테고리별로 `categoryId`, `categoryName`, `promptCount` 필드를 포함합니다.

## 공통 안내

- 모든 API는 표준화된 예외 처리 및 로깅 전략을 따릅니다.
- 인증이 필요한 API(카테고리, 프롬프트, 통계 등)는 반드시 'Authorization: Bearer {accessToken}' 헤더를 포함해야 합니다.
- 상세 요청/응답 구조는 Swagger 문서 또는 코드 참고.
- 모든 카테고리 조회 API의 응답은 CategoryResponse 단일 객체 또는 배열(List) 형태입니다.

---

## 즐겨찾기(Favorite) API

| 기능               | HTTP Method | 엔드포인트                                                  | 설명                  | 주요 파라미터/Body                                                                 | 응답 코드                        | 예외/특이사항               |
|------------------|-------------|--------------------------------------------------------|---------------------|------------------------------------------------------------------------------|------------------------------|-----------------------|
| 즐겨찾기 추가          | POST        | `/api/v1/prompts/{id}/favorite`                        | 프롬프트를 즐겨찾기에 추가      | path: id(UUID), header: Authorization(Bearer)                                | 201, 400, 401, 404, 409, 500 | 이미 추가 시 409(CONFLICT) |
| 즐겨찾기 삭제          | DELETE      | `/api/v1/prompts/{id}/favorite`                        | 프롬프트 즐겨찾기 삭제        | path: id(UUID), header: Authorization(Bearer)                                | 204, 400, 401, 404, 500      | 미존재 시 404             |
| 내 즐겨찾기 목록 조회     | GET         | `/api/v1/prompts/my/favorites`                         | 내가 즐겨찾기한 프롬프트 목록 조회 | query: page, size, sort, order, searchKeyword, header: Authorization(Bearer) | 200, 400, 401, 500           | 페이징 처리, 검색/정렬 지원      |
| 내 즐겨찾기 개수 조회     | GET         | `/api/v1/prompts/my/favorites/count`                   | 내가 즐겨찾기한 프롬프트 개수 조회 | header: Authorization(Bearer)                                                | 200, 401, 500                |                       |
| 프롬프트별 즐겨찾기 개수 조회 | GET         | `/api/v1/prompts/my/favorites/count?promptUuid={uuid}` | 특정 프롬프트의 즐겨찾기 개수 조회 | query: promptUuid(UUID), header: Authorization(Bearer)                       | 200, 400, 404, 500           |                       |

### 즐겨찾기 추가 요청/응답 예시

**요청**

```
POST /api/v1/prompts/123e4567-e89b-12d3-a456-426614174000/favorite
Authorization: Bearer {accessToken}
```

**응답**

```json
{
  "id": 1001,
  "promptTemplateId": "123e4567-e89b-12d3-a456-426614174000",
  "createdAt": "2024-03-21T10:00:00Z"
}
```

### 즐겨찾기 삭제 요청/응답 예시

**요청**

```
DELETE /api/v1/prompts/123e4567-e89b-12d3-a456-426614174000/favorite
Authorization: Bearer {accessToken}
```

**응답**

- 204 No Content

### 내 즐겨찾기 목록 조회 예시

**요청**

```
GET /api/v1/prompts/my/favorites?page=0&size=20&sort=createdAt&order=desc
Authorization: Bearer {accessToken}
```

**응답**

```json
{
  "content": [
    {
      "favoriteId": 1001,
      "promptUuid": "123e4567-e89b-12d3-a456-426614174000",
      "title": "예시 프롬프트",
      "description": "설명",
      "tags": ["ai", "stable-diffusion"],
      "createdById": 1,
      "createdByName": "홍길동",
      "categoryId": 2,
      "visibility": "PUBLIC",
      "status": "PUBLISHED",
      "promptCreatedAt": "2024-06-01T12:00:00",
      "promptUpdatedAt": "2024-06-02T12:00:00",
      "favoriteCreatedAt": "2024-06-03T10:00:00",
      "viewCount": 123,
      "favoriteCount": 10,
      "isFavorite": true
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

### 내 즐겨찾기 개수 조회 예시

**요청**

```
GET /api/v1/prompts/my/favorites/count
Authorization: Bearer {accessToken}
```

**응답**

```json
{
  "count": 100
}
```

| 필드명   | 타입   | 설명      |
|-------|------|---------|
| count | Long | 즐겨찾기 개수 |

### 프롬프트별 즐겨찾기 개수 조회 예시

**요청**

```
GET /api/v1/prompts/my/favorites/count?promptUuid=123e4567-e89b-12d3-a456-426614174000
Authorization: Bearer {accessToken}
```

**응답**

```json
5
```

---

## 프롬프트 좋아요(Like) API

| 기능            | HTTP Method | 엔드포인트                              | 설명                             | 주요 파라미터/Body                                     | 응답 코드 | 예외/특이사항             |
|---------------|-------------|------------------------------------|--------------------------------|--------------------------------------------------|-------|---------------------|
| 좋아요 추가        | POST        | `/api/v1/prompts/{id}/like`        | 프롬프트에 좋아요 추가 (Command)         | path: id, header: Authorization(Bearer)          | 200   |                     |
| 좋아요 취소        | DELETE      | `/api/v1/prompts/{id}/like`        | 프롬프트 좋아요 취소 (Command)          | path: id, header: Authorization(Bearer)          | 200   |                     |
| 좋아요 상태/카운트 조회 | GET         | `/api/v1/prompts/{id}/like-status` | 내 좋아요 여부 및 전체 좋아요 수 조회 (Query) | path: id, header: Authorization(Bearer)          | 200   |                     |
| 내가 좋아요한 목록 조회 | GET         | `/api/v1/prompts/liked`            | 내가 좋아요한 프롬프트 목록 조회 (Query)     | query: page, size, header: Authorization(Bearer) | 200   | PageResponse 페이징 반환 |

### 요청/응답 예시

**좋아요 추가/취소 응답**

```json
{
  "success": true,
  "likeCount": 12
}
```

**좋아요 상태/카운트 조회 응답**

```json
{
  "liked": true,
  "likeCount": 12
}
```

**내가 좋아요한 프롬프트 목록 조회 응답**

```json
{
  "content": [
    {
      "promptId": 123,
      "title": "AI 추천 프롬프트",
      "description": "이미지 생성에 최적화된 프롬프트"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false
}
```
