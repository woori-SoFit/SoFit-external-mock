# Design Document: Project Initial Setup

## Overview

본 설계 문서는 SoFit-external-mock 프로젝트의 초기 구조 세팅을 위한 기술 설계를 다룹니다. 이 프로젝트는 SoFit 서비스가 의존하는 외부 기관 API(국세청, CB사, 금융인증서)를 대체하는 Mock 서버로, Java 21과 Spring Boot 4.0.6 기반의 단일 모듈 Gradle 프로젝트입니다.

본 설계는 다음 영역을 포함합니다:
- Gradle 빌드 시스템 설정 (build.gradle, settings.gradle)
- Spring Boot 애플리케이션 설정 (application.yml)
- 데이터베이스 연결 설정 (MySQL)
- JPA/Hibernate 설정
- 로깅 설정
- 서버 포트 및 프로파일 설정
- 메인 애플리케이션 클래스

이 설계는 Infrastructure as Code 및 설정 파일 작성에 초점을 맞추고 있으며, 실제 비즈니스 로직이나 API 구현은 포함하지 않습니다.

## Architecture

### 시스템 아키텍처

```
┌─────────────────────────────────────────┐
│   SoFit-external-mock Application       │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │   Spring Boot 4.0.6                │ │
│  │   - Web MVC                        │ │
│  │   - Data JPA                       │ │
│  │   - Validation                     │ │
│  └────────────────────────────────────┘ │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │   Configuration Layer              │ │
│  │   - application.yml (prod profile)│ │
│  │   - Logging (Logback)              │ │
│  └────────────────────────────────────┘ │
│                                          │
└─────────────────┬────────────────────────┘
                  │
                  │ JDBC (MySQL Connector)
                  │
         ┌────────▼────────┐
         │  MySQL Database │
         │  sofit_external │
         │      _mock      │
         └─────────────────┘
```

### 빌드 시스템 아키텍처

```
┌──────────────────────────────────────┐
│  Gradle Build System                 │
│                                      │
│  ┌────────────────────────────────┐ │
│  │  settings.gradle               │ │
│  │  - rootProject.name            │ │
│  └────────────────────────────────┘ │
│                                      │
│  ┌────────────────────────────────┐ │
│  │  build.gradle                  │ │
│  │  - Java 21                     │ │
│  │  - Spring Boot Plugin 4.0.6    │ │
│  │  - Dependencies                │ │
│  │  - Test Configuration (제거)   │ │
│  │  - JaCoCo (제거)               │ │
│  │  - Checkstyle (제거)           │ │
│  └────────────────────────────────┘ │
└──────────────────────────────────────┘
```

### 프로파일 전략

본 프로젝트는 단일 환경(prod) 전략을 사용합니다:
- **prod 프로파일**: 개발 서버이자 배포 서버로 통합 운영
- 별도 노트북에서 `./gradlew bootRun` 또는 jar 직접 실행
- CI/CD 파이프라인 없음

## Components and Interfaces

### 1. Gradle 빌드 설정 (build.gradle)

**목적**: 프로젝트의 빌드 설정, 의존성 관리, Java 버전 설정

**주요 구성 요소**:
- **Plugins**:
  - `java`: Java 프로젝트 지원
  - `org.springframework.boot` (4.0.6): Spring Boot 애플리케이션 빌드
  - `io.spring.dependency-management`: Spring Boot 의존성 버전 관리

- **Project Metadata**:
  - `group`: 'com.sofit'
  - `version`: '0.0.1-SNAPSHOT'

- **Java Configuration**:
  - `sourceCompatibility`: Java 21

- **Dependencies**:
  - `spring-boot-starter-web`: REST API 지원
  - `spring-boot-starter-data-jpa`: JPA/Hibernate ORM
  - `spring-boot-starter-validation`: Bean Validation
  - `mysql-connector-j`: MySQL JDBC 드라이버 (runtime)
  - `lombok`: 보일러플레이트 코드 제거 (compileOnly + annotationProcessor)
  - `spring-security-crypto`: BCrypt 암호화 (PIN 해시 검증용)

**제거할 구성**:
- JUnit, Mockito 테스트 의존성
- JaCoCo 플러그인 및 설정
- Checkstyle 플러그인 및 설정

**설계 결정**:
- 테스트 프레임워크를 제거하는 이유: 본 프로젝트는 Mock 서버로, 실제 외부 API 호출 없이 DB 조회만으로 응답을 구성하므로 단위 테스트가 불필요합니다.
- 정적 분석 도구를 제거하는 이유: 소규모 Mock 서버로 코드 품질 도구 없이 수동 코드 리뷰로 충분합니다.

### 2. 프로젝트 설정 (settings.gradle)

**목적**: Gradle 프로젝트의 루트 이름 정의

**구성**:
```gradle
rootProject.name = 'sofit-external-mock'
```

**설계 결정**:
- 단일 모듈 프로젝트이므로 멀티모듈 설정 불필요
- 프로젝트 이름은 kebab-case 사용

### 3. 애플리케이션 설정 (application.yml)

**목적**: Spring Boot 애플리케이션의 런타임 설정

**구조**:
```yaml
spring:
  profiles:
    active: prod  # 기본 프로파일을 prod로 변경

---
spring:
  config:
    activate:
      on-profile: prod  # dev → prod로 변경
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sofit_external_mock?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: password
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
    open-in-view: false

logging:
  level:
    com.sofit.externalmock: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

server:
  port: 8080
```

**주요 설정 영역**:

#### 3.1 데이터베이스 연결 (spring.datasource)
- **driver-class-name**: MySQL JDBC 드라이버
- **url**: 
  - 호스트: localhost:3306
  - 데이터베이스: sofit_external_mock
  - 파라미터:
    - `useSSL=false`: SSL 비활성화 (로컬 개발 환경)
    - `serverTimezone=Asia/Seoul`: 서울 타임존
    - `characterEncoding=UTF-8`: UTF-8 인코딩
- **username**: root
- **password**: password

**설계 결정**:
- 로컬 환경에서만 실행되므로 SSL 비활성화
- 타임존을 명시적으로 설정하여 날짜/시간 데이터 일관성 보장

#### 3.2 JPA 설정 (spring.jpa)
- **hibernate.ddl-auto**: validate
  - 스키마 자동 생성 없음
  - 기존 스키마와 엔티티 매핑 검증만 수행
- **show-sql**: true (SQL 쿼리 로깅)
- **properties.hibernate.format_sql**: true (SQL 포맷팅)
- **properties.hibernate.dialect**: MySQLDialect
- **open-in-view**: false (OSIV 패턴 비활성화)

**설계 결정**:
- `ddl-auto: validate`: 프로덕션 환경에서 스키마 변경 방지, 수동 마이그레이션 권장
- `open-in-view: false`: 트랜잭션 범위를 명확히 하고 LazyInitializationException 방지

#### 3.3 로깅 설정 (logging.level)
- **com.sofit.externalmock**: DEBUG
- **org.hibernate.SQL**: DEBUG (SQL 쿼리 로깅)
- **org.hibernate.type.descriptor.sql.BasicBinder**: TRACE (바인딩 파라미터 로깅)

**설계 결정**:
- 개발/디버깅 편의를 위해 상세 로깅 활성화
- SQL 쿼리와 파라미터를 모두 로깅하여 DB 작업 추적 용이

#### 3.4 서버 설정 (server.port)
- **port**: 8080

### 4. 메인 애플리케이션 클래스

**위치**: `com.sofit.externalmock.ExternalMockApplication`

**구조**:
```java
package com.sofit.externalmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExternalMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalMockApplication.class, args);
    }

}
```

**설계 결정**:
- `@SpringBootApplication`: 컴포넌트 스캔, 자동 설정, 설정 클래스 기능 통합
- 패키지 위치: `com.sofit.externalmock` (모든 도메인 패키지의 루트)

## Data Models

본 설계는 초기 프로젝트 구조 세팅에 관한 것이므로, 실제 데이터 모델(Entity)은 포함하지 않습니다. 데이터 모델은 향후 각 도메인(KYC, 금융인증서, CB) 구현 시 별도로 설계됩니다.

**참고**: 향후 구현될 엔티티는 다음과 같습니다:
- `ExtKycRecord`: 국세청 KYC 데이터
- `ExtFinancialCert`: 금융인증서 데이터
- `ExtCbResult`: CB 조회 결과 데이터

## Error Handling

본 설계 단계에서는 애플리케이션 설정만 다루므로, 구체적인 에러 핸들링 로직은 포함하지 않습니다.

**향후 구현 방향**:
- `global/apiPayload/` 패키지에 공통 예외 처리 구조 구현
- `GlobalExceptionHandler`에서 `@ExceptionHandler`를 통한 일괄 처리
- 도메인별 `ErrorCode` enum 정의

## Testing Strategy

### 테스트 전략 개요

본 프로젝트는 **테스트 프레임워크를 사용하지 않습니다**. 이는 다음과 같은 이유 때문입니다:

1. **Mock 서버의 특성**: 실제 외부 API 호출 없이 DB 조회만으로 응답을 구성하는 단순한 구조
2. **비즈니스 로직 최소화**: 복잡한 비즈니스 로직이 없고, 주로 CRUD 작업과 단순 검증만 수행
3. **운영 환경**: 별도 노트북에서 수동 운영하며, CI/CD 파이프라인이 없음

### Property-Based Testing 비적용 사유

본 프로젝트는 다음과 같은 이유로 Property-Based Testing이 적합하지 않습니다:

1. **Infrastructure as Code**: Gradle 빌드 설정, application.yml 등 설정 파일 작성이 주요 작업
2. **설정 검증**: 설정 파일의 정확성은 애플리케이션 실행 시 Spring Boot의 자동 검증으로 확인 가능
3. **단순 CRUD**: 향후 구현될 API도 단순 DB 조회 및 응답 반환으로, 복잡한 변환 로직이 없음

### 검증 방법

설정의 정확성은 다음 방법으로 검증합니다:

1. **애플리케이션 실행 테스트**:
   ```bash
   ./gradlew bootRun
   ```
   - Spring Boot가 정상적으로 시작되는지 확인
   - 데이터베이스 연결이 성공하는지 확인
   - 로그에서 설정 값이 올바르게 적용되었는지 확인

2. **빌드 검증**:
   ```bash
   ./gradlew build
   ```
   - 컴파일 오류가 없는지 확인
   - 의존성이 올바르게 해결되는지 확인

3. **데이터베이스 연결 검증**:
   - 애플리케이션 시작 시 Hibernate가 스키마를 검증 (`ddl-auto: validate`)
   - 연결 실패 시 명확한 에러 메시지 출력

4. **수동 API 테스트**:
   - 향후 API 구현 후 Postman 또는 curl을 통한 수동 테스트
   - 예상 응답 형식과 실제 응답 비교

### 코드 품질 관리

테스트 프레임워크 대신 다음 방법으로 코드 품질을 관리합니다:

1. **코드 리뷰**: 팀원 간 수동 코드 리뷰
2. **컨벤션 준수**: BE 개발 컨벤션 문서 기반 일관성 유지
3. **실행 검증**: 실제 애플리케이션 실행을 통한 동작 확인

## Implementation Notes

### 구현 순서

1. **build.gradle 수정**:
   - 테스트 관련 의존성 제거 (JUnit, Mockito)
   - JaCoCo 플러그인 및 설정 제거
   - Checkstyle 플러그인 및 설정 제거
   - Spring Security Crypto 의존성 추가

2. **application.yml 수정**:
   - 기본 프로파일을 `dev`에서 `prod`로 변경
   - 프로파일 섹션 이름을 `dev`에서 `prod`로 변경

3. **검증**:
   - `./gradlew clean build` 실행하여 빌드 성공 확인
   - `./gradlew bootRun` 실행하여 애플리케이션 시작 확인
   - 로그에서 prod 프로파일이 활성화되었는지 확인
   - 데이터베이스 연결 성공 확인

### 주의사항

1. **데이터베이스 준비**:
   - MySQL 서버가 localhost:3306에서 실행 중이어야 함
   - `sofit_external_mock` 데이터베이스가 생성되어 있어야 함
   - root 계정의 비밀번호가 'password'로 설정되어 있어야 함

2. **Java 버전**:
   - Java 21이 설치되어 있어야 함
   - `JAVA_HOME` 환경 변수가 올바르게 설정되어 있어야 함

3. **Gradle 버전**:
   - Gradle Wrapper를 사용하므로 별도 설치 불필요
   - `./gradlew` (Linux/Mac) 또는 `gradlew.bat` (Windows) 사용

4. **설정 파일 보안**:
   - 현재는 개발 환경이므로 하드코딩된 비밀번호 사용
   - 향후 프로덕션 환경에서는 환경 변수 또는 외부 설정 파일 사용 권장

### 향후 확장 고려사항

1. **프로파일 분리**:
   - 현재는 prod 프로파일만 사용
   - 필요시 dev, test 프로파일 추가 가능

2. **외부 설정**:
   - 민감한 정보(DB 비밀번호)를 환경 변수로 관리
   - Spring Cloud Config 또는 외부 properties 파일 사용

3. **모니터링**:
   - Spring Boot Actuator 추가 고려
   - 헬스 체크 엔드포인트 활성화

4. **로깅 개선**:
   - Logback 설정 파일(logback-spring.xml) 추가
   - 파일 로깅, 로그 로테이션 설정

## Appendix

### 참고 문서

- [Spring Boot 4.0.6 Documentation](https://docs.spring.io/spring-boot/docs/4.0.6/reference/html/)
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- [MySQL Connector/J Documentation](https://dev.mysql.com/doc/connector-j/en/)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)

### 관련 Steering 파일

- `tech-stack.md`: 기술 스택 정의
- `product.md`: 서비스 개요 및 Mock API 목록
- `mock-conventions.md`: BE 개발 컨벤션
- `git-infra.md`: Git 및 인프라 컨벤션

### 설정 파일 예시

#### build.gradle (수정 후)
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.6'
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'com.sofit'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '21'
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // MySQL
    runtimeOnly 'com.mysql:mysql-connector-j'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Spring Security Crypto (BCrypt용)
    implementation 'org.springframework.security:spring-security-crypto'
}
```

#### application.yml (수정 후)
```yaml
spring:
  profiles:
    active: prod

---
spring:
  config:
    activate:
      on-profile: prod
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sofit_external_mock?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: password
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
    open-in-view: false

logging:
  level:
    com.sofit.externalmock: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

server:
  port: 8080
```
