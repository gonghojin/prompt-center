#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로고 출력
echo -e "${BLUE}
╔═══════════════════════════════════╗
║     Prompth Center Dev Starter    ║
║         로컬 개발환경 시작          ║
╚═══════════════════════════════════╝
${NC}"

# 현재 디렉토리 확인
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$PROJECT_ROOT/docker-compose.dev.yml"

# Docker Compose 파일 존재 확인
if [ ! -f "$COMPOSE_FILE" ]; then
    echo -e "${RED}❌ docker-compose.dev.yml 파일을 찾을 수 없습니다.${NC}"
    echo -e "${YELLOW}💡 프로젝트 루트 디렉토리에서 실행해주세요.${NC}"
    exit 1
fi

# .env 파일 확인
ENV_FILE="$PROJECT_ROOT/.env"
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${YELLOW}⚠️  .env 파일이 없습니다. 기본값으로 생성합니다...${NC}"
    cat > "$ENV_FILE" << 'EOF'
# Database
DB_NAME=prompt_center_dev
DB_USERNAME=prompth_user
DB_PASSWORD=dev_password_123

# Redis
REDIS_PASSWORD=dev_redis_123

# JWT
JWT_SECRET=c2VjcmV0LWtleS1tdXN0LWJlLWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmctZm9yLWp3dC1zaWduaW5n
EOF
    echo -e "${GREEN}✅ .env 파일이 생성되었습니다.${NC}"
fi

# 명령어 처리
case "${1:-all}" in
    "db"|"database")
        echo -e "${BLUE}🗄️  데이터베이스 서비스들을 시작합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" up -d db redis elasticsearch
        ;;
    "backend"|"api")
        echo -e "${BLUE}🚀 백엔드 서비스를 시작합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" up backend
        ;;
    "nginx"|"proxy")
        echo -e "${BLUE}🌐 Nginx 프록시를 시작합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" up -d nginx
        ;;
    "all"|"")
        echo -e "${BLUE}🚀 전체 개발환경을 시작합니다...${NC}"

        # 1단계: 데이터베이스 서비스들 시작
        echo -e "${YELLOW}📋 1단계: 데이터베이스 서비스들을 시작합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" up -d db redis elasticsearch

        # 서비스 준비 대기
        echo -e "${YELLOW}⏳ 데이터베이스 서비스들이 준비될 때까지 대기중... (30초)${NC}"
        for i in {1..30}; do
            echo -n "."
            sleep 1
        done
        echo ""

        # 2단계: 백엔드 애플리케이션 시작 (포그라운드)
        echo -e "${YELLOW}📋 2단계: 백엔드 애플리케이션을 시작합니다...${NC}"
        echo -e "${GREEN}✅ 백엔드 로그를 확인하세요. Ctrl+C로 중지할 수 있습니다.${NC}"
        docker-compose -f "$COMPOSE_FILE" up backend
        ;;
    "stop")
        echo -e "${YELLOW}🛑 모든 서비스를 중지합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" down
        echo -e "${GREEN}✅ 모든 서비스가 중지되었습니다.${NC}"
        ;;
    "restart")
        echo -e "${YELLOW}🔄 모든 서비스를 재시작합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" down
        sleep 2
        docker-compose -f "$COMPOSE_FILE" up -d db redis elasticsearch
        echo -e "${YELLOW}⏳ 데이터베이스 서비스들이 준비될 때까지 대기중... (30초)${NC}"
        sleep 30
        docker-compose -f "$COMPOSE_FILE" up backend
        ;;
    "logs")
        service="${2:-}"
        if [ -n "$service" ]; then
            echo -e "${BLUE}📋 $service 서비스의 로그를 확인합니다...${NC}"
            docker-compose -f "$COMPOSE_FILE" logs -f "$service"
        else
            echo -e "${BLUE}📋 전체 서비스의 로그를 확인합니다...${NC}"
            docker-compose -f "$COMPOSE_FILE" logs -f
        fi
        ;;
    "status")
        echo -e "${BLUE}📊 서비스 상태를 확인합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" ps
        ;;
    "clean")
        echo -e "${YELLOW}🧹 불필요한 컨테이너와 이미지를 정리합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" down -v
        docker system prune -f
        echo -e "${GREEN}✅ 정리가 완료되었습니다.${NC}"
        ;;
    "build")
        echo -e "${BLUE}🔨 백엔드 이미지를 다시 빌드합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" build backend
        echo -e "${GREEN}✅ 빌드가 완료되었습니다.${NC}"
        ;;
    "help"|"-h"|"--help")
        echo -e "${GREEN}
사용법: $0 [명령어]

명령어:
  all, (기본값)     - 전체 개발환경 시작 (데이터베이스 → 백엔드)
  db, database     - 데이터베이스 서비스들만 시작 (PostgreSQL, Redis, Elasticsearch)
  backend, api     - 백엔드 애플리케이션만 시작
  nginx, proxy     - Nginx 프록시만 시작
  stop             - 모든 서비스 중지
  restart          - 모든 서비스 재시작
  logs [서비스명]   - 로그 확인 (서비스명 생략시 전체)
  status           - 서비스 상태 확인
  clean            - 컨테이너와 볼륨 정리
  build            - 백엔드 이미지 재빌드
  help, -h, --help - 도움말 표시

예제:
  $0                    # 전체 환경 시작
  $0 db                 # 데이터베이스만 시작
  $0 logs backend       # 백엔드 로그 확인
  $0 stop               # 모든 서비스 중지

접속 정보:
  - 백엔드 API: http://localhost:8080
  - API 문서: http://localhost:8080/swagger-ui.html
  - PostgreSQL: localhost:5432
  - Redis: localhost:6379
  - Elasticsearch: http://localhost:9200
  - Nginx: http://localhost:80

환경변수 (.env 파일):
  - DB_NAME=prompt_center_dev
  - DB_USERNAME=prompth_user
  - DB_PASSWORD=dev_password_123
  - REDIS_PASSWORD=dev_redis_123
${NC}"
        ;;
    *)
        echo -e "${RED}❌ 알 수 없는 명령어: $1${NC}"
        echo -e "${YELLOW}💡 '$0 help'를 실행하여 사용법을 확인하세요.${NC}"
        exit 1
        ;;
esac
