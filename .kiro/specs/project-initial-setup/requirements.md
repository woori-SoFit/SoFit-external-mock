# Requirements Document

## Introduction

SoFit-external-mock 프로젝트의 초기 구조 세팅을 위한 요구사항 문서입니다. 이 프로젝트는 SoFit 서비스가 의존하는 외부 기관 API(국세청, CB사, 금융인증서)를 대체하는 Mock 서버로, Java 21과 Spring Boot 4.0.6 기반의 단일 모듈 Gradle 프로젝트입니다. 본 요구사항은 프로젝트의 기본 빌드 설정, 데이터베이스 연결 설정, JPA 설정, 로깅 설정, 서버 설정을 포함합니다.

## Glossary

- **Build_System**: Gradle 빌드 시스템 (build.gradle, settings.gradle)
- **Application_Configuration**: Spring Boot 애플리케이션 설정 파일 (application.yml)
- **Main_Application_Class**: Spring Boot 애플리케이션의 진입점 클래스
- **Database**: MySQL 데이터베이스
- **Prod_Profile**: 프로덕션 환경 프로파일 설정 (개발 서버이자 배포 서버로 통합)

## Requirements

### Requirement 1: Gradle 빌드 시스템 설정

**User Story:** As a 개발자, I want Gradle 빌드 설정이 완료되어 있기를, so that 프로젝트를 빌드하고 실행할 수 있다

#### Acceptance Criteria

1. THE Build_System SHALL use Java 21 as the source compatibility version
2. THE Build_System SHALL use Spring Boot version 4.0.6
3. THE Build_System SHALL include Spring Boot Starter Web dependency
4. THE Build_System SHALL include Spring Boot Starter Data JPA dependency
5. THE Build_System SHALL include Spring Boot Starter Validation dependency
6. THE Build_System SHALL include MySQL Connector dependency as runtime dependency
7. THE Build_System SHALL include Lombok as compile-only and annotation processor dependency
8. THE Build_System SHALL include Spring Security Crypto dependency for BCrypt password encoding
9. THE Build_System SHALL set the project group to 'com.sofit'
10. THE Build_System SHALL set the root project name to 'sofit-external-mock'

### Requirement 2: 데이터베이스 연결 설정

**User Story:** As a 개발자, I want MySQL 데이터베이스 연결 설정이 완료되어 있기를, so that 애플리케이션이 데이터베이스에 연결하여 데이터를 저장하고 조회할 수 있다

#### Acceptance Criteria

1. WHERE Prod_Profile is active, THE Application_Configuration SHALL use MySQL JDBC driver
2. WHERE Prod_Profile is active, THE Application_Configuration SHALL connect to database at `localhost:3306/sofit_external_mock`
3. WHERE Prod_Profile is active, THE Application_Configuration SHALL use UTF-8 character encoding
4. WHERE Prod_Profile is active, THE Application_Configuration SHALL use Asia/Seoul timezone
5. WHERE Prod_Profile is active, THE Application_Configuration SHALL disable SSL connection
6. WHERE Prod_Profile is active, THE Application_Configuration SHALL set database username to 'root'
7. WHERE Prod_Profile is active, THE Application_Configuration SHALL set database password to 'password'

### Requirement 3: JPA 설정

**User Story:** As a 개발자, I want JPA 설정이 완료되어 있기를, so that ORM을 통해 데이터베이스 작업을 수행할 수 있다

#### Acceptance Criteria

1. WHERE Prod_Profile is active, THE Application_Configuration SHALL set Hibernate DDL auto mode to 'validate'
2. WHERE Prod_Profile is active, THE Application_Configuration SHALL enable SQL logging
3. WHERE Prod_Profile is active, THE Application_Configuration SHALL enable SQL formatting
4. WHERE Prod_Profile is active, THE Application_Configuration SHALL use MySQL dialect
5. WHERE Prod_Profile is active, THE Application_Configuration SHALL disable open-in-view pattern

### Requirement 4: 로깅 설정

**User Story:** As a 개발자, I want 로깅 설정이 완료되어 있기를, so that 애플리케이션 실행 중 디버깅 정보를 확인할 수 있다

#### Acceptance Criteria

1. WHERE Prod_Profile is active, THE Application_Configuration SHALL set application logging level to DEBUG for 'com.sofit.externalmock' package
2. WHERE Prod_Profile is active, THE Application_Configuration SHALL set Hibernate SQL logging level to DEBUG
3. WHERE Prod_Profile is active, THE Application_Configuration SHALL set Hibernate SQL parameter binding logging level to TRACE

### Requirement 5: 서버 설정

**User Story:** As a 개발자, I want 서버 포트 설정이 완료되어 있기를, so that 애플리케이션이 지정된 포트에서 실행된다

#### Acceptance Criteria

1. WHERE Prod_Profile is active, THE Application_Configuration SHALL set server port to 8080

### Requirement 6: 프로파일 설정

**User Story:** As a 개발자, I want 기본 프로파일이 설정되어 있기를, so that 환경별 설정을 관리할 수 있다

#### Acceptance Criteria

1. THE Application_Configuration SHALL set default active profile to 'prod'
2. THE Application_Configuration SHALL define prod profile configuration section

### Requirement 7: 메인 애플리케이션 클래스

**User Story:** As a 개발자, I want Spring Boot 메인 클래스가 존재하기를, so that 애플리케이션을 실행할 수 있다

#### Acceptance Criteria

1. THE Main_Application_Class SHALL be located at package 'com.sofit.externalmock'
2. THE Main_Application_Class SHALL be named 'ExternalMockApplication'
3. THE Main_Application_Class SHALL be annotated with @SpringBootApplication
4. THE Main_Application_Class SHALL contain a main method that runs SpringApplication
