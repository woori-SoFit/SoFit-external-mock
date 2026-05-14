# be-conventions

# SoFit-external-mock BE 개발 컨벤션

## 패키지 구조 (도메인형)

```
src/main/java/com/sofit/externalmock/
├── domain/
│   ├── kyc/                  # 국세청 KYC Mock
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/           # ExtKycRecord
│   │   ├── dto/
│   │   ├── enums/
│   │   └── exception/
│   ├── financialcert/        # 금융인증서 Mock
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/           # ExtFinancialCert
│   │   ├── dto/
│   │   ├── enums/
│   │   └── exception/
│   └── cb/                   # CB 조회 Mock
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/           # ExtCbResult
│       ├── dto/
│       └── exception/
└── global/
    ├── config/
    ├── apiPayload/           # 공통 응답 포맷 (BaseResponse, BaseException, ErrorCode)
    └── util/
```

## 공통 응답 포맷

SoFit-backend와 동일한 포맷 사용

```json
{
  "isSuccess": true,
  "code": "COMMON2000",
  "message": "성공입니다.",
  "result": {}
}
```

### ErrorCode 목록

```
# 공통
COMMON5000 - 500: "서버 에러, 관리자에게 문의 바랍니다."
COMMON4000 - 400: "잘못된 요청입니다."
COMMON4004 - 404: "요청한 리소스를 찾을 수 없습니다."

# 금융인증서
AUTH4001 - 400: "PIN 번호가 올바르지 않습니다."
AUTH4005 - 404: "등록된 금융인증서를 찾을 수 없습니다."
AUTH2006 - 200: "금융인증서 본인인증에 성공했습니다."

# KYC
KYC4001 - 404: "등록된 사업자 정보를 찾을 수 없습니다."

# CB
CB4001 - 404: "등록된 CB 정보를 찾을 수 없습니다."
```

- 도메인별 ErrorCode는 해당 도메인 `exception/` 안에 정의
- `global/apiPayload/`의 GlobalExceptionHandler에서 일괄 처리
- 예외 발생 시 `throw new BaseException(ErrorCode.XXX)` 형태 통일

## API URL 컨벤션

- 모든 Mock API prefix: `/ext/**`
- 케밥 케이스 사용
- 예시:
  - `POST /ext/kyc/verify`
  - `POST /ext/financial-certs/verify`
  - `GET /ext/cb/{businessNumber}`

## 네이밍 컨벤션

SoFit-backend와 동일

```
Controller   → KycController
Service      → FinancialCertService (interface) / FinancialCertServiceImpl (구현체)
Repository   → ExtKycRecordRepository
DTO          → FinancialCertVerifyRequest, FinancialCertVerifyResponse
Entity       → ExtKycRecord, ExtFinancialCert, ExtCbResult
```

## DTO 분리 규칙

SoFit-backend와 동일

```
dto/
├── request/
│   └── FinancialCertVerifyRequest.java
└── response/
    └── FinancialCertVerifyResponse.java
```

- Entity를 Controller 레이어까지 올리지 않는다
- Converter 또는 Service에서 Entity ↔ DTO 변환 처리

## 주요 비즈니스 규칙

- PIN 검증은 반드시 BCryptPasswordEncoder 사용 — 원문 PIN과 pin_hash bcrypt 비교
- `resident_number`는 어떤 응답에도 포함하지 않는다
- `ext_financial_cert.status`가 VALID가 아니면 인증 실패 처리
- `ext_kyc_record.is_valid`가 FALSE면 폐업 사업자로 KYC 실패 처리
- Mock 데이터는 DB 사전 적재 방식 (API로 데이터 생성 기능 불필요)

## ORM

- JPA 사용
- N+1 문제 주의: fetch join 또는 @EntityGraph 사용
- QueryDSL 불필요 (단순 단건 조회만 사용)
