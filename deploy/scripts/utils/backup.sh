#!/bin/bash

# 데이터베이스 백업 스크립트
# 사용법: ./backup.sh [environment]

set -euo pipefail

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 로깅 함수
log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 파라미터
ENVIRONMENT=${1:-dev}

# 프로젝트 루트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
DEPLOY_DIR="$PROJECT_ROOT/deploy"
BACKUP_DIR="$DEPLOY_DIR/backups"

# 환경변수 파일 확인
ENV_FILE="$DEPLOY_DIR/config/env/$ENVIRONMENT.env"
if [[ ! -f "$ENV_FILE" ]]; then
    log_error "환경변수 파일이 없습니다: $ENV_FILE"
    exit 1
fi

# 환경변수 로드
source "$ENV_FILE"

# 백업 디렉토리 생성
mkdir -p "$BACKUP_DIR"

# 타임스탬프
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

log_info "🗄️  데이터베이스 백업을 시작합니다..."
log_info "   환경: $ENVIRONMENT"
log_info "   시간: $TIMESTAMP"

cd "$PROJECT_ROOT"

# PostgreSQL 백업
log_info "PostgreSQL 백업 중..."
POSTGRES_BACKUP="$BACKUP_DIR/postgres_backup_${TIMESTAMP}.sql"

if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T postgres pg_dump -U "$DB_USERNAME" "$DB_NAME" > "$POSTGRES_BACKUP"; then
    log_success "PostgreSQL 백업 완료: $POSTGRES_BACKUP"

    # 백업 파일 압축
    gzip "$POSTGRES_BACKUP"
    log_info "백업 파일 압축 완료: ${POSTGRES_BACKUP}.gz"
else
    log_error "PostgreSQL 백업 실패"
    exit 1
fi

# Redis 백업
log_info "Redis 백업 중..."
REDIS_BACKUP="$BACKUP_DIR/redis_backup_${TIMESTAMP}.rdb"

if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T redis redis-cli --rdb /tmp/dump.rdb > /dev/null 2>&1; then
    docker cp $(docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps -q redis):/tmp/dump.rdb "$REDIS_BACKUP"
    log_success "Redis 백업 완료: $REDIS_BACKUP"

    # 백업 파일 압축
    gzip "$REDIS_BACKUP"
    log_info "백업 파일 압축 완료: ${REDIS_BACKUP}.gz"
else
    log_error "Redis 백업 실패"
    exit 1
fi

# 프론트엔드 빌드 파일 백업 (선택적)
if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps | grep -q "prompth-frontend"; then
    log_info "프론트엔드 빌드 파일 백업 중..."
    FRONTEND_BACKUP="$BACKUP_DIR/frontend_backup_${TIMESTAMP}.tar"

    # 프론트엔드 컨테이너에서 빌드 파일 백업
    if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T frontend tar -cf /tmp/frontend_build.tar /app/.next /app/public 2>/dev/null; then
        docker cp $(docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps -q frontend):/tmp/frontend_build.tar "$FRONTEND_BACKUP"
        log_success "프론트엔드 백업 완료: $FRONTEND_BACKUP"

        # 백업 파일 압축
        gzip "$FRONTEND_BACKUP"
        log_info "백업 파일 압축 완료: ${FRONTEND_BACKUP}.gz"
    else
        log_warning "프론트엔드 백업 실패 (무시됨)"
    fi
fi

# 7일 이상 된 백업 파일 정리
log_info "오래된 백업 파일 정리 중..."
find "$BACKUP_DIR" -name "*.gz" -mtime +7 -delete 2>/dev/null || true
log_info "7일 이상 된 백업 파일이 정리되었습니다."

# 백업 결과 요약
BACKUP_SIZE=$(du -sh "$BACKUP_DIR" | cut -f1)
BACKUP_COUNT=$(find "$BACKUP_DIR" -name "*.gz" | wc -l)

log_success ""
log_success "🎉 백업이 성공적으로 완료되었습니다!"
log_success ""
log_success "📊 백업 정보:"
log_success "   • PostgreSQL: ${POSTGRES_BACKUP}.gz"
log_success "   • Redis: ${REDIS_BACKUP}.gz"
if [[ -n "${FRONTEND_BACKUP:-}" && -f "${FRONTEND_BACKUP}.gz" ]]; then
    log_success "   • 프론트엔드: ${FRONTEND_BACKUP}.gz"
fi
log_success "   • 전체 백업 크기: $BACKUP_SIZE"
log_success "   • 보관 중인 백업 파일 수: $BACKUP_COUNT"
log_success ""
log_success "📁 백업 디렉토리: $BACKUP_DIR"
log_success "🗓️  보관 정책: 7일 (이후 자동 삭제)"
log_success ""
