package com.sofit.externalmock.domain.financialcert.exception;

import com.sofit.externalmock.global.apiPayload.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FinancialCertErrorCode implements ErrorCode {
    INVALID_PIN(400, "AUTH4001", "PIN 번호가 올바르지 않습니다."),
    CERT_NOT_FOUND(404, "AUTH4005", "등록된 금융인증서를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
