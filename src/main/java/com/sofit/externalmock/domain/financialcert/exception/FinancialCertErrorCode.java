package com.sofit.externalmock.domain.financialcert.exception;

import com.sofit.externalmock.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FinancialCertErrorCode implements BaseErrorCode {
    INVALID_PIN(HttpStatus.BAD_REQUEST, "AUTH4001", "PIN 번호가 올바르지 않습니다."),
    IDENTITY_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4002", "본인인증 정보가 일치하지 않습니다."),
    CERT_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4005", "등록된 금융인증서를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
