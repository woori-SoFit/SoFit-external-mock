# Implementation Plan: Mock API Implementation

## 개요

SoFit-external-mock 프로젝트의 전체 기능을 구현합니다.
공통 인프라 → KYC → 금융인증서 → CB → Mock 데이터 순서로 진행하며,
기능 단위로 브랜치를 생성하고 `dev` 브랜치 기반으로 PR을 생성합니다.

---

## Phase 1: 공통 인프라 (feat/SOFIT-73-global-infra)

- [x] 1.1 `global/apiPayload/ErrorCode.java` 인터페이스 생성
  - `getStatus()`, `getCode()`, `getMessage()` 메서드 정의

- [x] 1.2 `global/apiPayload/CommonErrorCode.java` enum 생성
  - `INTERNAL_SERVER_ERROR` (500, COMMON5000), `BAD_REQUEST` (400, COMMON4000), `NOT_FOUND` (404, COMMON4004)
  - `ErrorCode` 인터페이스 구현

- [x] 1.3 `global/apiPayload/BaseException.java` 생성
  - `ErrorCode`를 필드로 보유하는 `RuntimeException` 서브클래스

- [x] 1.4 `global/apiPayload/BaseResponse.java` 제네릭 클래스 생성
  - `isSuccess`, `code`, `message`, `result` 필드
  - `of(code, message, result)` 정적 팩토리 메서드
  - `fail(code, message)` 정적 팩토리 메서드

- [x] 1.5 `global/apiPayload/GlobalExceptionHandler.java` 생성
  - `@RestControllerAdvice` 적용
  - `BaseException` 핸들러: ErrorCode의 status/code/message로 응답
  - `MethodArgumentNotValidException` 핸들러: COMMON4000으로 응답
  - `Exception` 핸들러: COMMON5000으로 응답

- [x] 1.6 `global/config/PasswordEncoderConfig.java` 생성
  - `BCryptPasswordEncoder` Bean 등록
  - `@Configuration` 적용

- [x] 1.7 브랜치 `feat/SOFIT-73-global-infra` 생성 후 커밋 및 PR
  - 커밋: `[SOFIT-73] Feat: 공통 인프라 구현 (BaseResponse, BaseException, GlobalExceptionHandler, PasswordEncoderConfig)`
  - PR 베이스: `dev`

---

## Phase 2: KYC 도메인 (feat/SOFIT-74-kyc-mock)

- [ ] 2.1 `domain/kyc/exception/KycErrorCode.java` enum 생성
  - `KYC_NOT_FOUND` (404, KYC4001, "등록된 사업자 정보를 찾을 수 없습니다.")
  - `ErrorCode` 인터페이스 구현

- [ ] 2.2 `domain/kyc/entity/ExtKycRecord.java` 엔티티 생성
  - `@Entity`, `@Table(name = "ext_kyc_record")` 적용
  - 필드: `kycId` (PK, BIGINT, AUTO_INCREMENT), `businessNumber`, `representativeName`, `businessCategory`, `businessType`, `businessName`, `businessAddress`, `openDate` (LocalDate), `isValid` (Boolean), `createdAt` (LocalDateTime)
  - `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 적용

- [ ] 2.3 `domain/kyc/repository/ExtKycRecordRepository.java` 생성
  - `JpaRepository<ExtKycRecord, Long>` 확장
  - `findByBusinessNumber(String businessNumber)` 메서드 선언

- [ ] 2.4 `domain/kyc/dto/request/KycVerifyRequest.java` 생성
  - `businessNumber` 필드 (`@NotBlank` 적용)

- [ ] 2.5 `domain/kyc/dto/response/KycVerifyResponse.java` 생성
  - 필드: `businessNumber`, `representativeName`, `businessCategory`, `businessType`, `businessName`, `businessAddress`, `openDate`, `isValid`
  - `ExtKycRecord`로부터 변환하는 정적 팩토리 메서드 `from(ExtKycRecord)` 포함

- [ ] 2.6 `domain/kyc/service/KycService.java` 인터페이스 생성
  - `BaseResponse<KycVerifyResponse> verify(KycVerifyRequest request)` 메서드 선언

- [ ] 2.7 `domain/kyc/service/KycServiceImpl.java` 구현체 생성
  - `@Service` 적용
  - `businessNumber`로 레코드 조회 → 없으면 `KYC_NOT_FOUND` 예외
  - `isValid == false`이면 `isSuccess: false` 응답 반환
  - `isValid == true`이면 성공 응답 반환

- [ ] 2.8 `domain/kyc/controller/KycController.java` 생성
  - `@RestController`, `@RequestMapping("/ext/kyc")` 적용
  - `POST /verify` 엔드포인트: `@RequestBody @Valid KycVerifyRequest` 수신 후 서비스 호출

- [ ] 2.9 브랜치 `feat/SOFIT-74-kyc-mock` 생성 후 커밋 및 PR
  - 커밋: `[SOFIT-74] Feat: KYC Mock API 구현`
  - PR 베이스: `dev`

---

## Phase 3: 금융인증서 도메인 (feat/SOFIT-75-financial-cert-mock)

- [ ] 3.1 `domain/financialcert/enums/CertStatus.java` enum 생성
  - `VALID`, `EXPIRED`, `REVOKED` 값 정의

- [ ] 3.2 `domain/financialcert/exception/FinancialCertErrorCode.java` enum 생성
  - `INVALID_PIN` (400, AUTH4001, "PIN 번호가 올바르지 않습니다.")
  - `CERT_NOT_FOUND` (404, AUTH4005, "등록된 금융인증서를 찾을 수 없습니다.")
  - `ErrorCode` 인터페이스 구현

- [ ] 3.3 `domain/financialcert/entity/ExtFinancialCert.java` 엔티티 생성
  - `@Entity`, `@Table(name = "ext_financial_cert")` 적용
  - 필드: `certId` (PK), `phoneNumber`, `pinHash`, `certNumber`, `holderName`, `residentNumber`, `status` (`@Enumerated(EnumType.STRING)`), `issuedAt`, `expiresAt`
  - `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 적용

- [ ] 3.4 `domain/financialcert/repository/ExtFinancialCertRepository.java` 생성
  - `JpaRepository<ExtFinancialCert, Long>` 확장
  - `findByPhoneNumber(String phoneNumber)` 메서드 선언

- [ ] 3.5 `domain/financialcert/dto/request/FinancialCertVerifyRequest.java` 생성
  - `phoneNumber`, `pin` 필드 (모두 `@NotBlank` 적용)

- [ ] 3.6 `domain/financialcert/dto/response/FinancialCertVerifyResponse.java` 생성
  - 필드: `phoneNumber`, `certNumber`, `holderName`, `status`, `issuedAt`, `expiresAt`
  - `residentNumber` 필드 절대 포함 금지
  - `ExtFinancialCert`로부터 변환하는 정적 팩토리 메서드 `from(ExtFinancialCert)` 포함

- [ ] 3.7 `domain/financialcert/service/FinancialCertService.java` 인터페이스 생성
  - `BaseResponse<FinancialCertVerifyResponse> verify(FinancialCertVerifyRequest request)` 메서드 선언

- [ ] 3.8 `domain/financialcert/service/FinancialCertServiceImpl.java` 구현체 생성
  - `@Service` 적용
  - `BCryptPasswordEncoder` 주입
  - 로직: 조회 → status 검증 → PIN bcrypt 검증 → 성공 응답 (AUTH2006)

- [ ] 3.9 `domain/financialcert/controller/FinancialCertController.java` 생성
  - `@RestController`, `@RequestMapping("/ext/financial-certs")` 적용
  - `POST /verify` 엔드포인트

- [ ] 3.10 브랜치 `feat/SOFIT-75-financial-cert-mock` 생성 후 커밋 및 PR
  - 커밋: `[SOFIT-75] Feat: 금융인증서 Mock API 구현`
  - PR 베이스: `dev`

---

## Phase 4: CB 도메인 (feat/SOFIT-76-cb-mock)

- [ ] 4.1 `domain/cb/exception/CbErrorCode.java` enum 생성
  - `CB_NOT_FOUND` (404, CB4001, "등록된 CB 정보를 찾을 수 없습니다.")
  - `ErrorCode` 인터페이스 구현

- [ ] 4.2 `domain/cb/entity/ExtCbResult.java` 엔티티 생성
  - `@Entity`, `@Table(name = "ext_cb_result")` 적용
  - 필드: `cbId` (PK), `businessNumber`, `representativeName`, `creditScore` (Integer), `grade`, `evaluatedAt` (LocalDateTime)
  - `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 적용

- [ ] 4.3 `domain/cb/repository/ExtCbResultRepository.java` 생성
  - `JpaRepository<ExtCbResult, Long>` 확장
  - `findByBusinessNumber(String businessNumber)` 메서드 선언

- [ ] 4.4 `domain/cb/dto/response/CbResultResponse.java` 생성
  - 필드: `businessNumber`, `representativeName`, `creditScore`, `grade`, `evaluatedAt`
  - `ExtCbResult`로부터 변환하는 정적 팩토리 메서드 `from(ExtCbResult)` 포함

- [ ] 4.5 `domain/cb/service/CbService.java` 인터페이스 생성
  - `BaseResponse<CbResultResponse> getCbResult(String businessNumber)` 메서드 선언

- [ ] 4.6 `domain/cb/service/CbServiceImpl.java` 구현체 생성
  - `@Service` 적용
  - `businessNumber`로 레코드 조회 → 없으면 `CB_NOT_FOUND` 예외
  - 성공 시 `CbResultResponse.from(result)` 반환

- [ ] 4.7 `domain/cb/controller/CbController.java` 생성
  - `@RestController`, `@RequestMapping("/ext/cb")` 적용
  - `GET /{businessNumber}` 엔드포인트

- [ ] 4.8 브랜치 `feat/SOFIT-76-cb-mock` 생성 후 커밋 및 PR
  - 커밋: `[SOFIT-76] Feat: CB 조회 Mock API 구현`
  - PR 베이스: `dev`

---

## Phase 5: Mock 데이터 적재 (feat/SOFIT-77-mock-data)

- [ ] 5.1 `src/main/resources/data.sql` 파일 생성
  - `ext_kyc_record` INSERT: 정상 사업자 3건 + 폐업 사업자 1건 (is_valid=FALSE)
  - `ext_financial_cert` INSERT: VALID 2건 + EXPIRED 1건 + REVOKED 1건
    - VALID 레코드의 `pin_hash`는 BCrypt 해시값 사용 (PIN "1234" 기준)
  - `ext_cb_result` INSERT: 다양한 신용등급 3건

- [ ] 5.2 `application.yml`에 data.sql 자동 실행 설정 추가
  - `spring.sql.init.mode: always`
  - `spring.jpa.defer-datasource-initialization: true`

- [ ] 5.3 브랜치 `feat/SOFIT-77-mock-data` 생성 후 커밋 및 PR
  - 커밋: `[SOFIT-77] Feat: Mock 데이터 사전 적재 (data.sql)`
  - PR 베이스: `dev`

---

## Phase 6: 통합 검증

- [ ] 6.1 애플리케이션 기동 확인
  - `./gradlew bootRun` 실행 후 정상 기동 확인

- [ ] 6.2 KYC API 수동 테스트
  - 정상 사업자 조회 → 200 + isSuccess: true
  - 폐업 사업자 조회 → 200 + isSuccess: false
  - 미등록 사업자 조회 → 404 + KYC4001

- [ ] 6.3 금융인증서 API 수동 테스트
  - 정상 PIN 인증 → 200 + AUTH2006
  - 잘못된 PIN → 400 + AUTH4001
  - EXPIRED 인증서 → isSuccess: false
  - 미등록 전화번호 → 404 + AUTH4005
  - 응답에 residentNumber 미포함 확인

- [ ] 6.4 CB API 수동 테스트
  - 등록된 사업자 조회 → 200 + CB 레코드 반환
  - 미등록 사업자 조회 → 404 + CB4001

---

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7"] },
    { "id": 1, "tasks": ["2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9"] },
    { "id": 2, "tasks": ["3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "3.10"] },
    { "id": 3, "tasks": ["4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8"] },
    { "id": 4, "tasks": ["5.1", "5.2", "5.3"] },
    { "id": 5, "tasks": ["6.1", "6.2", "6.3", "6.4"] }
  ]
}
```
