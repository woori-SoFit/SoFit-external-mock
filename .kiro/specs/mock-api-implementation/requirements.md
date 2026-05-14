# Requirements: Mock API Implementation

## 개요

SoFit 서비스가 의존하는 외부 기관 API(국세청 KYC, 금융인증서, CB 신용조회)를 대체하는 Mock 서버의 전체 기능을 구현한다. 실제 외부 API 호출 없이 DB 조회만으로 동일한 인터페이스를 제공하며, 내부망 전용으로 인증 없이 운영된다.

---

## 1. 공통 인프라 요구사항

### 1.1 공통 응답 포맷
- 모든 API 응답은 `{ isSuccess, code, message, result }` 구조를 따른다.
- 성공 시 `isSuccess: true`, 실패 시 `isSuccess: false`를 반환한다.
- `code`는 도메인별 ErrorCode enum 값을 사용한다.

### 1.2 글로벌 예외 처리
- `GlobalExceptionHandler`가 모든 `BaseException`을 일괄 처리한다.
- 처리되지 않은 예외는 `COMMON5000` 코드로 500 응답을 반환한다.
- 잘못된 요청(Bean Validation 실패 등)은 `COMMON4000` 코드로 400 응답을 반환한다.

### 1.3 ErrorCode 정의
- 공통 ErrorCode: `COMMON5000`, `COMMON4000`, `COMMON4004`
- KYC ErrorCode: `KYC4001` (404, "등록된 사업자 정보를 찾을 수 없습니다.")
- 금융인증서 ErrorCode: `AUTH4001` (400, "PIN 번호가 올바르지 않습니다."), `AUTH4005` (404, "등록된 금융인증서를 찾을 수 없습니다."), `AUTH2006` (200, "금융인증서 본인인증에 성공했습니다.")
- CB ErrorCode: `CB4001` (404, "등록된 CB 정보를 찾을 수 없습니다.")

### 1.4 BCryptPasswordEncoder 설정
- `BCryptPasswordEncoder` Bean을 `global/config/`에 등록한다.
- Spring Security 의존성 없이 `spring-security-crypto`만 사용한다.

### 1.5 Mock 데이터 사전 적재
- `src/main/resources/data.sql`에 각 도메인별 샘플 데이터를 INSERT SQL로 작성한다.
- 각 도메인당 최소 3건 이상의 데이터를 포함한다.
- 금융인증서 데이터의 `pin_hash`는 BCrypt로 해시된 값을 사용한다.
- KYC 데이터에는 `is_valid=FALSE`인 폐업 사업자 케이스를 포함한다.
- 금융인증서 데이터에는 `EXPIRED`, `REVOKED` 상태 케이스를 포함한다.

---

## 2. 국세청 KYC Mock

### 2.1 KYC 인증 API
- `POST /ext/kyc/verify` 엔드포인트를 제공한다.
- 요청 바디: `{ businessNumber }` (사업자등록번호, 필수)
- `business_number`로 `ext_kyc_record` 테이블을 조회한다.
- 레코드가 존재하지 않으면 `KYC4001` 예외를 발생시킨다.
- `is_valid=FALSE`이면 KYC 실패로 처리하며, `isSuccess: false`와 함께 폐업 사업자임을 나타내는 응답을 반환한다.
- `is_valid=TRUE`이면 KYC 성공으로 처리하며, 사업자 정보 전체를 `result`에 담아 반환한다.

### 2.2 KYC 성공 응답 필드
- 성공 시 `result`에 포함할 필드: `businessNumber`, `representativeName`, `businessCategory`, `businessType`, `businessName`, `businessAddress`, `openDate`, `isValid`

### 2.3 KYC 엔티티
- 테이블명: `ext_kyc_record`
- 컬럼: `kyc_id` (PK, BIGINT), `business_number` (VARCHAR 20), `representative_name` (VARCHAR 50), `business_category` (VARCHAR 50), `business_type` (VARCHAR 50), `business_name` (VARCHAR 50), `business_address` (VARCHAR 200), `open_date` (DATE), `is_valid` (BOOLEAN), `created_at` (DATETIME)

---

## 3. 금융인증서 Mock

### 3.1 금융인증서 PIN 인증 API
- `POST /ext/financial-certs/verify` 엔드포인트를 제공한다.
- 요청 바디: `{ phoneNumber, pin }` (전화번호, PIN, 모두 필수)
- `phone_number`로 `ext_financial_cert` 테이블을 조회한다.
- 레코드가 존재하지 않으면 `AUTH4005` 예외를 발생시킨다.
- `status`가 `VALID`가 아니면 인증 실패로 처리하며, `isSuccess: false` 응답을 반환한다.
- `BCryptPasswordEncoder.matches(pin, pinHash)`로 PIN을 검증한다.
- PIN이 일치하지 않으면 `AUTH4001` 예외를 발생시킨다.
- 모든 검증 통과 시 `AUTH2006` 코드와 함께 인증서 정보를 반환한다.

### 3.2 금융인증서 성공 응답 필드
- 성공 시 `result`에 포함할 필드: `phoneNumber`, `certNumber`, `holderName`, `status`, `issuedAt`, `expiresAt`
- `resident_number`(주민등록번호 앞 7자리)는 어떠한 경우에도 응답에 포함하지 않는다.

### 3.3 금융인증서 엔티티
- 테이블명: `ext_financial_cert`
- 컬럼: `cert_id` (PK, BIGINT), `phone_number` (VARCHAR 15), `pin_hash` (VARCHAR 255), `cert_number` (VARCHAR 100), `holder_name` (VARCHAR 50), `resident_number` (VARCHAR 7), `status` (ENUM: VALID/EXPIRED/REVOKED), `issued_at` (DATETIME), `expires_at` (DATETIME)
- `status` 컬럼은 Java enum `CertStatus`로 매핑한다.

---

## 4. CB 신용조회 Mock

### 4.1 CB 조회 API
- `GET /ext/cb/{businessNumber}` 엔드포인트를 제공한다.
- Path variable: `businessNumber` (사업자등록번호, 필수)
- `business_number`로 `ext_cb_result` 테이블을 조회한다.
- 레코드가 존재하지 않으면 `CB4001` 예외를 발생시킨다.
- 조회 성공 시 CB 레코드 전체를 `result`에 담아 반환한다.

### 4.2 CB 성공 응답 필드
- 성공 시 `result`에 포함할 필드: `businessNumber`, `representativeName`, `creditScore`, `grade`, `evaluatedAt`

### 4.3 CB 엔티티
- 테이블명: `ext_cb_result`
- 컬럼: `cb_id` (PK, BIGINT), `business_number` (VARCHAR 20), `representative_name` (VARCHAR 50), `credit_score` (INT), `grade` (VARCHAR 5), `evaluated_at` (DATETIME)

---

## 5. 패키지 구조 요구사항

### 5.1 도메인형 패키지 구조
- 루트 패키지: `com.sofit.externalmock`
- 도메인 패키지: `domain.kyc`, `domain.financialcert`, `domain.cb`
- 각 도메인 하위: `controller`, `service`, `repository`, `entity`, `dto/request`, `dto/response`, `exception`
- KYC, 금융인증서 도메인에는 `enums` 패키지 추가
- 공통 패키지: `global.config`, `global.apiPayload`, `global.util`

### 5.2 레이어 분리
- Entity를 Controller 레이어까지 노출하지 않는다.
- Service 또는 Converter에서 Entity ↔ DTO 변환을 처리한다.
- Service는 인터페이스와 구현체로 분리한다 (예: `FinancialCertService` / `FinancialCertServiceImpl`).

---

## 6. Git 브랜치 및 PR 요구사항

### 6.1 브랜치 전략
- 기능 단위로 브랜치를 생성한다.
- 브랜치명 형식: `feat/SOFIT-{번호}-{기능명}` (예: `feat/SOFIT-1-global-infra`)

### 6.2 커밋 컨벤션
- 커밋 형식: `[SOFIT-{번호}] Feat: {작업 내용}`

### 6.3 PR 단위
- 각 기능 브랜치마다 PR을 생성한다.
- PR 제목에 Jira 이슈 번호를 포함한다.
