# 통합 Location 설정 (Frontend + Backend)

# === 백엔드 전용 경로 ===

# API 요청 (최우선순위)
location /api/ {
    proxy_pass http://backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Port $server_port;
    proxy_http_version 1.1;
    proxy_set_header Connection "";
}

# Health Check
location /actuator/ {
    proxy_pass http://backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    access_log off;
}

# Swagger UI
location /swagger-ui/ {
    proxy_pass http://backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Port $server_port;
}

# API 문서
location /v3/api-docs {
    proxy_pass http://backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Port $server_port;
}

# === 프론트엔드 전용 경로 ===

# Next.js 내부 정적 파일 (_next/static/ 포함)
location /_next/ {
    proxy_pass http://frontend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    expires 1y;
    add_header Cache-Control "public, immutable";
    access_log off;
}

# 프론트엔드 정적 파일 (public 폴더)
location /static/ {
    proxy_pass http://frontend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    expires 1y;
    add_header Cache-Control "public, immutable";
    access_log off;
}

# favicon과 robot.txt 등 루트 레벨 정적 파일
location ~* ^/(favicon\.ico|robots\.txt|sitemap\.xml|manifest\.json)$ {
    proxy_pass http://frontend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    expires 7d;
    add_header Cache-Control "public";
    access_log off;
}

# 일반 정적 파일들 (images, fonts, CSS, JS 등)
location ~* \.(ico|png|jpg|jpeg|gif|svg|webp|avif|woff|woff2|ttf|eot|otf|css|js|map)$ {
    proxy_pass http://frontend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    expires 30d;
    add_header Cache-Control "public";
    access_log off;
}

# === 공통 설정 ===

# 프론트엔드 헬스체크
location /frontend-health {
    proxy_pass http://frontend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    access_log off;
}

# 모든 기타 요청을 프론트엔드로 라우팅 (SPA 지원)
location / {
    proxy_pass http://frontend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Port $server_port;

    # WebSocket 지원 (Next.js HMR 등)
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_cache_bypass $http_upgrade;

    # SPA 라우팅 지원을 위한 프록시 설정
    # 프론트엔드 애플리케이션이 직접 라우팅을 처리하도록 함
    proxy_redirect off;
}
