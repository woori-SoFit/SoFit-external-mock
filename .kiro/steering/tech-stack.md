# tech-stack

# SoFit-external-mock 기술 스택

## Backend

- **언어/프레임워크**: Java 21, Spring Boot 4.0.6
- **모듈 구조**: 단일 모듈 (멀티모듈 아님)
- **DB**: MySQL (Mock 데이터 저장)
- **ORM**: JPA
- **인증**: 없음 (내부망 전용 서버, 인증 불필요)
- **빌드**: Gradle

## 없는 것 (SoFit-backend와 다른 점)

- Redis 없음
- Spring Batch 없음
- Spring AI / LLM 연동 없음
- Spring Security 없음 (BCrypt만 사용)
- AOP 로그 수집 없음
- Gateway 없음
- 멀티모듈 없음
- 테스트 프레임워크 없음 (JUnit, Mockito 미사용)
- 코드 커버리지 도구 없음 (JaCoCo 미사용)
- 정적 분석 도구 없음 (CheckStyle, SonarQube 미사용)

## 운영 환경

- 별도 노트북에서 `./gradlew bootRun` 또는 jar 직접 실행
- Docker 불필요
- CI/CD 없음
- 개발 서버이자 배포 서버 (prod 프로파일 사용)
