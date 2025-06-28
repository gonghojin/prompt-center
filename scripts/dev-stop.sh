#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}
╔═══════════════════════════════════╗
║     Prompth Center Dev Stopper    ║
║        로컬 개발환경 정리           ║
╚═══════════════════════════════════╝
${NC}"

# 현재 디렉토리 확인
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$PROJECT_ROOT/docker-compose.dev.yml"

case "${1:-stop}" in
    "stop"|"")
        echo -e "${YELLOW}🛑 개발환경 서비스들을 중지합니다...${NC}"
        docker-compose -f "$COMPOSE_FILE" down
        echo -e "${GREEN}✅ 모든 서비스가 중지되었습니다.${NC}"
        ;;
    "clean")
        echo -e "${YELLOW}🧹 개발환경을 완전히 정리합니다...${NC}"
        echo -e "${RED}⚠️  모든 데이터(볼륨)가 삭제됩니다!${NC}"
        read -p "계속하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker-compose -f "$COMPOSE_FILE" down -v --remove-orphans
            docker system prune -f
            echo -e "${GREEN}✅ 완전 정리가 완료되었습니다.${NC}"
        else
            echo -e "${YELLOW}취소되었습니다.${NC}"
        fi
        ;;
    "reset")
        echo -e "${YELLOW}🔄 개발환경을 초기화합니다...${NC}"
        echo -e "${RED}⚠️  모든 데이터와 이미지가 삭제됩니다!${NC}"
        read -p "계속하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker-compose -f "$COMPOSE_FILE" down -v --remove-orphans
            docker-compose -f "$COMPOSE_FILE" down --rmi all
            docker system prune -af
            echo -e "${GREEN}✅ 초기화가 완료되었습니다.${NC}"
        else
            echo -e "${YELLOW}취소되었습니다.${NC}"
        fi
        ;;
    "help"|"-h"|"--help")
        echo -e "${GREEN}
사용법: $0 [명령어]

명령어:
  stop, (기본값)  - 서비스 중지 (데이터 보존)
  clean          - 서비스 중지 + 볼륨 삭제 (데이터 삭제)
  reset          - 완전 초기화 (이미지까지 삭제)
  help, -h       - 도움말 표시

예제:
  $0              # 서비스만 중지
  $0 clean        # 데이터까지 삭제
  $0 reset        # 완전 초기화
${NC}"
        ;;
    *)
        echo -e "${RED}❌ 알 수 없는 명령어: $1${NC}"
        echo -e "${YELLOW}💡 '$0 help'를 실행하여 사용법을 확인하세요.${NC}"
        exit 1
        ;;
esac
