#!/bin/bash

# 개발서버용 자체 서명 SSL 인증서 생성 스크립트
# 사용법: ./deploy/scripts/ssl/generate-dev-ssl.sh [도메인명]

set -e

# 컬러 출력을 위한 함수
print_info() {
    echo -e "\033[1;34m[INFO]\033[0m $1"
}

print_success() {
    echo -e "\033[1;32m[SUCCESS]\033[0m $1"
}

print_warning() {
    echo -e "\033[1;33m[WARNING]\033[0m $1"
}

print_error() {
    echo -e "\033[1;31m[ERROR]\033[0m $1"
}

# 도메인명 설정 (기본값: localhost)
DOMAIN=${1:-localhost}
SSL_DIR="deploy/config/nginx/ssl"
COUNTRY="KR"
STATE="Seoul"
CITY="Seoul"
ORG="Development"
ORG_UNIT="IT Department"

print_info "개발서버용 SSL 인증서 생성을 시작합니다..."
print_info "도메인: $DOMAIN"

# 프로젝트 루트로 이동
cd "$(dirname "$0")/../../.."

# SSL 디렉토리 생성
mkdir -p "$SSL_DIR"

# OpenSSL 설치 확인
if ! command -v openssl &> /dev/null; then
    print_error "OpenSSL이 설치되어 있지 않습니다."
    print_info "macOS: brew install openssl"
    print_info "Ubuntu: sudo apt-get install openssl"
    exit 1
fi

# 기존 인증서 백업
if [ -f "$SSL_DIR/privkey.pem" ]; then
    print_info "기존 인증서를 백업합니다..."
    timestamp=$(date +%Y%m%d_%H%M%S)
    mv "$SSL_DIR/privkey.pem" "$SSL_DIR/privkey_backup_$timestamp.pem" || true
    mv "$SSL_DIR/fullchain.pem" "$SSL_DIR/fullchain_backup_$timestamp.pem" || true
fi

# 개인키 생성
print_info "개인키 생성 중..."
openssl genrsa -out "$SSL_DIR/privkey.pem" 4096

# 인증서 서명 요청 (CSR) 생성
print_info "인증서 서명 요청 생성 중..."
openssl req -new -key "$SSL_DIR/privkey.pem" -out "$SSL_DIR/cert.csr" -subj "/C=$COUNTRY/ST=$STATE/L=$CITY/O=$ORG/OU=$ORG_UNIT/CN=$DOMAIN"

# Subject Alternative Name (SAN) 확장 설정 파일 생성
cat > "$SSL_DIR/san.conf" << EOF
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
prompt = no

[req_distinguished_name]
C=$COUNTRY
ST=$STATE
L=$CITY
O=$ORG
OU=$ORG_UNIT
CN=$DOMAIN

[v3_req]
keyUsage = keyEncipherment, dataEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
DNS.1 = $DOMAIN
DNS.2 = localhost
DNS.3 = *.localhost
DNS.4 = 127.0.0.1
IP.1 = 127.0.0.1
IP.2 = ::1
EOF

# 자체 서명 인증서 생성 (1년 유효)
print_info "자체 서명 인증서 생성 중..."
openssl x509 -req -in "$SSL_DIR/cert.csr" -signkey "$SSL_DIR/privkey.pem" \
    -out "$SSL_DIR/fullchain.pem" -days 365 \
    -extensions v3_req -extfile "$SSL_DIR/san.conf"

# 임시 파일 정리
rm -f "$SSL_DIR/cert.csr" "$SSL_DIR/san.conf"

# 파일 권한 설정
chmod 600 "$SSL_DIR/privkey.pem"
chmod 644 "$SSL_DIR/fullchain.pem"

# 인증서 정보 출력
print_success "✅ SSL 인증서가 성공적으로 생성되었습니다!"
print_success ""
print_success "📋 인증서 정보:"
print_success "   • 도메인:     $DOMAIN"
print_success "   • 유효기간:   1년"
print_success "   • 개인키:     $SSL_DIR/privkey.pem"
print_success "   • 인증서:     $SSL_DIR/fullchain.pem"
print_success ""

# 인증서 세부 정보 출력
print_info "인증서 세부 정보:"
openssl x509 -in "$SSL_DIR/fullchain.pem" -text -noout | grep -A 1 "Subject:"
openssl x509 -in "$SSL_DIR/fullchain.pem" -text -noout | grep -A 1 "Not Before"
openssl x509 -in "$SSL_DIR/fullchain.pem" -text -noout | grep -A 5 "Subject Alternative Name"

print_success ""
print_warning "⚠️  자체 서명 인증서는 브라우저에서 보안 경고를 표시합니다."
print_info "브라우저에서 '고급 설정' → '안전하지 않음으로 이동'을 클릭하여 접속하세요."
print_success ""
print_success "이제 HTTPS 배포를 실행할 수 있습니다:"
print_success "   ./deploy/scripts/deploy/deploy-https.sh"
print_success ""
