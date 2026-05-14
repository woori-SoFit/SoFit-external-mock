# product

# SoFit-external-mock 서비스 개요

## 서비스 소개

SoFit-external-mock은 SoFit 서비스가 의존하는 외부 기관 API(국세청, CB사, 금융인증서)를 대체하는 Mock 서버입니다.
실제 외부 API 없이 개발/테스트 환경에서 동일한 인터페이스로 동작합니다.
별도 노트북에서 수동 운영하며, CI/CD 및 이중화는 적용하지 않습니다.

## Mock API 목록

### 1. 국세청 KYC Mock (`ext_kyc_record`)
- 사업자등록번호로 사업자 진위를 확인하는 국세청 API를 대체
- 호출 주체: user-backend (회원가입 플로우)
- 조회 키: `business_number`

### 2. 금융인증서 Mock (`ext_financial_cert`)
- 금융인증서 연동 및 PIN 인증 API를 대체
- 호출 주체: user-backend (회원가입 실명 확인, 대출 신청 PIN 인증 등)
- 인증서 조회 키: `phone_number`
- PIN 인증: bcrypt로 저장된 `pin_hash`와 입력값 비교
- `resident_number`(주민번호 앞 7자리)는 회원가입 실명 확인 내부 매칭용 — 응답에 미포함

### 3. CB 조회 Mock (`ext_cb_result`)
- CB(신용조회) API를 대체
- 호출 주체: Spring Batch (admin-backend)
- 조회 키: `business_number`

## ERD 요약

### ext_kyc_record
| 컬럼 | 타입 | 설명 |
|---|---|---|
| kyc_id | BIGINT | PK |
| business_number | VARCHAR(20) | 사업자등록번호 |
| representative_name | VARCHAR(50) | 대표자명 |
| business_category | VARCHAR(50) | 업태 (대분류) |
| business_type | VARCHAR(50) | 업종 |
| business_name | VARCHAR(50) | 상호명 |
| business_address | VARCHAR(200) | 사업장 주소 |
| open_date | DATE | 개업일 |
| is_valid | BOOLEAN | 사업자 유효 여부 (폐업 시 FALSE) |
| created_at | DATETIME | 데이터 등록 일시 |

### ext_financial_cert
| 컬럼 | 타입 | 설명 |
|---|---|---|
| cert_id | BIGINT | PK |
| phone_number | VARCHAR(15) | 소유자 연락처 |
| pin_hash | VARCHAR(255) | bcrypt 해시 처리된 PIN 값 |
| cert_number | VARCHAR(100) | 금융인증서 고유 번호 |
| holder_name | VARCHAR(50) | 인증서 소유자 실명 |
| resident_number | VARCHAR(7) | 소유자 주민등록번호 앞 7자리 (실명 확인용, 응답 미포함) |
| status | ENUM | VALID / EXPIRED / REVOKED |
| issued_at | DATETIME | 인증서 발급 일시 |
| expires_at | DATETIME | 인증서 만료 일시 |

### ext_cb_result
| 컬럼 | 타입 | 설명 |
|---|---|---|
| cb_id | BIGINT | PK |
| business_number | VARCHAR(20) | 사업자등록번호 |
| representative_name | VARCHAR(50) | 대표자명 |
| credit_score | INT | 신용 점수 (0~1000) |
| grade | VARCHAR(5) | CB 신용 등급 |
| evaluated_at | DATETIME | CB 평가 기준 일시 |

## 응답 포맷

SoFit-backend와 동일한 공통 응답 포맷 사용

```json
{
  "isSuccess": true,
  "code": "AUTH2006",
  "message": "금융인증서 본인인증에 성공했습니다.",
  "result": {}
}
```

## 주의사항

- Mock 데이터는 DB에 사전 적재 (Insert SQL 또는 data.sql)
- 실제 외부 API 호출 없음 — DB 조회만으로 응답 구성
- `resident_number`는 내부 매칭 전용, 어떤 API 응답에도 포함하지 않음
- PIN 검증은 반드시 bcrypt 비교 (`BCryptPasswordEncoder`)
