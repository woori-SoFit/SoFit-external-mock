-- =============================================
-- ext_kyc_record Mock 데이터
-- 정상 사업자 3건 + 폐업 사업자 1건
-- =============================================
TRUNCATE TABLE ext_kyc_record;
INSERT INTO ext_kyc_record (business_number, representative_name, business_category, business_type, business_name, business_address, open_date, is_valid, created_at)
VALUES
    ('1234567890', '홍길동', '서비스업', '소프트웨어 개발', '소핏 주식회사', '서울시 강남구 테헤란로 123', '2020-01-15', TRUE, NOW()),
    ('2345678901', '김철수', '도소매업', '전자상거래', '철수마트', '서울시 마포구 홍대입구로 45', '2018-06-01', TRUE, NOW()),
    ('3456789012', '이영희', '제조업', '식품 제조', '영희푸드', '경기도 성남시 분당구 판교로 88', '2015-03-20', TRUE, NOW()),
    ('9998877777', '김폐업', '제조업', '기타', '폐업 주식회사', '서울시 종로구 종로 1', '2010-03-01', FALSE, NOW());

-- =============================================
-- ext_financial_cert Mock 데이터
-- VALID 2건 + EXPIRED 1건 + REVOKED 1건
-- pin_hash: BCrypt("1234") 해시값
-- =============================================
TRUNCATE TABLE ext_financial_cert;
INSERT INTO ext_financial_cert (phone_number, pin_hash, cert_number, holder_name, resident_number, status, issued_at, expires_at)
VALUES
    ('01012345678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CERT-2024-001', '홍길동', '9001011', 'VALID', '2024-01-01 00:00:00', '2027-01-01 00:00:00'),
    ('01098765432', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CERT-2024-002', '김철수', '8505152', 'VALID', '2024-03-01 00:00:00', '2027-03-01 00:00:00'),
    ('01011112222', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CERT-2023-003', '이영희', '9212034', 'EXPIRED', '2023-01-01 00:00:00', '2024-01-01 00:00:00'),
    ('01033334444', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CERT-2022-004', '박민준', '9107075', 'REVOKED', '2022-06-01 00:00:00', '2025-06-01 00:00:00');

-- =============================================
-- ext_cb_result Mock 데이터
-- 다양한 신용등급 3건
-- =============================================
TRUNCATE TABLE ext_cb_result;
INSERT INTO ext_cb_result (business_number, representative_name, credit_score, grade, evaluated_at)
VALUES
    ('1234567890', '홍길동', 850, '1', '2024-06-01 00:00:00'),
    ('2345678901', '김철수', 720, '2', '2024-06-01 00:00:00'),
    ('3456789012', '이영희', 580, '3', '2024-06-01 00:00:00');
