# 카테고리 API 목록

| 메서드 | 엔드포인트                                       | 설명                     | 주요 파라미터                                            |
|-----|---------------------------------------------|------------------------|----------------------------------------------------|
| GET | /api/v1/categories/{id}                     | ID로 카테고리 단건 조회         | id (Path)                                          |
| GET | /api/v1/categories                          | 카테고리 목록 조회             | name (Query, optional), isSystem (Query, optional) |
| GET | /api/v1/categories/roots                    | 루트(최상위) 카테고리 목록 조회     | -                                                  |
| GET | /api/v1/categories/{parentId}/subcategories | 특정 카테고리의 하위 카테고리 목록 조회 | parentId (Path)                                    |

---

## 상세 설명

### 1. 카테고리 단건 조회

- **GET** `/api/v1/categories/{id}`
- Path 파라미터: `id` (Long)
- 응답: 카테고리 정보 (없으면 404)

### 2. 카테고리 목록 조회

- **GET** `/api/v1/categories`
- Query 파라미터:
    - `name` (String, optional): 카테고리 이름으로 검색
    - `isSystem` (Boolean, optional): 시스템 카테고리 여부
- 응답: 카테고리 리스트

### 3. 루트 카테고리 목록 조회

- **GET** `/api/v1/categories/roots`
- 응답: 루트 카테고리 리스트

### 4. 하위 카테고리 목록 조회

- **GET** `/api/v1/categories/{parentId}/subcategories`
- Path 파라미터: `parentId` (Long)
- 응답: 하위 카테고리 리스트 (상위 카테고리 없으면 404)

---

## 응답 스키마

### CategoryResponse

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

※ 모든 카테고리 조회 API의 응답은 위 CategoryResponse 단일 객체 또는 배열(List) 형태입니다.
