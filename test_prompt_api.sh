#!/bin/bash

set -e

# 1. 백엔드 서비스 기동 대기
echo "백엔드 서비스 기동 대기 중..."
until curl -s http://localhost:8080/actuator/health | grep '"status":"UP"' > /dev/null; do
  sleep 2
done
echo "백엔드 서비스 기동 완료!"

# 2. 카테고리 생성 (POST)
echo "1. 카테고리 생성"

CAT_NAME="test_prompt_category19"
CAT_DISPLAY="테스트 프롬프트 카테고리"
CAT_DESC="프롬프트 테스트용 카테고리"
CAT_SYS=false
CREATE_CAT=$(jq -n \
  --arg name "$CAT_NAME" \
  --arg displayName "$CAT_DISPLAY" \
  --arg description "$CAT_DESC" \
  --argjson isSystem "$CAT_SYS" \
  '{name: $name, displayName: $displayName, description: $description, isSystem: $isSystem}' | \
  curl -s -X POST http://localhost:8080/api/v1/categories \
    -H "Content-Type: application/json" \
    -d @-)
echo "$CREATE_CAT" | jq .
CAT_ID=$(echo "$CREATE_CAT" | jq '.id')

# 3. 프롬프트 등록 (POST)
echo "2. 프롬프트 등록 테스트"
REQ_TITLE="테스트 프롬프트"
REQ_DESC="테스트용 프롬프트 설명"
REQ_CONTENT="이것은 테스트 프롬프트 내용입니다."
REQ_VARIABLES='{"user":"홍길동","age":30}'
REQ_TAGS='["테스트","AI"]'
REQ_INPUT_VARIABLES='["user","age"]'
REQ_VISIBILITY="PRIVATE"
REQ_STATUS="PUBLISHED"
REQ_CREATED_BY='{"id":"00000000-0000-0000-0000-000000000000","email":"temp@system.local","name":"System User"}'

CREATE_RES=$(jq -n \
  --arg title "$REQ_TITLE" \
  --arg description "$REQ_DESC" \
  --arg content "$REQ_CONTENT" \
  --argjson variablesSchema "$REQ_VARIABLES" \
  --argjson tags "$REQ_TAGS" \
  --argjson categoryId "$CAT_ID" \
  --argjson inputVariables "$REQ_INPUT_VARIABLES" \
  --arg visibility "$REQ_VISIBILITY" \
  --arg status "$REQ_STATUS" \
  --argjson createdBy "$REQ_CREATED_BY" \
  '{title: $title, description: $description, content: $content, variablesSchema: $variablesSchema, tags: $tags, categoryId: $categoryId, inputVariables: $inputVariables, visibility: $visibility, status: $status, createdBy: $createdBy}' | \
  curl -s -X POST http://localhost:8080/api/v1/prompts \
    -H "Content-Type: application/json" \
    -d @-)
echo "[DEBUG] CREATE_RES=$CREATE_RES"
NEW_ID=$(echo "$CREATE_RES" | jq -r '.id')
echo "[DEBUG] NEW_ID=$NEW_ID"

# 생성 결과 검증 (PromptResponse 구조에 맞게)
ACT_TITLE=$(echo "$CREATE_RES" | jq -r '.title')
ACT_DESC=$(echo "$CREATE_RES" | jq -r '.description')
ACT_CONTENT=$(echo "$CREATE_RES" | jq -r '.content')
# tags: 이름만 추출해서 정렬 후 비교
ACT_TAGS=$(echo "$CREATE_RES" | jq -c '[.tags[].name | select(.)] | sort')
REQ_TAGS=$(echo "$REQ_TAGS" | jq -c 'sort')
# author: id, email, name 비교
ACT_AUTHOR_ID=$(echo "$CREATE_RES" | jq -r '.author.id')
ACT_AUTHOR_EMAIL=$(echo "$CREATE_RES" | jq -r '.author.email')
ACT_AUTHOR_NAME=$(echo "$CREATE_RES" | jq -r '.author.name')
REQ_AUTHOR_ID=$(echo "$REQ_CREATED_BY" | jq -r '.id')
REQ_AUTHOR_EMAIL=$(echo "$REQ_CREATED_BY" | jq -r '.email')
REQ_AUTHOR_NAME=$(echo "$REQ_CREATED_BY" | jq -r '.name')
ACT_CATEGORY_ID=$(echo "$CREATE_RES" | jq '.categoryId')
ACT_VISIBILITY=$(echo "$CREATE_RES" | jq -r '.visibility')
ACT_STATUS=$(echo "$CREATE_RES" | jq -r '.status')

ERROR=0
if [[ "$ACT_TITLE" != "$REQ_TITLE" ]]; then
  echo "[ERROR] title 불일치: 기대값='$REQ_TITLE', 실제값='$ACT_TITLE'" >&2
  ERROR=1
fi
if [[ "$ACT_DESC" != "$REQ_DESC" ]]; then
  echo "[ERROR] description 불일치: 기대값='$REQ_DESC', 실제값='$ACT_DESC'" >&2
  ERROR=1
fi
if [[ "$ACT_CONTENT" != "$REQ_CONTENT" ]]; then
  echo "[ERROR] content 불일치: 기대값='$REQ_CONTENT', 실제값='$ACT_CONTENT'" >&2
  ERROR=1
fi
if [[ "$ACT_TAGS" != "$REQ_TAGS" ]]; then
  echo "[ERROR] tags 불일치: 기대값='$REQ_TAGS', 실제값='$ACT_TAGS'" >&2
  ERROR=1
fi
if [[ "$ACT_AUTHOR_ID" != "$REQ_AUTHOR_ID" ]]; then
  echo "[ERROR] author.id 불일치: 기대값='$REQ_AUTHOR_ID', 실제값='$ACT_AUTHOR_ID'" >&2
  ERROR=1
fi
if [[ "$ACT_AUTHOR_EMAIL" != "$REQ_AUTHOR_EMAIL" ]]; then
  echo "[ERROR] author.email 불일치: 기대값='$REQ_AUTHOR_EMAIL', 실제값='$ACT_AUTHOR_EMAIL'" >&2
  ERROR=1
fi
if [[ "$ACT_AUTHOR_NAME" != "$REQ_AUTHOR_NAME" ]]; then
  echo "[ERROR] author.name 불일치: 기대값='$REQ_AUTHOR_NAME', 실제값='$ACT_AUTHOR_NAME'" >&2
  ERROR=1
fi
if [[ "$ACT_CATEGORY_ID" != "$CAT_ID" ]]; then
  echo "[ERROR] categoryId 불일치: 기대값='$CAT_ID', 실제값='$ACT_CATEGORY_ID'" >&2
  ERROR=1
fi
# visibility, status 대문자 변환 비교 (macOS 호환)
toupper() { echo "$1" | tr '[:lower:]' '[:upper:]'; }
if [[ "$(toupper "$ACT_VISIBILITY")" != "$(toupper "$REQ_VISIBILITY")" ]]; then
  echo "[ERROR] visibility 불일치: 기대값='$REQ_VISIBILITY', 실제값='$ACT_VISIBILITY'" >&2
  ERROR=1
fi
if [[ "$(toupper "$ACT_STATUS")" != "$(toupper "$REQ_STATUS")" ]]; then
  echo "[ERROR] status 불일치: 기대값='$REQ_STATUS', 실제값='$ACT_STATUS'" >&2
  ERROR=1
fi
if [[ $ERROR -eq 1 ]]; then
  exit 1
fi

# 4. 프롬프트 단건 조회
echo "3. 프롬프트 단건 조회"
ONE=$(curl -s http://localhost:8080/api/v1/prompts/$NEW_ID)
echo "[DEBUG] ONE=$ONE"
echo "$ONE" | jq .
ONE_TITLE=$(echo "$ONE" | jq -r '.title')
ONE_DESC=$(echo "$ONE" | jq -r '.description')
ONE_CONTENT=$(echo "$ONE" | jq -r '.content')
ONE_TAGS=$(echo "$ONE" | jq -c '[.tags[].name | select(.)] | sort')
ONE_CATEGORY_ID=$(echo "$ONE" | jq '.categoryId')
ONE_INPUT_VARIABLES=$(echo "$ONE" | jq -c '.inputVariables')
ONE_VISIBILITY=$(echo "$ONE" | jq -r '.visibility')
ONE_STATUS=$(echo "$ONE" | jq -r '.status')

if [[ "$ONE_TITLE" != "$REQ_TITLE" || "$ONE_DESC" != "$REQ_DESC" || "$ONE_CONTENT" != "$REQ_CONTENT" || "$ONE_TAGS" != "$REQ_TAGS" || "$ONE_CATEGORY_ID" != "$CAT_ID" || "$(toupper "$ONE_VISIBILITY")" != "$(toupper "$REQ_VISIBILITY")" || "$(toupper "$ONE_STATUS")" != "$(toupper "$REQ_STATUS")" ]]; then
  echo "[ERROR] 단건 조회 데이터 불일치" >&2
  exit 1
fi

# 5. 전체 프롬프트 목록 조회
echo "4. 전체 프롬프트 목록 조회"
ALL_PROMPTS=$(curl -s http://localhost:8080/api/v1/prompts)
echo "$ALL_PROMPTS" | jq .
FOUND=$(echo "$ALL_PROMPTS" | jq -e --arg t "$REQ_TITLE" '.[] | select(.title==$t)')
if [[ -z "$FOUND" ]]; then
  echo "[ERROR] 전체 목록에 생성한 프롬프트가 없음" >&2
  exit 1
fi

# 6. 카테고리별 프롬프트 목록 조회 (정상)
echo "5. 카테고리별 프롬프트 목록 조회 (정상)"
CAT_PROMPTS=$(curl -s "http://localhost:8080/api/v1/prompts/category/$CAT_ID?status=PUBLISHED")
echo "$CAT_PROMPTS" | jq .
FOUND_CAT=$(echo "$CAT_PROMPTS" | jq -e --arg t "$REQ_TITLE" '.[] | select(.title==$t)')
if [[ -z "$FOUND_CAT" ]]; then
  echo "[ERROR] 카테고리별 목록에 생성한 프롬프트가 없음" >&2
  exit 1
fi

# 6. 카테고리별 프롬프트 목록 조회 (비정상 status 파라미터)
echo "6. 카테고리별 프롬프트 목록 조회 (비정상 status 파라미터)"
CAT_PROMPTS_INVALID=$(curl -s "http://localhost:8080/api/v1/prompts/category/$CAT_ID?status=INVALID")
echo "$CAT_PROMPTS_INVALID" | jq .
FOUND_CAT_INVALID=$(echo "$CAT_PROMPTS_INVALID" | jq -e --arg t "$REQ_TITLE" '.[] | select(.title==$t)')
if [[ -z "$FOUND_CAT_INVALID" ]]; then
  echo "[ERROR] 비정상 status 파라미터에도 프롬프트가 조회되어야 함 (기본값 PUBLISHED)" >&2
  exit 1
fi
# status가 실제로 PUBLISHED인지 검증
FOUND_CAT_INVALID_STATUS=$(echo "$CAT_PROMPTS_INVALID" | jq -r --arg t "$REQ_TITLE" '.[] | select(.title==$t) | .status')
if [[ "$(toupper "$FOUND_CAT_INVALID_STATUS")" != "PUBLISHED" ]]; then
  echo "[ERROR] 비정상 status 파라미터 사용 시 실제 status가 PUBLISHED가 아님" >&2
  exit 1
fi

# 7. 없는 카테고리로 조회 (404)
echo "7. 없는 카테고리로 조회 (404)"
NOT_FOUND=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/v1/prompts/category/9999999")
if [[ "$NOT_FOUND" != "404" ]]; then
  echo "[ERROR] 없는 카테고리 조회시 404가 반환되어야 함" >&2
  exit 1
fi

echo "프롬프트 API 통합 테스트 및 데이터 검증 완료!"
