#!/bin/bash

# 자동 백업 설정 스크립트
# 사용법: ./setup-cron.sh [environment]

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
BACKUP_SCRIPT="$PROJECT_ROOT/deploy/scripts/utils/backup.sh"

log_info "자동 백업 cron job 설정..."
log_info "   환경: $ENVIRONMENT"

# 백업 스크립트 실행 권한 확인
if [ ! -x "$BACKUP_SCRIPT" ]; then
    log_info "백업 스크립트에 실행 권한을 부여합니다..."
    chmod +x "$BACKUP_SCRIPT"
fi

echo ""
log_info "다음 중 원하는 백업 주기를 선택하세요:"
echo "1) 매일 오전 2시"
echo "2) 매주 일요일 오전 2시"
echo "3) 매일 오전 2시 + 오후 2시 (하루 2번)"
echo "4) 사용자 정의"
echo "5) cron job 제거"
echo ""

read -p "선택 (1-5): " choice

case $choice in
    1)
        CRON_SCHEDULE="0 2 * * *"
        DESCRIPTION="매일 오전 2시"
        ;;
    2)
        CRON_SCHEDULE="0 2 * * 0"
        DESCRIPTION="매주 일요일 오전 2시"
        ;;
    3)
        # 두 개의 cron job 추가
        echo "# Prompth Center 자동 백업 - 오전" > /tmp/prompth_cron
        echo "0 2 * * * cd $PROJECT_ROOT && $BACKUP_SCRIPT $ENVIRONMENT >> /tmp/backup.log 2>&1" >> /tmp/prompth_cron
        echo "# Prompth Center 자동 백업 - 오후" >> /tmp/prompth_cron
        echo "0 14 * * * cd $PROJECT_ROOT && $BACKUP_SCRIPT $ENVIRONMENT >> /tmp/backup.log 2>&1" >> /tmp/prompth_cron

        # 기존 crontab에 추가
        (crontab -l 2>/dev/null; cat /tmp/prompth_cron) | crontab -
        rm /tmp/prompth_cron

        log_success "자동 백업이 설정되었습니다:"
        log_success "   - 매일 오전 2시"
        log_success "   - 매일 오후 2시"
        log_success "   - 로그 위치: /tmp/backup.log"
        echo ""
        exit 0
        ;;
    4)
        echo ""
        log_info "cron 형식으로 입력하세요 (예: '0 2 * * *' = 매일 오전 2시):"
        log_info "형식: 분 시 일 월 요일"
        echo ""
        read -p "cron 스케줄: " CRON_SCHEDULE
        DESCRIPTION="사용자 정의 ($CRON_SCHEDULE)"
        ;;
    5)
        log_info "기존 백업 cron job을 제거합니다..."
        crontab -l 2>/dev/null | grep -v "Prompth Center 자동 백업" | grep -v "$BACKUP_SCRIPT" | crontab -
        log_success "자동 백업 cron job이 제거되었습니다."
        echo ""
        exit 0
        ;;
    *)
        log_error "잘못된 선택입니다."
        exit 1
        ;;
esac

# cron job 생성
echo "# Prompth Center 자동 백업 - $DESCRIPTION" > /tmp/prompth_cron
echo "$CRON_SCHEDULE cd $PROJECT_ROOT && $BACKUP_SCRIPT $ENVIRONMENT >> /tmp/backup.log 2>&1" >> /tmp/prompth_cron

# 기존 crontab에 추가
(crontab -l 2>/dev/null; cat /tmp/prompth_cron) | crontab -
rm /tmp/prompth_cron

log_success ""
log_success "🎉 자동 백업이 설정되었습니다!"
log_success ""
log_success "📋 설정 정보:"
log_success "   • 스케줄: $DESCRIPTION"
log_success "   • 스크립트: $BACKUP_SCRIPT"
log_success "   • 환경: $ENVIRONMENT"
log_success "   • 로그 위치: /tmp/backup.log"
log_success ""

log_info "현재 cron job 목록:"
crontab -l | grep -A1 -B1 "Prompth Center" || log_info "   (아직 설정된 job이 없습니다)"
echo ""

log_info "🔧 유용한 명령어:"
log_info "   • cron job 확인: crontab -l"
log_info "   • 백업 로그 확인: tail -f /tmp/backup.log"
log_info "   • 수동 백업 실행: $BACKUP_SCRIPT $ENVIRONMENT"
log_info "   • cron job 제거: $0 재실행 후 옵션 5 선택"
echo ""
