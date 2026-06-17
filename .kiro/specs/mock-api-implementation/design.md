# Design: Mock API Implementation

## 기술 스택

- Java 21, Spring Boot 4.0.6
- MySQL + JPA (Hibernate)
- Gradle 단일 모듈
- `spring-security-crypto` (BCryptPasswordEncoder 전용)
- Lombok

---

## 전체 패키지 구조

```
src/main/java/com/sofit/externalmock/
├── ExternalMockApplication.java
├── domain/
│   ├── kyc/
│   │   ├── controller/
│   │   │   └── KycController.java
│   │   ├── service/
│   │   │   ├── KycService.java              (interface)
│   │   │   └── KycServiceImpl.java
│   │   ├── repository/
│   │   │   └── ExtKycRecordRepository.java
│   │   ├── entity/
│   │   │   └── ExtKycRecord.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   └── KycVerifyRequest.java
│   │   │   └── response/
│   │   │       └── KycVerifyResponse.java
│   │   └── exception/
│   │       └── KycErrorCode.java
│   ├── financialcert/
│   │   ├── controller/
│   │   │   └── FinancialCertController.java
│   │   ├── service/
│   │   │   ├── FinancialCertService.java    (interface)
│   │   │   └── FinancialCertServiceImpl.java
│   │   ├── repository/
│   │   │   └── ExtFinancialCertRepository.java
│   │   ├── entity/
│   │   │   └── ExtFinancialCert.java
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   └── FinancialCertVerifyRequest.java
│   │   │   └── response/
│   │   │       └── FinancialCertVerifyResponse.java
│   │   ├── enums/
│   │   │   └── CertStatus.java
│   │   └── exception/
│   │       └── FinancialCertErrorCode.java
│   └── cb/
│       ├── controller/
│       │   └── CbController.java
│       ├── service/
│       │   ├── CbService.java               (interface)
│       │   └── CbServiceImpl.java
│       ├── repository/
│       │   └── ExtCbResultRepository.java
│       ├── entity/
│       │   └── ExtCbResult.java
│       ├── dto/
│       │   └── response/
│       │       └── CbResultResponse.java
│       └── exception/
│           └── CbErrorCode.java
└── global/
    ├── config/
    │   └── PasswordEncoderConfig.java
    ├── apiPayload/
    │   ├── BaseResponse.java
    │   ├── BaseException.java
    │   ├── CommonErrorCode.java
    │   └── GlobalExceptionHandler.java
    └── util/
```

---

## 1. 공통 인프라 설계

### 1.1 BaseResponse

```java
@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private final boolean isSuccess;
    private final String code;
    private final String message;
    private final T result;

    // 성공 응답 팩토리 메서드
    public static <T> BaseResponse<T> of(String code, String message, T result) {
        return new BaseResponse<>(true, code, message, result);
    }

    // 실패 응답 팩토리 메서드
    public static <T> BaseResponse<T> fail(String code, String message) {
        return new BaseResponse<>(false, code, message, null);
    }
}
```

### 1.2 ErrorCode 인터페이스

각 도메인 ErrorCode enum이 구현할 공통 인터페이스:

```java
public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
```

### 1.3 BaseException

```java
@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

### 1.4 CommonErrorCode

```java
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(500, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(400, "COMMON4000", "잘못된 요청입니다."),
    NOT_FOUND(404, "COMMON4004", "요청한 리소스를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### 1.5 GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BaseException 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(BaseResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    // Bean Validation 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(...) { ... }

    // 그 외 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(...) { ... }
}
```

### 1.6 PasswordEncoderConfig

```java
@Configuration
public class PasswordEncoderConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 2. KYC 도메인 설계

### 2.1 ExtKycRecord 엔티티

```java
@Entity
@Table(name = "ext_kyc_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtKycRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kycId;

    @Column(nullable = false, length = 20)
    private String businessNumber;

    @Column(nullable = false, length = 50)
    private String representativeName;

    @Column(length = 50)
    private String businessCategory;

    @Column(length = 50)
    private String businessType;

    @Column(length = 50)
    private String businessName;

    @Column(length = 200)
    private String businessAddress;

    private LocalDate openDate;

    @Column(nullable = false)
    private Boolean isValid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

### 2.2 ExtKycRecordRepository

```java
public interface ExtKycRecordRepository extends JpaRepository<ExtKycRecord, Long> {
    Optional<ExtKycRecord> findByBusinessNumber(String businessNumber);
}
```

### 2.3 KycVerifyRequest / KycVerifyResponse

```java
// Request
public record KycVerifyRequest(
    @NotBlank String businessNumber
) {}

// Response
public record KycVerifyResponse(
    String businessNumber,
    String representativeName,
    String businessCategory,
    String businessType,
    String businessName,
    String businessAddress,
    LocalDate openDate,
    Boolean isValid
) {}
```

### 2.4 KycErrorCode

```java
@Getter
@RequiredArgsConstructor
public enum KycErrorCode implements ErrorCode {
    KYC_NOT_FOUND(404, "KYC4001", "등록된 사업자 정보를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### 2.5 KycServiceImpl 로직

```
1. businessNumber로 ExtKycRecord 조회
2. 없으면 throw new BaseException(KycErrorCode.KYC_NOT_FOUND)
3. isValid == false이면 isSuccess: false 응답 반환 (예외 아님, 정상 응답)
4. isValid == true이면 KycVerifyResponse 생성 후 성공 응답 반환
```

### 2.6 KycController

```java
@RestController
@RequestMapping("/ext/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<KycVerifyResponse>> verify(
        @RequestBody @Valid KycVerifyRequest request
    ) {
        return ResponseEntity.ok(kycService.verify(request));
    }
}
```

---

## 3. 금융인증서 도메인 설계

### 3.1 CertStatus enum

```java
public enum CertStatus {
    VALID, EXPIRED, REVOKED
}
```

### 3.2 ExtFinancialCert 엔티티

```java
@Entity
@Table(name = "ext_financial_cert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtFinancialCert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certId;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String pinHash;

    @Column(nullable = false, length = 100)
    private String certNumber;

    @Column(nullable = false, length = 50)
    private String holderName;

    @Column(nullable = false, length = 7)
    private String residentNumber;  // 응답에 절대 포함하지 않음

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertStatus status;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}
```

### 3.3 ExtFinancialCertRepository

```java
public interface ExtFinancialCertRepository extends JpaRepository<ExtFinancialCert, Long> {
    Optional<ExtFinancialCert> findByPhoneNumber(String phoneNumber);
}
```

### 3.4 FinancialCertVerifyRequest / FinancialCertVerifyResponse

```java
// Request
public record FinancialCertVerifyRequest(
    @NotBlank String phoneNumber,
    @NotBlank String pin
) {}

// Response (resident_number 제외)
public record FinancialCertVerifyResponse(
    String phoneNumber,
    String certNumber,
    String holderName,
    CertStatus status,
    LocalDateTime issuedAt,
    LocalDateTime expiresAt
) {}
```

### 3.5 FinancialCertErrorCode

```java
@Getter
@RequiredArgsConstructor
public enum FinancialCertErrorCode implements ErrorCode {
    INVALID_PIN(400, "AUTH4001", "PIN 번호가 올바르지 않습니다."),
    CERT_NOT_FOUND(404, "AUTH4005", "등록된 금융인증서를 찾을 수 없습니다."),
    CERT_VERIFIED(200, "AUTH2006", "금융인증서 본인인증에 성공했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### 3.6 FinancialCertServiceImpl 로직

```
1. phoneNumber로 ExtFinancialCert 조회
2. 없으면 throw new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND)
3. status != VALID이면 isSuccess: false 응답 반환 (인증서 상태 실패)
4. BCryptPasswordEncoder.matches(pin, cert.getPinHash()) 검증
5. PIN 불일치 시 throw new BaseException(FinancialCertErrorCode.INVALID_PIN)
6. 성공 시 FinancialCertVerifyResponse 생성 (residentNumber 제외) 후 AUTH2006 코드로 반환
```

### 3.7 FinancialCertController

```java
@RestController
@RequestMapping("/ext/financial-certs")
@RequiredArgsConstructor
public class FinancialCertController {

    private final FinancialCertService financialCertService;

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<FinancialCertVerifyResponse>> verify(
        @RequestBody @Valid FinancialCertVerifyRequest request
    ) {
        return ResponseEntity.ok(financialCertService.verify(request));
    }
}
```

---

## 4. CB 도메인 설계

### 4.1 ExtCbResult 엔티티

```java
@Entity
@Table(name = "ext_cb_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtCbResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cbId;

    @Column(nullable = false, length = 20)
    private String businessNumber;

    @Column(nullable = false, length = 50)
    private String representativeName;

    @Column(nullable = false)
    private Integer creditScore;

    @Column(nullable = false, length = 5)
    private String grade;

    private LocalDateTime evaluatedAt;
}
```

### 4.2 ExtCbResultRepository

```java
public interface ExtCbResultRepository extends JpaRepository<ExtCbResult, Long> {
    Optional<ExtCbResult> findByBusinessNumber(String businessNumber);
}
```

### 4.3 CbResultResponse

```java
public record CbResultResponse(
    String businessNumber,
    String representativeName,
    Integer creditScore,
    String grade,
    LocalDateTime evaluatedAt
) {}
```

### 4.4 CbErrorCode

```java
@Getter
@RequiredArgsConstructor
public enum CbErrorCode implements ErrorCode {
    CB_NOT_FOUND(404, "CB4001", "등록된 CB 정보를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### 4.5 CbServiceImpl 로직

```
1. businessNumber로 ExtCbResult 조회
2. 없으면 throw new BaseException(CbErrorCode.CB_NOT_FOUND)
3. CbResultResponse 생성 후 성공 응답 반환
```

### 4.6 CbController

```java
@RestController
@RequestMapping("/ext/cb")
@RequiredArgsConstructor
public class CbController {

    private final CbService cbService;

    @GetMapping("/{businessNumber}")
    public ResponseEntity<BaseResponse<CbResultResponse>> getCbResult(
        @PathVariable String businessNumber
    ) {
        return ResponseEntity.ok(cbService.getCbResult(businessNumber));
    }
}
```

---

## 5. Mock 데이터 설계 (data.sql)

### 5.1 ext_kyc_record 샘플 데이터

| 케이스 | 설명 |
|---|---|
| is_valid=TRUE | 정상 사업자 (3건) |
| is_valid=FALSE | 폐업 사업자 (1건) |

### 5.2 ext_financial_cert 샘플 데이터

| 케이스 | 설명 |
|---|---|
| status=VALID | 정상 인증서 (2건, pin_hash는 BCrypt 해시값) |
| status=EXPIRED | 만료된 인증서 (1건) |
| status=REVOKED | 폐기된 인증서 (1건) |

### 5.3 ext_cb_result 샘플 데이터

| 케이스 | 설명 |
|---|---|
| 다양한 신용등급 | 1~3등급 (3건) |

---

## 6. API 응답 예시

### 6.1 KYC 성공

```json
{
  "isSuccess": true,
  "code": "COMMON2000",
  "message": "성공입니다.",
  "result": {
    "businessNumber": "123-45-67890",
    "representativeName": "홍길동",
    "businessCategory": "서비스업",
    "businessType": "소프트웨어 개발",
    "businessName": "소핏 주식회사",
    "businessAddress": "서울시 강남구 테헤란로 123",
    "openDate": "2020-01-15",
    "isValid": true
  }
}
```

### 6.2 KYC 실패 (폐업)

```json
{
  "isSuccess": false,
  "code": "COMMON2000",
  "message": "성공입니다.",
  "result": {
    "businessNumber": "999-88-77777",
    "representativeName": "김폐업",
    "businessCategory": "제조업",
    "businessType": "기타",
    "businessName": "폐업 주식회사",
    "businessAddress": "서울시 종로구 종로 1",
    "openDate": "2010-03-01",
    "isValid": false
  }
}
```

### 6.3 금융인증서 PIN 인증 성공

```json
{
  "isSuccess": true,
  "code": "AUTH2006",
  "message": "금융인증서 본인인증에 성공했습니다.",
  "result": {
    "phoneNumber": "010-1234-5678",
    "certNumber": "CERT-2024-001",
    "holderName": "홍길동",
    "status": "VALID",
    "issuedAt": "2024-01-01T00:00:00",
    "expiresAt": "2025-01-01T00:00:00"
  }
}
```

### 6.4 CB 조회 성공

```json
{
  "isSuccess": true,
  "code": "COMMON2000",
  "message": "성공입니다.",
  "result": {
    "businessNumber": "123-45-67890",
    "representativeName": "홍길동",
    "creditScore": 850,
    "grade": "1",
    "evaluatedAt": "2024-06-01T00:00:00"
  }
}
```

---

## 7. Git 브랜치 계획

| 이슈 번호 | 브랜치명 | 작업 내용 |
|---|---|---|
| SOFIT-1 | `feat/SOFIT-1-global-infra` | 공통 인프라 (BaseResponse, BaseException, ErrorCode, GlobalExceptionHandler, PasswordEncoderConfig) |
| SOFIT-2 | `feat/SOFIT-2-kyc-mock` | KYC 도메인 전체 구현 |
| SOFIT-3 | `feat/SOFIT-3-financial-cert-mock` | 금융인증서 도메인 전체 구현 |
| SOFIT-4 | `feat/SOFIT-4-cb-mock` | CB 도메인 전체 구현 |
| SOFIT-5 | `feat/SOFIT-5-mock-data` | data.sql Mock 데이터 적재 |
