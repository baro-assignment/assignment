# 프로젝트 설명
이 프로젝트는 Spring Boot 기반 JWT 인증/인가 기능을 구현하고, AWS 배포를 수행한 백엔드 프로젝트입니다.

# 구현 내용
## 1. 기능 구현
- 회원가입 및 로그인 API 구현
- 관리자 전용 API 구현
  - 관리자 권한 부여
  - 전체 사용자 프로필 조회
- 사용자 전용 API 구현
  - 본인 프로필 조회
- [JWT 기반 사용자 인증 모듈 구현](https://github.com/baro-assignment/assignment/pull/8)
- [Spring Security 인증/인가 실패 처리 모듈 구현](https://github.com/baro-assignment/assignment/pull/12)

## 2. Swagger 문서화
> [배포 서버 Swagger 문서](http://13.125.250.27:8080/swagger-ui/index.html)

위 링크를 클릭하여 배포된 서버의 Swagger 문서에 접속 가능합니다.

### 인증 헤더 설정 방법
<img width="1000" alt="Screenshot 2025-07-06 at 16 50 39" src="https://github.com/user-attachments/assets/e996206f-c3c2-45b0-b609-eef892e54f53" />


1. `사용자 회원가입` 혹은 `관리자 회원가입` API 중 하나를 실행해 회원가입을 진행합니다.

2. `로그인` API를 실행하여 응답으로 token 값을 얻습니다.

<img width="1000" alt="Screenshot 2025-07-06 at 16 37 01" src="https://github.com/user-attachments/assets/abd4e00a-b21c-4cbd-bbfc-eb7d287d34ee" />

3. `Authorize` 버튼을 클릭한 후, token 값을 그대로 입력합니다. Bearer prefix는 자동으로 추가되므로, 임의로 추가하지 않아야 합니다.

<img width="1000" alt="Screenshot 2025-07-06 at 16 50 51" src="https://github.com/user-attachments/assets/eda859fd-6877-463e-bdf3-532fe0fa3baf" />


### 로컬 실행 시
프로젝트를 로컬에서 실행한 경우, `/swagger` 경로로 Swagger UI에 접속 가능합니다.
- [커스텀 어노테이션을 구현하여 Swagger 문서에 쉽게 에러 코드, 예시가 등록되도록 구현](https://github.com/baro-assignment/assignment/pull/20)

## 3. Junit 기반 테스트 코드 작성
- [회원가입/로그인 컨트롤러 단위 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/auth/controller/AuthControllerTest.java)
- [관리자 권한 부여 컨트롤러 단위 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/admin/controller/AdminControllerTest.java)
- [회원가입/로그인 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/auth/AuthIntegrationTest.java)
- [관리자 권한 부여 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/domain/admin/AdminIntegrationTest.java)
- [인증 및 권한 제어 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/global/auth/AuthSecurityIntegrationTest.java)
- [JWT 토큰 검증 통합 테스트](https://github.com/baro-assignment/assignment/blob/dev/src/test/java/com/example/assignment/global/auth/JwtIntegrationTest.java)

# 실행 방법
## 1. 프로젝트 클론
```bash
git clone https://github.com/baro-assignment/assignment.git
cd assignment 
```
## 2. 환경 변수 설정
`.env` 파일 또는 시스템 환경변수에 `application.yml`에 명세된 값들을 설정하세요.

## 3. 로컬 서버 실행
```bash
./gradlew build
java -jar build/libs/assignment-0.0.1-SNAPSHOT.jar
```

# API 명세 요약
> 자세한 요청/응답 형식은 [Swagger 문서](http://13.125.250.27:8080/swagger-ui/index.html)

| 경로 (Path)                     | 메서드   | 설명                 | 권한    |
| ----------------------------- | ----- | ------------------ | ----- |
| [과제 필수] `/signup/user`                | POST  | 사용자 회원가입        | 공개    |
| [과제 필수] `/signup/admin`               | POST  | 관리자 회원가입        | 공개    |
| [과제 필수] `/login`                      | POST  | 로그인 및 JWT 토큰 발급 | 공개    |
| `/auth/check`                 | GET   | JWT 토큰 유효성 검증   | USER / ADMIN |
| `/users/me`                   | GET   | 내 정보 조회          | USER / ADMIN |
| `/admin/users`                | GET   | 전체 사용자 목록 조회       | ADMIN   |
| [과제 필수] `/admin/users/{userId}/grant` | PATCH | 특정 사용자에게 관리자 권한 부여 | ADMIN   |

