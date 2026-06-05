package com.sofit.externalmock.domain.financialcert.exception;

import com.sofit.externalmock.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FinancialCertSuccessCode implements BaseSuccessCode {
    PIN_VERIFIED(HttpStatus.OK, "AUTH2001", "금융인증서 PIN 인증에 성공했습니다."),
    IDENTITY_VERIFIED(HttpStatus.OK, "AUTH2002", "금융인증서 본인인증에 성공했습니다."),
    CERT_LOOKUP_SUCCESS(HttpStatus.OK, "AUTH2003", "금융인증서 조회에 성공했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
