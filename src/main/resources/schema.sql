-- 국세청 KYC Mock 테이블
CREATE TABLE IF NOT EXISTS ext_kyc_record (
    kyc_id              BIGINT       NOT NULL AUTO_INCREMENT,
    business_number     VARCHAR(20)  NOT NULL,
    representative_name VARCHAR(50)  NOT NULL,
    business_category   VARCHAR(50),
    business_type       VARCHAR(50),
    business_name       VARCHAR(50),
    business_address    VARCHAR(200),
    open_date           DATE,
    is_valid            BOOLEAN      NOT NULL,
    created_at          DATETIME     NOT NULL,
    PRIMARY KEY (kyc_id)
);

-- 금융인증서 Mock 테이블
CREATE TABLE IF NOT EXISTS ext_financial_cert (
    cert_id         BIGINT       NOT NULL AUTO_INCREMENT,
    phone_number    VARCHAR(15)  NOT NULL,
    pin_hash        VARCHAR(255) NOT NULL,
    cert_number     VARCHAR(100) NOT NULL,
    holder_name     VARCHAR(50)  NOT NULL,
    resident_number VARCHAR(7)   NOT NULL,
    status          VARCHAR(10)  NOT NULL,
    issued_at       DATETIME,
    expires_at      DATETIME,
    PRIMARY KEY (cert_id)
);

-- CB 신용조회 Mock 테이블
CREATE TABLE IF NOT EXISTS ext_cb_result (
    cb_id           BIGINT      NOT NULL AUTO_INCREMENT,
    name            VARCHAR(50) NOT NULL,
    resident_number VARCHAR(7)  NOT NULL,
    credit_score    INT         NOT NULL,
    grade           VARCHAR(5)  NOT NULL,
    evaluated_at    DATETIME,
    PRIMARY KEY (cb_id)
);
