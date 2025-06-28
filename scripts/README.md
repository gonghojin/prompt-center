# 로컬 개발용 스크립트 가이드

로컬 개발환경에서 `docker-compose.dev.yml`의 서비스들을 빠르고 편리하게 관리하기 위한 스크립트들입니다.

## 🚀 빠른 시작

### 1. 전체 개발환경 시작

```bash
# 데이터베이스 → 백엔드 순서로 시작
./scripts/dev-start.sh
```

### 2. 개발환경 중지

```bash
# 모든 서비스 중지 (데이터 보존)
./scripts/dev-stop.sh
```

## 📋 상세 명령어

### dev-start.sh - 개발환경 시작

```bash
# 전체 환경 시작 (기본값)
./scripts/dev-start.sh
./scripts/dev-start.sh all

# 데이터베이스 서비스들만 시작
./scripts/dev-start.sh db
./scripts/dev-start.sh database

# 백엔드만 시작 (데이터베이스가 미리 실행되어 있어야 함)
./scripts/dev-start.sh backend
./scripts/dev-start.sh api

# Nginx 프록시만 시작
./scripts/dev-start.sh nginx
./scripts/dev-start.sh proxy

# 서비스 상태 확인
./scripts/dev-start.sh status

# 로그 확인
./scripts/dev-start.sh logs
./scripts/dev-start.sh logs backend
./scripts/dev-start.sh logs db

# 서비스 재시작
./scripts/dev-start.sh restart

# 백엔드 이미지 재빌드
./scripts/dev-start.sh build

# 정리 (볼륨 포함)
./scripts/dev-start.sh clean

# 도움말
./scripts/dev-start.sh help
```

### dev-stop.sh - 개발환경 정리

```bash
# 서비스만 중지 (데이터 보존)
./scripts/dev-stop.sh
./scripts/dev-stop.sh stop

# 볼륨까지 삭제 (모든 데이터 삭제)
./scripts/dev-stop.sh clean

# 완전 초기화 (이미지까지 삭제)
./scripts/dev-stop.sh reset

# 도움말
./scripts/dev-stop.sh help
```

## 🔧 접속 정보

서비스 시작 후 다음 URL로 접속할 수 있습니다:

- **백엔드 API**: http://localhost:8080
- **API 문서**: http://localhost:8080/swagger-ui/index.html
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379
- **Elasticsearch**: http://localhost:9200
- **Nginx**: http://localhost:80

## 📝 환경 변수

`.env` 파일이 없으면 자동으로 기본값으로 생성됩니다:

```env
# Database
DB_NAME=prompt_center_dev
DB_USERNAME=prompth_user
DB_PASSWORD=dev_password_123

# Redis
REDIS_PASSWORD=dev_redis_123

# JWT
JWT_SECRET=c2VjcmV0LWtleS1tdXN0LWJlLWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmctZm9yLWp3dC1zaWduaW5n
```

필요에 따라 `.env` 파일을 수정하여 설정을 변경할 수 있습니다.

## 🔄 일반적인 개발 워크플로우

### 1. 하루 시작할 때

```bash
./scripts/dev-start.sh
```

### 2. 개발 중간에 백엔드만 재시작

```bash
# Ctrl+C로 백엔드 중지 후
./scripts/dev-start.sh backend
```

### 3. 하루 끝날 때

```bash
./scripts/dev-stop.sh
```

### 4. 깨끗하게 다시 시작하고 싶을 때

```bash
./scripts/dev-stop.sh clean
./scripts/dev-start.sh
```

## 🐛 문제 해결

### 포트 충돌 문제

```bash
# 기존 컨테이너 정리
./scripts/dev-stop.sh clean

# 시스템 포트 확인
sudo lsof -i :8080
sudo lsof -i :5432
```

### 백엔드 빌드 문제

```bash
# 이미지 재빌드
./scripts/dev-start.sh build

# 완전 초기화 후 재시작
./scripts/dev-stop.sh reset
./scripts/dev-start.sh
```

### 로그 확인

```bash
# 전체 로그
./scripts/dev-start.sh logs

# 특정 서비스 로그
./scripts/dev-start.sh logs backend
./scripts/dev-start.sh logs db
```

## 💡 팁

1. **백그라운드 실행**: 데이터베이스 서비스들은 백그라운드(`-d`)로 실행되어 터미널을 점유하지 않습니다.

2. **포그라운드 실행**: 백엔드는 포그라운드로 실행되어 로그를 바로 확인할 수 있습니다.

3. **단계별 실행**: 데이터베이스가 준비될 때까지 30초 대기 후 백엔드를 시작합니다.

4. **안전한 중지**: `Ctrl+C`로 백엔드를 중지해도 데이터베이스 서비스들은 계속 실행됩니다.

5. **빠른 재시작**: 데이터베이스는 그대로 두고 백엔드만 재시작할 수 있습니다.
