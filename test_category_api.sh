#!/bin/bash

set -e

# 1. 백엔드 서비스 기동 대기
echo "백엔드 서비스 기동 대기 중..."
until curl -s http://localhost:8080/actuator/health | grep '"status":"UP"' > /dev/null; do
  sleep 2
done
echo "백엔드 서비스 기동 완료!"

# 2. 카테고리 생성 (POST)
echo "1. 카테고리 생성 테스트"
REQ_NAME="test_category"
REQ_DISPLAY="테스트 카테고리"
REQ_DESC="테스트용 카테고리"
REQ_SYS=true
CREATE_RES=$(jq -n \
  --arg name "$REQ_NAME" \
  --arg displayName "$REQ_DISPLAY" \
  --arg description "$REQ_DESC" \
  --argjson isSystem "$REQ_SYS" \
  '{name: $name, displayName: $displayName, description: $description, isSystem: $isSystem}' | \
  curl -s -X POST http://localhost:8080/api/v1/categories \
    -H "Content-Type: application/json" \
    -d @-)
echo "$CREATE_RES" | jq .
NEW_ID=$(echo "$CREATE_RES" | jq '.id')

# 생성 결과 검증
ACT_NAME=$(echo "$CREATE_RES" | jq -r '.name')
ACT_DISPLAY=$(echo "$CREATE_RES" | jq -r '.displayName')
ACT_DESC=$(echo "$CREATE_RES" | jq -r '.description')
ACT_SYS=$(echo "$CREATE_RES" | jq -r '.isSystem')
if [[ "$ACT_NAME" != "$REQ_NAME" || "$ACT_DISPLAY" != "$REQ_DISPLAY" || "$ACT_DESC" != "$REQ_DESC" || "$ACT_SYS" != "$REQ_SYS" ]]; then
  echo "[ERROR] 카테고리 생성 응답 데이터 불일치" >&2
  exit 1
fi

# 3. 전체 카테고리 목록 조회
echo "2. 전체 카테고리 목록 조회"
ALL_CATEGORIES=$(curl -s http://localhost:8080/api/v1/categories)
echo "$ALL_CATEGORIES" | jq .
# 목록에 생성한 카테고리 포함 여부 확인
FOUND=$(echo "$ALL_CATEGORIES" | jq -e --arg n "$REQ_NAME" '.[] | select(.name==$n)')
if [[ -z "$FOUND" ]]; then
  echo "[ERROR] 전체 목록에 생성한 카테고리가 없음" >&2
  exit 1
fi

# 4. 생성한 카테고리 단건 조회
echo "3. 생성한 카테고리 단건 조회"
ONE=$(curl -s http://localhost:8080/api/v1/categories/$NEW_ID)
echo "$ONE" | jq .
ONE_NAME=$(echo "$ONE" | jq -r '.name')
ONE_DISPLAY=$(echo "$ONE" | jq -r '.displayName')
ONE_DESC=$(echo "$ONE" | jq -r '.description')
ONE_SYS=$(echo "$ONE" | jq -r '.isSystem')
if [[ "$ONE_NAME" != "$REQ_NAME" || "$ONE_DISPLAY" != "$REQ_DISPLAY" || "$ONE_DESC" != "$REQ_DESC" || "$ONE_SYS" != "$REQ_SYS" ]]; then
  echo "[ERROR] 단건 조회 데이터 불일치" >&2
  exit 1
fi

# 5. 최상위 카테고리 조회
echo "4. 최상위 카테고리 조회"
ROOTS=$(curl -s http://localhost:8080/api/v1/categories/roots)
echo "$ROOTS" | jq .
ROOT_FOUND=$(echo "$ROOTS" | jq -e --arg n "$REQ_NAME" '.[] | select(.name==$n)')
if [[ -z "$ROOT_FOUND" ]]; then
  echo "[ERROR] 최상위 목록에 생성한 카테고리가 없음" >&2
  exit 1
fi

# 6. 하위 카테고리 생성 및 조회
echo "5. 하위 카테고리 생성"
SUB_REQ_NAME="test_subcategory"
SUB_REQ_DISPLAY="테스트 하위"
SUB_REQ_DESC="테스트 하위 카테고리"
SUB_REQ_SYS=false
SUB_CREATE=$(jq -n \
  --arg name "$SUB_REQ_NAME" \
  --arg displayName "$SUB_REQ_DISPLAY" \
  --arg description "$SUB_REQ_DESC" \
  --argjson isSystem "$SUB_REQ_SYS" \
  --argjson parentCategoryId "$NEW_ID" \
  '{name: $name, displayName: $displayName, description: $description, isSystem: $isSystem, parentCategoryId: $parentCategoryId}' | \
  curl -s -X POST http://localhost:8080/api/v1/categories \
    -H "Content-Type: application/json" \
    -d @-)
echo "$SUB_CREATE" | jq .
SUB_ID=$(echo "$SUB_CREATE" | jq '.id')
# 하위 생성 결과 검증
SUB_ACT_NAME=$(echo "$SUB_CREATE" | jq -r '.name')
SUB_ACT_DISPLAY=$(echo "$SUB_CREATE" | jq -r '.displayName')
SUB_ACT_DESC=$(echo "$SUB_CREATE" | jq -r '.description')
SUB_ACT_SYS=$(echo "$SUB_CREATE" | jq -r '.isSystem')
SUB_ACT_PARENT=$(echo "$SUB_CREATE" | jq -r '.parentCategoryId')
if [[ "$SUB_ACT_NAME" != "$SUB_REQ_NAME" || "$SUB_ACT_DISPLAY" != "$SUB_REQ_DISPLAY" || "$SUB_ACT_DESC" != "$SUB_REQ_DESC" || "$SUB_ACT_SYS" != "$SUB_REQ_SYS" || "$SUB_ACT_PARENT" != "$NEW_ID" ]]; then
  echo "[ERROR] 하위 카테고리 생성 응답 데이터 불일치" >&2
  exit 1
fi

echo "6. 하위 카테고리 조회"
SUBS=$(curl -s http://localhost:8080/api/v1/categories/$NEW_ID/subcategories)
echo "$SUBS" | jq .
SUB_FOUND=$(echo "$SUBS" | jq -e --arg n "$SUB_REQ_NAME" '.[] | select(.name==$n)')
if [[ -z "$SUB_FOUND" ]]; then
  echo "[ERROR] 하위 목록에 생성한 하위 카테고리가 없음" >&2
  exit 1
fi

echo "통합 테스트 및 데이터 검증 완료!"
