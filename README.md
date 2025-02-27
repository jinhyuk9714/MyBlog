# 📝 MyBlog - 개인 블로그 프로젝트

이 저장소는 **Spring Boot 기반의 개인 블로그 프로젝트**입니다.  
사용자는 **게시글 작성, 댓글 관리, JWT 기반 로그인, OAuth2 소셜 로그인** 등을 사용할 수 있습니다.

---

## 📌 프로젝트 개요

이 프로젝트는 **Spring Boot + MongoDB + JWT + Redis**를 활용하여,  
개인 블로그 서비스를 제공하는 Full-Stack 백엔드 시스템입니다.

### ✅ 포함된 주요 기능
- **회원 가입 및 로그인**
  - 일반 로그인 (JWT 기반)
  - Google OAuth2 소셜 로그인
  - JWT + Redis 기반 리프레시 토큰 관리
- **블로그 기능**
  - 게시글 CRUD (작성, 수정, 삭제, 조회)
  - 댓글 기능 (댓글 작성, 삭제)
  - 게시글 좋아요 기능
- **관리자 기능**
  - 관리자만 게시글을 삭제할 수 있도록 권한 관리 (`ROLE_ADMIN`)
- **Swagger API 문서 제공**
  - `/swagger-ui.html` 에서 API 문서 확인 가능

---

## 🚀 기술 스택

### 🔹 Backend
- **Spring Boot 3.0**
- **Spring Security & OAuth2 Client** (JWT 인증 & 소셜 로그인)
- **MongoDB** (NoSQL 데이터베이스)
- **Redis** (세션 관리 & Refresh Token 저장)
- **Lombok** (코드 간결화)
- **Swagger 3.0** (API 문서 자동 생성)

---


