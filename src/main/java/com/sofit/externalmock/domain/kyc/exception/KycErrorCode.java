package com.sofit.externalmock.domain.kyc.exception;

import com.sofit.externalmock.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum KycErrorCode implements BaseErrorCode {
    KYC_NOT_FOUND(HttpStatus.NOT_FOUND, "KYC4001", "등록된 사업자 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
