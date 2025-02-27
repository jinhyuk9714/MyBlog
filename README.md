# 📝 MyBlog - 개인 블로그 프로젝트

이 저장소는 **Spring Boot 기반의 개인 블로그 프로젝트**입니다.  
사용자는 **게시글 작성, 댓글 관리, JWT 기반 로그인, OAuth2 소셜 로그인** 등을 사용할 수 있습니다.

---

## 📌 프로젝트 개요

이 프로젝트는 **Spring Boot + MongoDB + JWT + Redis**를 활용하여,  
개인 블로그 서비스를 제공하는 Full-Stack 백엔드 시스템입니다.

### ✅ 포함된 주요 기능
- **회원 가입 및 로그인**
  - ✅ 일반 로그인 (JWT 기반)
  - ✅ Google OAuth2 소셜 로그인
  - ✅ JWT + Redis 기반 리프레시 토큰 관리
- **블로그 기능 (미구현)**
  - ✅ 게시글 CRUD (작성, 수정, 삭제, 조회)
  - ✅ 댓글 기능 (댓글 작성, 삭제)
  - ✅ 게시글 좋아요 기능
- **관리자 기능 (미구현)**
  - ✅ 관리자만 게시글을 삭제할 수 있도록 권한 관리 (`ROLE_ADMIN`)
- **Swagger API 문서 제공**
  - `/swagger-ui.html` 에서 API 문서 확인 가능

---

## 📂 프로젝트 폴더 구조

이 프로젝트는 **Spring Boot 백엔드 코드**로 구성되어 있습니다.

```
📦 myblog
 ┣ 📂 src/
 ┃ ┣ 📂 main/java/com/example/myblog
 ┃ ┃ ┣ 📂 config/       # ✅ 보안 & 환경 설정 (JWT, Security, Redis 등)
 ┃ ┃ ┣ 📂 controller/   # ✅ REST API 컨트롤러 (블로그, 댓글, 인증 등)
 ┃ ┃ ┣ 📂 dto/          # ✅ 데이터 전송 객체 (Request, Response)
 ┃ ┃ ┣ 📂 entity/       # ✅ MongoDB 엔티티 (User, Post, Comment 등)
 ┃ ┃ ┣ 📂 repository/   # ✅ MongoDB 저장소 (User, Post, Comment)
 ┃ ┃ ┣ 📂 service/      # ✅ 비즈니스 로직 (인증, 게시글, 댓글 등)
 ┃ ┃ ┗ 📂 utils/        # ✅ 유틸리티 클래스 (JWT, TimeFormatter 등)
 ┣ 📄 pom.xml           # 프로젝트 의존성 관리 (Maven)
 ┣ 📄 README.md         # 현재 파일
 ┣ 📄 .gitignore        # Git 추적 제외 파일 목록
 ┣ 📄 application.yml   # 환경 설정 파일 (DB, OAuth, JWT 키 등)
 ┗ 📄 Dockerfile        # Docker 배포 설정 파일 (선택사항)
```

---

## 🚀 기술 스택

### 🔹 Backend
- **Spring Boot 3.0**
- **Spring Security & OAuth2 Client** (JWT 인증 & 소셜 로그인)
- **MongoDB** (NoSQL 데이터베이스)
- **Redis** (세션 관리 & Refresh Token 저장)
- **Lombok** (코드 간결화)
- **Swagger 3.0** (API 문서 자동 생성)

### 🔹 DevOps & 기타
- **Docker** (컨테이너 배포 지원)
- **GitHub Actions** (CI/CD 자동화)
- **Nginx + SSL** (보안 설정)
- **AWS EC2 + S3** (클라우드 배포)

---

## 🔑 API 사용 방법

### 🔹 1️⃣ 로컬 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/jinhyuk9714/myblog.git
cd myblog

# 2. 환경 변수 설정 (application.yml 필요)
cp src/main/resources/application.yml
# 필요한 정보 (DB 연결, OAuth 키) 수정

# 3. 빌드 & 실행
./mvnw clean package
java -jar target/myblog-0.0.1-SNAPSHOT.jar
```

---

## 🛠️ Swagger API 문서 확인

로컬 실행 후, 브라우저에서 다음 URL로 접속하세요.

```bash
http://localhost:8080/swagger-ui/index.html
```

---

## 📜 주요 기능 설명

### 🏷️ 1. 사용자 인증 (로그인 & 회원가입)

- 🔐 **JWT 기반 로그인** → `POST /api/auth/login`
- 🔐 **Google OAuth2 소셜 로그인** → `GET /api/auth/oauth-success`
- 🔐 **로그아웃 (Redis에서 리프레시 토큰 삭제)** → `POST /api/auth/logout`
- 🔐 **JWT 기반 사용자 정보 조회** → `GET /api/auth/user`

### 📝 2. 게시글 관리 (미구현)

- 📝 **게시글 작성** → `POST /api/posts`
- 📖 **게시글 조회** → `GET /api/posts/{id}`
- ✏️ **게시글 수정** → `PUT /api/posts/{id}`
- ❌ **게시글 삭제** → `DELETE /api/posts/{id}`

### 💬 3. 댓글 관리 (미구현)

- 💬 **댓글 작성** → `POST /api/comments`
- ❌ **댓글 삭제** → `DELETE /api/comments/{id}`

### ❤️ 4. 좋아요 기능 (미구현)

- ❤️ **게시글 좋아요** → `POST /api/posts/{id}/like`
- 💔 **게시글 좋아요 취소** → `DELETE /api/posts/{id}/like`

---

## 🎯 환경 변수 설정 (application.yml)

프로젝트를 실행하기 전에 환경 변수 설정이 필요합니다.  
MongoDB, Redis, JWT Secret Key, OAuth2 Client ID 등을 지정하세요.

```yaml
spring:
  config:
    import: optional:file:.env  # ✅ .env 파일에서 환경 변수 로드
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/blog}  # ✅ 환경 변수 사용
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
  cache:
    type: redis  # ✅ Redis를 기본 캐시로 설정
  jackson:
    time-zone: UTC  # ✅ JSON 직렬화 시 UTC 사용
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - profile
              - email
            redirect-uri: "{baseUrl}/login/oauth2/code/google"

server:
  port: 8080

jwt:
  secret-key: ${JWT_SECRET_KEY}
  access-token-expiration: ${ACCESS_TOKEN_EXPIRATION:900000}  # ✅ 15분 (밀리초)
  refresh-token-expiration: ${REFRESH_TOKEN_EXPIRATION:604800000}  # ✅ 7일 (밀리초)

logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
    com.example.myblog: DEBUG

```

---

## 🚀 배포 방법 (Docker + AWS) (미구현)

이 프로젝트는 Docker 컨테이너로 실행할 수 있습니다.

### 🔹 1️⃣ Docker 컨테이너 실행

```bash
docker build -t myblog .
docker run -p 8080:8080 myblog
```

### 🔹 2️⃣ AWS EC2 배포

```bash
scp -i my-key.pem myblog.jar ubuntu@ec2-ip:/home/ubuntu/
ssh -i my-key.pem ubuntu@ec2-ip
java -jar myblog.jar
```

---
