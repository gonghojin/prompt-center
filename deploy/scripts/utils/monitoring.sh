#!/bin/bash

# 서비스 모니터링 스크립트
# 사용법: ./monitoring.sh [environment]

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

# OS 확인
OS=$(uname)

# 날짜 계산 함수 (OS 호환)
calculate_days_left() {
    local cert_file="$1"
    local expire_date
    local days_left

    expire_date=$(openssl x509 -enddate -noout -in "$cert_file" | cut -d= -f2)

    if [[ "$OS" == "Darwin" ]]; then
        # MacOS
        expire_ts=$(date -j -f "%b %e %H:%M:%S %Y %Z" "$expire_date" "+%s" 2>/dev/null)
        if [ $? -ne 0 ]; then
            # 다른 형식 시도
            expire_ts=$(date -j -f "%b %d %H:%M:%S %Y %Z" "$expire_date" "+%s" 2>/dev/null)
        fi
        current_ts=$(date -j "+%s")
    else
        # Linux
        expire_ts=$(date -d "$expire_date" +%s)
        current_ts=$(date +%s)
    fi

    days_left=$(( (expire_ts - current_ts) / 86400 ))
    echo "$days_left"
}

# 파라미터
ENVIRONMENT=${1:-dev}

# 프로젝트 루트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
DEPLOY_DIR="$PROJECT_ROOT/deploy"

# 환경변수 파일 확인
ENV_FILE="$DEPLOY_DIR/config/env/$ENVIRONMENT.env"
if [[ ! -f "$ENV_FILE" ]]; then
    log_error "환경변수 파일이 없습니다: $ENV_FILE"
    exit 1
fi

cd "$PROJECT_ROOT"

log_info "📊 Prompth Center 서비스 모니터링"
log_info "   환경: $ENVIRONMENT"
log_info "   시간: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 컨테이너 상태 확인
log_info "🐳 컨테이너 상태:"
docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps
echo ""

# 서비스 헬스체크
log_info "🔍 서비스 헬스체크:"

check_container() {
    local name="$1"
    docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps --quiet "$name" 2>/dev/null
}

# Nginx 상태
if [ -n "$(check_container nginx)" ]; then
    if curl -f -s -o /dev/null http://localhost/actuator/health; then
        log_success "✅ Nginx: 정상"
    else
        log_error "❌ Nginx: 오류"
    fi
else
    log_warning "⚠️ Nginx: 실행되지 않음"
fi

# 프론트엔드 상태
if [ -n "$(check_container frontend)" ]; then
    if curl -f -s -o /dev/null http://localhost/api/health; then
        log_success "✅ 프론트엔드: 정상"
    else
        log_error "❌ 프론트엔드: 오류"
    fi
else
    log_warning "⚠️ 프론트엔드: 실행되지 않음"
fi

# Spring Boot 앱 상태
if [ -n "$(check_container app)" ]; then
    if curl -f -s http://localhost:8080/actuator/health | grep -q "UP"; then
        log_success "✅ Spring Boot (백엔드): 정상"
        # 상세 헬스 정보
        HEALTH_INFO=$(curl -s http://localhost:8080/actuator/health)
        echo "   상세 정보: $HEALTH_INFO"
    else
        log_error "❌ Spring Boot (백엔드): 오류"
    fi
else
    log_warning "⚠️ Spring Boot (백엔드): 실행되지 않음"
fi

# PostgreSQL 상태
if [ -n "$(check_container postgres)" ]; then
    if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T postgres pg_isready >/dev/null 2>&1; then
        log_success "✅ PostgreSQL: 정상"
    else
        log_error "❌ PostgreSQL: 오류"
    fi
else
    log_warning "⚠️ PostgreSQL: 실행되지 않음"
fi

# Redis 상태
if [ -n "$(check_container redis)" ]; then
    if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T redis redis-cli ping | grep -q "PONG"; then
        log_success "✅ Redis: 정상"
    else
        log_error "❌ Redis: 오류"
    fi
else
    log_warning "⚠️ Redis: 실행되지 않음"
fi

# Elasticsearch 상태
if [ -n "$(check_container elasticsearch)" ]; then
    if curl -f -s -o /dev/null http://localhost:9200/_cluster/health; then
        log_success "✅ Elasticsearch: 정상"
        # 클러스터 상태 정보
        CLUSTER_INFO=$(curl -s http://localhost:9200/_cluster/health)
        echo "   상세 정보: $CLUSTER_INFO"
    else
        log_error "❌ Elasticsearch: 오류"
    fi
else
    log_warning "⚠️ Elasticsearch: 실행되지 않음"
fi

echo ""

# 리소스 사용량
log_info "💾 리소스 사용량:"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}" $(docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps -q)
echo ""

# 디스크 사용량
log_info "💿 디스크 사용량:"
df -h | grep -E "Filesystem|/dev/"
echo ""

# 볼륨 사용량
log_info "📦 Docker 볼륨 사용량:"
for volume in postgres redis elasticsearch; do
    case $volume in
        postgres) display_name="PostgreSQL" ;;
        redis) display_name="Redis" ;;
        elasticsearch) display_name="Elasticsearch" ;;
    esac
    echo "$display_name 데이터:"
    volume_name=$(docker volume ls --filter name="${volume}_data" --format "{{.Name}}")
    if [ -n "$volume_name" ]; then
        du -sh $(docker volume inspect "${volume_name}" | jq -r '.[].Mountpoint' 2>/dev/null) 2>/dev/null || echo "볼륨 접근 불가"
    else
        echo "볼륨을 찾을 수 없음"
    fi
done
echo ""

# 최근 로그 (오류만)
log_info "📋 최근 오류 로그 (최근 10개):"
for service in nginx frontend app postgres redis elasticsearch; do
    if [ -n "$(check_container $service)" ]; then
        echo "[$service 로그]"
        docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" logs --tail=10 "$service" 2>&1 | grep -i "error\|exception" || echo "   오류 없음"
        echo ""
    fi
done

# SSL 인증서 상태 (HTTPS 사용 시)
SSL_DIR="$DEPLOY_DIR/config/nginx/ssl"
if [[ -f "$SSL_DIR/fullchain.pem" ]]; then
    log_info "🔒 SSL 인증서 상태:"
    DAYS_LEFT=$(calculate_days_left "$SSL_DIR/fullchain.pem")
    EXPIRE_DATE=$(openssl x509 -enddate -noout -in "$SSL_DIR/fullchain.pem" | cut -d= -f2)

    if [[ $DAYS_LEFT -gt 30 ]]; then
        log_success "✅ SSL 인증서: 정상 (${DAYS_LEFT}일 남음)"
    elif [[ $DAYS_LEFT -gt 7 ]]; then
        log_warning "⚠️  SSL 인증서: 주의 (${DAYS_LEFT}일 남음)"
    else
        log_error "❌ SSL 인증서: 곧 만료 (${DAYS_LEFT}일 남음)"
    fi
    echo "   만료일: $EXPIRE_DATE"
    echo ""
fi

# 백업 상태
BACKUP_DIR="$DEPLOY_DIR/backups"
if [[ -d "$BACKUP_DIR" ]]; then
    log_info "🗄️  백업 상태:"
    BACKUP_COUNT=$(find "$BACKUP_DIR" -name "*.gz" | wc -l)
    if [[ $BACKUP_COUNT -gt 0 ]]; then
        LATEST_BACKUP=$(find "$BACKUP_DIR" -name "*.gz" -type f -printf '%T@ %p\n' | sort -n | tail -1 | cut -d' ' -f2-)
        BACKUP_AGE=$(stat -c %Y "$LATEST_BACKUP")
        CURRENT_TIME=$(date +%s)
        HOURS_SINCE_BACKUP=$(( ($CURRENT_TIME - $BACKUP_AGE) / 3600 ))

        if [[ $HOURS_SINCE_BACKUP -lt 25 ]]; then
            log_success "✅ 백업: 정상 (${HOURS_SINCE_BACKUP}시간 전)"
        else
            log_warning "⚠️  백업: 오래됨 (${HOURS_SINCE_BACKUP}시간 전)"
        fi
        echo "   총 백업 파일: $BACKUP_COUNT개"
        echo "   최신 백업: $(basename "$LATEST_BACKUP")"
    else
        log_warning "⚠️  백업 파일이 없습니다."
    fi
    echo ""
fi

# 요약
log_info "📈 모니터링 요약:"
log_info "   - 정기적인 모니터링을 위해 cron 설정을 권장합니다"
log_info "   - 백업은 매일 자동 실행되도록 설정하세요"
log_info "   - SSL 인증서는 만료 30일 전에 갱신하세요"
echo ""

# 유용한 명령어
log_info "🔧 유용한 명령어:"
log_info "   • 전체 서비스 로그: docker-compose -f deploy/docker-compose.yml --env-file $ENV_FILE logs -f"
log_info "   • 특정 서비스 로그: docker-compose -f deploy/docker-compose.yml --env-file $ENV_FILE logs -f [service]"
log_info "   • 서비스 재시작: docker-compose -f deploy/docker-compose.yml --env-file $ENV_FILE restart [service]"
log_info "   • 리소스 모니터링: docker stats"
log_info "   • 백업 실행: ./deploy/scripts/utils/backup.sh"
