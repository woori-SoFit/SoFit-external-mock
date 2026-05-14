# Implementation Plan: Project Initial Setup

## Overview

SoFit-external-mock 프로젝트의 초기 구조를 세팅합니다. 이 작업은 Gradle 빌드 설정, Spring Boot 애플리케이션 설정, 데이터베이스 연결 설정, JPA 설정, 로깅 설정을 포함합니다. 본 프로젝트는 Java 21과 Spring Boot 4.0.6 기반의 단일 모듈 Gradle 프로젝트입니다.

## Tasks

- [ ] 1. Gradle 빌드 설정 수정
  - [x] 1.1 build.gradle 파일 수정
    - 테스트 관련 의존성 제거 (JUnit, Mockito 등)
    - JaCoCo 플러그인 및 설정 제거
    - Checkstyle 플러그인 및 설정 제거
    - Spring Security Crypto 의존성 추가 (BCrypt용)
    - Java 21, Spring Boot 4.0.6 버전 확인
    - 필수 의존성 확인: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, mysql-connector-j, lombok
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.10_
  
  - [x] 1.2 settings.gradle 파일 확인
    - rootProject.name이 'sofit-external-mock'로 설정되어 있는지 확인
    - _Requirements: 1.10_

- [ ] 2. Spring Boot 애플리케이션 설정 파일 수정
  - [ ] 2.1 application.yml 파일 수정
    - 기본 활성 프로파일을 'prod'로 설정
    - prod 프로파일 섹션 생성 (기존 dev 프로파일 이름 변경)
    - 데이터베이스 연결 설정: MySQL JDBC 드라이버, localhost:3306/sofit_external_mock, UTF-8 인코딩, Asia/Seoul 타임존, SSL 비활성화
    - 데이터베이스 인증 정보: username=root, password=password
    - JPA 설정: ddl-auto=validate, show-sql=true, format_sql=true, MySQLDialect, open-in-view=false
    - 로깅 설정: com.sofit.externalmock=DEBUG, Hibernate SQL=DEBUG, BasicBinder=TRACE
    - 서버 포트: 8080
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 4.3, 5.1, 6.1, 6.2_

- [ ] 3. 메인 애플리케이션 클래스 생성
  - [ ] 3.1 ExternalMockApplication 클래스 생성
    - 패키지: com.sofit.externalmock
    - @SpringBootApplication 어노테이션 추가
    - main 메서드에서 SpringApplication.run() 호출
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ] 4. 빌드 및 실행 검증
  - [ ] 4.1 빌드 검증
    - `./gradlew clean build` 실행하여 컴파일 오류 없는지 확인
    - 의존성이 올바르게 해결되는지 확인
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8_
  
  - [ ] 4.2 애플리케이션 실행 검증
    - `./gradlew bootRun` 실행하여 Spring Boot가 정상 시작되는지 확인
    - 로그에서 prod 프로파일이 활성화되었는지 확인
    - 데이터베이스 연결 시도 확인 (연결 실패는 정상 - DB가 아직 준비되지 않았을 수 있음)
    - 서버가 8080 포트에서 시작되는지 확인
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 3.1, 4.1, 4.2, 4.3, 5.1, 6.1, 7.1, 7.2, 7.3, 7.4_

- [ ] 5. Checkpoint - 초기 설정 완료 확인
  - 모든 설정 파일이 올바르게 수정되었는지 확인
  - 빌드가 성공하고 애플리케이션이 시작되는지 확인
  - 사용자에게 질문이 있으면 문의

## Notes

- 본 작업은 Infrastructure as Code 및 설정 파일 작성에 초점을 맞추고 있으며, 실제 비즈니스 로직이나 API 구현은 포함하지 않습니다
- 데이터베이스 연결 검증을 위해서는 MySQL 서버가 localhost:3306에서 실행 중이어야 하며, `sofit_external_mock` 데이터베이스가 생성되어 있어야 합니다
- Java 21이 설치되어 있고 `JAVA_HOME` 환경 변수가 올바르게 설정되어 있어야 합니다
- 테스트 프레임워크를 제거하는 이유: Mock 서버로 단순 DB 조회만 수행하므로 단위 테스트가 불필요합니다
- 정적 분석 도구를 제거하는 이유: 소규모 Mock 서버로 수동 코드 리뷰로 충분합니다
- 모든 작업은 코딩 에이전트가 수행할 수 있는 파일 수정 작업입니다

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2"] },
    { "id": 1, "tasks": ["2.1", "3.1"] },
    { "id": 2, "tasks": ["4.1"] },
    { "id": 3, "tasks": ["4.2"] }
  ]
}
```
