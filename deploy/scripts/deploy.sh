#!/bin/bash

# 통합 배포 스크립트 (HTTP/HTTPS 지원)
# 사용법: ./deploy.sh [mode] [environment] [domain] [email]
# mode: http | https
# environment: dev | prod
# domain: 도메인명 (HTTPS 시 필수)
# email: 이메일 (운영 HTTPS 시 필수)

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

# 기본값 설정
MODE=${1:-http}
ENVIRONMENT=${2:-dev}
DOMAIN=${3:-localhost}
EMAIL=${4:-}

# 프로젝트 루트 디렉토리
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DEPLOY_DIR="$PROJECT_ROOT/deploy"

cd "$PROJECT_ROOT"

log_info "🚀 Prompth Center 배포를 시작합니다..."
log_info "   모드: $MODE"
log_info "   환경: $ENVIRONMENT"
log_info "   도메인: $DOMAIN"

# 환경변수 파일 확인
ENV_FILE="$DEPLOY_DIR/config/env/$ENVIRONMENT.env"
if [[ ! -f "$ENV_FILE" ]]; then
    log_error "환경변수 파일이 없습니다: $ENV_FILE"
    log_info "다음 파일을 복사하여 설정하세요:"
    log_info "  cp $DEPLOY_DIR/config/env/$ENVIRONMENT.env.example $ENV_FILE"
    exit 1
fi

log_success "환경변수 파일 확인됨"

# HTTPS 모드일 때 SSL 인증서 확인/생성
if [[ "$MODE" == "https" ]]; then
    SSL_DIR="$DEPLOY_DIR/config/nginx/ssl"
    CERT_FILE="$SSL_DIR/fullchain.pem"
    KEY_FILE="$SSL_DIR/privkey.pem"

    if [[ ! -f "$CERT_FILE" || ! -f "$KEY_FILE" ]]; then
        log_info "SSL 인증서가 없습니다. 생성합니다..."

        if [[ "$ENVIRONMENT" == "prod" ]]; then
            if [[ -z "$EMAIL" ]]; then
                log_error "운영환경 HTTPS 배포 시 이메일이 필요합니다."
                log_info "사용법: $0 https prod your-domain.com admin@your-domain.com"
                exit 1
            fi
            "$DEPLOY_DIR/scripts/ssl/generate.sh" prod "$DOMAIN" "$EMAIL"
        else
            "$DEPLOY_DIR/scripts/ssl/generate.sh" dev "$DOMAIN"
        fi
    else
        log_success "SSL 인증서 확인됨"
    fi

    # Nginx 설정을 HTTPS용으로 업데이트
    log_info "Nginx 설정을 HTTPS용으로 업데이트합니다..."

    # 백업 생성
    cp "$DEPLOY_DIR/config/nginx/conf.d/default.conf" "$DEPLOY_DIR/config/nginx/conf.d/default.conf.bak"

    # HTTPS 리다이렉트 추가
    TEMP_FILE="$DEPLOY_DIR/config/nginx/conf.d/default.conf.tmp"
    awk '
        /^\s*# 공통 location 블록 포함/ && !inserted && inHttpServer {
            print "    # HTTPS 리다이렉트"
            print "    return 301 https://$server_name$request_uri;"
            print ""
            inserted = 1
        }
        /^# HTTP 서버/ { inHttpServer = 1 }
        /^# HTTPS 서버/ { inHttpServer = 0 }
        /^server {/ && inHttpServer { inHttpServer = 1 }
        { print }
    ' "$DEPLOY_DIR/config/nginx/conf.d/default.conf" > "$TEMP_FILE"
    mv "$TEMP_FILE" "$DEPLOY_DIR/config/nginx/conf.d/default.conf"
else
    # HTTP 모드일 때는 원래 설정으로 복원
    if [[ -f "$DEPLOY_DIR/config/nginx/conf.d/default.conf.bak" ]]; then
        mv "$DEPLOY_DIR/config/nginx/conf.d/default.conf.bak" "$DEPLOY_DIR/config/nginx/conf.d/default.conf"
    fi
fi

# 프론트엔드 빌드 및 확인
if [[ -d "$PROJECT_ROOT/frontend" ]]; then
    log_info "프론트엔드 소스 디렉토리 확인됨"

    # 프론트엔드 의존성 확인
    if [[ ! -f "$PROJECT_ROOT/frontend/package.json" ]]; then
        log_error "프론트엔드 package.json이 없습니다."
        log_info "프론트엔드 프로젝트를 $PROJECT_ROOT/frontend 에 위치시키세요."
        exit 1
    fi

    # 프론트엔드 의존성 및 설정 업데이트
    log_info "프론트엔드 의존성을 업데이트합니다..."
    cd "$PROJECT_ROOT/frontend"

#    # package-lock.json 삭제 (의존성 충돌 방지)
#    if [[ -f "package-lock.json" ]]; then
#        rm package-lock.json
#        log_info "기존 package-lock.json 삭제됨"
#    fi

    # node_modules 삭제 (깨끗한 설치)
    if [[ -d "node_modules" ]]; then
        rm -rf node_modules
        log_info "기존 node_modules 삭제됨"
    fi

    # 의존성 설치
    log_info "npm 의존성 설치 중..."
    npm ci
    if [[ $? -ne 0 ]]; then
        log_error "npm 의존성 설치 실패"
        exit 1
    fi
    log_success "npm 의존성 설치 완료"

    # 프로젝트 루트로 복귀
    cd "$PROJECT_ROOT"

    # 프론트엔드 Dockerfile 확인
    if [[ ! -f "$PROJECT_ROOT/frontend/Dockerfile.production" ]]; then
        log_warning "프론트엔드 Dockerfile.production이 없습니다."
        log_info "기본 Dockerfile을 사용합니다."
    fi
else
    log_warning "프론트엔드 소스 디렉토리가 없습니다 ($PROJECT_ROOT/frontend)"
    log_info "백엔드만 배포됩니다."
fi

# 필요한 디렉토리 생성
mkdir -p "$DEPLOY_DIR/logs/nginx" "$DEPLOY_DIR/logs/app" "$DEPLOY_DIR/logs/frontend" "$DEPLOY_DIR/backups"

# 기존 컨테이너 중지
log_info "기존 컨테이너를 중지합니다..."
docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" down 2>/dev/null || true

# 네트워크 정리
log_info "네트워크 정리 중..."
docker network prune -f 2>/dev/null || true

# Docker 이미지 빌드
log_info "Docker 이미지를 빌드합니다..."
docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" build --no-cache

# 컨테이너 시작
log_info "$MODE 모드로 컨테이너를 시작합니다..."
docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" up -d

# 헬스체크
log_info "서비스 헬스체크를 수행합니다..."

# PostgreSQL 헬스체크
log_info "PostgreSQL 연결 확인 중..."
for i in {1..30}; do
    if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T postgres pg_isready >/dev/null 2>&1; then
        log_success "PostgreSQL 연결 성공"
        break
    fi
    if [[ $i -eq 30 ]]; then
        log_error "PostgreSQL 연결 실패"
        exit 1
    fi
    echo -n "."
    sleep 2
done

# Redis 헬스체크
log_info "Redis 연결 확인 중..."
for i in {1..30}; do
    if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" exec -T redis redis-cli ping >/dev/null 2>&1; then
        log_success "Redis 연결 성공"
        break
    fi
    if [[ $i -eq 30 ]]; then
        log_error "Redis 연결 실패"
        exit 1
    fi
    echo -n "."
    sleep 2
done

# Spring Boot 애플리케이션 헬스체크
log_info "Spring Boot 애플리케이션 확인 중..."
PROTOCOL="http"
PORT="80"
if [[ "$MODE" == "https" ]]; then
    PROTOCOL="https"
    PORT="443"
fi

for i in {1..60}; do
    if curl -f -k "$PROTOCOL://localhost:$PORT/actuator/health" >/dev/null 2>&1; then
        log_success "Spring Boot 애플리케이션 정상"
        break
    fi
    if [[ $i -eq 60 ]]; then
        log_error "Spring Boot 애플리케이션 시작 실패"
        log_info "로그를 확인하세요:"
        log_info "  docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' logs app"
        exit 1
    fi
    echo -n "."
    sleep 3
done

# 프론트엔드 헬스체크 (프론트엔드가 있는 경우)
if [[ -d "$PROJECT_ROOT/frontend" ]]; then
    log_info "프론트엔드 애플리케이션 확인 중..."
    for i in {1..60}; do
        if curl -f -k "$PROTOCOL://localhost:$PORT/frontend-health" >/dev/null 2>&1; then
            log_success "프론트엔드 애플리케이션 정상"
            break
        fi
        if curl -f -k "$PROTOCOL://localhost:$PORT/" >/dev/null 2>&1; then
            log_success "프론트엔드 애플리케이션 정상 (홈페이지 응답)"
            break
        fi
        if [[ $i -eq 60 ]]; then
            log_warning "프론트엔드 직접 헬스체크 실패, 컨테이너 상태 확인 중..."
            if docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps | grep -q "prompth-frontend.*Up"; then
                log_success "프론트엔드 컨테이너 실행 중"
            else
                log_error "프론트엔드 컨테이너 시작 실패"
                log_info "로그를 확인하세요:"
                log_info "  docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' logs frontend"
            fi
        fi
        echo -n "."
        sleep 3
    done
fi

# 최종 상태 확인
log_info "최종 서비스 상태:"
docker-compose -f deploy/docker-compose.yml --env-file "$ENV_FILE" ps

log_success ""
log_success "🎉 $MODE 배포가 성공적으로 완료되었습니다!"
log_success ""
log_success "📋 서비스 정보:"
if [[ "$MODE" == "https" ]]; then
    log_success "   • 웹사이트 (프론트엔드): https://$DOMAIN"
    log_success "   • API 엔드포인트: https://$DOMAIN/api"
    log_success "   • Health Check: https://$DOMAIN/actuator/health"
    log_success "   • Swagger UI: https://$DOMAIN/swagger-ui.html"
else
    log_success "   • 웹사이트 (프론트엔드): http://$DOMAIN"
    log_success "   • API 엔드포인트: http://$DOMAIN/api"
    log_success "   • Health Check: http://$DOMAIN/actuator/health"
    log_success "   • Swagger UI: http://$DOMAIN/swagger-ui.html"
fi
log_success ""
log_success "📊 모니터링 명령어:"
log_success "   • 통합 로그 확인: docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' logs -f"
log_success "   • 프론트엔드 로그: docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' logs -f frontend"
log_success "   • 백엔드 로그: docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' logs -f app"
log_success "   • Nginx 로그: docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' logs -f nginx"
log_success "   • 컨테이너 상태: docker-compose -f deploy/docker-compose.yml --env-file '$ENV_FILE' ps"
log_success "   • 리소스 사용량: docker stats"
log_success ""

if [[ "$MODE" == "https" && "$ENVIRONMENT" == "dev" ]]; then
    log_warning "개발환경 자체 서명 인증서로 인해 브라우저에서 보안 경고가 표시될 수 있습니다."
    log_warning "브라우저에서 '고급 설정' → '안전하지 않음으로 이동'을 클릭하세요."
fi
