#!/bin/bash

# SSL 인증서 생성 스크립트
# 사용법: ./generate.sh [mode] [domain] [email]
# mode: dev | prod
# domain: 도메인명
# email: 이메일 (prod 모드 시 필수)

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
MODE=${1:-dev}
DOMAIN=${2:-localhost}
EMAIL=${3:-}

# 프로젝트 루트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
SSL_DIR="$PROJECT_ROOT/deploy/config/nginx/ssl"

log_info "SSL 인증서 생성을 시작합니다..."
log_info "   모드: $MODE"
log_info "   도메인: $DOMAIN"

# SSL 디렉토리 생성
mkdir -p "$SSL_DIR"

if [[ "$MODE" == "prod" ]]; then
    # 운영환경: Let's Encrypt 인증서
    if [[ -z "$EMAIL" ]]; then
        log_error "운영환경에서는 이메일이 필요합니다."
        log_info "사용법: $0 prod your-domain.com admin@your-domain.com"
        exit 1
    fi

    log_info "Let's Encrypt 인증서를 생성합니다..."

    # certbot 설치 확인
    if ! command -v certbot &> /dev/null; then
        log_info "certbot을 설치합니다..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            brew install certbot
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            if command -v apt-get &> /dev/null; then
                sudo apt-get update && sudo apt-get install -y certbot
            elif command -v yum &> /dev/null; then
                sudo yum install -y certbot
            else
                log_error "패키지 관리자를 찾을 수 없습니다. certbot을 수동으로 설치하세요."
                exit 1
            fi
        fi
    fi

    # Let's Encrypt 인증서 생성
    sudo certbot certonly \
        --standalone \
        --preferred-challenges http \
        --http-01-port 80 \
        --domains "$DOMAIN" \
        --email "$EMAIL" \
        --agree-tos \
        --non-interactive

    # 인증서 복사
    sudo cp "/etc/letsencrypt/live/$DOMAIN/fullchain.pem" "$SSL_DIR/"
    sudo cp "/etc/letsencrypt/live/$DOMAIN/privkey.pem" "$SSL_DIR/"
    sudo chown $(whoami):$(whoami) "$SSL_DIR/fullchain.pem" "$SSL_DIR/privkey.pem"

    log_success "Let's Encrypt 인증서가 생성되었습니다."
    log_info "인증서 위치: $SSL_DIR"
    log_warning "인증서는 90일마다 갱신이 필요합니다."
    log_info "자동 갱신 설정: sudo crontab -e"
    log_info "  0 2 * * * certbot renew --quiet && docker-compose restart nginx"

else
    # 개발환경: 자체 서명 인증서
    log_info "개발용 자체 서명 인증서를 생성합니다..."

    # OpenSSL 설치 확인
    if ! command -v openssl &> /dev/null; then
        log_error "OpenSSL이 설치되어 있지 않습니다."
        exit 1
    fi

    # 자체 서명 인증서 생성
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout "$SSL_DIR/privkey.pem" \
        -out "$SSL_DIR/fullchain.pem" \
        -subj "/C=KR/ST=Seoul/L=Seoul/O=PromptH/OU=Dev/CN=$DOMAIN"

    # 권한 설정
    chmod 600 "$SSL_DIR/privkey.pem"
    chmod 644 "$SSL_DIR/fullchain.pem"

    log_success "개발용 자체 서명 인증서가 생성되었습니다."
    log_info "인증서 위치: $SSL_DIR"
    log_warning "자체 서명 인증서는 브라우저에서 보안 경고를 표시합니다."
fi

# 인증서 정보 출력
log_info "인증서 정보:"
openssl x509 -in "$SSL_DIR/fullchain.pem" -text -noout | grep -E "Subject:|Not Before:|Not After:"

log_success "SSL 인증서 생성이 완료되었습니다."
