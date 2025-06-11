## 프로젝트 설명
이 프로젝트는 Spring Boot 기반 JWT 인증/인가 기능을 구현하고, AWS 배포를 수행한 백엔드 프로젝트입니다.

## 구현 내용
### 1. 기능 구현
- 회원가입 및 로그인 API 구현
  - 회원가입 : `POST /signup`
  - 로그인 : `POST /login`
- 관리자 전용 API 구현
  - 관리자 권한 부여 : `PATCH /admin/users/{userId}/roles`
  - 전체 사용자 프로필 조회 : `GET /admin/users`
- 사용자 전용 API 구현
  - 본인 프로필 조회 : `GET /users/me`
- [JWT 기반 사용자 인증 모듈 구현](https://github.com/baro-assignment/assignment/pull/8)
- [Spring Security 인증/인가 실패 처리 모듈 구현](https://github.com/baro-assignment/assignment/pull/12)

### 2. Swagger 문서화
> [배포 서버 Swagger 문서](http://43.200.182.80:8080/swagger-ui/index.html)

프로젝트를 로컬에서 실행한 경우, `/swagger` 경로로 Swagger UI에 접속 가능합니다.
- [커스텀 어노테이션을 구현하여 Swagger 문서에 쉽게 에러 코드, 예시가 등록되도록 구현](https://github.com/baro-assignment/assignment/pull/20)

### 3. Junit 기반 테스트 코드 작성
- [회원가입/로그인 컨트롤러 단위 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/auth/controller/AuthControllerTest.java)
- [관리자 권한 부여 컨트롤러 단위 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/admin/controller/AdminControllerTest.java)
- [회원가입/로그인 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/auth/AuthIntegrationTest.java)
- [인증/인가 보안 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/global/auth/AuthSecurityIntegrationTest.java)
- [JWT 토큰 검증 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/global/auth/JwtIntegrationTest.java)

## 실행 방법
### 1. 프로젝트 클론 및 의존성 설치
```bash
git clone https://github.com/baro-assignment/assignment.git
cd assignment
./gradlew build
```
### 2. 환경 변수 설정
`.env` 파일 또는 시스템 환경변수에 `application.yml`에 명세된 값들을 설정하세요.

### 3. 로컬 서버 실행
```bash
./gradlew build
java -jar build/libs/assignment-0.0.1-SNAPSHOT.jar
```

## API 명세 요약
> 자세한 요청/응답 형식은 [Swagger 문서](http://43.200.182.80:8080/swagger-ui/index.html)

| 경로 (Path)                     | 메서드   | 설명                 | 권한    |
| ----------------------------- | ----- | ------------------ | ----- |
| `/signup`                     | POST  | 회원가입               | 공개    |
| `/login`                      | POST  | 로그인 및 JWT 토큰 발급    | 공개    |
| `/auth/check`                 | GET   | JWT 토큰 유효성 검증      | 인증 필요 |
| `/users/me`                   | GET   | 내 정보 조회            | 인증 필요 |
| `/admin/users`                | GET   | 전체 사용자 목록 조회       | 관리자   |
| `/admin/users/{userId}/roles` | PATCH | 특정 사용자에게 관리자 권한 부여 | 관리자   |


